package com.example.mysafer.untils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.example.mysafer.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Along on 2015/9/14.
 */
public class AppUtils {
    public static List<AppInfo> getAppInfos(Context context) {
        List<AppInfo> appsInfos = new ArrayList<AppInfo>();
        //获取包管理者
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for(PackageInfo packageInfo : installedPackages) {
            //获取到应用程序的图标
            Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
            //获取应用名称
            String apkName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            //获取包名
            String packageName = packageInfo.packageName;
            //获取安装路径
            String sourceDir = packageInfo.applicationInfo.sourceDir;
            File file = new File(sourceDir);
            //获取apk大小
            long length = file.length();
            //判断是够为用户应用
            boolean userApp = sourceDir.startsWith("/data");
            //判断安装位置
            int isExternal = 0;
            try {
                isExternal = packageManager.getApplicationInfo(packageName,0).FLAG_EXTERNAL_STORAGE;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            boolean isRom = isExternal==1 ? false : true;
            AppInfo info = new AppInfo(drawable, apkName, packageName, length, userApp, isRom);
            appsInfos.add(info);
        }
        return appsInfos;
    }


}
