package com.example.administrator.myapplication;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.accessibility.AccessibilityManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SearchView;
import android.widget.Toast;

import net.java.utils.PermissionsChecker;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

import static net.java.utils.serviceUtils.isAccessibilitySettingsOn;


/**
 * Created by Administrator on 2018/3/6.
 */

public class ShowActivity extends AppCompatActivity{
    private static String TAG="ShowActivity";
    //-------------------------------------------------------------------
    private static final int REQUEST_CODE = 0; // 请求码

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };

//    @Bind(R.id.main_t_toolbar)
    Toolbar mTToolbar;

    private PermissionsChecker mPermissionsChecker; // 权限检测器
    //----------------------------------------------------------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //----------------------------------------检查权限--start-----------------------------------
//        ButterKnife.bind(this);
        mTToolbar=findViewById(R.id.main_t_toolbar);
//        setSupportActionBar(mTToolbar);
        mPermissionsChecker = new PermissionsChecker(this);
        //-----------------------------------------end---------------------------------------------
        setContentView(R.layout.activity_show);
        webV("http://www.baidu.com/");
        searchV();
        //getPackage();
    }

    private void getPackage(){
        PackageManager mPackageManager = this.getPackageManager();
        //获得所有已经安装的包信息
        List<PackageInfo> infos = mPackageManager.getInstalledPackages(0);
        Log.d("packages=",infos.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,1,"人物查询");
        menu.add(0,2,2,"下滑分页");
        menu.add(0,3,3,"上传下载");
        menu.add(0,4,4,"设备管理");
        menu.add(0,5,5,"手机信息");
        menu.add(0,6,6,"应用列表");
        menu.add(0,7,7,"设备控制");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getGroupId() == 0 &&item.getItemId() == 1) {
            Intent intent = new Intent(this, IndexActivity.class);
            startActivity(intent);
        }else if (item.getGroupId() == 0 &&item.getItemId() == 2){
            Intent intent = new Intent(this, ListScrollActivity.class);
            startActivity(intent);
        }else if (item.getGroupId() == 0 &&item.getItemId() == 3){
            Intent intent = new Intent(this, UpOrDownActicity.class);
            startActivity(intent);
        }else if (item.getGroupId() == 0 &&item.getItemId() == 4){
            Intent intent = new Intent(this, DevicesActivity.class);
            startActivity(intent);
        }else if (item.getGroupId() == 0 &&item.getItemId() == 5){
            Intent intent = new Intent(this, AppInfoActivity.class);
            startActivity(intent);
        }else if (item.getGroupId() == 0 &&item.getItemId() == 6){
            Intent intent = new Intent(this, AppListActivity.class);
            startActivity(intent);
        }else if (item.getGroupId() == 0 &&item.getItemId() == 7){
            Intent intent = new Intent(this, PermissionActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    /*
     * Class　　:　　MyWebViewClient，用于辅助WebView，处理各种通知、请求等事件
     */
    private class MyWebViewClient extends WebViewClient {

              //重写父类方法，让新打开的网页在当前的WebView中显示
              public boolean shouldOverrideUrlLoading(WebView view, String url) {
                     view.loadUrl(url);
                     return true;
              }

    }
    private void searchV(){
        SearchView searchView=this.findViewById(R.id.searchView);
        String content=searchView.getQuery().toString();
        System.out.println(content);
        // 设置搜索文本监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    webV("https://www.baidu.com/s?wd="+newText);
                }else{
                    webV("https://www.baidu.com/");
                }
                return false;
            }
        });
    }

    private void webV(String url){
        WebView webView=this.findViewById(R.id.webView);
        webView.loadUrl(url);
        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        MyWebViewClient myWebViewClient = new MyWebViewClient();
        webView.setWebViewClient(myWebViewClient);
    }

    //------------------------------------------------------------------------------------
    @Override protected void onResume() {
        super.onResume();
        //无障碍服务
        accessibility();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }

    //检查无障碍服务是否开启
    private boolean isServiceEnabled() {
        AccessibilityManager accessibilityManager = (AccessibilityManager)getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(
                        AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().contains("com.example.administrator.myapplication/com.example.administrator.myapplication.MyAccessibilityService")) {
                return true;
            }
        }
        return false;
    }




    private void accessibility(){
//        if(!isServiceEnabled()){
//            //打开系统无障碍设置界面
//            Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//            startActivity(accessibleIntent);
//        }
        if (!isAccessibilitySettingsOn(getApplicationContext())) {
//            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            //打开系统无障碍设置界面
            Intent intent = new Intent(this, AccessibilityOpenHelperActivity.class);
            startActivity(intent);
        }
    }
}
