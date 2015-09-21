package com.example.mysafer.bean;

/**
 * 作者：陈志辉
 * 日期：2015/9/18 12:57
 * 描述：
 */
public class ScanInfo {
    private String apkName;
    private String packageName;
    private Boolean desc;

    public ScanInfo() {
    }

    public ScanInfo(String apkName, String packageName, Boolean desc) {
        this.apkName = apkName;
        this.packageName = packageName;
        this.desc = desc;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Boolean getDesc() {
        return desc;
    }

    public void setDesc(Boolean desc) {
        this.desc = desc;
    }
}
