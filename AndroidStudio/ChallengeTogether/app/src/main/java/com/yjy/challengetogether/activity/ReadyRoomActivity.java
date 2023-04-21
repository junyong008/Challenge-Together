package com.yjy.challengetogether.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

    private ImageButton ibutton_close, ibutton_menu;
    private TextView textView_roomnum;
    private ImageView imageView_icon;
    private TextView textView_title, textView_writer, textView_targetday, textView_passwd, textView_content, textView_participates;
    private Button button_act;

    private RecyclerView recyclerView_participants;
    private ReadyRoomActivityRvAdapter adapter;
    private LinearLayoutManager llm;
    private List<RoomItem> items;
    private int position;
    private String roomidx, roomPermission;
    private String roomCurrentUserNum;
    private com.yjy.challengetogether.util.Util util = new Util(ReadyRoomActivity.this);

    @Override
    public void onBackPressed() {
        // 뒤로가기 할때 변경사항을 외부 리사이클러뷰에 최신화
        Intent resultIntent = new Intent();
        resultIntent.putExtra("result", "changed");
        resultIntent.putExtra("position", position);
        resultIntent.putExtra("changedCurrentUserNum", roomCurrentUserNum);

        setResult(Activity.RESULT_OK, resultIntent);

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readyroom);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        roomidx = intent.getStringExtra("roomidx");

        ibutton_close = findViewById(R.id.ibutton_close);
        ibutton_menu = findViewById(R.id.ibutton_menu);
        textView_roomnum = findViewById(R.id.textView_roomnum);
        imageView_icon = findViewById(R.id.imageView_icon);
        textView_title = findViewById(R.id.textView_title);
        textView_writer = findViewById(R.id.textView_writer);
        textView_targetday = findViewById(R.id.textView_targetday);
        textView_passwd = findViewById(R.id.textView_passwd);
        textView_content = findViewById(R.id.textView_content);
        textView_participates = findViewById(R.id.textView_participates);
        button_act = findViewById(R.id.button_act);


        // 뒤로가기 버튼 클릭
        ibutton_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        // 참가하기 or 시작하기 버튼 클릭
        button_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Status = button_act.getText().toString();
                if (Status.equals("시작하기")) {
                    // 시작하기

                    util.showCustomDialog(new Util.OnConfirmListener() {
                        @Override
                        public void onConfirm(boolean isConfirmed, String msg) {
                            if (isConfirmed) {

                                OnTaskCompleted onStartRoomTaskCompleted = new OnTaskCompleted() {
                                    @Override
                                    public void onTaskCompleted(String result) {

                                        if (result.indexOf("START SUCCESS") != -1) {
                                            util.saveOngoingChallenges();
                                            StyleableToast.makeText(ReadyRoomActivity.this, "챌린지가 시작되었습니다!", R.style.successToast).show();

                                            // 바로 시작방으로 화면전환을 하며 현재 액티비티 종료
                                            Intent intent = new Intent(ReadyRoomActivity.this, StartRoomActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("roomidx", roomidx);
                                            startActivity(intent);
                                            finish();

                                            // StartRoomActivty는 CompleteRoom이 아니면 뒤로가기를 했을때 모든 액티비티를 클리어하고 HomeFragment를 띄우므로 isUserClicked는 불필요

                                        } else {
                                            util.checkHttpResult(result);
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

                } else if (Status.equals("참여하기")) {
                    // 참여하기

                    util.showCustomDialog(new Util.OnConfirmListener() {
                        @Override
                        public void onConfirm(boolean isConfirmed, String msg) {
                            if (isConfirmed) {

                                OnTaskCompleted onStartRoomTaskCompleted = new OnTaskCompleted() {
                                    @Override
                                    public void onTaskCompleted(String result) {

                                        if (result.indexOf("PARTICIPATE SUCCESS") != -1) {
                                            recreate();
                                        } else if (result.indexOf("FULL ROOM") != -1) {
                                            StyleableToast.makeText(ReadyRoomActivity.this, "방이 가득 찼습니다.", R.style.errorToast).show();
                                            recreate();
                                        } else if (result.indexOf("ALREADY START") != -1) {
                                            StyleableToast.makeText(ReadyRoomActivity.this, "이미 시작한 방입니다.", R.style.errorToast).show();

                                            Intent resultIntent = new Intent();
                                            resultIntent.putExtra("result", "deleted");
                                            resultIntent.putExtra("position", position);

                                            setResult(Activity.RESULT_OK, resultIntent);

                                            finish();
                                        } else {
                                            util.checkHttpResult(result);
                                        }
                                    }
                                };

                                HttpAsyncTask startRoomTask = new HttpAsyncTask(ReadyRoomActivity.this, onStartRoomTaskCompleted);
                                String phpFile = "service.php";
                                String postParameters = "service=participateroom&roomidx=" + roomidx;

                                startRoomTask.execute(phpFile, postParameters, util.getSessionKey());
                            }
                        }
                    }, "참가하시겠습니까?", "confirm");
                } else {
                    // 나가기

                    OnTaskCompleted onExitRoomTaskCompleted = new OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(String result) {

                            if (result.indexOf("EXIT SUCCESS") != -1) {

                                // TogetherSearchFragment를 통해 들어온 경우 recreate() 메인에서 그 외엔 바로 메인이동
                                if (position != -1) {
                                    recreate();
                                } else {
                                    Intent intent = new Intent(ReadyRoomActivity.this, MainpageActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }

                            } else if (result.indexOf("DELETED ROOM") != -1) {
                                StyleableToast.makeText(ReadyRoomActivity.this, "삭제된 방입니다.", R.style.errorToast).show();

                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("result", "deleted");
                                resultIntent.putExtra("position", position);

                                setResult(Activity.RESULT_OK, resultIntent);

                                finish();
                            } else if (result.indexOf("ALREADY START") != -1) {
                                StyleableToast.makeText(ReadyRoomActivity.this, "이미 시작한 방입니다.", R.style.errorToast).show();

                                // 나갈때 이미 시작한 방이라는 메시지가 뜨는건 챌린지의 참여자 라는뜻. 고로 바로 StartRoom으로 이동시킨다.

                                Intent intent = new Intent(ReadyRoomActivity.this, StartRoomActivity.class);
                                intent.putExtra("roomidx", roomidx);
                                startActivity(intent);
                                finish();
                            } else {
                                util.checkHttpResult(result);
                            }
                        }
                    };

                    HttpAsyncTask exitRoomTask = new HttpAsyncTask(ReadyRoomActivity.this, onExitRoomTaskCompleted);
                    String phpFile = "service.php";
                    String postParameters = "service=exitroom&roomidx=" + roomidx;

                    exitRoomTask.execute(phpFile, postParameters, util.getSessionKey());
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
                        roomCurrentUserNum = obj1.getString("CURRENTUSERNUM");
                        String roomMaxUserNum = obj1.getString("MAXUSERNUM");
                        roomPermission = obj1.getString("PERMISSION");

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

                        // 방 권한에 따라 시작 or 참가 or 나가기 버튼으로 변경
                        if (roomPermission.equals("owner")) {
                            button_act.setText("시작하기");
                        } else if (roomPermission.equals("participant")) {
                            button_act.setText("나가기");
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

                    recyclerView_participants.setHasFixedSize(false);
                    recyclerView_participants.setLayoutManager(llm);

                    //SnapHelper snapHelper = new PagerSnapHelper();
                    //snapHelper.attachToRecyclerView(rv);

                    adapter = new ReadyRoomActivityRvAdapter(ReadyRoomActivity.this, items);
                    recyclerView_participants.setAdapter(adapter);

                } else if (result.indexOf("DELETED ROOM") != -1) {
                    StyleableToast.makeText(ReadyRoomActivity.this, "삭제된 방입니다.", R.style.errorToast).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("result", "deleted");
                    resultIntent.putExtra("position", position);

                    setResult(Activity.RESULT_OK, resultIntent);

                    finish();
                } else if (result.indexOf("ALREADY START") != -1) {
                    StyleableToast.makeText(ReadyRoomActivity.this, "이미 시작된 방입니다.", R.style.errorToast).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("result", "deleted");
                    resultIntent.putExtra("position", position);

                    setResult(Activity.RESULT_OK, resultIntent);

                    finish();
                } else {
                    util.checkHttpResult(result);
                }
            }
        };

        HttpAsyncTask loadReadyRoomInfoTask = new HttpAsyncTask(ReadyRoomActivity.this, onLoadReadyRoomInfoTaskCompleted);
        String phpFile = "service.php";
        String postParameters = "service=getreadyroominfos&roomidx=" + roomidx;

        loadReadyRoomInfoTask.execute(phpFile, postParameters, util.getSessionKey());


        // 메뉴 버튼 클릭
        ibutton_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

    }


    // 팝업 메뉴 표시
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(ReadyRoomActivity.this, view, Gravity.END,0,R.style.MyPopupMenu);
        popupMenu.inflate(R.menu.readyroom_menu);

        // 아이템 숨김 처리
        Menu menu = popupMenu.getMenu();
        if (!roomPermission.equals("owner")) {
            // 방 주인이 아닌경우 삭제 숨김
            MenuItem deleteMenuItem = menu.findItem(R.id.menu_room_delete);
            deleteMenuItem.setVisible(false);
        }

        // 옵션 처리
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_room_refresh:
                        refreshRoom();
                        return true;
                    case R.id.menu_room_delete:
                        deleteRoom();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    // 방 삭제 처리
    private void deleteRoom() {

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
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                util.checkHttpResult(result);
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

    // 방 새로고침
    private void refreshRoom() {
        recreate();
    }

}