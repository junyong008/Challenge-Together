package com.yjy.challengetogether;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.CustomViewHolder> {

    private Context context;
    private List<HomeItem> items;

    public RvAdapter(Context context, List<HomeItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final HomeItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());

        CardView cardView = holder.itemView.findViewById(R.id.item_cardview);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CardView가 클릭되었을 때 실행할 코드
                StyleableToast.makeText(holder.itemView.getContext(), item.getTitle(), R.style.successToast).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;

        public CustomViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_tv_title);
            content = itemView.findViewById(R.id.item_tv_content);
        }
    }
}


