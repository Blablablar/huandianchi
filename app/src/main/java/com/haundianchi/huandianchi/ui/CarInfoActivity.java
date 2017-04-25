package com.haundianchi.huandianchi.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.haundianchi.huandianchi.R;

/**
 * Created by blablabla on 2017/4/24.
 */

public class CarInfoActivity extends Activity implements View.OnClickListener{
    private ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);
        backBtn = (ImageButton)findViewById(R.id.btn_back);
        backBtn.setOnClickListener(this);
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }
    public void init(){
        ((TextView)findViewById(R.id.tv_title)).setText("车辆信息");
    }
}