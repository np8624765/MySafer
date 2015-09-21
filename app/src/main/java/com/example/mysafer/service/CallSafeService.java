package com.example.mysafer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.example.mysafer.db.BlankNumberDao;

import java.lang.reflect.Method;

public class CallSafeService extends Service {

    private BlankSMSReceiver blankSMSReceiver;
    private BlankNumberDao dao;
    private TelephonyManager tm;


    //黑名单短信广播接受者
    class BlankSMSReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取短信信息
            Object[] objects = (Object[])intent.getExtras().get("pdus");
            for (Object o : objects) {
                //获得一条短信
                SmsMessage message = SmsMessage.createFromPdu((byte[]) o);
                //获得短信的发送者的号码
                String origin = message.getOriginatingAddress();
                int mode = dao.findMode(origin);
                if(mode==0||mode==2) {
                    abortBroadcast();
                }

            }
        }
    }

    //监听系统电话状态
    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                //电话铃响,判断是否在黑名单中
                case TelephonyManager.CALL_STATE_RINGING:
                    int mode = dao.findMode(incomingNumber);
                    if(mode==0||mode==1) {
                        //通过内存提供者清除通话记录的黑名单号码
                        Uri uri = Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri, true,
                                new MyContentObserver(new Handler(), incomingNumber));
                        //挂断电话
                        enCall();
                    }
                    break;
            }
        }
    }

    //自定义内存观察者
    class MyContentObserver extends ContentObserver {
        private String incomingNumber;
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            getContentResolver().unregisterContentObserver(this);
            deleteCallLog(incomingNumber);
            super.onChange(selfChange);
        }
    }

    public CallSafeService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化数据库
        dao = new BlankNumberDao(this);
        //获取系统的电话服务
        tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        tm.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
        //注册短信广播接收者
        blankSMSReceiver = new BlankSMSReceiver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(blankSMSReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(blankSMSReceiver);
    }

    //挂断电话,通过AIDL调用不同进程的方法
    private void enCall() {
        try {
            //通过类加载器得到ServiceManage类
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
            //通过反射得到getService方法
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (IBinder)method.invoke(null, TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除通话记录
    private void deleteCallLog(String incomingNumber) {
        Uri uri = Uri.parse("content://call_log/calls");
        getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});
    }
}
