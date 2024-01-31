package com.yjy.challengetogether.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.adapter.NotificationRvAdapter;
import com.yjy.challengetogether.etc.NotificationItem;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class NotificationRecordActivity extends AppCompatActivity {

    private ImageButton ibutton_back, ibutton_menu;
    private RecyclerView recyclerView_notifications;
    private NotificationRvAdapter adapter;
    private LinearLayoutManager llm;
    private List<NotificationItem> items;

    private ImageView imageView_none;
    private TextView textView_none;
    private SwipeRefreshLayout refresh;
    private ProgressBar progressBar;
    private NestedScrollView ScrollView;

    private int limit, minAlarmIdx, countOfLastLoad;

    private Util util = new Util(NotificationRecordActivity.this);
    private OnTaskCompleted onLoadAlarmTaskCompleted;

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificationrecord);

        ibutton_back = findViewById(R.id.ibutton_back);
        ibutton_menu = findViewById(R.id.ibutton_menu);
        imageView_none = findViewById(R.id.imageView_none);
        textView_none = findViewById(R.id.textView_none);
        refresh = findViewById(R.id.refresh);
        progressBar = findViewById(R.id.progressBar);
        ScrollView = findViewById(R.id.ScrollView);

        recyclerView_notifications = findViewById(R.id.recyclerView_notifications);
        llm = new LinearLayoutManager(NotificationRecordActivity.this);

        limit = 10; // 한번에 보여지는 항목 개수
        minAlarmIdx = 10000000;
        countOfLastLoad = 0; // 만약 마지막 받아온 항목이 limit 일때만 추가로 로드작업을 하기 위함.
        items = new ArrayList<>(); // 외부에 선언하여 계속해서 서버 통신 없이 항목을 적재받기 위함.

        // 상단 뒤로가기 버튼 클릭
        ibutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 메뉴 버튼 클릭
        ibutton_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        onLoadAlarmTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                Boolean isJSON = util.isJson(result);

                if (isJSON) {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        countOfLastLoad = 0;

                        // items를 미리 onCreate 에서 선언해두고 서버요청을 할때마다 계속 쌓는 방식. 서버 부담이 완화된다.
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            NotificationItem item = new NotificationItem();

                            item.setUseralarmidx(obj.getString("USERALARM_IDX"));
                            item.setTitle(obj.getString("TITLE"));
                            item.setContent(obj.getString("CONTENT"));
                            item.setCreatedate(obj.getString("CREATEDATE"));
                            item.setType(obj.getString("TYPE"));
                            item.setLinkidx(obj.getString("LINKIDX"));
                            item.setIsview(obj.getString("ISVIEW"));

                            countOfLastLoad++;
                            if (obj.getInt("USERALARM_IDX") < minAlarmIdx) {
                                minAlarmIdx = obj.getInt("USERALARM_IDX");
                            }

                            items.add(item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    recyclerView_notifications.setHasFixedSize(false);
                    recyclerView_notifications.setLayoutManager(llm);

                    adapter = new NotificationRvAdapter(NotificationRecordActivity.this, items, util);
                    recyclerView_notifications.setAdapter(adapter);


                    new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                        @Override
                        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                            return false;
                        }

                        @Override
                        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                            int position = viewHolder.getAdapterPosition();

                            switch(direction){

                                case ItemTouchHelper.LEFT:

                                    //삭제할 아이템
                                    NotificationItem deleteItem = items.get(position);

                                    // 배열로부터 삭제
                                    items.remove(position);
                                    adapter.notifyItemRemoved(position);

                                    // 글이 더이상 존재하지 않으면 안내메시지 보이기
                                    if(items.size() > 0) {
                                        imageView_none.setVisibility(View.GONE);
                                        textView_none.setVisibility(View.GONE);
                                    } else {
                                        imageView_none.setVisibility(View.VISIBLE);
                                        textView_none.setVisibility(View.VISIBLE);
                                    }

                                    OnTaskCompleted onDeleteNotiTaskCompleted = new OnTaskCompleted() {
                                        @Override
                                        public void onTaskCompleted(String result) {

                                            if (result.indexOf("DELETE SUCCESS") != -1) {
                                                // 5개가 되면 서버로 부터 추가로 받아와 보충
                                                if (items.size() == 5) {
                                                    progressBar.setVisibility(View.VISIBLE);

                                                    HttpAsyncTask loadAlarmTask = new HttpAsyncTask(NotificationRecordActivity.this, onLoadAlarmTaskCompleted);
                                                    String phpFile = "service.php";
                                                    String postParameters = "service=getnotifications&limit=" + 5 + "&lastalarmidx=" + minAlarmIdx;

                                                    loadAlarmTask.execute(phpFile, postParameters, util.getSessionKey());
                                                }
                                            } else {
                                                util.checkHttpResult(result);
                                            }
                                        }
                                    };

                                    HttpAsyncTask deleteNotiTask = new HttpAsyncTask(NotificationRecordActivity.this, onDeleteNotiTaskCompleted);
                                    String phpFile = "service.php";
                                    String postParameters = "service=deletealarm&alarmidx=" + deleteItem.getUseralarmidx();
                                    deleteNotiTask.execute(phpFile, postParameters, util.getSessionKey());

                                    break;
                            }
                        }

                        @Override
                        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder,
                                    dX, dY, actionState, isCurrentlyActive)
                                    .addSwipeLeftBackgroundColor(Color.parseColor("#DB4455"))
                                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                                    .create()
                                    .decorate();

                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        }
                    }).attachToRecyclerView(recyclerView_notifications);


                    progressBar.setVisibility(View.GONE);
                    refresh.setRefreshing(false);

                    // 글이 존재하면 안내메시지 가리기
                    if(items.size() > 0) {
                        imageView_none.setVisibility(View.GONE);
                        textView_none.setVisibility(View.GONE);
                    } else {
                        imageView_none.setVisibility(View.VISIBLE);
                        textView_none.setVisibility(View.VISIBLE);
                    }

                } else {
                    util.checkHttpResult(result);
                }
            }
        };


        HttpAsyncTask loadAlarmTask = new HttpAsyncTask(NotificationRecordActivity.this, onLoadAlarmTaskCompleted);
        String phpFile = "service.php";
        String postParameters = "service=getnotifications&limit=" + limit + "&lastalarmidx=" + minAlarmIdx;

        loadAlarmTask.execute(phpFile, postParameters, util.getSessionKey());

        // 스크롤바 움직일때
        ScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                // 스크롤이 끝에 닿고, 마지막 받아온 항목이 limit 일때만 로드
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() && countOfLastLoad == limit) {
                    progressBar.setVisibility(View.VISIBLE);

                    HttpAsyncTask loadAlarmTask = new HttpAsyncTask(NotificationRecordActivity.this, onLoadAlarmTaskCompleted);
                    String phpFile = "service.php";
                    String postParameters = "service=getnotifications&limit=" + limit + "&lastalarmidx=" + minAlarmIdx;

                    loadAlarmTask.execute(phpFile, postParameters, util.getSessionKey());
                }
            }
        });

        // 새로고침
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                minAlarmIdx = 10000000;
                items = new ArrayList<>();

                HttpAsyncTask loadAlarmTask = new HttpAsyncTask(NotificationRecordActivity.this, onLoadAlarmTaskCompleted);
                String phpFile = "service.php";
                String postParameters = "service=getnotifications&limit=" + limit + "&lastalarmidx=" + minAlarmIdx;

                loadAlarmTask.execute(phpFile, postParameters, util.getSessionKey());
            }
        });

    }

    // 팝업 메뉴 표시
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(NotificationRecordActivity.this, view, Gravity.END,0,R.style.MyPopupMenu);
        popupMenu.inflate(R.menu.notificationrecord_menu);

        // 옵션 처리
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_noti_setting:
                        Intent intent = new Intent(NotificationRecordActivity.this, PushSettingActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                        return true;
                    case R.id.menu_noti_delete:
                        deleteNoti();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    // 알림 전체 삭제
    private void deleteNoti() {

        util.showCustomDialog(new Util.OnConfirmListener() {
            @Override
            public void onConfirm(boolean isConfirmed, String msg) {
                if (isConfirmed) {  // 안내 Dialog 확인버튼을 눌렀을 경우만

                    OnTaskCompleted onDeleteNotiTaskCompleted = new OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(String result) {

                            if (result.indexOf("DELETE SUCCESS") != -1) {
                                recreate();
                            } else {
                                util.checkHttpResult(result);
                            }
                        }
                    };

                    HttpAsyncTask deleteNotiTask = new HttpAsyncTask(NotificationRecordActivity.this, onDeleteNotiTaskCompleted);
                    String phpFile = "service.php";
                    String postParameters = "service=deletealarm&alarmidx=";

                    deleteNotiTask.execute(phpFile, postParameters, util.getSessionKey());
                }
            }
        }, "모든 알림을 지우시겠습니까?", "confirm");
    }
}