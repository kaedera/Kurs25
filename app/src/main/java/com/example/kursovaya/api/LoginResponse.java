package com.example.kursovaya.api;

public class LoginResponse {
    private String message;
    private int ID_student;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getID_user() {
        return ID_student;
    }

    public void setID_user(int ID_user) {
        this.ID_student = ID_user;
    }
}
