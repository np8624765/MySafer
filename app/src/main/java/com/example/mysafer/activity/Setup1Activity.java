package com.example.mysafer.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.mysafer.R;

public class Setup1Activity extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void showPrevPage() {

    }

    @Override
    public void showNextPage() {
        //下一页
        startActivity(new Intent(Setup1Activity.this, Setup2Activity.class));
        finish();
        //跳转Activity的动画效果
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }
}
