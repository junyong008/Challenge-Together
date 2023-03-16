package com.yjy.challengetogether.adapter;

import static android.app.Activity.RESULT_OK;
import static com.yjy.challengetogether.activity.SelecticonActivity.EXTRA_SELECTED_ITEM1;
import static com.yjy.challengetogether.activity.SelecticonActivity.EXTRA_SELECTED_ITEM2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.etc.IconItem;
import com.yjy.challengetogether.R;

import java.util.List;

public class SelecticonRvAdapter extends RecyclerView.Adapter<SelecticonRvAdapter.CustomViewHolder> {

    private Context context;
    private List<IconItem> items;
    private Activity activity;

    public SelecticonRvAdapter(Context context, List<IconItem> items, Activity activity) {
        this.context = context;
        this.items = items;
        this.activity = activity;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selecticonrv, parent, false);
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

        // 타이틀 설정
        holder.textView_title.setText(item.getTitle());

        CardView cardView_item = holder.itemView.findViewById(R.id.cardView_item);
        cardView_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CardView가 클릭되었을 때 실행할 코드
                // StyleableToast.makeText(holder.itemView.getContext(), item.getTitle(), R.style.successToast).show();

                // 결괏값(타이틀, 아이콘)을 반환. 부모 액티비티(다이얼로그액티비티 종료)
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_SELECTED_ITEM1, item.getIcon());
                resultIntent.putExtra(EXTRA_SELECTED_ITEM2, item.getTitle());

                activity.setResult(RESULT_OK, resultIntent);
                activity.finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView imageView_icon;
        TextView textView_title;
        public CustomViewHolder(View itemView) {
            super(itemView);
            imageView_icon = itemView.findViewById(R.id.imageView_icon);
            textView_title = itemView.findViewById(R.id.textView_title);
        }
    }
}


