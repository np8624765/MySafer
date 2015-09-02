package com.example.mysafer.untils;

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
}
