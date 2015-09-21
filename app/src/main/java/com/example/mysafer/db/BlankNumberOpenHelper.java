package com.example.mysafer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Along on 2015/9/11.
 */
public class BlankNumberOpenHelper extends SQLiteOpenHelper {
    public BlankNumberOpenHelper(Context context) {
        super(context, "mysafer.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //新建数据表
        db.execSQL(
                "create table blanknumber" +
                        " (_id integer primary key autoincrement, number varchar(20), mode int(1))");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
