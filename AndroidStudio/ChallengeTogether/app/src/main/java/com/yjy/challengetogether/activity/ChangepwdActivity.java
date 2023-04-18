package com.yjy.challengetogether.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import io.github.muddz.styleabletoast.StyleableToast;

public class ChangepwdActivity extends AppCompatActivity {
    private EditText edit_changepwd;
    private EditText edit_changepwdcheck;
    private Button button_changepwd;
    private com.yjy.challengetogether.util.Util util = new Util(ChangepwdActivity.this);

    @Override
    public void onBackPressed() {
        StyleableToast.makeText(ChangepwdActivity.this, "비밀번호를 변경해주세요.", R.style.errorToast).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepwd);

        edit_changepwd = findViewById(R.id.edit_changepwd);
        edit_changepwdcheck = findViewById(R.id.edit_changepwdcheck);
        button_changepwd = findViewById(R.id.button_changepwd);

        button_changepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = edit_changepwd.getText().toString().trim();
                String pwdcheck = edit_changepwdcheck.getText().toString().trim();

                // 예외 처리
                if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwdcheck)) {
                    StyleableToast.makeText(ChangepwdActivity.this, "정보를 입력해주세요.", R.style.errorToast).show();
                    return;
                }
                if (pwd.length() <= 5) {
                    StyleableToast.makeText(ChangepwdActivity.this, "비밀번호를 6자리 이상 입력해주세요.", R.style.errorToast).show();
                    return;
                }
                if (!pwd.equals(pwdcheck)) {
                    StyleableToast.makeText(ChangepwdActivity.this, "비밀번호가 일치하지 않습니다.", R.style.errorToast).show();
                    return;
                }

                OnTaskCompleted onChangePwdTaskCompleted = new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(String result) {
                        if (result.indexOf("CHANGE SUCCESS") != -1) {
                            // 비밀번호 변경이 성공했을경우 스토리지 세션키를 제거 하고 로그인화면으로 이동
                            util.saveSessionKey("");
                            StyleableToast.makeText(ChangepwdActivity.this, "비밀번호가 변경되었습니다.", R.style.successToast).show();

                            // 로그인 액티비티 실행 후 그 외 모두 삭제
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            util.checkHttpResult(result);
                        }
                    }
                };

                HttpAsyncTask changePwdTask = new HttpAsyncTask(ChangepwdActivity.this, onChangePwdTaskCompleted);
                String phpFile = "service.php";
                String postParameters = "service=changepwd&changepwd=" + util.getHash(pwd);

                changePwdTask.execute(phpFile, postParameters, util.getSessionKey());
            }
        });
    }
}