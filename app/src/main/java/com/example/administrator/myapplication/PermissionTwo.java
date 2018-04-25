package com.example.administrator.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.java.utils.SystemUtil;
import net.java.utils.TrafficUtils;

public class PermissionTwo extends AppCompatActivity{
    private static boolean isTrue=true;
    private static boolean opData=true;
    private TextView wifiShow;
    private TextView timeShow;
    private TextureView mTextureView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_two);
        wifiShow = findViewById(R.id.txtFlow);
        timeShow = findViewById(R.id.txtTime);
        mTextureView= findViewById(R.id.textureView);
    }

    public void weChat(View view){
        if(isTrue) {
            ((MyApp) getApplication()).setWechat(true);
            isTrue=false;
        }else{
            ((MyApp) getApplication()).setWechat(false);
            isTrue=true;
        }
    }

    public void mobData(View view){
        if(opData){
//            SystemUtil.toggleMobileData(this,true);
//            SystemUtil.openAPN(this);
            SystemUtil.setMobileData(0,true,this);
            opData=false;
        }else{
//            SystemUtil.toggleMobileData(this,false);
//            SystemUtil.closeAPN(this);
            SystemUtil.setMobileData(0,false,this);
            opData=true;
        }
    }

    public void getFlow(View view){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm!=null) {
            boolean isWifitrue = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
            boolean isGprstrue = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED;
            if(isWifitrue){
                //这个就是连接的无线网
                Toast.makeText(this,"当前连接的是无线网",Toast.LENGTH_SHORT).show();
                TrafficUtils wifi = new TrafficUtils(wifiShow,timeShow);
                wifi.action(this);
            }
            if(isGprstrue){
                //这个就是连接的数据网络
                Toast.makeText(this,"当前连接的是数据网络",Toast.LENGTH_SHORT).show();
            }
            if(!isGprstrue&&!isWifitrue){
                Toast.makeText(this,"当前无网络连接",Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void camera(View view){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
}
