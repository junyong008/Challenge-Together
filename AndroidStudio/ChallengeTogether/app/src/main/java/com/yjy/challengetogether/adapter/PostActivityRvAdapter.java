package com.yjy.challengetogether.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.activity.PostActivity;
import com.yjy.challengetogether.etc.CommunityCommentItem;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class PostActivityRvAdapter extends RecyclerView.Adapter<PostActivityRvAdapter.CustomViewHolder> {

    private Context context;
    private PostActivity postActivity;
    private List<CommunityCommentItem> items;
    private String sessionUserIdx;
    private String writerIdx;
    public int replyCommentIdx;
    private CardView replyCardView;
    private Util util;


    public PostActivityRvAdapter(Context context, PostActivity postActivity, List<CommunityCommentItem> items, String sessionUserIdx, String writerIdx, Util util) {
        this.context = context;
        this.postActivity = postActivity;
        this.items = items;
        this.sessionUserIdx = sessionUserIdx;
        this.writerIdx = writerIdx;
        this.replyCommentIdx = -1;
        this.util = util;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_commentrv, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    public void clearHighlight() {
        if (replyCommentIdx != -1) {
            replyCardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            replyCardView = null;
            replyCommentIdx = -1;
        }
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final CommunityCommentItem item = items.get(position);

        // 닉네임, 댓글 내용 설정
        holder.textView_name.setText(item.getNickname());
        if (item.getNickname().equals("(삭제)")) {
            holder.textView_name.setTextColor(ContextCompat.getColor(context, R.color.gray));
            holder.textView_content.setTextColor(ContextCompat.getColor(context, R.color.gray));
            holder.textView_content.setText("삭제된 댓글입니다.");
        } else if (item.getNickname().equals("(탈퇴 회원)")) {
            holder.textView_name.setTextColor(ContextCompat.getColor(context, R.color.gray));
            holder.textView_content.setText(item.getContent());
        } else {
            holder.textView_content.setText(item.getContent());
        }

        // 댓글 길이가 너무 길면 더보기 버튼 보이기
        int contentLine = item.getContent().split("\n").length;
        if (contentLine > 10) {
            holder.textView_more.setVisibility(View.VISIBLE);
            holder.textView_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.textView_content.setMaxLines(Integer.MAX_VALUE);
                    holder.textView_more.setVisibility(View.GONE);
                }
            });
        }

        // yyyy-MM-dd HH:mm:ss 형식을 MM/dd HH:mm로 변환하여 표시
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormat.parse(item.getCreatedate());

            Calendar calendar = Calendar.getInstance();
            long now = calendar.getTimeInMillis();
            long diff = now - date.getTime();

            if (diff >= 60 * 60 * 1000) {
                // 1시간 이상 차이나면 "MM/dd HH:mm" 형식으로 표기
                SimpleDateFormat newDateFormat = new SimpleDateFormat("MM/dd HH:mm");
                String newDateString = newDateFormat.format(date);
                holder.textView_createdate.setText(newDateString);
            } else {
                // 1시간 이내 차이나면 "mm분 전" 형식으로 표기
                long minutes = diff / (60 * 1000);
                String newDateString = minutes + "분 전";
                holder.textView_createdate.setText(newDateString);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 댓글 작성자가 글의 작성자 일경우 작성자 태그 보임처리
        if (item.getUseridx().equals(writerIdx)) {
            holder.imageView_writertag.setVisibility(View.VISIBLE);
        }


        // 현 댓글 이후 댓글에 대댓글이 있으면 view 숨기기
        if (position + 1 < getItemCount()) {
            if (items.get(position + 1).getParentidx() > 0) {
                holder.view.setVisibility(View.GONE); // 맨 아래 구분선 지우기
            }
        }

        // 댓글이 대댓글일 경우
        if (item.getParentidx() > 0) {
            holder.imageView_replyenter.setVisibility(View.VISIBLE); // 엔터 표시 띄우기

            // 앞 marin 설정
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.constraintLayout.getLayoutParams();
            layoutParams.setMargins(200, 10, 0, 16); // Margin 설정
            holder.constraintLayout.setLayoutParams(layoutParams);

            // 배경 둥근 회색으로 변경
            Drawable drawable = context.getResources().getDrawable(R.drawable.edit_round_litegray);
            holder.constraintLayout.setBackground(drawable);

            holder.textView_reply.setVisibility(View.GONE); // 답글 달기 버튼 지우기
        }

        // 현 댓글 이후 댓글에 대댓글이 있으면 구분선 숨기기 만약 없으면 그리기
        if (position + 1 < getItemCount()) {
            if (items.get(position + 1).getParentidx() > 0) {
                holder.view.setVisibility(View.GONE); // 맨 아래 구분선 지우기
            } else {
                holder.view.setVisibility(View.VISIBLE); // 맨 아래 구분선 그리기
            }
        }

        // 댓글 메뉴 버튼 추가
        holder.ibutton_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v, item);
            }
        });

        // 대댓글 달기 버튼 클릭
        holder.textView_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // postActivity의 edit_comment에 포커스를 주고 키보드를 올림
                postActivity.getEditComment().requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(postActivity.getEditComment(), InputMethodManager.SHOW_IMPLICIT);

                // 이전에 하이라이트 된 댓글이 있으면 먼저 삭제 후 새로운 댓글 하이라이트
                if (replyCommentIdx != -1) {
                    replyCardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                    replyCardView = null;
                    replyCommentIdx = -1;
                }

                // 해당 댓글 하이라이트
                replyCommentIdx = item.getCommentidx();
                replyCardView = holder.cardView_item;
                holder.cardView_item.setCardBackgroundColor(Color.parseColor("#ffe9d0"));
            }
        });
    }


    // 팝업 메뉴 표시
    private void showPopupMenu(View view, CommunityCommentItem item) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.comment_menu);

        // 아이템 숨김 처리
        Menu menu = popupMenu.getMenu();
        if (item.getUseridx().equals(sessionUserIdx)) {
            // 댓글을 접속한 사용자가 작성한 경우 - 신고버튼 숨김

            MenuItem reportMenuItem = menu.findItem(R.id.menu_comment_report);
            reportMenuItem.setVisible(false);
        } else {
            // 다른 사용자의 댓글일 경우 - 삭제버튼 숨김

            MenuItem deleteMenuItem = menu.findItem(R.id.menu_comment_delete);
            deleteMenuItem.setVisible(false);
        }

        // 옵션 처리
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_comment_delete:
                        deleteComment(item);
                        return true;
                    case R.id.menu_comment_report:
                        reportComment(item);
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    // 댓글 삭제 처리
    private void deleteComment(CommunityCommentItem selectedItem) {

        util.showCustomDialog(new Util.OnConfirmListener() {
            @Override
            public void onConfirm(boolean isConfirmed, String msg) {
                if (isConfirmed) {
                    OnTaskCompleted onDeleteCommentTaskCompleted = new OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(String result) {
                            if (result.indexOf("DELETE SUCCESS") != -1) {
                                postActivity.reLoadComments();
                            } else {
                                util.checkHttpResult(result);
                            }
                        }
                    };

                    HttpAsyncTask deleteCommentTask = new HttpAsyncTask(postActivity, onDeleteCommentTaskCompleted);
                    String phpFile = "service.php";
                    String postParameters = "service=deletecomment&commentidx=" + selectedItem.getCommentidx();

                    deleteCommentTask.execute(phpFile, postParameters, util.getSessionKey());
                }
            }
        }, "댓글을 삭제하시겠습니까?", "confirm");
    }

    // 댓글 신고 처리
    private void reportComment(CommunityCommentItem selectedItem) {
        util.showCustomDialog(new Util.OnConfirmListener() {
            @Override
            public void onConfirm(boolean isConfirmed, String reportReason) {
                if (isConfirmed) {

                    OnTaskCompleted onAddReportTaskCompleted = new OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(String result) {
                            if (result.indexOf("ADD SUCCESS") != -1) {
                                StyleableToast.makeText(postActivity, "신고가 접수되었습니다.", R.style.successToast).show();
                            } else if (result.indexOf("ALREADY REPORT") != -1) {
                                StyleableToast.makeText(postActivity, "이미 신고가 접수되었습니다.", R.style.errorToast).show();
                            } else {
                                util.checkHttpResult(result);
                            }
                        }
                    };

                    HttpAsyncTask addReportTask = new HttpAsyncTask(postActivity, onAddReportTaskCompleted);
                    String phpFile = "service.php";
                    String postParameters = "service=addreport&postidx=0&commentidx=" + selectedItem.getCommentidx() + "&reason=" + reportReason;

                    addReportTask.execute(phpFile, postParameters, util.getSessionKey());
                }
            }
        }, "\uD83D\uDCE2   신고사유를 선택해주세요.", "report");
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        CardView cardView_item;
        TextView textView_name, textView_content, textView_more, textView_createdate, textView_like, textView_dislike, textView_comment, textView_reply;
        ImageView imageView_replyenter, imageView_writertag;
        ImageButton ibutton_menu;
        ConstraintLayout constraintLayout;
        View view;


        public CustomViewHolder(View itemView) {
            super(itemView);

            cardView_item = itemView.findViewById(R.id.cardView_item);
            textView_name = itemView.findViewById(R.id.textView_name);
            textView_content = itemView.findViewById(R.id.textView_content);
            textView_more = itemView.findViewById(R.id.textView_more);
            textView_createdate = itemView.findViewById(R.id.textView_createdate);
            textView_like = itemView.findViewById(R.id.textView_like);
            textView_dislike = itemView.findViewById(R.id.textView_dislike);
            textView_comment = itemView.findViewById(R.id.textView_comment);
            ibutton_menu = itemView.findViewById(R.id.ibutton_menu);
            textView_reply = itemView.findViewById(R.id.textView_reply);
            imageView_replyenter = itemView.findViewById(R.id.imageView_replyenter);
            imageView_writertag = itemView.findViewById(R.id.imageView_writertag);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            view = itemView.findViewById(R.id.view);
        }
    }
}


