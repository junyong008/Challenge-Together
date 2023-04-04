package com.yjy.challengetogether.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import java.util.Locale;

import io.github.muddz.styleabletoast.StyleableToast;

public class CheckVerifyCodeActivity extends AppCompatActivity {
    private EditText editText1, editText2, editText3, editText4, editText5, editText6;
    private TextView TextView_timer;
    private CountDownTimer countDownTimer;
    private final long startTimeInMillis = 5 * 60 * 1000; // 5분
    private ImageView ivbutton_back;
    private com.yjy.challengetogether.util.Util util = new Util(CheckVerifyCodeActivity.this);

    @Override
    public void onBackPressed() {
        countDownTimer.cancel();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkverifycode);

        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        editText4 = findViewById(R.id.editText4);
        editText5 = findViewById(R.id.editText5);
        editText6 = findViewById(R.id.editText6);
        TextView_timer = findViewById(R.id.TextView_timer);
        ivbutton_back = findViewById(R.id.ivbutton_back);

        // 뒤로가기 버튼 클릭
        ivbutton_back.setOnClickListener(new View.OnClickListener() {
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
                TextView_timer.setText(timeLeftFormatted);
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

        // 모든칸에 값이 입력됐을 경우.
        if (!edit1.isEmpty() && !edit2.isEmpty() && !edit3.isEmpty() && !edit4.isEmpty() && !edit5.isEmpty() && !edit6.isEmpty()) {
            Intent intent = getIntent();
            String email = intent.getStringExtra("email");
            String InputCode = edit1 + edit2 + edit3 + edit4 + edit5 + edit6;

            OnTaskCompleted onCheckVerifyCodeTaskCompleted = new OnTaskCompleted() {
                @Override
                public void onTaskCompleted(String result) {
                    Boolean isSessionKey = util.isSessionKey(result);

                    if (isSessionKey) {
                        util.saveSessionKey(result);
                        StyleableToast.makeText(CheckVerifyCodeActivity.this, "새로운 비밀번호를 설정해주세요.", R.style.successToast).show();
                        countDownTimer.cancel();

                        Intent intent2 = new Intent(CheckVerifyCodeActivity.this, ChangepwdActivity.class);
                        startActivity(intent2);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else if (result.indexOf("OUT OF TIME") != -1) {
                        StyleableToast.makeText(CheckVerifyCodeActivity.this, "만료된 인증코드입니다.", R.style.errorToast).show();
                        finish();
                    } else if (result.indexOf("WRONG CODE") != -1) {
                        StyleableToast.makeText(CheckVerifyCodeActivity.this, "인증코드가 일치하지 않습니다.", R.style.errorToast).show();
                        editText1.setText(""); editText2.setText(""); editText3.setText(""); editText4.setText(""); editText5.setText(""); editText6.setText(""); editText1.requestFocus();
                        return;
                    } else {
                        util.checkHttpResult(result);
                    }
                }
            };

            HttpAsyncTask checkVerifyCodeTask = new HttpAsyncTask(CheckVerifyCodeActivity.this, onCheckVerifyCodeTaskCompleted);
            String phpFile = "check_verifycode.php";
            String postParameters = "useremail=" + email + "&verifycode=" + InputCode;

            checkVerifyCodeTask.execute(phpFile, postParameters);
        }
    }
}
