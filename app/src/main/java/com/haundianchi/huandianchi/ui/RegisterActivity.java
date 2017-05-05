package com.haundianchi.huandianchi.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.haundianchi.huandianchi.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegisterActivity extends Activity implements View.OnClickListener{
    private Button registerBtn;
    Button mSendCodeBtn;
    RequestQueue mQueue;
    String MsgCode;
    EditText mNameEt;
    EditText mPhoneEt;
    EditText mCodeEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_sendMsg:
                if(ConfirmEdit()==-1){
                    Toast.makeText(this, "未输入名称或手机号", Toast.LENGTH_SHORT).show();
                    break;
                }
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        "http://116.62.56.64/test/Login/basicLogin", new Response.Listener<String>() {
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
                mQueue.add(stringRequest);

                break;
            case R.id.btn_register:
                if(ConfirmEdit()==-1){
                    Toast.makeText(this, "未输入名称或手机号", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(mCodeEt.getText().toString().equals("")){
                    Toast.makeText(this, "未输入验证码", Toast.LENGTH_SHORT).show();
                    break;
                }
                StringRequest stringRequest1 = new StringRequest(Request.Method.POST,
                        "http://116.62.56.64/test/Login/verifyCode", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.get("code").toString().equals("200")){
                                MsgCode=jsonObject.get("result").toString();
                                Intent intent = new Intent(getApplicationContext(),RegisterNextActivity.class);
                                intent.putExtra("PhoneNumber",mPhoneEt.getText().toString());
                                intent.putExtra("Name",mNameEt.getText().toString());
                                startActivity(intent);
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
                mQueue.add(stringRequest1);
                break;
            default:
                break;
        }
    }

    public void init(){
        mNameEt=(EditText)findViewById(R.id.et_name);
        mPhoneEt=(EditText)findViewById(R.id.et_phone_number);
        mCodeEt=(EditText)findViewById(R.id.et_code);

        mNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String editable = mNameEt.getText().toString();
                String str = stringFilter(editable.toString());
                if(!editable.equals(str)){
                    mNameEt.setText(str);
                    //设置新的光标所在位置
                    mNameEt.setSelection(str.length());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSendCodeBtn=(Button)findViewById(R.id.btn_sendMsg);
        mSendCodeBtn.setOnClickListener(this);
        registerBtn = (Button)findViewById(R.id.btn_register);
        registerBtn.setOnClickListener(this);

        mQueue = Volley.newRequestQueue(this);
    }
    //确认用户填写 名称&手机号
    public int ConfirmEdit(){
        if(mNameEt.getText().toString().equals(""))
            return -1;
        if(mPhoneEt.getText().toString().equals(""))
            return -1;
        return 1;
    }
    public static String stringFilter(String str)throws PatternSyntaxException {
        // 只允许字母、数字和汉字
        String   regEx  =  "[^a-zA-Z0-9\u4E00-\u9FA5]";
        Pattern   p   =   Pattern.compile(regEx);
        Matcher m   =   p.matcher(str);
        return   m.replaceAll("").trim();
    }

    public void MsgCountDown(){
        CountDownTimer timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mSendCodeBtn.setText((millisUntilFinished / 1000) + "秒");
                mSendCodeBtn.setClickable(false);
            }
            @Override
            public void onFinish() {
                mSendCodeBtn.setClickable(true);
                mSendCodeBtn.setText("获取验证码");
            }
        };
        timer.start();
    }
}
