package com.example.mysafer.untils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;
import android.widget.Toast;

import com.example.mysafer.bean.MyMessage;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Along on 2015/9/15.
 * 短信工具类
 */
public class SmsUtils {
    //备份短信
    public static boolean backup(Context context) {
        //判断SD卡是否挂载
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms/");
            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"},
                    null, null, null);

            List<MyMessage> list = new ArrayList<>();
            while(cursor.moveToNext()) {
                MyMessage msg = new MyMessage(cursor.getString(3), cursor.getString(1),
                        cursor.getString(0), cursor.getString(2));
                list.add(msg);
            }
            cursor.close();
            xmlStorge(list);
            return true;
        }else{
            Toast.makeText(context, "未发现SD卡", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //用xml文件存储短信
    private static void xmlStorge(List<MyMessage> list) {
        XmlSerializer xs = Xml.newSerializer();
        try {
            xs.setOutput(
                    new FileOutputStream(Environment.getExternalStorageDirectory()+"/smsbackup.xml"), "utf-8");
            xs.startDocument("utf-8", true);
            xs.startTag(null, "smsbackup");
            for(MyMessage m : list){
                xs.startTag(null, "sms");

                xs.startTag(null, "body");
                xs.text(m.getBody());
                xs.endTag(null, "body");

                xs.startTag(null, "date");
                xs.text(m.getDate());
                xs.endTag(null, "date");

                xs.startTag(null, "address");
                xs.text(m.getAddress());
                xs.endTag(null, "address");

                xs.startTag(null, "type");
                xs.text(m.getType());
                xs.endTag(null, "type");

                xs.endTag(null, "sms");
            }
            xs.endTag(null, "smsbackup");
            xs.endDocument();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
