package com.example.mysafer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mysafer.R;
import com.example.mysafer.views.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

    private SettingItemView sivSIM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        sivSIM = (SettingItemView)findViewById(R.id.siv_sim);
        sivSIM.setChecked(sp.getBoolean("SIM", false));
        sivSIM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换勾选状态
                sivSIM.setChecked(!sivSIM.isChecked());
                //更新sp
                sp.edit().putBoolean("SIM", sivSIM.isChecked()).commit();
            }
        });
    }

    //显示上一页
    @Override
    public void showPrevPage() {
        startActivity(new Intent(Setup2Activity.this, Setup1Activity.class));
        finish();
        overridePendingTransition(R.anim.prev_in, R.anim.prev_out);
    }

    //显示下一页
    @Override
    public void showNextPage() {
        startActivity(new Intent(Setup2Activity.this, Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }
}
