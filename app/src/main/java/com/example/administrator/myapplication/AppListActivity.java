package com.example.administrator.myapplication;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import net.java.entity.AppBean;
import net.java.utils.MacUtil;

import java.util.List;

public class AppListActivity extends AppCompatActivity{
    LinearLayout body;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applist);
        body=this.findViewById(R.id.appBody);
        getAppList();
    }

    private void getAppList(){
        List<AppBean> appLists= MacUtil.getAllApk(this);
        for(AppBean app:appLists){
            final AppBean oneApp=app;
            if(app.isSd()) {
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setMinimumHeight(50);
                TextView textView = new TextView(this);
                ImageView imageView = new ImageView(this);
                imageView.setImageDrawable(app.getAppIcon());
                imageView.setMaxWidth(50);
                imageView.setMaxHeight(50);
                String info = "\r\n"+app.getAppName() + "\r\n";
                textView.setText(info);
                textView.setTextSize(14);
                textView.setGravity(Gravity.CENTER);
                linearLayout.addView(imageView);
                linearLayout.addView(textView);
                linearLayout.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(AppListActivity.this,"版本："+oneApp.getAppVersionName()+"\r\n大小："+oneApp.getAppSize()+"\r\n包名："+oneApp.getAppPackageName()+"\r\n路径："+oneApp.getApkPath(),Toast.LENGTH_SHORT).show();
                    }
                });
                body.addView(linearLayout);
            }
        }
    }
}
