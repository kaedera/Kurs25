package com.example.kursovaya;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.kursovaya.ui.dashboard.DashboardViewModel;
import com.example.kursovaya.ui.home.HomeViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.kursovaya.databinding.ActivityMainScreenBinding;

public class MainScreen extends AppCompatActivity {

    private ActivityMainScreenBinding binding;
    private HomeViewModel homeViewModel;
    private DashboardViewModel dashboardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Проверка наличия ActionBar перед его скрытием
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding = ActivityMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Получаем ID студента из Intent
        int studentId = getIntent().getIntExtra("STUDENT_ID", -1);
        homeViewModel.setSharedData(studentId);
        dashboardViewModel.setSharedData(studentId);
        // Передаём ID студента в DashboardFragment
        if (studentId != -1) {
            Bundle bundle = new Bundle();
            bundle.putInt("STUDENT_ID", studentId);

            // Устанавливаем аргументы для DashboardFragment
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main_screen);
            navController.setGraph(R.navigation.mobile_navigation, bundle);
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Настройка навигации
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main_screen);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}