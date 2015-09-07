package com.example.mysafer.bean;

/**
 * Created by Along on 2015/9/6.
 */
public class Contacts {
    private String name;
    private String phone;

    public Contacts() {

    }

    public Contacts(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "姓名：" + name + ",电话：" + phone;
    }
}
