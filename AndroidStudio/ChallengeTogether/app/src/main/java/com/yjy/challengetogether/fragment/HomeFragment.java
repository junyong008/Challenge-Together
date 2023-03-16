package com.yjy.challengetogether.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.activity.PostroomActivity;
import com.yjy.challengetogether.adapter.HomeFragmentRvAdapter;
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

public class HomeFragment extends Fragment {

    private View view;
    private String TAG  = "HOME 프레그먼트";

    private ImageButton ibutton_addchallenge;
    private RecyclerView recyclerView_mychallenges;
    private LinearLayoutManager llm;
    private List<HomeItem> items;
    private Util util;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle saveInstanceState) {
        Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_home, container, false);

        util = new Util(requireActivity());

        ibutton_addchallenge = view.findViewById(R.id.ibutton_addchallenge);
        recyclerView_mychallenges = view.findViewById(R.id.recyclerView_mychallenges);
        llm = new LinearLayoutManager(getActivity());












        OnTaskCompleted onLoadRoomTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                Boolean isJSON = util.isJson(result);

                if (isJSON) {

                    try {
                        JSONArray jsonArray = new JSONArray(result);

                        // items 이란 배열을 만들고, json 안의 내용을 HomeItem 클래스 객체 형태로 item에 저장후 이걸 items 배열에 주르륵 저장
                        items = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            HomeItem item = new HomeItem();

                            item.setTitle(obj.getString("TITLE"));
                            item.setContent(obj.getString("CONTENT"));

                            items.add(item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    recyclerView_mychallenges.setHasFixedSize(true);
                    recyclerView_mychallenges.setLayoutManager(llm);

                    //SnapHelper snapHelper = new PagerSnapHelper();
                    //snapHelper.attachToRecyclerView(rv);

                    HomeFragmentRvAdapter adapter = new HomeFragmentRvAdapter(getActivity().getApplication(), items);
                    recyclerView_mychallenges.setAdapter(adapter);

                } else if (result.indexOf("ROOM NOT FOUND") != -1) {
                    StyleableToast.makeText(getActivity(), result, R.style.errorToast).show();
                } else {
                    StyleableToast.makeText(getActivity(), result, R.style.errorToast).show();
                    return;
                }
            }
        };

        HttpAsyncTask loadRoomTask = new HttpAsyncTask(getActivity(), onLoadRoomTaskCompleted);
        String phpFile = "service.php";
        String postParameters = "service=loadroom&roomtype=Participated";

        loadRoomTask.execute(phpFile, postParameters, util.getSessionKey());
        
        // 도전 추가 버튼 클릭
        ibutton_addchallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Count", "ADD BUTTON PRESSED");
                Intent intent = new Intent(getActivity(), PostroomActivity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
            }

        });

        return view;
    }


}