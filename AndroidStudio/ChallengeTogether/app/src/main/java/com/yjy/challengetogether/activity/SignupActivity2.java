package com.yjy.challengetogether.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import io.github.muddz.styleabletoast.StyleableToast;

public class SignupActivity2 extends AppCompatActivity {
    private EditText edit_nickname;
    private Button button_endsignup;
    private ImageView ivbutton_back;
    private com.yjy.challengetogether.util.Util util = new Util(SignupActivity2.this);

    @Override
    public void onBackPressed() {
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

        ivbutton_back = findViewById(R.id.ivbutton_back);
        edit_nickname = findViewById(R.id.edit_nickname);
        button_endsignup = findViewById(R.id.button_endsignup);

        // 뒤로가기 버튼 클릭
        ivbutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        // 닉네임 결정 버튼 클릭
        button_endsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = edit_nickname.getText().toString().trim();

                // 예외 처리
                if (TextUtils.isEmpty(name)) {
                    StyleableToast.makeText(SignupActivity2.this, "닉네임을 입력해주세요.", R.style.errorToast).show();
                    return;
                }

                OnTaskCompleted onSignUpTaskCompleted = new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(String result) {
                        Boolean isSessionKey = util.isSessionKey(result);

                        if (isSessionKey) {  // 세션키를 받아왔다 = 회원가입 성공했다.
                            util.saveSessionKey(result);
                            StyleableToast.makeText(SignupActivity2.this, "회원가입에 성공하셨습니다.", R.style.successToast).show();

                            // 메인페이지 액티비티 실행 후 그 외 모두 삭제
                            Intent intent = new Intent(getApplicationContext(), MainpageActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else if (result.indexOf("Duplicate") != -1) {
                            StyleableToast.makeText(SignupActivity2.this, "중복된 닉네임입니다.", R.style.errorToast).show();
                            return;
                        } else {
                            StyleableToast.makeText(SignupActivity2.this, result, R.style.errorToast).show();
                            return;
                        }
                    }
                };

                HttpAsyncTask signUpTask = new HttpAsyncTask(SignupActivity2.this, onSignUpTaskCompleted);
                String phpFile = "signup.php";
                String postParameters = "useremail=" + email + "&userpw=" + util.getHash(pwd) + "&username=" + name;

                signUpTask.execute(phpFile, postParameters);
            }
        });
    }
}