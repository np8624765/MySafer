package com.example.mysafer.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * 作者：陈志辉
 * 日期：2015/9/18 10:58
 * 描述：
 */
public class AntivirusDao {
    private static final String PATH = "data/data/com.example.mysafer/files/antivirus.db";

    public static String checkFileVirus(String md5) {
        String desc = null;
        //打开病毒数据库
        SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select desc from datable where md5=?", new String[]{md5});
        if(cursor.moveToNext()) {
            desc = cursor.getString(0);
        }
        cursor.close();;
        db.close();
        return desc;
    }

    public static void addVirus(String md5, String desc) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READWRITE);
        String result = checkFileVirus(md5);
        //不存在，则添加
        if(TextUtils.isEmpty(result)) {
            ContentValues datas = new ContentValues();
            datas.put("md5", md5);
            datas.put("desc", desc);
            datas.put("type", 6);
            datas.put("name", "Android.Adware.AirAD.a");
            db.insert("datable", null, datas);
        }
        db.close();
    }
}
