package com.flyscale.mms.bean;

import java.io.Serializable;

/**
 * Created by MrBian on 2018/1/13.
 */

public class SmsInfo implements Serializable {


    public SmsInfo() {
    }


    public SmsInfo(String smsbody, String phoneNumber, long date, String type, int
            read) {
        this.smsbody = smsbody;
        this.phoneNumber = phoneNumber;
        this.date = date;
        this.type = type;
        this.read = read;
    }

    public boolean isMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    private boolean mark = false;
    /**
     * 短信内容
     */
    private String smsbody;
    /**
     * 发送短信的电话号码
     */
    private String phoneNumber;
    /**
     * 发送短信的日期和时间
     */
    private long date;
    /**
     * 短信类型1是接收到的，2是已发出
     */
    private String type;
    private int read;
    private String person;
    private int id;
    private String service_center;

    @Override
    public String toString() {
        return "SmsInfo{" +
                "mark=" + mark +
                ", smsbody='" + smsbody + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", date=" + date +
                ", type='" + type + '\'' +
                ", read=" + read +
                ", person='" + person + '\'' +
                ", id=" + id +
                ", service_center='" + service_center + '\'' +
                '}';
    }

    public String getService_center() {
        return service_center;
    }

    public void setService_center(String service_center) {
        this.service_center = service_center;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SmsInfo(String smsbody, String phoneNumber, long date, String type, int read, String
            person, int id) {
        this.smsbody = smsbody;
        this.phoneNumber = phoneNumber;
        this.date = date;
        this.type = type;
        this.read = read;
        this.person = person;
        this.id = id;
    }

    public boolean isRead() {
        return read == 1;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String getSmsbody() {
        return smsbody;
    }

    public void setSmsbody(String smsbody) {
        this.smsbody = smsbody;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
