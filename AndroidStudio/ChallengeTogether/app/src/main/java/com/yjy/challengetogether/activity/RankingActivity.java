package com.yjy.challengetogether.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.adapter.RankingRvAdapter;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.etc.RankingItem;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class RankingActivity extends AppCompatActivity {

    private com.yjy.challengetogether.util.Util util = new Util(RankingActivity.this);
    private ImageButton ibutton_back;
    private RecyclerView recyclerView_participants;
    private RankingRvAdapter adapter;
    private LinearLayoutManager llm;
    private List<RankingItem> items;

    // 랭킹 액티비티 나갈시 타이머 회수
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.stopUpdatingTime();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        Intent intent = getIntent();
        String roomidx = intent.getStringExtra("roomidx");
        String roomTargetDay = intent.getStringExtra("roomtargetday");

        ibutton_back = findViewById(R.id.ibutton_back);

        // 뒤로가기 버튼 클릭
        ibutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        recyclerView_participants = findViewById(R.id.recyclerView_participants);
        llm = new LinearLayoutManager(RankingActivity.this);

        OnTaskCompleted onLoadRankingTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                Boolean isJSON = util.isJson(result);

                if (isJSON) {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        items = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            RankingItem item = new RankingItem();

                            item.setNickname(obj.getString("NAME"));
                            item.setRecentstarttime(obj.getString("RECENTSTARTTIME")); // 타이머로 실시간 업데이트 위함
                            item.setCurrenttime(Integer.parseInt(util.DiffWithLocalTime(obj.getString("RECENTSTARTTIME"), "SEC")));

                            items.add(item);
                        }

                        // item들을 Currenttime에 따라 높은 순으로 정렬
                        Collections.sort(items, Collections.reverseOrder(new Comparator<RankingItem>() {
                            @Override
                            public int compare(RankingItem item1, RankingItem item2) {
                                return Integer.compare(item1.getCurrenttime(), item2.getCurrenttime());
                            }
                        }));

                        // 각 item들을 돌면서 자신의 Currenttime보다 높은 아이템이 몇개 있는지 저장
                        for (int i = 0; i < items.size(); i++) {
                            int rank = 1;
                            for (int j = 0; j < items.size(); j++) {
                                if (i == j) continue;
                                if (items.get(i).getCurrenttime() < items.get(j).getCurrenttime()) {
                                    rank++;
                                }
                            }
                            items.get(i).setRanking(rank);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    recyclerView_participants.setHasFixedSize(true);
                    recyclerView_participants.setLayoutManager(llm);

                    adapter = new RankingRvAdapter(RankingActivity.this, items, roomTargetDay);
                    recyclerView_participants.setAdapter(adapter);

                } else if (result.indexOf("NO SESSION") != -1) {
                    StyleableToast.makeText(RankingActivity.this, "세션이 만료되었습니다.\n다시 로그인해주세요.", R.style.errorToast).show();

                    // 로그인 액티비티 실행 후 그 외 모두 삭제
                    Intent intent = new Intent(RankingActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    StyleableToast.makeText(RankingActivity.this, result, R.style.errorToast).show();
                    return;
                }
            }
        };

        HttpAsyncTask loadRankingTask = new HttpAsyncTask(RankingActivity.this, onLoadRankingTaskCompleted);
        String phpFile = "service.php";
        String postParameters = "service=getranking&roomidx=" + roomidx;

        loadRankingTask.execute(phpFile, postParameters, util.getSessionKey());



    }
}