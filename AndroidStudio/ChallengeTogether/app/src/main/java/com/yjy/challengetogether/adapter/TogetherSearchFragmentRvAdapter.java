package com.yjy.challengetogether.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.activity.ReadyRoomActivity;
import com.yjy.challengetogether.etc.HomeItem;
import com.yjy.challengetogether.util.Util;

import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class TogetherSearchFragmentRvAdapter extends RecyclerView.Adapter<TogetherSearchFragmentRvAdapter.CustomViewHolder> {

    private Context context;
    private List<HomeItem> items;
    private Util util;

    public TogetherSearchFragmentRvAdapter(Context context, List<HomeItem> items, Util util) {
        this.context = context;
        this.items = items;
        this.util = util;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publicroomrv, parent, false);
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

        if (item.getEndTime() < 36500) {
            holder.textView_time.setText(item.getRoomidx() + "번 대기방 : " +  item.getEndTime() + "일 목표");
        } else {
            holder.textView_time.setText(item.getRoomidx() +  "번 대기방 : 무제한 목표");
        }

        holder.textView_usernum.setText(item.getCurrentUserNum() + " / " + item.getMaxUserNum() + (TextUtils.isEmpty(item.getRoomPasswd()) ? "" : "  [비밀방]"));

        CardView cardView_item = holder.itemView.findViewById(R.id.cardView_item);
        cardView_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(item.getRoomPasswd())) {
                    // 비밀번호가 있으면 다이얼로그로 비밀번호 입력받기
                    util.showCustomDialog(new Util.OnConfirmListener() {
                        @Override
                        public void onConfirm(boolean isConfirmed, String inputpwd) {
                            if (TextUtils.isEmpty(inputpwd)) {
                                StyleableToast.makeText(holder.itemView.getContext(), "비밀번호를 입력해주세요.", R.style.errorToast).show();
                                return;
                            } else if (inputpwd.equals(item.getRoomPasswd())) {
                                String roomIdx = item.getRoomidx();
                                Intent intent = new Intent(holder.itemView.getContext(), ReadyRoomActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("roomidx", roomIdx);
                                holder.itemView.getContext().startActivity(intent);
                            } else {
                                StyleableToast.makeText(holder.itemView.getContext(), "비밀번호가 일치하지 않습니다.", R.style.errorToast).show();
                                return;
                            }
                        }
                    }, "비밀번호를 입력해주세요.", "inputpasswd");
                } else {
                    // 비밀번호가 없으면 바로 방을 보여줌
                    String roomIdx = item.getRoomidx();
                    Intent intent = new Intent(holder.itemView.getContext(), ReadyRoomActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("roomidx", roomIdx);
                    holder.itemView.getContext().startActivity(intent);
                }
            }
        });

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
        TextView textView_usernum;

        public CustomViewHolder(View itemView) {
            super(itemView);
            imageView_icon = itemView.findViewById(R.id.imageView_icon);
            textView_title = itemView.findViewById(R.id.textView_title);
            textView_content = itemView.findViewById(R.id.textView_content);
            textView_time = itemView.findViewById(R.id.textView_time);
            textView_usernum = itemView.findViewById(R.id.textView_usernum);
        }
    }
}


