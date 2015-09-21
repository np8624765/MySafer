package com.example.mysafer.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Along on 2015/9/14.
 */
public class AppInfo {
    //图标
    private Drawable icon;
    //名称
    private String apkName;
    //包名
    private String apkPackageName;
    //大小
    private long apkSize;
    //应用类别
    private boolean userApp;
    //安装位置
    private boolean isRom;

    public AppInfo() {

    }

    public AppInfo(Drawable icon, String apkName, String apkPackageName, long apkSize, boolean userApp, boolean isRom) {
        this.icon = icon;
        this.apkName = apkName;
        this.apkPackageName = apkPackageName;
        this.apkSize = apkSize;
        this.userApp = userApp;
        this.isRom = isRom;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setIsRom(boolean isRom) {
        this.isRom = isRom;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public String getApkPackageName() {
        return apkPackageName;
    }

    public void setApkPackageName(String apkPackageName) {
        this.apkPackageName = apkPackageName;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    @Override
    public String toString() {
        return apkName;
    }
}
