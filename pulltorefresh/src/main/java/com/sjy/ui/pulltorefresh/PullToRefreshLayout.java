package com.sjy.ui.pulltorefresh;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ScrollView;

import com.sjy.ui.pulltorefresh.adapter.RefreshHeaderAdapter;
import com.sjy.ui.pulltorefresh.headers.CommonHeaderAdapter;
import com.sjy.ui.pulltorefresh.listeners.IOnRefreshListener;


/**
 * 下拉刷新控件
 * Created by sjy on 2018/2/12.
 * |—————————————————————————————|
 * |   header1                   |
 * |—————————————————————————————|
 * |   header2                   |
 * |_____________________________|
 * |                             |
 * |   content                   |
 * |                             |
 * |_____________________________|
 * |   footer1                   |
 * |—————————————————————————————|
 * |   footer2                   |
 * |_____________________________|
 */

public class PullToRefreshLayout extends ViewGroup {

    private RefreshHeaderAdapter adapter;
    private IOnRefreshListener iOnRefreshListener;
    private View headView;//头布局
    private View contentView;//主体布局
    private float actionDownY;
    private int refreshHeight = 200;//固定刷新高度
    private int historyScorllLength;//再次点击时，父view历史滚动长度
    private int sonHistoryScorllLength;//再次点击时，子view历史滚动长度
    private boolean isStopAnima = false;//是否停止动画（动画时用户手指触摸）

    private boolean isUp = false;//是否向上滑动
    private boolean isIntercept = false;//父view是否拦截子view的分发事件
    private MotionEvent lastMotionEvent;//保存最新的event事件

    public PullToRefreshLayout(Context context) {
        super(context);
        initDefaultAdapter();
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDefaultAdapter();
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDefaultAdapter();
    }


    /**
     * 设置头部样式
     *
     * @param adapter
     */
    public void setAdapter(RefreshHeaderAdapter adapter) {
        if (adapter != null) {
            this.adapter = adapter;
            headView = adapter.inflateHeaderView(getContext());
            addHeader(headView);
            refreshHeight = adapter.getRefreshHeight();
        }

    }

    /**
     * 刷新监听
     *
     * @param iOnRefreshListener
     */
    public void setOnRefreshListener(IOnRefreshListener iOnRefreshListener) {
        this.iOnRefreshListener = iOnRefreshListener;
    }

    /**
     * 开始刷新
     */
    public void refresh() {
        if (adapter != null) {
            adapter.autoRefresh();
            adapter.onRefresh();//通知headerView开始刷新
        }
        recoveryPosition(0, -refreshHeight);
    }

    /**
     * 关闭刷新
     */
    public void closeRefresh() {
        recoveryPosition(getScrollY(), 0);
    }

