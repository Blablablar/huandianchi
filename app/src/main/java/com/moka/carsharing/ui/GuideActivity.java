package com.moka.carsharing.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moka.carsharing.Http.VolleyListenerInterface;
import com.moka.carsharing.Http.VolleyRequest;
import com.moka.carsharing.cache.CarInfo;
import com.moka.carsharing.model.IndentModel;
import com.moka.carsharing.R;
import com.moka.carsharing.cache.Order;
import com.moka.carsharing.cache.SystemConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by blablabla on 2017/5/3.
 */

public class GuideActivity extends Activity{
    RequestQueue mQueue;
    String phoneNumberStr;
    String tokenStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        mQueue = Volley.newRequestQueue(this);
    }
    public void login(){
        System.out.println("start login");
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://116.62.56.64/test/Login/validate", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    System.out.println(jsonObject.toString());
                    if(jsonObject.get("code").toString().equals("200")){
                        VolleyRequest.setToken(tokenStr,phoneNumberStr);
                        getSysInfo();
                    }else if(jsonObject.get("code").toString().equals("400")){
                        Toast.makeText(getApplicationContext(), jsonObject.get("error").toString(), Toast.LENGTH_SHORT).show();
                        finish();
                        SharedPreferences pref = getSharedPreferences("first", Activity.MODE_PRIVATE);
                        Boolean isFirst = pref.getBoolean("status",true);
                        if(isFirst){
                            Intent intent = new Intent(getApplicationContext(),GuidePageActivity.class);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                            startActivity(intent);
                        }
                    }else if(jsonObject.get("code").toString().equals("504")){

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                System.out.println(phoneNumberStr+"-"+tokenStr);
                headers.put("MK-AUTH", phoneNumberStr+"-"+tokenStr);
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,5, 5));
        mQueue.add(stringRequest);
    }

    public Boolean getUserInfo(){
        SharedPreferences share=getSharedPreferences("user",Activity.MODE_PRIVATE);
        phoneNumberStr = share.getString("phoneNumber","");
        tokenStr = share.getString("token","");
        if (phoneNumberStr.equals(""))
            return false;
        return true;
    }

    public void toLoginPage(){
        CountDownTimer timer = new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                finish();
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        };
        timer.start();
    }
