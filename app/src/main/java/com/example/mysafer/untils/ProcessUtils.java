package com.example.mysafer.untils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.text.TextUtils;

import com.example.mysafer.bean.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：陈志辉
 * 日期：2015/9/16 15:55
 * 描述：
 */
public class ProcessUtils {
    public static List<ProcessInfo> getProcessInfos(Context context) {
        List<ProcessInfo> infos = new ArrayList<>();
        //获取包管理器
        PackageManager packageManager = (PackageManager)context.getPackageManager();
        //获取进程管理器
        ActivityManager activityManager =
                (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取手机上正在运行的进程
        List<ActivityManager.RunningAppProcessInfo>  runningAppProcesses =
                activityManager.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            //获取进程名
            String packageName = info.processName;
            Drawable icon = null;
            String appName = null;
            long memorySize = 0;
            boolean userApp = false;
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
                icon = packageInfo.applicationInfo.loadIcon(packageManager);
                appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();

                Debug.MemoryInfo[] MemoryInfo = activityManager.getProcessMemoryInfo(
                        new int[]{info.pid});
                memorySize = MemoryInfo[0].getTotalPrivateDirty()*1024;

                String sourceDir = packageInfo.applicationInfo.sourceDir;
                userApp = sourceDir.startsWith("/data");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if(!TextUtils.isEmpty(appName)) {
                ProcessInfo processInfo = new ProcessInfo(icon, appName, packageName, memorySize, userApp);
                infos.add(processInfo);
            }
        }
        return infos;
    }
}
