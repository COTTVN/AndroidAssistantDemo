package net.java.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.format.Formatter;

import net.java.entity.AppBean;

import java.io.File;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018/3/12.
 */

public class MacUtil {
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    public static List<AppBean> getAllApk(Context ctx) {
        List<AppBean>appBeanList=new ArrayList<>();
        AppBean bean=null;
        PackageManager packageManager= ctx.getPackageManager();
        List<PackageInfo> list=packageManager.getInstalledPackages(0);
        for (PackageInfo p:list) {
            bean=new AppBean();
            bean.setAppIcon(p.applicationInfo.loadIcon(packageManager));
            bean.setAppName(packageManager.getApplicationLabel(p.applicationInfo).toString());
            bean.setAppPackageName(p.applicationInfo.packageName);
            bean.setApkPath(p.applicationInfo.sourceDir);
            bean.setAppVersionName(p.versionName);
            File file=new File(p.applicationInfo.sourceDir);
            long apkSize = file.length();
            bean.setAppSize(Formatter.formatFileSize(ctx,apkSize));
            int flags=p.applicationInfo.flags;
            //判断是否是属于系统的apk
            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0){
                bean.setSystem(true);
            }else {
                bean.setSd(true);
            }
            if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0){
                //那么当前安装的就是sd卡
                bean.setSd(true);
            }
            appBeanList.add(bean);

        }
        return appBeanList;
    }
}
