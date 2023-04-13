package com.yjy.challengetogether.activity;

import android.content.Intent;
import android.os.Bundle;
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


public class FindpwdActivity extends AppCompatActivity {
    private EditText edit_email;
    private Button button_sendverifycode;
    private ImageView ivbutton_back;

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpwd);

        edit_email = findViewById(R.id.edit_email);
        button_sendverifycode = findViewById(R.id.button_sendverifycode);
        ivbutton_back = findViewById(R.id.ivbutton_back);

        // 뒤로가기 버튼 클릭
        ivbutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.stay, R.anim.slide_out_right);
            }
        });

        // 인증코드 보내기 버튼 클릭
        button_sendverifycode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edit_email.getText().toString().trim();

                // 예외 처리
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    StyleableToast.makeText(FindpwdActivity.this, "이메일을 입력해주세요.", R.style.errorToast).show();
                    return;
                }

                OnTaskCompleted onFindPwdTaskCompleted = new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(String result) {
                        if (result.indexOf("SEND SUCCESS") != -1) {
                            StyleableToast.makeText(FindpwdActivity.this, "인증코드가 전송되었습니다.\n이메일을 열어 확인해주세요.", R.style.successToast).show();

                            Intent intent = new Intent(FindpwdActivity.this, CheckVerifyCodeActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            return;
                        } else if (result.indexOf("EMAIL NOT FOUND") != -1) {
                            StyleableToast.makeText(FindpwdActivity.this, "가입되지 않은 이메일입니다.", R.style.errorToast).show();
                            return;
                        } else {
                            StyleableToast.makeText(FindpwdActivity.this, result, R.style.errorToast).show();
                            return;
                        }
                    }
                };

                HttpAsyncTask findPwdTask = new HttpAsyncTask(FindpwdActivity.this, onFindPwdTaskCompleted);
                String phpFile = "request_verifycode.php";
                String postParameters = "useremail=" + email;

                findPwdTask.execute(phpFile, postParameters);
            }
        });
    }
}