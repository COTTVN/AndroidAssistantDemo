package com.example.administrator.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mob.MobSDK;

import net.java.service.commonServiceI;
import net.java.service.commonServiceImpl;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2018/3/5.
 */

public class RegisterActivity extends AppCompatActivity {
    public boolean validation;//短信码验证
    public boolean receive;//获取验证码
    EditText tphone;
    EditText userName;
    EditText userPwd;
    EditText tcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        MobSDK.init(this);
        SMSSDK.registerEventHandler(eh); //注册短信回调（记得销毁，避免泄露内存）
        tphone=this.findViewById(R.id.tphone);
        userName=this.findViewById(R.id.userName);
        userPwd=this.findViewById(R.id.userPwd);
        tcode=this.findViewById(R.id.tcode);
    }

    EventHandler eh=new EventHandler(){
        @Override
        public void afterEvent(int event, int result, Object data) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    toast("手机验证成功");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnRegister();
                        }
                    });
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){       //获取验证码成功
                    toast("获取验证码成功");
                }
            }else{//错误等在这里（包括验证失败）
                //错误码请参照http://wiki.mob.com/android-api-错误码参考/这里我就不再继续写了
                ((Throwable)data).printStackTrace();
                toast("手机验证失败,请稍后再试...");
                String str = data.toString();
                toast(str);
            }
        }
    };
    private void toast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void register(View view){
        String code=tcode.getText().toString();
        String country="86";
        String phone=tphone.getText().toString();
        SMSSDK.submitVerificationCode(country, phone, code);
    }

    public void btnRegister(){
        String name=userName.getText().toString();
        String pwd=userPwd.getText().toString();
        String phone=tphone.getText().toString();
        commonServiceI com=new commonServiceImpl();
        String url="http://10.10.10.213:8118/relation/insert";
        String[] parameters={name,pwd,phone};
        String errNum=com.getReponse(url,parameters);
        if("0".equals(errNum)){
            Toast.makeText(RegisterActivity.this,"注册成功！",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            //从第一个active跳到第二个active上
            intent.setClass(RegisterActivity.this, MainActivity.class);
            RegisterActivity.this.startActivity(intent);
        }else if("1".equals(errNum)){
            Toast.makeText(RegisterActivity.this,"该用户已存在！",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(RegisterActivity.this,"注册失败，请稍后再试...",Toast.LENGTH_SHORT).show();
        }
    }

    public void getCode(View view){
        String country="86";
        String phone=tphone.getText().toString();
        SMSSDK.getVerificationCode(country, phone);
    }


    //销毁短信注册
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销回调接口registerEventHandler必须和unregisterEventHandler配套使用，否则可能造成内存泄漏。
        SMSSDK.unregisterEventHandler(eh);
    }
}
