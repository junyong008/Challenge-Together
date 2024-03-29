package com.yjy.challengetogether.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.yjy.challengetogether.R;
import com.yjy.challengetogether.activity.AddRoomActivity;
import com.yjy.challengetogether.activity.CompleteChallengeListActivity;
import com.yjy.challengetogether.activity.NotificationRecordActivity;
import com.yjy.challengetogether.activity.RecordActivity;
import com.yjy.challengetogether.adapter.HomeFragmentRvAdapter;
import com.yjy.challengetogether.etc.HomeItem;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.Const;
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


    private ImageView imageView_grade;
    private ImageButton ibutton_notification, ibutton_moreinfo;
    private TextView textView_nickname;
    private TextView textView_record;
    private TextView textView_none;
    private TextView textView_completechallenges;
    private RecyclerView recyclerView_mychallenges;
    private HomeFragmentRvAdapter adapter;
    private LinearLayoutManager llm;
    private List<HomeItem> items;
    private ImageButton ibutton_addchallenge;
    private Util util;
    private Handler mHandler;
    private String recentCompleteChallengeTitle = "";
    private int recordColor;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.stopUpdatingTime();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle saveInstanceState) {
        Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_home, container, false);

        imageView_grade = view.findViewById(R.id.imageView_grade);
        ibutton_notification = view.findViewById(R.id.ibutton_notification);
        ibutton_moreinfo = view.findViewById(R.id.ibutton_moreinfo);
        textView_nickname = view.findViewById(R.id.textView_nickname);
        textView_record = view.findViewById(R.id.textView_record);
        recordColor = ContextCompat.getColor(getContext(), R.color.black_darkmode);
        textView_completechallenges = view.findViewById(R.id.textView_completechallenges);
        textView_completechallenges.setPaintFlags(textView_completechallenges.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView_none = view.findViewById(R.id.textView_none);

        // 알림 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] permissions = new String[] {Manifest.permission.POST_NOTIFICATIONS};


            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {
                    StyleableToast.makeText(getActivity().getApplicationContext(), "알림 표시 권한이 거부되었습니다.", R.style.errorToast).show();
                }
            };

            TedPermission.create()
                    .setPermissionListener(permissionlistener)
                    .setPermissions(Manifest.permission.POST_NOTIFICATIONS)
                    .check();
        }

        // FCM 토큰값을 받아와 DB에 업데이트
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String fcmToken = task.getResult();
                    util.registerTokenToServer(fcmToken);
                } else {
                    StyleableToast.makeText(getActivity().getApplicationContext(), "Fail to get FCM Token", R.style.errorToast).show();
                }
            }
        });

        util = new Util(requireActivity());

        // 위젯 최신화
        // 추가로 서버와 동기화가 필요한 부분 : 방생성(생성시 HomeFragment로 이동하므로 생략), 챌린지포기(동일이유 생략), 챌린지리셋, 챌린지종료, 챌린지시작-방장, 챌린지시작감지-개인
        // 왜 아무 서버와 변동 없는 HomeFragment에 접속하는것 만으로 서버에 동기화 해줘야하는가?
        // -> 만약 참가한 챌린지의 방장이 사용자들이 어플에 접속하지 않았을때 시작을 하면 이는 FCM을 통해 전송되고 사용자들을 이를 받아 서버와 동기화하고 푸시를 수신받는다.
        //    하지만 이때 모종의 이유로 서버와 접속이 원활치 않는 사용자는 서버와 동기화가 되지않는다. 이렇게 되면 위의 5가지 행동을 하지 않으면 위젯의 정보가 누락된다. 이를 방지
        util.saveOngoingChallenges();

        util.checkAppUpdate();

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

                        // 전달받은 JSON에서 userInfo 부분만 추출 ( 열람안한 알림 갯수, 이름, 최대기록 )
                        JSONObject obj1 = new JSONObject(jsonObject.getString("userInfo"));
                        int unviewedNotificationCount = obj1.getInt("UNVIEWEDNOTICOUNT");
                        String userName = obj1.getString("NAME");
                        long userBest = obj1.getLong("BESTTIME");
                        textView_nickname.setText(userName);
                        textView_record.setText(util.secondsToDHMS(userBest, "DHMS"));

                        // 확인 안한 알림이 있으면 알림아이콘 변경
                        if (unviewedNotificationCount > 0) {
                            ibutton_notification.setImageResource(R.drawable.ic_notification_new);
                        } else {
                            ibutton_notification.setImageResource(R.drawable.ic_notification);
                        }

                        if (userBest >= Const.MASTER_SECONDS) {
                            imageView_grade.setImageResource(R.drawable.ic_master);
                        } else if (userBest >= Const.DIAMOND_SECONDS) {
                            imageView_grade.setImageResource(R.drawable.ic_diamond);
                        } else if (userBest >= Const.PLATINUM_SECONDS) {
                            imageView_grade.setImageResource(R.drawable.ic_platinum);
                        } else if (userBest >= Const.GOLD_SECONDS) {
                            imageView_grade.setImageResource(R.drawable.ic_gold);
                        } else if (userBest >= Const.SILVER_SECONDS) {
                            imageView_grade.setImageResource(R.drawable.ic_silver);
                        } else {
                            imageView_grade.setImageResource(R.drawable.ic_bronze);
                        }

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


                            // 완료된 챌린지는 추가하지 않음.
                            // ISCOMPLETE로 서버에서 필터링해 보내줄 수 있는데 어플에서 이걸 계산해서 필터링하는 이유?
                            // --> HomeFragment정보를 가져올때, 모든 방을 조회하고 방이 있으면 그때 완료된 챌린지를 계산해 검사하고 갱신하기에 과거에 갱신된 완료된 챌린지는 필터링 되지만
                            // 방을 조회하고 나서 갱신된 챌린지는 필터링이 안되므로 어플에서 그냥 필터링 하도록 설정. 또한 서버에 부담을 줄이는것이 우선이기에 어플에서 계산 수행하는것이 이득.
                            if (!obj2.getString("RECENTSTARTTIME").equals("0000-00-00 00:00:00")) {
                                int dayOfCurrentTime = Integer.parseInt(util.DiffWithLocalTime(obj2.getString("RECENTSTARTTIME"), "DAY"));
                                int endTime = obj2.getInt("ENDTIME");
                                if (dayOfCurrentTime >= endTime) {
                                    continue;
                                }
                            }


                            HomeItem item = new HomeItem();

                            item.setRoomidx(obj2.getString("ROOM_IDX"));
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
                            updateTextViewTime(oldestItem.getRecentStartTime(), userBest);
                        }



                        // 전달받은 JSON에서 completeRoomList 부분만 추출 ( 사용자가 처음 확인한 완료된 챌린지들 제목 )
                        recentCompleteChallengeTitle = "";
                        JSONArray jsonArray3 = new JSONArray(jsonObject.getString("completeRoomList"));
                        for (int i = 0; i < jsonArray3.length(); i++) {
                            JSONObject obj3 = jsonArray3.getJSONObject(i);

                            if (TextUtils.isEmpty(recentCompleteChallengeTitle)) {
                                recentCompleteChallengeTitle = recentCompleteChallengeTitle + "\"" + obj3.getString("TITLE") + "\"";
                            } else {
                                recentCompleteChallengeTitle = recentCompleteChallengeTitle + ", \"" + obj3.getString("TITLE") + "\"";
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    recyclerView_mychallenges.setHasFixedSize(false);
                    recyclerView_mychallenges.setLayoutManager(llm);

                    //SnapHelper snapHelper = new PagerSnapHelper();
                    //snapHelper.attachToRecyclerView(rv);

                    adapter = new HomeFragmentRvAdapter(getActivity().getApplication(), items);
                    recyclerView_mychallenges.setAdapter(adapter);

                    // 만약 최근에 완료한 챌린지 (유저가 아직 확인을 안한)가 있으면 다이얼로그로 축하메시지와 어디서 확인 가능한지 안내
                    if (!TextUtils.isEmpty(recentCompleteChallengeTitle)) {
                        util.showCustomDialog(new Util.OnConfirmListener() {
                            @Override
                            public void onConfirm(boolean isConfirmed, String msg) {
                                // 확인 버튼 누르면 성공한 챌린지로 이동하도록 유도
                                Intent intent = new Intent(getActivity(), CompleteChallengeListActivity.class);
                                startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                            }
                        }, recentCompleteChallengeTitle  + "\n챌린지를 성공하셨습니다!", "congrats");
                    }

                    // 챌린지가 존재하면 안내메시지 가리기
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

        HttpAsyncTask loadRoomTask = new HttpAsyncTask(getActivity(), onLoadRoomTaskCompleted);
        String phpFile = "service.php";
        String postParameters = "service=gethomefraginfos";

        loadRoomTask.execute(phpFile, postParameters, util.getSessionKey());


        // 알림 정보 버튼 클릭
        ibutton_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ibutton_notification.setImageResource(R.drawable.ic_notification);

                Intent intent = new Intent(getActivity(), NotificationRecordActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
            }
        });

        // 유저 정보 버튼 클릭
        ibutton_moreinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecordActivity.class);
                startActivity(intent);
            }
        });

        // 성공한 챌린지 클릭
        textView_completechallenges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CompleteChallengeListActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
            }
        });


        // 도전 추가 버튼 클릭
        ibutton_addchallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 다이얼로그 띄워서 [새 챌린지 / 자유 챌린지] 선택 기능 제공
                util.showCustomDialog(new Util.OnConfirmListener() {
                    @Override
                    public void onConfirm(boolean isNewChallenge, String msg) {
                        Intent intent = new Intent(getActivity(), AddRoomActivity.class);
                        intent.putExtra("isNew", isNewChallenge);
                        startActivity(intent);
                    }
                }, "", "chooseChallenge");
            }

        });

        return view;
    }

    private void updateTextViewTime(String oldestDate, long userBest) {
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
            textView_record.setText(util.secondsToDHMS(diffSeconds, "DHMS"));
        } else {
            textView_record.setTextColor(recordColor);
            textView_record.setText(util.secondsToDHMS(userBest, "DHMS"));
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

}