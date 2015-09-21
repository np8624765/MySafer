package com.example.mysafer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 作者：陈志辉
 * 日期：2015/9/20 13:22
 * 描述：
 */
public class AppLockDao {

    private AppLockOpenHepler hepler;
    private SQLiteDatabase db;

    public AppLockDao(Context context){
        hepler= new AppLockOpenHepler(context);
    }

    //加锁
    public boolean locked(String packageName) {
        db = hepler.getWritableDatabase();
        ContentValues datas = new ContentValues();
        datas.put("packagename", packageName);
        long row = db.insert("info", null, datas);
        db.close();
        if(row!=-1) {
            return true;
        }
        return false;
    }

    //解锁
    public boolean unlock(String packageName) {
        db = hepler.getWritableDatabase();
        int result = db.delete("info", "packagename=?", new String[]{packageName});
        db.close();
        if(result==1) {
            return true;
        }
        return false;
    }

    //判断应用是否加锁
    public boolean isLocked(String packageName) {
        db = hepler.getReadableDatabase();
        Cursor cursor = db.query("info", new String[]{"packagename"}, "packagename=?"
                , new String[]{packageName}, null, null, null);
        if(cursor.moveToNext()) {
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }
}
