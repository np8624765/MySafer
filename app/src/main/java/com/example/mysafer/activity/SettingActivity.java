package com.example.mysafer.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.mysafer.R;
import com.example.mysafer.views.SettingItemView;

public class SettingActivity extends Activity {

    private SettingItemView sivUpdate;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //存储设置信息
        sp = getSharedPreferences("config", MODE_PRIVATE);

        //更新设置
        sivUpdate = (SettingItemView)findViewById(R.id.siv_update);
        sivUpdate.setTitle("自动检查更新设置");
        sivUpdate.setChecked(sp.getBoolean("auto_update", true));
        sivUpdate.setDesc(sivUpdate.isChecked() ? "已开启" : "未开启");
        sivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换勾选状态
                sivUpdate.setChecked(!sivUpdate.isChecked());
                sivUpdate.setDesc(sivUpdate.isChecked() ? "已开启" : "未开启");
                //更新sp
                sp.edit().putBoolean("auto_update", sivUpdate.isChecked()).commit();
            }
        });
    }


}
