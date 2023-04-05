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

public class ModifyPostActivity extends AppCompatActivity {

    private ImageButton ibutton_close;
    private EditText edit_content;
    private Button button_addpost;
    private String postidx, postcontent;
    private Util util = new Util(ModifyPostActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifypost);

        Intent intent = getIntent();
        postidx = intent.getStringExtra("postidx");
        postcontent = intent.getStringExtra("postcontent");

        ibutton_close = findViewById(R.id.ibutton_close);
        edit_content = findViewById(R.id.edit_content);
        edit_content.setText(postcontent);
        button_addpost = findViewById(R.id.button_addpost);

        // 닫기 버튼 클릭
        ibutton_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 게시글 추가 버튼 클릭
        button_addpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String modiPostContent = edit_content.getText().toString().trim();

                // 내용이 비어있을 경우, 안내메시지 반환
                if (TextUtils.isEmpty(modiPostContent)) {
                    StyleableToast.makeText(ModifyPostActivity.this, "내용을 입력해주세요.", R.style.errorToast).show();
                    return;
                }

                OnTaskCompleted onModifyPostTaskCompleted = new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(String result) {

                        if (result.indexOf("MODIFY SUCCESS") != -1) {

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("result", "success");
                            setResult(Activity.RESULT_OK, resultIntent);

                            finish();
                            // overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
                        } else {
                            util.checkHttpResult(result);
                        }
                    }
                };

                HttpAsyncTask modifyPostTask = new HttpAsyncTask(ModifyPostActivity.this, onModifyPostTaskCompleted);
                String phpFile = "service.php";
                String postParameters = "service=modifypost&postidx=" + postidx + "&postcontent=" + modiPostContent;

                modifyPostTask.execute(phpFile, postParameters, util.getSessionKey());

            }
        });
    }
}