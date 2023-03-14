package com.yjy.challengetogether;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class PostroomActivity extends AppCompatActivity {

    private ImageButton postroom_back;

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼을 눌렀을 때 처리할 로직을 작성합니다.
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postroom);

        postroom_back = findViewById(R.id.postroom_back);

        // 뒤로 가기 버튼 클릭 시
        postroom_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 애니메이션을 적용한 뒤 액티비티 종료
                finish();
                overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
            }
        });

    }

}