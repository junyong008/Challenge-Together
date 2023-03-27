package com.yjy.challengetogether.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.adapter.TogetherFragmentRvAdapter;
import com.yjy.challengetogether.etc.IconItem;

import java.util.ArrayList;
import java.util.List;



public class TogetherFragment extends Fragment {

    private View view;
    private String TAG  = "TOGETHER 프레그먼트";

    private List<IconItem> items;
    private RecyclerView recyclerView_category;
    private TogetherFragmentRvAdapter adapter;
    private GridLayoutManager glm;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle saveInstanceState) {
        Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_together, container, false);

        // 카테고리를 클릭하면, 해당 카테고리 정보를 bundle 객체로 담아 방 리스트 Fragment (TogetherSearchFragment)로 전환
        /*
        button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("key", "전달하고자 하는 데이터");

                TogetherSearchFragment TogetherSearchFragment = new TogetherSearchFragment();
                TogetherSearchFragment.setArguments(bundle); // 생성한 Bundle 객체를 전달 받는 Fragment에 설정

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.remove(TogetherFragment.this);
                transaction.add(R.id.fragment_mainpage, TogetherSearchFragment);
                transaction.commit();
            }
        });
        */


        recyclerView_category = view.findViewById(R.id.recyclerView_category);
        glm = new GridLayoutManager(getActivity(), 2);


        items = new ArrayList<>();

        IconItem item = new IconItem();
        item.setIcon("ic_all");
        item.setTitle("모든 도전");
        items.add(item);

        item = new IconItem();
        item.setIcon("ic_smoke");
        item.setTitle("흡연 끊기");
        items.add(item);

        item = new IconItem();
        item.setIcon("ic_drink");
        item.setTitle("음주 끊기");
        items.add(item);

        item = new IconItem();
        item.setIcon("ic_porno");
        item.setTitle("음란물 끊기");
        items.add(item);

        item = new IconItem();
        item.setIcon("ic_casino");
        item.setTitle("도박 끊기");
        items.add(item);

        item = new IconItem();
        item.setIcon("ic_drug");
        item.setTitle("약물 끊기");
        items.add(item);

        item = new IconItem();
        item.setIcon("ic_game");
        item.setTitle("게임 끊기");
        items.add(item);

        item = new IconItem();
        item.setIcon("ic_youtube");
        item.setTitle("유튜브 끊기");
        items.add(item);

        item = new IconItem();
        item.setIcon("ic_sns");
        item.setTitle("SNS 끊기");
        items.add(item);

        item = new IconItem();
        item.setIcon("ic_bakery");
        item.setTitle("밀가루 끊기");
        items.add(item);

        item = new IconItem();
        item.setIcon("ic_coffee");
        item.setTitle("커피 끊기");
        items.add(item);

        item = new IconItem();
        item.setIcon("ic_fastfood");
        item.setTitle("패스트푸드 끊기");
        items.add(item);

        item = new IconItem();
        item.setIcon("ic_shop");
        item.setTitle("쇼핑 끊기");
        items.add(item);

        item = new IconItem();
        item.setIcon("ic_cuss");
        item.setTitle("욕설 끊기");
        items.add(item);

        item = new IconItem();
        item.setIcon("ic_cell");
        item.setTitle("스마트폰 끊기");
        items.add(item);

        recyclerView_category.setHasFixedSize(true);
        recyclerView_category.setLayoutManager(glm);

        //SnapHelper snapHelper = new PagerSnapHelper();
        //snapHelper.attachToRecyclerView(rv);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        adapter = new TogetherFragmentRvAdapter(getActivity().getApplication(), items, transaction, TogetherFragment.this);
        recyclerView_category.setAdapter(adapter);

        return view;
    }
}