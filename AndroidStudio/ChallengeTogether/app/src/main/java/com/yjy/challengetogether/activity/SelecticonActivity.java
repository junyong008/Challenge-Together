package com.yjy.challengetogether.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.etc.IconItem;
import com.yjy.challengetogether.R;
import com.yjy.challengetogether.adapter.SelecticonRvAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelecticonActivity extends AppCompatActivity {
    public static final String EXTRA_SELECTED_ITEM1 = "selected_item1";
    public static final String EXTRA_SELECTED_ITEM2 = "selected_item2";
    private ImageButton ibutton_close;
    private RecyclerView recyclerView_challengelist;
    private LinearLayoutManager llm;
    private List<IconItem> items;
    private SelecticonRvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀바 제거
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_selecticon);

        // 다이얼로그 액티비티는 창크기 설정이 어려우니, 디바이스 화면 크기로 비율 설정
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = (int) (displayMetrics.widthPixels * 0.8);
        int height = (int) (displayMetrics.heightPixels * 0.8);
        getWindow().setLayout(width, height);

        ibutton_close = findViewById(R.id.ibutton_close);
        ibutton_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        recyclerView_challengelist = findViewById(R.id.recyclerView_challengelist);
        llm = new LinearLayoutManager(SelecticonActivity.this);

        // 아이콘 항목 입력
        items = new ArrayList<>();

        IconItem item = new IconItem();
        item.setIcon("ic_trop");
        item.setTitle("나의 도전");
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



        recyclerView_challengelist.setHasFixedSize(true);
        recyclerView_challengelist.setLayoutManager(llm);

        //SnapHelper snapHelper = new PagerSnapHelper();
        //snapHelper.attachToRecyclerView(rv);

        SelecticonRvAdapter adapter = new SelecticonRvAdapter(SelecticonActivity.this, items, this);
        recyclerView_challengelist.setAdapter(adapter);
    }
}