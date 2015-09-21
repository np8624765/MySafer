package com.example.mysafer.bean;

/**
 * Created by Along on 2015/9/12.
 */
public class BlankNumberInfo {
    private String number;
    private int mode;

    public BlankNumberInfo() {

    }

    public BlankNumberInfo(String number, int mode) {
        this.number = number;
        this.mode = mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getMode() {
        return mode;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return number + ":" + mode;
    }
}
