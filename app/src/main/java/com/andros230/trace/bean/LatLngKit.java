package com.andros230.trace.bean;


import com.andros230.trace.utils.util;

import cn.bmob.v3.BmobObject;

public class LatLngKit extends BmobObject {
    private String lat;
    private String lng;
    private String date;
    private String time;
    private String mark;


    public LatLngKit() {
        this.setTableName(util.macClassName);
    }


    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
