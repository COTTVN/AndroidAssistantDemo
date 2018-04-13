package com.example.administrator.myapplication;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.VpnService;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import net.java.utils.DBOpenHelper;
import net.java.utils.VpnUtil;

import cz.msebera.android.httpclient.Header;

import static com.example.administrator.myapplication.DevicesActivity.setTranslucent;
import static net.java.utils.MacUtil.getMacAddr;


public class MainActivity extends AppCompatActivity{

    //-----------------------------------------------------------------------
    AsyncHttpClient client;
    EditText userName;
    EditText userPwd;
    Button btnLogin;
    //------------------------------------------------------------------------

    /**
     * 激活组件的请求码
     */
    private static final int REQUEST_CODE_ACTIVE_COMPONENT = 1;
    /**
     * 设备安全管理服务，2.2之前需要通过反射技术获取
     */
    private DevicePolicyManager devicePolicyManager = null;
    /**
     * 对应自定义DeviceAdminReceiver的组件
     */
    private ComponentName componentName = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().
                detectDiskWrites().detectNetwork().penaltyLog().build());
        //----------------------------------初始化变量---start-----------------------------------------
        client=new AsyncHttpClient();
        userName=this.findViewById(R.id.userName);
        userPwd=this.findViewById(R.id.userPwd);
        btnLogin=this.findViewById(R.id.btnLogin);
        //-----------------------------------------end------------------------------------------------
        //启动服务
        startService();

        //----------------------------------------------激活设备管理器--start-----------------------------
        setTranslucent(this);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this,MonitorSysReceiver.class);

        active();
        //-------------------------------------------------end------------------------------------
        //vpnRun();
    }
    public void loginOn(View view){
        btnLogin.setEnabled(false);
        String name=userName.getText().toString();
        String pwd=userPwd.getText().toString();

        String[] parameters={name,pwd};
        StringBuilder URL=new StringBuilder("http://10.10.10.213:8118/relation/isLogin");
        asyncHttp(URL,parameters); //使用AsyncHttpClient网络框架请求url

    }

    private void asyncHttp(StringBuilder url,String[] parameters){
        if(parameters.length>1){
            for(String param:parameters){
                String newParam="/"+param;
                url.append(newParam);
            }
        }
        client.get(url.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                String message="";
                if("true".equals(responseString)){
                    loginInfo();
                    Intent intent = new Intent();
                    //从第一个active跳到第二个active上
                    intent.setClass(MainActivity.this,ShowActivity.class);
                    MainActivity.this.startActivity(intent);
                    message="登陆成功！";
                    btnLogin.setEnabled(true);
                }else if("false".equals(responseString)){
                    message="登陆失败！";
                }
                Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void loginInfo(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String name=userName.getText().toString();
                String url="http://10.10.10.213:8118/relation/addLog";
                RequestParams params = new RequestParams();
                params.put("name", name);
                params.put("mac",getMacAddr());
                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
            }
        });
    }
    public void registerView(View view){
        try {
            Intent intent = new Intent();
            //从第一个active跳到第二个active上
            intent.setClass(MainActivity.this, RegisterActivity.class);
            MainActivity.this.startActivity(intent);
        }catch(Exception e){
            Toast.makeText(MainActivity.this,"当前无法注册，请稍后再试...",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void startService() {
        Intent i = new Intent(MainActivity.this,SysService.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(i);
    }

    private void active() {
        if (!isAdminActive()){
            // 打开管理器的激活窗口
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            // 指定需要激活的组件
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "(激活窗口中的描述信息)");
            startActivityForResult(intent, REQUEST_CODE_ACTIVE_COMPONENT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ACTIVE_COMPONENT) {
            // 激活组件的响应
            if (resultCode == Activity.RESULT_CANCELED) {
                //如果点击取消激活则关闭页面
                finish();
            } else {
                Toast.makeText(this, "已触发DeviceAdminReceiver.onEnabled", Toast.LENGTH_SHORT).show();
            }
        }
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(this, OwnVPNService.class);
            startService(intent);
        }
    }


    /**
     * 判断该组建是否有系统管理员的权限（系统安全-设备管理器 中是否激活）
     * @return bollean true:false?激活:未激活
     */
    private boolean isAdminActive(){
        return devicePolicyManager.isAdminActive(componentName);
    }

    //运行vpn服务
    private void vpnRun(){
        //OwnVPNService.prepare(this);
        Intent intent = VpnService.prepare(this);
        if (intent != null) {
            startActivityForResult(intent, 0);
        } else {
            onActivityResult(0, RESULT_OK, null);
        }
    }

    //测试快捷通道
    public void btnTest(View view){
        Intent intent = new Intent();
        //从第一个active跳到第二个active上
        intent.setClass(MainActivity.this,ShowActivity.class);
        MainActivity.this.startActivity(intent);
    }

    public void onClick(View view){
        VpnUtil.init(MainActivity.this);
        if(view.getId() == R.id.bt_connect){
            //查询检查是否已经存在VPN
            Object vpnProfile = VpnUtil.getVpnProfile();
            if(vpnProfile == null){
                vpnProfile = VpnUtil.createVpnProfile("ssgmVPN", "10.10.10.157", "stan", "smith");
            }else{
                VpnUtil.setParams(vpnProfile,"ssgmVPN", "10.10.10.157", "stan", "smith");
            }
            //连接
            boolean status=VpnUtil.connect(MainActivity.this,vpnProfile);
            if(status) {
                Toast.makeText(this, "开启VPN", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "VPN连接失败", Toast.LENGTH_SHORT).show();
            }
        }else if(view.getId() == R.id.bt_disconnect){
            //断开连接
            boolean status=VpnUtil.disconnect(MainActivity.this);
            if(status){
                Toast.makeText(this, "VPN已断开", Toast.LENGTH_SHORT).show();
            }
        }else if(view.getId() == R.id.bt_listen){
            vpnRun();
        }else if(view.getId() == R.id.bt_listen_end){
            OwnVPNService own= new OwnVPNService();
            own.stopVpn();
        }
    }

    public void connectDB(View view){
        DBOpenHelper helper = new DBOpenHelper(MainActivity.this);
        //当磁盘已经满了时，getWritableDatabase会抛异常，而getReadableDatabase不会报错，它此时不会返回读写数据库的对象，而是仅仅返回一个读数据库的对象
        SQLiteDatabase db = helper.getWritableDatabase();//得到的是SQLiteDatabase对象
        helper.insert(db,null,"person");
        helper.query(db);
    }
}
