package com.yjy.challengetogether.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import androidx.core.content.ContextCompat;
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

    private ImageButton ibutton_back, ibutton_send, ibutton_bookmark, ibutton_menu;
    private Button button_like, button_dislike;
    private TextView textView_name, textView_createdate, textView_content, textView_likecount, textView_dislikecount, textView_commentcount, textView_none;
    private RecyclerView recyclerView_comments;
    private PostActivityRvAdapter adapter;
    private LinearLayoutManager llm;
    private List<CommunityCommentItem> items;
    private EditText edit_comment;
    private String sessionUserIdx, writerIdx, postidx, postContent;
    private boolean isChange, isBookmark;
    private OnTaskCompleted onLoadCommentTaskCompleted;
    private com.yjy.challengetogether.util.Util util = new Util(PostActivity.this);

    @Override
    public void onBackPressed() {
        if (adapter.replyCommentIdx != -1) {
            adapter.clearHighlight();
        } else {

            // 댓글달기, 좋아요 or 싫어요 클릭과 같은 외부 표시 변경사항이 있으면 뒤로가기할때 다시 불러오기 요청
            if (isChange) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("result", "success");
                setResult(Activity.RESULT_OK, resultIntent);
            }
            finish();
            overridePendingTransition(R.anim.stay, R.anim.slide_out_right);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();
        postidx = intent.getStringExtra("postidx");
        isChange = false;

        ibutton_back = findViewById(R.id.ibutton_back);
        textView_name = findViewById(R.id.textView_name);
        textView_createdate = findViewById(R.id.textView_createdate);
        ibutton_bookmark = findViewById(R.id.ibutton_bookmark);
        ibutton_menu = findViewById(R.id.ibutton_menu);
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
                    Log.d("test", String.valueOf(isChange));
                    if (isChange) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("result", "success");
                        setResult(Activity.RESULT_OK, resultIntent);
                    }
                    finish();
                    overridePendingTransition(R.anim.stay, R.anim.slide_out_right);
                }
            }
        });


        // 게시글 정보 및 댓글 목록 쭉 받아오기. 댓글을 달거나 추천 등을 클릭하면 아래 로직을 다시 실행시킨다.
        onLoadCommentTaskCompleted = new OnTaskCompleted() {
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
                        String modifydate = obj.getString("MODIFYDATE");
                        writerIdx = obj.getString("USER_IDX");

                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                            // 수정된 기록이 존재하면 수정된 날짜를 기준으로 표기
                            Date date;
                            if (!modifydate.equals("0000-00-00 00:00:00")) {
                                date = dateFormat.parse(modifydate);
                            } else {
                                date = dateFormat.parse(createdate);
                            }


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

                        postContent = obj.getString("CONTENT");
                        String likeCount = obj.getString("LIKECOUNT");
                        String dislikeCount = obj.getString("DISLIKECOUNT");
                        String commentCount = obj.getString("COMMENTCOUNT");
                        sessionUserIdx = obj.getString("SESSIONUSER_IDX");   // 현재 세션 주인 (로그인 유저)의 USER_IDX를 받아와 게시글/댓글 주인 여부 확인
                        if (obj.getString("ISBOOKMARK").equals("1")) {
                            isBookmark = true;
                        } else {
                            isBookmark = false;
                        }


                        // 북마크 된 게시글이면 색칠하기
                        if (isBookmark) {
                            ibutton_bookmark.setImageResource(R.drawable.ic_bookmarkfill);
                        }

                        // 탈퇴 회원이면 닉네임 회색처리
                        textView_name.setText(writerName);
                        if (writerName.equals("(탈퇴 회원)")) {
                            textView_name.setTextColor(ContextCompat.getColor(PostActivity.this, R.color.gray));
                        }

                        // 수정된 게시글이면 '수정됨' 표기를 붙여서 수정됨을 알림
                        if (!modifydate.equals("0000-00-00 00:00:00")) {
                            textView_createdate.setText(createdate + "  ·  수정됨");
                        } else {
                            textView_createdate.setText(createdate);
                        }
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

                            // 댓글 입력 창 비우기
                            edit_comment.setText("");

                            // 다시 게시글 정보 받아와서 최신화 하기
                            reLoadComments();
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
        ibutton_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });




        // 북마크 추가/제거 서버 통신
        OnTaskCompleted onChangeBookmarkTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                if (result.indexOf("ADD SUCCESS") != -1) {
                    isChange = true;
                    isBookmark = true;
                    ibutton_bookmark.setImageResource(R.drawable.ic_bookmarkfill);
                    StyleableToast.makeText(PostActivity.this, "북마크가 추가되었습니다.", R.style.successToast).show();
                } else if (result.indexOf("DELETE SUCCESS") != -1) {
                    isChange = true;
                    isBookmark = false;
                    ibutton_bookmark.setImageResource(R.drawable.ic_bookmark);
                    StyleableToast.makeText(PostActivity.this, "북마크가 제거되었습니다.", R.style.successToast).show();
                } else {
                    util.checkHttpResult(result);
                }
            }
        };

        // 북마크 버튼 클릭
        ibutton_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                util.showCustomDialog(new Util.OnConfirmListener() {
                    @Override
                    public void onConfirm(boolean isConfirmed, String msg) {
                        if (isConfirmed) {

                            if (!isBookmark) {
                                // 북마크 안돼있으면 추가
                                HttpAsyncTask changeBookmarkTask = new HttpAsyncTask(PostActivity.this, onChangeBookmarkTaskCompleted);
                                String phpFile = "service.php";
                                String postParameters = "service=changebookmark&postidx=" + postidx;
                                changeBookmarkTask.execute(phpFile, postParameters, util.getSessionKey());
                            } else {
                                // 북마크 돼있으면 제거
                                HttpAsyncTask changeBookmarkTask = new HttpAsyncTask(PostActivity.this, onChangeBookmarkTaskCompleted);
                                String phpFile = "service.php";
                                String postParameters = "service=changebookmark&postidx=" + postidx;
                                changeBookmarkTask.execute(phpFile, postParameters, util.getSessionKey());
                            }

                        }
                    }
                }, "북마크를 " + (isBookmark ? "제거" : "추가") + "하시겠습니까?", "confirm");

            }
        });



        // 좋아요 혹은 싫어요 서버 통신
        OnTaskCompleted onAddActionTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                if (result.indexOf("ADD SUCCESS") != -1) {
                    reLoadComments();
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

    public void reLoadComments() {
        isChange = true;

        // 서버에서 다시 게시글 정보 받아오기
        HttpAsyncTask loadCommentTask = new HttpAsyncTask(PostActivity.this, onLoadCommentTaskCompleted);
        String phpFile = "service.php";
        String postParameters = "service=getcomments&postidx=" + postidx;
        loadCommentTask.execute(phpFile, postParameters, util.getSessionKey());
    }

    // 팝업 메뉴 표시
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(PostActivity.this, view);
        popupMenu.inflate(R.menu.post_menu);

        // 아이템 숨김 처리
        Menu menu = popupMenu.getMenu();
        if (writerIdx.equals(sessionUserIdx)) {
            // 게시글을 접속한 사용자가 작성한 경우 - 신고버튼 숨김

            MenuItem reportMenuItem = menu.findItem(R.id.menu_post_report);
            reportMenuItem.setVisible(false);
        } else {
            // 다른 사용자의 댓글일 경우 - 삭제, 수정 버튼 숨김

            MenuItem deleteMenuItem = menu.findItem(R.id.menu_post_delete);
            MenuItem modifyMenuItem = menu.findItem(R.id.menu_post_modify);
            deleteMenuItem.setVisible(false);
            modifyMenuItem.setVisible(false);
        }

        // 옵션 처리
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_post_delete:
                        deletePost();
                        return true;
                    case R.id.menu_post_modify:
                        modifyPost();
                        return true;
                    case R.id.menu_post_report:
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
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("result", "success");
                                setResult(Activity.RESULT_OK, resultIntent);
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

    // 게시글 수정 처리
    private void modifyPost() {
        Intent intent = new Intent(PostActivity.this, ModifyPostActivity.class);
        intent.putExtra("postidx", postidx);
        intent.putExtra("postcontent", postContent);
        startActivityForResult(intent, 1);
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

    // ModifyPostActivity에서 반환된 결과 처리
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra("result");
            if (result.equals("success")) {
                reLoadComments();
            }
        }
    }

    // PostActivityRvAdapter 에서 답글달기를 할때 포커스 및 스크롤 이동효과를 주기 위한 접근용 getter 메서드
    public EditText getEditComment() {
        return edit_comment;
    }
}