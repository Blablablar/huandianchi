package com.haundianchi.huandianchi.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import static com.haundianchi.huandianchi.Http.VolleyRequest.context;

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

}
