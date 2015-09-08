package com.example.mysafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mysafer.R;

//高级工具界面
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

}
