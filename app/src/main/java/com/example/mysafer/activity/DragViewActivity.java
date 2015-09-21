package com.example.mysafer.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mysafer.R;

public class DragViewActivity extends Activity {

    private TextView tvTop;
    private TextView tvBottom;
    private ImageView ivDrag;

    private int startX;
    private int startY;

    private SharedPreferences sp;

    private int winWidth;
    private int winHeigh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);

        sp = getSharedPreferences("config", MODE_PRIVATE);

        tvTop = (TextView)findViewById(R.id.tv_top);
        tvBottom = (TextView)findViewById(R.id.tv_bottom);
        ivDrag = (ImageView)findViewById(R.id.iv_drag);

        //获得屏幕尺寸
        winWidth = getWindowManager().getDefaultDisplay().getWidth();
        winHeigh = getWindowManager().getDefaultDisplay().getHeight();

        //计算显示提示框的位置，是输出在上还是下
        if(sp.getInt("LastY", 0)>(winHeigh/2)){
            tvBottom.setVisibility(View.INVISIBLE);
            tvTop.setVisibility(View.VISIBLE);
        }else {
            tvBottom.setVisibility(View.VISIBLE);
            tvTop.setVisibility(View.INVISIBLE);
        }

        //根据sp中保存的坐标，设置图片初始的位置
        //ivDrag.layout(sp.getInt("LastX", 0), sp.getInt("LastY", 0),
        //        sp.getInt("LastX", 0)+ivDrag.getWidth(),  sp.getInt("LastY", 0)+ivDrag.getHeight());
        //获取布局对象
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivDrag.getLayoutParams();
        layoutParams.leftMargin = sp.getInt("LastX", 0);
        layoutParams.topMargin =  sp.getInt("LastY", 0);
        ivDrag.setLayoutParams(layoutParams);

        //为图片设置触摸监听
        ivDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:           //鼠标按下，获取是起始点的坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:            //鼠标移动时，记录结束点，并计算偏移量
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();
                        int dx = endX - startX;
                        int dy = endY - startY;
                        //计算变化后的坐标
                        int left = ivDrag.getLeft() + dx;
                        int right = ivDrag.getRight() + dx;
                        int top = ivDrag.getTop() + dy;
                        int bottom = ivDrag.getBottom() + dy;
                        //要减去通知栏的高度
                        if (left < 0 || right > winWidth || top < 20 || bottom > winHeigh  ) {
                            break;
                        }
                        if (top > (winHeigh / 2)) {
                            tvBottom.setVisibility(View.INVISIBLE);
                            tvTop.setVisibility(View.VISIBLE);
                        } else {
                            tvBottom.setVisibility(View.VISIBLE);
                            tvTop.setVisibility(View.INVISIBLE);
                        }
                        //更新图片的坐标
                        ivDrag.layout(left, top, right, bottom);
                        //更新起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:              //鼠标抬起后，存储坐标
                        sp.edit().putInt("LastX", ivDrag.getLeft()).commit();
                        sp.edit().putInt("LastY", ivDrag.getTop()).commit();
                        break;
                    default:
                        break;
                }
                //把事件拦截
                return false;
            }
        });

    }

    //监听双击事件
    private long[] hits = new long[2];
    public void doubleClickListener(View view) {
        System.arraycopy(hits, 1, hits, 0 ,hits.length-1);
        hits[hits.length-1] = SystemClock.uptimeMillis();   //重开机之后开始计算的时间
        if(SystemClock.uptimeMillis()-hits[0]<500) {
            ivDrag.layout(winWidth/2 - ivDrag.getWidth()/2, winHeigh/2 - ivDrag.getHeight()/2,
                    winWidth/2 + ivDrag.getWidth()/2,   winHeigh/2 + ivDrag.getHeight()/2);
            sp.edit().putInt("LastX", ivDrag.getLeft()).commit();
            sp.edit().putInt("LastY", ivDrag.getTop()).commit();

        }
    }

}
