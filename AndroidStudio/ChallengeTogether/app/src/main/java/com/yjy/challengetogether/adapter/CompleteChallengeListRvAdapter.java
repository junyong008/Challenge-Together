package com.yjy.challengetogether.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.activity.StartRoomActivity;
import com.yjy.challengetogether.etc.HomeItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CompleteChallengeListRvAdapter extends RecyclerView.Adapter<CompleteChallengeListRvAdapter.CustomViewHolder> {

    private Context context;
    private List<HomeItem> items;
    private Handler mHandler;

    public CompleteChallengeListRvAdapter(Context context, List<HomeItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_completelistsrv, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final HomeItem item = items.get(position);

        // 아이콘 설정
        int drawableId = context.getResources().getIdentifier(item.getIcon(), "drawable", context.getPackageName());
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        holder.imageView_icon.setImageDrawable(drawable);

        holder.textView_title.setText(item.getTitle());
        holder.textView_content.setText(item.getContent());

        // 최근 리셋 시간에 endtime을 더해서 챌린지 종료 날짜 산출
        try {
            String completeDate = addDaysToDate(item.getRecentStartTime(), item.getEndTime());
            holder.textView_time.setText("성공 날짜 :   " + completeDate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CardView cardView_item = holder.itemView.findViewById(R.id.cardView_item);
        cardView_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CardView가 클릭되었을 때 실행할 코드
                String roomIdx = item.getRoomidx();
                // 시작방
                Intent intent = new Intent(holder.itemView.getContext(), StartRoomActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("roomidx", roomIdx);
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    public static String addDaysToDate(String dateStr, long daysToAdd) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, (int) daysToAdd);
        date = calendar.getTime();
        return sdf.format(date);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView_icon;
        TextView textView_title;
        TextView textView_content;
        TextView textView_time;

        public CustomViewHolder(View itemView) {
            super(itemView);
            imageView_icon = itemView.findViewById(R.id.imageView_icon);
            textView_title = itemView.findViewById(R.id.textView_title);
            textView_content = itemView.findViewById(R.id.textView_content);
            textView_time = itemView.findViewById(R.id.textView_time);
        }
    }
}


