package com.yjy.challengetogether.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.etc.HomeItem;
import com.yjy.challengetogether.R;

import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class HomeFragmentRvAdapter extends RecyclerView.Adapter<HomeFragmentRvAdapter.CustomViewHolder> {

    private Context context;
    private List<HomeItem> items;

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
        holder.textView_title.setText(item.getTitle());
        holder.textView_content.setText(item.getContent());

        CardView cardView_item = holder.itemView.findViewById(R.id.cardView_item);
        cardView_item.setOnClickListener(new View.OnClickListener() {
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
        TextView textView_title;
        TextView textView_content;

        public CustomViewHolder(View itemView) {
            super(itemView);
            textView_title = itemView.findViewById(R.id.textView_title);
            textView_content = itemView.findViewById(R.id.textView_content);
        }
    }
}


