package com.example.kursovaya.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kursovaya.R;
import com.example.kursovaya.api.StudentExamsResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExamsAdapter extends ArrayAdapter<StudentExamsResponse> {

    private final Context context;
    private final List<StudentExamsResponse> examsList;

    public ExamsAdapter(Context context, List<StudentExamsResponse> examsList) {
        super(context, R.layout.item_exam, examsList);
        this.context = context;
        this.examsList = examsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_exam, parent, false);

        TextView textViewDiscipline = rowView.findViewById(R.id.textViewDiscipline);
        TextView textViewDate = rowView.findViewById(R.id.textViewDate);
        TextView textViewOffice = rowView.findViewById(R.id.textViewOffice);

        StudentExamsResponse exam = examsList.get(position);
        textViewDiscipline.setText(exam.getDiscipline());

        // Форматирование даты
        String formattedDate = formatDate(exam.getDate());
        textViewDate.setText("Дата: " + formattedDate);

        textViewOffice.setText("Кабинет: " + exam.getOffice());

        return rowView;
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