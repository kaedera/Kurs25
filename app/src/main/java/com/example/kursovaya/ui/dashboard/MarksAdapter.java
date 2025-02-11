package com.example.kursovaya.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kursovaya.R;
import com.example.kursovaya.api.StudentMarksResponse;

import java.util.List;

public class MarksAdapter extends ArrayAdapter<StudentMarksResponse> {

    private final Context context;
    private final List<StudentMarksResponse> marksList;

    public MarksAdapter(Context context, List<StudentMarksResponse> marksList) {
        super(context, R.layout.item_mark, marksList);
        this.context = context;
        this.marksList = marksList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_mark, parent, false);

        TextView textViewDiscipline = rowView.findViewById(R.id.textViewDiscipline);
        TextView textViewMarks = rowView.findViewById(R.id.textViewMarks);
        TextView textViewAverage = rowView.findViewById(R.id.textViewAverage);

        StudentMarksResponse mark = marksList.get(position);
        textViewDiscipline.setText(mark.getDiscipline());

        // Оценки
        String[] marksArray = mark.getMark().split(" ");
        StringBuilder marksText = new StringBuilder();

        for (String markValue : marksArray) {
            // Цвет оценки
            int color = getColorForMark(markValue);
            marksText.append("<font color='").append(color).append("'>").append(markValue).append("</font> ");
        }

        // Установка текста с цветами
        textViewMarks.setText(android.text.Html.fromHtml(marksText.toString()));

        // Вычисление среднего балла
        double average = calculateAverage(mark.getMark());
        textViewAverage.setText(String.format("Средний балл: %.2f", average));

        return rowView;
    }

    private int getColorForMark(String mark) {
        try {
            int markValue = Integer.parseInt(mark);
            switch (markValue) {
                case 5:
                    return getContext().getResources().getColor(R.color.color_mark_5);
                case 4:
                    return getContext().getResources().getColor(R.color.color_mark_4);
                case 3:
                    return getContext().getResources().getColor(R.color.color_mark_3);
                case 2:
                    return getContext().getResources().getColor(R.color.color_mark_2);
                default:
                    return android.R.color.black;
            }
        } catch (NumberFormatException e) {
            return android.R.color.black;
        }
    }

    private double calculateAverage(String marks) {
        String[] marksArray = marks.split(" ");
        double sum = 0;
        int count = 0;

        for (String mark : marksArray) {
            try {
                int m = Integer.parseInt(mark);
                sum += m;
                count++;
            } catch (NumberFormatException e) {
                // Пропускаем нечисловые значения
            }
        }

        return count > 0 ? sum / count : 0;
    }
}