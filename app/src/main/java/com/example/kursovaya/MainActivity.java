package com.example.kursovaya;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button authorizationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Связываем кнопку с элементом в XML
        authorizationButton = findViewById(R.id.authorizationButton);

        // Устанавливаем слушатель нажатий на кнопку
        authorizationButton.setOnClickListener(view -> {
            // Создаем Intent для перехода на Autho Activity
            Intent intent = new Intent(MainActivity.this, Autho.class);

            // Запускаем Autho Activity
            startActivity(intent);
        });

    }

}