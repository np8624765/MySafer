package com.example.mysafer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
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
                String simSerialNumber = tm.getSimSerialNumber()+"1";
                //比较sp中保存的SIM号，和当前手机SIM号
                if(simNumber.equals(simSerialNumber)) {
                    System.out.println("手机SIM未变化");
                }else {
                    //发送报警短信
                    String phone = sp.getString("safe_phone", "");
                    SmsManager sm = SmsManager.getDefault();
                    sm.sendTextMessage(phone, null, "SIM card has changed!", null, null);
                }
            }
        }
    }
}
