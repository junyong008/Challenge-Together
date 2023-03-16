package com.yjy.challengetogether.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.CustomProgressDialog;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import io.github.muddz.styleabletoast.StyleableToast;

public class LoginActivity extends AppCompatActivity {
    private EditText edit_email;
    private EditText edit_pw;
    private Button button_login;
    private TextView tbutton_signup;
    private TextView tbutton_findpwd;
    private com.yjy.challengetogether.util.Util util = new Util(LoginActivity.this);
    private CustomProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edit_email = findViewById(R.id.edit_email);
        edit_pw = findViewById(R.id.edit_pw);
        button_login = findViewById(R.id.button_login);
        tbutton_signup = findViewById(R.id.tbutton_signup);
        tbutton_findpwd = findViewById(R.id.tbutton_findpwd);

        // 진행 프로그레스 시작
        customProgressDialog = new CustomProgressDialog(LoginActivity.this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customProgressDialog.show();

        // 스토리지에서 세션키를 받아오고, 세션키가 서버에 유효하면 바로 메인페이지 입성
        String SessionKeyFromStorage = util.getSessionKey();
        if (SessionKeyFromStorage != null && !SessionKeyFromStorage.isEmpty()) { // SessionKeyFromStorage가 있는경우

            OnTaskCompleted onCheckSessionTaskCompleted = new OnTaskCompleted() {
                @Override
                public void onTaskCompleted(String result) {

                    if (result.indexOf("VALID SESSION") != -1) {
                        // SessionKeyFromStorage가 있으면서 서버에도 세션이 유효한경우 바로 다음 액티비티로 이동.
                        Intent intent = new Intent(LoginActivity.this, MainpageActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // 세션키는 스토리지에 있으나 서버에서 더이상 유효하지 않은 경우 스토리지에서 세션키 삭제
                        util.saveSessionKey("");
                        customProgressDialog.dismiss();
                        StyleableToast.makeText(LoginActivity.this, "세션이 만료되었습니다.\n다시 로그인해주세요.", R.style.errorToast).show();
                        return;
                    }
                }
            };

            HttpAsyncTask checkSessionTask = new HttpAsyncTask(LoginActivity.this, onCheckSessionTaskCompleted);
            String phpFile = "service.php";
            String postParameters = "service=";

            checkSessionTask.execute(phpFile, postParameters, SessionKeyFromStorage);
        } else {
            customProgressDialog.dismiss();
        }

        // 로그인 버튼 클릭
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = edit_email.getText().toString().trim();
                String pwd = edit_pw.getText().toString().trim();

                // 예외 처리
                if (TextUtils.isEmpty(id) || TextUtils.isEmpty(pwd)) {
                    StyleableToast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력해주세요.", R.style.errorToast).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(id).matches()) {
                    StyleableToast.makeText(LoginActivity.this, "아이디는 이메일 형식으로 작성해주세요.", R.style.errorToast).show();
                    return;
                }

                // 아래 비동기 작업이 완료된 후 실행
                OnTaskCompleted onLoginTaskCompleted = new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(String result) {
                        Boolean isSessionKey = util.isSessionKey(result);

                        if (isSessionKey) {
                            // 로그인 성공.
                            util.saveSessionKey(result);
                            StyleableToast.makeText(LoginActivity.this, "로그인 성공", R.style.successToast).show();

                            Intent intent = new Intent(LoginActivity.this, MainpageActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (result.indexOf("USER NOT FOUND") != -1) {
                            StyleableToast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 일치하지 않습니다.", R.style.errorToast).show();
                            return;
                        } else {
                            StyleableToast.makeText(LoginActivity.this, result, R.style.errorToast).show();
                            return;
                        }
                    }
                };

                HttpAsyncTask loginTask = new HttpAsyncTask(LoginActivity.this, onLoginTaskCompleted);
                String phpFile = "login.php";
                String postParameters = "userid=" + id + "&userpw=" + util.getHash(pwd);

                loginTask.execute(phpFile, postParameters);
            }
        });

        // 회원가입 버튼 클릭
        tbutton_signup.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        });

        // 비밀번호 찾기 버튼 클릭
        tbutton_findpwd.setOnClickListener(v -> {
            Intent intent = new Intent(this, FindpwdActivity.class);
            startActivity(intent);
        });
    }
}