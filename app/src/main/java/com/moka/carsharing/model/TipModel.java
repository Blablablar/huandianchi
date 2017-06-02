package com.moka.carsharing.model;

import com.amap.api.maps2d.model.LatLng;

/**
 * Created by Burgess on 2017/5/21.
 */

public class TipModel {
    public String name;
    public String address;
    public String district;
    public LatLng latLng;

    public TipModel(String name, String address, String district, LatLng latLng) {
        this.name = name;
        this.address = address;
        this.district = district;
        this.latLng = latLng;
    }
}
