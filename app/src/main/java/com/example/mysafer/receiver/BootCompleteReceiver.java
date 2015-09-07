package com.example.mysafer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

public class BootCompleteReceiver extends BroadcastReceiver {

    private SharedPreferences sp;

    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        //首先判断手机防盗功能是否开启
        if(sp.getBoolean("sjfd", false)) {
            //再判断SIM卡是否发生改变
            if(sp.getBoolean("SIM", false)) {
                String simNumber = sp.getString("simNumber", null);
                TelephonyManager tm = (TelephonyManager)context.getSystemService(
                        context.TELEPHONY_SERVICE);
                String simSerialNumber = tm.getSimSerialNumber();
                if(simNumber.equals(simSerialNumber)) {
                    System.out.println("手机SIM未变化");
                }else {
                    System.out.println("手机SIM已变化");
                }
            }
        }
    }
}
