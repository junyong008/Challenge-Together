package com.yjy.challengetogether.util;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.yjy.challengetogether.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util extends Application {
    private Context context;

    public Util(Context context) {
        this.context = context;
    }


    /** 문자열이 세션키인지 아닌지 검사 */
    public static boolean isSessionKey(String checkString) {
        if (checkString.matches("^[a-z0-9]{26}$")) {
            return true;
        } else {
            return false;
        }
    }

    /** 문자열로 부터 해시값 추출 */
    public static String getHash(String str) {
        String digest = "";
        try {
            MessageDigest sh = MessageDigest.getInstance("SHA-256"); // SHA-256 해시함수를 사용
            sh.update(str.getBytes()); // str의 문자열을 해싱하여 sh에 저장
            byte byteData[] = sh.digest(); // sh 객체의 다이제스트를 얻는다.

            //얻은 결과를 string으로 변환
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            digest = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            digest = null;
        }
        return digest;
    }

    /** 스토리지에 세션값 저장 */
    public void saveSessionKey(String sessionKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("logininfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sessionKey", sessionKey);
        editor.apply();
    }

    /** 스토리지에서 세션값 불러오기 */
    public String getSessionKey() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("logininfo", context.MODE_PRIVATE);
        return sharedPreferences.getString("sessionKey", "");
    }

    /** 문자열이 JSON인지 검사 */
    public static boolean isJson(String jsonString) {
        try {
            new JSONObject(jsonString);
        } catch (JSONException jsonEx1) {
            try {
                new JSONArray(jsonString);
            } catch (JSONException jsonEx2) {
                return false;
            }
        }
        return true;
    }

    /** 시간(초)를 nDAYS nHRS nMINS nSECS 형식으로 변환 */
    public String secondsToDHMS(long intputSeconds, String Type) {
        StringBuilder formattedTime = new StringBuilder();

        long days = intputSeconds / (24 * 60 * 60);
        intputSeconds %= (24 * 60 * 60);
        formattedTime.append(days).append(" DAYS ");

        long hours = intputSeconds / (60 * 60);
        intputSeconds %= (60 * 60);
        formattedTime.append(hours).append(" HRS\n");


        if (Type.indexOf("DHMS") != -1) {
            long minutes = intputSeconds / 60;
            intputSeconds %= 60;
            formattedTime.append(minutes).append(" MINS ");


            long seconds = intputSeconds;
            formattedTime.append(seconds).append(" SECS");
        }

        return formattedTime.toString().trim();
    }

    public interface OnConfirmListener {
        void onConfirm(boolean isConfirmed);
    }

    public void showCustomDialog(final OnConfirmListener listener, String Message){
        Dialog dialog = new Dialog(context); // Dialog 초기화
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dialog.setCancelable(false); // 배경 클릭해도 닫히지 않게
        dialog.setContentView(R.layout.dialog_confirm); // xml 레이아웃 파일과 연결
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 투명 배경

        TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
        confirmTextView.setText(Message);

        // 아니오 버튼
        Button noBtn = dialog.findViewById(R.id.noButton);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss(); // 다이얼로그 닫기
                listener.onConfirm(false); // 취소 버튼 클릭 시 false를 전달
            }
        });

        // 네 버튼
        Button yesButton = dialog.findViewById(R.id.yesButton);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listener.onConfirm(true); // 확인 버튼 클릭 시 true를 전달
            }
        });

        dialog.show(); // 다이얼로그 띄우기
    }
}
