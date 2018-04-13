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
    @Override
    public void onInterrupt() {
        //当服务要被中断时调用.会被调用多次
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //监听的App的包名 PACKAGE_NAMES
//        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
//        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
//        info.packageNames = PACKAGE_NAMES;
//        setServiceInfo(info);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //当窗口发生的事件是我们配置监听的事件时,会回调此方法.会被调用多次
        //com.example.administrator.myapplication
        //event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                //界面点击
                Log.i(TAG,"界面点击已经触发");

                //安卓开发，已开启
//                String mDefenseName = "安卓开发";
//                System.out.println("eventtype======="+event.getEventType());
//                if (event.getPackageName().equals("com.android.settings")) {
//                    CharSequence className = event.getClassName();
//                    if (className.equals("android.widget.RelativeLayout")) {
//                        AccessibilityNodeInfo nodeInfo = findViewByText("取消激活");
//                        Log.d("tag：","findViewByText(mDefenseName)->"+findViewByText(mDefenseName));
//                        if (nodeInfo != null && findViewByText(mDefenseName) != null) {
//                            performBackClick();
//                        }
//                    }
//                }
                AccessibilityNodeInfo rowNode = getRootInActiveWindow();
                if (rowNode == null) {
                    Log.i(TAG, "noteInfo is　null");
                    return;
                } else {
                    recycle(rowNode,1);
                }

                PackageManager mPackageManager = this.getPackageManager();
                //获得所有已经安装的包信息
                List<PackageInfo> infos = mPackageManager.getInstalledPackages(0);
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                //界面文字改动
                Toast.makeText(this,"界面内容已经改动",Toast.LENGTH_SHORT).show();
                AccessibilityNodeInfo rowNode2 = event.getSource();
                if (rowNode2 == null) {
                    Log.i(TAG, "noteInfo is　null");
                    return;
                } else {
                    recycle2(rowNode2);
                }
//                if(event.getSource()!=null&&event.getSource().getText()!=null) {
//                    Log.i(TAG,"eventText================="+event.getSource().getText());
//                    int id = event.getSource().getWindowId();
//                    String tag = event.getSource().getText().toString();
//                    int result1=tag.indexOf("jun");
//                    int result2=tag.indexOf("军");
//                    int result3=tag.indexOf("部队");
//                    if(result1 == -1 || result2 == -1 || result3 == -1){
//                        Toast.makeText(this,"检测到你输入了包含有“军”“部队”等关键字的内容",Toast.LENGTH_SHORT).show();
//                        event.getSource().setText("");
//                    }
//                }
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:// 通知栏事件
                Log.i(TAG,"event type:TYPE_NOTIFICATION_STATE_CHANGED");
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED://窗体状态改变
                Log.i(TAG,"event type:TYPE_WINDOW_STATE_CHANGED");
                break;
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED://View获取到焦点
                Log.i(TAG,"event type:TYPE_VIEW_ACCESSIBILITY_FOCUSED");
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                Log.i(TAG,"event type:TYPE_VIEW_ACCESSIBILITY_FOCUSED");
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                Log.i(TAG,"event type:TYPE_GESTURE_DETECTION_END");
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Log.i(TAG,"event type:TYPE_WINDOW_CONTENT_CHANGED");
//                Log.i(TAG,"eventText================="+event.getSource().getText());
//                Log.i(TAG,"eventText================="+event.getText().get(0).toString());
                //“安卓开发”将会被删除。
                if(Build.VERSION.SDK_INT>25){
                    String tag1="卸载\"手机助手\"";
                    String tag2="“手机助手”将会被删除。";
                    if(event.getSource()!=null&&event.getSource().getText()!=null) {
                        Log.i(TAG,"eventText================="+event.getSource().getText());
                        String tag = event.getSource().getText().toString();
                        if(tag.contains("手机助手")){
                            if(tag.contains("卸载")||tag.contains("删除")){
                                Log.i(TAG,"监听到正在准备删除开发APP");
                                clickByText("取消");
                            }
                        }
//                        if (tag1.equals(tag) || tag2.equals(tag)) {
//                            Log.i(TAG,"监听到正在准备删除开发APP");
//                            clickByText("取消");
//                        }
                    }
                }else{
                    AccessibilityNodeInfo rowNode1 = getRootInActiveWindow();
                    if (rowNode1 == null) {
                        Log.i(TAG, "noteInfo is　null");
                        return;
                    } else {
                        recycle(rowNode1,2);
                    }
                }
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                Log.i(TAG,"event type:TYPE_VIEW_SCROLLED");
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                Log.i(TAG,"event type:TYPE_VIEW_TEXT_SELECTION_CHANGED");
                break;
        }
    }
    public void recycle(AccessibilityNodeInfo info,int type) {
        if (info.getChildCount() == 0) {
//            Log.i(TAG, "child widget------------------------------------------------" + info.getClassName());
//            Log.i(TAG, "showDialog:" + info.canOpenPopup());
//            Log.i(TAG, "Text：" + info.getText());
//            Log.i(TAG, "windowId:" + info.getWindowId());
//            Log.i(TAG, "child widget------------------------------------------------");

            if(1==type) {
                //此管理器已激活，允许应用程序“安卓开发”执行以下操作：
                CharSequence device = info.getText();
                if (device != null) {
                    String tag=device.toString();
//                    String tags = "此管理器已激活，允许应用程序“手机助手”执行以下操作：";
//                    String tags2 = "此管理器已激活，允许应用“手机助手”执行以下操作：";
//                    if (tags.equals(device.toString()) || tags2.equals(device.toString())) {
//                        Toast.makeText(this, "正在操作设备管理器", Toast.LENGTH_SHORT).show();
//                        performBackClick();
//                    }
                    if(tag.contains("此管理")&&tag.contains("允许")&&tag.contains("手机助手")){
                        if(tag.contains("已激活")||tag.contains("已启用")) {
                            Toast.makeText(this, "正在操作设备管理器", Toast.LENGTH_SHORT).show();
                            performBackClick();
                        }
                    }
                }
            }

            if(2==type) {
                CharSequence text = info.getText();
                if (text != null) {
                    String tag=text.toString();
//                    String UninstallTags = "“手机助手”将会被删除。";
//                    String UninstallTags2 = "卸载\"手机助手\"";
//                    if (UninstallTags.equals(text.toString()) || UninstallTags2.equals(text.toString())) {
//                        Log.i(TAG,"监听到正在准备删除开发APP");
//                        clickByText("取消");
//                    }
                    if(tag.contains("手机助手")){
                        if(tag.contains("卸载")||tag.contains("删除")){
                            Log.i(TAG,"监听到正在准备删除开发APP");
                            clickByText("取消");
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if(info.getChild(i)!=null){
                    recycle(info.getChild(i),type);
                }
            }
        }
    }
    public void recycle2(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            Log.i(TAG, "child widget------------------------------------------------" + info.getClassName());
            Log.i(TAG, "showDialog:" + info.canOpenPopup());
            Log.i(TAG, "Text：" + info.getText());
            Log.i(TAG, "windowId:" + info.getWindowId());
            Log.i(TAG, "child widget------------------------------------------------");
            if(info.getText()!=null) {
                String tag = info.getText().toString();
                replaceKey(info,tag);
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if(info.getChild(i)!=null){
                    recycle2(info.getChild(i));
                }
            }
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

}
