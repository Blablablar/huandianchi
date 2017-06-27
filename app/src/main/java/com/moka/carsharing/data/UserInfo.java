package com.moka.carsharing.data;

import android.graphics.Bitmap;

/**
 * Created by blablabla on 2017/5/9.
 */

public class UserInfo {
    private static String MobilePhoneStr = null;
    private static String NameStr = null;
    private static String CarIdStr = null;
    private static Bitmap HeadPortrait = null;

    public static String getNameStr() {
        return NameStr;
    }

    public static String getMobilePhoneStr() {
        return MobilePhoneStr;
    }

    public static Bitmap getHeadPortrait() {
        return HeadPortrait;
    }

    public static String getCarIdStr() {
        return CarIdStr;
    }

    public static void setNameStr(String nameStr) {
        NameStr = nameStr;
    }

    public static void setMobilePhoneStr(String mobilePhoneStr) {
        MobilePhoneStr = mobilePhoneStr;
    }

    public static void setHeadPortrait(Bitmap headPortrait) {
        HeadPortrait = headPortrait;
    }

    public static void setCarIdStr(String carIdStr) {
        CarIdStr = carIdStr;
    }

    public static void clear(){
        MobilePhoneStr=null;
        NameStr = null;
        CarIdStr = null;
        HeadPortrait = null;
    }
}
