package com.yjy.challengetogether.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.activity.PostActivity;
import com.yjy.challengetogether.etc.CommunityPostItem;
import com.yjy.challengetogether.fragment.CommunityFragment;
import com.yjy.challengetogether.util.Const;
import com.yjy.challengetogether.util.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CommunityFragmentRvAdapter extends RecyclerView.Adapter<CommunityFragmentRvAdapter.CustomViewHolder> {

    private Context context;
    private CommunityFragment communityFragment;
    private List<CommunityPostItem> items;
    private Util util;

    public CommunityFragmentRvAdapter(Context context, CommunityFragment communityFragment, List<CommunityPostItem> items, Util util) {
        this.context = context;
        this.communityFragment = communityFragment;
        this.items = items;
        this.util = util;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_postrv, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final CommunityPostItem item = items.get(position);

        holder.textView_name.setText(item.getNickname());
        if (item.getNickname().equals("(탈퇴 회원)")) {
            holder.textView_name.setTextColor(ContextCompat.getColor(context, R.color.gray));
        }
        holder.textView_content.setText(item.getContent());
        holder.textView_like.setText(item.getLike());
        holder.textView_dislike.setText(item.getDislike());
        holder.textView_comment.setText(item.getCommentcount());

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

        String newDateString;
        // yyyy-MM-dd HH:mm:ss 형식을 MM/dd HH:mm로 변환하여 표시
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // 수정된 기록이 존재하면 수정된 날짜를 기준으로 표기
            Date date;
            if (!item.getModifydate().equals("0000-00-00 00:00:00")) {
                date = dateFormat.parse(item.getModifydate());
            } else {
                date = dateFormat.parse(item.getCreatedate());
            }

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (!item.getModifydate().equals("0000-00-00 00:00:00")) {
            holder.textView_createdate.setText(newDateString + "  ·  수정됨");
        } else {
            holder.textView_createdate.setText(newDateString);
        }

        CardView cardView_item = holder.itemView.findViewById(R.id.cardView_item);
        cardView_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), PostActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("postidx", item.getPostidx());
                communityFragment.startActivityForResult(intent, 1);

                communityFragment.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay);

            }
        });

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView textView_name, textView_content, textView_createdate, textView_like, textView_dislike, textView_comment;
        ImageView imageView_grade;

        public CustomViewHolder(View itemView) {
            super(itemView);

            textView_name = itemView.findViewById(R.id.textView_name);
            textView_content = itemView.findViewById(R.id.textView_content);
            textView_createdate = itemView.findViewById(R.id.textView_createdate);
            textView_like = itemView.findViewById(R.id.textView_like);
            textView_dislike = itemView.findViewById(R.id.textView_dislike);
            textView_comment = itemView.findViewById(R.id.textView_comment);
            imageView_grade = itemView.findViewById(R.id.imageView_grade);
        }
    }
}


