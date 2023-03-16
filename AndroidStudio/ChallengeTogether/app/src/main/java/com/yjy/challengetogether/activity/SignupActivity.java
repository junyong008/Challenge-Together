package com.yjy.challengetogether.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.HttpAsyncTask;

import io.github.muddz.styleabletoast.StyleableToast;

public class SignupActivity extends AppCompatActivity {
    private EditText edit_email;
    private EditText edit_pwd;
    private EditText edit_pwdcheck;
    private Button button_signup;
    private ImageView ivbutton_back;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edit_email = findViewById(R.id.edit_email);
        edit_pwd = findViewById(R.id.edit_pwd);
        edit_pwdcheck = findViewById(R.id.edit_pwdcheck);
        button_signup = findViewById(R.id.button_signup);
        ivbutton_back = findViewById(R.id.ivbutton_back);


        // 뒤로가기 버튼 클릭
        ivbutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 회원가입 버튼 클릭
        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edit_email.getText().toString().trim();
                String pwd = edit_pwd.getText().toString().trim();
                String pwdcheck = edit_pwdcheck.getText().toString().trim();

                // 예외 처리
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwdcheck)) {
                    StyleableToast.makeText(SignupActivity.this, "정보를 입력해주세요.", R.style.errorToast).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    StyleableToast.makeText(SignupActivity.this, "아이디를 이메일 형식으로 입력해주세요.", R.style.errorToast).show();
                    return;
                }
                if (pwd.length() <= 5) {
                    StyleableToast.makeText(SignupActivity.this, "비밀번호를 6자리 이상 입력해주세요.", R.style.errorToast).show();
                    return;
                }
                if (!pwd.equals(pwdcheck)) {
                    StyleableToast.makeText(SignupActivity.this, "비밀번호가 일치하지 않습니다.", R.style.errorToast).show();
                    return;
                }

                OnTaskCompleted onCheckEmailValidTaskCompleted = new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(String result) {
                        if (result.indexOf("EMAIL NOT FOUND") != -1) {
                            // 기존에 가입된 이메일이 정확히 없을때만 현재 액티비티의 이메일, 패스워드를 다음 가입 액티비티로 넘김
                            Intent intent = new Intent(SignupActivity.this, SignupActivity2.class);
                            intent.putExtra("email", email);
                            intent.putExtra("password", pwd);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            StyleableToast.makeText(SignupActivity.this, "이미 가입된 이메일입니다.", R.style.errorToast).show();
                            return;
                        }
                    }
                };

                HttpAsyncTask checkEmailValidTask = new HttpAsyncTask(SignupActivity.this, onCheckEmailValidTaskCompleted);
                String phpFile = "check_emailvalid.php";
                String postParameters = "useremail=" + email;

                checkEmailValidTask.execute(phpFile, postParameters);
            }
        });
    }
}