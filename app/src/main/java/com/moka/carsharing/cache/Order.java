package com.moka.carsharing.cache;

import com.moka.carsharing.model.IndentModel;

import java.util.ArrayList;

/**
 * Created by blablabla on 2017/5/22.
 */

public class Order {
    public static ArrayList<IndentModel> unpay_list = new ArrayList<>();
    public static ArrayList<IndentModel> payed_list = new ArrayList<>();

    public static ArrayList<IndentModel> getUnPayList() {
        return unpay_list;
    }

    public static ArrayList<IndentModel> getPayedList() {
        return payed_list;
    }
}
