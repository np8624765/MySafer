package com.example.mysafer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.example.mysafer.bean.BlankNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 黑名单数据库
 * Created by Along on 2015/9/11.
 */
public class BlankNumberDao {

    private  BlankNumberOpenHelper helper;
    private  SQLiteDatabase db;

    public BlankNumberDao(Context context) {
        helper = new BlankNumberOpenHelper(context);
    }

    //添加黑名单
    public boolean add(String number, int mode) {
        db = helper.getWritableDatabase();
        ContentValues datas = new ContentValues();
        datas.put("number", number);
        datas.put("mode", mode);
        long result = db.insert("blanknumber", null, datas);
        if(result==-1) {
            return false;
        }
        db.close();
        return true;
    }

    //删除黑名单,通过电话号码
    public boolean delete(String number) {
        db = helper.getWritableDatabase();
        int result = db.delete("blanknumber", "number=?", new String[]{number});
        if(result==0) {
            return false;
        }
        db.close();
        return true;
    }

    //修改模式
    public boolean changeNumberMode(String number, int mode) {
        db = helper.getWritableDatabase();
        ContentValues datas = new ContentValues();
        datas.put("mode", mode);
        int result = db.update("blanknumber", datas, "number=?", new String[]{number});
        if(result==0) {
            return false;
        }
        db.close();
        return true;
    }

    //通过号码，查询模式
    public int findMode(String number) {
        db = helper.getWritableDatabase();
        int mode = -1;
        Cursor cursor = db.query("blanknumber", new String[]{"mode"},
                "number=?", new String[]{number}, null, null, null);
        if (cursor.moveToNext()) {
            mode = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return mode;
    }

    //查找所有黑名单
    public List<BlankNumberInfo> findAll() {
        db = helper.getWritableDatabase();
        List<BlankNumberInfo> datas = new ArrayList<BlankNumberInfo>();
        Cursor cursor = db.query(
                "blanknumber", new String[]{"number", "mode"}, null, null, null, null, null);
        while(cursor.moveToNext()) {
            BlankNumberInfo info = new BlankNumberInfo(cursor.getString(0), cursor.getInt(1));
            datas.add(info);
        }
        cursor.close();
        db.close();
        //睡3秒，模拟网络加载延迟
        SystemClock.sleep(2000);
        return datas;
    }

    //获得黑名单的总数
    public int getCount() {
        db = helper.getWritableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery("select count(*) from blanknumber", null);
        if(cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    //分段获取黑名单信息
    public List<BlankNumberInfo> findSome(int start, int size) {
        db = helper.getWritableDatabase();
        List<BlankNumberInfo> datas = new ArrayList<BlankNumberInfo>();
        Cursor cursor =  db.rawQuery("select number,mode from blanknumber order by _id desc limit ?,? ",
                new String[]{String.valueOf(start), String.valueOf(size)});
        while(cursor.moveToNext()) {
            BlankNumberInfo info = new BlankNumberInfo(cursor.getString(0), cursor.getInt(1));
            datas.add(info);
        }
        cursor.close();
        db.close();
        return datas;
    }

}
