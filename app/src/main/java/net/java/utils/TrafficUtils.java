package net.java.utils;

import android.content.Context;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class TrafficUtils {
    private long totalWifi;
    private static String wifiTraffic ;
    private static long wf =0;
    private static int wifiStr;
    private DecimalFormat df = new DecimalFormat(".##");
    private Handler handler1= null;
    private static int hours = 0,minutes = 0,seconds = 0;
    private static TextView wifiFlowShow;
    private static TextView mobTimeShow;

    public TrafficUtils(TextView wifi,TextView time){
        this.wifiFlowShow=wifi;
        this.mobTimeShow=time;
    }

    static class MyHandler extends Handler{
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            if(msg.what == 1){
                String info="WIFI上传+下载使用了：" + wifiTraffic;
                wifiFlowShow.setText(info);
            }
        }
    }
    static class MyHandler1 extends Handler{
        public void handleMessage(Message msg1){
            super.handleMessage(msg1);
            if(msg1.what == 1){
                Log.i("Wifi-Time",(new DecimalFormat("00").format(hours) + ":" +
                        new DecimalFormat("00").format(minutes) + ":" + new DecimalFormat("00").format(seconds)));
                String time = new DecimalFormat("00").format(hours) + ":" +
                        new DecimalFormat("00").format(minutes) + ":" + new DecimalFormat("00").format(seconds);
                mobTimeShow.setText(time);
            }
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void action(Context ctx){
        WifiManager wifiManager = (WifiManager)ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager!=null) {
            wifiStr = wifiManager.getWifiState();
        }
        if(wifiStr == 3){
            final Handler handler = new MyHandler();
            if(wifiStr == 3){
                handler1 = new MyHandler1();
            }
            new Thread(new Runnable() {
                public void run(){
                    for(int i = 0;;i++){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        seconds++;
                        Message msg1 = new Message();
                        msg1.what = 1;
                        handler1.sendMessage(msg1);
                        if(seconds == 60){
                            seconds = 0;
                            minutes++;
                            if(minutes == 60){
                                minutes = 0;
                                hours++;
                            }
                        }
                    }

                }
            }).start();

            new Thread(new Runnable() {

                @Override
                public void run() {
                    while(true){
                        double currentTime = System.currentTimeMillis();
                        //	getWifiTraffic(currentTime);
                        long totalWifi01 = getWifiTraffic(currentTime);;
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        double frontTime = System.currentTimeMillis();
                        //	getWifiTraffic(frontTime);
                        long totalWifi02 = getWifiTraffic(frontTime);
                        long errorTraffic = totalWifi02 - totalWifi01;
//                        if(errorTraffic < 512){
//                            errorTraffic = 1;
//                        }

                        wf += errorTraffic;
                        wifiTraffic = getPrintSize(wf);
                        //	Log.i("使用的流量", wifiTraffic + "");
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);

                    }
                }
            }).start();
        }
    }

    private long getWifiTraffic(double time){
        long rtotalGprs = TrafficStats.getTotalRxBytes();
        long ttotalGprs = TrafficStats.getTotalTxBytes();
        long rgprs = TrafficStats.getMobileRxBytes();
        long tgprs = TrafficStats.getMobileTxBytes();
        long rwifi = rtotalGprs - rgprs;
        long twifi = ttotalGprs - tgprs;
        totalWifi = rwifi + twifi;
        Log.i("手机移动流量","（"+time+"）上传："+getPrintSize(tgprs)+" 下载："+getPrintSize(rgprs));
        Log.i("无线网络流量","（"+time+"）上传："+getPrintSize(twifi)+" 下载："+getPrintSize(rwifi));
        return totalWifi;
        //totalWifi = rtotalGprs + ttotalGprs;
    }
    private static String getPrintSize(long size) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
        }
    }
}
