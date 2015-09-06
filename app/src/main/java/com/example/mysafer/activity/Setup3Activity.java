package com.example.mysafer.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.mysafer.R;

public class Setup3Activity extends BaseSetupActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

    }

    @Override
    public void showPrevPage() {
        //上一页
        startActivity(new Intent(Setup3Activity.this, Setup2Activity.class));
        finish();
        overridePendingTransition(R.anim.prev_in, R.anim.prev_out);
    }

    @Override
    public void showNextPage() {
        //下一页
        startActivity(new Intent(Setup3Activity.this, Setup4Activity.class));
        finish();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

}
