package com.moka.carsharing.model;

import com.amap.api.maps2d.model.LatLng;

import java.io.Serializable;

/**
 * Created by Burgess on 2017/4/17.
 */

public class CarPositionModel implements Serializable{
    public String id;
    public String lat;
    public String lon;
    public String name;
    public String detail;
    public String validity;
    public String batteryType;

    public CarPositionModel(String id, String name, String detail, String validity, String batteryType) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.validity = validity;
        this.batteryType = batteryType;
    }

    public void setLng(String lat, String lon){
        this.lat = lat;
        this.lon = lon;
    }

    public void setLng(LatLng lng){
        this.lat = String.valueOf(lng.latitude);
        this.lon = String.valueOf(lng.longitude);
    }

    public LatLng getLng(){
        return new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
    }
}
