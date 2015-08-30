package com.example.mysafer.untils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Along on 2015/8/29.
 * 用于读取流的工具类
 */
public class SteamUtils {

    //将字节输入流解析成字符串
    public static String readFromStream(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len = 0;
        byte[] buf = new byte[1024];
        if((len=in.read(buf))!=-1) {
            out.write(buf, 0, len);
        }
        String result = out.toString();
        in.close();
        out.close();
        return result;
    }
}
