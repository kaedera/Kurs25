package com.example.kursovaya.api;

public class StudentMarksResponse {
    private String Discipline;
    private String Mark;
    private String Date;
    private int ID_discipline;

    public String getDiscipline() {
        return Discipline;
    }

    public void setDiscipline(String discipline) {
        Discipline = discipline;
    }

    public String getMark() {
        return Mark;
    }

    public void setMark(String mark) {
        Mark = mark;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getID_discipline() {
        return ID_discipline;
    }

    public void setID_discipline(int ID_discipline) {
        this.ID_discipline = ID_discipline;
    }
}
