package com.yjy.challengetogether.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.mcdev.quantitizerlibrary.HorizontalQuantitizer;
import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.RangeTimePickerDialog;
import com.yjy.challengetogether.util.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.github.muddz.styleabletoast.StyleableToast;

public class AddRoomActivity extends AppCompatActivity {

    private Boolean isNewChallenge;
    private TextView textView_title;
    private ImageButton ibutton_close;
    private EditText edit_title;
    private String outputIcon;
    private static final int REQUEST_CODE_DIALOG_ACTIVITY = 1;
    private ImageButton ibutton_listoficon;
    private EditText edit_content;
    private EditText edit_targetday;
    private LabeledSwitch switch_targetday;
    private TextView textView_together;
    private CheckBox checkBox_together;
    private ConstraintLayout constraintLayout_startDay, constraintLayout4;
    private HorizontalQuantitizer hq_particicount;
    private EditText edit_passwd;
    private Button button_startDay, button_addroom;
    private View view2;
    Calendar currentCalendar = Calendar.getInstance();
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.getDefault());
    private com.yjy.challengetogether.util.Util util = new Util(AddRoomActivity.this);


    @Override
    public void onBackPressed() {
        finish();
        //overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addroom);

        isNewChallenge = getIntent().getBooleanExtra("isNew", true);

        textView_title = findViewById(R.id.textView_title);
        ibutton_close = findViewById(R.id.ibutton_close);
        edit_title = findViewById(R.id.edit_title);
        ibutton_listoficon = findViewById(R.id.ibutton_listoficon);
        edit_content = findViewById(R.id.edit_content);
        switch_targetday = findViewById(R.id.switch_targetday);
        edit_targetday = findViewById(R.id.edit_targetday);
        textView_together = findViewById(R.id.textView_together);
        checkBox_together = findViewById(R.id.checkBox_together);
        constraintLayout_startDay = findViewById(R.id.constraintLayout_startDay);
        constraintLayout4 = findViewById(R.id.constraintLayout4);
        hq_particicount = findViewById(R.id.hq_particicount);
        edit_passwd = findViewById(R.id.edit_passwd);
        button_startDay = findViewById(R.id.button_startDay);
        button_addroom = findViewById(R.id.button_addroom);
        view2 = findViewById(R.id.view2);

        // 새로운 챌린지가 아니라면 시작일 지정 UI 보여주고 같이하기 UI 숨기기
        if (!isNewChallenge) {
            textView_title.setText("자유 챌린지");
            constraintLayout_startDay.setVisibility(View.VISIBLE);
            view2.setVisibility(View.INVISIBLE);
            textView_together.setVisibility(View.INVISIBLE);
            checkBox_together.setVisibility(View.INVISIBLE);

            // 현재 시간으로 기본값 설정
            String defaultDateTime = sdf.format(currentCalendar.getTime());
            button_startDay.setText(defaultDateTime);
        }

        // 시작일 선택 버튼 클릭
        button_startDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddRoomActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // TimePickerDialog를 표시하여 시간 선택
                                RangeTimePickerDialog timePickerDialog = new RangeTimePickerDialog(AddRoomActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                // 선택된 날짜 및 시간을 저장
                                                calendar.set(Calendar.YEAR, year);
                                                calendar.set(Calendar.MONTH, month);
                                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                calendar.set(Calendar.MINUTE, minute);

                                                // 선택된 날짜와 시간을 버튼 텍스트에 표시
                                                String selectedDateTime = sdf.format(calendar.getTime());
                                                button_startDay.setText(selectedDateTime);
                                            }
                                        }, 0, 0, false);

                                // 날짜가 오늘이라면 현재 시간 이후로는 선택 불가하게 막기
                                if (year == currentCalendar.get(Calendar.YEAR)
                                        && month == currentCalendar.get(Calendar.MONTH)
                                        && dayOfMonth == currentCalendar.get(Calendar.DAY_OF_MONTH)
                                ) {
                                    timePickerDialog.setMax(currentCalendar.get(Calendar.HOUR_OF_DAY), currentCalendar.get(Calendar.MINUTE));
                                }
                                timePickerDialog.show();
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                // 현재 날짜 이후의 날짜는 선택하지 못하게 설정
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

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
                Intent intent = new Intent(AddRoomActivity.this, SelecticonActivity.class);
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

        // 같이하기 체크박스 변경
        checkBox_together.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 체크박스가 체크되었을 때 : 같이하기 레이아웃 표시
                    constraintLayout4.setVisibility(View.VISIBLE);
                } else {
                    // 체크박스가 체크해제되었을 때 : 같이하기 레이아웃 가리기
                    constraintLayout4.setVisibility(View.GONE);
                }
            }
        });

        // 같이할 인원 수 레이아웃 설정
        constraintLayout4.setVisibility(View.GONE);
        hq_particicount.setReadOnly(true);
        hq_particicount.setMinValue(2);
        hq_particicount.setMaxValue(10);
        hq_particicount.setValue(2);
        hq_particicount.setPlusIconBackgroundColor("#F9BB74");
        hq_particicount.setMinusIconBackgroundColor("#F9BB74");
        hq_particicount.setMinusIconColor("#FFFFFF");
        hq_particicount.setPlusIconColor("#FFFFFF");

        // 생성하기 버튼 클릭
        button_addroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DB에 저장해야할 모든 정보를 가져옴

                String postTitle = edit_title.getText().toString().trim();
                String postContent = edit_content.getText().toString().trim();

                // 아이콘을 따로 안정했으면 트로피
                String postIcon;
                if (TextUtils.isEmpty(outputIcon)) {
                    postIcon = "ic_trop";
                } else {
                    postIcon = outputIcon;
                }

                // 내용이 비어있을 경우, 제목을 내용으로
                if (TextUtils.isEmpty(postContent)) {
                    postContent = postTitle;
                }


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String postStartDay = sdf.format(calendar.getTime());

                String postTargetday = "";
                if (switch_targetday.isOn()) {
                    // 날짜를 설정했을 경우
                    postTargetday = edit_targetday.getText().toString().trim();
                } else {
                    postTargetday = "36500"; // 무한은 100년으로 설정.
                }

                // 혼자하면 최대 참가 인원이 1명
                String postMaxperson = "1";
                String postPwd = "";
                if (checkBox_together.isChecked()) {
                    // 같이하기를 체크헀을 경우
                    postMaxperson = String.valueOf(hq_particicount.getValue());
                    postPwd = edit_passwd.getText().toString().trim();
                }

                // 예외 처리
                if (TextUtils.isEmpty(postTitle) || TextUtils.isEmpty(postTargetday)) {
                    StyleableToast.makeText(AddRoomActivity.this, "제목과 목표일은 필수로 입력해 주세요.", R.style.errorToast).show();
                    return;
                }
                if (Integer.parseInt(postTargetday) == 0) {
                    StyleableToast.makeText(AddRoomActivity.this, "목표일은 최소 1일 이상으로 설정해주세요.", R.style.errorToast).show();
                    return;
                }



                OnTaskCompleted onAddRoomTaskCompleted = new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(String result) {

                        if (result.indexOf("ADD SUCCESS") != -1) {
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

                HttpAsyncTask addRoomTask = new HttpAsyncTask(AddRoomActivity.this, onAddRoomTaskCompleted);
                String phpFile = "service.php";
                String postParameters;
                if (isNewChallenge)
                    postParameters = "service=addroom&roomtitle=" + postTitle + "&roomcontent=" + postContent + "&roomicon=" + postIcon + "&targetperiod=" + postTargetday + "&maxparticipant=" + postMaxperson + "&roompwd=" + postPwd;
                else
                    postParameters = "service=addfreemoderoom&roomtitle=" + postTitle + "&roomcontent=" + postContent + "&roomicon=" + postIcon + "&targetperiod=" + postTargetday + "&startDate=" + postStartDay;

                addRoomTask.execute(phpFile, postParameters, util.getSessionKey());
                /*
                postTitle
                postContent
                postIcon
                postTargetday
                postMaxperson
                postPwd
                 */
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