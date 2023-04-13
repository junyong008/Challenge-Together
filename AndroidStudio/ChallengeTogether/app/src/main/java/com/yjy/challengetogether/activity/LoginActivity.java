package com.yjy.challengetogether.activity;

import android.content.Intent;
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

        // 스토리지에서 세션키가 있으면 바로 메인 페이지 진입. 메인 페이지에서 세션키 유효성을 검사하기에 여기서 매번 검사할 필요 없음.
        String SessionKeyFromStorage = util.getSessionKey();
        if (SessionKeyFromStorage != null && !SessionKeyFromStorage.isEmpty()) { // SessionKeyFromStorage가 있는경우
            Intent intent = new Intent(LoginActivity.this, MainpageActivity.class);
            startActivity(intent);
            finish();
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
                            util.checkHttpResult(result);
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
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        });

        // 비밀번호 찾기 버튼 클릭
        tbutton_findpwd.setOnClickListener(v -> {
            Intent intent = new Intent(this, FindpwdActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        });
    }
}