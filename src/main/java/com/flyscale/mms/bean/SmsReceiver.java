package com.flyscale.mms.bean;

/**
 * Created by MrBian on 2018/1/16.
 */

public class SmsReceiver {
    public String name;
    public boolean isContact;
    public String phone;


    public SmsReceiver(boolean isContact, String phone) {
        this.isContact = isContact;
        this.phone = phone;
    }

    public SmsReceiver(String name, boolean isContact, String phone) {
        this.name = name;
        this.isContact = isContact;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "SmsReceiver{" +
                "name='" + name + '\'' +
                ", isContact=" + isContact +
                ", phone='" + phone + '\'' +
                '}';
    }
}
