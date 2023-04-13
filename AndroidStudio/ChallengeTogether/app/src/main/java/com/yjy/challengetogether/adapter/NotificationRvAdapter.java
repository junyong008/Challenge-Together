package com.yjy.challengetogether.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.activity.PostActivity;
import com.yjy.challengetogether.activity.ReadyRoomActivity;
import com.yjy.challengetogether.activity.StartRoomActivity;
import com.yjy.challengetogether.etc.NotificationItem;
import com.yjy.challengetogether.util.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotificationRvAdapter extends RecyclerView.Adapter<NotificationRvAdapter.CustomViewHolder> {

    private Context context;
    private List<NotificationItem> items;
    private Util util;

    public NotificationRvAdapter(Context context, List<NotificationItem> items, Util util) {
        this.context = context;
        this.items = items;
        this.util = util;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificationrv, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final NotificationItem item = items.get(position);

        // 아이콘 종류는 챌린지 관련, 커뮤니티 관련 두개로 단순화
        if (item.getType().indexOf("community") != -1) {
            holder.imageView.setImageResource(R.drawable.ic_alarm_community);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_alarm_challenge);
        }

        holder.textView_title.setText(item.getTitle());
        holder.textView_content.setText(item.getContent());

        String newDateString;
        // yyyy-MM-dd HH:mm:ss 형식을 MM/dd HH:mm로 변환하여 표시
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // 수정된 기록이 존재하면 수정된 날짜를 기준으로 표기
            Date date = dateFormat.parse(item.getCreatedate());

            Calendar calendar = Calendar.getInstance();
            long now = calendar.getTimeInMillis();
            long diff = now - date.getTime();

            if (diff >= 60 * 60 * 1000) {
                // 1시간 이상 차이나면 "MM/dd HH:mm" 형식으로 표기
                SimpleDateFormat newDateFormat = new SimpleDateFormat("MM/dd HH:mm");
                newDateString = newDateFormat.format(date);
            } else {
                // 1시간 이내 차이나면 "mm분 전" 형식으로 표기
                long minutes = diff / (60 * 1000);
                newDateString = minutes + "분 전";
            }

            holder.textView_createdate.setText(newDateString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CardView cardView_item = holder.itemView.findViewById(R.id.cardView_item);
        cardView_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (item.getType().indexOf("community") != -1) {
                    Intent intent = new Intent(holder.itemView.getContext(), PostActivity.class);
                    intent.putExtra("postidx", item.getLinkidx());
                    ((Activity) context).startActivity(intent);
                } else if (item.getType().indexOf("successchallenge") != -1 || item.getType().indexOf("participantreset") != -1 || item.getType().indexOf("readyroomstart") != -1) {
                    Intent intent = new Intent(holder.itemView.getContext(), StartRoomActivity.class);
                    intent.putExtra("roomidx", item.getLinkidx());
                    ((Activity) context).startActivity(intent);
                } else if (item.getType().indexOf("newparticipate") != -1) {
                    Intent intent = new Intent(holder.itemView.getContext(), ReadyRoomActivity.class);
                    intent.putExtra("roomidx", item.getLinkidx());
                    ((Activity) context).startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView_title, textView_content, textView_createdate;

        public CustomViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView_title = itemView.findViewById(R.id.textView_title);
            textView_content = itemView.findViewById(R.id.textView_content);
            textView_createdate = itemView.findViewById(R.id.textView_createdate);
        }
    }
}


