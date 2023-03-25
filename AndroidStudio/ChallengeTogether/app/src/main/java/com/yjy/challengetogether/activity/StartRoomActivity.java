package com.yjy.challengetogether.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.CustomDayDecorator;
import com.yjy.challengetogether.etc.EventDecorator;
import com.yjy.challengetogether.etc.MySelectorDecorator;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.etc.OneDayDecorator;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import io.github.muddz.styleabletoast.StyleableToast;

public class StartRoomActivity extends AppCompatActivity {

    private boolean isCompleteChallenge;
    private ImageButton ibutton_close;
    private ImageView imageView_icon;
    private TextView textView_title, textView_content, textView_currentTime, textView_TargetTime, textView_remainTime, textView_rank, textView_wisesaying;
    private RoundCornerProgressBar progress_achievement;
    MaterialCalendarView calendarView;
    private Button button_showrank, button_reset, button_giveup;
    private View constraintLayout3, constraintLayout4;
    private Handler mHandler;
    private com.yjy.challengetogether.util.Util util = new Util(StartRoomActivity.this);

    @Override
    public void onBackPressed() {
        if (!isCompleteChallenge) {
            // 방을 추가하거나, 시간을 리셋하는 등 방의 내용을 바꿀 수 있는 액티비티를 실행하고 메인으로 돌아갈땐 액티비티 다시 불러오기. 그 외에는 불필요한 refresh를 줄여 서버 부담 최소화
            Intent intent = new Intent(getApplicationContext(), MainpageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startroom);

        Intent intent = getIntent();
        String roomidx = intent.getStringExtra("roomidx");

        ibutton_close = findViewById(R.id.ibutton_close);
        imageView_icon = findViewById(R.id.imageView_icon);
        textView_title = findViewById(R.id.textView_title);
        textView_content = findViewById(R.id.textView_content);
        textView_currentTime = findViewById(R.id.textView_currentTime);
        textView_TargetTime = findViewById(R.id.textView_TargetTime);
        textView_remainTime = findViewById(R.id.textView_remainTime);
        textView_rank = findViewById(R.id.textView_rank);
        textView_wisesaying = findViewById(R.id.textView_wisesaying);
        progress_achievement = findViewById(R.id.progress_achievement);
        calendarView = findViewById(R.id.calendarView);
        button_showrank = findViewById(R.id.button_showrank);
        button_reset = findViewById(R.id.button_reset);
        button_giveup = findViewById(R.id.button_giveup);
        constraintLayout3 = findViewById(R.id.constraintLayout3);
        constraintLayout4 = findViewById(R.id.constraintLayout4);

        // 뒤로가기 버튼 클릭
        ibutton_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCompleteChallenge) {
                    Intent intent = new Intent(getApplicationContext(), MainpageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                finish();
            }
        });

