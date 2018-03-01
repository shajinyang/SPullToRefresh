package com.sjy.uitest.sndroiduitest.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

/**
 * 下雪花view
 * Created by sjy on 2018/2/9.
 */
public class SnowView extends View{
    private int viewWidth;//view的宽度
    private int viewHeight;//view的高度
    private boolean isGetBottom=false;//雪花是否到达底部,如果没有到达底部将会持续创建新的雪花
    private int begainNums=5;//起始雪花数
    private int addSnowDelay=500;//创建雪花延迟
    private int snowAvgSpeed;//雪花平均速度，所有雪花的速度均基于该速度浮动
    private int snowAvgRadius;//雪花的平均大小，所有雪花的大小均基于该大小浮动
    private Paint mPaint;
    private ArrayList<SnowBean> snowBeans=new ArrayList<>();
    public SnowView(Context context) {
        super(context);
        initPaint();
    }

    public SnowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public SnowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint(){
        mPaint=new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
    }

    private void initSnow(){
        for(int i=0;i<begainNums;i++){
            createSnow();
        }
        final Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addSnow();
                handler.postDelayed(this,addSnowDelay);
            }
        },addSnowDelay);
    }

    private void addSnow(){
        final Random randomNum=new Random();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isGetBottom==false){
                    int addSize=randomNum.nextInt(3);
                    for(int i=0;i<addSize;i++){
                        createSnow();
                    }
                }
            }
        },addSnowDelay);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth=MeasureSpec.getSize(widthMeasureSpec);
        viewHeight=MeasureSpec.getSize(heightMeasureSpec);
        if(snowBeans.size()==0){
            initSnow();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private class SnowBean{
        private int snowSpeed;//雪花速度
        private int snowRadius;//雪花半径
        private Double snowAngle;//雪花的角度(0-180)
        private float snowX;
        private float snowY;

        public int getSnowSpeed() {
            return snowSpeed;
        }

        public void setSnowSpeed(int snowSpeed) {
            this.snowSpeed = snowSpeed;
        }

        public int getSnowRadius() {
            return snowRadius;
        }

        public void setSnowRadius(int snowRadius) {
            this.snowRadius = snowRadius;
        }

        public Double getSnowAngle() {
            return snowAngle;
        }

        public void setSnowAngle(Double snowAngle) {
            this.snowAngle = snowAngle;
        }

        public float getSnowX() {
            return snowX;
        }

        public void setSnowX(float snowX) {
            this.snowX = snowX;
        }

        public float getSnowY() {
            return snowY;
        }

        public void setSnowY(float snowY) {
            this.snowY = snowY;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (SnowBean snowBean:snowBeans
             ) {
            if(snowBean.getSnowY()>viewHeight){
                isGetBottom=true;
            }
            if(snowBean.getSnowX()>viewWidth||snowBean.getSnowY()>viewHeight){
                reSetSnow(snowBean);//重置屏幕外的雪花
            }
            snowBean.setSnowX((float) (snowBean.getSnowX()+snowBean.getSnowSpeed()*Math.cos(Math.PI*snowBean.getSnowAngle()/180)));
            snowBean.setSnowY((float) (snowBean.getSnowY()+snowBean.getSnowSpeed()*Math.sin(Math.PI*snowBean.getSnowAngle()/180)));
            RadialGradient radialGradient = new RadialGradient(
                    snowBean.getSnowX(),snowBean.getSnowY(),
                   snowBean.getSnowRadius(),
                    new int[]{Color.WHITE,Color.WHITE,  Color.parseColor("#80ffffff")}, null,
                    Shader.TileMode.MIRROR
            );
            mPaint.setShader(radialGradient);
            canvas.drawCircle( snowBean.getSnowX()
                    ,snowBean.getSnowY()
            ,snowBean.getSnowRadius()
            ,mPaint);
            invalidate();
        }
    }

    private void createSnow(){
        Random randomStartX=new Random();
        Random randomRadius=new Random();
        Random randomAngle=new Random();
        Random randomSpeed=new Random();
        SnowBean snowBean=new SnowBean();
        snowBean.setSnowX(randomStartX.nextInt(viewWidth));
        snowBean.setSnowY(0);
        snowBean.setSnowAngle((double) (80+randomAngle.nextInt(20)));
        snowBean.setSnowSpeed(2+randomSpeed.nextInt(2));
        snowBean.setSnowRadius(10+randomRadius.nextInt(10));
        snowBeans.add(snowBean);
    }

    private void reSetSnow(SnowBean snowBean){
        Random randomStartX=new Random();
        Random randomRadius=new Random();
        Random randomAngle=new Random();
        Random randomSpeed=new Random();
        snowBean.setSnowX(randomStartX.nextInt(viewWidth));
        snowBean.setSnowY(0);
        snowBean.setSnowAngle((double) (80+randomAngle.nextInt(20)));
        snowBean.setSnowSpeed(2+randomSpeed.nextInt(2));
        snowBean.setSnowRadius(10+randomRadius.nextInt(10));
    }
}
