package com.example.mysafer.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;

import com.example.mysafer.activity.InputPwdActivity;
import com.example.mysafer.db.AppLockDao;

import java.util.List;

//监控用户点击应用
public class WatchDogService extends Service {

    private boolean flag = false;

    private ActivityManager activityManager;
    private AppLockDao appLockDao;

    private String tempStopProtectPackageName;

    class WatchDogReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.example.mysafer.stopprotect")) {
                tempStopProtectPackageName = intent.getStringExtra("packageName");
            }
        }
    }

    public WatchDogService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        tempStopProtectPackageName = "";
        appLockDao = new AppLockDao(this);
        activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        //动态注册广播接收者
        WatchDogReceiver watchDogReceiver = new WatchDogReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.mysafer.stopprotect");

        registerReceiver(watchDogReceiver, intentFilter);
        //获取任务栈
        startWatchDog();
    }

    private void startWatchDog() {
        new Thread(){
            @Override
            public void run() {
                flag = true;
                while(flag) {
                    //获取1个应用栈
                    List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo taskInfo = tasks.get(0);
                    String packageName = taskInfo.topActivity.getPackageName();
                    //System.out.println("--------------------"+packageName);
                    if(appLockDao.isLocked(packageName)) {
                        if(!tempStopProtectPackageName.equals(packageName)) {
                            //从服务中启动Activity
                            Intent intent = new Intent(WatchDogService.this, InputPwdActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packageName", packageName);
                            startActivity(intent);
                        }
                    }
                    SystemClock.sleep(500);
                }

            }
        }.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
    }
}
