package com.moka.carsharing.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moka.carsharing.Http.VolleyListenerInterface;
import com.moka.carsharing.Http.VolleyRequest;
import com.moka.carsharing.R;
import com.moka.carsharing.cache.CarInfo;
import com.moka.carsharing.cache.Order;
import com.moka.carsharing.cache.SystemConfig;
import com.moka.carsharing.model.IndentModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterNextActivity extends Activity implements View.OnClickListener{
    private ImageButton backBtn;
    private Button submitBtn;
    EditText mCarIdEt;
    RequestQueue mQueue;
    String tokenStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_next);
        init();
    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_submit:
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        "http://116.62.56.64/test/Car/bind", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.get("code").toString().equals("200")){
                                SharedPreferences share=getSharedPreferences("user",Activity.MODE_PRIVATE);
                                SharedPreferences.Editor edit = share.edit(); //编辑文件
                                edit.putString("phoneNumber",getPhoneNumber());         //根据键值对添加数据
                                edit.putString("token", jsonObject.get("result").toString());
                                edit.commit();  //保存数据信息
                                tokenStr=jsonObject.get("result").toString();
                                getSysInfo();
                            }else if(jsonObject.get("code").toString().equals("400"))
                                Toast.makeText(getApplicationContext(), jsonObject.get("error").toString(), Toast.LENGTH_SHORT).show();
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
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("mobile", getPhoneNumber());
                        map.put("carId", mCarIdEt.getText().toString());
                        map.put("name", getName());
                        return map;
                    }
                };
                mQueue.add(stringRequest);

                break;
            default:
                break;
        }
    }

    public void init(){
        backBtn=(ImageButton)findViewById(R.id.btn_back);
        submitBtn=(Button)findViewById(R.id.btn_submit);
        backBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        mCarIdEt=(EditText)findViewById(R.id.et_car_id);

        mQueue = Volley.newRequestQueue(this);
    }

    public String getPhoneNumber(){
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        return bundle.getString("PhoneNumber");
    }
    public String getName(){
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        return bundle.getString("Name");
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
                headers.put("MK-AUTH", getPhoneNumber()+"-"+tokenStr);
                return headers;
            }
        };
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
                    //跳转到主页
                    Intent intent = new Intent(getApplicationContext(),HomePageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "车辆信息获取失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
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
                headers.put("MK-AUTH", getPhoneNumber()+"-"+tokenStr);
                return headers;
            }
        };
        mQueue.add(stringRequest);
    }
}
