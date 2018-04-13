package net.java.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;
import static android.content.Context.TELEPHONY_SERVICE;
import static android.content.Context.TELEPHONY_SUBSCRIPTION_SERVICE;

public class SystemUtil {
    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return  语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return  手机IMEI
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String[] getIMEI(Context ctx) {
        String[] info={"null","null"};
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            if (tm != null) {
                for (int slot = 0; slot < tm.getPhoneCount(); slot++) {
                    String imei = tm.getDeviceId(slot);
                    Log.i(TAG,"手机IMEI：-----"+imei);
                    info[slot]=imei;
                }
            }
        }
        return info;
    }

    /**
     *
     * @return 手机IMSI
     */
    public static String getIMSI(Context ctx){
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (tm != null) {
                String _imsi = tm.getSubscriberId();
                if (_imsi != null && !_imsi.equals("")) {
                    return _imsi;
                }
            }
        }
        return null;
    }

    //获取系统版本
    public static String[] getVersion(){
        String[] version={"null","null","null","null"};
        String str1 = "/proc/version";
        String str2;
        String[] arrayOfString;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            version[0]=arrayOfString[2];//KernelVersion
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        version[1] = Build.VERSION.RELEASE;// firmware version
        version[2]=Build.MODEL;//model
        version[3]=Build.DISPLAY;//system version
        return version;
    }

    //获取CPU信息
    public static String[] getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2="";
        String[] cpuInfo={"",""};
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cpuInfo;
    }

    //获取内存
    public static String getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2="";
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            if ((str2 = localBufferedReader.readLine()) != null) {
                Log.i(TAG, "---" + str2);
                return str2;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取剩余内存信息
    public static long getAvailMemory(Context ctx) {
        ActivityManager am = (ActivityManager)ctx.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        if(am==null){
            return 0;
        }
        am.getMemoryInfo(mi);
        return mi.availMem;
    }

    //设备详细信息
    public static Map<String,String> deviceIndo(){
        Map<String,String> map=new HashMap<>();
        Field[] fields = Build.class.getDeclaredFields();
        //遍历字段名数组
        for (Field field : fields) {
            try {
                //将字段都设为public可获取
                field.setAccessible(true);
                //filed.get(null)得到的即是设备信息
                map.put(field.getName(), field.get(null).toString());
                Log.d("CrashHandler", field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }
    //使用时需要注册该监听服务
    //registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    public BroadcastReceiver batteryReceiver=new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            //  level加%就是当前电量了
        }
    };

    public static String getSim2IMSI(Context ctx){
        String imsi=null;
        TelephonyManager tm =(TelephonyManager) ctx.getSystemService(TELEPHONY_SERVICE);
        try {
            Method getSubId=TelephonyManager.class.getMethod("getSubscriberId",int.class);
            @SuppressLint("WrongConstant") SubscriptionManager sm = (SubscriptionManager)ctx.getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE);
            imsi=(String)getSubId.invoke(tm,sm.getActiveSubscriptionInfoForSimSlotIndex(1).getSubscriptionId());
            return imsi;
        }catch (Exception e){
            e.printStackTrace();
        }
        return imsi;
    }

    //判断手机是否开启调试
    public static boolean isAdb(Context ctx){
        return (Settings.Secure.getInt(ctx.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) > 0);
    }

    //判断手机是否root
    public static boolean isRoot() {
        String binPath = "/system/bin/su";
        String xBinPath = "/system/xbin/su";

        if (new File(binPath).exists() && isCanExecute(binPath)) {
            return true;
        }
        if (new File(xBinPath).exists() && isCanExecute(xBinPath)) {
            return true;
        }
        return false;
    }
    private static boolean isCanExecute(String filePath) {
        java.lang.Process process = null;
        try {
            process = Runtime.getRuntime().exec("ls -l " + filePath);
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str = in.readLine();
            if (str != null && str.length() >= 4) {
                char flag = str.charAt(3);
                if (flag == 's' || flag == 'x')
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }
}