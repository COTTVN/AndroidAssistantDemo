package com.example.administrator.myapplication;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import net.java.entity.StorageInfo;
import net.java.utils.StorageUtils;
import net.java.utils.SystemUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppInfoActivity extends AppCompatActivity{
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appinfo);
        setConfigure();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setConfigure(){
        try {
            TextView appInfo = findViewById(R.id.appInfo);
            Map<String, String> deviceIndo = SystemUtil.deviceIndo();
            String devices = getSeviceInfoToString(deviceIndo);
            String[] imeis = SystemUtil.getIMEI(this);
            String imsi = SystemUtil.getIMSI(this);
            String imsi2 = SystemUtil.getSim2IMSI(this);
            String[] sys_version = SystemUtil.getVersion();
            String battery = batteryInfo();
            String storage = storageInfoList();

            String result =
                            "IMEI--------" + imeis[0] + "  " + imeis[1] + "\r\n" +
                            "IMSI--------" + imsi +"  "+imsi2+ "\r\n" +
                            "是否开启调试-------"+SystemUtil.isAdb(this)+"\r\n"+
                            "是否ROOT手机-------"+SystemUtil.isRoot()+"\r\n"+
                            "系统版本---------------------------------------------------\r\n" + sys_version[0] + " " + sys_version[1] + " " + sys_version[2] + " " + sys_version[3] + "\r\n" +
                            "电池信息---------------------------------------------------\r\n" + battery + "\r\n" +
                            "存储信息---------------------------------------------------\r\n" + storage + "\r\n" +
                            "详细信息---------------------------------------------------\r\n" + devices;
            appInfo.setText(result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getSeviceInfoToString(Map<String,String> map){
        String infol="";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            String str=entry.getKey()+"--------"+entry.getValue()+"\r\n";
            infol=infol.concat(str);
        }
        return infol;
    }

    //电池信息
    private String batteryInfo(){
        Intent batteryInfoIntent = getApplicationContext()
                .registerReceiver( null ,
                        new IntentFilter( Intent.ACTION_BATTERY_CHANGED ) ) ;
        if(batteryInfoIntent!=null) {
            int level = batteryInfoIntent.getIntExtra("level", 0);//电量（0-100）
            int status = batteryInfoIntent.getIntExtra("status", 0);
            int health = batteryInfoIntent.getIntExtra("health", 1);
            boolean present = batteryInfoIntent.getBooleanExtra("present", false);
            int scale = batteryInfoIntent.getIntExtra("scale", 0); //规格
            int plugged = batteryInfoIntent.getIntExtra("plugged", 0);//插口
            int voltage = batteryInfoIntent.getIntExtra("voltage", 0);//电压
            int temperature = batteryInfoIntent.getIntExtra("temperature", 0); // 温度的单位是10℃
            String technology = batteryInfoIntent.getStringExtra("technology");//技术

            String battery="电量------"+level+"  刻度------"+scale+"\r\n电压------"+voltage+"  温度------"+temperature+"\r\n";
            return battery;
        }
        return "未获取到";
    }
    //手机存储设备的状态信息
    private String storageInfoList() throws Exception{
        String storage="内存信息--------------------------\r\n"+"总内存："+StorageUtils.getRAM(this)+"  可用大小："+ Formatter.formatFileSize(this,SystemUtil.getAvailMemory(this))+"\r\n";
        storage+="内置SD卡---------------------------\r\n"+StorageUtils.getOut(this);
//        if(StorageUtils.isExternalStorageAvailable()){
//            storage+="\r\n外置SD卡--------------------------\r\n"+StorageUtils.getSD(this);
//        }
        return storage;
    }

}
