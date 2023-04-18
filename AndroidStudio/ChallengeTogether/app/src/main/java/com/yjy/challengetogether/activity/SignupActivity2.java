package com.yjy.challengetogether.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.muddz.styleabletoast.StyleableToast;

public class SignupActivity2 extends AppCompatActivity {
    private EditText edit_nickname;
    private Button button_endsignup;
    private ImageView ivbutton_back;
    private TextView textView_info;
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
        String kakaoid = intent.getStringExtra("kakaoid");
        String googleid = intent.getStringExtra("googleid");
        String naverid = intent.getStringExtra("naverid");

        ivbutton_back = findViewById(R.id.ivbutton_back);
        edit_nickname = findViewById(R.id.edit_nickname);
        button_endsignup = findViewById(R.id.button_endsignup);
        textView_info = findViewById(R.id.textView_info);

        // 뒤로가기 버튼 클릭
        ivbutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        // 개인정보 처리방침 링크 설정
        Pattern pattern = Pattern.compile("개인정보 처리방침");
        Linkify.TransformFilter transformFilter = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher matcher, String s) {
                return "";
            }
        };
        Linkify.addLinks(textView_info, pattern, "https://sites.google.com/view/challenge-together/%ED%99%88", null, transformFilter);


        // 닉네임의 특문, 띄어쓰기 제한
        edit_nickname.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$");
                if (source.equals("") || ps.matcher(source).matches()) {
                    return source;
                }
                StyleableToast.makeText(SignupActivity2.this, "한글, 영문, 숫자만 입력 가능합니다.", R.style.errorToast).show();
                return "";
            }
        },new InputFilter.LengthFilter(9)});

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
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else if (result.indexOf("Duplicate") != -1) {
                            StyleableToast.makeText(SignupActivity2.this, "중복된 닉네임입니다.", R.style.errorToast).show();
                            return;
                        } else {
                            util.checkHttpResult(result);
                        }
                    }
                };

                HttpAsyncTask signUpTask = new HttpAsyncTask(SignupActivity2.this, onSignUpTaskCompleted);
                String phpFile = "signup.php";
                String postParameters = "useremail=" + email + "&userpw=" + (pwd.equals("") ? "" : util.getHash(pwd)) + "&kakaoid=" + kakaoid + "&googleid=" + googleid + "&naverid=" + naverid + "&username=" + name;

                signUpTask.execute(phpFile, postParameters);
            }
        });
    }
}