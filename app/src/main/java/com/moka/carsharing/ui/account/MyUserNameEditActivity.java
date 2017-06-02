package com.moka.carsharing.ui.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.moka.carsharing.utils.ActivityBuilder;
import com.moka.carsharing.R;
import com.moka.carsharing.widget.TitleBar;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyUserNameEditActivity extends AppCompatActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.edit_username)
    EditText mUsername;
    @BindView(R.id.btn_submit)
    Button mSubmitBtn;
    RequestQueue mQueue;
    String phoneNumberStr;
    String tokenStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_user_name_edit);
        ButterKnife.bind(this);
        init();
        getToken();
    }

    private void init() {
        mTitleBar.bindActivity(this);
        mQueue = Volley.newRequestQueue(this);
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkName())
                    ChangeName();
                else
                    Toast.makeText(getApplicationContext(), "未输入用户名", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class Builder extends ActivityBuilder {
        public Builder(Context context) {
            super(context);
        }
        @Override
        public Intent create() {
            return new Intent(getContext(), MyUserNameEditActivity.class);
        }
    }

    private void ChangeName() {
        // 上传至服务器
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://116.62.56.64/test/Member/update", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.get("code").toString().equals("200")){
                        finish();
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
                map.put("mobile", phoneNumberStr);
                map.put("name", mUsername.getText().toString());
                return map;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("MK-AUTH", phoneNumberStr+"-"+tokenStr);
                return headers;
            }
        };
        mQueue.add(stringRequest);
    }

    public Boolean getToken(){
        SharedPreferences share=getSharedPreferences("user", Activity.MODE_PRIVATE);
        phoneNumberStr = share.getString("phoneNumber","");
        tokenStr = share.getString("token","");
        if (phoneNumberStr.equals(""))
            return false;
        return true;
    }

    public Boolean checkName(){
        if(mUsername.getText().toString().equals(""))
            return false;
        return true;
    }
}