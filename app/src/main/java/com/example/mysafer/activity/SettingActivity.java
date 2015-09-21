package com.example.mysafer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.mysafer.R;
import com.example.mysafer.service.AddressService;
import com.example.mysafer.service.CallSafeService;
import com.example.mysafer.service.WatchDogService;
import com.example.mysafer.untils.ServiceStatusUtils;
import com.example.mysafer.views.SettingClickView;
import com.example.mysafer.views.SettingItemView;

//设置中心界面
public class SettingActivity extends Activity {

    private SettingItemView sivUpdate;
    private SettingItemView sivAddress;
    private SettingItemView sivCallSafe;
    private SettingItemView sivAppLock;

    private SettingClickView scvAddressStyle;
    private SettingClickView scvAddressLocation;


    private SharedPreferences sp;

    private static final String[] items = new String[]{"深空灰", "海天蓝", "青春绿", "妖媚紫", "热血红", "活力黄"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //存储设置信息
        sp = getSharedPreferences("config", MODE_PRIVATE);
        //监听更新设置
        updateListener();
        //监听去电来电归属地设置
        addressListener();
        //监听去电来电归属地显示风格
        addressStyleListener();
        //监听去电来电归属地显示位置按钮
        addressLocationListener();
        //黑名单监听
        blankNumberListener();
        //监听程序锁
        appLockListener();
    }

    //更新设置监听
    private void updateListener() {
        //更新设置
        sivUpdate = (SettingItemView)findViewById(R.id.siv_update);
        sivUpdate.setChecked(sp.getBoolean("auto_update", true));
        sivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换勾选状态
                sivUpdate.setChecked(!sivUpdate.isChecked());
                //更新sp
                sp.edit().putBoolean("auto_update", sivUpdate.isChecked()).commit();
            }
        });
    }

    //归属地设置监听
    private void addressListener() {
        //更新设置
        sivAddress = (SettingItemView)findViewById(R.id.siv_address);
        //判断服务是否运行
        boolean isRunning = ServiceStatusUtils.isServiceRunning(
                this, "com.example.mysafer.service.AddressService");
        sivAddress.setChecked(isRunning);
        sivAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换勾选状态
                sivAddress.setChecked(!sivAddress.isChecked());
                //更新sp
                sp.edit().putBoolean("show_address", sivAddress.isChecked()).commit();
                if(sivAddress.isChecked()) {
                    //开启归属地服务
                    startService(new Intent(SettingActivity.this, AddressService.class));
                }else {
                    //停止归属地服务
                    stopService(new Intent(SettingActivity.this, AddressService.class));
                }
            }
        });
    }

    //监听去电来电风格按钮
    private void addressStyleListener() {
        scvAddressStyle = (SettingClickView)findViewById(R.id.scv_address_style);
        //根据sp中保存的样式，显示名称
        scvAddressStyle.setDesc(items[sp.getInt("address_style", 0)]);
        scvAddressStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出单选框
                showSingleChooseDialog();
                //Toast.makeText(SettingActivity.this, "1111", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //监听去电来电提示框位置
    private void addressLocationListener() {
        scvAddressLocation = (SettingClickView)findViewById(R.id.scv_address_location);
        scvAddressLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, DragViewActivity.class));
            }
        });
    }

    //弹出选择归属地风格的单选框
    private void showSingleChooseDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("归属地显示框风格");

        //点击选择样式
        builder.setSingleChoiceItems(items, sp.getInt("address_style", 0), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sp.edit().putInt("address_style", which).commit();
                //将选择的样式保存在sp中
                scvAddressStyle.setDesc(items[which]);
                dialog.dismiss();
            }
        });
        //点击取消按钮
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    //黑名单监听
    private void blankNumberListener() {
        //更新设置
        sivCallSafe = (SettingItemView)findViewById(R.id.siv_callsafe);
        //判断服务是否运行
        boolean isRunning = ServiceStatusUtils.isServiceRunning(
                this, "com.example.mysafer.service.CallSafeService");
        sivCallSafe.setChecked(isRunning);
        sivCallSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换勾选状态
                sivCallSafe.setChecked(!sivCallSafe.isChecked());
                //更新sp
                sp.edit().putBoolean("call_safe", sivCallSafe.isChecked()).commit();
                if (sivCallSafe.isChecked()) {
                    //开启归属地服务
                    startService(new Intent(SettingActivity.this, CallSafeService.class));
                } else {
                    //停止归属地服务
                    stopService(new Intent(SettingActivity.this, CallSafeService.class));
                }
            }
        });
    }

    //手机程序锁
    private void appLockListener() {
        sivAppLock = (SettingItemView)findViewById(R.id.siv_applock);
        //判断服务是否运行
        boolean isRunning = ServiceStatusUtils.isServiceRunning(
                this, "com.example.mysafer.service.WatchDogService");
        sivAppLock.setChecked(isRunning);
        sivAppLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换勾选状态
                sivAppLock.setChecked(!sivAppLock.isChecked());
                //更新sp
                sp.edit().putBoolean("app_lock", sivAppLock.isChecked()).commit();
                if (sivAppLock.isChecked()) {
                    //开启归属地服务
                    startService(new Intent(SettingActivity.this, WatchDogService.class));
                } else {
                    //停止归属地服务
                    stopService(new Intent(SettingActivity.this, WatchDogService.class));
                }
            }
        });
    }
}
