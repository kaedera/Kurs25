package com.example.kursovaya.ui.dashboard;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.kursovaya.R;
import com.example.kursovaya.MainActivity;
import com.example.kursovaya.api.ApiService;
import com.example.kursovaya.api.RetrofitClient;
import com.example.kursovaya.api.StudentInfoResponse;
import com.example.kursovaya.api.StudentMarksResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private TextView textViewFIO, textViewBirthDate, textViewEmail;
    private ListView listViewMarks;
    private MarksAdapter marksAdapter;
    private List<StudentMarksResponse> marksList;
    private DashboardViewModel dashboardViewModel;
    private int studentId; // ID студента

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
        dashboardViewModel.getSharedData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                loadStudentData(integer);
                loadStudentMarks(integer);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Получаем ID студента из аргументов
        if (getArguments() != null) {
            studentId = getArguments().getInt("STUDENT_ID", -1);
        }

        // Инициализация элементов интерфейса
        textViewFIO = root.findViewById(R.id.textView);
        textViewBirthDate = root.findViewById(R.id.textView2);
        textViewEmail = root.findViewById(R.id.text_dashboard);
        listViewMarks = root.findViewById(R.id.listView);

        // Инициализация списка оценок
        marksList = new ArrayList<>();
        marksAdapter = new MarksAdapter(getContext(), marksList);
        listViewMarks.setAdapter(marksAdapter);

        // Кнопка выхода
        Button buttonLogout = root.findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(v -> showLogoutConfirmationDialog());

        return root;
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Выход из аккаунта")
                .setMessage("Вы уверены, что хотите выйти из аккаунта?")
                .setPositiveButton("Да", (dialog, which) -> {
                    // Переход на MainActivity
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                    requireActivity().finish(); // Закрыть текущую активность
                })
                .setNegativeButton("Нет", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void loadStudentData(int studentId) {
        ApiService apiService = RetrofitClient.getApiService();

        // Запрос информации о студенте
        Call<StudentInfoResponse> callStudentInfo = apiService.getStudentInfo(studentId);
        callStudentInfo.enqueue(new Callback<StudentInfoResponse>() {
            @Override
            public void onResponse(@NonNull Call<StudentInfoResponse> call, @NonNull Response<StudentInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StudentInfoResponse studentInfo = response.body();

                    // Установка данных в TextView
                    textViewFIO.setText(studentInfo.getLastName() + " " + studentInfo.getFirstName() + " " + studentInfo.getPatronymic());

                    // Форматирование даты рождения
                    String formattedDate = formatDate(studentInfo.getDateOfBirth());
                    textViewBirthDate.setText("Дата рождения: " + formattedDate);

                    textViewEmail.setText("Email: " + studentInfo.getEmail());

                    // Загрузка оценок студента
                    loadStudentMarks(studentId);
                } else {
                    // Обработка ошибки
                    textViewFIO.setText("Ошибка загрузки данных");
                }
            }

            @Override
            public void onFailure(@NonNull Call<StudentInfoResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
                textViewFIO.setText("Ошибка сети");
            }
        });
    }

    private void loadStudentMarks(int studentId) {
        ApiService apiService = RetrofitClient.getApiService();

        // Запрос оценок студента
        Call<List<StudentMarksResponse>> callStudentMarks = apiService.getStudentMarks(studentId);
        callStudentMarks.enqueue(new Callback<List<StudentMarksResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<StudentMarksResponse>> call, @NonNull Response<List<StudentMarksResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<StudentMarksResponse> marks = response.body();

                    // Группировка оценок по названию предмета
                    Map<String, StudentMarksResponse> groupedMarks = new HashMap<>();
                    for (StudentMarksResponse mark : marks) {
                        String discipline = mark.getDiscipline();
                        if (!groupedMarks.containsKey(discipline)) {
                            groupedMarks.put(discipline, new StudentMarksResponse());
                            groupedMarks.get(discipline).setDiscipline(discipline);
                            groupedMarks.get(discipline).setMark("");
                        }
                        groupedMarks.get(discipline).setMark(groupedMarks.get(discipline).getMark() + mark.getMark() + " ");
                    }

                    // Преобразуем Map в List для адаптера
                    List<StudentMarksResponse> groupedList = new ArrayList<>(groupedMarks.values());

                    // Очистка списка оценок
                    marksList.clear();
                    marksList.addAll(groupedList);

                    // Обновление ListView
                    marksAdapter.notifyDataSetChanged();
                } else {
                    // Обработка ошибки
                    marksList.clear();
                    marksAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<StudentMarksResponse>> call, @NonNull Throwable t) {
                t.printStackTrace();
                marksList.clear();
                marksAdapter.notifyDataSetChanged();
            }
        });
    }

    // Метод для форматирования даты
    private String formatDate(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return inputDate; // Возвращаем исходную дату в случае ошибки
        }
    }
}