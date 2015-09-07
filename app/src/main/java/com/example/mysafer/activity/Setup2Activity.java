package com.example.mysafer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

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
                //将SIM卡的序列号存储起来，以方便下次使用
                if(sivSIM.isChecked()) {
                    TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
                    //获取序列号
                    String simSerialNumber = tm.getSimSerialNumber();
                    //System.out.println("序列号：" + simSerialNumber);
                    sp.edit().putString("simNumber", simSerialNumber).commit();
                }else {
                    sp.edit().remove("simNumber").commit();
                }
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
        if(!sivSIM.isChecked()){
            Toast.makeText(this, "必须绑定SIM卡", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(new Intent(Setup2Activity.this, Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }
}
