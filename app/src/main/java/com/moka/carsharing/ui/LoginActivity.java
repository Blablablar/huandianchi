package com.moka.carsharing.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.moka.carsharing.cache.Order;
import com.moka.carsharing.cache.SystemConfig;
import com.moka.carsharing.model.IndentModel;
import com.moka.carsharing.R;
import com.moka.carsharing.utils.ClearEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by blablabla on 2017/4/11.
 */

public class LoginActivity extends Activity implements View.OnClickListener{
    private Button loginBtn;
    private TextView registerTv;
    private Button mMsgBtn;
    ClearEditText mPhoneEt;
    EditText mCodeEt;
    RequestQueue mQueue;
    String MsgCode;
    String phoneNumberStr;
    String tokenStr;
    int status=0;//0登录 1注册
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
                //隐藏软键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                if(status==0){
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
                                    phoneNumberStr=mPhoneEt.getText().toString();
                                    tokenStr=jsonObject.get("result").toString();
                                    VolleyRequest.setToken(tokenStr,phoneNumberStr);
                                    getSysInfo();
    //                                finish();
    //                                Intent intent1 = new Intent(getApplicationContext(),HomePageActivity.class);
    //                                startActivity(intent1);
                                }else if(jsonObject.get("code").toString().equals("400")){
                                    Toast.makeText(getApplicationContext(), jsonObject.get("error").toString(), Toast.LENGTH_SHORT).show();
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
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("mobile", mPhoneEt.getText().toString());
                            map.put("code", mCodeEt.getText().toString());
                            return map;
                        }
                    };
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,5, 5));
                    mQueue.add(stringRequest);
                }else
                {
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
                                    intent.putExtra("Name",mPhoneEt.getText().toString());
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
                    stringRequest1.setRetryPolicy(new DefaultRetryPolicy(10000,5, 5));
                    mQueue.add(stringRequest1);
                }
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
                            }else if(jsonObject.get("code").toString().equals("400")) {
                                if(jsonObject.get("error").toString().equals("手机号未注册")){
                                    registerMsg();
                                    status=1;
                                }
                                //Toast.makeText(getApplicationContext(), , Toast.LENGTH_SHORT).show();
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
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("mobile", mPhoneEt.getText().toString());
                        return map;
                    }
                };
                stringRequest1.setRetryPolicy(new DefaultRetryPolicy(10000,5, 5));
                mQueue.add(stringRequest1);
                break;
            default:
                break;
        }
    }

    public void init(){
        mMsgBtn=(Button)findViewById(R.id.btn_sendMsg);
        mMsgBtn.setOnClickListener(this);

        mPhoneEt=(ClearEditText)findViewById(R.id.et_phone_number);
        mCodeEt=(EditText)findViewById(R.id.et_code);
        final LinearLayout ll=(LinearLayout)findViewById(R.id.ll_login);
        final RelativeLayout rl_icon=(RelativeLayout)findViewById(R.id.ll_icon);
        final Rect rectangle= new Rect();

        getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        //收缩键盘
//        ll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
//            @Override
//            public void onGlobalLayout(){
//                //比较Activity根布局与当前布局的大小
//                int heightDiff = ll.getRootView().getHeight()- ll.getHeight()-rectangle.top;
//                if(heightDiff >250){
//                    //大小超过50时，一般为显示虚拟键盘事件
//                    int height=rl_icon.getHeight();
//                    LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)rl_icon.getLayoutParams();
//                    if(height-heightDiff>0) {
//                        params.height = height - heightDiff;
//                    }else
//                        params.height=(int)(250.0/ll.getRootView().getHeight()*1920);
//                    params.setMargins(0, 0, 0, 10);
//                    rl_icon.setLayoutParams(params);
//                }else{
//                    //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
//                    LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)rl_icon.getLayoutParams();
//                    params.height= ViewGroup.LayoutParams.WRAP_CONTENT;
//                    params.setMargins(0,0,0,dip2px(getApplicationContext(),50.0f));
//                    rl_icon.setLayoutParams(params);
//                }
//            }
//        });

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
        //跳转到主页
        getOrderData();
        finish();
        Intent intent = new Intent(getApplicationContext(),HomePageActivity.class);
        startActivity(intent);
    }

    public void getOrderData(){
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
                                    indentModel.setStation((new JSONObject(data.get("station").toString())).get("address").toString());
                                    indentModel.setStatus(data.get("status").toString());
                                    indentModel.setTradeTime(data.get("tradeTime").toString());
                                    indentModel.setOrderNum(data.get("orderNum").toString());
                                    Order.getPayedList().add(indentModel);
                                }
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

    public void registerMsg(){
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,5, 5));
        mQueue.add(stringRequest);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}