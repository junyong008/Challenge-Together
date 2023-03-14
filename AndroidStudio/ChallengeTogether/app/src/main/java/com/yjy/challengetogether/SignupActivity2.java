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
import android.widget.ImageView;

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

public class SignupActivity2 extends AppCompatActivity {

    // 사용할 변수들 선언
    private static String IP_ADDRESS = "3.37.234.141";
    private static String TAG = "phptest"; //phptest log 찍으려는 용도


    private EditText signup_name;
    private Button signup_button2;
    private ImageView signup_back;

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼을 눌렀을 때 처리할 로직을 작성합니다.
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);



        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String pwd = intent.getStringExtra("password");

        signup_back = findViewById(R.id.signup_back);
        signup_name = findViewById(R.id.signup_name);
        signup_button2 = findViewById(R.id.signup_button2);

        signup_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });


        signup_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = signup_name.getText().toString().trim();

                //회원가입을 할 때 예외 처리를 해준 것이다.
                if (name.equals(""))
                {
                    StyleableToast.makeText(SignupActivity2.this, "닉네임을 입력해주세요.", R.style.errorToast).show();
                }
                else {
                        InsertData task = new InsertData(); //PHP 통신을 위한 InsertData 클래스의 task 객체 생성
                        //본인이 접속할 PHP 주소와 보낼 데이터를 입력해준다.
                        try {
                            String signupResult = task.execute("http://"+IP_ADDRESS+"/signup.php",email,getHash(pwd),name).get();

                            if (signupResult.indexOf("DUP NAME") != -1) {
                                StyleableToast.makeText(SignupActivity2.this, "중복된 닉네임입니다.", R.style.errorToast).show();
                            }
                            else if (signupResult.indexOf("ERROR") != -1) {
                                StyleableToast.makeText(SignupActivity2.this, signupResult, R.style.errorToast).show();
                            }
                            else {
                                // 회원가입 성공. 세션키가 받아진 부분임. 이제 세션키를 스토리지에 저장하고 첫 화면으로 넘어가기만 하면됨. 각 서비스마다 스토리지에 저장된 세션키로 통신

                                saveSessionKey(signupResult);
                                StyleableToast.makeText(SignupActivity2.this, "회원가입에 성공하셨습니다.", R.style.successToast).show();

                                // 목표 액티비티 실행 후 그 외 모두 삭제
                                Intent intent = new Intent(getApplicationContext(), MainpageActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                }
            }
        });
    }

    class InsertData extends AsyncTask<String,Void,String> { // 통신을 위한 InsertData 생성
        CustomProgressDialog customProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //진행 다이얼로그 생성
            customProgressDialog = new CustomProgressDialog(SignupActivity2.this);
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
            /*
            PHP 파일을 실행시킬 수 있는 주소와 전송할 데이터를 준비한다.
            POST 방식으로 데이터 전달시에는 데이터가 주소에 직접 입력되지 않는다.
            이 값들은 InsertData 객체.excute 에서 매개변수로 준 값들이 배열 개념으로 차례대로 들어가
            값을 받아오는 개념이다.
             */
            String serverURL = (String) params[0];

            String useremail = (String)params[1];
            String userpw = (String)params[2];
            String username = (String)params[3];

            /*
            HTTP 메세지 본문에 포함되어 전송되기 때문에 따로 데이터를 준비해야한다.
            전송할 데이터는 "이름=값" 형식이며 여러 개를 보내야 할 경우에 항목 사이에 &를 추가해준다.
            여기에 적어준 이름들은 나중에 PHP에서 사용하여 값을 얻게 된다.
             */
            String postParameters ="useremail="+useremail+"&userpw="+userpw+"&username="+username;

            try{ // HttpURLConnection 클래스를 사용하여 POST 방식으로 데이터를 전송한다.
                URL url = new URL(serverURL); //주소가 저장된 변수를 이곳에 입력한다.
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000); //5초안에 응답이 오지 않으면 예외가 발생한다.

                httpURLConnection.setConnectTimeout(5000); //5초안에 연결이 안되면 예외가 발생한다.

                httpURLConnection.setRequestMethod("POST"); //요청 방식을 POST로 한다.

                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();

                //전송할 데이터가 저장된 변수를 이곳에 입력한다. 인코딩을 고려해줘야 하기 때문에 UTF-8 형식으로 넣어준다.
                outputStream.write(postParameters.getBytes("UTF-8"));

                Log.d("php postParameters_데이터 : ",postParameters); //postParameters의 값이 정상적으로 넘어왔나 Log를 찍어줬다.

                outputStream.flush();//현재 버퍼에 저장되어 있는 내용을 클라이언트로 전송하고 버퍼를 비운다.
                outputStream.close(); //객체를 닫음으로써 자원을 반납한다.


                int responseStatusCode = httpURLConnection.getResponseCode(); //응답을 읽는다.
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

                String signupResult;
                // 결괏값 반환
                if (CheckSessionId != null) {
                    signupResult = CheckSessionId;
                }
                else if (sb.toString().indexOf("for key 'NAME_UNIQUE'") != -1) {
                    signupResult = "DUP NAME";
                }
                else {
                    signupResult = "SIGNUP ERROR : " + String.valueOf(responseStatusCode);
                }

                return  signupResult;
            }

            catch (Exception e) {

                Log.d(TAG, "InsertData: Error",e);

                return  new String("Error " + e.getMessage());

            }

        }
    }

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
}