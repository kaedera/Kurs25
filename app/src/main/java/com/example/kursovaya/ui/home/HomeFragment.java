package com.example.kursovaya.ui.home;

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
import com.example.kursovaya.api.StudentExamsResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView textViewGreeting, textViewName;
    private ListView listViewExams;
    private ArrayAdapter<String> examsAdapter;
    private List<String> examsList;
    private int studentId; // ID студента

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
        examsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, examsList);
        listViewExams.setAdapter(examsAdapter);

        // Загрузка данных студента и экзаменов
        if (studentId != -1) {
            loadStudentData(studentId);
            loadStudentExams(studentId);
        }

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
                    textViewName.setText(studentInfo.getFirstName() + " " + studentInfo.getLastName());
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

                    // Парсинг и добавление экзаменов в список
                    for (StudentExamsResponse exam : exams) {
                        examsList.add(exam.getDiscipline() + ": " + exam.getDate() + " (каб. " + exam.getOffice() + ")");
                    }

                    // Обновление ListView
                    examsAdapter.notifyDataSetChanged();
                } else {
                    // Обработка ошибки
                    examsList.clear();
                    examsList.add("Ошибка загрузки экзаменов");
                    examsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<StudentExamsResponse>> call, @NonNull Throwable t) {
                t.printStackTrace();
                examsList.clear();
                examsList.add("Ошибка сети");
                examsAdapter.notifyDataSetChanged();
            }
        });
    }
}