    /**
     * 添加刷新头部view
     */
    private void addHeader(View headView) {
        this.headView = headView;
        addView(headView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int contentSize = getChildCount();
        if (contentSize > 0) {
            for (int i = 0; i < contentSize; i++) {
                contentView = getChildAt(i);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量子view
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int headL = 0;
        int headT = 0;
        int headR = r;
        int headB = 0;
        //添加头部view
        if (headView != null) {
            headB += 0;
            headT = headB - headView.getMeasuredHeight();
            headView.layout(headL, headT, headR, headB);
        }
        //添加主体布局
        if (contentView != null) {
            headB += contentView.getMeasuredHeight();
            headT = headB - contentView.getMeasuredHeight();
            contentView.layout(headL, headT, headR, headB);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (contentView != null) {
            lastMotionEvent = event;
            float y = event.getY();//触摸时y轴位置
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isStopAnima = true;//手指触摸屏幕，停止复位动画
                    actionDownY = y;
                    historyScorllLength = getScrollY();//记录子view本身的移动位置
                    super.dispatchTouchEvent(event);
                    return true;
                case MotionEvent.ACTION_UP:
                    isStopAnima = false;//标记动画开始
                    sonHistoryScorllLength = contentView.getScrollY();//记录子view内容滚动位置
                    historyScorllLength = getScrollY();//记录子view本身的移动位置
                    if (Math.abs(getScrollY()) > refreshHeight) {
                        recoveryPosition(getScrollY(), -refreshHeight);
                    } else {
                        recoveryPosition(getScrollY(), 0);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    //向下滑动
                    if (actionDownY - event.getY() < 0) {
                        isUp = false;
                    } else {
                        isUp = true;
                    }

                    if (!isChildViewCanScroll()) {
                        //如果父view开始拦截,则通知子view取消事件，通知父view按下事件
                        if (!isIntercept) {
                            sendCancelEvent();
                            sendDownEventFather();
                        }
                        if ((actionDownY - y) / 2 + historyScorllLength < 0) {
                            touchScroll(0, (int) ((actionDownY - y) / 2 + historyScorllLength));
                        } else {
                            //处理上滑时，getScrollY小于0，但上滑距离大于0
                            if (getScrollY() < 0) {
                                touchScroll(0, 0);
                            }
                        }
                        adapter.pullDistance(getScrollY());
                        isIntercept = true;
                    } else {
                        //如果父view开始分发给childView,则通知子view按下事件，通知父view取消事件
                        if (isIntercept) {
                            sendDownEvent();
                            sendCancelEventFather();
                        }
                        isIntercept = false;
                    }
                    break;

            }

        }
        if (isIntercept) {
            return isIntercept;
        } else {
            return super.dispatchTouchEvent(event);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    /**
     * 初始化默认头部
     */
    private void initDefaultAdapter() {
        adapter = new CommonHeaderAdapter();
        setAdapter(adapter);
    }

    /**
     * 按住滚动
     */
    private void touchScroll(int posX, int posY) {
        scrollTo(posX, posY);
    }

    /**
     * 松手滚动
     */
    private void recoveryPosition(int startPosition, final int endPosition) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startPosition, endPosition);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isStopAnima) {
                    if (endPosition == 0) {
                        adapter.finishRefresh();
                    } else {
                        adapter.onRefresh();//通知headerView开始刷新
                        if (iOnRefreshListener != null) {//通知外部调用开始刷新
                            iOnRefreshListener.onRefresh();
                        }
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isStopAnima) {
                    animation.cancel();//取消动画
                    return;
                }
                scrollTo(0, (Integer) animation.getAnimatedValue());
            }
        });
        valueAnimator.setDuration(300)
                .start();
    }


    /**
     * 判断子视图能否滚动（是否到达最上端）
     *
     * @return
     */
    private boolean isChildViewCanScroll() {
        if (contentView != null) {
            /*if (contentView instanceof ScrollView) {
                if (getScrollY() < 0) {
                    return false;
                }
                if (contentView.getScrollY() == 0 && !isUp) {
                    return false;
                } else {
                    return true;
                }
            }*/
            if (getScrollY() < 0) {
                return false;
            }
            if (contentView.getScrollY() == 0 && !isUp) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 每次事件的分发都是分发完整的一个事件序列（ACTION_DOWN   ACTION_MOVE   ACTION_UP）
     * 也就是说，父View和子View   在分发过程中  共享一个完整的事件序列
     * 为了避免由父View  分发事件给子View 造成touch事件的触摸误差
     * 因此需要在父View  模拟一些touch事件  分发给子View
     * 模拟发送取消事件(当父view开始滑动时，通知childview 取消事件)
     */
    private void sendCancelEvent() {
        super.dispatchTouchEvent(MotionEvent.obtain(lastMotionEvent.getDownTime(), lastMotionEvent.getEventTime(), MotionEvent.ACTION_UP, lastMotionEvent.getX(), lastMotionEvent.getY(), lastMotionEvent.getMetaState()));
    }

    /**
     * 每次事件的分发都是分发完整的一个事件序列（ACTION_DOWN   ACTION_MOVE   ACTION_UP）
     * 也就是说，父View和子View   在分发过程中  共享一个完整的事件序列
     * 为了避免由父View  分发事件给子View 造成touch事件的触摸误差
     * 因此需要在父View  模拟一些touch事件  分发给子View
     * 模拟发送按下事件(当父view停止滑动时，通知childview 按下事件)
     * 按下位置为最新的event事件的位置
     */
    private void sendDownEvent() {
        super.dispatchTouchEvent(MotionEvent.obtain(lastMotionEvent.getDownTime(), lastMotionEvent.getEventTime(), MotionEvent.ACTION_DOWN, lastMotionEvent.getX(), lastMotionEvent.getY(), lastMotionEvent.getMetaState()));
    }

    /**
     * 每次事件的分发都是分发完整的一个事件序列（ACTION_DOWN   ACTION_MOVE   ACTION_UP）
     * 也就是说，父View和子View   在分发过程中  共享一个完整的事件序列
     * 为了避免由父View  分发事件给子View 造成touch事件的触摸误差
     * 因此需要在父View  模拟一些touch事件  发送给自己
     * 模拟发送取消事件(当父view开始滑动时，通知自己 按下事件)
     */
    private void sendCancelEventFather() {
        dispatchTouchEvent(MotionEvent.obtain(lastMotionEvent.getDownTime(), lastMotionEvent.getEventTime(), MotionEvent.ACTION_UP, lastMotionEvent.getX(), lastMotionEvent.getY(), lastMotionEvent.getMetaState()));
    }

    /**
     * 每次事件的分发都是分发完整的一个事件序列（ACTION_DOWN   ACTION_MOVE   ACTION_UP）
     * 也就是说，父View和子View   在分发过程中  共享一个完整的事件序列
     * 为了避免由父View  分发事件给子View 造成touch事件的触摸误差
     * 因此需要在父View  模拟一些touch事件  发送自己
     * 模拟发送按下事件(当父view停止滑动时，通知childview 按下事件，通知自己取消事件)
     * 按下位置为最新的event事件的位置
     */
    private void sendDownEventFather() {
        dispatchTouchEvent(MotionEvent.obtain(lastMotionEvent.getDownTime(), lastMotionEvent.getEventTime(), MotionEvent.ACTION_DOWN, lastMotionEvent.getX(), lastMotionEvent.getY(), lastMotionEvent.getMetaState()));
    }


}
