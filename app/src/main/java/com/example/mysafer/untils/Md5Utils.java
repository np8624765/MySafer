package com.example.mysafer.untils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Along on 2015/9/1.
 */
public class Md5Utils {
    public static String encode(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(str.getBytes());
            StringBuilder result = new StringBuilder();
            for(byte b : digest) {
                int i = b & 0xff;
                String hex = Integer.toHexString(i);
                if(hex.length()<2) {
                    hex = '0' + hex;
                }
                result.append(hex);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileMd5(String sourceDir) {
        File file = new File(sourceDir);
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            FileInputStream in = new FileInputStream(file);
            byte[] buf = new byte[1024];
            int len = 0;
            while((len=in.read(buf))!=-1) {
                messageDigest.update(buf, 0 ,len);
            }
            byte[] digest = messageDigest.digest();
            StringBuilder result = new StringBuilder();
            for(byte b : digest) {
                int i = b & 0xff;
                String hex = Integer.toHexString(i);
                if(hex.length()<2) {
                    hex = '0' + hex;
                }
                result.append(hex);
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