//    public void getSysInfo(){
//        VolleyRequest.RequestGet(getApplicationContext(), "/Order/list", "Sys",
//                new VolleyListenerInterface(getApplicationContext(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
//            @Override
//            public void onMySuccess(String result) {
//                System.out.println("SysInfo"+result);
//            }
//
//            @Override
//            public void onMyError(VolleyError error) {
//
//            }
//        });
//        finish();
//        Intent intent = new Intent(getApplicationContext(),HomePageActivity.class);
//        startActivity(intent);
//    }
    @Override
    protected void onResume() {
        super.onResume();
        if(getUserInfo()){
            CountDownTimer timer = new CountDownTimer(1500, 1500) {
                @Override
                public void onTick(long millisUntilFinished) {}
                @Override
                public void onFinish() {
                    login();
                }
            };
            timer.start();
        }
        else{
            CountDownTimer timer = new CountDownTimer(1500, 1500) {
                @Override
                public void onTick(long millisUntilFinished) {}
                @Override
                public void onFinish() {
                    SharedPreferences pref = getSharedPreferences("first", Activity.MODE_PRIVATE);
                    Boolean isFirst = pref.getBoolean("status",true);
                    if(isFirst){
                        finish();
                        Intent intent = new Intent(getApplicationContext(),GuidePageActivity.class);
                        startActivity(intent);
                    }else
                        toLoginPage();
                }
            };
            timer.start();
        }

    }

    public void getSysInfo(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://116.62.56.64/test/SysIndex/get", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONObject result=new JSONObject(jsonObject.get("result").toString());
                    SystemConfig.unitPrice=result.get("unitPrice").toString();
                    SystemConfig.mileage=Integer.parseInt(result.get("mileage").toString());
                    SystemConfig.rescueTel=result.get("rescueTel").toString();
                    SystemConfig.serviceTel=result.get("serviceTel").toString();
                    //获取汽车状态
                    getCarInfo();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "系统参数获取失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                Toast.makeText(getApplicationContext(), "系统参数获取失败", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("MK-AUTH", phoneNumberStr+"-"+tokenStr);
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,5, 5));
        mQueue.add(stringRequest);
    }

    public void getCarInfo(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://116.62.56.64/test/Car/position", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONObject result=new JSONObject(jsonObject.get("result").toString());
                    CarInfo.batteryPercent=result.get("batteryPercent").toString();
                    CarInfo.batteryType=result.get("batteryType").toString();
                    CarInfo.endAddressX=result.get("endAddressX").toString();
                    CarInfo.endAddressY=result.get("endAddressY").toString();
                    CarInfo.speed=result.get("speed").toString();
                    CarInfo.batteryState=result.get("batteryState").toString();
                    CarInfo.vehicleNumber=result.get("vehicleNumber").toString();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "车辆信息获取失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                //获取已完成订单
                getPayedOrder();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                Toast.makeText(getApplicationContext(), "车辆信息获取失败", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("MK-AUTH", phoneNumberStr+"-"+tokenStr);
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,5, 5));
        mQueue.add(stringRequest);
    }

    public void getPayedOrder(){
        VolleyRequest.RequestGet(getApplicationContext(), "/Order/list?type=1","2",
                new VolleyListenerInterface(getApplicationContext(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        try{
                            System.out.println("已完成 "+result);
                            JSONObject jsonObject = new JSONObject(result);
                            Order.getPayedList().clear();
                            if(jsonObject.get("code").toString().equals("200")){
                                JSONArray resultArray  = new JSONArray(jsonObject.get("result").toString());
                                for(int i=0;i<resultArray.length();i++){
                                    JSONObject data=resultArray.getJSONObject(i);
                                    IndentModel indentModel=new IndentModel();
                                    indentModel.setPrice(data.get("price").toString());
                                    indentModel.setStation((new JSONObject(data.get("station").toString())).get("name").toString());
                                    indentModel.setStatus(data.get("status").toString());
                                    indentModel.setTradeTime(data.get("tradeTime").toString());
                                    indentModel.setOrderNum(data.get("orderNum").toString());
                                    indentModel.setElectricity(data.get("electricity").toString());
                                    indentModel.setElectricityOfBefore(data.get("electricityOfBefore").toString());
                                    indentModel.setPayType(data.get("payType").toString());
                                    if(data.isNull("actualPrice"))
                                        indentModel.setActualPrice(data.getString("actualPrice"));
                                    if(!data.isNull("promotionCode")){
                                        JSONObject jsonObject1=new JSONObject(data.getString("promotionCode"));
                                        indentModel.setYouhuiPrice(jsonObject1.getString("discount"));
                                    }
                                    Order.getPayedList().add(indentModel);
                                }
                                getUnPayOrder();
                            }else if(jsonObject.get("code").toString().equals("400"))
                                Toast.makeText(getApplicationContext(), jsonObject.get("error").toString(), Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onMyError(VolleyError error) {

                    }
                });
    }

    public void getUnPayOrder() {
        VolleyRequest.RequestGet(getApplicationContext(), "/Order/list?type=0","1",
                new VolleyListenerInterface(getApplicationContext(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        try{
                            System.out.println("未支付 "+result);
                            JSONObject jsonObject = new JSONObject(result);
                            Order.getUnPayList().clear();
                            if(jsonObject.get("code").toString().equals("200")){
                                JSONArray resultArray  = new JSONArray(jsonObject.get("result").toString());
                                for(int i=0;i<resultArray.length();i++){
                                    JSONObject data=resultArray.getJSONObject(i);
                                    IndentModel indentModel=new IndentModel();
                                    indentModel.setPrice(data.get("price").toString());
                                    indentModel.setStation((new JSONObject(data.get("station").toString())).get("name").toString());
                                    indentModel.setBatteryModel((new JSONObject(data.get("station").toString())).get("model").toString());
                                    if(!(new JSONObject(data.get("station").toString())).isNull("validCount"))
                                        indentModel.setValidCount((new JSONObject(data.get("station").toString())).get("validCount").toString());
                                    else
                                        indentModel.setValidCount("0");
                                    indentModel.setStatus(data.get("status").toString());
                                    indentModel.setOrderNum(data.get("orderNum").toString());
                                    indentModel.setOrderType(data.get("type").toString());
                                    if(data.get("status").toString().equals("2"))
                                        indentModel.setTradeTime(data.get("ackTime").toString());
                                    else if(data.get("status").toString().equals("1")){
                                        indentModel.setTradeTime(data.get("appointTime").toString());
                                        indentModel.setElectricity(data.get("electricity").toString());
                                        indentModel.setElectricityOfBefore(data.get("electricityOfBefore").toString());
                                    }
                                    else if(data.get("status").toString().equals("0")){
                                        indentModel.setTradeTime(data.get("appointTime").toString());
                                        indentModel.setPayTimeRemain(data.get("payTimeRemain").toString());
                                    }
                                    indentModel.setId(data.get("id").toString());
                                    Order.getUnPayList().add(indentModel);
                                }
                                finish();
                                Intent intent = new Intent(getApplicationContext(),HomePageActivity.class);
                                startActivity(intent);
                            }else if(jsonObject.get("code").toString().equals("400"))
                                Toast.makeText(getApplicationContext(), jsonObject.get("error").toString(), Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onMyError(VolleyError error) {

                    }
                });
    }
}
