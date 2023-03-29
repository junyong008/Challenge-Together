package com.yjy.challengetogether.activity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.HomeItem;
import com.yjy.challengetogether.etc.MyRemoteViewsService;
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
    private List<HomeItem> items;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_widget);

        // 위젯(최상단 레이아웃) 클릭시 어플 이동
        Intent intent = new Intent(context, MainpageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.parentLayout, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        util = new Util(context);

        try {
            OnTaskCompleted onLoadRoomTaskCompleted = new OnTaskCompleted() {
                @Override
                public void onTaskCompleted(String result) {

                    Boolean isJSON = util.isJson(result);

                    if (isJSON) {

                        if (!result.contains("roomList")) { // 방이 없으면 리턴
                            return;
                        }

                        // RemoteViewsService 실행 등록
                        Intent serviceIntent = new Intent(context, MyRemoteViewsService.class);
                        serviceIntent.putExtra("result", result);

                        RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.main_widget);
                        widget.setRemoteAdapter(R.id.widget_listview, serviceIntent);

                        // 위젯 뷰 업데이트
                        appWidgetManager.updateAppWidget(appWidgetIds, widget);
                    }
                }
            };

            HttpAsyncTask_Widget loadRoomTask = new HttpAsyncTask_Widget(context, onLoadRoomTaskCompleted);
            String phpFile = "service.php";
            String postParameters = "service=gethomefraginfos";

            loadRoomTask.execute(phpFile, postParameters, util.getSessionKey());
        } catch (Exception e) {
            Log.d("test", e.toString());
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
                serverURL = "http://3.37.234.141/" + (String) params[0];   // 서버 URL
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