package com.example.mysafer.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.mysafer.R;
import com.example.mysafer.service.LocationService;


//拦截短信
public class SmsReceiver extends BroadcastReceiver {

    private SharedPreferences sp;

    //设备管理器
    private DevicePolicyManager dpm;
    private ComponentName das;

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences(
                "config", context.MODE_PRIVATE);
        //手机防盗功能打开，才进行下面的功能
        if(sp.getBoolean("sjfd", false)) {
            //获取短信信息
            Object[] objects = (Object[])intent.getExtras().get("pdus");
            for (Object o : objects) {
                //获得一条短信
                SmsMessage message = SmsMessage.createFromPdu((byte[])o);
                //获得短信的发送者的号码
                String origin = message.getOriginatingAddress();
                //获得短信的内容
                String body = message.getMessageBody();
                System.out.println(origin+":"+body);
                //收到播放报警的声音
                if("#*alarm*#".equals(body)) {
                    //终止广播继续传递,Android4.4开始，拦截系统短信广播是无效的
                    abortBroadcast();
                    //播放报警音乐
                    MediaPlayer mp = MediaPlayer.create(context, R.raw.alarm);
                    mp.setVolume(0.3f, 0.3f);
                    mp.setLooping(true);
                    mp.start();
                }else if("#*location*#".equals(body)) {     //收到发生位置信息的短信
                    //开启定位服务
                    context.startService(new Intent(context, LocationService.class));
                    String location = sp.getString("location", "暂无位置信息");
                    //System.out.println("当前的经纬度为："+location);
                    //将经纬度发出
                    String phone = sp.getString("safe_phone", "");
                    SmsManager sm = SmsManager.getDefault();
                    sm.sendTextMessage(phone, null, location, null, null);
                }else if("#*wipedata*#".equals(body)) {     //清除数据
                    initDeviceManager(context);
                    wipeData(context);

                }else if("#*lockscreen*#".equals(body)) {   //锁屏
                    initDeviceManager(context);
                    lockScreen(context);
                }
            }
        }
    }

    //初始化设备管理器
    private void initDeviceManager(Context context) {
        //获得设备管理器
        dpm = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        //获取系统设备管理组件
        das = new ComponentName(context, AdminReceiver.class);
    }

    private void lockScreen(Context context) {
        //判断是否已经激活设备管理器
        if(dpm.isAdminActive(das)) {
            dpm.lockNow();  //立即锁屏
            dpm.resetPassword("123456", 0); //设置解锁密码
        }else {
            Toast.makeText(context, "必须先激活设备管理器", Toast.LENGTH_SHORT).show();
        }
    }

    private void wipeData(Context context) {
        //判断是否已经激活设备管理器
        if(dpm.isAdminActive(das)) {
            dpm.wipeData(0);    //清除数据，恢复出厂设置
        }else {
            Toast.makeText(context, "必须先激活设备管理器", Toast.LENGTH_SHORT).show();
        }
    }

}
