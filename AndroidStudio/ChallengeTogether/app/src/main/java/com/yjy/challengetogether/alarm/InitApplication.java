package com.yjy.challengetogether.alarm;

import android.app.Application;
import android.content.Context;

import com.kakao.sdk.common.KakaoSdk;
import com.navercorp.nid.NaverIdLoginSDK;
import com.yjy.challengetogether.R;

public class InitApplication extends Application {
    private Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.createNotificationChannel(getApplicationContext()); // Notification 채널 생성
        KakaoSdk.init(this, getString(R.string.KAKAO_NATIVE_APP_KEY)); // 카카오 SDK 등록
        NaverIdLoginSDK.INSTANCE.initialize(this, getString(R.string.NAVER_CLIENT_ID), getString(R.string.NAVER_CLIENT_SECRET), "챌린지 투게더"); // 네이버 SDK 등록
    }
}
