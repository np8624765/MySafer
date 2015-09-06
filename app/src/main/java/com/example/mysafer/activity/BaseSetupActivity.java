package com.example.mysafer.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Along on 2015/9/6.
 * 设置向导页的基类，注意不需要在Manifest中注册，因为该页面不需要界面展示
 */
public abstract class BaseSetupActivity extends Activity{

    private GestureDetector gd;
    protected SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //存储设置信息
        sp = getSharedPreferences("config", MODE_PRIVATE);
        //手势控制
        gd = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //判断Y轴滑动的大小
                if (Math.abs(e2.getRawY() - e1.getRawY()) < 100) {
                    //上一页
                    if(e2.getRawX()-e1.getRawX() > 100) {
                        showPrevPage();
                    }
                    //下一页
                    if(e1.getRawX()-e2.getRawX() > 100) {
                        showNextPage();
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    //显示上一页,作为抽象方法，让子类必须实现
    public abstract void showPrevPage();

    //显示下一页，作为抽象方法，让子类必须实现
    public abstract void showNextPage();

    //点击按钮 下一页
    public void next(View v) {
        //下一页
        showNextPage();
    }

    //点击按钮上一页
    public void prev(View v) {
        //上一页
        showPrevPage();
    }

    //监听触屏事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //交给手势控制器去监听
        gd.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
