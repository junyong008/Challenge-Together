package com.yjy.challengetogether.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.adapter.PostActivityRvAdapter;
import com.yjy.challengetogether.etc.CommunityCommentItem;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class PostActivity extends AppCompatActivity {

    private ImageButton ibutton_back, ibutton_send;
    private Button button_like, button_dislike;
    private TextView textView_name, textView_createdate, textView_menu, textView_content, textView_likecount, textView_dislikecount, textView_commentcount, textView_none;
    private RecyclerView recyclerView_comments;
    private PostActivityRvAdapter adapter;
    private LinearLayoutManager llm;
    private List<CommunityCommentItem> items;
    private EditText edit_comment;
    private String sessionUserIdx;
    private String writerIdx;
    private String postidx;
    private com.yjy.challengetogether.util.Util util = new Util(PostActivity.this);

    @Override
    public void onBackPressed() {
        if (adapter.replyCommentIdx != -1) {
            adapter.clearHighlight();
        } else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();
        postidx = intent.getStringExtra("postidx");

        ibutton_back = findViewById(R.id.ibutton_back);
        textView_name = findViewById(R.id.textView_name);
        textView_createdate = findViewById(R.id.textView_createdate);
        textView_menu = findViewById(R.id.textView_menu);
        textView_content = findViewById(R.id.textView_content);
        textView_likecount = findViewById(R.id.textView_likecount);
        textView_dislikecount = findViewById(R.id.textView_dislikecount);
        button_like = findViewById(R.id.button_like);
        button_dislike = findViewById(R.id.button_dislike);
        textView_commentcount = findViewById(R.id.textView_commentcount);
        textView_none = findViewById(R.id.textView_none);
        edit_comment = findViewById(R.id.edit_comment);
        ibutton_send = findViewById(R.id.ibutton_send);
        recyclerView_comments = findViewById(R.id.recyclerView_comments);
        llm = new LinearLayoutManager(PostActivity.this);

        // 상단 뒤로가기 버튼 클릭
        ibutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.replyCommentIdx != -1) {
                    adapter.clearHighlight();
                } else {
                    finish();
                }
            }
        });

        OnTaskCompleted onLoadCommentTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                Boolean isJSON = util.isJson(result);

                if (isJSON) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        // 게시글 정보 추출, 설정
                        JSONObject obj = new JSONObject(jsonObject.getString("postInfo"));
                        String writerName = obj.getString("NAME");
                        String createdate = obj.getString("CREATEDATE");
                        writerIdx = obj.getString("USER_IDX");

                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = dateFormat.parse(createdate);

                            Calendar calendar = Calendar.getInstance();
                            long now = calendar.getTimeInMillis();
                            long diff = now - date.getTime();

                            if (diff >= 60 * 60 * 1000) {
                                // 1시간 이상 차이나면 "MM/dd HH:mm" 형식으로 표기
                                SimpleDateFormat newDateFormat = new SimpleDateFormat("MM/dd HH:mm");
                                String newDateString = newDateFormat.format(date);
                                createdate = newDateString;
                            } else {
                                // 1시간 이내 차이나면 "mm분 전" 형식으로 표기
                                long minutes = diff / (60 * 1000);
                                String newDateString = minutes + "분 전";
                                createdate = newDateString;
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        String postContent = obj.getString("CONTENT");
                        String likeCount = obj.getString("LIKECOUNT");
                        String dislikeCount = obj.getString("DISLIKECOUNT");
                        String commentCount = obj.getString("COMMENTCOUNT");
                        sessionUserIdx = obj.getString("SESSIONUSER_IDX");   // 현재 세션 주인 (로그인 유저)의 USER_IDX를 받아와 게시글/댓글 주인 여부 확인

                        textView_name.setText(writerName);
                        textView_createdate.setText(createdate);
                        textView_content.setText(postContent);
                        textView_likecount.setText(likeCount);
                        textView_dislikecount.setText(dislikeCount);
                        textView_commentcount.setText("댓글 " + commentCount);



                        // 댓글 정보 추출, 리스트뷰 전달할 items 설정
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("commentList"));
                        items = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            obj = jsonArray.getJSONObject(i);

                            CommunityCommentItem item = new CommunityCommentItem();

                            item.setUseridx(obj.getString("USER_IDX"));
                            item.setNickname(obj.getString("NAME"));
                            item.setContent(obj.getString("CONTENT"));
                            item.setCreatedate(obj.getString("CREATEDATE"));
                            item.setCommentidx(obj.getInt("COMCOMMENT_IDX"));
                            item.setParentidx(obj.getInt("PARENT_IDX"));

                            items.add(item);
                        }

                        // Parentidx가 0이 아닌 (대댓글)인 댓글을 모두 tempItems에 임시 저장. 그리고 items 배열에서는 제거
                        List<CommunityCommentItem> tempItems = new ArrayList<>();
                        for (int i = 0; i < items.size(); i++) {
                            if (items.get(i).getParentidx() != 0) {
                                tempItems.add(items.get(i));
                                items.remove(i);
                                i--;
                            }
                        }

                        // 모든 item을 순회하며 대댓글의 Parentidx와 일치하면 하나씩 삽입, 삽입된 항목은 tempItems 에서 제거
                        for (int i = 0; i < items.size(); i++) {
                            for (int j = tempItems.size() - 1; j >= 0; j--) {
                                if (items.get(i).getCommentidx() == tempItems.get(j).getParentidx()) {
                                    items.add(i + 1, tempItems.get(j)); // 원 댓글 바로 다음 항목에 추가. 대댓글은 역순으로 돌기에 정순으로 추가가 됨.
                                    tempItems.remove(j);
                                }
                            }
                        }

                        // 댓글이 존재하면 안내메시지 가리기
                        if(items.size() > 0) {
                            textView_none.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    recyclerView_comments.setHasFixedSize(true);
                    recyclerView_comments.setLayoutManager(llm);

                    adapter = new PostActivityRvAdapter(PostActivity.this.getApplication(), PostActivity.this, items, sessionUserIdx, writerIdx, util);
                    recyclerView_comments.setAdapter(adapter);

                } else {
                    util.checkHttpResult(result);
                }
            }
        };

        HttpAsyncTask loadCommentTask = new HttpAsyncTask(PostActivity.this, onLoadCommentTaskCompleted);
        String phpFile = "service.php";
        String postParameters = "service=getcomments&postidx=" + postidx;

        loadCommentTask.execute(phpFile, postParameters, util.getSessionKey());


        // 댓글 달기 버튼 클릭
        ibutton_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String commentContent = edit_comment.getText().toString().trim();

                // 내용이 비어있을 경우, 안내메시지 반환
                if (TextUtils.isEmpty(commentContent)) {
                    StyleableToast.makeText(PostActivity.this, "내용을 입력해주세요.", R.style.errorToast).show();
                    return;
                }


                OnTaskCompleted onAddCommentTaskCompleted = new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(String result) {
                        if (result.indexOf("ADD SUCCESS") != -1) {

                            // 키보드 내리기
                            InputMethodManager imm = (InputMethodManager) PostActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null && imm.isActive()) {
                                imm.hideSoftInputFromWindow(edit_comment.getWindowToken(), 0);
                            }

                            // 댓글 입력 창 비우고 recreate
                            edit_comment.setText("");
                            recreate();
                        } else {
                            util.checkHttpResult(result);
                        }
                    }
                };



                HttpAsyncTask addCommentTask = new HttpAsyncTask(PostActivity.this, onAddCommentTaskCompleted);
                String phpFile = "service.php";
                String postParameters;
                if (adapter.replyCommentIdx != -1) {
                    // 대댓글 달기
                    postParameters = "service=addcomment&postidx=" + postidx + "&commentcontent=" + commentContent + "&parentidx=" + adapter.replyCommentIdx;
                } else {
                    // 새로운 댓글 달기
                    postParameters = "service=addcomment&postidx=" + postidx + "&commentcontent=" + commentContent + "&parentidx=0";
                }

                addCommentTask.execute(phpFile, postParameters, util.getSessionKey());
            }
        });


        // 게시글 메뉴 버튼 클릭
        textView_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });



        // 좋아요 혹은 싫어요 서버 통신
        OnTaskCompleted onAddActionTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                if (result.indexOf("ADD SUCCESS") != -1) {
                    recreate();
                } else if (result.indexOf("ALREADY LIKE") != -1) {
                    StyleableToast.makeText(PostActivity.this, "이미 추천하셨습니다.", R.style.errorToast).show();
                } else if (result.indexOf("ALREADY DISLIKE") != -1) {
                    StyleableToast.makeText(PostActivity.this, "이미 비추천하셨습니다.", R.style.errorToast).show();
                } else {
                    util.checkHttpResult(result);
                }
            }
        };

        // 좋아요 버튼 클릭
        button_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask addLikeTask = new HttpAsyncTask(PostActivity.this, onAddActionTaskCompleted);
                String phpFile = "service.php";
                String postParameters = "service=addaction&postidx=" + postidx + "&actiontype=LIKE";

                addLikeTask.execute(phpFile, postParameters, util.getSessionKey());
            }
        });

        // 싫어요 버튼 클릭
        button_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAsyncTask addDisLikeTask = new HttpAsyncTask(PostActivity.this, onAddActionTaskCompleted);
                String phpFile = "service.php";
                String postParameters = "service=addaction&postidx=" + postidx + "&actiontype=DISLIKE";

                addDisLikeTask.execute(phpFile, postParameters, util.getSessionKey());
            }
        });

    }

    // 팝업 메뉴 표시
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(PostActivity.this, view);
        popupMenu.inflate(R.menu.post_menu);

        // 아이템 숨김 처리
        Menu menu = popupMenu.getMenu();
        if (writerIdx.equals(sessionUserIdx)) {
            // 게시글을 접속한 사용자가 작성한 경우 - 신고버튼 숨김

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
                        deletePost();
                        return true;
                    case R.id.menu_comment_report:
                        reportPost();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    // 게시글 삭제 처리
    private void deletePost() {

        util.showCustomDialog(new Util.OnConfirmListener() {
            @Override
            public void onConfirm(boolean isConfirmed, String msg) {
                if (isConfirmed) {
                    OnTaskCompleted onDeletePostTaskCompleted = new OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(String result) {
                            if (result.indexOf("DELETE SUCCESS") != -1) {
                                StyleableToast.makeText(PostActivity.this, "삭제되었습니다.", R.style.successToast).show();
                                Intent intent = new Intent(PostActivity.this, MainpageActivity.class);
                                intent.putExtra("FRAGMENT_TO_SHOW", "community_fragment");
                                startActivity(intent);
                                finish();
                            } else {
                                util.checkHttpResult(result);
                            }
                        }
                    };

                    HttpAsyncTask deletePostTask = new HttpAsyncTask(PostActivity.this, onDeletePostTaskCompleted);
                    String phpFile = "service.php";
                    String postParameters = "service=deletepost&postidx=" + postidx;

                    deletePostTask.execute(phpFile, postParameters, util.getSessionKey());
                }
            }
        }, "게시글을 삭제하시겠습니까?", "confirm");
    }

    // 게시글 신고 처리
    private void reportPost() {
        util.showCustomDialog(new Util.OnConfirmListener() {
            @Override
            public void onConfirm(boolean isConfirmed, String reportReason) {
                if (isConfirmed) {

                    OnTaskCompleted onAddReportTaskCompleted = new OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(String result) {
                            if (result.indexOf("ADD SUCCESS") != -1) {
                                StyleableToast.makeText(PostActivity.this, "신고가 접수되었습니다.", R.style.successToast).show();
                            } else if (result.indexOf("ALREADY REPORT") != -1) {
                                StyleableToast.makeText(PostActivity.this, "이미 신고가 접수되었습니다.", R.style.errorToast).show();
                            } else {
                                util.checkHttpResult(result);
                            }
                        }
                    };

                    HttpAsyncTask addReportTask = new HttpAsyncTask(PostActivity.this, onAddReportTaskCompleted);
                    String phpFile = "service.php";
                    String postParameters = "service=addreport&postidx=" + postidx + "&commentidx=0&reason=" + reportReason;

                    addReportTask.execute(phpFile, postParameters, util.getSessionKey());
                }
            }
        }, "\uD83D\uDCE2   신고사유를 선택해주세요.", "report");
    }

    // PostActivityRvAdapter 에서 답글달기를 할때 포커스 및 스크롤 이동효과를 주기 위한 접근용 getter 메서드
    public EditText getEditComment() {
        return edit_comment;
    }
}