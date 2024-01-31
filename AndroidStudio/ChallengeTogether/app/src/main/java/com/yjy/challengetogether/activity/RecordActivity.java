package com.yjy.challengetogether.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;
import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.Const;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RecordActivity extends AppCompatActivity {

    private ImageView ivbutton_back;
    private ImageView ivbutton_share;
    private ImageView ivbutton_info;
    private ImageView imageView_grade;
    private RoundCornerProgressBar progress_nextgrade;
    private TextView textView_nextgrade;
    private TextView textView_record;
    private TextView textView_trycount;
    private TextView textView_successcount;
    private TextView textView_resetcount;
    private com.yjy.challengetogether.util.Util util = new Util(RecordActivity.this);
    private ToolTipsManager mToolTipsManager;

    @Override
    public void onBackPressed() {
        finish();
        //overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        ivbutton_back = findViewById(R.id.ivbutton_back);
        ivbutton_share = findViewById(R.id.ivbutton_share);
        ivbutton_info = findViewById(R.id.ivbutton_info);
        imageView_grade = findViewById(R.id.imageView_grade);
        progress_nextgrade = findViewById(R.id.progress_nextgrade);
        textView_nextgrade = findViewById(R.id.textView_nextgrade);
        textView_record = findViewById(R.id.textView_record);
        textView_trycount = findViewById(R.id.textView_trycount);
        textView_successcount = findViewById(R.id.textView_successcount);
        textView_resetcount = findViewById(R.id.textView_resetcount);

        // 뒤로가기 버튼 클릭
        ivbutton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 공유 버튼 클릭
        ivbutton_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View shareView = getWindow().getDecorView().getRootView();
                shareView.setDrawingCacheEnabled(true);  //화면에 뿌릴때 캐시를 사용하게 한다

                //캐시를 비트맵으로 변환
                Bitmap screenBitmap = Bitmap.createBitmap(shareView.getDrawingCache());
                try {
                    File cachePath = new File(getApplicationContext().getCacheDir(), "images");
                    cachePath.mkdirs(); // don't forget to make the directory
                    FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
                    screenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();

                    File newFile = new File(cachePath, "image.png");
                    Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.yjy.challengetogether.fileprovider", newFile);

                    Intent Sharing_intent = new Intent(Intent.ACTION_SEND);
                    Sharing_intent.setType("image/png");
                    Sharing_intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    startActivity(Intent.createChooser(Sharing_intent, "Share image"));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // 안내(info) 버튼 클릭
        ivbutton_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 툴팁으로 기록이 어떤걸 의미하는지 상세 설명

                // 이전에 보여진 툴팁이 있다면 제거
                if (mToolTipsManager != null) {
                    mToolTipsManager.findAndDismiss(ivbutton_info);
                }

                mToolTipsManager = new ToolTipsManager();
                ToolTip.Builder builder = new ToolTip.Builder(RecordActivity.this, ivbutton_info, findViewById(R.id.ConstraintLayout_parent), "도전 횟수는 무제한 도전을 포함한 횟수에요!", ToolTip.POSITION_ABOVE);
                builder.setBackgroundColor(getResources().getColor(R.color.black));
                builder.setTextAppearance(R.style.TooltipTextAppearance);
                mToolTipsManager.show(builder.build());

                // 2초 후에 툴팁을 숨기기
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mToolTipsManager.findAndDismiss(ivbutton_info);
                    }
                }, 2000);
            }
        });

        OnTaskCompleted onLoadRecordTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                Boolean isJSON = util.isJson(result);

                if (isJSON) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        long record_bestTime = jsonObject.getLong("BESTTIME");
                        String record_tryCount = jsonObject.getString("TRYCOUNT");
                        String record_successCount = jsonObject.getString("SUCCESSCOUNT");
                        String record_resetCount = jsonObject.getString("RESETCOUNT");

                        textView_record.setText(util.secondsToDHMS(record_bestTime, "DH"));
                        textView_trycount.setText(record_tryCount);
                        textView_successcount.setText(record_successCount);
                        textView_resetcount.setText(record_resetCount);

                        if (record_bestTime >= Const.MASTER_SECONDS) {
                            imageView_grade.setImageResource(R.drawable.ic_master);
                            progress_nextgrade.setProgress(100);
                            textView_nextgrade.setText("최고 등급");
                        } else if (record_bestTime >= Const.DIAMOND_SECONDS) {
                            imageView_grade.setImageResource(R.drawable.ic_diamond);
                            progress_nextgrade.setProgress((int)((double)(record_bestTime - Const.DIAMOND_SECONDS) / (Const.MASTER_SECONDS - Const.DIAMOND_SECONDS) * 100));

                            long requireSecToNextGrade = Const.MASTER_SECONDS - record_bestTime;
                            long requireDayToNextGrade = requireSecToNextGrade / (24 * 60 * 60) + 1;
                            textView_nextgrade.setText("다음 등급까지 " + requireDayToNextGrade + "일 남음");
                        } else if (record_bestTime >= Const.PLATINUM_SECONDS) {
                            imageView_grade.setImageResource(R.drawable.ic_platinum);
                            progress_nextgrade.setProgress((int)((double)(record_bestTime - Const.PLATINUM_SECONDS) / (Const.DIAMOND_SECONDS-Const.PLATINUM_SECONDS) * 100));

                            long requireSecToNextGrade = Const.DIAMOND_SECONDS - record_bestTime;
                            long requireDayToNextGrade = requireSecToNextGrade / (24 * 60 * 60) + 1;
                            textView_nextgrade.setText("다음 등급까지 " + requireDayToNextGrade + "일 남음");
                        } else if (record_bestTime >= Const.GOLD_SECONDS) {
                            imageView_grade.setImageResource(R.drawable.ic_gold);
                            progress_nextgrade.setProgress((int)((double)(record_bestTime - Const.GOLD_SECONDS) / (Const.PLATINUM_SECONDS - Const.GOLD_SECONDS) * 100));

                            long requireSecToNextGrade = Const.PLATINUM_SECONDS - record_bestTime;
                            long requireDayToNextGrade = requireSecToNextGrade / (24 * 60 * 60) + 1;
                            textView_nextgrade.setText("다음 등급까지 " + requireDayToNextGrade + "일 남음");
                        } else if (record_bestTime >= Const.SILVER_SECONDS) {
                            imageView_grade.setImageResource(R.drawable.ic_silver);
                            progress_nextgrade.setProgress((int)((double)(record_bestTime - Const.SILVER_SECONDS) / (Const.GOLD_SECONDS - Const.SILVER_SECONDS) * 100));

                            long requireSecToNextGrade = Const.GOLD_SECONDS - record_bestTime;
                            long requireDayToNextGrade = requireSecToNextGrade / (24 * 60 * 60) + 1;
                            textView_nextgrade.setText("다음 등급까지 " + requireDayToNextGrade + "일 남음");
                        } else {
                            imageView_grade.setImageResource(R.drawable.ic_bronze);
                            progress_nextgrade.setProgress((int)((double)record_bestTime / Const.SILVER_SECONDS * 100));

                            long requireSecToNextGrade = Const.SILVER_SECONDS - record_bestTime;
                            long requireDayToNextGrade = requireSecToNextGrade / (24 * 60 * 60) + 1;
                            textView_nextgrade.setText("다음 등급까지 " + requireDayToNextGrade + "일 남음");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    util.checkHttpResult(result);
                }
            }
        };

        HttpAsyncTask loadRecordTask = new HttpAsyncTask(RecordActivity.this, onLoadRecordTaskCompleted);
        String phpFile = "service 1.1.0.php";
        String postParameters = "service=getrecordactivityinfos";

        loadRecordTask.execute(phpFile, postParameters, util.getSessionKey());
    }
}