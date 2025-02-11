package com.example.kursovaya.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.kursovaya.R;
import com.example.kursovaya.api.ApiService;
import com.example.kursovaya.api.RetrofitClient;
import com.example.kursovaya.api.StudentInfoResponse;
import com.example.kursovaya.api.StudentExamsResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView textViewGreeting, textViewName;
    private ListView listViewExams;
    private HomeViewModel homeViewModel;
    private ExamsAdapter examsAdapter;
    private List<StudentExamsResponse> examsList;
    private int studentId; // ID студента

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        homeViewModel.getSharedData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                loadStudentData(integer);
                loadStudentExams(integer);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Получаем ID студента из аргументов
        if (getArguments() != null) {
            studentId = getArguments().getInt("STUDENT_ID", -1);
        }

        // Инициализация элементов интерфейса
        textViewGreeting = root.findViewById(R.id.textViewGreeting);
        textViewName = root.findViewById(R.id.textViewName);
        listViewExams = root.findViewById(R.id.listViewExams);

        // Установка приветствия в зависимости от времени суток
        setGreeting();

        // Инициализация списка экзаменов
        examsList = new ArrayList<>();
        examsAdapter = new ExamsAdapter(getContext(), examsList);
        listViewExams.setAdapter(examsAdapter);

        return root;
    }

    private void setGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 6 && hour < 12) {
            textViewGreeting.setText("Доброе утро,");
        } else if (hour >= 12 && hour < 18) {
            textViewGreeting.setText("Добрый день,");
        } else {
            textViewGreeting.setText("Добрый вечер,");
        }
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

                    // Установка имени студента
                    textViewName.setText(studentInfo.getFirstName());
                } else {
                    // Обработка ошибки
                    textViewName.setText("Ошибка загрузки данных");
                }
            }

            @Override
            public void onFailure(@NonNull Call<StudentInfoResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
                textViewName.setText("Ошибка сети");
            }
        });
    }

    private void loadStudentExams(int studentId) {
        ApiService apiService = RetrofitClient.getApiService();

        // Запрос экзаменов студента
        Call<List<StudentExamsResponse>> callStudentExams = apiService.getStudentExams(studentId);
        callStudentExams.enqueue(new Callback<List<StudentExamsResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<StudentExamsResponse>> call, @NonNull Response<List<StudentExamsResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<StudentExamsResponse> exams = response.body();

                    // Очистка списка экзаменов
                    examsList.clear();
                    examsList.addAll(exams);

                    // Обновление ListView
                    examsAdapter.notifyDataSetChanged();
                } else {
                    // Обработка ошибки
                    examsList.clear();
                    examsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<StudentExamsResponse>> call, @NonNull Throwable t) {
                t.printStackTrace();
                examsList.clear();
                examsAdapter.notifyDataSetChanged();
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