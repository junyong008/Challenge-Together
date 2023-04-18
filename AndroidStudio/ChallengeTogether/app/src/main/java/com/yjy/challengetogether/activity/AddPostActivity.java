package com.yjy.challengetogether.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import io.github.muddz.styleabletoast.StyleableToast;

public class AddPostActivity extends AppCompatActivity {

    private ImageButton ibutton_close;
    private EditText edit_content;
    private Button button_addpost;
    private com.yjy.challengetogether.util.Util util = new Util(AddPostActivity.this);

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);

        ibutton_close = findViewById(R.id.ibutton_close);
        edit_content = findViewById(R.id.edit_content);
        button_addpost = findViewById(R.id.button_addpost);

        // 닫기 버튼 클릭
        ibutton_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        // 게시글 추가 버튼 클릭
        button_addpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String postContent = edit_content.getText().toString().trim();

                // 내용이 비어있을 경우, 안내메시지 반환
                if (TextUtils.isEmpty(postContent)) {
                    StyleableToast.makeText(AddPostActivity.this, "내용을 입력해주세요.", R.style.errorToast).show();
                    return;
                }

                OnTaskCompleted onAddPostTaskCompleted = new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(String result) {

                        if (result.indexOf("ADD SUCCESS") != -1) {

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("result", "refresh");
                            setResult(Activity.RESULT_OK, resultIntent);

                            finish();
                            // overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
                        } else {
                            util.checkHttpResult(result);
                        }
                    }
                };

                HttpAsyncTask addPostTask = new HttpAsyncTask(AddPostActivity.this, onAddPostTaskCompleted);
                String phpFile = "service.php";
                String postParameters = "service=addpost&postcontent=" + postContent;

                addPostTask.execute(phpFile, postParameters, util.getSessionKey());

            }
        });
    }
}