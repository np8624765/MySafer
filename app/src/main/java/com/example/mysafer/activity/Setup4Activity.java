package com.example.mysafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.mysafer.R;

public class Setup4Activity extends Activity {

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        sp = getSharedPreferences("config", MODE_PRIVATE);
    }

    public void next(View v) {
        //下一页
        startActivity(new Intent(Setup4Activity.this, LostFindActivity.class));
        //已经进入过设置向导
        sp.edit().putBoolean("configed", true).commit();
        finish();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    public void prev(View v) {
        //上一页
        startActivity(new Intent(Setup4Activity.this, Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.prev_in, R.anim.prev_out);
    }


}
