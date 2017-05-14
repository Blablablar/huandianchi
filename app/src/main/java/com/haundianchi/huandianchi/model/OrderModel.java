package com.haundianchi.huandianchi.model;

/**
 * Created by Burgess on 2017/4/27.
 */

public class OrderModel {
    public String id;
    public String orderNum;
    public String date;
    public String position;
    public String content;
    public double price;

    public OrderModel(String id, String orderNum, String date, String position, String content, double price) {
        this.id = id;
        this.orderNum = orderNum;
        this.date = date;
        this.position = position;
        this.content = content;
        this.price = price;
    }
}
