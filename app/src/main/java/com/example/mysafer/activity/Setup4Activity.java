package com.example.mysafer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mysafer.R;
import com.example.mysafer.views.SettingItemView;

//手机防盗设置向导4界面
public class Setup4Activity extends BaseSetupActivity {

    private SettingItemView sivFd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        sivFd = (SettingItemView)findViewById(R.id.siv_sjfd);
        sivFd.setChecked(sp.getBoolean("sjfd", false));
        sivFd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换勾选状态
                sivFd.setChecked(!sivFd.isChecked());
                //更新sp
                sp.edit().putBoolean("sjfd", sivFd.isChecked()).commit();
            }
        });

    }

    @Override
    public void showPrevPage() {
        //上一页
        startActivity(new Intent(Setup4Activity.this, Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.prev_in, R.anim.prev_out);
    }

    @Override
    public void showNextPage() {
        //下一页
        startActivity(new Intent(Setup4Activity.this, LostFindActivity.class));
        //已经进入过设置向导
        sp.edit().putBoolean("configed", true).commit();
        finish();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

}
