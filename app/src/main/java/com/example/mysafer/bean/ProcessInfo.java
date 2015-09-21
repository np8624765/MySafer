package com.example.mysafer.bean;

import android.graphics.drawable.Drawable;

/**
 * 作者：陈志辉
 * 日期：2015/9/16 15:56
 * 描述：
 */
public class ProcessInfo {
    private Drawable icon;
    private String appName;
    private String packageName;
    private long memorySize;
    private boolean userApk;
    private boolean isCheck;

    public ProcessInfo() {
    }

    public ProcessInfo(Drawable icon, String appName, String packageName, long memorySize,  boolean userApk) {
        this.icon = icon;
        this.appName = appName;
        this.memorySize = memorySize;
        this.packageName = packageName;
        this.userApk = userApk;
        //判断是否被选中
        this.isCheck = false;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(long memorySize) {
        this.memorySize = memorySize;
    }

    public boolean isUserApk() {
        return userApk;
    }

    public void setUserApk(boolean userApk) {
        this.userApk = userApk;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
}
