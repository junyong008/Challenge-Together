package com.yjy.challengetogether.adapter;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.HomeItem;

import java.util.List;

public class MyRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    //context 설정하기
    private Context context = null;
    private List<HomeItem> items;


    public MyRemoteViewsFactory(Context context, List<HomeItem> items) {
        this.context = context;
        this.items = items;
    }

    //실행 최초로 호출되는 함수
    @Override
    public void onCreate() {
    }

    //항목 추가 및 제거 등 데이터 변경이 발생했을 때 호출되는 함수
    //브로드캐스트 리시버에서 notifyAppWidgetViewDataChanged()가 호출 될 때 자동 호출
    @Override
    public void onDataSetChanged() {
    }

    //마지막에 호출되는 함수
    @Override
    public void onDestroy() {

    }

    // 항목 개수를 반환하는 함수
    @Override
    public int getCount() {
        return this.items.size();
    }

    //각 항목을 구현하기 위해 호출, 매개변수 값을 참조하여 각 항목을 구성하기위한 로직이 담긴다.
    // 항목 선택 이벤트 발생 시 인텐트에 담겨야 할 항목 데이터를 추가해주어야 하는 함수
    @Override
    public RemoteViews getViewAt(int position) {
        final HomeItem item = items.get(position);

        RemoteViews listviewWidget = new RemoteViews(context.getPackageName(), R.layout.item_widgetlv);

        // 아이콘 설정
        int drawableId = context.getResources().getIdentifier(item.getIcon(), "drawable", context.getPackageName());
        listviewWidget.setImageViewResource(R.id.imageView_icon, drawableId);

        // 제목 설정
        listviewWidget.setTextViewText(R.id.textView_title, item.getTitle());

        // 시간 설정 (MyRemoteViewsService에서 로컬시간과 차이 계산하여 D일 H시간 형식으로 RecentStartTime에 넣어서 보내줌)
        listviewWidget.setTextViewText(R.id.textView_time, item.getRecentStartTime());

        return listviewWidget;
    }

    //로딩 뷰를 표현하기 위해 호출, 없으면 null
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    //항목의 타입 갯수를 판단하기 위해 호출, 모든 항목이 같은 뷰 타입이라면 1을 반환하면 된다.
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    //각 항목의 식별자 값을 얻기 위해 호출
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 같은 ID가 항상 같은 개체를 참조하면 true 반환하는 함수
    @Override
    public boolean hasStableIds() {
        return false;
    }
}
