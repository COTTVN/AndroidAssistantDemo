package net.java.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.myapplication.R;

/**
 * Created by Administrator on 2018/3/7.
 */

public class LoadListView extends ListView implements OnScrollListener{
    View footer; //底部布局
    int totalItemCount;//总数量
    int lastVisibleItem;//最后一个可见item
    boolean isLoading;//正在加载
    ILoadListener iLoadListener;
    public LoadListView(Context context) {
        super(context);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    //添加底部加载提示布局到listView
    private void initView(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        footer=inflater.inflate(R.layout.list_scroll,null);
        footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
        this.addFooterView(footer);
        this.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(totalItemCount==lastVisibleItem
                && i==SCROLL_STATE_IDLE){
            if(!isLoading) {
                isLoading=true;
                footer.findViewById(R.id.load_layout).setVisibility(View.VISIBLE);
                //调用接口加载更多
                iLoadListener.onLoad();
            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        this.lastVisibleItem=i+i1;
        this.totalItemCount=i2;
        System.out.println("lastVisibleItem="+(i+i1)+"  totalItemCount="+i2);
    }


    public void setInterface(ILoadListener iLoadListener){
        this.iLoadListener = iLoadListener;
    }
    //加载更多数据回调接口
    public interface ILoadListener{
         void onLoad();
    }

    //加载完毕
    public void loadComplete(){
        isLoading=false;
        footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
    }
}
