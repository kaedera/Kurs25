package com.example.kursovaya.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Body;
import java.util.List;

public interface ApiService {
    @POST("/api/login")
    Call<LoginResponse> login(@Body User user);

    @GET("/api/student/{id_student}")
    Call<StudentInfoResponse> getStudentInfo(@Path("id_student") int studentId);

    @GET("/api/student/{id_student}/marks")
    Call<List<StudentMarksResponse>> getStudentMarks(@Path("id_student") int studentId);

    @GET("/api/student/{id_student}/exams")
    Call<List<StudentExamsResponse>> getStudentExams(@Path("id_student") int studentId);
}
