package com.yjy.challengetogether.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;
import com.yjy.challengetogether.R;
import com.yjy.challengetogether.util.Util;

public class PushSettingActivity extends AppCompatActivity {

    private Util util = new Util(PushSettingActivity.this);

    private ImageButton ibutton_back;
    private ToolTipsManager mToolTipsManager;
    private ImageView ivbutton_info;
    private LabeledSwitch switch_all, switch_successchallenge, switch_newparticipate, switch_participantreset, switch_readyroomstart, switch_communitypost, switch_communitycomment;

    @Override
    public void onBackPressed() {
        saveSettingsToSharedPreferences();  // 뒤로가기 할때 알람 설정 정보들을 스토리지에 저장
        util.setWorkManager(); // 알림을 사용자가 종료했을 수도 있으니 WorkManger 갱신. 사용자가 알림을 끄면 바로 WorkManager를 해지하도록
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushsetting);

        ibutton_back = findViewById(R.id.ibutton_back);
        ivbutton_info = findViewById(R.id.ivbutton_info);
        switch_all = findViewById(R.id.switch_all);
        switch_successchallenge = findViewById(R.id.switch_successchallenge);
        switch_newparticipate = findViewById(R.id.switch_newparticipate);
        switch_participantreset = findViewById(R.id.switch_participantreset);
        switch_readyroomstart = findViewById(R.id.switch_readyroomstart);
        switch_communitypost = findViewById(R.id.switch_communitypost);
        switch_communitycomment = findViewById(R.id.switch_communitycomment);

        // 뒤로가기 버튼 클릭
        ibutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 안내(info) 버튼 클릭
        ivbutton_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 툴팁으로 기록이 어떤걸 의미하는지 상세 설명

                // 이전에 보여진 툴팁이 있다면 제거
                if (mToolTipsManager != null) {
                    mToolTipsManager.findAndDismiss(ivbutton_info);
                }

                mToolTipsManager = new ToolTipsManager();
                ToolTip.Builder builder = new ToolTip.Builder(PushSettingActivity.this, ivbutton_info, findViewById(R.id.ConstraintLayout_parent), "알림이 정상적으로 울리지 않을경우,\n\n휴대전화 설정 -> 애플리케이션 -> 알림\n에 들어가 알림을 허용해주세요!", ToolTip.POSITION_BELOW);

                builder.setBackgroundColor(getResources().getColor(R.color.black));
                builder.setTextAppearance(R.style.TooltipTextAppearance);
                mToolTipsManager.show(builder.build());



                // 2초 후에 툴팁을 숨기기
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mToolTipsManager.findAndDismiss(ivbutton_info);
                    }
                }, 2000);
            }
        });

        // 알림설정이 꺼져있으면 모두 비활성화
        boolean isAllOn = util.getPushSettings("allpush");
        switch_all.setOn(isAllOn);

        if (!isAllOn) {
            switch_successchallenge.setEnabled(false);
            switch_newparticipate.setEnabled(false);
            switch_participantreset.setEnabled(false);
            switch_readyroomstart.setEnabled(false);
            switch_communitypost.setEnabled(false);
            switch_communitycomment.setEnabled(false);

            switch_successchallenge.setOn(false);
            switch_newparticipate.setOn(false);
            switch_participantreset.setOn(false);
            switch_readyroomstart.setOn(false);
            switch_communitypost.setOn(false);
            switch_communitycomment.setOn(false);
        } else {
            // 알림 설정이 켜져있으면 스토리지에 저장된 세팅값 받아와서 설정
            switch_successchallenge.setOn(util.getPushSettings("successchallenge"));
            switch_newparticipate.setOn(util.getPushSettings("newparticipate"));
            switch_participantreset.setOn(util.getPushSettings("participantreset"));
            switch_readyroomstart.setOn(util.getPushSettings("readyroomstart"));

            switch_communitypost.setOn(util.getPushSettings("communitypost"));
            switch_communitycomment.setOn(util.getPushSettings("communitycomment"));
        }

        // 전체 알림 토글 변경
        switch_all.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (!isOn) {
                    // 토글이 꺼질때
                    switch_successchallenge.setEnabled(false);
                    switch_newparticipate.setEnabled(false);
                    switch_participantreset.setEnabled(false);
                    switch_readyroomstart.setEnabled(false);
                    switch_communitypost.setEnabled(false);
                    switch_communitycomment.setEnabled(false);

                    switch_successchallenge.setOn(false);
                    switch_newparticipate.setOn(false);
                    switch_participantreset.setOn(false);
                    switch_readyroomstart.setOn(false);
                    switch_communitypost.setOn(false);
                    switch_communitycomment.setOn(false);
                } else {
                    // 토글이 활성화될때
                    switch_successchallenge.setEnabled(true);
                    switch_newparticipate.setEnabled(true);
                    switch_participantreset.setEnabled(true);
                    switch_readyroomstart.setEnabled(true);
                    switch_communitypost.setEnabled(true);
                    switch_communitycomment.setEnabled(true);

                    switch_successchallenge.setOn(true);
                    switch_newparticipate.setOn(true);
                    switch_participantreset.setOn(true);
                    switch_readyroomstart.setOn(true);
                    switch_communitypost.setOn(true);
                    switch_communitycomment.setOn(true);
                }
            }
        });
    }

    private void saveSettingsToSharedPreferences() {
        util.savePushSettings("allpush", switch_all.isOn());
        util.savePushSettings("successchallenge", switch_successchallenge.isOn());
        util.savePushSettings("newparticipate", switch_newparticipate.isOn());
        util.savePushSettings("participantreset", switch_participantreset.isOn());
        util.savePushSettings("readyroomstart", switch_readyroomstart.isOn());

        util.savePushSettings("communitypost", switch_communitypost.isOn());
        util.savePushSettings("communitycomment", switch_communitycomment.isOn());
    }
}