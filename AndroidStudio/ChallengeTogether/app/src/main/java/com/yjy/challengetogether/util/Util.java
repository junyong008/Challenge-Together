package com.yjy.challengetogether.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

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
}