        // 리셋 버튼 클릭
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                util.showCustomDialog(new Util.OnConfirmListener() {
                    @Override
                    public void onConfirm(boolean isConfirmed) {
                        if (isConfirmed) {

                            OnTaskCompleted onResetTimeTaskCompleted = new OnTaskCompleted() {
                                @Override
                                public void onTaskCompleted(String result) {

                                    if (result.indexOf("RESET SUCCESS") != -1) {
                                        StyleableToast.makeText(StartRoomActivity.this, "리셋되었습니다.", R.style.successToast).show();
                                        recreate(); // 현재 액티비티를 다시 불러옴

                                    } else if (result.indexOf("NO SESSION") != -1) {
                                        StyleableToast.makeText(StartRoomActivity.this, "세션이 만료되었습니다.\n다시 로그인해주세요.", R.style.errorToast).show();

                                        // 로그인 액티비티 실행 후 그 외 모두 삭제
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        StyleableToast.makeText(StartRoomActivity.this, result, R.style.errorToast).show();
                                        return;
                                    }
                                }
                            };

                            HttpAsyncTask resetTimeTask = new HttpAsyncTask(StartRoomActivity.this, onResetTimeTaskCompleted);
                            String phpFile = "service.php";
                            String postParameters = "service=resettime&roomidx=" + roomidx;

                            resetTimeTask.execute(phpFile, postParameters, util.getSessionKey());
                        }
                    }
                }, "리셋하시겠습니까?", "confirm");
            }
        });

        // 포기하기 버튼 클릭
        button_giveup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String giveUporDelete;
                if (isCompleteChallenge) {
                    giveUporDelete = "삭제";
                } else {
                    giveUporDelete = "포기";
                }

                util.showCustomDialog(new Util.OnConfirmListener() {
                    @Override
                    public void onConfirm(boolean isConfirmed) {
                        if (isConfirmed) {
                            OnTaskCompleted onGiveUpTaskCompleted = new OnTaskCompleted() {
                                @Override
                                public void onTaskCompleted(String result) {

                                    if (result.indexOf("GIVEUP SUCCESS") != -1) {
                                        StyleableToast.makeText(StartRoomActivity.this, giveUporDelete + "하였습니다.", R.style.successToast).show();

                                        Intent intent = new Intent(getApplicationContext(), MainpageActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();

                                    } else if (result.indexOf("NO SESSION") != -1) {
                                        StyleableToast.makeText(StartRoomActivity.this, "세션이 만료되었습니다.\n다시 로그인해주세요.", R.style.errorToast).show();

                                        // 로그인 액티비티 실행 후 그 외 모두 삭제
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        StyleableToast.makeText(StartRoomActivity.this, result, R.style.errorToast).show();
                                        return;
                                    }
                                }
                            };

                            HttpAsyncTask giveUpTask = new HttpAsyncTask(StartRoomActivity.this, onGiveUpTaskCompleted);
                            String phpFile = "service.php";
                            String postParameters = "service=giveup&roomidx=" + roomidx;

                            giveUpTask.execute(phpFile, postParameters, util.getSessionKey());
                        }
                    }
                }, "챌린지를 " + giveUporDelete + "하시겠습니까?", "confirm");
            }
        });

        // 명언 뽑기
        {
            String[] sentences = {"\"자신을 이긴 사람은 세상을 이긴다.\"", "\"나의 내일은 오늘의 선택에 달려있다.\"", "\"나의 오늘은 누군가 그토록 바라는 미래이다.\"", "\"쾌락은 짧고 후회는 길다.\"", "\"가장 잘 견디는 자가 무엇이든지\n가장 잘 할 수 있는 사람이다.\"", "\"성공의 첫번째 법칙은 인내다.\"", "\"성공을 붙잡지 못하는 사람이\n가지지 못한 것은 재능이 아니라\n인내력이다.\"", "\"생각을 바꾸면 행동이 바뀌고,\n행동을 바꾸면 습관이 바뀌고,\n습관을 바꾸면 인격이 바뀌고,\n인격이 바뀌면 운명이 바뀐다.\"", "\"어제와 오늘의 나는 다르다. 그것이 성장이다.\"", "\"어제보다 더 나은 오늘을 위해.\""};
            Random random = new Random();
            int index = random.nextInt(sentences.length);
            String selectedSentence = sentences[index];
            textView_wisesaying.setText(selectedSentence);
        }

        // 방 정보 불러와서 변경
        {
            OnTaskCompleted onLoadStartRoomInfoTaskCompleted = new OnTaskCompleted() {
                @Override
                public void onTaskCompleted(String result) {
                    Boolean isJSON = util.isJson(result);

                    if (isJSON) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);

                            // 전달받은 JSON에서 roomInfo 부분만 추출
                            String roomType = jsonObject.getString("CHALLENGETYPE");
                            String roomTitle = jsonObject.getString("TITLE");
                            String roomContent = jsonObject.getString("CONTENT");
                            String roomStartTime = jsonObject.getString("STARTTIME");
                            String userRecentResetTime = jsonObject.getString("RECENTSTARTTIME");
                            String roomTargetDay = jsonObject.getString("ENDTIME");
                            String rankNumberOfPeopleAbove = jsonObject.getString("RANK");
                            String roomCurrentUserNum = jsonObject.getString("CURRENTUSERNUM");

                            // 방 정보 설정
                            int drawableId = StartRoomActivity.this.getResources().getIdentifier(roomType, "drawable", StartRoomActivity.this.getPackageName());
                            imageView_icon.setImageResource(drawableId);
                            textView_title.setText(roomTitle);
                            textView_content.setText(roomContent);


                            // 끝난 챌린지인지 진행중인 챌린지인지에 따라서 요소 변경
                            int dayOfCurrentTime = Integer.parseInt(util.DiffWithLocalTime(userRecentResetTime, "DAY"));
                            int endTime = Integer.parseInt(roomTargetDay);
                            if (dayOfCurrentTime >= endTime) {
                                // 만약 성공한(종료된) 챌린지라면
                                isCompleteChallenge = true;

                                textView_currentTime.setText(roomTargetDay + "일 0시간 0분 0초");
                                textView_TargetTime.setText("/ " + roomTargetDay + "일");
                                progress_achievement.setProgress(100);
                                textView_remainTime.setText("성공");

                                button_reset.setVisibility(View.GONE);
                                constraintLayout4.setVisibility(View.GONE);
                                button_giveup.setText("삭제하기");

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date forTransFormatDate = dateFormat.parse(roomStartTime);
                                CalendarDay challengeStartDay = CalendarDay.from(forTransFormatDate);

                                forTransFormatDate = dateFormat.parse(userRecentResetTime);
                                CalendarDay startDay = CalendarDay.from(forTransFormatDate);

                                // roomTargetDay를 startDay에 더한 값을 endDay로 설정
                                Calendar endCalendar = Calendar.getInstance();
                                endCalendar.set(startDay.getYear(), startDay.getMonth(), startDay.getDay());
                                endCalendar.add(Calendar.DATE, Integer.parseInt(roomTargetDay));
                                CalendarDay endDay = CalendarDay.from(endCalendar.getTime());

                                // 중간 날짜들의 디자인 변경 (위아래 여백이 있는 옅은 주황)
                                calendarView.addDecorators(
                                        new MySelectorDecorator(StartRoomActivity.this)
                                );

                                // 시작 날짜의 디자인 변경 (좌측 테두리 둥글게)
                                CustomDayDecorator customDayDecorator = new CustomDayDecorator(StartRoomActivity.this, startDay, true);
                                calendarView.addDecorators(customDayDecorator);

                                // 종료 날짜의 디자인 변경 (우측 테두리 둥글게)
                                CustomDayDecorator customDayDecorator2 = new CustomDayDecorator(StartRoomActivity.this, endDay, false);
                                calendarView.addDecorators(customDayDecorator2);

                                // 좌우 화살표 커스텀
                                Drawable leftArrow = ContextCompat.getDrawable(StartRoomActivity.this, R.drawable.ic_leftround);
                                calendarView.setLeftArrowMask(leftArrow);
                                Drawable rightArrow = ContextCompat.getDrawable(StartRoomActivity.this, R.drawable.ic_rightround);
                                calendarView.setRightArrowMask(rightArrow);

                                // 사용자가 선택하지 못하게 설정
                                calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);

                                // 날짜 설정
                                calendarView.setDateSelected(startDay, true);
                                calendarView.setDateSelected(endDay, true);
                                calendarView.selectRange(startDay, endDay);
                                calendarView.addDecorator(new EventDecorator(Color.parseColor("#DB4455"), Collections.singleton(challengeStartDay)));
                            } else {
                                isCompleteChallenge = false;

                                textView_currentTime.setText(util.DiffWithLocalTime(userRecentResetTime, "DHMS"));
                                updateTextViewTime(userRecentResetTime, Integer.parseInt(roomTargetDay)); // 계속해서 업데이트, 목표달성 순간 검사

                                if (Integer.parseInt(roomTargetDay) < 36500) {
                                    textView_TargetTime.setText("/ " + roomTargetDay + "일");

                                    long remaindays = Integer.parseInt(roomTargetDay) - Integer.parseInt(util.DiffWithLocalTime(userRecentResetTime, "DAY"));
                                    textView_remainTime.setText(remaindays + "일 남음");

                                    // 초로 계산하여 퍼센트를 표시해 더 정확한 퍼센트를 표기
                                    int currentTime_sec = Integer.parseInt(util.DiffWithLocalTime(userRecentResetTime, "SEC"));
                                    double targetTime_sec = Integer.parseInt(roomTargetDay) * 86400;
                                    double archivePercent = currentTime_sec / targetTime_sec * 100;
                                    progress_achievement.setProgress((int)archivePercent);
                                } else {
                                    textView_TargetTime.setText("/ 무제한");
                                    textView_remainTime.setText("");
                                    progress_achievement.setProgress(100);
                                    progress_achievement.setProgressColor(ContextCompat.getColor(StartRoomActivity.this, R.color.gray));
                                }

                                // 캘린더 설정
                                {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date forTransFormatDate = dateFormat.parse(roomStartTime);
                                    CalendarDay challengeStartDay = CalendarDay.from(forTransFormatDate);

                                    forTransFormatDate = dateFormat.parse(userRecentResetTime);
                                    CalendarDay startDay = CalendarDay.from(forTransFormatDate);

                                    Calendar today = Calendar.getInstance();
                                    CalendarDay endDay = CalendarDay.from(today);

                                    // 중간 날짜들의 디자인 변경 (위아래 여백이 있는 옅은 주황)
                                    calendarView.addDecorators(
                                            new MySelectorDecorator(StartRoomActivity.this)
                                    );

                                    // 시작 날짜의 디자인 변경 (좌측 테두리 둥글게)
                                    CustomDayDecorator customDayDecorator = new CustomDayDecorator(StartRoomActivity.this, startDay, true);
                                    calendarView.addDecorators(customDayDecorator);

                                    // 오늘 날짜의 디자인 변경 (Bold, 우측 테두리 둥글게), 만약 시작일과 현재일이 같다면 그냥 동그랗게 표시
                                    boolean isSingleDay;
                                    if (startDay.equals(endDay)) {
                                        isSingleDay = true;
                                    } else {
                                        isSingleDay = false;
                                    }
                                    OneDayDecorator oneDayDecorator = new OneDayDecorator(StartRoomActivity.this, isSingleDay);
                                    calendarView.addDecorators(
                                            oneDayDecorator
                                    );

                                    // 좌우 화살표 커스텀
                                    Drawable leftArrow = ContextCompat.getDrawable(StartRoomActivity.this, R.drawable.ic_leftround);
                                    calendarView.setLeftArrowMask(leftArrow);
                                    Drawable rightArrow = ContextCompat.getDrawable(StartRoomActivity.this, R.drawable.ic_rightround);
                                    calendarView.setRightArrowMask(rightArrow);

                                    // 사용자가 선택하지 못하게 설정
                                    calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);

                                    // 날짜 설정
                                    calendarView.setDateSelected(startDay, true);
                                    calendarView.setDateSelected(endDay, true);
                                    calendarView.selectRange(startDay, endDay);
                                    calendarView.addDecorator(new EventDecorator(Color.parseColor("#DB4455"), Collections.singleton(challengeStartDay))); // 챌린지 최초 시작시간 포인트 표시
                                }
                            }



                            // 랭킹 설정
                            if (Integer.parseInt(roomCurrentUserNum) > 1) {
                                // 2명 이상이서 하는 방이면 랭킹 표시
                                constraintLayout3.setVisibility(View.VISIBLE);
                                int userRank = Integer.parseInt(rankNumberOfPeopleAbove) + 1;
                                textView_rank.setText(String.valueOf(userRank) + " / " + roomCurrentUserNum);

                                // 랭킹보기 버튼 클릭
                                button_showrank.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(StartRoomActivity.this, RankingActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("roomidx", roomidx);
                                        intent.putExtra("roomtargetday", roomTargetDay);
                                        startActivity(intent);
                                    }
                                });
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                    } else if (result.indexOf("NO SESSION") != -1) {
                        StyleableToast.makeText(StartRoomActivity.this, "세션이 만료되었습니다.\n다시 로그인해주세요.", R.style.errorToast).show();

                        // 로그인 액티비티 실행 후 그 외 모두 삭제
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        StyleableToast.makeText(StartRoomActivity.this, result, R.style.errorToast).show();
                        return;
                    }
                }
            };


            HttpAsyncTask loadStartRoomInfoTask = new HttpAsyncTask(StartRoomActivity.this, onLoadStartRoomInfoTaskCompleted);
            String phpFile = "service.php";
            String postParameters = "service=getstartroominfos&roomidx=" + roomidx;

            loadStartRoomInfoTask.execute(phpFile, postParameters, util.getSessionKey());
        }
    }

    private void updateTextViewTime(String recentResetDate, int targetTime_day) {

        String viewDHMS = util.DiffWithLocalTime(recentResetDate, "DHMS");
        textView_currentTime.setText(viewDHMS);

        int currentTime_sec = Integer.parseInt(util.DiffWithLocalTime(recentResetDate, "SEC"));

        // 무한이 아닐경우 목표 달성순간 캐치를 위해 설정
        if (targetTime_day < 10000) {
            int targetTime_sec = targetTime_day * 86400;
            if (currentTime_sec >= targetTime_sec) {
                Log.d("StartRoomActivity", "Archive IT!!!!");
            }
        }

        // 1초마다 업데이트
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTextViewTime(recentResetDate, targetTime_day);
            }
        }, 1000);
    }
}