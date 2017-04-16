package com.haundianchi.huandianchi.model;

/**
 * Created by Burgess on 2017/4/16.
 */

public class TicketModel {
    public String name;
    public String date;
    public String city;
    public String property;
    public int price;

    public TicketModel(String name, String date, String city, String property, int price) {
        this.name = name;
        this.date = date;
        this.city = city;
        this.property = property;
        this.price = price;
    }
}
