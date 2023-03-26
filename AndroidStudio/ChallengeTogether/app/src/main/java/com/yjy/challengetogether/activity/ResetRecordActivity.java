package com.yjy.challengetogether.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.adapter.ResetRecordRvAdapter;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.etc.ResetItem;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class ResetRecordActivity extends AppCompatActivity {

    private com.yjy.challengetogether.util.Util util = new Util(ResetRecordActivity.this);
    private ImageButton ibutton_back;
    private RecyclerView recyclerView_resetlist;
    private ResetRecordRvAdapter adapter;
    private LinearLayoutManager llm;
    private List<ResetItem> items;
    private TextView textView_none;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetrecord);

        Intent intent = getIntent();
        String roomidx = intent.getStringExtra("roomidx");


        textView_none = findViewById(R.id.textView_none);
        ibutton_back = findViewById(R.id.ibutton_back);

        // 뒤로가기 버튼 클릭
        ibutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        recyclerView_resetlist = findViewById(R.id.recyclerView_resetlist);
        llm = new LinearLayoutManager(ResetRecordActivity.this);

        OnTaskCompleted onLoadResetListTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                Boolean isJSON = util.isJson(result);

                if (isJSON) {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        items = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            ResetItem item = new ResetItem();

                            item.setResetdate(obj.getString("RESETDATE"));
                            item.setAbstinencetime(obj.getString("ABSTINENCETIME")); // 타이머로 실시간 업데이트 위함
                            item.setResetmemo(obj.getString("RESETMEMO"));

                            items.add(item);
                        }

                        // 리셋 기록이 존재하면 안내메시지 가리기
                        if(items.size() > 0) {
                            textView_none.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    recyclerView_resetlist.setHasFixedSize(true);
                    recyclerView_resetlist.setLayoutManager(llm);

                    adapter = new ResetRecordRvAdapter(ResetRecordActivity.this, items);
                    recyclerView_resetlist.setAdapter(adapter);

                } else if (result.indexOf("NO SESSION") != -1) {
                    StyleableToast.makeText(ResetRecordActivity.this, "세션이 만료되었습니다.\n다시 로그인해주세요.", R.style.errorToast).show();

                    // 로그인 액티비티 실행 후 그 외 모두 삭제
                    Intent intent = new Intent(ResetRecordActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    StyleableToast.makeText(ResetRecordActivity.this, result, R.style.errorToast).show();
                    return;
                }
            }
        };

        HttpAsyncTask loadResetListTask = new HttpAsyncTask(ResetRecordActivity.this, onLoadResetListTaskCompleted);
        String phpFile = "service.php";
        String postParameters = "service=getresetrecord&roomidx=" + roomidx;

        loadResetListTask.execute(phpFile, postParameters, util.getSessionKey());
    }
}