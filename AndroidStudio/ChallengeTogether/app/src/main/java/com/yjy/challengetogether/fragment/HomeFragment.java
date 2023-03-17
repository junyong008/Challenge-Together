package com.yjy.challengetogether.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.activity.AddRoomActivity;
import com.yjy.challengetogether.activity.RecordActivity;
import com.yjy.challengetogether.adapter.HomeFragmentRvAdapter;
import com.yjy.challengetogether.etc.HomeItem;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.muddz.styleabletoast.StyleableToast;

public class HomeFragment extends Fragment {

    private View view;
    private String TAG  = "HOME 프레그먼트";


    private ImageButton ibutton_moreinfo;
    private TextView textView_nickname;
    private TextView textView_record;
    private RecyclerView recyclerView_mychallenges;
    private HomeFragmentRvAdapter adapter;
    private LinearLayoutManager llm;
    private List<HomeItem> items;
    private ImageButton ibutton_addchallenge;
    private Util util;
    private Handler mHandler;

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopUpdatingTime();
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle saveInstanceState) {
        Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_home, container, false);

        ibutton_moreinfo = view.findViewById(R.id.ibutton_moreinfo);
        textView_nickname = view.findViewById(R.id.textView_nickname);
        textView_record = view.findViewById(R.id.textView_record);


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
                        JSONObject jsonObject = new JSONObject(result);

                        // 전달받은 JSON에서 userInfo 부분만 추출 ( 이름, 최대기록 )
                        JSONObject obj1 = new JSONObject(jsonObject.getString("userInfo"));
                        String userName = obj1.getString("NAME");
                        String userBest = obj1.getString("BESTTIME");
                        textView_nickname.setText(userName);
                        textView_record.setText(secondsToDHMS(Integer.parseInt(userBest)));

                        // 방이 없으면 리턴
                        if (!result.contains("roomList")) {
                            return;
                        }

                        // 전달받은 JSON에서 roomList 부분만 추출 ( 방 표면에 보여질 정보들 )
                        JSONArray jsonArray2 = new JSONArray(jsonObject.getString("roomList"));
                        // items 이란 배열을 만들고, json 안의 내용을 HomeItem 클래스 객체 형태로 item에 저장후 이걸 items 배열에 주르륵 저장
                        items = new ArrayList<>();
                        for (int i = 0; i < jsonArray2.length(); i++) {
                            JSONObject obj2 = jsonArray2.getJSONObject(i);
                            HomeItem item = new HomeItem();

                            item.setIcon(obj2.getString("CHALLENGETYPE"));
                            item.setTitle(obj2.getString("TITLE"));
                            item.setContent(obj2.getString("CONTENT"));
                            item.setRecentStartTime(obj2.getString("RECENTSTARTTIME"));
                            item.setEndTime(obj2.getLong("ENDTIME") * 24 * 60 * 60);
                            item.setCurrentUserNum(obj2.getString("CURRENTUSERNUM"));
                            item.setMaxUserNum(obj2.getString("MAXUSERNUM"));
                            item.setRoomPasswd(obj2.getString("PASSWD"));

                            items.add(item);
                        }

                        // 리스트중 현재 자신의 최고기록을 갱신한게 있는지 확인
                        // RecentStartTime 초기값은 "9999-12-31 23:59:59"으로 설정
                        String oldestRecentStartTime = "9999-12-31 23:59:59";
                        HomeItem oldestItem = null;

                        // items 배열을 반복하면서 가장 과거의 RecentStartTime 값을 가진 항목을 찾음
                        for (HomeItem item : items) {
                            String recentStartTime = item.getRecentStartTime();
                            if (!recentStartTime.equals("0000-00-00 00:00:00") && recentStartTime.compareTo(oldestRecentStartTime) < 0) {
                                oldestRecentStartTime = recentStartTime;
                                oldestItem = item;
                            }
                        }

                        // 가장 과거의 RecentStartTime 값을 가진 항목을 찾았으면, 계속해서 비교하며 현재 항목중 기록을 깨는 항목이 있는지 검사
                        if (oldestItem != null) {
                            updateTextViewTime(oldestItem.getRecentStartTime(), Integer.parseInt(userBest));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    recyclerView_mychallenges.setHasFixedSize(true);
                    recyclerView_mychallenges.setLayoutManager(llm);

                    //SnapHelper snapHelper = new PagerSnapHelper();
                    //snapHelper.attachToRecyclerView(rv);

                    adapter = new HomeFragmentRvAdapter(getActivity().getApplication(), items);
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
        String postParameters = "service=gethomefraginfos";

        loadRoomTask.execute(phpFile, postParameters, util.getSessionKey());



        // 유저 정보 버튼 클릭
        ibutton_moreinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecordActivity.class);
                startActivity(intent);
            }
        });



        // 도전 추가 버튼 클릭
        ibutton_addchallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Count", "ADD BUTTON PRESSED");
                Intent intent = new Intent(getActivity(), AddRoomActivity.class);
                startActivity(intent);
                // requireActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
            }

        });

        return view;
    }

    private void updateTextViewTime(String oldestDate, int userBest) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date inputTimeDate = null;

        try {
            inputTimeDate = sdf.parse(oldestDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long currentTimeMillis = System.currentTimeMillis();
        long inputTimeMillis = inputTimeDate.getTime();

        long diffMillis = currentTimeMillis - inputTimeMillis;
        long diffSeconds = diffMillis / 1000;

        // 만약 현재 도전 중 가장 높은 참은시간이 유저의 최고기록을 넘는순간
        if (diffSeconds >= userBest) {
            textView_record.setTextColor(Color.parseColor("#DC143C"));
            textView_record.setText(secondsToDHMS(diffSeconds));
        } else {
            textView_record.setTextColor(Color.parseColor("#000000"));
            textView_record.setText(secondsToDHMS(userBest));
        }


        // 1초마다 업데이트
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTextViewTime(oldestDate, userBest);
            }
        }, 1000);
    }

    private String secondsToDHMS(long intputSeconds) {
        StringBuilder formattedTime = new StringBuilder();

        long days = intputSeconds / (24 * 60 * 60);
        intputSeconds %= (24 * 60 * 60);

        if (days > 0) {
            formattedTime.append(days).append(" DAYS ");
        }

        long hours = intputSeconds / (60 * 60);
        intputSeconds %= (60 * 60);

        if (hours > 0) {
            formattedTime.append(hours).append(" HRS\n");
        }

        long minutes = intputSeconds / 60;
        intputSeconds %= 60;

        if (minutes > 0) {
            formattedTime.append(minutes).append(" MINS ");
        }


        long seconds = intputSeconds;
        formattedTime.append(seconds).append(" SECS");

        return formattedTime.toString().trim();
    }
}