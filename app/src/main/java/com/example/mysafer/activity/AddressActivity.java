package com.example.mysafer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysafer.R;
import com.example.mysafer.db.AddressDao;

//归属地查询界面
public class AddressActivity extends Activity {

    private EditText etNumber;
    private TextView tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        etNumber = (EditText)findViewById(R.id.et_address_phone);
        tvAddress = (TextView)findViewById(R.id.tv_address);

        //监听文本框变化
        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                String address = AddressDao.getAddress(s.toString());
                tvAddress.setText("归属地："+address);
            }
        });
    }

    public void queryAddress(View view) {
        String number = etNumber.getText().toString().trim();
        if(!TextUtils.isEmpty(number)) {
            String address = AddressDao.getAddress(number);
            tvAddress.setText("归属地："+address);
        }else {
            Toast.makeText(this, "你输入电话号码后再进行查询", Toast.LENGTH_SHORT).show();
            vibrator();
        }

    }

    //手机震动
    private void vibrator() {
        Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
    }

}
