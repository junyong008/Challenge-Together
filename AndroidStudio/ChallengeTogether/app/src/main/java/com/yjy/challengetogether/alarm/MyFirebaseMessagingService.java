package com.yjy.challengetogether.alarm;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yjy.challengetogether.util.Util;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    Util util = new Util(MyFirebaseMessagingService.this);

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        // 앱을 디바이스에서 처음 실행하거나 토큰 값이 변경될때 실행된다.
        // 하지만 아직 로그인도 하지 않은 사용자의 토큰값을 받아와도 DB에 저장할 수 없으니 HomeFragment에서 DB에 저장하도록 설정

        // 아래는 만약 사용자가 접속하지 않는 사이 토큰이 만료되어 재발급 됐을 경우에 유저가 접속하기 전 알람을 수신하게 하기 위한 재발급 및 서버 동기화 코드
        if (!TextUtils.isEmpty(util.getSessionKey())) {
            util.registerTokenToServer(token);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // 메시지 데이터에서 제목과 내용을 추출

        String type = remoteMessage.getData().get("type");
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("body");

        // https://firebase.google.com/docs/cloud-messaging/concept-options?hl=ko 참조
        // json 형식에 따라 커스텀 해서 받아올 수도 있고 기본 제공 json으로 받아올 수도 있는듯
        // String message = remoteMessage.getData().get("");

        // 지정된 알림 설정이 꺼져있으면 알림을 울리지 않음
        if (!util.getPushSettings(type)) {
            return;
        }

        NotificationHelper notificationHelper = new NotificationHelper(MyFirebaseMessagingService.this);
        notificationHelper.createNotification(type, title, message, remoteMessage.getData().get("idx"));
    }
}
