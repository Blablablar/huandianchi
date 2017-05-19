package com.haundianchi.huandianchi.model;

import com.amap.api.maps2d.model.LatLng;

/**
 * Created by Burgess on 2017/4/17.
 */

public class CarPositionModel {
    public String id;
    public String lat;
    public String lon;
    public String name;
    public String detail;

    public CarPositionModel(String id, String name, String detail) {
        this.id = id;
        this.name = name;
        this.detail = detail;
    }

    public void setLng(String lat, String lon){
        this.lat = lat;
        this.lon = lon;
    }

    public void setLng(LatLng lng){
        this.lat = String.valueOf(lng.latitude);
        this.lon = String.valueOf(lng.longitude);
    }
}
