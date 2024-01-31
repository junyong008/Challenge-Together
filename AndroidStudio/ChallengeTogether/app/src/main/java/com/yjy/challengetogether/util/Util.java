package com.yjy.challengetogether.util;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.activity.LoginActivity;
import com.yjy.challengetogether.alarm.AlarmWorker;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.widget.MainWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.github.muddz.styleabletoast.StyleableToast;

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

    /** 스토리지에서 사용자가 Walkthrough를 경험했는지 확인. 경험안했다면 경험했다고 변경하고 경험시키기 */
    public boolean checkIsUserWalked() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("walkthrough", context.MODE_PRIVATE);
        Boolean isWalked = sharedPreferences.getBoolean("isWalked", false);

        // 아직 경험하지 않았다면 경험하게 하도록 하고 스토리지에 경험했음을 저장
        if (!isWalked) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isWalked", true);
            editor.apply();

            return false;
        } else {
            return true;
        }
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

    /** 서버에 저장된 세션 삭제 + 스토리지에 저장된 모든 정보 삭제, 위젯 비우기, WorkManager 해제, FCM 토큰 해제 */
    public void initSettings() {

        // DB에 등록된 FCM 토큰값 비우기
        registerTokenToServer("");

        // 서버에 저장된 세션 삭제
        OnTaskCompleted onDeleteSessionTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                if (result.indexOf("DELETE SESSION SUCCESS") != -1) {

                    // 스토리지에 저장된 세션키 값 삭제
                    SharedPreferences sharedPreferences = context.getSharedPreferences("logininfo", context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    // 스토리지에 저장된 알림 설정값, 진행중인 도전 정보값 삭제
                    sharedPreferences = context.getSharedPreferences("appinfo", context.MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    // 위젯 업데이트
                    Intent intent = new Intent(context, MainWidget.class);
                    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    context.sendBroadcast(intent);

                    // WorkManager 해제
                    WorkManager.getInstance(context).cancelAllWorkByTag("AlarmWorker");
                }
            }
        };

        HttpAsyncTask_Util deleteSessionTask = new HttpAsyncTask_Util(context, onDeleteSessionTaskCompleted);
        String phpFile = "service 1.1.0.php";
        String postParameters = "service=deletesession";

        deleteSessionTask.execute(phpFile, postParameters, getSessionKey());
    }


    // WorkManager를 알림 설정이 켜져있으면 15분마다 AlarmWorker 클래스를 실행하도록 설정. AlarmWorker에서 조건을 확인해 조건이 충족돼면 사용자에게 알림 전송
    public void setWorkManager() {

        // 알림 설정이 켜져있으면 WorkManager 등록. 이미 등록돼 있으면 요청은 무시된다.
        if (getPushSettings("allpush")) {
            PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(AlarmWorker.class, 15, TimeUnit.MINUTES);
            builder.addTag("AlarmWorker");
            PeriodicWorkRequest workRequest = builder.build();
            WorkManager.getInstance(context).enqueueUniquePeriodicWork("uniqueWork", ExistingPeriodicWorkPolicy.KEEP, workRequest);

        // 알림 설정이 꺼져있으면 기존 WorkManager 해제
        } else {
            WorkManager.getInstance(context).cancelAllWorkByTag("AlarmWorker");
        }
    }


    /** 스토리지에 진행중인 도전 정보들 서버로 부터 받아와 저장 */
    public void saveOngoingChallenges() {
        OnTaskCompleted onLoadRoomTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                if (isJson(result) || result.indexOf("NO ROOM") != -1) {

                    SharedPreferences sharedPreferences = context.getSharedPreferences("appinfo", context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // 방이 없으면 스토리지에 빈값 저장
                    if (result.indexOf("NO ROOM") != -1) {
                        editor.putString("challenges", "");

                    // 방이 있으면 해당 방들
                    } else {
                        editor.putString("challenges", result);
                    }
                    editor.apply();

                    // 위젯 업데이트
                    Intent intent = new Intent(context, MainWidget.class);
                    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    context.sendBroadcast(intent);

                    // 알림 workmanager 갱신
                    setWorkManager();
                }
            }
        };

        HttpAsyncTask_Util loadRoomTask = new HttpAsyncTask_Util(context, onLoadRoomTaskCompleted);
        String phpFile = "service 1.1.0.php";
        String postParameters = "service=getongoingchallenges";

        loadRoomTask.execute(phpFile, postParameters, getSessionKey());
    }

    /** 스토리지에 저장된 진행중인 도전 정보들 받아와서 반환해주기 */
    public String getOngoingChallenges() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appinfo", context.MODE_PRIVATE);
        return sharedPreferences.getString("challenges", "");
    }


    /** 스토리지에 알림 설정 정보 저장 */
    public void savePushSettings(String settingItem, boolean isOn) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appinfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(settingItem, isOn);
        editor.apply();
    }

    /** 스토리지에 저장된 알림 설정 정보 가져오기 */
    public boolean getPushSettings(String settingItem) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appinfo", context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(settingItem, true);
    }


    /** Firebase의 토큰값을 User 테이블에 등록한다. */
    public void registerTokenToServer(String token) {

        OnTaskCompleted onRegisterTokenTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                if (result.indexOf("REGISTER SUCCESS") == -1) {
                    Log.d("registerTokenToServer", "Fail to Register - " + result);
                    checkHttpResult(result);
                }
            }
        };

        HttpAsyncTask_Util registerTokenTask = new HttpAsyncTask_Util(context, onRegisterTokenTaskCompleted);
        String phpFile = "service 1.1.0.php";
        String postParameters = "service=registertoken&token=" + token;

        registerTokenTask.execute(phpFile, postParameters, getSessionKey());
    }



    /** UserAlarm 테이블에 새로운 알림을 저장한다. */
    public void addAlarmRecord(String title, String content, String type) {
        OnTaskCompleted onAddAlarmTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
            }
        };

        HttpAsyncTask_Util addAlarmTask = new HttpAsyncTask_Util(context, onAddAlarmTaskCompleted);
        String phpFile = "service 1.1.0.php";
        String postParameters = "service=addalarm&title=" + title + "&content=" + content + "&type=" + type;

        addAlarmTask.execute(phpFile, postParameters, getSessionKey());
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


    /** "0000-00-00 00:00:00" 형식 String과 현재 로컬시간의 차이를 다양한 형식으로 변환 */
    public String DiffWithLocalTime(String inputDate, String outputType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date inputTimeDate = null;

        try {
            inputTimeDate = sdf.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long currentTimeMillis = System.currentTimeMillis();
        long inputTimeMillis = inputTimeDate.getTime();

        long diffMillis = currentTimeMillis - inputTimeMillis;
        long diffSeconds = diffMillis / 1000;

        StringBuilder formattedTime = new StringBuilder();

        if (outputType.equals("SEC")) {
            return String.valueOf(diffSeconds);
        }

        long days = diffSeconds / (24 * 60 * 60);
        diffSeconds %= (24 * 60 * 60);

        if (outputType.equals("DAY")) {
            return String.valueOf(days);
        }

        long hours = diffSeconds / (60 * 60);
        diffSeconds %= (60 * 60);

        long minutes = diffSeconds / 60;
        diffSeconds %= 60;

        long seconds = diffSeconds;

        if (outputType.equals("DHMS")) {
            formattedTime.append(days).append("일 ");
            formattedTime.append(hours).append("시간 ");
            formattedTime.append(minutes).append("분 ");
            formattedTime.append(seconds).append("초");
            return formattedTime.toString().trim();
        }

        if (outputType.equals("DH")) {
            formattedTime.append(days).append("일 ");
            formattedTime.append(hours).append("시간 ");
            return formattedTime.toString().trim();
        }

        return "";
    }


    public void checkHttpResult(String result) {
        if (result.indexOf("NO SESSION") != -1) {
            StyleableToast.makeText(context, "세션이 만료되었습니다.\n다시 로그인해주세요.", R.style.errorToast).show();

            // 세션키 삭제, 로그인 액티비티 실행 후 그 외 모두 삭제
            saveSessionKey("");
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            ((Activity) context).startActivity(intent);
            ((Activity) context).finish();
        } else {
            showCustomDialog(new Util.OnConfirmListener() {
                @Override
                public void onConfirm(boolean isConfirmed, String msg) {
                    if (isConfirmed) {
                        ((Activity) context).finishAffinity();
                        System.exit(0); // 어플 종료
                    }
                }
            }, "서버와 연결이 원활하지 않습니다.\n잠시후 다시 이용해주세요.", "onlyconfirm");
            Log.d("HTTP ERROR", result);
            return;
        }
    }


    public interface OnConfirmListener {
        void onConfirm(boolean isConfirmed, String message);
    }

    public void showCustomDialog(final OnConfirmListener listener, String Message, String Type){
        Dialog dialog = new Dialog(context); // Dialog 초기화
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dialog.setCancelable(false); // 배경 클릭해도 닫히지 않게
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 투명 배경

        if (Type.equals("confirm")) {   // 단순 메시지, 확인, 취소 다이얼로그
            dialog.setContentView(R.layout.dialog_confirm); // xml 레이아웃 파일과 연결

            TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
            confirmTextView.setText(Message);

            // 아니오 버튼
            Button noBtn = dialog.findViewById(R.id.noButton);
            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss(); // 다이얼로그 닫기
                    listener.onConfirm(false, ""); // 취소 버튼 클릭 시 false를 전달
                }
            });

            // 네 버튼
            Button yesButton = dialog.findViewById(R.id.yesButton);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    listener.onConfirm(true, ""); // 확인 버튼 클릭 시 true를 전달
                }
            });

        } else if (Type.equals("onlyconfirm")) {   // 단순 메시지, 확인 다이얼로그
            dialog.setContentView(R.layout.dialog_onlyconfirm); // xml 레이아웃 파일과 연결

            TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
            confirmTextView.setText(Message);

            // 네 버튼
            Button yesButton = dialog.findViewById(R.id.yesButton);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    listener.onConfirm(true, ""); // 확인 버튼 클릭 시 true를 전달
                }
            });

        } else if (Type.equals("reset")) {  // 메시지, 에딧창, 확인, 취소 구성
            dialog.setContentView(R.layout.dialog_reset); // xml 레이아웃 파일과 연결

            TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
            confirmTextView.setText(Message);

            TextView TextView_info = dialog.findViewById(R.id.TextView_info);
            TextView_info.setText("다짐 새기기");

            EditText edit_content = dialog.findViewById(R.id.edit_content);

            // 아니오 버튼
            Button noBtn = dialog.findViewById(R.id.noButton);
            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss(); // 다이얼로그 닫기
                    listener.onConfirm(false, ""); // 취소 버튼 클릭 시 false를 전달
                }
            });

            // 네 버튼
            Button yesButton = dialog.findViewById(R.id.yesButton);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    listener.onConfirm(true, edit_content.getText().toString().trim()); // 확인 버튼 클릭 시 true를 전달하고 다짐 내용을 전달
                }
            });

        } else if (Type.equals("congrats")) {   // 이미지, 확인 구성
            dialog.setContentView(R.layout.dialog_congrats);

            TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
            confirmTextView.setText(Message);

            TextView TextView2 = dialog.findViewById(R.id.TextView2);
            TextView2.setText("축하합니다!");

            // 네 버튼
            Button yesButton = dialog.findViewById(R.id.yesButton);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    listener.onConfirm(true, ""); // 확인 버튼 클릭 시 true를 전달
                }
            });
        } else if (Type.equals("inputpasswd")) {   // 메시지, 에딧창, 확인 구성
            dialog.setContentView(R.layout.dialog_inputpasswd);

            TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
            confirmTextView.setText(Message);

            EditText edit_content = dialog.findViewById(R.id.edit_content);

            // 네 버튼
            Button yesButton = dialog.findViewById(R.id.yesButton);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    listener.onConfirm(true, edit_content.getText().toString().trim());
                }
            });
        } else if (Type.equals("report")) {   // 메시지, 라디오 선택, 확인, 취소 구성
            dialog.setContentView(R.layout.dialog_report);

            TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
            confirmTextView.setText(Message);

            // 라디오 버튼
            RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);

            // 신고 버튼
            Button yesButton = dialog.findViewById(R.id.yesButton);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton selectedRadioButton = dialog.findViewById(selectedId);
                    String selectedValue = selectedRadioButton.getText().toString();
                    dialog.dismiss();
                    listener.onConfirm(true, selectedValue); // 확인 버튼 클릭 시 true를 전달하고 선택한 라디오 버튼의 값을 전달
                }
            });

            // 닫기 버튼
            ImageButton ibutton_close = dialog.findViewById(R.id.ibutton_close);
            ibutton_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss(); // 다이얼로그 닫기
                    listener.onConfirm(false, "");
                }
            });

            // 항목 선택시 신고버튼 활성화
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // yesButton 버튼 활성화
                    yesButton.setEnabled(true);
                    yesButton.setBackgroundResource(R.drawable.button_round);
                }
            });
        } else if (Type.equals("deleteaccount")) {   // 메시지, 라디오 선택, 확인, 취소 구성
            dialog.setContentView(R.layout.dialog_deleteaccount);

            CheckBox checkBox = dialog.findViewById(R.id.checkBox);

            // 탈퇴 버튼
            Button yesButton = dialog.findViewById(R.id.yesButton);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    listener.onConfirm(true, ""); // 확인 버튼 클릭 시 true를 전달하고 선택한 라디오 버튼의 값을 전달
                }
            });

            // 닫기 버튼
            ImageButton ibutton_close = dialog.findViewById(R.id.ibutton_close);
            ibutton_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss(); // 다이얼로그 닫기
                    listener.onConfirm(false, "");
                }
            });

            // 체크박스 클릭시 회원탈퇴 버튼 활성화
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        yesButton.setEnabled(true);
                        yesButton.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_round));
                    } else {
                        yesButton.setEnabled(false);
                        yesButton.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_round_litegray));
                    }
                }
            });
        } else if (Type.equals("chooseChallenge")) {
            dialog.setContentView(R.layout.dialog_choose_challenge);

            // 새 챌린지
            CardView cardView_new = dialog.findViewById(R.id.cardView_new);
            cardView_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    listener.onConfirm(true, "");
                }
            });

            // 자유 챌린지
            CardView cardView_old = dialog.findViewById(R.id.cardView_old);
            cardView_old.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    listener.onConfirm(false, "");
                }
            });

            // 닫기 버튼
            ImageButton ibutton_close = dialog.findViewById(R.id.ibutton_close);
            ibutton_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss(); // 다이얼로그 닫기
                }
            });
        }

        dialog.show(); // 다이얼로그 띄우기
    }




    // 프로그레스 바를 제거한 HttpAsyncTask.
    // HomeFragment 에서 FCM 토큰 업데이트, 스토리지 정보 동기화 이 두가지 작업을 시행하는데 프로그레스바를 띄우면 3겹으로 프로그레스바가 생기는 것이다.
    // 또한 AlarmWorker가 완료된 챌린지를 감지하면 UserAlarm 테이블에 항목을 추가하고 스토리지 정보 동기화를 해야하는데 Worker에서 UI를 생성하는 쓰레드를 생성하면 Exception이 뜬다
    // 위 두가지 이유로 이렇게 프로그레스바를 제거한 별도의 HttpAsyncTask를 생성해서 사용한다.
    // 하지만 이는 코드의 중복, 구조상 보기싫은건 당연하다.
    // 추후 AsyncTask는 RxJava로 대체할 예정. 이는 임시로 사용하기에 이런식으로 사용한다.
    public class HttpAsyncTask_Util extends AsyncTask<String,Void,String> {

        private Context context;
        private String TAG = "HttpAsyncTask";
        private CustomProgressDialog customProgressDialog;
        private OnTaskCompleted listener;

        public HttpAsyncTask_Util(Context context, OnTaskCompleted listener) {
            this.context = context;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "finalResult - " + result);
            listener.onTaskCompleted(result);
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = null;
            String postParameters = null;
            String sessionKey = null;

            try {
                Resources res = context.getResources();
                res.getString(R.string.SERVER_IP);

                serverURL = "http://" + res.getString(R.string.SERVER_IP) + "/" + (String) params[0];   // 서버 URL
                postParameters = (String) params[1];  // 전송 파라미터
                sessionKey = (String) params[2];  // 세션키
            } catch (RuntimeException e) { }

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                // 시간초과설정 및 메소드
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");

                // 세션키도 같이 들어오면 헤더에 세션키를 포함
                if (sessionKey != null) {
                    httpURLConnection.setRequestProperty("X-Session-ID", sessionKey);
                }

                httpURLConnection.connect(); // 연결

                // 결과 받고 인코딩 설정
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));

                // 객체 비우기
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode(); // 응답 코드 200, 404, 500 ...

                // 응답 헤더들을 읽고, 세션아이디가 헤더에 포함되면 세션아이디를 읽어옴
                Map<String, List<String>> headers = httpURLConnection.getHeaderFields();
                List<String> sessionIds = headers.get("X-Session-ID");
                String sessionId = null;

                if (sessionIds != null && sessionIds.size() > 0) {
                    sessionId = sessionIds.get(0);
                }


                InputStream inputStream;
                if(responseStatusCode == httpURLConnection.HTTP_OK) { // 만약 정상적인 응답코드라면
                    inputStream=httpURLConnection.getInputStream();
                }
                else {
                    inputStream = httpURLConnection.getErrorStream(); // 만약 정상적이지 않은 응답코드라면
                }

                // StringBuilder를 사용하여 수신되는 데이터를 저장한다.
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();

                String httpResult = sb.toString();

                // url / postParameters / responseStatusCode / sessionId / httpResult
                Log.d(TAG, "url - " + url + "\npostParameters - " + postParameters + "\nresponseStatusCode - " + responseStatusCode + "\nsessionId - " + sessionId + "\nhttpResult - " + httpResult);

                // 세션값을 받아오면 세션을 반환해주고, 아니라면 http결괏값을 반환
                if (sessionId != null) {
                    return sessionId;
                }
                else {
                    return httpResult;
                }
            } catch (Exception e) { // 에러 발생시
                return  new String("HttpAsyncTask ERROR - " + e.getMessage());
            }
        }
    }
}
