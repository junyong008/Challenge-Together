package com.yjy.challengetogether.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.ResetItem;

import java.util.List;

public class ResetRecordRvAdapter extends RecyclerView.Adapter<ResetRecordRvAdapter.CustomViewHolder> {

    private Context context;
    private List<ResetItem> items;

    public ResetRecordRvAdapter(Context context, List<ResetItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resetrecordrv, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final ResetItem item = items.get(position);

        String resetMemo = item.getResetmemo();
        if (TextUtils.isEmpty(resetMemo)) {
            holder.textView_memo.setVisibility(View.GONE);
            holder.view_div.setVisibility(View.GONE);
        } else {
            holder.textView_memo.setText(resetMemo);
        }

        holder.textView_resetdate.setText("리셋 날짜 :   " + item.getResetdate());
        holder.textView_abstinencetime.setText("참은 시간 :   " + secondsToDHMS(Integer.parseInt(item.getAbstinencetime())));
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView textView_memo, textView_resetdate, textView_abstinencetime;
        View view_div;

        public CustomViewHolder(View itemView) {
            super(itemView);

            textView_memo = itemView.findViewById(R.id.textView_memo);
            textView_resetdate = itemView.findViewById(R.id.textView_resetdate);
            textView_abstinencetime = itemView.findViewById(R.id.textView_abstinencetime);
            view_div = itemView.findViewById(R.id.view_div);
        }
    }

    public String secondsToDHMS(long intputSeconds) {
        StringBuilder formattedTime = new StringBuilder();

        long days = intputSeconds / (24 * 60 * 60);
        intputSeconds %= (24 * 60 * 60);
        if (days > 0) {
            formattedTime.append(days).append("일 ");
        }

        long hours = intputSeconds / (60 * 60);
        intputSeconds %= (60 * 60);
        if (hours > 0) {
            formattedTime.append(hours).append("시간 ");
        }

        long minutes = intputSeconds / 60;
        intputSeconds %= 60;
        if (minutes > 0) {
            formattedTime.append(minutes).append("분 ");
        }

        long seconds = intputSeconds;
        formattedTime.append(seconds).append("초");

        return formattedTime.toString().trim();
    }
}


