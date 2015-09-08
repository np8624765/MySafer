package com.example.mysafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.mysafer.R;
import com.example.mysafer.service.AddressService;
import com.example.mysafer.untils.ServiceStatusUtils;
import com.example.mysafer.views.SettingItemView;

//设置中心界面
public class SettingActivity extends Activity {

    private SettingItemView sivUpdate;
    private SettingItemView sivAddress;
    private SharedPreferences sp;

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

}
