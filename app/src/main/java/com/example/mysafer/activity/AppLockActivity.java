package com.example.mysafer.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.mysafer.R;
import com.example.mysafer.fragment.LockedFragment;
import com.example.mysafer.fragment.UnLockFragment;

public class AppLockActivity extends FragmentActivity {

    private FrameLayout flContent;
    private TextView tvUnlock;
    private TextView tvLocked;

    private FragmentManager fragmentManager;
    private LockedFragment lockedFragment;
    private UnLockFragment unLockFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    public void initUI() {
        setContentView(R.layout.activity_app_lock);
        flContent = (FrameLayout)findViewById(R.id.fl_content);
        tvUnlock = (TextView)findViewById(R.id.tv_unlock);
        tvLocked = (TextView)findViewById(R.id.tv_locked);
        //未加锁按钮
        tvUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                tvUnlock.setBackgroundResource(R.drawable.tab_left_pressed);
                tvLocked.setBackgroundResource(R.drawable.tab_right_default);
                fragmentTransaction.replace(R.id.fl_content, unLockFragment).commit();
            }
        });
        //已加锁按钮
        tvLocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                tvUnlock.setBackgroundResource(R.drawable.tab_left_default);
                tvLocked.setBackgroundResource(R.drawable.tab_right_pressed);
                fragmentTransaction.replace(R.id.fl_content, lockedFragment).commit();
            }
        });
        //获取到Fragment管理者
        fragmentManager = getFragmentManager();
        //开启事务,默认加载unlock的Fragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        lockedFragment = new LockedFragment();
        unLockFragment = new UnLockFragment();
        fragmentTransaction.replace(R.id.fl_content, unLockFragment).commit();
    }

}
