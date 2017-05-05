package com.haundianchi.huandianchi.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.haundianchi.huandianchi.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by blablabla on 2017/4/11.
 */

public class LoginActivity extends Activity implements View.OnClickListener{
    private Button loginBtn;
    private TextView registerTv;
    private Button mMsgBtn;
    EditText mPhoneEt;
    EditText mCodeEt;
    RequestQueue mQueue;
    String MsgCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtn = (Button)findViewById(R.id.btn_login);
        registerTv=(TextView)findViewById(R.id.tv_register);
        loginBtn.setOnClickListener(this);
        registerTv.setOnClickListener(this);
        init();

        mQueue = Volley.newRequestQueue(this);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_login:
//                Intent intent1 = new Intent(getApplicationContext(),HomePageActivity.class);
//                startActivity(intent1);
                if(ConfirmEdit()==-1){
                    Toast.makeText(this, "未输入手机号或验证码", Toast.LENGTH_SHORT).show();
                    break;
                }
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        "http://116.62.56.64/test/Login/check", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.get("code").toString().equals("200")){
                                SharedPreferences share=getSharedPreferences("user",Activity.MODE_PRIVATE);
                                SharedPreferences.Editor edit = share.edit(); //编辑文件
                                edit.putString("phoneNumber", mPhoneEt.getText().toString());         //根据键值对添加数据
                                edit.putString("token", jsonObject.get("result").toString());
                                edit.commit();  //保存数据信息
                                finish();
                                Intent intent1 = new Intent(getApplicationContext(),HomePageActivity.class);
                                startActivity(intent1);
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
                        map.put("mobile", mPhoneEt.getText().toString());
                        map.put("code", mCodeEt.getText().toString());
                        return map;
                    }
                };
                mQueue.add(stringRequest);
                break;
            case R.id.tv_register:
                Intent intent2 = new Intent(this.getApplicationContext(),RegisterActivity.class);
                startActivity(intent2);
                break;
            case R.id.btn_sendMsg:
//                RequestParams params=new RequestParams();
//                params.put("mobile", "18901997403");
//                params.put("messageType", "102");//mobile=18901997403&messageType=102
//                HttpClient.get("/Message/send", params, new JsonHttpResponseHandler() {
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        // Pull out the first event on the public timeline
//                        super.onSuccess(statusCode, headers, response);
//                        try {
////                            JSONObject firstEvent = (JSONObject) timeline.get(0);
//                        }catch (Exception e){
//
//                        }
//                        // Do something with the response
//                        System.out.println(response);
//                    }
//                });
                if(ConfirmPhone()==-1){
                    Toast.makeText(this, "未输入手机号", Toast.LENGTH_SHORT).show();
                    break;
                }
                StringRequest stringRequest1 = new StringRequest(Request.Method.POST,
                        "http://116.62.56.64/test/Login/basicCheck", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.get("code").toString().equals("200")){
                                MsgCountDown();//验证码发送倒计时
                                MsgCode=jsonObject.get("result").toString();
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
                        map.put("mobile", mPhoneEt.getText().toString());
                        return map;
                    }
                };
                mQueue.add(stringRequest1);
                break;
            default:
                break;
        }
    }

    public void init(){
        mMsgBtn=(Button)findViewById(R.id.btn_sendMsg);
        mMsgBtn.setOnClickListener(this);

        mPhoneEt=(EditText)findViewById(R.id.et_phone_number);
        mCodeEt=(EditText)findViewById(R.id.et_code);
    }

    //确认用户填写 手机号
    public int ConfirmPhone(){
        if(mPhoneEt.getText().toString().equals(""))
            return -1;
        return 1;
    }

    //确认用户填写 手机号
    public int ConfirmEdit(){
        if(mPhoneEt.getText().toString().equals(""))
            return -1;
        if(mCodeEt.getText().toString().equals(""))
            return -1;
        return 1;
    }

    public void MsgCountDown(){
        CountDownTimer timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mMsgBtn.setText((millisUntilFinished / 1000) + "秒");
                mMsgBtn.setClickable(false);
            }
            @Override
            public void onFinish() {
                mMsgBtn.setClickable(true);
                mMsgBtn.setText("获取验证码");
            }
        };
        timer.start();
    }
}