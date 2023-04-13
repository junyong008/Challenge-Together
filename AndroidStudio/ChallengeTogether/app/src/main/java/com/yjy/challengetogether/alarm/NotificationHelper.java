package com.yjy.challengetogether.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.activity.MainpageActivity;
import com.yjy.challengetogether.activity.PostActivity;
import com.yjy.challengetogether.activity.ReadyRoomActivity;
import com.yjy.challengetogether.activity.StartRoomActivity;
import com.yjy.challengetogether.util.Util;

import io.github.muddz.styleabletoast.StyleableToast;

public class NotificationHelper {

    private Context mContext;
    private static Util util;
    private static String NOTIFICATION_CHANNEL_ID = "CHALLENGE";


    public NotificationHelper(Context context) {
        mContext = context;
        util = new Util(mContext);
    }

    // 한번 실행 시 이후 재호출해도 동작 안함
    public static void createNotificationChannel(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                // NotificationChannel 초기화
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "모든 알림", NotificationManager.IMPORTANCE_DEFAULT);

                // Configure the notification channel
                notificationChannel.setDescription("상세 설정은 앱 내 알림설정을 이용해주세요.");
                notificationChannel.enableLights(true); // 화면활성화 설정
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500}); // 진동패턴 설정
                notificationChannel.enableVibration(true); // 진동 설정
                notificationManager.createNotificationChannel(notificationChannel); // channel 생성
            }
        } catch (NullPointerException nullException) {
            StyleableToast.makeText(context, "푸시 알림 채널 생성에 실패했습니다. 앱을 재실행하거나 재설치해주세요.", R.style.errorToast).show();
            nullException.printStackTrace();
        }
    }



    public void createNotification(String workName, String title, String content, String data) {

        createNotificationChannel(mContext);

        // Notificatoin을 이루는 공통 부분 정의
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.appicongray) // 기본 제공되는 이미지
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true); // 클릭 시 Notification 제거

        if (workName.equals("successchallenge")) {
            int requestCode = 0;

            Intent intent = new Intent(mContext, MainpageActivity.class); // 클릭 시 MainpageActivity 호출
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT); // 대기열에 이미 있다면 MainActivity를 활성화
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // Notification 클릭 시 동작할 Intent 입력, 중복 방지를 위해 FLAG_CANCEL_CURRENT로 설정, CODE를 다르게하면 개별 생성
            // Code가 같으면 같은 알림으로 인식하여 갱신작업 진행
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);

            // Notification 제목, 컨텐츠 설정
            notificationBuilder.setContentTitle(title).setContentText(content).setContentIntent(pendingIntent);

            if (notificationManager != null) {
                notificationManager.notify(requestCode, notificationBuilder.build());
            }

        } else if (workName.equals("newparticipate") || workName.equals("readyroomstart") || workName.equals("participantreset")) {
            int requestCode = (int) System.currentTimeMillis(); // requestCode를 현재시간으로 지정해 중복해서 만들어짐.
            String roomidx = data;

            Intent intent1 = new Intent(mContext, MainpageActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            Intent intent2;
            if (workName.equals("newparticipate")) {
                intent2 = new Intent(mContext, ReadyRoomActivity.class);
                intent2.putExtra("roomidx", roomidx);
            } else {
                intent2 = new Intent(mContext, StartRoomActivity.class);
                intent2.putExtra("roomidx", roomidx);
            }

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
            stackBuilder.addNextIntent(intent1);
            stackBuilder.addNextIntent(intent2);

            // PendingIntent 생성 : Notification을 클릭했을때 위의 TaskStackBuilder intent를 쭉 실행함.
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_IMMUTABLE);

            // Notification 제목, 컨텐츠 설정
            notificationBuilder.setContentTitle(title).setContentText(content).setContentIntent(pendingIntent);

            if (notificationManager != null) {
                notificationManager.notify(requestCode, notificationBuilder.build());
            }
        } else if (workName.equals("communitypost") || workName.equals("communitycomment")) {
            int requestCode = (int) System.currentTimeMillis(); // requestCode를 현재시간으로 지정해 중복해서 만들어짐.
            String postidx = data;

            Intent intent1 = new Intent(mContext, MainpageActivity.class);
            intent1.putExtra("FRAGMENT_TO_SHOW", "fragment_community");
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            Intent intent2 = new Intent(mContext, PostActivity.class);
            intent2.putExtra("postidx", postidx);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
            stackBuilder.addNextIntent(intent1);
            stackBuilder.addNextIntent(intent2);

            // PendingIntent 생성 : Notification을 클릭했을때 위의 TaskStackBuilder intent를 쭉 실행함.
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_IMMUTABLE);

            // Notification 제목, 컨텐츠 설정
            notificationBuilder.setContentTitle(title).setContentText(content).setContentIntent(pendingIntent);

            if (notificationManager != null) {
                notificationManager.notify(requestCode, notificationBuilder.build());
            }
        }
    }

    // 알림 채널이 생성돼있는지 검사
    public static Boolean isNotificationChannelCreated(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                return notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) != null;
            }
            return true;
        } catch (NullPointerException nullException) {
            StyleableToast.makeText(context, "푸시 알림 기능에 문제가 발생했습니다.\n앱을 재실행해주세요.", R.style.errorToast).show();
            return false;
        }
    }

}
