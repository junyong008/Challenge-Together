package com.yjy.challengetogether.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.adapter.MyPostCommentRvAdapter;
import com.yjy.challengetogether.etc.CommunityPostItem;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookmarkPostActivity extends AppCompatActivity {

    private ImageButton ibutton_back;
    private RecyclerView recyclerView_posts;
    private MyPostCommentRvAdapter adapter;
    private LinearLayoutManager llm;
    private List<CommunityPostItem> items;

    private TextView textView_none;
    private SwipeRefreshLayout refresh;
    private ProgressBar progressBar;
    private NestedScrollView ScrollView;

    private int limit, minPostIdx, countOfLastLoad;

    private Util util = new Util(BookmarkPostActivity.this);

    @Override
    public void onBackPressed() {
        // 뒤로가기 할시 사용자가 자신의 글을 삭제하거나 수정했을 수 있으니 다시 불러오게 result 반환
        Intent resultIntent = new Intent();
        resultIntent.putExtra("result", "success");
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

        overridePendingTransition(R.anim.stay, R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarkpost);

        ibutton_back = findViewById(R.id.ibutton_back);
        textView_none = findViewById(R.id.textView_none);
        refresh = findViewById(R.id.refresh);
        progressBar = findViewById(R.id.progressBar);
        ScrollView = findViewById(R.id.ScrollView);

        recyclerView_posts = findViewById(R.id.recyclerView_posts);
        llm = new LinearLayoutManager(BookmarkPostActivity.this);

        limit = 10; // 한번에 보여지는 항목 개수
        minPostIdx = 10000000; // DB에 요청을 하면 COMPOST_IDX를 큰것부터 limit씩 끊어서 보내주는데 마지막 최소 COMPOST_IDX를 저장함. 다음 요청시 마지막 COMPOST_IDX보다 작은거부터 추가로 받아오는 방식
        countOfLastLoad = 0; // 만약 마지막 받아온 항목이 limit 일때만 추가로 로드작업을 하기 위함.
        items = new ArrayList<>(); // 외부에 선언하여 계속해서 서버 통신 없이 항목을 적재받기 위함.

        // 상단 뒤로가기 버튼 클릭
        ibutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("result", "success");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();

                overridePendingTransition(R.anim.stay, R.anim.slide_out_right);
            }
        });


        OnTaskCompleted onLoadBookmarkPostTaskCompleted = new OnTaskCompleted() {
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

                            CommunityPostItem item = new CommunityPostItem();

                            item.setPostidx(obj.getString("COMPOST_IDX"));
                            item.setUseridx(obj.getString("USER_IDX"));
                            item.setCreatedate(obj.getString("CREATEDATE"));
                            item.setContent(obj.getString("CONTENT"));
                            item.setModifydate(obj.getString("MODIFYDATE"));
                            item.setLike(obj.getString("LIKECOUNT"));
                            item.setDislike(obj.getString("DISLIKECOUNT"));
                            item.setNickname(obj.getString("NAME"));
                            item.setCommentcount(obj.getString("COMMENTCOUNT"));


                            countOfLastLoad++;
                            if (obj.getInt("COMPOST_IDX") < minPostIdx) {
                                minPostIdx = obj.getInt("COMPOST_IDX");
                            }

                            items.add(item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    recyclerView_posts.setHasFixedSize(true);
                    recyclerView_posts.setLayoutManager(llm);

                    //SnapHelper snapHelper = new PagerSnapHelper();
                    //snapHelper.attachToRecyclerView(rv);

                    adapter = new MyPostCommentRvAdapter(BookmarkPostActivity.this, items, util);
                    recyclerView_posts.setAdapter(adapter);

                    progressBar.setVisibility(View.GONE);
                    refresh.setRefreshing(false);

                    // 글이 존재하면 안내메시지 가리기
                    if(items.size() > 0) {
                        textView_none.setVisibility(View.GONE);
                    } else {
                        textView_none.setVisibility(View.VISIBLE);
                    }

                } else {
                    util.checkHttpResult(result);
                }
            }
        };


        HttpAsyncTask loadBookmarkPostTask = new HttpAsyncTask(BookmarkPostActivity.this, onLoadBookmarkPostTaskCompleted);
        String phpFile = "service.php";
        String postParameters = "service=getcommunitybookmarks&limit=" + limit + "&lastpostidx=" + minPostIdx;

        loadBookmarkPostTask.execute(phpFile, postParameters, util.getSessionKey());

        // 스크롤바 움직일때
        ScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                // 스크롤이 끝에 닿고, 마지막 받아온 항목이 limit 일때만 로드
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() && countOfLastLoad == limit) {
                    progressBar.setVisibility(View.VISIBLE);

                    HttpAsyncTask loadBookmarkPostTask = new HttpAsyncTask(BookmarkPostActivity.this, onLoadBookmarkPostTaskCompleted);
                    String phpFile = "service.php";
                    String postParameters = "service=getcommunitybookmarks&limit=" + limit + "&lastpostidx=" + minPostIdx;

                    loadBookmarkPostTask.execute(phpFile, postParameters, util.getSessionKey());
                }
            }
        });

        // 새로고침
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                minPostIdx = 10000000;
                items = new ArrayList<>();

                HttpAsyncTask loadBookmarkPostTask = new HttpAsyncTask(BookmarkPostActivity.this, onLoadBookmarkPostTaskCompleted);
                String phpFile = "service.php";
                String postParameters = "service=getcommunitybookmarks&limit=" + limit + "&lastpostidx=" + minPostIdx;

                loadBookmarkPostTask.execute(phpFile, postParameters, util.getSessionKey());
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra("result");
            if (result.equals("success")) {
                recreate();
            }
        }
    }
}