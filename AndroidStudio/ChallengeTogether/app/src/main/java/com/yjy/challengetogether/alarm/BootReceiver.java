package com.yjy.challengetogether.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yjy.challengetogether.util.Util;

public class BootReceiver extends BroadcastReceiver {

    private Util util;

    @Override
    public void onReceive(Context context, Intent intent) {
        util = new Util(context);
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // 부팅 완료 시 saveOngoingChallenges() 함수를 실행하여 workmanager 갱신, 위젯 업데이트
            util.saveOngoingChallenges();
        }
    }
}
