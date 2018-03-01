package com.sjy.ui.pulltorefresh.adapter;

import android.content.Context;
import android.view.View;

/**
 * 头部基础适配器
 * Created by sjy on 2018/2/22.
 */

public abstract class RefreshHeaderAdapter {

    /**
     * 下拉距离
     * @param dis
     */
   public abstract void pullDistance(int dis);

    /**
     * 刷新高度（下拉到指定高度松开，才会刷新）
     * @return
     */
    public abstract int getRefreshHeight();


    /**
     * 正在刷新
     */
    public abstract void onRefresh();

    /**
     * 自动刷新
     */
    public abstract void autoRefresh();

    /**
     * 刷新完成
     */
    public abstract void finishRefresh();


    /**
     * 初始化头部view
     * @return
     */
    public abstract View inflateHeaderView(Context context);


}
