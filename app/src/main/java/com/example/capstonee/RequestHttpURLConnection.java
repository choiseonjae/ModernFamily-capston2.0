package com.example.capstonee;

import android.content.ContentValues;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RequestHttpURLConnection {
    public String request(String _url, ContentValues _params){
        HttpURLConnection urlConn = null;
        StringBuffer sbParams = new StringBuffer();

        if(_params == null)
            sbParams.append("");
        else{
            boolean isAnd = false;
            String key;
            String value;

            for(Map.Entry<String, Object> parameter : _params.valueSet()){
                key = parameter.getKey();
                value = parameter.getValue().toString();

                // 파라미터가 두 개 이상이면 &를 붙인다.
                if(isAnd) sbParams.append("&");
                sbParams.append(key).append("=").append(value);

                // 2개 이상이면, isAnd를 true로 바꾸고 다음 루프부터 &를 붙인다.
                if(!isAnd)
                    if(_params.size() >= 2)
                        isAnd = true;
            }
        }
        try{
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();

            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Accept-Charset", "UTF-8");
            urlConn.setRequestProperty("Context-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            String strParams = sbParams.toString(); //sbParams에 정리한 파라미터를 스트링으로
            OutputStream os = urlConn.getOutputStream();
            os.write(strParams.getBytes(StandardCharsets.UTF_8));
            os.flush(); // 출력 바이트 모두 강제실행
            os.close(); // 자원 해제

            if(urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), StandardCharsets.UTF_8));

            String line;
            String page = "";

            while((line = reader.readLine()) != null)
                page += line;

            return page;
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(urlConn != null)
                urlConn.disconnect();
        }
        return null;
    }
}
