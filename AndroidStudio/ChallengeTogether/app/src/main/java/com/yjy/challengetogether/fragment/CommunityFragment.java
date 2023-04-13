package com.yjy.challengetogether.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.activity.AddPostActivity;
import com.yjy.challengetogether.activity.BookmarkPostActivity;
import com.yjy.challengetogether.activity.MyCommentActivity;
import com.yjy.challengetogether.activity.MyPostActivity;
import com.yjy.challengetogether.adapter.CommunityFragmentRvAdapter;
import com.yjy.challengetogether.etc.CommunityPostItem;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {

    private View view;
    private String TAG  = "COMMUNITY 프레그먼트";

    private RecyclerView recyclerView_posts;
    private CommunityFragmentRvAdapter adapter;
    private LinearLayoutManager llm;
    private List<CommunityPostItem> items;

    private EditText edit_search;
    private TextView textView_none;
    private SwipeRefreshLayout refresh;
    private ProgressBar progressBar;
    private NestedScrollView ScrollView;
    private ImageButton ibutton_search, ibutton_menu, ibutton_addpost, ibutton_closesearch;
    private Util util;
    private int limit, minPostIdx, countOfLastLoad;
    private String searchword;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle saveInstanceState) {
        Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_community, container, false);

        util = new Util(requireActivity());

        edit_search = view.findViewById(R.id.edit_search);
        ibutton_search = view.findViewById(R.id.ibutton_search);
        ibutton_closesearch = view.findViewById(R.id.ibutton_closesearch);
        ibutton_menu = view.findViewById(R.id.ibutton_menu);
        textView_none = view.findViewById(R.id.textView_none);
        refresh = view.findViewById(R.id.refresh);
        progressBar = view.findViewById(R.id.progressBar);
        ScrollView = view.findViewById(R.id.ScrollView);
        ibutton_addpost = view.findViewById(R.id.ibutton_addpost);
        recyclerView_posts = view.findViewById(R.id.recyclerView_posts);
        llm = new LinearLayoutManager(getActivity());

        limit = 10; // 한번에 보여지는 항목 개수
        minPostIdx = 10000000; // DB에 요청을 하면 COMPOST_IDX를 큰것부터 limit씩 끊어서 보내주는데 마지막 최소 COMPOST_IDX를 저장함. 다음 요청시 마지막 COMPOST_IDX보다 작은거부터 추가로 받아오는 방식
        searchword = "";
        countOfLastLoad = 0; // 만약 마지막 받아온 항목이 limit 일때만 추가로 로드작업을 하기 위함.
        items = new ArrayList<>(); // 외부에 선언하여 계속해서 서버 통신 없이 항목을 적재받기 위함.

        OnTaskCompleted onLoadPostTaskCompleted = new OnTaskCompleted() {
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

                    adapter = new CommunityFragmentRvAdapter(getActivity().getApplication(), CommunityFragment.this, items, util);
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

        HttpAsyncTask loadPostTask = new HttpAsyncTask(getActivity(), onLoadPostTaskCompleted);
        String phpFile = "service.php";
        String postParameters = "service=getcommunityposts&searchword=&limit=" + limit + "&lastpostidx=" + minPostIdx;

        loadPostTask.execute(phpFile, postParameters, util.getSessionKey());

        // 검색 버튼 클릭
        ibutton_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_search.setVisibility(View.VISIBLE);
                ibutton_closesearch.setVisibility(View.VISIBLE);
            }
        });

        // 검색창 닫기 버튼 클릭
        ibutton_closesearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchword="";
                edit_search.setText("");
                edit_search.setVisibility(View.GONE);
                ibutton_closesearch.setVisibility(View.GONE);

                minPostIdx = 10000000;
                items = new ArrayList<>();

                HttpAsyncTask loadPostTask = new HttpAsyncTask(getActivity(), onLoadPostTaskCompleted);
                String phpFile = "service.php";
                String postParameters = "service=getcommunityposts&searchword=" + searchword + "&limit=" + limit + "&lastpostidx=" + minPostIdx;

                loadPostTask.execute(phpFile, postParameters, util.getSessionKey());
            }
        });

        // 엔터를 누르면 문자열을 받아와 검색
        edit_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER){
                    searchword = edit_search.getText().toString();

                    minPostIdx = 10000000;
                    items = new ArrayList<>();

                    HttpAsyncTask loadPostTask = new HttpAsyncTask(getActivity(), onLoadPostTaskCompleted);
                    String phpFile = "service.php";
                    String postParameters = "service=getcommunityposts&searchword=" + searchword + "&limit=" + limit + "&lastpostidx=" + minPostIdx;

                    loadPostTask.execute(phpFile, postParameters, util.getSessionKey());
                    return true;
                }
                return false;
            }
        });


        // 커뮤니티 우측 상단 메뉴 버튼 클릭
        ibutton_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        // 게시글 추가 버튼 클릭
        ibutton_addpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPostActivity.class);
                startActivityForResult(intent, 1);
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
            }
        });

        // 스크롤바 움직일때
        ScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                // 스크롤이 끝에 닿고, 마지막 받아온 항목이 limit 일때만 로드
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() && countOfLastLoad == limit) {
                    progressBar.setVisibility(View.VISIBLE);

                    HttpAsyncTask loadPostTask = new HttpAsyncTask(getActivity(), onLoadPostTaskCompleted);
                    String phpFile = "service.php";
                    String postParameters = "service=getcommunityposts&searchword=" + searchword + "&limit=" + limit + "&lastpostidx=" + minPostIdx;

                    loadPostTask.execute(phpFile, postParameters, util.getSessionKey());
                }
            }
        });

        // 새로고침
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                minPostIdx = 10000000;
                items = new ArrayList<>();

                HttpAsyncTask loadPostTask = new HttpAsyncTask(getActivity(), onLoadPostTaskCompleted);
                String phpFile = "service.php";
                String postParameters = "service=getcommunityposts&searchword=" + searchword + "&limit=" + limit + "&lastpostidx=" + minPostIdx;

                loadPostTask.execute(phpFile, postParameters, util.getSessionKey());
            }
        });

        return view;
    }


    // 팝업 메뉴 표시
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view, Gravity.END,0,R.style.MyPopupMenu);
        popupMenu.inflate(R.menu.community_menu);

        // 옵션 처리
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_bookmark:
                        getBookmark();
                        return true;
                    case R.id.menu_writtenpost:
                        getWrittenPost();
                        return true;
                    case R.id.menu_writtencomment:
                        getWrittenComment();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    // 북마크한 게시글 불러오기
    private void getBookmark() {
        Intent intent = new Intent(getActivity(), BookmarkPostActivity.class);
        startActivityForResult(intent, 1);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }

    // 작성한 게시글 불러오기
    private void getWrittenPost() {
        Intent intent = new Intent(getActivity(), MyPostActivity.class);
        startActivityForResult(intent, 1);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }

    // 작성한 댓글이 포함된 게시글 불러오기
    private void getWrittenComment() {
        Intent intent = new Intent(getActivity(), MyCommentActivity.class);
        startActivityForResult(intent, 1);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }


    // AddPostActivity에서 반환된 결과 처리
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra("result");
            if (result.equals("success")) {
                // 게시글 목록 다시 불러오기
                getActivity().recreate();
            }
        }
    }
}