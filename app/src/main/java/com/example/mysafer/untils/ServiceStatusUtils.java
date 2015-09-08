package com.example.mysafer.untils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 *
 * Created by Along on 2015/9/8.
 */
public class ServiceStatusUtils {

    //检测服务是否正在运行
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        //返回正在运行的服务集合
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100);
        for(ActivityManager.RunningServiceInfo r : runningServices) {
            //服务的名称
            String className = r.service.getClassName();
            if(className.equals(serviceName)){
                return true;
            }
        }
        return false;
    }
}
