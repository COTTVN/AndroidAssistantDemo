package com.example.administrator.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import net.java.service.commonServiceI;
import net.java.service.commonServiceImpl;
import net.java.utils.LoadListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/7.
 */

public class ListScrollActivity extends AppCompatActivity implements LoadListView.ILoadListener {
    private int total=0;//数据总条数
    private int page=0;//当前索引页
    private int totalPage=0;//总页数
    private LoadListView listView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_scroll);
        listView=this.findViewById(R.id.scrollView);
        listView.setInterface(this);
        findViewById(R.id.btnTop).setVisibility(View.GONE);
        loadData();
    }

    private void loadData(){
        String url="http://10.10.10.213:8118/compound/search";
        String[] parameters={"0",page+"","5"};
        commonServiceI con=new commonServiceImpl();
        String result=con.getReponse(url,parameters);
        setData(listView,result);
    }
    private void setData(ListView listView, String result){
        try {
            JSONObject json=new JSONObject(result);
            JSONArray conts=json.getJSONArray("conts");
            total=json.getInt("count");
            totalPage=json.getInt("pageNum");
            List<Map<String, String>> list = new ArrayList<>();
            for(int i=0;i<conts.length();i++){
                JSONObject cont=conts.getJSONObject(i);
                Map<String, String> map = new HashMap<String, String>();
                map.put("Username",cont.getString("Username"));
                map.put("Usergroup",cont.getString("Usergroup"));
                map.put("Prostaus",cont.getString("Prostaus"));
                map.put("Proname",cont.getString("Proname"));
                list.add(map);
            }
            String[] head=new String[]{"Username","Usergroup","Prostaus","Proname"};
            SimpleAdapter sad=new SimpleAdapter(this,list,R.layout.list_item,head,
                    new int[]{R.id.name,R.id.group,R.id.status,R.id.prod});
            listView.setAdapter(sad);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(ListScrollActivity.this,"连接数据库失败！",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoad() {
        Handler handler=new Handler();
        //2秒后进行加载
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //获取更多数据
                //更新ListView数据
                page=page+1;
                if(page<totalPage) {
                    loadData();
                }else{
                    Toast.makeText(ListScrollActivity.this,"到达底部！",Toast.LENGTH_SHORT).show();
                    findViewById(R.id.btnTop).setVisibility(View.VISIBLE);
                }
                //通知listView加载完毕
                listView.loadComplete();
            }
        },2000);
    }

    public void backTop(View view){
        page=0;
        loadData();
        findViewById(R.id.btnTop).setVisibility(View.GONE);
    }
}
