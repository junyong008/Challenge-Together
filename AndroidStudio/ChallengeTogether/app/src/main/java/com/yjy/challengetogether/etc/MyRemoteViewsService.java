package com.yjy.challengetogether.etc;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.yjy.challengetogether.adapter.MyRemoteViewsFactory;
import com.yjy.challengetogether.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * RemoteViewsService를 상속받은 개발자 서비스 클래스
 * RemoteViesFactory를 얻을 목적으로 인텐트 발생에 의해 실행됩니다.
 */
public class MyRemoteViewsService extends RemoteViewsService {
    private List<HomeItem> items;
    private com.yjy.challengetogether.util.Util util = new Util(MyRemoteViewsService.this);;

    //필수 오버라이드 함수 : RemoteViewsFactory를 반환한다.
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        String result = intent.getStringExtra("result");

        try {
            JSONObject jsonObject = new JSONObject(result);

            // 전달받은 JSON에서 roomList 부분만 추출 ( 방 표면에 보여질 정보들 )
            JSONArray jsonArray = new JSONArray(jsonObject.getString("roomList"));
            // items 이란 배열을 만들고, json 안의 내용을 HomeItem 클래스 객체 형태로 item에 저장후 이걸 items 배열에 주르륵 저장
            items = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                HomeItem item = new HomeItem();

                // 완료된 챌린지는 추가하지 않음.
                if (!obj.getString("RECENTSTARTTIME").equals("0000-00-00 00:00:00")) {
                    int dayOfCurrentTime = Integer.parseInt(util.DiffWithLocalTime(obj.getString("RECENTSTARTTIME"), "DAY"));
                    int endTime = obj.getInt("ENDTIME");
                    if (dayOfCurrentTime >= endTime) {
                        continue;
                    }
                }

                // 대기중인 챌린지는 추가하지 않음
                if (!obj.getString("ISSTART").equals("1")) {
                    continue;
                }

                item.setIcon(obj.getString("CHALLENGETYPE"));
                item.setTitle(obj.getString("TITLE"));
                item.setRecentStartTime(util.DiffWithLocalTime(obj.getString("RECENTSTARTTIME"), "DH"));

                items.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new MyRemoteViewsFactory(this.getApplicationContext(), items);
    }
}