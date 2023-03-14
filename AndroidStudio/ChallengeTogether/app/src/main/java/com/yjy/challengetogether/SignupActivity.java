package com.yjy.challengetogether;

import android.content.Intent;
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
import java.util.concurrent.ExecutionException;

import io.github.muddz.styleabletoast.StyleableToast;

public class SignupActivity extends AppCompatActivity {

    // 사용할 변수들 선언
    private static String IP_ADDRESS = "3.37.234.141"; //본인 IP주소를 넣으세요.
    private static String TAG = "phptest"; //phptest log 찍으려는 용도

    private EditText signup_id;
    private EditText signup_pwd;
    private EditText signup_pwd2;
    private Button signup_button;
    private ImageView signup_back;

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼을 눌렀을 때 처리할 로직을 작성합니다.
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signup_id = findViewById(R.id.signup_id);
        signup_pwd = findViewById(R.id.signup_pwd);
        signup_pwd2 = findViewById(R.id.signup_pwd2);

        signup_button = findViewById(R.id.signup_button);
        signup_back = findViewById(R.id.signup_back);

        signup_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = signup_id.getText().toString().trim();
                String pwd = signup_pwd.getText().toString().trim();
                String pwdcheck = signup_pwd2.getText().toString().trim();

                //회원가입을 할 때 예외 처리를 해준 것이다.
                if (email.equals("")  || pwd.equals("") || pwdcheck.equals(""))
                {
                    StyleableToast.makeText(SignupActivity.this, "정보를 입력해주세요.", R.style.errorToast).show();
                }
                else {
                    if(pwd.equals(pwdcheck)) {
                        if(pwd.length()<=5){
                            StyleableToast.makeText(SignupActivity.this, "비밀번호를 6자리 이상 입력해주세요.", R.style.errorToast).show();
                        }
                        else if(!email.contains("@") || !email.contains(".com")){
                            StyleableToast.makeText(SignupActivity.this, "아이디에 @ 및 .com을 포함시키세요.", R.style.errorToast).show();
                        }
                        else {


                            SignupActivity.CheckEmail task = new SignupActivity.CheckEmail(); //PHP 통신을 위한 InsertData 클래스의 task 객체 생성


                            try {
                                String CheckResult = task.execute("http://"+IP_ADDRESS+"/check_emailvalid.php",email).get();

                                if (CheckResult.indexOf("valid") != -1) {
                                    StyleableToast.makeText(SignupActivity.this, "이미 가입된 이메일입니다.", R.style.errorToast).show();
                                    return;
                                }
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            // 만약 이메일, 비밀번호 형식이 일치하고 이메일 중복이 아니라면 다음 액티비티로 같이 정보 전송
                            Intent intent = new Intent(SignupActivity.this, SignupActivity2.class);
                            intent.putExtra("email", email);
                            intent.putExtra("password", pwd);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    }
                    else {
                        StyleableToast.makeText(SignupActivity.this, "비밀번호가 일치하지 않습니다.", R.style.errorToast).show();
                    }
                }
            }
        });
    }

    class CheckEmail extends AsyncTask<String,Void,String> {
        CustomProgressDialog customProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //진행 다이얼로그 생성
            customProgressDialog = new CustomProgressDialog(SignupActivity.this);
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

            /*
            HTTP 메세지 본문에 포함되어 전송되기 때문에 따로 데이터를 준비해야한다.
            전송할 데이터는 "이름=값" 형식이며 여러 개를 보내야 할 경우에 항목 사이에 &를 추가해준다.
            여기에 적어준 이름들은 나중에 PHP에서 사용하여 값을 얻게 된다.
             */
            String postParameters ="useremail="+useremail;

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
                if (sb.toString().indexOf("Valid") != -1 || responseStatusCode != 200) {
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

}