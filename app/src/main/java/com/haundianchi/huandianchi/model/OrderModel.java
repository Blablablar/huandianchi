package com.haundianchi.huandianchi.model;

/**
 * Created by Burgess on 2017/4/27.
 */

public class OrderModel {
    public String date;
    public String position;
    public String content;
    public double price;

    public OrderModel(String date, String position, String content, double price) {
        this.date = date;
        this.position = position;
        this.content = content;
        this.price = price;
    }
}
