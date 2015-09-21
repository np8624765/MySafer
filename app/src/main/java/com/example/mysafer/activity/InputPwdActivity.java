package com.example.mysafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mysafer.R;

public class InputPwdActivity extends Activity {

    private EditText etPwd;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageName = getIntent().getStringExtra("packageName");
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_input_pwd);
        etPwd = (EditText)findViewById(R.id.et_pwd);
        //设置键盘自动弹出，并聚焦在EditView
        etPwd.setFocusable(true);
        etPwd.setFocusableInTouchMode(true);
        etPwd.requestFocus();
        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(etPwd, 0);
    }

    public void pressOk(View view) {
        String pwd = etPwd.getText().toString();
        if(pwd.equals("123456")) {
            Intent intent = new Intent();
            intent.setAction("com.example.mysafer.stopprotect");
            intent.putExtra("packageName", packageName);
            sendBroadcast(intent);
            finish();
        }else {
            Toast.makeText(this, "密码错误！", Toast.LENGTH_SHORT).show();
        }
    }

}
