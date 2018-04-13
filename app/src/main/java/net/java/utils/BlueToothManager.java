package net.java.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class BlueToothManager {
    private Context ctx;
    private static BroadcastReceiver  blueTooth;
    private static BroadcastReceiver  blueWhiteList;
    public BlueToothManager(Context ctx){
        this.ctx=ctx;
    }

    public void startListener(){
        blueTooth=new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                String action = intent.getAction();
                if(action != null) {
//                    if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
//                        //监听蓝牙设备连接状态
//                        Log.e("blueTooth", "正在监听蓝牙设备连接状态");
//                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
//                        if(state==BluetoothAdapter.STATE_ON){
//                            BluetoothAdapter.getDefaultAdapter().disable();
//                        }
//                    }else
                    if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                        //监听蓝牙打开与否的状态
                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

                        switch (state) {
                            case BluetoothAdapter.STATE_TURNING_ON:
                                Log.e("blueTooth", "蓝牙模块正在打开");
                                BluetoothAdapter.getDefaultAdapter().disable();
                                break;
                            case BluetoothAdapter.STATE_ON:
                                Log.e("blueTooth", " 蓝牙模块处于开启状态");
                                BluetoothAdapter.getDefaultAdapter().disable();
                                break;
                        }
                    }
                }
            }
        };

        IntentFilter filter1,filter2,filter3;
        filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//        filter2 = new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
//        filter3 = new IntentFilter("android.bluetooth.BluetoothAdapter.STATE_ON");
        ctx.registerReceiver(blueTooth,filter1);
//        ctx.registerReceiver(blueTooth,filter2);
//        ctx.registerReceiver(blueTooth,filter3);
    }

    public void stopListener(){
        ctx.unregisterReceiver(blueTooth);
    }

    public void startBlueWhiteListener(){
        blueWhiteList=new BroadcastReceiver() {
            //得到配对的设备列表，清除已配对的设备
            private void removePairDevice(String address){
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if(mBluetoothAdapter!=null){
                    Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
                    for(BluetoothDevice device : bondedDevices ){
                        Log.i("蓝牙",device.getName()+":::"+device.getAddress());
                        if(!address.equals(device.getAddress())){
                            unpairDevice(device);
                        }
                    }
                }

            }
            //反射来调用BluetoothDevice.removeBond取消设备的配对
            private void unpairDevice(BluetoothDevice device) {
                try {
                    Method m = device.getClass()
                            .getMethod("removeBond", (Class[]) null);
                    m.invoke(device, (Object[]) null);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            //反射来调用BluetoothDevice.cancelPairingUserInput取消用户输入
            private void cancelPairingUserInput(BluetoothDevice device){
                try{
                    Method m = device.getClass().
                            getMethod("cancelPairingUserInput", (Class[]) null);
                    cancelBondProcess(device);
                    m.invoke(device,(Object[])null);
                }catch (Exception e){
                    Log.e(TAG,e.getMessage());
                }
            }
            //反射来调用BluetoothDevice.cancelBondProcess取消配对弹窗
            private void cancelBondProcess(BluetoothDevice device){
                try{
                    Method m = device.getClass().
                            getMethod("cancelBondProcess", (Class[]) null);
                    m.invoke(device,(Object[])null);
                }catch (Exception e){
                    Log.e(TAG,e.getMessage());
                }
            }
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action != null) {
                    if(action.equals("android.bluetooth.device.action.PAIRING_REQUEST")){
                        String address="3C:95:09:62:59:6C";
                        BluetoothDevice btDevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if(!address.equals(btDevice.getAddress())){
                            cancelPairingUserInput(btDevice);
                        }
                    }else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                        //监听蓝牙打开与否的状态
                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                        String address="3C:95:09:62:59:6C";
                        switch (state) {
                            case BluetoothAdapter.STATE_ON:
                                Log.e("blueTooth", "蓝牙模块已经打开");
                                removePairDevice(address);
                                break;
                            case BluetoothAdapter.STATE_TURNING_ON:
                                Log.e("blueTooth", "蓝牙模块正在打开中");
                                removePairDevice(address);
                                break;
                        }
                    }
                }
            }
        };

        IntentFilter intentFilter;
        intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);//连接蓝牙，断开蓝牙
//        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED); //配对时，发起连接
//        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED); //配对开始时，配对成功时
        intentFilter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");//配对请求
        ctx.registerReceiver(blueWhiteList,intentFilter);
    }
    public void stopBlueWhiteListener(){
        ctx.unregisterReceiver(blueWhiteList);
    }


}
