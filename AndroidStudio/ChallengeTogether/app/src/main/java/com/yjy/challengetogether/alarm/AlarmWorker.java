package com.yjy.challengetogether.alarm;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.yjy.challengetogether.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AlarmWorker extends Worker {

    private Context context;
    private Util util;

    public AlarmWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        util = new Util(context);
    }

    @Override
    public Result doWork() {

        // 만약 성공한 챌린지 알람을 받기로 설정돼 있다면
        if (util.getPushSettings("successchallenge")) {

            // SharedPreferences에 저장된 도전들 받아와서 현재시간과 비교하여 완료한 챌린지가 있는지 검사
            try {
                String result = util.getOngoingChallenges();
                String recentCompleteChallengeTitle = "";   // 완료한 챌린지들의 제목을 담을 String
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    // 완료된 챌린지가 존재하면 recentCompleteChallengeTitle에 콤마(,)를 구분자로 저장
                    int dayOfCurrentTime = Integer.parseInt(util.DiffWithLocalTime(obj.getString("RECENTSTARTTIME"), "DAY"));
                    int endTime = obj.getInt("ENDTIME");
                    if (dayOfCurrentTime >= endTime) {
                        if (TextUtils.isEmpty(recentCompleteChallengeTitle)) {
                            recentCompleteChallengeTitle = recentCompleteChallengeTitle + "\"" + obj.getString("TITLE") + "\"";
                        } else {
                            recentCompleteChallengeTitle = recentCompleteChallengeTitle + ", \"" + obj.getString("TITLE") + "\"";
                        }
                    }

                }

                // recentCompleteChallengeTitle가 비어있지 않다면(완료한 챌린지가 있다면) 알람을 보내고 서버와 도전 정보들 동기화하기
                if (!TextUtils.isEmpty(recentCompleteChallengeTitle)) {

                    String notificationTitle = "챌린지 목표를 달성했습니다!";
                    String notificationContent = recentCompleteChallengeTitle + " 챌린지를 성공하셨습니다!";
                    String notificationType = "successchallenge";

                    // Notification 띄우기
                    NotificationHelper notificationHelper = new NotificationHelper(context);
                    notificationHelper.createNotification(notificationType, notificationTitle, notificationContent, "");

                    // UserAlarm 테이블에 알림 내역 추가
                    util.addAlarmRecord(notificationTitle, notificationContent, notificationType);

                    // 스토리지의 도전 정보 서버와 동기화
                    util.saveOngoingChallenges();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return Result.success();
    }
}
