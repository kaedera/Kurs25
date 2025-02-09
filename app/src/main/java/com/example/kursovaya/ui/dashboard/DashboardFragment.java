package com.example.kursovaya.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.kursovaya.R;
import com.example.kursovaya.api.ApiService;
import com.example.kursovaya.api.RetrofitClient;
import com.example.kursovaya.api.StudentInfoResponse;
import com.example.kursovaya.api.StudentMarksResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private TextView textViewFIO, textViewBirthDate, textViewEmail;
    private ListView listViewMarks;
    private ArrayAdapter<String> marksAdapter;
    private List<String> marksList;
    private int studentId; // ID студента

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
        marksAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, marksList);
        listViewMarks.setAdapter(marksAdapter);

        // Загрузка данных студента
        if (studentId != -1) {
            loadStudentData(studentId);
        }

        return root;
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
                    textViewBirthDate.setText("Дата рождения: " + studentInfo.getDateOfBirth());
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

                    // Очистка списка оценок
                    marksList.clear();

                    // Парсинг и добавление оценок в список
                    for (StudentMarksResponse mark : marks) {
                        marksList.add(mark.getDiscipline() + ": " + mark.getMark() + " (" + mark.getDate() + ")");
                    }

                    // Обновление ListView
                    marksAdapter.notifyDataSetChanged();
                } else {
                    // Обработка ошибки
                    marksList.clear();
                    marksList.add("Ошибка загрузки оценок");
                    marksAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<StudentMarksResponse>> call, @NonNull Throwable t) {
                t.printStackTrace();
                marksList.clear();
                marksList.add("Ошибка сети");
                marksAdapter.notifyDataSetChanged();
            }
        });
    }
}