package com.yjy.challengetogether.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.RankingItem;
import com.yjy.challengetogether.util.Const;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RankingRvAdapter extends RecyclerView.Adapter<RankingRvAdapter.CustomViewHolder> {

    private Context context;
    private List<RankingItem> items;
    String roomTargetDay;
    private Handler mHandler;

    public RankingRvAdapter(Context context, List<RankingItem> items, String roomTargetDay) {
        this.context = context;
        this.items = items;
        this.roomTargetDay = roomTargetDay;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rankingrv, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final RankingItem item = items.get(position);

        // 1~3위 아이콘 설정
        if (item.getRanking() < 4) {
            String rankIcon;
            if (item.getRanking() == 1) {
                rankIcon = "ic_medalfirst";
            } else if (item.getRanking() == 2){
                rankIcon = "ic_medalsecond";
            } else {
                rankIcon = "ic_medalthird";
            }

            int drawableId = context.getResources().getIdentifier(rankIcon, "drawable", context.getPackageName());
            Drawable drawable = ContextCompat.getDrawable(context, drawableId);
            holder.textView_rank.setBackground(drawable);
            holder.textView_rank.setText("");
        } else {
            holder.textView_rank.setText(String.valueOf(item.getRanking()));
        }

        // 각 사용자 랭킹 표시
        Long userBest = item.getBestTime();
        if (userBest >= Const.MASTER_SECONDS) {
            holder.imageView_grade.setImageResource(R.drawable.ic_master2);
        } else if (userBest >= Const.DIAMOND_SECONDS) {
            holder.imageView_grade.setImageResource(R.drawable.ic_diamond2);
        } else if (userBest >= Const.PLATINUM_SECONDS) {
            holder.imageView_grade.setImageResource(R.drawable.ic_platinum2);
        } else if (userBest >= Const.GOLD_SECONDS) {
            holder.imageView_grade.setImageResource(R.drawable.ic_gold2);
        } else if (userBest >= Const.SILVER_SECONDS) {
            holder.imageView_grade.setImageResource(R.drawable.ic_silver2);
        } else {
            holder.imageView_grade.setImageResource(R.drawable.ic_bronze2);
        }

        holder.textView_nickname.setText(item.getNickname());
        if (roomTargetDay.equals("36500")) {
            updateTextViewTime(holder, item);
        } else {
            if (item.getCurrenttime() >= Integer.parseInt(roomTargetDay) * 86400) {
                // 이미 목표를 달성한 유저는 계속 시간을 표기할 필요 없음. RECENTSTARTTIME에 ENDTIME을 더해서 챌린지 종료일을 대신 작성

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(dateFormat.parse(item.getRecentstarttime()));
                    calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(roomTargetDay));
                    String finishDate = dateFormat.format(calendar.getTime());
                    holder.textView_time.setText(finishDate + " 성공");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                updateTextViewTime(holder, item);
            }
        }
    }

    private void updateTextViewTime(CustomViewHolder holder, RankingItem item) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date inputTimeDate = null;

        try {
            inputTimeDate = sdf.parse(item.getRecentstarttime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long currentTimeMillis = System.currentTimeMillis();
        long inputTimeMillis = inputTimeDate.getTime();

        long diffMillis = currentTimeMillis - inputTimeMillis;
        long diffSeconds = diffMillis / 1000;

        StringBuilder formattedTime = new StringBuilder();

        long days = diffSeconds / (24 * 60 * 60);
        diffSeconds %= (24 * 60 * 60);

        if (days > 0) {
            formattedTime.append(days).append("일 ");
        }

        long hours = diffSeconds / (60 * 60);
        diffSeconds %= (60 * 60);

        if (hours > 0) {
            formattedTime.append(hours).append("시간 ");
        }

        long minutes = diffSeconds / 60;
        diffSeconds %= 60;

        if (minutes > 0) {
            formattedTime.append(minutes).append("분 ");
        }

        long seconds = diffSeconds;
        formattedTime.append(seconds).append("초");

        String result = formattedTime.toString().trim();
        holder.textView_time.setText(result);

        // 1초마다 업데이트
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTextViewTime(holder, item);
            }
        }, 1000);
    }

    public void stopUpdatingTime() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView textView_nickname, textView_time, textView_rank;
        View constraintLayout;
        ImageView imageView_grade;

        public CustomViewHolder(View itemView) {
            super(itemView);

            textView_nickname = itemView.findViewById(R.id.textView_nickname);
            textView_time = itemView.findViewById(R.id.textView_time);
            textView_rank = itemView.findViewById(R.id.textView_rank);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            imageView_grade = itemView.findViewById(R.id.imageView_grade);
        }
    }
}


