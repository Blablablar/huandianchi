package com.haundianchi.huandianchi.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.haundianchi.huandianchi.Http.VolleyRequest;
import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.data.UserInfo;

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
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://116.62.56.64/test/Login/validate", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.get("code").toString().equals("200")){
                        CountDownTimer timer = new CountDownTimer(2000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {}
                            @Override
                            public void onFinish() {
                                finish();
                                VolleyRequest.setToken(getApplicationContext());
                                Intent intent = new Intent(getApplicationContext(),HomePageActivity.class);
                                startActivity(intent);
                            }
                        };
                        timer.start();
                    }else if(jsonObject.get("code").toString().equals("400")){
                        Toast.makeText(getApplicationContext(), jsonObject.get("error").toString(), Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
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
                headers.put("MK-AUTH", phoneNumberStr+"-"+tokenStr);
                return headers;
            }
        };
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

    @Override
    protected void onResume() {
        super.onResume();
        if(getUserInfo())
            login();
        else
            toLoginPage();
    }
}
