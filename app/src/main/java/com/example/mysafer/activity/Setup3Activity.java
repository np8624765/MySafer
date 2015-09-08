package com.example.mysafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mysafer.R;

//手机防盗设置向导3界面
public class Setup3Activity extends BaseSetupActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        String phone = sp.getString("safe_phone", "");
        ((EditText)findViewById(R.id.et_safe_phone)).setText(phone);
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
        //没有填写号码，不允许进行下一步
        String phone = ((EditText)findViewById(R.id.et_safe_phone)).getText().toString();
        if(TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "报警号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //将安全号码保存
        sp.edit().putString("safe_phone", phone).commit();

        //下一页
        startActivity(new Intent(Setup3Activity.this, Setup4Activity.class));
        finish();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    public void choice(View v) {
        //startActivity(new Intent(Setup3Activity.this, ContactsActivity.class));
        startActivityForResult(new Intent(Setup3Activity.this, ContactsActivity.class), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK) {
            String phone = data.getStringExtra("phone").replaceAll("-", " ").replaceAll(" ", "");
            ((EditText)findViewById(R.id.et_safe_phone)).setText(phone);
        }
    }
}
