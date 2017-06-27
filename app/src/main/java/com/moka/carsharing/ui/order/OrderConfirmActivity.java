package com.moka.carsharing.ui.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.android.volley.VolleyError;
import com.moka.carsharing.Http.VolleyListenerInterface;
import com.moka.carsharing.Http.VolleyRequest;
import com.moka.carsharing.cache.CarInfo;
import com.moka.carsharing.model.CarPositionModel;
import com.moka.carsharing.ui.HomePageActivity;
import com.moka.carsharing.utils.ActivityBuilder;
import com.moka.carsharing.utils.SharedPreferencesHelper;
import com.moka.carsharing.widget.TitleBar;
import com.moka.carsharing.widget.dialog.OrderConfirmDialog;
import com.moka.carsharing.R;
import com.moka.carsharing.cache.SystemConfig;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderConfirmActivity extends AppCompatActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.tv_position_name)
    TextView tvPositionName;
    @BindView(R.id.vg_data)
    LinearLayout vgData;
    @BindView(R.id.battery_info)
    TextView batteryInfo;
    @BindView(R.id.btn_order)
    Button btnOrder;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_battery_type)
    TextView tvBatteryType;
    @BindView(R.id.tv_time_distance)
    TextView tvTimeDistance;
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_status)
    TextView tvStatus;

    private int distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mTitleBar.bindActivity(this);
        double speed = Double.valueOf(CarInfo.speed) < 30 ? 30 : Double.valueOf(CarInfo.speed);
        LatLng sLng = new LatLng(Double.valueOf(SharedPreferencesHelper.getInstance(this).getString("sLat")),
                Double.valueOf(SharedPreferencesHelper.getInstance(this).getString("sLon")));
        LatLng eLng = new LatLng(Double.valueOf(SharedPreferencesHelper.getInstance(this).getString("tLat")),
                Double.valueOf(SharedPreferencesHelper.getInstance(this).getString("tLon")));
        distance = (int) (AMapUtils.calculateLineDistance(sLng,eLng) / 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        CarPositionModel model = (CarPositionModel) getIntent().getSerializableExtra("station");
        tvPositionName.setText(model.name);
        batteryInfo.setText(model.validity.equals("1") ? "电池可更换" : "电池不可更换");
        tvDate.setText(sdf.format(new Date()));
        tvBatteryType.setText(model.batteryType);
        tvTimeDistance.setText(distance + "公里 " + distance / speed * 60 + "分钟");
        tvDistance.setText(String.valueOf(SystemConfig.mileage * (Double.valueOf(CarInfo.batteryPercent)) / 100) + "公里");
        tvMoney.setText(String.valueOf(Double.parseDouble(SystemConfig.unitPrice) * (100 - Double.valueOf(CarInfo.batteryPercent))) + "元");
        if (Double.valueOf(CarInfo.batteryState) > 0){
            tvStatus.setText("良好");
            tvStatus.setTextColor(getResources().getColor(R.color.underline));
            btnOrder.setText("预约电站");
        }else{
            tvStatus.setText("损坏");
            tvStatus.setTextColor(getResources().getColor(R.color.red));
            btnOrder.setText("前往救援");
        }

    }

    @OnClick(R.id.btn_order)
    public void onViewClicked() {
        if (btnOrder.getText().equals("预约电站")){
            CarPositionModel model = (CarPositionModel) getIntent().getSerializableExtra("station");
            Map<String, String> params = new HashMap<>();
            params.put("stationId", model.id);
            params.put("orderNum", String.valueOf(new Date().getTime()));
            params.put("price", String.valueOf(Double.parseDouble(SystemConfig.unitPrice) * (100 - Double.valueOf(CarInfo.batteryPercent))));

            VolleyRequest.RequestPost(getApplicationContext(), "/Order/create", "getOrderModels", params,
                    new VolleyListenerInterface(getApplicationContext(), VolleyListenerInterface.mListener, VolleyListenerInterface.mErrorListener) {
                        @Override
                        public void onMySuccess(String result) {
                            try {
                                JSONObject obj = new JSONObject(result);
                                if (obj.getString("code").equals("200")) {
                                    System.out.println(result);
                                    OrderConfirmDialog.Builder builder = new OrderConfirmDialog.Builder(OrderConfirmActivity.this);
                                    builder.show();
                                } else {
                                    Toast.makeText(OrderConfirmActivity.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onMyError(VolleyError error) {
                            Toast.makeText(OrderConfirmActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Intent intent = new Intent(OrderConfirmActivity.this, HomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("callForHelp", true);
            startActivity(intent);
        }
    }

    public static class Builder extends ActivityBuilder {
        public CarPositionModel model;

        public Builder(Context context, CarPositionModel model) {
            super(context);
            this.model = model;
        }

        @Override
        public Intent create() {
            Intent intent = new Intent(getContext(), OrderConfirmActivity.class);
            intent.putExtra("station", model);
            return intent;
        }
    }
}
