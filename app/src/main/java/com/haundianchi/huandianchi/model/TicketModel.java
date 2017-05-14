package com.haundianchi.huandianchi.model;

/**
 * Created by Burgess on 2017/4/16.
 */

public class TicketModel {
    public String id;
    public String name;
    public String date;
    public String city;
    public String property;
    public String price;

    public TicketModel(String id, String name, String date, String city, String property, String price) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.city = city;
        this.property = property;
        this.price = price;
    }
}
