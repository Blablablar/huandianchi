package com.moka.carsharing.ui.Indent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.VolleyError;
import com.moka.carsharing.Http.VolleyListenerInterface;
import com.moka.carsharing.Http.VolleyRequest;
import com.moka.carsharing.R;
import com.moka.carsharing.pay.apli.PayResult;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by blablabla on 2017/5/10.
 */

public class PayMethodActivity extends Activity implements View.OnClickListener{
    private ImageButton backBtn;
//    private RadioGroup mMethodRg;
    Button mConfirmBtn;
    private CheckBox mWeiXinCb;
    private CheckBox mZfbCb;

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    //微信
    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    public static final String APP_ID ="wx264137b1d77d8237";
    private IWXAPI api;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_method);
        init();

    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_pay_confirm:
                if(mWeiXinCb.isChecked())
                    WxPay();
                else
                    pay();
                //payConfirm();
                break;
            default:
                break;
        }
    }

    public void init(){
        ((TextView)findViewById(R.id.tv_title)).setText("选择支付方式");
        backBtn = (ImageButton)findViewById(R.id.btn_back);
        mConfirmBtn=(Button)findViewById(R.id.btn_pay_confirm);
        mConfirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        mWeiXinCb=(CheckBox)findViewById(R.id.rb_wx);
        mZfbCb=(CheckBox)findViewById(R.id.rb_zfb);
        //默认选择微信支付
        mWeiXinCb.setChecked(true);
        mWeiXinCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeiXinCb.setChecked(true);
                mZfbCb.setChecked(false);
            }
        });
        mZfbCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeiXinCb.setChecked(false);
                mZfbCb.setChecked(true);
            }
        });
        TextView amountTv=(TextView)findViewById(R.id.tv_amount);
        amountTv.setText("订单金额：¥ "+ getIntent().getDoubleExtra("price",0)+" 元");
    }

    public void payConfirm(){
        Map<String, String> params=new HashMap<>();
        params.put("id",getIntent().getStringExtra("id"));
        params.put("orderNum",getIntent().getStringExtra("orderNum"));
        params.put("payType","wx");
        params.put("price",getIntent().getIntExtra("price",0)+"");
        VolleyRequest.RequestPost(getApplicationContext(), "/Order/confirm","3",params,
                new VolleyListenerInterface(getApplicationContext(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        try{
                            System.out.println(result);
                            JSONObject jsonObject = new JSONObject(result);
                            if(jsonObject.get("code").toString().equals("200")){
                                Toast.makeText(getApplicationContext(), "订单支付成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(mContext, IndentActivity.class);
                                intent.putExtra("fragment","payed");
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    public void pay(){
        if(mZfbCb.isChecked()){
            String code="";
            if(getIntent().getStringExtra("code")!=null)
                code="?promotionCodeBody="+getIntent().getStringExtra("code");
            System.out.println(code);
            VolleyRequest.RequestGet(getApplicationContext(), "/Pay/getPayInfo"+code,"zfb_pay",
                    new VolleyListenerInterface(getApplicationContext(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
                        @Override
                        public void onMySuccess(String result) {
                            try{
                                System.out.println(result);
                                JSONObject jsonObject = new JSONObject(result);
                                if(jsonObject.get("code").toString().equals("200")){
                                    final String orderInfo = jsonObject.get("result").toString();
                                    if(jsonObject.get("status").equals("支付成功"))
                                    {
                                        Toast.makeText(getApplicationContext(), "订单支付成功", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), IndentActivity.class);
                                        intent.putExtra("fragment","payed");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }else{
                                        Runnable payRunnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                PayTask alipay = new PayTask(PayMethodActivity.this);
                                                Map<String, String> result = alipay.payV2(orderInfo, true);
                                                Log.i("msp", result.toString());

                                                Message msg = new Message();
                                                msg.what = SDK_PAY_FLAG;
                                                msg.obj = result;
                                                mHandler.sendMessage(msg);
                                            }
                                        };
                                        Thread payThread = new Thread(payRunnable);
                                        payThread.start();
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
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(getApplicationContext(), "订单支付成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), IndentActivity.class);
                        intent.putExtra("fragment","payed");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(PayMethodActivity.this, "订单支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    public void WxPay() {
        api = WXAPIFactory.createWXAPI(this, APP_ID);
        api.registerApp(APP_ID);
        //api.handleIntent(getIntent(), this);
        String code="";
        if(getIntent().getStringExtra("code")!=null)
            code="&promotionCodeBody="+getIntent().getStringExtra("code");
                Toast.makeText(getApplicationContext(), "获取订单中...", Toast.LENGTH_SHORT).show();
                VolleyRequest.RequestGet(getApplicationContext(), "/Pay/getWxSign?createIp=192.168.1.1"+code, "wx_pay",
                        new VolleyListenerInterface(getApplicationContext(), VolleyListenerInterface.mListener, VolleyListenerInterface.mErrorListener) {
                            @Override
                            public void onMySuccess(String result) {
                                try {
                                    System.out.println(result);
                                    JSONObject jsonObject = new JSONObject(result);
                                    if (jsonObject.get("code").toString().equals("200")) {
                                        if(jsonObject.get("status").equals("支付成功"))
                                        {
                                            Toast.makeText(getApplicationContext(), "订单支付成功", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), IndentActivity.class);
                                            intent.putExtra("fragment","payed");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                        JSONObject resultJson=new JSONObject(jsonObject.getString("result"));
                                        //Toast.makeText(getApplicationContext(), "订单信息获取成功", Toast.LENGTH_SHORT).show();
                                        PayReq req = new PayReq();
                                        req.appId = resultJson.getString("appid");
                                        req.partnerId = resultJson.getString("partnerid");
                                        req.prepayId = resultJson.getString("prepayid");
                                        req.nonceStr = resultJson.getString("noncestr");
                                        req.timeStamp = resultJson.getString("timestamp");
                                        req.packageValue = "Sign=WXPay";//resultJson.getString("package");
                                        req.sign = resultJson.getString("sign");
                                        //req.extData = "app data"; // optional
                                        //Toast.makeText(getApplicationContext(), "正常调起支付", Toast.LENGTH_SHORT).show();
                                        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                                        api.sendReq(req);
                                    } else if (jsonObject.get("code").toString().equals("400"))
                                        Toast.makeText(getApplicationContext(), jsonObject.get("error").toString(), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onMyError(VolleyError error) {

                            }
                        });
    }
}
