package com.yjy.challengetogether.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.fragment.WalkthroughFragment1;
import com.yjy.challengetogether.fragment.WalkthroughFragment2;
import com.yjy.challengetogether.fragment.WalkthroughFragment3;
import com.yjy.challengetogether.fragment.WalkthroughFragment4;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class WalkthroughActivity extends AppCompatActivity {

    private ViewPager vpPager;
    private TextView textView_title, textView_content;
    private ImageButton ibutton_left, ibutton_right;
    private CircleIndicator indicator;
    private Button button_start;
    private List<Fragment> fragmentList = new ArrayList<>();
    private String[] pageTitle = {"다양한 챌린지 제공", "작심삼일? 이젠 작심백일!", "같이 중독에서 벗어나기", "커뮤니티"};
    private String[] pageContent = {"무한한 챌린지 제공\n광고 없이 원하는 챌린지 참여하기", "실시간 시간표시/캘린더/위젯/명언 기능\n리셋 기록으로 과거 돌아보기", "실시간 랭킹을 보면서 경쟁하고,\n다같이 도전하며 성취감을 얻어보세요", "커뮤니티에서 경험을 공유하며 소통하고,\n원하는 글귀를 북마크해 소장해보세요!"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);

        vpPager = findViewById(R.id.vpPager);
        textView_title = findViewById(R.id.textView_title);
        textView_content = findViewById(R.id.textView_content);
        ibutton_left = findViewById(R.id.ibutton_left);
        ibutton_right = findViewById(R.id.ibutton_right);
        indicator = findViewById(R.id.indicator);
        button_start = findViewById(R.id.button_start);

        // 프레그먼트 생성
        Fragment walkthroughFragment1 = new WalkthroughFragment1();
        Fragment walkthroughFragment2 = new WalkthroughFragment2();
        Fragment walkthroughFragment3 = new WalkthroughFragment3();
        Fragment walkthroughFragment4 = new WalkthroughFragment4();

        // 생성된 프래그먼트를 리스트에 추가
        fragmentList.add(walkthroughFragment1);
        fragmentList.add(walkthroughFragment2);
        fragmentList.add(walkthroughFragment3);
        fragmentList.add(walkthroughFragment4);

        // ViewPager와 어댑터 연결
        MyPagerAdapter adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), fragmentList);
        vpPager.setAdapter(adapterViewPager);

        // 페이지를 넘길 시 설명 텍스트 변경
        textView_title.setText(pageTitle[0]);
        textView_content.setText(pageContent[0]);
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {

                // 페이지 이동 화살표 숨김/보임 처리
                if (position == 0) {
                    ibutton_left.setVisibility(View.GONE);
                    ibutton_right.setVisibility(View.VISIBLE);
                } else if (position == fragmentList.size() - 1) {
                    ibutton_left.setVisibility(View.VISIBLE);
                    ibutton_right.setVisibility(View.GONE);

                    indicator.setVisibility(View.INVISIBLE);
                    button_start.setVisibility(View.VISIBLE);
                } else {
                    ibutton_left.setVisibility(View.VISIBLE);
                    ibutton_right.setVisibility(View.VISIBLE);

                    indicator.setVisibility(View.VISIBLE);
                    button_start.setVisibility(View.GONE);
                }

                // 페이지 변경 시 내용 변경
                textView_title.setText(pageTitle[position]);
                textView_content.setText(pageContent[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        // 페이지 넘김 버튼 클릭
        ibutton_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpPager.setCurrentItem(vpPager.getCurrentItem() - 1);
            }
        });
        ibutton_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpPager.setCurrentItem(vpPager.getCurrentItem() + 1);
            }
        });

        // 시작하기 버튼 클릭
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalkthroughActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 인디케이터 설정
        indicator.setViewPager(vpPager);
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;

        public MyPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragmentList) {
            super(fragmentManager);
            this.fragmentList = fragmentList;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return fragmentList.size();
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
    }
}
