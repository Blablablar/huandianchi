package com.moka.carsharing.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.moka.carsharing.R;
import com.moka.carsharing.cache.CarInfo;
import com.moka.carsharing.data.UserInfo;

/**
 * Created by blablabla on 2017/4/24.
 */

public class CarInfoActivity extends Activity implements View.OnClickListener{
    private ImageButton backBtn;
    private TextView carIdTv;
    private TextView batteryTypeTv;
    private TextView carLicenseTv;
    private TextView statusTv;
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
        carIdTv=(TextView)findViewById(R.id.tv_car_id);
        carIdTv.setText(UserInfo.getCarIdStr());
        batteryTypeTv=(TextView)findViewById(R.id.tv_type);
        batteryTypeTv.setText(CarInfo.batteryType);
        carLicenseTv=(TextView)findViewById(R.id.tv_car_model);
        statusTv=(TextView)findViewById(R.id.tv_status);
        carLicenseTv.setText(CarInfo.vehicleNumber);
        if(CarInfo.batteryState.equals("1")){
            statusTv.setText("良好");
        }
        else{
            statusTv.setText("损坏");
            statusTv.setTextColor(Color.rgb(255, 0, 0));
        }
    }
}