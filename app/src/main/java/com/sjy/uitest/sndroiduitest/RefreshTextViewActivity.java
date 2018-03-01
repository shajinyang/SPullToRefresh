package com.sjy.uitest.sndroiduitest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sjy.ui.pulltorefresh.PullToRefreshLayout;
import com.sjy.ui.pulltorefresh.listeners.IOnRefreshListener;


/**
 * Created by sjy on 2018/2/12.
 */

public class RefreshTextViewActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refreshlayout_textview);
//        ((PullToRefreshLayout)findViewById(R.id.refresh_view)).setAdapter(new CommonHeaderAdapter());
        ((PullToRefreshLayout)findViewById(R.id.refresh_view)).refresh();
        ((PullToRefreshLayout)findViewById(R.id.refresh_view)).setOnRefreshListener(new IOnRefreshListener() {
            @Override
            public void onRefresh() {
                findViewById(R.id.refresh_view).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((PullToRefreshLayout)findViewById(R.id.refresh_view)).closeRefresh();
                    }
                },1000);
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();

    }
}
