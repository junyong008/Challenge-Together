package com.yjy.challengetogether;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainpageActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private String TAG = "메인 액티비티";

    Fragment fragment_home;
    Fragment fragment_together;
    Fragment fragment_my;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        // 프레그먼트 생성
        fragment_home = new HomeFragment();
        fragment_together = new TogetherFragment();
        fragment_my = new MyFragment();

        // 바텀 네비게이션

        bottomNavigationView = findViewById(R.id.main_bottomnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        getSupportFragmentManager().beginTransaction() . replace(R.id.main_fragment,fragment_home).commitAllowingStateLoss();
                        return true;
                    case R.id.action_together:
                        getSupportFragmentManager().beginTransaction() . replace(R.id.main_fragment,fragment_together).commitAllowingStateLoss();
                        return true;
                    case R.id.action_my:
                        getSupportFragmentManager().beginTransaction() . replace(R.id.main_fragment,fragment_my).commitAllowingStateLoss();
                        return true;
                }
                return true;
            }
        });
    }
}