package com.example.mysafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.example.mysafer.R;
import com.example.mysafer.untils.SmsUtils;

/*
 * 高级工具
 */
public class AToolsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    //号码归属地查询
    public void numberAddressQuery(View view) {
        startActivity(new Intent(this, AddressActivity.class));
    }

    //短信备份
    public void smsBackup(View view) {
        new Thread(){
            @Override
            public void run() {
                if(SmsUtils.backup(AToolsActivity.this)) {
                    Looper.prepare();
                    Toast.makeText(AToolsActivity.this, "备份成功", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }else {
                    Looper.prepare();
                    Toast.makeText(AToolsActivity.this, "备份失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }.start();
    }

    //程序锁
    public void appLock(View view) {
        startActivity(new Intent(this, AppLockActivity.class));
    }
}
