package com.haundianchi.huandianchi.ui.Indent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.Text;
import com.android.volley.VolleyError;
import com.haundianchi.huandianchi.Http.VolleyListenerInterface;
import com.haundianchi.huandianchi.Http.VolleyRequest;
import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.ui.HomePageActivity;

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
                payConfirm();
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
    }

    public void payConfirm(){
        Map<String, String> params=new HashMap<>();
        params.put("id",getIntent().getStringExtra("id"));
        params.put("orderNum",getIntent().getStringExtra("orderNum"));
        params.put("payType","wx");
        params.put("price",getIntent().getStringExtra("price"));
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
}
