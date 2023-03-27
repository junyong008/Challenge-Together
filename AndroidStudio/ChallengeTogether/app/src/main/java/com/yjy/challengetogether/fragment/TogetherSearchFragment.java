package com.yjy.challengetogether.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.activity.LoginActivity;
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

import io.github.muddz.styleabletoast.StyleableToast;

public class TogetherSearchFragment extends Fragment {

    private View view;
    private String TAG  = "TOGETHER SEARCH 프레그먼트";
    private ImageButton ibutton_back, ibutton_search;
    private TextView textView_title, textView_none;
    private EditText edit_search;
    private String categoryicon, categorytitle;
    private RecyclerView recyclerView_room;
    private TogetherSearchFragmentRvAdapter adapter;
    private LinearLayoutManager llm;
    private List<HomeItem> items;
    private Util util;

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
        textView_none = view.findViewById(R.id.textView_none);

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
                        // 전달받은 JSON에서 roomList 부분만 추출 ( 방 표면에 보여질 정보들 )
                        JSONArray jsonArray = new JSONArray(result);
                        // items 이란 배열을 만들고, json 안의 내용을 HomeItem 클래스 객체 형태로 item에 저장후 이걸 items 배열에 주르륵 저장
                        items = new ArrayList<>();
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

                            items.add(item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    recyclerView_room.setHasFixedSize(true);
                    recyclerView_room.setLayoutManager(llm);

                    //SnapHelper snapHelper = new PagerSnapHelper();
                    //snapHelper.attachToRecyclerView(rv);

                    adapter = new TogetherSearchFragmentRvAdapter(getActivity().getApplication(), items, util);
                    recyclerView_room.setAdapter(adapter);

                    // 방이 존재하면 안내메시지 가리기
                    if(items.size() > 0) {
                        textView_none.setVisibility(View.GONE);
                    }

                } else if (result.indexOf("NO SESSION") != -1) {
                    StyleableToast.makeText(getActivity(), "세션이 만료되었습니다.\n다시 로그인해주세요.", R.style.errorToast).show();

                    // 로그인 액티비티 실행 후 그 외 모두 삭제
                    Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    StyleableToast.makeText(getActivity(), result, R.style.errorToast).show();
                    return;
                }
            }
        };

        HttpAsyncTask loadRoomTask = new HttpAsyncTask(getActivity(), onLoadRoomTaskCompleted);
        String phpFile = "service.php";
        String postParameters = "service=getreadyrooms&roomtype=" + categoryicon + "&searchword=";

        loadRoomTask.execute(phpFile, postParameters, util.getSessionKey());




        // 검색버튼 클릭 : edit_search의 내용을 받아와서 DB에서 다시 조건 검색.
        ibutton_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchword = edit_search.getText().toString().trim();

                HttpAsyncTask loadRoomTask = new HttpAsyncTask(getActivity(), onLoadRoomTaskCompleted);
                String phpFile = "service.php";
                String postParameters = "service=getreadyrooms&roomtype=" + categoryicon + "&searchword=" + searchword;

                loadRoomTask.execute(phpFile, postParameters, util.getSessionKey());
            }
        });






        return view;
    }
}