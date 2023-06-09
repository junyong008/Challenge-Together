package com.yjy.challengetogether.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.activity.LoginActivity;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.CustomProgressDialog;
import com.yjy.challengetogether.util.Util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;


public class MainWidget extends AppWidgetProvider {

    private Util util;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        util = new Util(context);

        String result = util.getOngoingChallenges();

        // 리스트뷰 Adapter 설정하여 업데이트
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_widget);

        // 위젯(최상단 레이아웃) 클릭시 어플 이동 설정
        Intent intent = new Intent(context, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.parentLayout, pendingIntent);

        if (TextUtils.isEmpty(result)) {
            // 아무항목도 없을경우 없음 메시지 표시
            views.setViewVisibility(R.id.textView_none, View.VISIBLE);
        } else {
            // 항목이 있을경우 메시지 가림
            views.setViewVisibility(R.id.textView_none, View.GONE);

            // RemoteViewsService 실행 등록
            Intent serviceIntent = new Intent(context, MyRemoteViewsService.class);
            serviceIntent.putExtra("result", result);
            serviceIntent.setData(Uri.fromParts("content", String.valueOf(System.currentTimeMillis()), null)); // 이 부분을 추가하여 Intent 객체를 새로 생성
            views.setRemoteAdapter(R.id.widget_listview, serviceIntent);
        }

        // 위젯 뷰 업데이트
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName testWidget = new ComponentName(context.getPackageName(), MainWidget.class.getName());

        int[] widgetIds = appWidgetManager.getAppWidgetIds(testWidget);

        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            if(widgetIds != null && widgetIds.length >0) {
                this.onUpdate(context, AppWidgetManager.getInstance(context), widgetIds);
            }
        }
    }


    public class HttpAsyncTask_Widget extends AsyncTask<String,Void,String> {

        private Context context;
        private String TAG = "HttpAsyncTask";
        private CustomProgressDialog customProgressDialog;
        private OnTaskCompleted listener;

        public HttpAsyncTask_Widget(Context context, OnTaskCompleted listener) {
            this.context = context;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "finalResult - " + result);
            listener.onTaskCompleted(result);
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = null;
            String postParameters = null;
            String sessionKey = null;

            try {
                Resources res = context.getResources();
                res.getString(R.string.SERVER_IP);

                serverURL = "http://" + res.getString(R.string.SERVER_IP) + "/" + (String) params[0];   // 서버 URL
                postParameters = (String) params[1];  // 전송 파라미터
                sessionKey = (String) params[2];  // 세션키
            } catch (RuntimeException e) { }

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                // 시간초과설정 및 메소드
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");

                // 세션키도 같이 들어오면 헤더에 세션키를 포함
                if (sessionKey != null) {
                    httpURLConnection.setRequestProperty("X-Session-ID", sessionKey);
                }

                httpURLConnection.connect(); // 연결

                // 결과 받고 인코딩 설정
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));

                // 객체 비우기
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode(); // 응답 코드 200, 404, 500 ...

                // 응답 헤더들을 읽고, 세션아이디가 헤더에 포함되면 세션아이디를 읽어옴
                Map<String, List<String>> headers = httpURLConnection.getHeaderFields();
                List<String> sessionIds = headers.get("X-Session-ID");
                String sessionId = null;

                if (sessionIds != null && sessionIds.size() > 0) {
                    sessionId = sessionIds.get(0);
                }


                InputStream inputStream;
                if(responseStatusCode == httpURLConnection.HTTP_OK) { // 만약 정상적인 응답코드라면
                    inputStream=httpURLConnection.getInputStream();
                }
                else {
                    inputStream = httpURLConnection.getErrorStream(); // 만약 정상적이지 않은 응답코드라면
                }

                // StringBuilder를 사용하여 수신되는 데이터를 저장한다.
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();

                String httpResult = sb.toString();

                // url / postParameters / responseStatusCode / sessionId / httpResult
                Log.d(TAG, "url - " + url + "\npostParameters - " + postParameters + "\nresponseStatusCode - " + responseStatusCode + "\nsessionId - " + sessionId + "\nhttpResult - " + httpResult);

                // 세션값을 받아오면 세션을 반환해주고, 아니라면 http결괏값을 반환
                if (sessionId != null) {
                    return sessionId;
                }
                else {
                    return httpResult;
                }
            } catch (Exception e) { // 에러 발생시
                return  new String("HttpAsyncTask ERROR - " + e.getMessage());
            }
        }
    }
}