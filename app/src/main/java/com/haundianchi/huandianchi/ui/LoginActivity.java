package com.haundianchi.huandianchi.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.haundianchi.huandianchi.HttpClient;
import com.haundianchi.huandianchi.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by blablabla on 2017/4/11.
 */

public class LoginActivity extends Activity implements View.OnClickListener{
    private Button loginBtn;
    private TextView registerTv;
    private Button mMsgBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtn = (Button)findViewById(R.id.btn_login);
        registerTv=(TextView)findViewById(R.id.tv_register);
        loginBtn.setOnClickListener(this);
        registerTv.setOnClickListener(this);
        init();
    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_login:
                Intent intent1 = new Intent(this.getApplicationContext(),HomePageActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_register:
                Intent intent2 = new Intent(this.getApplicationContext(),RegisterActivity.class);
                startActivity(intent2);
                break;
            case R.id.btn_sendMsg:
                RequestParams params=new RequestParams();
                params.put("mobile", "18901997403");
                params.put("messageType", "102");//mobile=18901997403&messageType=102
                HttpClient.get("/Message/send", params, new JsonHttpResponseHandler() {
                    public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                        // Pull out the first event on the public timeline
                        try {
//                            JSONObject firstEvent = (JSONObject) timeline.get(0);
                        }catch (Exception e){

                        }
                        // Do something with the response
                        System.out.println(timeline);
                    }
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                        super.onFailure(statusCode, headers, throwable, errorResponse);
//                        //Log.d(TAG, "onFailure: ");
//                    }
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        super.onFailure(statusCode, headers, responseString, throwable);
//                        //Log.d(TAG, "onFailure: ");
//                    }
                });
                break;
            default:
                break;
        }
    }

    public void init(){
        mMsgBtn=(Button)findViewById(R.id.btn_sendMsg);
        mMsgBtn.setOnClickListener(this);
    }
}