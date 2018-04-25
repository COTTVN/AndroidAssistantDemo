package com.example.administrator.myapplication;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import net.java.utils.BaseAccessibilityService;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by ssgm_d501_tzp on 2018/3/21.
 */

public class MyAccessibilityService extends BaseAccessibilityService {
    private String eventText;
    @Override
    public void onInterrupt() {
        //当服务要被中断时调用.会被调用多次
    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = getServiceInfo();
        //这里可以设置多个包名，监听多个应用
        info.packageNames = new String[]{"com.tencent.mm", "com.tencent.mobileqq",
                "com.example.administrator.myapplication","com.android.settings",
                "com.oppo.launcher"};
        setServiceInfo(info);
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        Log.i("AccessibilityService","测试...");
        //当窗口发生的事件是我们配置监听的事件时,会回调此方法.会被调用多次
        //com.example.administrator.myapplication
        //com.tencent.mobileqq.activity.NearbyActivity
        //com.oppo.launcher
        //com.android.systemui
        int eventType = event.getEventType();
        List<CharSequence> texts = event.getText();
        if(event.getSource()!=null&&event.getSource().getChildCount()>0&&event.getSource().getChild(0)!=null) {
            CharSequence t = event.getSource().getChild(0).getText();
            if(t!=null) {
                eventText=t.toString();
                Log.i("Text", t + " <--"+event.getEventType());
            }
        }
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                //界面点击
                Log.i(TAG,"界面点击已经触发");
                Log.i(TAG,eventText+" <--");
                Log.i(TAG,"获取文本集合："+texts);
                AccessibilityNodeInfo root = getRootInActiveWindow();
                if (root == null) {
                    Log.i(TAG, "noteInfo is　null");
                    return;
                } else {
                    if(texts.size()>0 && "SGM设备管理".equals(texts.get(0).toString())){
                        Log.i("Text","已点击SGM设备管理");
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                    //启用禁用微信/QQ个别功能
                    boolean weChat = ((MyApp) getApplication()).isWechat();
                    if (texts.size()>0&&weChat) {
                        getNodes(texts);
                    }
                }
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                //界面文字改动
                Log.i(TAG,"界面内容改动");
                AccessibilityNodeInfo rowNode2 = event.getSource();
                if (rowNode2 == null) {
                    Log.i(TAG, "noteInfo is　null");
                    return;
                } else {
                    recycle2(rowNode2);
                }
                break;
//            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:// 通知栏事件
//                Log.i(TAG,"event type:TYPE_NOTIFICATION_STATE_CHANGED");
//                break;
//            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED://窗体状态改变
//                Log.i(TAG,"event type:TYPE_WINDOW_STATE_CHANGED");
//                break;
//            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED://View获取到焦点
//                Log.i(TAG,"event type:TYPE_VIEW_ACCESSIBILITY_FOCUSED");
//                break;
//            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START://表示开始手势检测的事件。
//                Log.i(TAG,"event type:TYPE_VIEW_ACCESSIBILITY_FOCUSED");
//                break;
//            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END://表示结束手势检测的事件。
//                Log.i(TAG,"event type:TYPE_GESTURE_DETECTION_END");
//                break;
//            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
//                Log.i(TAG,"event type:TYPE_VIEW_SCROLLED");
//                break;
//            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
//                Log.i(TAG,"event type:TYPE_VIEW_TEXT_SELECTION_CHANGED");
//                break;
//            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//                Log.i("AccessibilityService","event type:TYPE_WINDOW_CONTENT_CHANGED");
////                try {
////                    //“安卓开发”将会被删除。
//////                    if (Build.VERSION.SDK_INT > 25) {
////                        if (event.getSource() != null && event.getSource().getText() != null) {
////                            Log.i(TAG, "eventText=================" + event.getSource().getText());
////                            String tag = event.getSource().getText().toString();
////                            if (tag.contains("手机助手")) {
////                                if (tag.contains("卸载") || tag.contains("删除")) {
////                                    Log.i(TAG, "监听到正在准备删除开发APP");
////                                    clickByText("取消");
////                                }
////                            }
////                        }
//////                    } else {
//////                        AccessibilityNodeInfo rowNode1 = getRootInActiveWindow();
//////                        if (rowNode1 == null) {
//////                            Log.i(TAG, "noteInfo is　null");
//////                            return;
//////                        } else {
//////                            recycle(rowNode1,2);
//////                        }
//////                    }
////                }catch (Exception e){
////                    Log.e("AccessibilityService",e.getMessage());
////                }
//                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED://窗体状态改变
                try {
                    Log.i(TAG, "event type:TYPE_WINDOW_STATE_CHANGED");
                    Log.i(TAG,"获取文本集合："+texts);
                    String tag = eventText;
                    if (null!=tag&&tag.contains("手机助手")) {
                        if (tag.contains("卸载") || tag.contains("删除")) {
                            Log.i(TAG, "监听到正在准备删除开发APP");
                            clickByText("取消");
                        }
                    }
                }catch (Exception e){
                    Log.e("AccessibilityError",e.getMessage());
                }
                break;
        }
    }
    private void getNodes(List<CharSequence> texts){
        try {
            String text = texts.get(0).toString();
           if("漂流瓶".equals(text)|| "摇一摇".equals(text)||"附近的人".equals(text)){
               performBackClick();
           }
           if("附近".equals(text)){
               try {
                   Thread.sleep(400);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
               performGlobalAction(GLOBAL_ACTION_BACK);
           }
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }
    }
    private void recycle2(AccessibilityNodeInfo info) {
        try {
            if (info.getChildCount() == 0) {
                Log.i(TAG, "child widget2------------------------------------------------" + info.getClassName());
                Log.i(TAG, "showDialog:" + info.canOpenPopup());
                Log.i(TAG, "Text：" + info.getText());
                Log.i(TAG, "windowId:" + info.getWindowId());
                Log.i(TAG, "child widget2------------------------------------------------");
                if (info.getText() != null) {
                    String tag = info.getText().toString();
                    replaceKey(info, tag);
                }
            } else {
                for (int i = 0; i < info.getChildCount(); i++) {
                    if (info.getChild(i) != null) {
                        recycle2(info.getChild(i));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }
    }

    private void replaceKey(AccessibilityNodeInfo info,String text){
        String newText="";
        Bundle arguments = new Bundle();
        String [] keys={"军队","部队","jundui","budui"};
        for(String key:keys){
            if(text.contains(key)){
                newText=text.replace(key,"&%$@#*");
                arguments.putCharSequence(
                        AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, newText);
                info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            }
        }
    }

    //unutilized
    private void recycle(AccessibilityNodeInfo info,int type) {
        try {

            if (info.getChildCount() == 0) {

                if (1 == type) {
                    //此管理器已激活，允许应用程序“安卓开发”执行以下操作：
                    CharSequence device = info.getText();
                    if (device != null) {
                        String tag = device.toString();
                        if (tag.contains("此管理") && tag.contains("允许") && tag.contains("手机助手")) {
                            if (tag.contains("已激活") || tag.contains("已启用")) {
                                Toast.makeText(this, "正在操作设备管理器", Toast.LENGTH_SHORT).show();
                                performBackClick();
                            }
                        }
                    }
                }

//                if (2 == type) {
//                    CharSequence text = info.getText();
//                    if (text != null) {
//                        String tag = text.toString();
//                        if (tag.contains("手机助手")) {
//                            if (tag.contains("卸载") || tag.contains("删除")) {
//                                Log.i(TAG, "监听到正在准备删除开发APP");
//                                clickByText("取消");
//                            }
//                        }
//                    }
//                }
            } else {
                for (int i = 0; i < info.getChildCount(); i++) {
                    if (info.getChild(i) != null) {
                        recycle(info.getChild(i), type);
                    }
                }
            }
            Log.i("AccessibilityService","\r\n一测试。。。。。。");
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }
    }

}
