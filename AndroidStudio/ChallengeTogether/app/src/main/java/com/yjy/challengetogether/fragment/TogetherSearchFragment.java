package com.yjy.challengetogether.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.adapter.TogetherSearchFragmentRvAdapter;
import com.yjy.challengetogether.etc.HomeItem;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TogetherSearchFragment extends Fragment {

    private View view;
    private String TAG  = "TOGETHER SEARCH 프레그먼트";
    private ImageButton ibutton_back, ibutton_search;
    private ImageView imageView_none;
    private TextView textView_title, textView_none;
    private EditText edit_search;
    private String categoryicon, categorytitle;
    private SwipeRefreshLayout refresh;
    private RecyclerView recyclerView_room;
    private TogetherSearchFragmentRvAdapter adapter;
    private LinearLayoutManager llm;
    private ProgressBar progressBar;
    private NestedScrollView ScrollView;
    private List<HomeItem> items;
    private Util util;
    private int limit, minRoomIdx, countOfLastLoad;
    private String searchword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle saveInstanceState) {
        Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_togethersearch, container, false);
        util = new Util(requireActivity());

        ibutton_back = view.findViewById(R.id.ibutton_back);
        textView_title = view.findViewById(R.id.textView_title);
        ibutton_search = view.findViewById(R.id.ibutton_search);
        edit_search = view.findViewById(R.id.edit_search);
        imageView_none = view.findViewById(R.id.imageView_none);
        textView_none = view.findViewById(R.id.textView_none);
        refresh = view.findViewById(R.id.refresh);
        progressBar = view.findViewById(R.id.progressBar);
        ScrollView = view.findViewById(R.id.ScrollView);

        limit = 10; // 한번에 보여지는 항목 개수
        minRoomIdx = 10000000; // 보여준 항목들의 가장 마지막 ROOM_IDX를 저장했다가 유저가 스크롤을 계속 넘기면 그 이후 항목 10개를 추가로 받아오기 위함
        searchword = "";
        countOfLastLoad = 0; // 만약 마지막 받아온 항목이 limit 일때만 추가로 로드작업을 하기 위함.
        items = new ArrayList<>(); // 외부에 선언하여 계속해서 서버 통신 없이 항목을 적재받기 위함.


        // 뒤로가기 버튼 클릭 (TogetherFragment로 전환 : 카테고리 선택 영역)
        ibutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.remove(TogetherSearchFragment.this);
                transaction.add(R.id.fragment_mainpage, new TogetherFragment());
                transaction.commit();
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            categoryicon = bundle.getString("categoryicon"); // 저장한 데이터 꺼내 사용
            categorytitle = bundle.getString("categorytitle"); // 저장한 데이터 꺼내 사용
            textView_title.setText(categorytitle);
        }


        recyclerView_room = view.findViewById(R.id.recyclerView_room);
        llm = new LinearLayoutManager(getActivity());

        OnTaskCompleted onLoadRoomTaskCompleted = new OnTaskCompleted() {
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

                            HomeItem item = new HomeItem();

                            item.setRoomidx(obj.getString("ROOM_IDX"));
                            item.setIcon(obj.getString("CHALLENGETYPE"));
                            item.setTitle(obj.getString("TITLE"));
                            item.setContent(obj.getString("CONTENT"));
                            item.setEndTime(obj.getLong("ENDTIME"));
                            item.setCurrentUserNum(obj.getString("CURRENTUSERNUM"));
                            item.setMaxUserNum(obj.getString("MAXUSERNUM"));
                            item.setRoomPasswd(obj.getString("PASSWD"));

                            countOfLastLoad++;
                            if (obj.getInt("ROOM_IDX") < minRoomIdx) {
                                minRoomIdx = obj.getInt("ROOM_IDX");
                            }

                            items.add(item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    recyclerView_room.setHasFixedSize(true);
                    recyclerView_room.setLayoutManager(llm);

                    //SnapHelper snapHelper = new PagerSnapHelper();
                    //snapHelper.attachToRecyclerView(rv);

                    adapter = new TogetherSearchFragmentRvAdapter(getActivity().getApplication(), TogetherSearchFragment.this, items, util);
                    recyclerView_room.setAdapter(adapter);

                    progressBar.setVisibility(View.GONE);
                    refresh.setRefreshing(false);

                    // 방이 존재하면 안내메시지 가리기
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

        HttpAsyncTask loadRoomTask = new HttpAsyncTask(getActivity(), onLoadRoomTaskCompleted);
        String phpFile = "service.php";
        String postParameters = "service=getreadyrooms&roomtype=" + categoryicon + "&searchword=&limit=" + limit + "&lastroomidx=" + minRoomIdx;

        loadRoomTask.execute(phpFile, postParameters, util.getSessionKey());

        // 스크롤바 움직일때
        ScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                // 스크롤이 끝에 닿고, 마지막 받아온 항목이 limit 일때만 로드
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() && countOfLastLoad == limit) {
                    progressBar.setVisibility(View.VISIBLE);

                    HttpAsyncTask loadRoomTask = new HttpAsyncTask(getActivity(), onLoadRoomTaskCompleted);
                    String phpFile = "service.php";
                    String postParameters = "service=getreadyrooms&roomtype=" + categoryicon + "&searchword=" + searchword + "&limit=" + limit + "&lastroomidx=" + minRoomIdx;

                    loadRoomTask.execute(phpFile, postParameters, util.getSessionKey());
                }
            }
        });

        // 새로고침
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                minRoomIdx = 10000000;
                items = new ArrayList<>();

                HttpAsyncTask loadRoomTask = new HttpAsyncTask(getActivity(), onLoadRoomTaskCompleted);
                String phpFile = "service.php";
                String postParameters = "service=getreadyrooms&roomtype=" + categoryicon + "&searchword=" + searchword + "&limit=" + limit + "&lastroomidx=" + minRoomIdx;

                loadRoomTask.execute(phpFile, postParameters, util.getSessionKey());
            }
        });

        // 검색버튼 클릭 : edit_search의 내용을 받아와서 DB에서 다시 조건 검색.
        ibutton_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchword = edit_search.getText().toString().trim();
                minRoomIdx = 10000000;
                items = new ArrayList<>();

                HttpAsyncTask loadRoomTask = new HttpAsyncTask(getActivity(), onLoadRoomTaskCompleted);
                String phpFile = "service.php";
                String postParameters = "service=getreadyrooms&roomtype=" + categoryicon + "&searchword=" + searchword + "&limit=" + limit + "&lastroomidx=" + minRoomIdx;

                loadRoomTask.execute(phpFile, postParameters, util.getSessionKey());
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra("result");

            // 대기방 참여자 수가 변경됐을 경우
            if (result.equals("changed")) {

                // 변경된 항목의 위치가 정상적으로 넘어오면 변경
                int position = data.getIntExtra("position", -1);
                if (position != -1) {

                    HomeItem changedItem = items.get(position);

                    // 변경될 수 있는 항목들 받아와서 changedItem 수정
                    changedItem.setCurrentUserNum(data.getStringExtra("changedCurrentUserNum"));

                    // 리사이클러뷰에 해당 아이템이 수정됐음을 알려서 최신화
                    items.set(position, changedItem);
                    adapter.notifyItemChanged(position);
                }

                // 대기방이 이미 시작했거나 삭제됐을 경우
            } else if (result.equals("deleted")) {

                int position = data.getIntExtra("position", -1);
                if (position != -1) {
                    items.remove(position);
                    adapter.notifyItemRemoved(position);

                    // 방이 존재하면 안내메시지 가리기
                    if(items.size() > 0) {
                        imageView_none.setVisibility(View.GONE);
                        textView_none.setVisibility(View.GONE);
                    } else {
                        imageView_none.setVisibility(View.VISIBLE);
                        textView_none.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }
}