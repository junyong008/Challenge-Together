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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.github.muddz.styleabletoast.StyleableToast;

public class LoginActivity extends AppCompatActivity {

    // 사용할 변수들 선언
    private static String IP_ADDRESS = "3.37.234.141"; //본인 IP주소를 넣으세요.
    private static String TAG = "phptest"; //phptest log 찍으려는 용도
    private EditText login_id;
    private EditText login_pw;
    private Button login_submit;
    private TextView login_signin;
    private TextView login_findpasswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_id = findViewById(R.id.login_id);
        login_pw = findViewById(R.id.login_pw);
        login_submit = findViewById(R.id.login_submit);
        login_signin = findViewById(R.id.login_signin);
        login_findpasswd = findViewById(R.id.login_findpasswd);


        // 스토리지에서 세션키를 받아오고, 세션키가 서버에 유효하면 바로 메인페이지 입성
        String SessionKeyFromStorage = getSessionKey();
        if(SessionKeyFromStorage != null && !SessionKeyFromStorage.isEmpty()) {
            // SessionKeyFromStorage가 있는경우
            try {
                LoginActivity.CheckSession checktask = new LoginActivity.CheckSession();
                String CheckValidSession = checktask.execute("http://"+IP_ADDRESS+"/service.php",getSessionKey()).get();
                if (CheckValidSession.indexOf("valid") != -1) {
                    // SessionKeyFromStorage가 있으면서 서버에도 세션이 유효한경우 바로 다음 액티비티로 이동.
                    Intent intent = new Intent(LoginActivity.this, MainpageActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    // 세션키는 스토리지에 있으나 서버에서 더이상 유효하지 않은 경우 세션키 삭제
                    saveSessionKey("");
                    StyleableToast.makeText(LoginActivity.this, "세션이 만료되었습니다.\n다시 로그인해주세요.", R.style.errorToast).show();
                }
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        // 로그인
        login_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = login_id.getText().toString().trim();
                String pwd = login_pw.getText().toString().trim();

                //회원가입을 할 때 예외 처리를 해준 것이다.
                if (id.equals("")  || pwd.equals(""))
                {
                    //Toast.makeText(LoginActivity.this, "정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    StyleableToast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력해주세요.", R.style.errorToast).show();
                }
                else {
                    if(!id.contains("@") || !id.contains(".com")){
                        StyleableToast.makeText(LoginActivity.this, "아이디는 이메일 형식으로 작성해주세요.", R.style.errorToast).show();
                    }
                    else {
                        LoginActivity.SelectData task = new LoginActivity.SelectData(); //PHP 통신을 위한 SelectData 클래스의 task 객체 생성

                        //본인이 접속할 PHP 주소와 보낼 데이터를 입력해준다.
                        try {
                            String loginResult = task.execute("http://"+IP_ADDRESS+"/login.php",id,getHash(pwd)).get();

                            if (loginResult.indexOf("ERROR") != -1) {
                                StyleableToast.makeText(LoginActivity.this, loginResult, R.style.errorToast).show();
                            }
                            else if (loginResult.indexOf("NONE") != -1) {
                                StyleableToast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 일치하지 않습니다.", R.style.errorToast).show();
                            }
                            else {
                                // 로그인 성공. 세션키가 받아진 부분임. 이제 세션키를 스토리지에 저장하고 첫 화면으로 넘어가기만 하면됨. 각 서비스마다 스토리지에 저장된 세션키로 통신

                                saveSessionKey(loginResult);
                                StyleableToast.makeText(LoginActivity.this, "로그인 성공", R.style.successToast).show();

                                Intent intent = new Intent(LoginActivity.this, MainpageActivity.class);
                                startActivity(intent);
                                finish();
                                // Toast.makeText(LoginActivity.this, "스토리지 - " + getSessionKey(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });

        //회원가입 버튼 클릭시, 회원가입 페이지로 이동
        login_signin.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        });

        //비밀번호 찾기 버튼 클릭시, 비밀번호 찾기 페이지로 이동
        login_findpasswd.setOnClickListener(v -> {
            Intent intent = new Intent(this, FindpwdActivity.class);
            startActivity(intent);
        });
    }

    class SelectData extends AsyncTask<String,Void,String> { // 통신을 위한 InsertData 생성
        CustomProgressDialog customProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //진행 다이얼로그 생성
            customProgressDialog = new CustomProgressDialog(LoginActivity.this);
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
            String serverURL = (String)params[0];

            String userid = (String)params[1];
            String userpw = (String)params[2];

            String postParameters ="userid="+userid+"&userpw="+userpw;

            try{
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                // 시간초과설정 및 메소드
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                //httpURLConnection.setRequestProperty("X-Session-ID", "ij5tao570qn4rv1hiju8nhr3i0");

                // 전송
                httpURLConnection.connect();

                // 결과 받고 인코딩 설정
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                Log.d("php postParameters_데이터 : ",postParameters);

                // 객체 비우기
                outputStream.flush();
                outputStream.close();

                // 응답 읽기
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code-" + responseStatusCode);
                Log.d(TAG, "URL : " + url);

                // 응답 헤더들을 읽고, 세션아이디를 받아옴
                Map<String, List<String>> headers = httpURLConnection.getHeaderFields();
                List<String> sessionIds = headers.get("X-Session-ID");
                String CheckSessionId = null;
                if (sessionIds != null) {
                    for (String sessionId : sessionIds) {
                        if (sessionId != null) {
                            CheckSessionId = sessionId;
                        }
                        Log.d(TAG, "세션아이디 : " + sessionId);
                    }
                }

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

                String loginResult;

                // 결괏값 반환
                if (CheckSessionId != null) {
                    loginResult = CheckSessionId;   // 세션키를 헤더에서 받아오면 성공으로 판단
                }
                else if (sb.toString().indexOf("일치하는") != -1) {
                    loginResult = "NONE";
                }
                else {
                    loginResult = "LOGIN ERROR : " + String.valueOf(responseStatusCode);
                }

                return  loginResult;
            }

            catch (Exception e) {

                Log.d(TAG, "InsertData: Error",e);

                return  new String("Error " + e.getMessage());

            }

        }
    }

    class CheckSession extends AsyncTask<String,Void,String> { // 통신을 위한 InsertData 생성
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "POST response  - " + result); // result 값 확인하기
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String)params[0];
            String sessionid = (String)params[1];
            String postParameters ="service=";

            try{
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                // 시간초과설정 및 메소드
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("X-Session-ID", sessionid);    // 스토리지에서 받아온 세션키를 헤더로 설정

                // 전송
                httpURLConnection.connect();

                // 결과 받고 인코딩 설정
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                Log.d("php postParameters_데이터 : ",postParameters);

                // 객체 비우기
                outputStream.flush();
                outputStream.close();

                // 응답 읽기
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

                // 결괏값 반환
                if (sb.toString().indexOf("Valid") != -1) {
                    return "valid";
                }
                else {
                    return "none";
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