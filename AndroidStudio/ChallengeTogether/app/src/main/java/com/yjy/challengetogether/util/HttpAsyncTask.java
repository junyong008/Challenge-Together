package com.yjy.challengetogether.util;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;

import com.yjy.challengetogether.etc.OnTaskCompleted;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class HttpAsyncTask extends AsyncTask<String,Void,String> {

    private Context context;
    private String TAG = "HttpAsyncTask";
    private CustomProgressDialog customProgressDialog;
    private OnTaskCompleted listener;

    public HttpAsyncTask(Context context, OnTaskCompleted listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // 진행 프로그레스 시작
        customProgressDialog = new CustomProgressDialog(context);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customProgressDialog.show();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // 프로그레스 종료
        customProgressDialog.dismiss();
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
