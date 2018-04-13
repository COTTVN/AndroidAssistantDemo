package com.example.administrator.myapplication;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import net.java.service.commonServiceI;
import net.java.service.commonServiceImpl;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/5.
 */

public class IndexActivity extends AppCompatActivity {
    private Button search;
    private TextView pageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().
                detectDiskWrites().detectNetwork().penaltyLog().build());
        search=findViewById(R.id.btnSearch);
        pageView=findViewById(R.id.pageView);
        hidden();
    }

    private int total=0;//数据总条数
    private int page=0;//当前索引页
    private int totalPage=0;//总页数
    public void query(View view){
        EditText searchName=this.findViewById(R.id.search);
        final ListView listView=this.findViewById(R.id.list);
        String name=searchName.getText().toString();
        if("".equals(name)){
            name="0";
        }
        String url="http://10.10.10.213:8118/compound/search";
        String[] parameters={name,page+"","5"};

        commonServiceI con=new commonServiceImpl();
        String result=con.getReponse(url,parameters);
        setData(listView,result);
        hidden();
        String ctr = (page+1)+"/"+totalPage;
        pageView.setText(ctr);
//        listView.setOnScrollListener(new OnScrollListener(){
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int i) {
//                switch (i) {
//                    //空閒狀態
//                    case OnScrollListener.SCROLL_STATE_IDLE:
//                        Toast.makeText(IndexActivity.this,"空閒狀態",Toast.LENGTH_SHORT).show();
//                        break;
//                    //滑動狀態
//                    case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
//                        Toast.makeText(IndexActivity.this,"滑動狀態",Toast.LENGTH_SHORT).show();
//                        scrollFlag = true;
//                        break;
//                    //慣性
//                    case OnScrollListener.SCROLL_STATE_FLING:
//                        Toast.makeText(IndexActivity.this,"慣性",Toast.LENGTH_SHORT).show();
//                        scrollFlag = false;
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            //滚动状态
//            //第一参数listView视图，第二个参数首个条目，第三个参数可件可见条目数，第四个参数总条目数
//            @Override
//            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
//                endVisibleItem=i+i1;
//                lastVisibleItemPosition = i;
//                if(scrollFlag) {
//                    page = page + 1;
//                    search.performClick();
//                    Toast.makeText(IndexActivity.this,"true",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    public void next(View view){
        if((page+1)<totalPage) {
            page = page + 1;
            search.callOnClick();
        }
    }
    public void per(View view){
        if((page+1)>1){
            page = page - 1;
            search.callOnClick();
        }
    }

    private void hidden(){
        Button btnNext=findViewById(R.id.btnNext);
        Button btnPer=findViewById(R.id.btnPer);
        LinearLayout listHead=findViewById(R.id.linearLayout);
        if(0==totalPage){
            listHead.setVisibility(View.GONE);
        }else{
            listHead.setVisibility(View.VISIBLE);
        }
        if(0==totalPage||1==totalPage){
            btnNext.setVisibility(View.GONE);
            btnPer.setVisibility(View.GONE);
            pageView.setVisibility(View.GONE);
        }else{
            btnNext.setVisibility(View.VISIBLE);
            btnPer.setVisibility(View.VISIBLE);
            pageView.setVisibility(View.VISIBLE);
        }
    }

    private void setData(ListView listView,String result){
        try {
            JSONObject json=new JSONObject(result);
            JSONArray conts=json.getJSONArray("conts");
            total=json.getInt("count");
            totalPage=json.getInt("pageNum");
            List<Map<String, String>> list = new ArrayList<>();
            for(int i=0;i<conts.length();i++){
                JSONObject cont=conts.getJSONObject(i);
                Map<String, String> map = new HashMap<String, String>();
                map.put("Username",cont.getString("username"));
                map.put("Usergroup",cont.getString("usergroup"));
                map.put("Prostaus",cont.getString("prostatus"));
                map.put("Proname",cont.getString("proname"));
                list.add(map);
            }
            String[] head=new String[]{"Username","Usergroup","Prostaus","Proname"};
            SimpleAdapter sad=new SimpleAdapter(this,list,R.layout.list_item,head,
                    new int[]{R.id.name,R.id.group,R.id.status,R.id.prod});
            listView.setAdapter(sad);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(IndexActivity.this,"连接数据库失败！",Toast.LENGTH_SHORT).show();
        }
    }
}
