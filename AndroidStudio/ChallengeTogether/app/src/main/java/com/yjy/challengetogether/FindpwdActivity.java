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


public class FindpwdActivity extends AppCompatActivity {

    // 사용할 변수들 선언
    private static String IP_ADDRESS = "3.37.234.141"; //본인 IP주소를 넣으세요.
    private static String TAG = "phptest"; //phptest log 찍으려는 용도

    private EditText findpwd_email;
    private Button findpwd_button;
    private ImageView findpwd_back;

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼을 눌렀을 때 처리할 로직을 작성합니다.
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpwd);

        findpwd_email = findViewById(R.id.findpwd_email);
        findpwd_button = findViewById(R.id.findpwd_button);
        findpwd_back = findViewById(R.id.findpwd_back);

        // 뒤로가기 버튼
        findpwd_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 로그인 확인
        findpwd_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = findpwd_email.getText().toString().trim();

                if (!email.contains("@") || !email.contains(".com"))
                    StyleableToast.makeText(FindpwdActivity.this, "이메일을 입력해주세요.", R.style.errorToast).show();
                else {
                    FindpwdActivity.RequestVerify task = new FindpwdActivity.RequestVerify();

                    try {
                        String CheckResult = task.execute("http://"+IP_ADDRESS+"/request_verifycode.php",email).get();

                        if (CheckResult.indexOf("valid") != -1) {
                            // 이메일이 있음을 확인했을때

                            StyleableToast.makeText(FindpwdActivity.this, "인증코드가 전송되었습니다.\n이메일을 열어 확인해주세요.", R.style.successToast).show();

                            Intent intent = new Intent(FindpwdActivity.this, CheckVerifyCodeActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            return;
                        }
                        else {
                            // 이메일이 존재하지 않으면
                            StyleableToast.makeText(FindpwdActivity.this, "가입되지 않은 이메일입니다.", R.style.errorToast).show();
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


    class RequestVerify extends AsyncTask<String,Void,String> {
        CustomProgressDialog customProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //진행 다이얼로그 생성
            customProgressDialog = new CustomProgressDialog(FindpwdActivity.this);
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
            String useremail = (String)params[1];

            String postParameters ="useremail="+useremail;

            try{
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST"); //요청 방식을 POST로 한다.
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
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

                // 로직의 차이. 회원가입시에는 가입된 이메일은 무조건 중복가입을 막아야함. 고로 200요청이 아니면 모두 걸렀음
                // 하지만 비밀번호 찾기는 반대로 반드시 있는 이메일에만 전송을 해야하므로 조건을 축소
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
}