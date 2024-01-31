package com.yjy.challengetogether.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import io.github.muddz.styleabletoast.StyleableToast;

public class ModifyRoomActivity extends AppCompatActivity {

    private ImageButton ibutton_close;
    private EditText edit_title;
    private String outputIcon, roomidx, postTitle, postContent, postIcon, postTargetday;
    private static final int REQUEST_CODE_DIALOG_ACTIVITY = 1;
    private ImageButton ibutton_listoficon;
    private EditText edit_content;
    private EditText edit_targetday;
    private LabeledSwitch switch_targetday;
    private Button button_modifyroom;
    private Util util = new Util(ModifyRoomActivity.this);


    @Override
    public void onBackPressed() {
        finish();
        //overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyroom);

        Intent intent = getIntent();
        roomidx = intent.getStringExtra("roomidx");
        String roomType = intent.getStringExtra("roomType");
        String roomTitle = intent.getStringExtra("roomTitle");
        String roomContent = intent.getStringExtra("roomContent");
        String roomTargetDay = intent.getStringExtra("roomTargetDay");
        String roomCurrentDay = intent.getStringExtra("roomCurrentDay");

        ibutton_close = findViewById(R.id.ibutton_close);
        edit_title = findViewById(R.id.edit_title);
        ibutton_listoficon = findViewById(R.id.ibutton_listoficon);
        edit_content = findViewById(R.id.edit_content);
        switch_targetday = findViewById(R.id.switch_targetday);
        edit_targetday = findViewById(R.id.edit_targetday);
        button_modifyroom = findViewById(R.id.button_modifyroom);

        // 초기 값들 설정
        outputIcon = roomType;
        int drawableId = ModifyRoomActivity.this.getResources().getIdentifier(outputIcon, "drawable", ModifyRoomActivity.this.getPackageName());
        ibutton_listoficon.setImageResource(drawableId);
        edit_title.setText(roomTitle);
        edit_content.setText(roomContent);
        if (roomTargetDay.equals("36500")) {
            edit_targetday.setText("∞");
            edit_targetday.setEnabled(false);
            switch_targetday.setOn(false);
        } else {
            edit_targetday.setText(roomTargetDay);
            edit_targetday.setEnabled(true);
        }

        // 뒤로가기 버튼 클릭
        ibutton_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 도전 리스트 버튼 클릭
        ibutton_listoficon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 리스트뷰 액티비티를 다이얼로그 형식으로 띄운다.
                Intent intent = new Intent(ModifyRoomActivity.this, SelecticonActivity.class);
                startActivityForResult(intent, REQUEST_CODE_DIALOG_ACTIVITY);
            }
        });

        // 도전 목표일 토글 변경
        switch_targetday.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (!isOn) {
                    edit_targetday.setText("∞");
                    edit_targetday.setEnabled(false);
                } else {
                    edit_targetday.setText("");
                    edit_targetday.setEnabled(true);
                }
            }
        });


        // 수정하기 버튼 클릭
        button_modifyroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DB에 저장해야할 모든 정보를 가져옴

                postTitle = edit_title.getText().toString().trim();
                postContent = edit_content.getText().toString().trim();

                // 아이콘을 따로 안정했으면 트로피
                if (TextUtils.isEmpty(outputIcon)) {
                    postIcon = "ic_trop";
                } else {
                    postIcon = outputIcon;
                }

                // 내용이 비어있을 경우, 제목을 내용으로
                if (TextUtils.isEmpty(postContent)) {
                    postContent = postTitle;
                }

                if (switch_targetday.isOn()) {
                    // 날짜를 설정했을 경우
                    postTargetday = edit_targetday.getText().toString().trim();
                } else {
                    postTargetday = "36500"; // 무한은 100년으로 설정.
                }

                // 예외 처리
                if (TextUtils.isEmpty(postTitle) || TextUtils.isEmpty(postTargetday)) {
                    StyleableToast.makeText(ModifyRoomActivity.this, "제목과 목표일은 필수로 입력해 주세요.", R.style.errorToast).show();
                    return;
                }
                if (Integer.parseInt(postTargetday) == 0) {
                    StyleableToast.makeText(ModifyRoomActivity.this, "목표일은 최소 1일 이상으로 설정해주세요.", R.style.errorToast).show();
                    return;
                }

                // 목표일이 현재 진행일보다 낮으면 챌린지가 바로 종료됨을 알림
                int targetDay = Integer.parseInt(postTargetday);
                int currentDay = Integer.parseInt(roomCurrentDay);
                if (targetDay <= currentDay) {

                    util.showCustomDialog(new Util.OnConfirmListener() {
                        @Override
                        public void onConfirm(boolean isConfirmed, String msg) {
                            if (isConfirmed) {
                                OnTaskCompleted onModifyRoomTaskCompleted2 = new OnTaskCompleted() {
                                    @Override
                                    public void onTaskCompleted(String result) {

                                        if (result.indexOf("MODIFY SUCCESS") != -1) {
                                            Intent intent = new Intent(getApplicationContext(), MainpageActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                            // overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
                                        } else {
                                            util.checkHttpResult(result);
                                        }
                                    }
                                };

                                HttpAsyncTask modifyRoomTask = new HttpAsyncTask(ModifyRoomActivity.this, onModifyRoomTaskCompleted2);
                                String phpFile = "service.php";
                                String postParameters = "service=modifyroom&roomidx=" + roomidx + "&roomtitle=" + postTitle + "&roomcontent=" + postContent + "&roomicon=" + postIcon + "&targetperiod=" + postTargetday;

                                modifyRoomTask.execute(phpFile, postParameters, util.getSessionKey());
                            }
                        }
                    }, "현재 달성율이 " + (targetDay - currentDay) + "일 초과했습니다.\n수정시 즉시 챌린지가 완료됩니다.", "confirm");
                } else {
                    OnTaskCompleted onModifyRoomTaskCompleted = new OnTaskCompleted() {
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

                    HttpAsyncTask modifyRoomTask = new HttpAsyncTask(ModifyRoomActivity.this, onModifyRoomTaskCompleted);
                    String phpFile = "service.php";
                    String postParameters = "service=modifyroom&roomidx=" + roomidx + "&roomtitle=" + postTitle + "&roomcontent=" + postContent + "&roomicon=" + postIcon + "&targetperiod=" + postTargetday;

                    modifyRoomTask.execute(phpFile, postParameters, util.getSessionKey());
                }
            }
        });
    }


    // 아이콘 선택 액티비티를 띄울때 결괏값을 받아오는 부분.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_DIALOG_ACTIVITY && resultCode == RESULT_OK) {
            if (data != null) {
                outputIcon = data.getStringExtra(SelecticonActivity.EXTRA_SELECTED_ITEM1);
                String outputTitle = data.getStringExtra(SelecticonActivity.EXTRA_SELECTED_ITEM2);

                // 아이콘 설정
                int drawableId = this.getResources().getIdentifier(outputIcon, "drawable", this.getPackageName());
                ibutton_listoficon.setImageResource(drawableId);

                // 제목 설정
                edit_title.setText(outputTitle);
            }
        }
    }
}