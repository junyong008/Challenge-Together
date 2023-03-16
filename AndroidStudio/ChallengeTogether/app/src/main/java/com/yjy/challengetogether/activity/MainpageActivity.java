package com.yjy.challengetogether.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yjy.challengetogether.fragment.HomeFragment;
import com.yjy.challengetogether.fragment.MyFragment;
import com.yjy.challengetogether.R;
import com.yjy.challengetogether.fragment.TogetherFragment;

public class MainpageActivity extends AppCompatActivity {
    BottomNavigationView bottomnav_mainpage;
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
        bottomnav_mainpage = findViewById(R.id.bottomnav_mainpage);
        bottomnav_mainpage.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        getSupportFragmentManager().beginTransaction() . replace(R.id.fragment_mainpage,fragment_home).commitAllowingStateLoss();
                        return true;
                    case R.id.action_together:
                        getSupportFragmentManager().beginTransaction() . replace(R.id.fragment_mainpage,fragment_together).commitAllowingStateLoss();
                        return true;
                    case R.id.action_my:
                        getSupportFragmentManager().beginTransaction() . replace(R.id.fragment_mainpage,fragment_my).commitAllowingStateLoss();
                        return true;
                }
                return true;
            }
        });
    }
}