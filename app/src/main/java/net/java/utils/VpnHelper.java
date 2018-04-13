package net.java.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class VpnHelper {
    public static void resetVpn(final Context context) {
        disconnect(context);//调用连接前先断开已连接的VPN，保证顺利连接我们要连接的VPN
        //断开当前VPN连接到再次连接VPN之间需要时间间隔，否则会连接失败。
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Object profile = getVpnProfile();
                connect(context, profile);
            }
        }, 500);
    }

    private static Object getVpnProfile() {
        try {
            Class clsKeyStore = Class.forName("android.security.KeyStore");
            Method methodGetInstance = clsKeyStore.getDeclaredMethod(
                    "getInstance", (Class[])null);
            methodGetInstance.setAccessible(true);
            Object objKeyStore = methodGetInstance.invoke(null, (Object[])null);

            Method methodSaw = clsKeyStore.getDeclaredMethod("saw",
                    String.class);
            methodSaw.setAccessible(true);
            String[] keys = (String[]) methodSaw.invoke(objKeyStore, "VPN_");

            Class clsVpnProfile = Class
                    .forName("com.android.internal.net.VpnProfile");
            Method methodDecode = clsVpnProfile.getDeclaredMethod("decode",
                    String.class, byte[].class);
            methodDecode.setAccessible(true);

            Method methodGet = clsKeyStore.getDeclaredMethod("get",
                    String.class);
            methodGet.setAccessible(true);

            Object byteArrayValue = methodGet.invoke(objKeyStore, "VPN_"
                    + keys[0]);//此处0表示连接VPN列表中的第一个VPN

            Object objVpnProfile = methodDecode.invoke(null, keys[0],
                    byteArrayValue);

            return objVpnProfile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class getVpnProfileClass() {
        try {
            return Class.forName("com.android.internal.net.VpnProfile");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void connect(Context context, Object profile) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Field fieldIConManager = null;

        try {
            fieldIConManager = cm.getClass().getDeclaredField("mService");
            fieldIConManager.setAccessible(true);
            Object objIConManager = fieldIConManager.get(cm);
            Class clsIConManager = Class.forName(objIConManager.getClass()
                    .getName());

            Class clsVpnProfile = Class
                    .forName("com.android.internal.net.VpnProfile");
            Method metStartLegacyVpn = clsIConManager.getDeclaredMethod(
                    "startLegacyVpn", clsVpnProfile);
            metStartLegacyVpn.setAccessible(true);

            metStartLegacyVpn.invoke(objIConManager, profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void disconnect(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Field fieldIConManager = null;

        try {
            fieldIConManager = cm.getClass().getDeclaredField("mService");
            fieldIConManager.setAccessible(true);
            Object objIConManager = fieldIConManager.get(cm);
            Class clsIConManager = Class.forName(objIConManager.getClass()
                    .getName());
            Method metPrepare = clsIConManager.getDeclaredMethod("prepareVpn",
                    String.class, String.class);
            metPrepare.invoke(objIConManager, "[Legacy VPN]", "[Legacy VPN]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
