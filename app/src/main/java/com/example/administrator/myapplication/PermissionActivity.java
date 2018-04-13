package com.example.administrator.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import net.java.utils.BlueToothManager;
import net.java.utils.ScreenShot;
import net.java.utils.ScreenShotListenManager;
import net.java.utils.Telephone;
import net.java.utils.WifiListener;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;

import static android.content.ContentValues.TAG;

public class PermissionActivity extends AppCompatActivity{
    private boolean isdis=false;
    private boolean iswifi=false;
    private static boolean  isStopBlueToothWhite=true;
    private static boolean  isStopWifiWhitelist=true;
    private static boolean  isStopPhoneWhite=true;
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;

    //截屏
    private int result = 0;
    private Intent intent = null;
    private int REQUEST_MEDIA_PROJECTION = 1;
    private static MediaProjectionManager mMediaProjectionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        mMediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    public void click(View view)throws SocketException{
        if(view.getId() == R.id.btnBlueToothOff){
            blueTooth(0);
        }else if(view.getId() == R.id.btnBlueToothOn){
            blueTooth(1);
        }else if(view.getId() == R.id.btnWIFIOff){
            wifi(0);
        }else if(view.getId() == R.id.btnWifiOn){
            wifi(1);
        }else if(view.getId() == R.id.btnScreenOff){
            screen(0);
        }else if(view.getId() == R.id.btnScreenOn){
            screen(1);
        }else if(view.getId() == R.id.btnScreenshot){
            shot();
        }else if(view.getId() == R.id.btnBlueWhiteOn){
            //开启
            blueWhiteList();
        }else if(view.getId() == R.id.btnOwnWifiOn){
            wifiWhiteList();
        }else if(view.getId() == R.id.btnPhoneWhiteOn){
            phoneWhite();
        }
    }

    //蓝牙  0:1?禁用:启用
    private void blueTooth(int type){
        BlueToothManager blue = new BlueToothManager(this);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(0 == type){
            if(mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
            }
            blue.startListener();
            isdis=true;
        }else if(1 == type){
            if(isdis) {
                blue.stopListener();
            }
            if(!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
            isdis=false;
        }
    }

    //wifi
    private void wifi(int type)throws SocketException{
        WifiListener wifi = new WifiListener(PermissionActivity.this);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager!=null) {
            if (0 == type) {
                wifiManager.setWifiEnabled(false);
                wifi.stratSwitch();
                iswifi=true;
            }else if(1 == type){
                if(iswifi) {
                    wifi.stopSwitch();
                }
                wifiManager.setWifiEnabled(true);
                iswifi=false;
//--------------------------------------------------------------------------------------------------
//                List<WifiConfiguration> wifiConfigLists=wifiManager.getConfiguredNetworks();
//                for(WifiConfiguration config:wifiConfigLists){
//                    Log.i(TAG,"wifi网络配置信息："+config.SSID+"  "+config.hiddenSSID);
//                }
            }
        }
    }

    //禁止截屏
    private void screen(int type){
        Window window = getWindow();
        //ScreenShot screenShot = new ScreenShot(this);
        ScreenShotListenManager manager = ScreenShotListenManager.newInstance(this);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            // 请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_CODE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (0 == type) {
                window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
                manager.setListener(
                        new ScreenShotListenManager.OnScreenShotListener() {
                            public void onShot(String imagePath) {
                                //删除截屏
                                File file = new File(imagePath);
                                boolean status = file.delete();
                                if(status) {
                                    Toast.makeText(PermissionActivity.this, "该设备已被禁止截屏", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
                manager.startListen();
                //screenShot.start();
            } else if (1 == type) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                manager.stopListen();
                //screenShot.stop();
                Toast.makeText(this, "该设备已允许截屏", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //截屏
    private void shot(){
        startIntent();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopService();
            }
        }, 1600);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startIntent() {
        if (intent != null && result != 0) {
            ((MyApp) getApplication()).setResultCode(result);
            ((MyApp) getApplication()).setIntent(intent);
            Intent intent = new Intent(getApplicationContext(), ScreenShotService.class);
            startService(intent);
        } else {
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
            ((MyApp) getApplication()).setmMediaProjectionManager(mMediaProjectionManager);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                result = resultCode;
                intent = data;
                ((MyApp) getApplication()).setResultCode(resultCode);
                ((MyApp) getApplication()).setIntent(data);
                Intent intent = new Intent(getApplicationContext(), ScreenShotService.class);
                startService(intent);
//                finish();
            }
        }
    }

    private void stopService(){
        Intent intent = new Intent(getApplicationContext(), ScreenShotService.class);
        stopService(intent);
    }

    private void blueWhiteList(){
        BlueToothManager blue = new BlueToothManager(this);
        if(isStopBlueToothWhite) {
            blue.startBlueWhiteListener();
            isStopBlueToothWhite=false;
        }else{
            blue.stopBlueWhiteListener();
            isStopBlueToothWhite=true;
        }
    }

    private void wifiWhiteList(){
        WifiListener wifi = new WifiListener(this);
        if(isStopWifiWhitelist) {
            wifi.startWifiWhiteLimit();
            isStopWifiWhitelist=false;
        }else{
            wifi.stopWifiWhiteLimit();
            isStopWifiWhitelist=true;
        }
    }

    private void phoneWhite(){
        Telephone phone = new Telephone(this);
        if(isStopPhoneWhite) {
            phone.startPhone();
            isStopPhoneWhite=false;
        }else{
            phone.stopPhone();
            isStopPhoneWhite=true;
        }
    }

}
