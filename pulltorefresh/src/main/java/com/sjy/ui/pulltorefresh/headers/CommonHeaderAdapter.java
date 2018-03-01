package com.sjy.ui.pulltorefresh.headers;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sjy.ui.pulltorefresh.R;
import com.sjy.ui.pulltorefresh.adapter.RefreshHeaderAdapter;

/**
 * 默认样式头部view
 * Created by sjy on 2018/2/22.
 */

public class CommonHeaderAdapter extends RefreshHeaderAdapter {

    private ImageView iv;
    private TextView tv;
    private ProgressBar progressBar;
    private boolean isRefresh=false;//是否刷新
    private int refreshHeight=200;

    @Override
    public void pullDistance(int dis) {
        dealScroll(dis);
    }

    @Override
    public int getRefreshHeight() {
        return refreshHeight;
    }


    @Override
    public void onRefresh() {
        tv.setText("正在加载数据");
        iv.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void autoRefresh() {
        tv.setText("正在加载数据");
        iv.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishRefresh() {
        iv.setRotation(0);
        iv.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        tv.setText("下拉刷新");
    }

    @Override
    public View inflateHeaderView(Context context) {
        View view=LayoutInflater.from(context).inflate(R.layout.view_header,null);
        iv=view.findViewById(R.id.iv);
        tv=view.findViewById(R.id.tv);
        progressBar=view.findViewById(R.id.progress_bar);
        return view;
    }

    /**
     * 处理下拉 头部事件
     * @param dis
     */
    private void dealScroll(int dis){
        if(Math.abs(dis)>refreshHeight){
            if(!isRefresh) {
                tv.setText("松开加载");
                ObjectAnimator.ofFloat(iv, "rotation", 0, -180)
                        .start();
                isRefresh = true;
            }
        }else {
            if(isRefresh) {
                tv.setText("下拉刷新");
                ObjectAnimator.ofFloat(iv, "rotation", -180, 0)
                        .start();
                isRefresh = false;
            }
        }
    }


}
