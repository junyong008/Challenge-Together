package com.yjy.challengetogether;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.github.muddz.styleabletoast.StyleableToast;

public class CheckVerifyCodeActivity extends AppCompatActivity {
    private static String TAG = "phptest"; //phptest log 찍으려는 용도
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;
    private EditText editText5;
    private EditText editText6;
    private TextView timerTextView;
    private CountDownTimer countDownTimer;
    private final long startTimeInMillis = 5 * 60 * 1000; // 5분
    private ImageView checkverify_back;

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼을 눌렀을 때 처리할 로직을 작성합니다.
        countDownTimer.cancel();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_verify_code);

        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        editText4 = findViewById(R.id.editText4);
        editText5 = findViewById(R.id.editText5);
        editText6 = findViewById(R.id.editText6);
        timerTextView = findViewById(R.id.timerTextView);
        checkverify_back = findViewById(R.id.checkverify_back);

        // 뒤로가기 버튼
        checkverify_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        // 타이머 카운트 시작
        countDownTimer = new CountDownTimer(startTimeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 남은 시간을 분과 초로 변환
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;

                // TextView 업데이트
                String timeLeftFormatted = String.format(Locale.getDefault(), "%2d분 %2d초", minutes, seconds);
                timerTextView.setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                // 타이머가 종료되면 처리할 작업 : 메시지를 띄우고 뒤로가기
                StyleableToast.makeText(CheckVerifyCodeActivity.this, "유효시간이 초과되었습니다.", R.style.errorToast).show();
                finish();
            }
        };
        countDownTimer.start();

        // Edit에 숫자를 입력하면 뒤에 있는 edit으로 포커스를 주는 로직
        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    editText2.requestFocus();
                }
                checkInputCode();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    editText3.requestFocus();
                }
                checkInputCode();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    editText4.requestFocus();
                }
                checkInputCode();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    editText5.requestFocus();
                }
                checkInputCode();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editText5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    editText6.requestFocus();
                }
                checkInputCode();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editText6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputCode();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void checkInputCode() {
        String edit1 = editText1.getText().toString().trim();
        String edit2 = editText2.getText().toString().trim();
        String edit3 = editText3.getText().toString().trim();
        String edit4 = editText4.getText().toString().trim();
        String edit5 = editText5.getText().toString().trim();
        String edit6 = editText6.getText().toString().trim();

        if (!edit1.isEmpty() && !edit2.isEmpty() && !edit3.isEmpty() && !edit4.isEmpty() && !edit5.isEmpty() && !edit6.isEmpty()) {
            // 모든 칸에 값이 있는 경우에만

            String IP_ADDRESS = "3.37.234.141";
            Intent intent = getIntent();
            String email = intent.getStringExtra("email");
            String InputCode = edit1 + edit2 + edit3 + edit4 + edit5 + edit6;


            CheckVerifyCodeActivity.CheckVerify task = new CheckVerifyCodeActivity.CheckVerify();
            try {
                String CheckResult = task.execute("http://"+IP_ADDRESS+"/check_verifycode.php",email,InputCode).get();

                if (CheckResult.indexOf("OUT TIME") != -1) {
                    StyleableToast.makeText(CheckVerifyCodeActivity.this, "만료된 인증코드입니다.", R.style.errorToast).show();
                    finish();
                }
                else if (CheckResult.indexOf("WRONG") != -1) {
                    StyleableToast.makeText(CheckVerifyCodeActivity.this, "인증코드가 일치하지 않습니다.", R.style.errorToast).show();
                    editText1.setText(""); editText2.setText(""); editText3.setText(""); editText4.setText(""); editText5.setText(""); editText6.setText(""); editText1.requestFocus();
                }
                else if (CheckResult.indexOf("ERROR") != -1) {
                    StyleableToast.makeText(CheckVerifyCodeActivity.this, CheckResult, R.style.errorToast).show();
                }
                else {
                    // 코드가 일치하고 만료되지도 않았다면 세션키를 반환받음.
                    saveSessionKey(CheckResult);
                    StyleableToast.makeText(CheckVerifyCodeActivity.this, "확인되었습니다.\n새로운 비밀번호를 설정해주세요.", R.style.successToast).show();
                    countDownTimer.cancel();

                    Intent intent2 = new Intent(CheckVerifyCodeActivity.this, ChangepwdActivity.class);
                    startActivity(intent2);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    class CheckVerify extends AsyncTask<String,Void,String> {
        CustomProgressDialog customProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //진행 다이얼로그 생성
            customProgressDialog = new CustomProgressDialog(CheckVerifyCodeActivity.this);
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
            String verifycode = (String)params[2];

            String postParameters ="useremail="+useremail+"&verifycode="+verifycode;

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

                String CheckResult;

                if (CheckSessionId != null) {
                    CheckResult = CheckSessionId;
                }
                else if (sb.toString().indexOf("Out Of Time") != -1) {
                    CheckResult = "OUT TIME";
                }
                else if (sb.toString().indexOf("IncorrectCode") != -1) {
                    CheckResult = "WRONG";
                }
                else {
                    CheckResult = "VERIFY ERROR : " + String.valueOf(responseStatusCode);
                }

                return  CheckResult;
            }

            catch (Exception e) {

                Log.d(TAG, "InsertData: Error",e);

                return  new String("Error " + e.getMessage());

            }

        }
    }

    private void saveSessionKey(String sessionKey) {
        SharedPreferences sharedPreferences = getSharedPreferences("logininfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sessionKey", sessionKey);
        editor.apply();
    }
}
