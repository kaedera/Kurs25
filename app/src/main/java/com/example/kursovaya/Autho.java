package com.example.kursovaya;

import com.example.kursovaya.api.User;
import com.example.kursovaya.api.ApiService;
import com.example.kursovaya.api.RetrofitClient;
import com.example.kursovaya.api.LoginResponse;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.view.View;

public class Autho extends AppCompatActivity {

    ImageButton backButton;
    private ImageView passwordVisibility;
    private EditText passwordEditText;
    private EditText usernameEditText;
    Button loginButton;
    private ProgressBar progressBar;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_autho);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Связываем элементы с XML
        backButton = findViewById(R.id.backButton);
        passwordVisibility = findViewById(R.id.passwordVisibility);
        passwordEditText = findViewById(R.id.passwordEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar); // Прогресс-бар

        // Устанавливаем слушатель нажатий на кнопку "назад"
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(Autho.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Устанавливаем слушатель нажатий на кнопку видимости пароля
        passwordVisibility.setOnClickListener(view -> {
            if (isPasswordVisible) {
                // Показываем пароль
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordVisibility.setImageResource(R.drawable.visibility_on);
            } else {
                // Скрываем пароль
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordVisibility.setImageResource(R.drawable.visibility_off);
            }
            isPasswordVisible = !isPasswordVisible;

            // Перемещаем курсор в конец текста
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        // Устанавливаем слушатель нажатий на кнопку "войти"
        loginButton.setOnClickListener(view -> {
            String login = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(Autho.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            } else if (!isNetworkAvailable()) {
                Toast.makeText(Autho.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE); // Показываем прогресс-бар

                User user = new User(login, password);
                ApiService apiService = RetrofitClient.getApiService();
                Call<LoginResponse> call = apiService.login(user);

                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                        progressBar.setVisibility(View.GONE); // Скрываем прогресс-бар

                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse loginResponse = response.body();
                            if (loginResponse.getMessage().equals("Авторизация успешна")) {
                                int studentId = loginResponse.getID_user(); // Получаем ID студента
                                Intent intent = new Intent(Autho.this, MainScreen.class);
                                intent.putExtra("STUDENT_ID", studentId); // Передаём ID студента
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Autho.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Autho.this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE); // Скрываем прогресс-бар
                        if (t instanceof java.net.ConnectException) {
                            Toast.makeText(Autho.this, "Ошибка сети: Не удается подключиться к серверу", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Autho.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    // Проверка доступности сети
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}