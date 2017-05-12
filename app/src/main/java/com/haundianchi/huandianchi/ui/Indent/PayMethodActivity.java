package com.haundianchi.huandianchi.ui.Indent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.maps.model.Text;
import com.haundianchi.huandianchi.R;

/**
 * Created by blablabla on 2017/5/10.
 */

public class PayMethodActivity extends Activity implements View.OnClickListener{
    private ImageButton backBtn;
//    private RadioGroup mMethodRg;
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
            default:
                break;
        }
    }

    public void init(){
        ((TextView)findViewById(R.id.tv_title)).setText("选择支付方式");
        backBtn = (ImageButton)findViewById(R.id.btn_back);
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
}
