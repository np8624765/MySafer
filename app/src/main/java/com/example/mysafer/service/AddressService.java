package com.example.mysafer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.mysafer.R;
import com.example.mysafer.db.AddressDao;

//来电归属地，是用系统提供的管理器实现
public class AddressService extends Service {

    private TelephonyManager tm;
    private MyListener myListener;
    private OutCallReceiver receiver;
    private WindowManager wm;
    private View tvToast;
    private SharedPreferences sp;

    private int startX;
    private int startY;

    private int winWidth;
    private int winHeigh;

    class MyListener extends PhoneStateListener {
        //当电话状态发生改变时
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //System.out.println("来电...");
                    String address = AddressDao.getAddress(incomingNumber);
                    showToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:  //电话闲置状态,将自定义Toast取消
                    if (wm!=null&&tvToast!=null) {
                        wm.removeView(tvToast);
                        tvToast = null;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //动态就开启广播接收站，用于监听是否拨号
    class OutCallReceiver extends BroadcastReceiver {
        public OutCallReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取去电号码
            String number = getResultData();
            String address = AddressDao.getAddress(number);
            //Toast.makeText(context, address, Toast.LENGTH_LONG).show();
            showToast(address);
        }
    }

    public AddressService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //存储设置信息
        sp = getSharedPreferences("config", MODE_PRIVATE);

        tm  = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        myListener = new MyListener();
        //监听电话发生状态的变化
        tm.listen(myListener, PhoneStateListener.LISTEN_CALL_STATE);

        receiver = new OutCallReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //停止监听
        tm.listen(myListener, PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(receiver);
    }

    //自定义Toast弹窗
    private void showToast(String text) {

        //使用windowManager可以在其他应用界面中显示自己的窗口
        wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);

        //获得屏幕尺寸
        winWidth = wm.getDefaultDisplay().getWidth();
        winHeigh = wm.getDefaultDisplay().getHeight();

        //初始化布局参数
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        //将中心设为屏幕左上角（0.0）
        params.gravity = Gravity.LEFT + Gravity.TOP;
        params.setTitle("Toast");
        //设置相对于重心的偏移量
        params.x = sp.getInt("LastX", 0);
        params.y = sp.getInt("LastY", 0);

        //tvToast = new TextView(this);
        tvToast = View.inflate(this, R.layout.toast_address, null);
        //根据sp中存储的样式，更新提示框背景
        int[] bgs = new int[]{R.drawable.address_gray, R.drawable.address_blue,
                R.drawable.address_green, R.drawable.address_purple, R.drawable.address_red,
                R.drawable.address_yellow};
        tvToast.setBackgroundResource(bgs[sp.getInt("address_style", 0)]);
        TextView tvAddress = (TextView)tvToast.findViewById(R.id.tv_address);
        tvAddress.setText(text);
        wm.addView(tvToast, params);

        tvToast.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();
                        int dx = endX - startX;
                        int dy = endY - startY;
                        params.x += dx;
                        params.y += dy;
                        //防止坐标偏离
                        if (params.x < 0){
                            params.x = 0;
                        }
                        if (params.x > winWidth-tvToast.getWidth()){
                            params.x = winWidth-tvToast.getWidth();
                        }
                        if(params.y < 0){
                            params.y = 0;
                        }
                        if(params.y > winHeigh-tvToast.getHeight()){
                            params.y = winHeigh-tvToast.getHeight();
                        }
                        wm.updateViewLayout(tvToast, params);
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        sp.edit().putInt("LastX", params.x).commit();
                        sp.edit().putInt("LastY", params.y).commit();
                        break;
                }
                return true;
            }
        });
    }
}
