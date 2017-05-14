package com.haundianchi.huandianchi.model;

import org.json.JSONObject;

/**
 * Created by blablabla on 2017/5/13.
 */

public class IndentModel {
    public String payTimeRemain;
    public String station;
    public String status;
    public String price;
    public String tradeTime;
    public String orderNum;
    public String id;
    public String orderType;

    public void setId(String id) {
        this.id = id;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public void setPayTimeRemain(String payTimeRemain) {
        this.payTimeRemain = payTimeRemain;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getStation() {
        return station;
    }

    public String getPrice() {
        return price;
    }

    public String getPayTimeRemain() {
        return payTimeRemain;
    }

    public String getStatus() {
        return status;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public String getId() {
        return id;
    }

    public String getOrderType() {
        return orderType;
    }
}
