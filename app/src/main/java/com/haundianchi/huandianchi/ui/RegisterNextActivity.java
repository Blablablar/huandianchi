package com.haundianchi.huandianchi.ui;

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
import com.haundianchi.huandianchi.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterNextActivity extends Activity implements View.OnClickListener{
    private ImageButton backBtn;
    private Button submitBtn;
    EditText mCarIdEt;
    RequestQueue mQueue;
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
                                Intent intent = new Intent(getApplicationContext(),HomePageActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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
}
