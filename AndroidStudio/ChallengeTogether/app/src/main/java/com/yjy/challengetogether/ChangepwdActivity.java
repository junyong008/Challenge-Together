package com.yjy.challengetogether;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import io.github.muddz.styleabletoast.StyleableToast;

public class ChangepwdActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "3.37.234.141";
    private static String TAG = "phptest"; //phptest log 찍으려는 용도
    private EditText changepwd_pwd;
    private EditText changepwd_pwdcheck;
    private Button changepwd_button;

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼을 눌렀을 때 처리할 로직을 작성합니다.
        StyleableToast.makeText(ChangepwdActivity.this, "비밀번호를 변경해주세요.", R.style.errorToast).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepwd);

        changepwd_pwd = findViewById(R.id.changepwd_pwd);
        changepwd_pwdcheck = findViewById(R.id.changepwd_pwdcheck);
        changepwd_button = findViewById(R.id.changepwd_button);

        changepwd_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = changepwd_pwd.getText().toString().trim();
                String pwdcheck = changepwd_pwdcheck.getText().toString().trim();

                if (pwd.equals("") || pwdcheck.equals(""))
                {
                    StyleableToast.makeText(ChangepwdActivity.this, "정보를 입력해주세요.", R.style.errorToast).show();
                }
                else {
                    if(pwd.equals(pwdcheck)) {
                        if(pwd.length()<=5){
                            StyleableToast.makeText(ChangepwdActivity.this, "비밀번호를 6자리 이상 입력해주세요.", R.style.errorToast).show();
                        }
                        else {
                            ChangepwdActivity.ChangePWD task = new ChangepwdActivity.ChangePWD(); //PHP 통신을 위한 InsertData 클래스의 task 객체 생성

                            try {
                                String CheckResult = task.execute("http://"+IP_ADDRESS+"/service.php", getSessionKey(), "changepwd", getHash(pwd)).get();

                                if (CheckResult.indexOf("success") != -1) {
                                    // 비밀번호 변경이 성공했을경우 세션키를 초기화 하고 로그인화면으로 이동
                                    saveSessionKey("");
                                    StyleableToast.makeText(ChangepwdActivity.this, "비밀번호가 변경되었습니다.", R.style.successToast).show();

                                    // 목표 액티비티 실행 후 그 외 모두 삭제
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();

                                    return;
                                }
                                else {
                                    StyleableToast.makeText(ChangepwdActivity.this, CheckResult, R.style.errorToast).show();
                                }
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    else {
                        StyleableToast.makeText(ChangepwdActivity.this, "비밀번호가 일치하지 않습니다.", R.style.errorToast).show();
                    }
                }




            }
        });

    }



    class ChangePWD extends AsyncTask<String,Void,String> {
        CustomProgressDialog customProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //진행 다이얼로그 생성
            customProgressDialog = new CustomProgressDialog(ChangepwdActivity.this);
            customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            // 로딩창 보여주기
            customProgressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // 로딩창 닫기
            customProgressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result); // result 값 확인하기
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String) params[0];
            String sessionid = (String)params[1];
            String serviceName = (String)params[2];
            String changepwd = (String)params[3];
            String postParameters ="service="+serviceName+"&changepwd="+changepwd;

            try{
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("X-Session-ID", sessionid);    // 스토리지에서 받아온 세션키를 헤더로 설정
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                Log.d("php postParameters_데이터 : ",postParameters);
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code-" + responseStatusCode);
                Log.d(TAG, "URL : " + url);

                InputStream inputStream;
                if(responseStatusCode == httpURLConnection.HTTP_OK){ //만약 정상적인 응답 데이터 라면
                    inputStream=httpURLConnection.getInputStream();
                    Log.d("php정상: ","정상적으로 출력"); //로그 메세지로 정상적으로 출력을 찍는다.
                }
                else {
                    inputStream = httpURLConnection.getErrorStream(); //만약 에러가 발생한다면
                    Log.d("php비정상: ","비정상적으로 출력"); // 로그 메세지로 비정상적으로 출력을 찍는다.
                }

                // StringBuilder를 사용하여 수신되는 데이터를 저장한다.
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) !=null ) {
                    sb.append(line);
                }

                bufferedReader.close();

                Log.d("php 값 :", sb.toString());

                String signupResult;
                // 결괏값 반환
                if (sb.toString().indexOf("Success") != -1) {
                    return "success";
                }
                else {
                    return "fail";
                }
            }

            catch (Exception e) {

                Log.d(TAG, "InsertData: Error",e);

                return  new String("Error " + e.getMessage());

            }

        }
    }

    /** 문자열로부터 해시함수 추출 함수 */
    public static String getHash(String str) {
        String digest = "";
        try {

            //암호화
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

    /** 세션키를 스토리지에 저장하는 함수 */
    private void saveSessionKey(String sessionKey) {
        SharedPreferences sharedPreferences = getSharedPreferences("logininfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sessionKey", sessionKey);
        editor.apply();
    }

    /** 스토리지에 있는 세션키를 불러오는 함수 */
    private String getSessionKey() {
        SharedPreferences sharedPreferences = getSharedPreferences("logininfo", MODE_PRIVATE);
        return sharedPreferences.getString("sessionKey", "");
    }
}