package com.moka.carsharing.model;

/**
 * Created by blablabla on 2017/5/13.
 */

public class IndentModel {
    public String payTimeRemain;
    public String station;
    public String status;
    public String actualPrice;
    public String price;
    public String youhuiPrice;
    public String tradeTime;
    public String orderNum;
    public String id;
    public String orderType;
    public String batteryModel;
    public String validCount;
    public String electricity;
    public String electricityOfBefore;
    public String payType;

    public void setYouhuiPrice(String youhuiPrice) {
        this.youhuiPrice = youhuiPrice;
    }

    public String getYouhuiPrice() {
        return youhuiPrice;
    }

    public void setActualPrice(String actualPrice) {
        this.actualPrice = actualPrice;
    }

    public String getActualPrice() {
        return actualPrice;
    }

    public String getBatteryModel() {
        return batteryModel;
    }

    public void setBatteryModel(String batteryModel) {
        this.batteryModel = batteryModel;
    }

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

    public String getValidCount() {
        return validCount;
    }

    public void setValidCount(String validCount) {
        this.validCount = validCount;
    }

    public void setElectricity(String electricity) {
        this.electricity = electricity;
    }

    public void setElectricityOfBefore(String electricityOfBefore) {
        this.electricityOfBefore = electricityOfBefore;
    }

    public String getElectricity() {
        return electricity;
    }

    public String getElectricityOfBefore() {
        return electricityOfBefore;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayType() {
        return payType;
    }
}

