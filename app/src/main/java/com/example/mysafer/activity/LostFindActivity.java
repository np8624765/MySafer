package com.example.mysafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.mysafer.R;

public class LostFindActivity extends Activity {

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //判断是否已经进入过向导设置
        sp = getSharedPreferences("config", MODE_PRIVATE);
        if(sp.getBoolean("configed", false)) {
            setContentView(R.layout.activity_lost_find);
        }else {
            //进入设置向导
            startActivity(new Intent(LostFindActivity.this, Setup1Activity.class));
            finish();
        }
    }

    //重新进入设置向导
    public void reEnter(View v) {
        startActivity(new Intent(this, Setup1Activity.class));
        finish();
    }


}
