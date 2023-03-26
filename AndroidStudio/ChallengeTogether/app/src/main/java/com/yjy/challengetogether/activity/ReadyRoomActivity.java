package com.yjy.challengetogether.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.adapter.ReadyRoomActivityRvAdapter;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.etc.RoomItem;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class ReadyRoomActivity extends AppCompatActivity {

    private ImageButton ibutton_close;
    private TextView textView_roomnum;
    private ImageView imageView_icon;
    private TextView textView_title, textView_writer, textView_targetday, textView_passwd, textView_content, textView_participates;
    private ImageButton ibutton_delete;
    private Button button_act;

    private RecyclerView recyclerView_participants;
    private ReadyRoomActivityRvAdapter adapter;
    private LinearLayoutManager llm;
    private List<RoomItem> items;
    private com.yjy.challengetogether.util.Util util = new Util(ReadyRoomActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readyroom);

        Intent intent = getIntent();
        String roomidx = intent.getStringExtra("roomidx");


        ibutton_close = findViewById(R.id.ibutton_close);
        textView_roomnum = findViewById(R.id.textView_roomnum);
        imageView_icon = findViewById(R.id.imageView_icon);
        textView_title = findViewById(R.id.textView_title);
        textView_writer = findViewById(R.id.textView_writer);
        textView_targetday = findViewById(R.id.textView_targetday);
        textView_passwd = findViewById(R.id.textView_passwd);
        textView_content = findViewById(R.id.textView_content);
        textView_participates = findViewById(R.id.textView_participates);
        ibutton_delete = findViewById(R.id.ibutton_delete);
        button_act = findViewById(R.id.button_act);


        // 뒤로가기 버튼 클릭
        ibutton_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // 삭제 버튼 클릭
        ibutton_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                util.showCustomDialog(new Util.OnConfirmListener() {
                    @Override
                    public void onConfirm(boolean isConfirmed, String msg) {
                        if (isConfirmed) {  // 안내 Dialog 확인버튼을 눌렀을 경우만

                            OnTaskCompleted onDeleteRoomTaskCompleted = new OnTaskCompleted() {
                                @Override
                                public void onTaskCompleted(String result) {

                                    if (result.indexOf("DELETE SUCCESS") != -1) {
                                        StyleableToast.makeText(ReadyRoomActivity.this, "삭제되었습니다.", R.style.successToast).show();

                                        // 메인 액티비티 실행 후 그 외 모두 삭제
                                        Intent intent = new Intent(getApplicationContext(), MainpageActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else if (result.indexOf("NO SESSION") != -1) {
                                        StyleableToast.makeText(ReadyRoomActivity.this, "세션이 만료되었습니다.\n다시 로그인해주세요.", R.style.errorToast).show();

                                        // 로그인 액티비티 실행 후 그 외 모두 삭제
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        StyleableToast.makeText(ReadyRoomActivity.this, result, R.style.errorToast).show();
                                        return;
                                    }
                                }
                            };

                            HttpAsyncTask deleteRoomTask = new HttpAsyncTask(ReadyRoomActivity.this, onDeleteRoomTaskCompleted);
                            String phpFile = "service.php";
                            String postParameters = "service=deleteroom&roomidx=" + roomidx;

                            deleteRoomTask.execute(phpFile, postParameters, util.getSessionKey());
                        }
                    }
                }, "방을 삭제하시겠습니까?", "confirm");
            }
        });


        // 참가하기 or 시작하기 버튼 클릭
        button_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Status = button_act.getText().toString();
                if (Status.equals("시작하기")) {
                    Log.d("ReadyRoomActivity", "시작하기");

                    util.showCustomDialog(new Util.OnConfirmListener() {
                        @Override
                        public void onConfirm(boolean isConfirmed, String msg) {
                            if (isConfirmed) {

                                OnTaskCompleted onStartRoomTaskCompleted = new OnTaskCompleted() {
                                    @Override
                                    public void onTaskCompleted(String result) {

                                        if (result.indexOf("START SUCCESS") != -1) {
                                            StyleableToast.makeText(ReadyRoomActivity.this, "챌린지가 시작되었습니다!", R.style.successToast).show();

                                            // 메인 액티비티 실행 후 그 외 모두 삭제
                                            Intent intent = new Intent(getApplicationContext(), MainpageActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else if (result.indexOf("NO SESSION") != -1) {
                                            StyleableToast.makeText(ReadyRoomActivity.this, "세션이 만료되었습니다.\n다시 로그인해주세요.", R.style.errorToast).show();

                                            // 로그인 액티비티 실행 후 그 외 모두 삭제
                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            StyleableToast.makeText(ReadyRoomActivity.this, result, R.style.errorToast).show();
                                            return;
                                        }
                                    }
                                };

                                HttpAsyncTask startRoomTask = new HttpAsyncTask(ReadyRoomActivity.this, onStartRoomTaskCompleted);
                                String phpFile = "service.php";
                                String postParameters = "service=startroom&roomidx=" + roomidx;

                                startRoomTask.execute(phpFile, postParameters, util.getSessionKey());
                            }
                        }
                    }, "챌린지를 시작하시겠습니까?", "confirm");

                } else {
                    Log.d("ReadyRoomActivity", "참여하기");
                }
            }
        });


        recyclerView_participants = findViewById(R.id.recyclerView_participants);
        llm = new LinearLayoutManager(ReadyRoomActivity.this);


        OnTaskCompleted onLoadReadyRoomInfoTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                Boolean isJSON = util.isJson(result);

                if (isJSON) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        // 전달받은 JSON에서 roomInfo 부분만 추출
                        JSONObject obj1 = new JSONObject(jsonObject.getString("roomInfo"));
                        String roomType = obj1.getString("CHALLENGETYPE");
                        String roomTitle = obj1.getString("TITLE");
                        String roomContent = obj1.getString("CONTENT");
                        String roomWriter = obj1.getString("NAME");
                        String roomTargetDay = obj1.getString("ENDTIME");
                        String roomPasswd = obj1.getString("PASSWD");
                        String roomCurrentUserNum = obj1.getString("CURRENTUSERNUM");
                        String roomMaxUserNum = obj1.getString("MAXUSERNUM");
                        String roomPermission = obj1.getString("PERMISSION");

                        // 방 정보 설정
                        textView_roomnum.setText(roomidx + " 번 방");

                        int drawableId = ReadyRoomActivity.this.getResources().getIdentifier(roomType, "drawable", ReadyRoomActivity.this.getPackageName());
                        imageView_icon.setImageResource(drawableId);

                        textView_title.setText(roomTitle);
                        textView_writer.setText("작성자  " + roomWriter);
                        if (Integer.parseInt(roomTargetDay) >= 36500) {
                            textView_targetday.setText("도전 목표일  무한");
                        } else {
                            textView_targetday.setText("도전 목표일  " + roomTargetDay + "일");
                        }

                        if (!TextUtils.isEmpty(roomPasswd)) {
                            textView_passwd.setText("비밀번호 [ " + roomPasswd + " ]");
                            textView_passwd.setVisibility(View.VISIBLE);
                        }
                        textView_content.setText(roomContent);
                        textView_participates.setText("참여할 사람들  [ " + roomCurrentUserNum + " / " + roomMaxUserNum + " ]");

                        // 방 권한에 따라 삭제, 시작 버튼 visible
                        if (roomPermission.equals("owner")) {
                            ibutton_delete.setVisibility(View.VISIBLE);
                            button_act.setText("시작하기");
                        }


                        // 전달받은 JSON에서 roomMemberInfo 부분만 추출 ( 방 참가자 정보들 )
                        JSONArray jsonArray2 = new JSONArray(jsonObject.getString("roomMemberInfo"));
                        items = new ArrayList<>();
                        for (int i = 0; i < jsonArray2.length(); i++) {
                            JSONObject obj2 = jsonArray2.getJSONObject(i);
                            RoomItem item = new RoomItem();

                            item.setUseridx(obj2.getString("USER_IDX"));
                            item.setPermission(roomPermission);
                            item.setUsername(obj2.getString("NAME"));

                            items.add(item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    recyclerView_participants.setHasFixedSize(true);
                    recyclerView_participants.setLayoutManager(llm);

                    //SnapHelper snapHelper = new PagerSnapHelper();
                    //snapHelper.attachToRecyclerView(rv);

                    adapter = new ReadyRoomActivityRvAdapter(ReadyRoomActivity.this, items);
                    recyclerView_participants.setAdapter(adapter);

                } else if (result.indexOf("NO SESSION") != -1) {
                    StyleableToast.makeText(ReadyRoomActivity.this, "세션이 만료되었습니다.\n다시 로그인해주세요.", R.style.errorToast).show();

                    // 로그인 액티비티 실행 후 그 외 모두 삭제
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    StyleableToast.makeText(ReadyRoomActivity.this, result, R.style.errorToast).show();
                    return;
                }
            }
        };

        HttpAsyncTask loadReadyRoomInfoTask = new HttpAsyncTask(ReadyRoomActivity.this, onLoadReadyRoomInfoTaskCompleted);
        String phpFile = "service.php";
        String postParameters = "service=getreadyroominfos&roomidx=" + roomidx;

        loadReadyRoomInfoTask.execute(phpFile, postParameters, util.getSessionKey());


    }
}