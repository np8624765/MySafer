package com.example.mysafer.bean;

/**
 * Created by Along on 2015/9/15.
 */
public class MyMessage {
    private String body;
    private String date;
    private String address;
    private String type;

    public MyMessage(String body, String date, String address, String type) {
        setBody(body);
        setDate(date);
        setAddress(address);
        setType(type);
    }
    public void setBody(String body) {
        this.body = body;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }
}
