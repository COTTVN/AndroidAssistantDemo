package com.example.administrator.myapplication;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.UserHandle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

import static net.java.utils.MacUtil.getMacAddr;

/**
 * Created by tzp on 2018/3/12.
 */

public class MonitorSysReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Toast.makeText(context, "设备管理器:已激活", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Toast.makeText(context, "设备管理器:未激活", Toast.LENGTH_SHORT).show();
    }


    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        Intent intent1 = context.getPackageManager().getLaunchIntentForPackage("com.android.settings");
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
        final DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        dpm.lockNow();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while(i<30){
                    dpm.lockNow();
                    try {
                        Thread.sleep(100);
                        i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return "这是一个取消激活回掉响应信息";
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
        Toast.makeText(context, "设备管理；密码已经改变", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);
        Toast.makeText(context, "设备管理：改变密码失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);
        Toast.makeText(context, "设备管理；改变密码成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPasswordExpiring(Context context, Intent intent) {
        super.onPasswordExpiring(context, intent);
        Toast.makeText(context, "设备管理；密码过期", Toast.LENGTH_SHORT).show();
    }

}
