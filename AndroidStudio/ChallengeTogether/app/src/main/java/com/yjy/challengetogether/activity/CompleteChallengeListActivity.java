package com.yjy.challengetogether.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.adapter.CompleteChallengeListRvAdapter;
import com.yjy.challengetogether.etc.HomeItem;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class CompleteChallengeListActivity extends AppCompatActivity {

    private ImageView ivbutton_back;
    private TextView textView_none;
    private RecyclerView recyclerView_mychallenges;
    private CompleteChallengeListRvAdapter adapter;
    private LinearLayoutManager llm;
    private List<HomeItem> items;   // 어차피 받아오고 전달하는 내용 자체는 크게 다르지 않으니 재활용.
    private com.yjy.challengetogether.util.Util util = new Util(CompleteChallengeListActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completechallengelist);

        ivbutton_back = findViewById(R.id.ivbutton_back);
        textView_none = findViewById(R.id.textView_none);
        recyclerView_mychallenges = findViewById(R.id.recyclerView_mychallenges);

        // 뒤로가기 버튼 클릭
        ivbutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        llm = new LinearLayoutManager(CompleteChallengeListActivity.this);

        OnTaskCompleted onLoadCompleteRoomTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                Boolean isJSON = util.isJson(result);

                if (isJSON) {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        // items 이란 배열을 만들고, json 안의 내용을 HomeItem 클래스 객체 형태로 item에 저장후 이걸 items 배열에 주르륵 저장
                        items = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj2 = jsonArray.getJSONObject(i);

                            // 진행중이거나 대기중인 챌린지는 패스
                            if (obj2.getString("RECENTSTARTTIME").equals("0000-00-00 00:00:00")) {
                                continue;
                            }
                            int dayOfCurrentTime = Integer.parseInt(util.DiffWithLocalTime(obj2.getString("RECENTSTARTTIME"), "DAY"));
                            int endTime = obj2.getInt("ENDTIME");
                            if (dayOfCurrentTime < endTime) {
                                continue;
                            }

                            HomeItem item = new HomeItem();

                            item.setRoomidx(obj2.getString("ROOM_IDX"));
                            item.setIcon(obj2.getString("CHALLENGETYPE"));
                            item.setTitle(obj2.getString("TITLE"));
                            item.setContent(obj2.getString("CONTENT"));
                            item.setRecentStartTime(obj2.getString("RECENTSTARTTIME"));
                            item.setEndTime(obj2.getLong("ENDTIME"));

                            items.add(item);
                        }

                        // 성공한 챌린지가 존재하면 안내메시지 가리기
                        if(items.size() > 0) {
                            textView_none.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    recyclerView_mychallenges.setHasFixedSize(true);
                    recyclerView_mychallenges.setLayoutManager(llm);

                    //SnapHelper snapHelper = new PagerSnapHelper();
                    //snapHelper.attachToRecyclerView(rv);

                    adapter = new CompleteChallengeListRvAdapter(CompleteChallengeListActivity.this, items);
                    recyclerView_mychallenges.setAdapter(adapter);

                } else if (result.indexOf("NO SESSION") != -1) {
                    StyleableToast.makeText(CompleteChallengeListActivity.this, "세션이 만료되었습니다.\n다시 로그인해주세요.", R.style.errorToast).show();

                    // 로그인 액티비티 실행 후 그 외 모두 삭제
                    Intent intent = new Intent(CompleteChallengeListActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    StyleableToast.makeText(CompleteChallengeListActivity.this, result, R.style.errorToast).show();
                    return;
                }
            }
        };

        HttpAsyncTask loadCompleteRoomTask = new HttpAsyncTask(CompleteChallengeListActivity.this, onLoadCompleteRoomTaskCompleted);
        String phpFile = "service.php";
        String postParameters = "service=getcompleterooms";

        loadCompleteRoomTask.execute(phpFile, postParameters, util.getSessionKey());



    }
}