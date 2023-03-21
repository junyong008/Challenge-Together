package com.yjy.challengetogether.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.RoomItem;

import java.util.List;

public class ReadyRoomActivityRvAdapter extends RecyclerView.Adapter<ReadyRoomActivityRvAdapter.CustomViewHolder> {

    private Context context;
    private List<RoomItem> items;

    public ReadyRoomActivityRvAdapter(Context context, List<RoomItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_readyroomrv, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final RoomItem item = items.get(position);

        holder.textView_name.setText(item.getUsername());

        // 첫번째 사용자는 왕관을 씌워 방장임을 알림
        if (position == 0) {
            holder.imageView_crown.setVisibility(View.VISIBLE);
        }

        // 방 소유자이면 2번째 유저부터 삭제표시
        if (item.getPermission().equals("owner") && position >= 1) {
            holder.ibutton_remove.setVisibility(View.VISIBLE);

            holder.ibutton_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 해당 유저 삭제
                    String deleteUserIdx = item.getUseridx();
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView textView_name;
        ImageButton ibutton_remove;
        ImageView imageView_crown;

        public CustomViewHolder(View itemView) {
            super(itemView);

            textView_name = itemView.findViewById(R.id.textView_name);
            ibutton_remove = itemView.findViewById(R.id.ibutton_remove);
            imageView_crown = itemView.findViewById(R.id.imageView_crown);
        }
    }
}


