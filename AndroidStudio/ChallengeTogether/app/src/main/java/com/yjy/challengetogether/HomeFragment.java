package com.yjy.challengetogether;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private View view;
    private String TAG  = "HOME 프레그먼트";




    private ImageButton home_add;
    private RecyclerView rv;
    private LinearLayoutManager llm;
    private List<HomeItem> items;
    private RvAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle saveInstanceState) {
        Log.i(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_home, container, false);

        home_add = view.findViewById(R.id.home_add);

        rv = view.findViewById(R.id.home_recyclerView);
        llm = new LinearLayoutManager(getActivity());







        String json = "{\"items\":[{\"title\":\"새해 금연\",\"content\":\"새해부터 시작하는 금연 도전!\"},{\"title\":\"테스트 타이틀 22312412412513514134131251234124\",\"content\":\"테스트 컨텐츠 ㅁ나ㅣ루마ㅣㄴ뤼마누리ㅏㅁㄴ룸ㄴㄹ가나다랃라마다ㅏㅁ나람ㄷㄹ\"},{\"title\":\"title3\",\"content\":\"content3\"}]}";

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            // items 이란 배열을 만들고, json 안의 내용을 HomeItem 클래스 객체 형태로 item에 저장후 이걸 items 배열에 주르륵 저장
            items = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                HomeItem item = new HomeItem();
                item.setTitle(obj.getString("title"));
                item.setContent(obj.getString("content"));
                items.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        rv.setHasFixedSize(true);
        rv.setLayoutManager(llm);

        //SnapHelper snapHelper = new PagerSnapHelper();
        //snapHelper.attachToRecyclerView(rv);

        RvAdapter adapter = new RvAdapter(getActivity().getApplication(), items);
        rv.setAdapter(adapter);




        home_add.setOnClickListener(new View.OnClickListener() {
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