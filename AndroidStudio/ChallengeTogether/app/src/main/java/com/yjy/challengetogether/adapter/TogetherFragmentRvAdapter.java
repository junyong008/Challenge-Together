package com.yjy.challengetogether.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.IconItem;
import com.yjy.challengetogether.fragment.TogetherFragment;
import com.yjy.challengetogether.fragment.TogetherSearchFragment;

import java.util.List;

public class TogetherFragmentRvAdapter extends RecyclerView.Adapter<TogetherFragmentRvAdapter.CustomViewHolder> {

    private Context context;
    private List<IconItem> items;
    private Handler mHandler;
    FragmentTransaction transaction;
    TogetherFragment fragment;

    public TogetherFragmentRvAdapter(Context context, List<IconItem> items, FragmentTransaction transaction, TogetherFragment fragment) {
        this.context = context;
        this.items = items;
        this.transaction = transaction;
        this.fragment = fragment;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoryrv, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final IconItem item = items.get(position);

        // 아이콘 설정
        int drawableId = context.getResources().getIdentifier(item.getIcon(), "drawable", context.getPackageName());
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        holder.imageView_icon.setImageDrawable(drawable);

        holder.textView_title.setText(item.getTitle());

        CardView cardView_item = holder.itemView.findViewById(R.id.cardView_item);
        cardView_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CardView가 클릭되었을 때 실행할 코드

                Bundle bundle = new Bundle();
                bundle.putString("categoryicon", item.getIcon());
                bundle.putString("categorytitle", item.getTitle());

                TogetherSearchFragment TogetherSearchFragment = new TogetherSearchFragment();
                TogetherSearchFragment.setArguments(bundle); // 생성한 Bundle 객체를 전달 받는 Fragment에 설정

                transaction.remove(fragment);
                transaction.add(R.id.fragment_mainpage, TogetherSearchFragment);
                transaction.addToBackStack(null); // 뒤로가기 했을시 TogetherFragment로 돌아오도록 설정. 바텀네비게이션 클릭시 스택 삭제되므로 제어 가능.
                transaction.commit();
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

        public CustomViewHolder(View itemView) {
            super(itemView);
            imageView_icon = itemView.findViewById(R.id.imageView_icon);
            textView_title = itemView.findViewById(R.id.textView_title);
        }
    }
}


