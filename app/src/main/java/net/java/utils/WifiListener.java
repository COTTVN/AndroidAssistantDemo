package net.java.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class WifiListener {
    private Context ctx;
    private static BroadcastReceiver mwifiBroadcastReceiver;
    private static BroadcastReceiver mwifiWhiteLimitListener;
    public WifiListener(Context ctx){
        this.ctx=ctx;
    }

    public void stratSwitch(){
        // 监听wifi状态广播
        mwifiBroadcastReceiver = new BroadcastReceiver() {
            private void disabled(){
                WifiManager wifiManager = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if(wifiManager!=null){
                    wifiManager.setWifiEnabled(false);
                }
            }

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String action=intent.getAction();
                    if(action!=null&&WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
//                         WIFI_STATE_DISABLED    WLAN已经关闭
//                         WIFI_STATE_DISABLING   WLAN正在关闭
//                         WIFI_STATE_ENABLED     WLAN已经打开
//                         WIFI_STATE_ENABLING    WLAN正在打开
//                         WIFI_STATE_UNKNOWN     未知
                        int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                        switch (state) {
                            case WifiManager.WIFI_STATE_ENABLING:
                                Log.d(TAG, "WLAN正在打开中");
                                disabled();
                                break;
                            case WifiManager.WIFI_STATE_ENABLED:
                                Log.d(TAG, "WLAN已经打开，正在关闭");
                                disabled();
                                break;
                        }
                    }
                }
            }
        };

        IntentFilter myIntentFilter;
        myIntentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        this.ctx.registerReceiver(mwifiBroadcastReceiver,myIntentFilter);
    }

    public void stopSwitch(){
        Log.d(TAG, "注销该WIFI广播");
        try {
            this.ctx.unregisterReceiver(mwifiBroadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startWifiWhiteLimit(){
        mwifiWhiteLimitListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String action=intent.getAction();
                    if(action!=null&&WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                        Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                        WifiManager wifiManager = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
//                        String bssid = intent.getStringExtra(WifiManager.EXTRA_BSSID);
                        if(parcelableExtra!=null&&wifiManager!=null){
                            NetworkInfo.State state = ((NetworkInfo) parcelableExtra).getState();
                            if(state==NetworkInfo.State.CONNECTED){
                                String ssid="\"SSGM-COM-2.4G\"";
                                String bssid="40:a5:ef:78:e0:22";
                                String con_ssid=wifiInfo.getSSID();
                                String con_bssid=wifiInfo.getBSSID();
                                if (ssid.equals(con_ssid) && bssid.equals(con_bssid)) {
                                    Log.i(TAG, "是指定wifi");
                                } else {
                                    Log.i(TAG, "这不是指定wifi");
                                    wifiManager.disableNetwork(wifiInfo.getNetworkId());
                                }
                            }
                        }
                    }
                }
            }
        };

        IntentFilter myIntentFilter;
        myIntentFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        this.ctx.registerReceiver(mwifiWhiteLimitListener,myIntentFilter);
    }

    public void stopWifiWhiteLimit(){
        try {
            this.ctx.unregisterReceiver(mwifiWhiteLimitListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
