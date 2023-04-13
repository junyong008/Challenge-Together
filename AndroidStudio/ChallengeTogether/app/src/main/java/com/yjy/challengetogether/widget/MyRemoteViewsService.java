package com.yjy.challengetogether.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.yjy.challengetogether.etc.HomeItem;
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
            JSONArray jsonArray = new JSONArray(result);
            // items 이란 배열을 만들고, json 안의 내용을 HomeItem 클래스 객체 형태로 item에 저장후 이걸 items 배열에 주르륵 저장
            items = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                HomeItem item = new HomeItem();

                // 완료된 챌린지는 추가하지 않음. 서버에서 걸러주지만 사용자가 접속해서 서버와 동기화하지 않았을때 스토리지에 저장된 도전이 완료되면 바로 제거해서 보여주기 위함.
                if (!obj.getString("RECENTSTARTTIME").equals("0000-00-00 00:00:00")) {
                    int dayOfCurrentTime = Integer.parseInt(util.DiffWithLocalTime(obj.getString("RECENTSTARTTIME"), "DAY"));
                    int endTime = obj.getInt("ENDTIME");
                    if (dayOfCurrentTime >= endTime) {
                        continue;
                    }
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