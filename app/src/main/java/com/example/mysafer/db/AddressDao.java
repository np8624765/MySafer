package com.example.mysafer.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Along on 2015/9/8.
 * 号码归属地数据库查询
 */
public class AddressDao {

    private static final String PATH = "data/data/com.example.mysafer/files/address.db";

    public static String getAddress(String number) {
        String address = "未知归属地";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        //正则表达式匹配手机号码(^1[3-8]\d{9}$)
        if(number.matches("^1[3-8]\\d{9}$")) {
            //使用已经存在的数据库
            Cursor cursor = db.rawQuery(
                    "Select data2.location from data1,data2 where data1.id = ? and data1.outkey = data2.id"
                    , new String[]{number.substring(0, 7)});
            if(cursor.moveToNext()) {
                address = cursor.getString(0);
            }
            cursor.close();
        }else if(number.matches("^\\d+$")) {
            switch (number.length()) {
                case 3:
                    address = "报警电话";
                    break;
                case 4:
                    address = "模拟器";
                    break;
                case 5:
                    address = "客服电话";
                    break;
                case 7:
                case 8:
                    address = "本地电话";
                    break;
                default:
                    //有可能是长途电话
                    if(number.startsWith("0")&&number.length()>10)
                    {
                        //截取区号
                        Cursor cursor = db.rawQuery("Select location from data2 where area = ?"
                                , new String[]{number.substring(1,4)});
                        if(cursor.moveToNext()) {
                            address = cursor.getString(0);
                        }else {
                            cursor.close();
                            cursor = db.rawQuery("Select location from data2 where area = ?"
                                    , new String[]{number.substring(1,3)});
                            if(cursor.moveToNext()) {
                                address = cursor.getString(0);
                            }
                            cursor.close();
                        }
                    }
                    break;
            }
        }
        //关闭数据库
        db.close();
        return address;
    }
}
