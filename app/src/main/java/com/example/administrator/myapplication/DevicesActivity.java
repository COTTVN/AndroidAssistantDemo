package com.example.administrator.myapplication;

import static junit.framework.Assert.*;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import static android.app.admin.DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED;

/**
 * Created by Administrator on 2018/3/13.
 */

public class DevicesActivity extends AppCompatActivity{


    /**
     * 激活组件的请求码
     */
    private static final int REQUEST_CODE_ACTIVE_COMPONENT = 1;

    /**
     * 设备安全管理服务，2.2之前需要通过反射技术获取
     */
    private DevicePolicyManager devicePolicyManager = null;
    /**
     * 对应自定义DeviceAdminReceiver的组件
     */
    private ComponentName componentName = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        setTranslucent(this);

        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this,MonitorSysReceiver.class);

        addBtnListener();
    }

    private void addBtnListener(){
        /**
         * 激活设备管理器
         */
        findViewById(R.id.btn_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdminActive()){
                    Toast.makeText(DevicesActivity.this, "设备管理器已激活", Toast.LENGTH_SHORT).show();
                }else {
                    // 打开管理器的激活窗口
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    // 指定需要激活的组件
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "(激活窗口中的描述信息)");
                    startActivityForResult(intent, REQUEST_CODE_ACTIVE_COMPONENT);
                }
            }
        });

        /**
         * 取消激活
         */
        findViewById(R.id.btn_cancel_active).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdminActive()){

                    devicePolicyManager.removeActiveAdmin(componentName);
                    Toast.makeText(DevicesActivity.this, "将触发deviceAdminReceiver.onDisabled", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(DevicesActivity.this, "设备管理未激活", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * 锁屏
         */
        findViewById(R.id.btn_lock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdminActive()){
                    devicePolicyManager.lockNow();
                }else {
                    Toast.makeText(DevicesActivity.this, "设备管理未激活", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * 禁止使用摄像头
         */
        findViewById(R.id.btn_setCameraDisabled).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdminActive()){
                    devicePolicyManager.setCameraDisabled(componentName,true);
                }
            }
        });

        /**
         * 启动摄像头
         */
        findViewById(R.id.btn_setCameraDisabled1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdminActive()){
                    devicePolicyManager.setCameraDisabled(componentName,false);
                }
            }
        });

        /**
         * 设置密码
         */
        findViewById(R.id.btn_change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isAdminActive()) {
                        devicePolicyManager.resetPassword("123456", 0);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        /**
         * 取消密码
         */
        findViewById(R.id.btn_cancel_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdminActive()){
                    try {
                        devicePolicyManager.resetPassword("", 0);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //清除锁屏
    @TargetApi(Build.VERSION_CODES.M)
    public boolean setKeyguardDisabled(@NonNull ComponentName admin, boolean disabled) {
        try {
            return devicePolicyManager.setKeyguardDisabled(admin, disabled);
        } catch (Exception re) {
            Log.w("Device", "Failed talking with device policy service", re);
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void test(View view){
        boolean isok=setKeyguardDisabled(componentName,true);
    }

    /**
     * 恢复出厂设置
     */
    public void wipe(){
        devicePolicyManager.wipeData(0);
    }
    /**
     * 清除sd卡数据
     */
    public void cleanData(){
        devicePolicyManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
    }
    /**
     * 设置密码相关
     */
    //只能使用PIN码与密码
    private static final int PASSWORD_QUALITY_NUMERIC = 0x20000;
    //只能使用密码
    private static final int PASSWORD_QUALITY_ALPHABETIC = 0x40000;
    //用户必须输入至少含有密码两者>数字和 字母（或其它符号）的字符。请注意质量常数是有序的
    private static final int PASSWORD_QUALITY_ALPHANUMERIC=0x00050000;
    public void pwdLen(View view){
        try {
            int length=devicePolicyManager.getPasswordMinimumLength(componentName);
            if(length!=5) {
            devicePolicyManager.setPasswordQuality(componentName,PASSWORD_QUALITY_ALPHANUMERIC);
                devicePolicyManager.setPasswordMinimumLength(componentName, 5);//密码最小长度
            }
//            assertEquals(5,devicePolicyManager.getPasswordMinimumLength(componentName));
            Intent intentPwd = new Intent("android.app.action.SET_NEW_PASSWORD");
            startActivity(intentPwd);
//            devicePolicyManager.setPasswordMinimumLetters(componentName,3);//包含字母数
            //setNetworkLoggingEnabled(ComponentName admin, boolean enabled)
            //如果设置为2，则表示新密码和前2次设置的不能相同
            devicePolicyManager.setPasswordHistoryLength(componentName,2);//历史记录次数
            devicePolicyManager.setPasswordExpirationTimeout(componentName,600000);//密码过期时间一周605000000  87000000
            devicePolicyManager.setMaximumTimeToLock(componentName, 600000);//锁屏时间10分钟
        }catch(Exception e){
            e.printStackTrace();
            Log.e("Device",e.getMessage());
        }
    }

    public void storageEncryption(View view){
        int a = setStorageEncryption(componentName,true);
        if (0x00000003 == a){
            Toast.makeText(this,"加密是有效的",Toast.LENGTH_SHORT).show();
        }else if(0x00000001 == a){
            Toast.makeText(this,"加密有效尚未支持",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DevicePolicyManager.ACTION_START_ENCRYPTION);
            startActivity(intent);
        }else if(0x00000004 == a){
            Toast.makeText(this,"加密处于活动状态，但用户尚未设置加密密钥",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DevicePolicyManager.ACTION_START_ENCRYPTION);
            startActivity(intent);
        }else if(0x00000000 == a){
            Toast.makeText(this,"加密不支持",Toast.LENGTH_SHORT).show();
        }
    }

    public void stopStorageEncry(View view){
        int a = setStorageEncryption(componentName,false);
    }

    //屏幕解锁尝试次数
    public void setMaximumFailedPasswordsForWipe(@NonNull ComponentName admin, int num) {
        if (devicePolicyManager != null) {
            try {
                devicePolicyManager.setMaximumFailedPasswordsForWipe(admin, num);
            } catch (Exception e) {
                Log.w("Device", "Failed talking with device policy service", e);
            }
        }
    }
    public int getCurrentFailedPasswordAttempts() {
        if (devicePolicyManager != null) {
            try {
                return devicePolicyManager.getCurrentFailedPasswordAttempts();
            } catch (Exception e) {
                Log.w("Device", "Failed talking with device policy service", e);
            }
        }
        return -1;
    }

    //设定密码保存的时间
    public void setPasswordExpirationTimeout(@NonNull ComponentName admin, long timeout) {
        if (devicePolicyManager != null) {
            try {
                devicePolicyManager.setPasswordExpirationTimeout(admin, timeout);
            } catch (Exception e) {
                Log.w("Device", "Failed talking with device policy service", e);
            }
        }
    }

    //禁止截屏
    public void setScreenCaptureDisabled(@NonNull ComponentName admin, boolean disabled) {
        if (devicePolicyManager != null) {
            try {
                devicePolicyManager.setScreenCaptureDisabled(admin, disabled);
            } catch (Exception e) {
                Log.w("Device", "Failed talking with device policy service", e);
            }
        }
    }

    //存储加密
    public int setStorageEncryption(@NonNull ComponentName admin, boolean encrypt) {
        if (devicePolicyManager != null) {
            return devicePolicyManager.setStorageEncryption(admin, encrypt);
        }
        return ENCRYPTION_STATUS_UNSUPPORTED;
    }
    //停用相机
    public void setCameraDisabled(){
        devicePolicyManager.setCameraDisabled(componentName, false);
    }

    /**
     * 判断该组建是否有系统管理员的权限（系统安全-设备管理器 中是否激活）
     * @return
     */
    private boolean isAdminActive(){
        return devicePolicyManager.isAdminActive(componentName);
    }

    /**
     * 用户是否点击激活或取消的回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ACTIVE_COMPONENT) {
            // 激活组件的响应
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "用户手动取消激活", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "已触发DeviceAdminReceiver.onEnabled", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public static void setTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }
}
