package com.yjy.challengetogether.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.HomeItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.muddz.styleabletoast.StyleableToast;

public class HomeFragmentRvAdapter extends RecyclerView.Adapter<HomeFragmentRvAdapter.CustomViewHolder> {

    private Context context;
    private List<HomeItem> items;
    private Handler mHandler;

    public HomeFragmentRvAdapter(Context context, List<HomeItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homerv, parent, false);
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


        // 시작을 했을떄만 유저의 최근 리셋 시작이 등록되기에 대기방인지 진행중인 방인지 구분
        if (item.getRecentStartTime().equals("0000-00-00 00:00:00")) {
            holder.textView_time.setText("대기방");
            holder.progressBar_percent.setVisibility(View.INVISIBLE);
            holder.textView_percent.setText("");

            holder.textView_usernum.setText(item.getCurrentUserNum() + " / " + item.getMaxUserNum() + (TextUtils.isEmpty(item.getRoomPasswd()) ? "" : "  [비밀방]"));
            holder.textView_usernum.setVisibility(View.VISIBLE);
        } else {
            updateTextViewTime(holder, item);
        }



        CardView cardView_item = holder.itemView.findViewById(R.id.cardView_item);
        cardView_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CardView가 클릭되었을 때 실행할 코드
                StyleableToast.makeText(holder.itemView.getContext(), item.getTitle(), R.style.successToast).show();
            }
        });

    }

    private void updateTextViewTime(CustomViewHolder holder, HomeItem item) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date inputTimeDate = null;

        try {
            inputTimeDate = sdf.parse(item.getRecentStartTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long currentTimeMillis = System.currentTimeMillis();
        long inputTimeMillis = inputTimeDate.getTime();

        long diffMillis = currentTimeMillis - inputTimeMillis;
        long diffSeconds = diffMillis / 1000;


        long endTime = item.getEndTime();
        if (endTime >= 2000000000) {
            // 무제한일경우
            holder.textView_percent.setText("");
            holder.progressBar_percent.setVisibility(View.INVISIBLE);
        } else {
            int archivePercent = (int)(((double)diffSeconds / endTime) * 100);
            if (archivePercent <= 100) {
                holder.textView_percent.setText(String.valueOf(archivePercent) + "%");
                holder.progressBar_percent.setProgress(archivePercent);
            }
        }

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
        ImageView imageView_icon;
        TextView textView_title;
        TextView textView_content;
        TextView textView_time;
        TextView textView_percent;
        DonutProgress progressBar_percent;
        TextView textView_usernum;

        public CustomViewHolder(View itemView) {
            super(itemView);
            imageView_icon = itemView.findViewById(R.id.imageView_icon);
            textView_title = itemView.findViewById(R.id.textView_title);
            textView_content = itemView.findViewById(R.id.textView_content);
            textView_time = itemView.findViewById(R.id.textView_time);
            textView_percent = itemView.findViewById(R.id.textView_percent);
            progressBar_percent = itemView.findViewById(R.id.progressBar_percent);
            textView_usernum = itemView.findViewById(R.id.textView_usernum);
        }
    }
}


