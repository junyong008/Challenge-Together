package com.yjy.challengetogether.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yjy.challengetogether.R;
import com.yjy.challengetogether.fragment.CommunityFragment;
import com.yjy.challengetogether.fragment.HomeFragment;
import com.yjy.challengetogether.fragment.MyFragment;
import com.yjy.challengetogether.fragment.TogetherFragment;

public class MainpageActivity extends AppCompatActivity {
    BottomNavigationView bottomnav_mainpage;
    Fragment fragment_home;
    Fragment fragment_together;
    Fragment fragment_community;
    Fragment fragment_my;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        // 프레그먼트 생성
        fragment_home = new HomeFragment();
        fragment_together = new TogetherFragment();
        fragment_community = new CommunityFragment();
        fragment_my = new MyFragment();

        // 다른 액티비티에서 메인페이지를 실행할때 특정 Fragment를 실행하는지 여부 확인
        String fragmentToShow = getIntent().getStringExtra("FRAGMENT_TO_SHOW");
        if (fragmentToShow != null) {
            Fragment fragment = null;

            if (fragmentToShow.equals("fragment_home")) {
                fragment = fragment_home;
            } else if (fragmentToShow.equals("fragment_together")) {
                fragment = fragment_together;
            } else if (fragmentToShow.equals("fragment_community")) {
                fragment = fragment_community;
            } else if (fragmentToShow.equals("fragment_my")) {
                fragment = fragment_my;
            }

            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction() . replace(R.id.fragment_mainpage, fragment).commitAllowingStateLoss();
        }

        // 바텀 네비게이션
        bottomnav_mainpage = findViewById(R.id.bottomnav_mainpage);
        bottomnav_mainpage.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); // TogetherFragment에서 addToBackStack로 쌓인 스택 모두 삭제
                        getSupportFragmentManager().beginTransaction() . replace(R.id.fragment_mainpage,fragment_home).commitAllowingStateLoss();
                        return true;
                    case R.id.action_together:
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); // TogetherFragment에서 addToBackStack로 쌓인 스택 모두 삭제
                        getSupportFragmentManager().beginTransaction() . replace(R.id.fragment_mainpage,fragment_together).commitAllowingStateLoss();
                        return true;
                    case R.id.action_community:
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); // TogetherFragment에서 addToBackStack로 쌓인 스택 모두 삭제
                        getSupportFragmentManager().beginTransaction() . replace(R.id.fragment_mainpage,fragment_community).commitAllowingStateLoss();
                        return true;
                    case R.id.action_my:
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); // TogetherFragment에서 addToBackStack로 쌓인 스택 모두 삭제
                        getSupportFragmentManager().beginTransaction() . replace(R.id.fragment_mainpage,fragment_my).commitAllowingStateLoss();
                        return true;
                }
                return true;
            }
        });

    }
}