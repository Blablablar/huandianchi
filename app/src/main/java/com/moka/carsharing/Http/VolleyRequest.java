package com.moka.carsharing.Http;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.moka.carsharing.LIMSApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by blablabla on 2017/4/30.
 */

public class VolleyRequest{
    public static StringRequest stringRequest;
    public static Context context;
    public static String token="";
    public static String phoneNum="";
    public static String baseUrl="http://116.62.56.64/test";
    /*
    * 获取GET请求内容
    * 参数：
    * context：当前上下文；
    * url：请求的url地址；
    * tag：当前请求的标签；
    * volleyListenerInterface：VolleyListenerInterface接口；
    * */

    public static void setToken(Context context) {
        SharedPreferences share=context.getSharedPreferences("user", Activity.MODE_PRIVATE);
        String tokenStr = share.getString("token","");
        String phoneNumStr = share.getString("token","");
        if (!tokenStr.equals(""))
            token=tokenStr;
        if (!tokenStr.equals(""))
            phoneNum=phoneNumStr;
    }
    public static void setToken(String token,String phoneNum) {
        VolleyRequest.token=token;
        VolleyRequest.phoneNum=phoneNum;
    }

    public static void RequestGet(final Context context, String url, String tag, VolleyListenerInterface volleyListenerInterface) {
        // 清除请求队列中的tag标记请求
        LIMSApplication.getRequestQueue().cancelAll(tag);
        // 创建当前的请求，获取字符串内容
        stringRequest = new StringRequest(Request.Method.GET, baseUrl+url, volleyListenerInterface.responseListener(), volleyListenerInterface.errorListener()){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if(token.equals(""))
                    setToken(context);
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("MK-AUTH", phoneNum+"-"+token);
                return headers;
            }
        };
        // 为当前请求添加标记
        stringRequest.setTag(tag);
        // 将当前请求添加到请求队列中
        LIMSApplication.getRequestQueue().add(stringRequest);
        // 重启当前请求队列
        LIMSApplication.getRequestQueue().start();
    }

    /*
    * 获取POST请求内容（请求的代码为Map）
    * 参数：
    * context：当前上下文；
    * url：请求的url地址；
    * tag：当前请求的标签；
    * params：POST请求内容；
    * volleyListenerInterface：VolleyListenerInterface接口；
    * */
    public static void RequestPost(final Context context, String url, String tag, final Map<String, String> params, VolleyListenerInterface volleyListenerInterface) {
        // 清除请求队列中的tag标记请求
        LIMSApplication.getRequestQueue().cancelAll(tag);
        // 创建当前的POST请求，并将请求内容写入Map中
        stringRequest = new StringRequest(Request.Method.POST, baseUrl+url, volleyListenerInterface.responseListener(), volleyListenerInterface.errorListener()){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                if(token.equals(""))
                    setToken(context);
                headers.put("MK-AUTH", phoneNum+"-"+token);
                return headers;
            }
        };
        // 为当前请求添加标记
        stringRequest.setTag(tag);
        // 将当前请求添加到请求队列中
        LIMSApplication.getRequestQueue().add(stringRequest);
        // 重启当前请求队列
        LIMSApplication.getRequestQueue().start();
    }
}