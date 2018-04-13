package net.java.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2018/3/5.
 */

public class commonServiceImpl implements commonServiceI{
    @Override
    public String getReponse(String strURL, String[] parameters) {
        //调用后台服务接口
        String result="";
        try {

            if(parameters.length>1){
                for(String param:parameters){
                    String newParam="/"+param;
                    strURL+=newParam;
                }
            }
            URL url = new URL(strURL);
            HttpURLConnection httpConn = (HttpURLConnection)
                    url.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            httpConn.disconnect();
            result=builder.toString();

        }catch (Exception e){
            e.printStackTrace();
            result="3";
        }
        return result;
    }
}
