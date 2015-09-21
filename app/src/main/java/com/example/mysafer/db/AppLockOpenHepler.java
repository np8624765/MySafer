package com.example.mysafer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 作者：陈志辉
 * 日期：2015/9/20 13:18
 * 描述：
 */
public class AppLockOpenHepler extends SQLiteOpenHelper {
    public AppLockOpenHepler(Context context) {
        super(context, "applock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //新建数据表
        db.execSQL(
                "create table info" +
                        " (_id integer primary key autoincrement, packagename varchar(255))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
