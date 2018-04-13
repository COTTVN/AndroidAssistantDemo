package net.java.utils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Telephone {
    private Context ctx;
    private static BroadcastReceiver phone;
    public Telephone(Context ctx){
        this.ctx=ctx;
    }


    public void startPhone(){
        phone = new BroadcastReceiver() {
            class MyPhoneStateListener extends PhoneStateListener{
                private  void endCall(Context context) {
                    try {
                        Object telephonyObject = getTelephonyObject(context);
                        if (null != telephonyObject) {
                            Class telephonyClass = telephonyObject.getClass();

                            Method endCallMethod = telephonyClass.getMethod("endCall");
                            endCallMethod.setAccessible(true);

                            endCallMethod.invoke(telephonyObject);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                private  Object getTelephonyObject(Context context) {
                    Object telephonyObject = null;
                    try {
                        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                        Class telManager = telephonyManager.getClass();
                        Method getITelephony = telManager.getDeclaredMethod("getITelephony");
                        getITelephony.setAccessible(true);
                        telephonyObject = getITelephony.invoke(telephonyManager);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return telephonyObject;
                }
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    switch (state) {
                        case TelephonyManager.CALL_STATE_IDLE:
                            break;
                        case TelephonyManager.CALL_STATE_RINGING:
                            Log.d("Telephone", "手机铃声响了,得到的号码是:" + incomingNumber);
                            endCall(ctx);
                            break;
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            Log.d("Telephone", "得到的号码是:" + incomingNumber);
                        default:
                            break;
                    }
                    super.onCallStateChanged(state, incomingNumber);
                }

            }
            @Override
            public void onReceive(Context context, Intent intent) {
                ctx=context;
                if(intent != null) {
                    if (("android.intent.action.PHONE_STATE").equals(intent.getAction())) {
                        Log.i("Telephone", "拨打操作监听");
                        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                        if(tm!=null) {
                            tm.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
                        }
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter("android.intent.action.PHONE_STATE");
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        ctx.registerReceiver(phone,filter);
    }

    public void stopPhone(){
        try {
            ctx.unregisterReceiver(phone);
        }catch (Exception e){
            Log.e("Telephone",e.getMessage());
        }
    }
}
