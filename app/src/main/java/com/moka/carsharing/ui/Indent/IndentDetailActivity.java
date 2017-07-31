package com.moka.carsharing.ui.Indent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.moka.carsharing.cache.CarInfo;
import com.moka.carsharing.Http.VolleyListenerInterface;
import com.moka.carsharing.Http.VolleyRequest;
import com.moka.carsharing.R;
import com.moka.carsharing.cache.Order;
import com.moka.carsharing.cache.SystemConfig;
import com.moka.carsharing.model.IndentModel;
import com.moka.carsharing.ui.HomePageActivity;
import com.moka.carsharing.ui.position.CarPositionActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

/**
 * Created by blablabla on 2017/5/2.
 */

public class IndentDetailActivity extends Activity implements View.OnClickListener{
    private ImageButton backBtn;
    private Button cancelBtn;
    private Button orderCancelBtn;
    private static final int MSG_INDENT_DETAIL= 1;
    private static final int MSG_COUNT_DOWN= 2;
    private TextView stationTv;
    private TextView orderNumTv;
    private TextView timeTv;
    private TextView statuTv;
    private TextView priceTv;
    private TextView orderTimeTv;
    private TextView actualTitleTv;
    TextView batteryTypeTv;
    //TextView ischangeTv;
    private TextView batteryStatusTv;
    private TextView remainBatteryTv;
    private TextView remainDistanceTv;
    private TextView actualPriceTv;
    private RelativeLayout priceRl;
    private RelativeLayout actualPriceRl;
    private String time;
    CountDownTimer timer;
    LinearLayout secondLineRl;
    ImageView secondLineIv;
    TextView orderHint;
    String status;
    String type;
    int position;
    String countDownTime;
    private SwipeRefreshLayout swipeRefreshLayout;

    RelativeLayout batteryRl;
    RelativeLayout orderRl;
    RelativeLayout youhuiquanRl;
    RelativeLayout orderMethodRl;
    LinearLayout detailPageLl;
    private JSONObject Object;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indent_detail);

        backBtn = (ImageButton)findViewById(R.id.btn_back);
        backBtn.setOnClickListener(this);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.id_swipe_ly);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                timer.cancel();
                if(getIntent().getStringExtra("id")!=null)
                    getDetail(getIntent().getStringExtra("id"));
                else if(orderNumTv.getText().length()>6)
                    getDetail(orderNumTv.getText().subSequence(5,orderNumTv.getText().length()).toString());

                CountDownTimer refreshTimer = new CountDownTimer(5*1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }
                    @Override
                    public void onFinish() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                };
                refreshTimer.start();
            }
        });
        timer = new CountDownTimer(30*60*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mHandler.sendEmptyMessage(MSG_COUNT_DOWN);
            }
            @Override
            public void onFinish() {

            }
        };
        /////
        cancelBtn=(Button)findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);
        orderCancelBtn=(Button)findViewById(R.id.btn_order_cancel);
        orderCancelBtn.setOnClickListener(this);
        stationTv=(TextView)findViewById(R.id.tv_station_name);
        orderNumTv=(TextView)findViewById(R.id.tv_order_num);
        secondLineRl=(LinearLayout)findViewById(R.id.rl_second_line);
        secondLineIv=(ImageView)findViewById(R.id.iv_time);
        orderHint=(TextView) findViewById(R.id.tv_time_title);
        timeTv=(TextView)findViewById(R.id.tv_time);
        statuTv =(TextView)findViewById(R.id.tv_statu);
        orderTimeTv=(TextView)findViewById(R.id.tv_order_time);
        priceTv=(TextView)findViewById(R.id.tv_price);
        actualTitleTv=(TextView)findViewById(R.id.tv_actual_price_title);
        actualPriceTv=(TextView)findViewById(R.id.tv_actual_price);
        priceRl=(RelativeLayout)findViewById(R.id.rl_price);
        actualPriceRl=(RelativeLayout)findViewById(R.id.rl_actual_price);
        batteryTypeTv=(TextView)findViewById(R.id.tv_battery_type);
        batteryStatusTv=(TextView)findViewById(R.id.tv_battery_status);
        remainBatteryTv=(TextView)findViewById(R.id.tv_remain_battery);
        remainDistanceTv=(TextView)findViewById(R.id.tv_remain_distance);
        batteryRl=(RelativeLayout)findViewById(R.id.rl_battery_statu);
        orderRl=(RelativeLayout)findViewById(R.id.rl_order_sum);
        youhuiquanRl=(RelativeLayout)findViewById(R.id.rl_youhuiquan);
        orderMethodRl=(RelativeLayout)findViewById(R.id.rl_order_method);
        detailPageLl=(LinearLayout)findViewById(R.id.ll_detail_page);
        //先隐藏页面
        detailPageLl.setVisibility(View.INVISIBLE);
        //////
        ((TextView)findViewById(R.id.tv_title)).setText("订单详情");
        if(getIntent().getStringExtra("id")==null){
            type=getIntent().getStringExtra("type");
            position=getIntent().getIntExtra("position", 0);
            countDownTime=getIntent().getStringExtra("time");
            init();
        }
        else{
            getDetail(getIntent().getStringExtra("id"));
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_back:
                Intent backIntent=new Intent(this, IndentActivity.class);
                backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if(status!=null){
                    if(status.equals("3")||status.equals("4"))
                        backIntent.putExtra("fragment","payed");
                    else
                        backIntent.putExtra("fragment","unpay");
                }else
                    backIntent.putExtra("fragment","unpay");
                startActivity(backIntent);
                break;
            case R.id.btn_cancel:
//                if(status.equals("0"))
//                    cancel(v);
//                else
                if(status.equals("1"))
                {
                    ArrayList<IndentModel> orderList = new ArrayList<>();
                    if(type.equals("0"))
                        orderList=Order.getUnPayList();
                    else
                        orderList=Order.getPayedList();
                    if(getIntent().getStringExtra("id")==null)
                    {
                        Intent intent=new Intent(this, IndentConfirmActivity.class);
                        intent.putExtra("id",orderList.get(position).getId());
                        intent.putExtra("orderNum",orderList.get(position).getOrderNum());
                        intent.putExtra("orderType","0");
                        intent.putExtra("price",orderList.get(position).getPrice());
                        intent.putExtra("stationName",orderList.get(position).getStation());
                        intent.putExtra("batteryModel",orderList.get(position).getBatteryModel());
                        startActivity(intent);
                    }
                    else
                    {
                        try {
                            Intent intent=new Intent(this, IndentConfirmActivity.class);
                            intent.putExtra("id",Object.getString("id"));
                            intent.putExtra("orderNum",getIntent().getStringExtra("id"));
                            intent.putExtra("orderType","0");
                            intent.putExtra("price",Object.getString("price"));
                            intent.putExtra("stationName",(new JSONObject(Object.getString("station"))).getString("name"));
                            intent.putExtra("batteryModel",new JSONObject(Object.getString("station")).getString("model"));
                            intent.putExtra("electricity",Object.getString("electricity"));
                            startActivity(intent);
                        }catch (Exception e){

                        }

                    }
                }
                else if(status.equals("2"))
                {
                    finish();
                    Intent intent=new Intent(this, CarPositionActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.btn_order_cancel:
                showSingleChoiceDialog();
                break;
            default:
                break;
        }
    }
    public void init(){
        ArrayList<IndentModel> orderList = new ArrayList<>();
        if(type.equals("0"))
            orderList=Order.getUnPayList();
        else
            orderList=Order.getPayedList();
        status=orderList.get(position).getStatus();
        stationTv.setText(orderList.get(position).getStation());
        orderNumTv.setText("订单编号："+orderList.get(position).getOrderNum());
//        statusTv=(TextView)findViewById(R.id.tv_status);
//        statusTv.setText(getStatus(orderList.get(position).getStatus()));
        priceTv.setText(orderList.get(position).getPrice()+" 元");
        actualPriceTv.setText("¥"+orderList.get(position).getPrice()+" 元");
        if(CarInfo.batteryType!=null)
            batteryTypeTv.setText(CarInfo.batteryType);
//        ischangeTv=(TextView)findViewById(R.id.tv_ischange);
//        TextView isChangeTv=(TextView)findViewById(R.id.tv_ischange_content);
        if(CarInfo.batteryState==null)
            batteryStatusTv.setText("null");
        else if(CarInfo.batteryState.equals("1")){
            batteryStatusTv.setText("良好");
        }
        else{
            batteryStatusTv.setText("损坏");
            batteryStatusTv.setTextColor(Color.rgb(255, 0, 0));
        }
        if(CarInfo.batteryPercent!=null){
            remainBatteryTv.setText(CarInfo.batteryPercent+"%");
            int percent=Integer.parseInt(CarInfo.batteryPercent);
            int mileage= SystemConfig.mileage;
            remainDistanceTv.setText((int)(percent/100.0f*mileage)+" km");
        }
        long orderTime = Long.parseLong(orderList.get(position).getTradeTime());
        Date date = new Date(orderTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);
        orderTimeTv.setText("下单时间："+dateStr);
        if(status.equals("0"))
        {
            orderHint.setText("预约保留时长: ");
            statuTv.setText("预约中");
            cancelBtn.setText("立即支付");
            cancelBtn.setVisibility(View.VISIBLE);
            cancelBtn.setEnabled(false);
            cancelBtn.setBackgroundColor(Color.rgb(191, 191, 191));
            time=countDownTime;
            timer.start();
            TextView titleTv = (TextView)findViewById(R.id.tv_title_shijifeiyong);
            titleTv.setText("预计费用");
//            if(Integer.valueOf(orderList.get(position).getValidCount())>0)
//                isChangeTv.setText("可更换");
//            else
//                isChangeTv.setText("不可更换");
        }
        else if(orderList.get(position).getStatus().equals("1")) {
            actualPriceRl.setVisibility(View.VISIBLE);
            statuTv.setText("换电成功");
            //mHandler.sendEmptyMessage(3);
            orderCancelBtn.setVisibility(GONE);
            secondLineIv.setImageDrawable(getResources().getDrawable(R.mipmap.success_icon));
            orderHint.setText("换电成功请尽快完成支付");
            orderHint.setTextColor(getResources().getColor(R.color.red));
            cancelBtn.setVisibility(View.VISIBLE);
            cancelBtn.setText("立即支付");
//            if(Integer.valueOf(orderList.get(position).getValidCount())>0)
//                isChangeTv.setText("可更换");
//            else
//                isChangeTv.setText("不可更换");
        }
        else if(orderList.get(position).getStatus().equals("2")) {
            actualPriceRl.setVisibility(View.VISIBLE);
            actualTitleTv.setText("预计支付");
            orderCancelBtn.setVisibility(GONE);
            statuTv.setText("预约取消");
            secondLineIv.setImageDrawable(getResources().getDrawable(R.mipmap.cancel_icon));
            orderHint.setText("已取消请重新预约");
            orderHint.setTextColor(getResources().getColor(R.color.red));
            cancelBtn.setVisibility(View.VISIBLE);
            cancelBtn.setText("重新预约");
            RelativeLayout remainRL=(RelativeLayout)findViewById(R.id.rl_battery_remian);
            RelativeLayout kexuhangRl=(RelativeLayout)findViewById(R.id.rl_kexuhang);
//            if(Integer.valueOf(orderList.get(position).getValidCount())>0)
//                isChangeTv.setText("可更换");
//            else
//                isChangeTv.setText("不可更换");
        }
        else if(orderList.get(position).getStatus().equals("3")) {
            priceRl.setVisibility(View.VISIBLE);
            secondLineRl.setVisibility(GONE);
            orderCancelBtn.setVisibility(GONE);
//            RelativeLayout IsChangeRl=(RelativeLayout)findViewById(R.id.rl_battery_ischange);
            orderMethodRl.setVisibility(View.VISIBLE);
//            IsChangeRl.setVisibility(GONE);
            //batteryRl.setVisibility(GONE);
            orderRl.setVisibility(View.VISIBLE);
            youhuiquanRl.setVisibility(View.VISIBLE);
            TextView titleTv = (TextView)findViewById(R.id.tv_title_shijifeiyong);
            titleTv.setText("实际支付");
            TextView remainTitleTv = (TextView)findViewById(R.id.tv_title_remain_battery);
            remainTitleTv.setText("旧电池剩余电量");
            TextView kexuhangTv = (TextView)findViewById(R.id.tv_kexuhang);
            kexuhangTv.setText("新电池剩余电量");
            remainBatteryTv.setText(orderList.get(position).getElectricityOfBefore()+"%");
            int percent=Integer.parseInt(orderList.get(position).getElectricity());
            remainDistanceTv.setText(percent+"%");
            TextView paytypeTv=(TextView)findViewById(R.id.tv_paytype);
            if((orderList.get(position).getPayType()).equals("ali"))
                paytypeTv.setText("支付宝");
            else
                paytypeTv.setText("微信");
            TextView orderPriceTv=(TextView)findViewById(R.id.tv_order_price);
            orderPriceTv.setText(orderList.get(position).getActualPrice()+"元");
            TextView youhuiquanTv=(TextView)findViewById(R.id.tv_youhuiquan);
            if(!orderList.get(position).getYouhuiPrice().equals(""))
                youhuiquanTv.setText(orderList.get(position).getYouhuiPrice()+"元");
        }
        else if(orderList.get(position).getStatus().equals("4")) {
            priceRl.setVisibility(View.VISIBLE);
            orderCancelBtn.setVisibility(GONE);
            secondLineRl.setVisibility(GONE);
//            RelativeLayout IsChangeRl=(RelativeLayout)findViewById(R.id.rl_battery_ischange);
            RelativeLayout batteryRl=(RelativeLayout)findViewById(R.id.rl_battery_statu);
            RelativeLayout orderRl=(RelativeLayout)findViewById(R.id.rl_order_sum);
            RelativeLayout youhuiquanRl=(RelativeLayout)findViewById(R.id.rl_youhuiquan);
            RelativeLayout orderMethodRl=(RelativeLayout)findViewById(R.id.rl_order_method);
            orderMethodRl.setVisibility(View.VISIBLE);
//            IsChangeRl.setVisibility(GONE);
            //batteryRl.setVisibility(GONE);
            orderRl.setVisibility(View.VISIBLE);
            youhuiquanRl.setVisibility(View.VISIBLE);
            TextView titleTv = (TextView)findViewById(R.id.tv_title_shijifeiyong);
            titleTv.setText("实际支付");
            TextView remainTitleTv = (TextView)findViewById(R.id.tv_title_remain_battery);
            remainTitleTv.setText("旧电池剩余电量");
            TextView kexuhangTv = (TextView)findViewById(R.id.tv_kexuhang);
            kexuhangTv.setText("新电池剩余电量");
            TextView paytypeTv=(TextView)findViewById(R.id.tv_paytype);
            remainBatteryTv.setText(orderList.get(position).getElectricityOfBefore()+"%");
            int percent=Integer.parseInt(orderList.get(position).getElectricity());
            remainDistanceTv.setText(percent+"%");
            if((orderList.get(position).getPayType()).equals("ali"))
                paytypeTv.setText("支付宝");
            else
                paytypeTv.setText("微信");
            TextView orderPriceTv=(TextView)findViewById(R.id.tv_order_price);
            orderPriceTv.setText(orderList.get(position).getActualPrice()+"元");
            TextView youhuiquanTv=(TextView)findViewById(R.id.tv_youhuiquan);
            if(!orderList.get(position).getYouhuiPrice().equals(""))
                youhuiquanTv.setText(orderList.get(position).getYouhuiPrice()+"元");
        }
        //数据都获取成功了，再加载页面
        detailPageLl.setVisibility(View.VISIBLE);
    }

    public void getDetail(String orderNum){
//        String orderNum=getIntent().getStringExtra("id");
        VolleyRequest.RequestGet(getApplicationContext(), "/Order/getByOrderNum?orderNum="+orderNum,"4",
                new VolleyListenerInterface(getApplicationContext(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        try{
                            System.out.println(result);
                            JSONObject jsonObject = new JSONObject(result);
                            if(jsonObject.get("code").toString().equals("200")){
                                swipeRefreshLayout.setRefreshing(false);
                                Message msg=new Message();
                                msg.obj=new JSONObject(jsonObject.get("result").toString());
                                msg.what=MSG_INDENT_DETAIL;
                                Object=new JSONObject(jsonObject.get("result").toString());
                                mHandler.sendMessage(msg);
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

    public void cancel(String orderNum){
        Map<String, String> params=new HashMap<>();
        params.put("status","2");
        params.put("orderNum", orderNum);
        VolleyRequest.RequestPost(getApplicationContext(), "/Order/updateStatusByOrderId","3",params,
                new VolleyListenerInterface(getApplicationContext(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        try{
                            System.out.println(result);
                            JSONObject jsonObject = new JSONObject(result);
                            if(jsonObject.get("code").toString().equals("200")){
                                Toast.makeText(getApplicationContext(), "预约取消成功", Toast.LENGTH_SHORT).show();
//                                mHandler.sendEmptyMessage(MSG_COUNT_DOWN);
//                                getDetail();
                                Intent intent=new Intent(getApplication(), IndentActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("fragment","unpay");
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
    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what) {
                case MSG_INDENT_DETAIL:
                    JSONObject jsonObject=(JSONObject)msg.obj;
                    try{
                        System.out.println("刷新界面");
                        if(!jsonObject.isNull("price"))
                            priceTv.setText(jsonObject.getString("price")+" 元");
                        if(!jsonObject.isNull("actualPrice"))
                            actualPriceTv.setText("¥"+jsonObject.getString("actualPrice")+" 元");
                        if(CarInfo.batteryType!=null)
                            batteryTypeTv.setText(CarInfo.batteryType);
                        if(CarInfo.batteryState==null)
                            batteryStatusTv.setText("null");
                        else if(CarInfo.batteryState.equals("1")){
                            batteryStatusTv.setText("良好");
                        }
                        else{
                            batteryStatusTv.setText("损坏");
                            batteryStatusTv.setTextColor(Color.rgb(255, 0, 0));
                        }
                        if(CarInfo.batteryPercent!=null){
                            remainBatteryTv.setText(CarInfo.batteryPercent+"%");
                            int percent=Integer.parseInt(CarInfo.batteryPercent);
                            int mileage= SystemConfig.mileage;
                            remainDistanceTv.setText((int)(percent/100.0f*mileage)+" km");
                        }
                        if(!jsonObject.isNull("station"))
                            stationTv.setText((new JSONObject(jsonObject.getString("station"))).getString("name"));
                        if(!jsonObject.isNull("orderNum"))
                            orderNumTv.setText("订单编号："+jsonObject.getString("orderNum"));
                        if(!jsonObject.isNull("price"))
                            priceTv.setText(jsonObject.get("price").toString()+" 元");
                        if(jsonObject.isNull("status"))
                            break;
                        status=jsonObject.getString("status");
                        type=jsonObject.getString("type");
                        String order_status=jsonObject.getString("status");
                        if(order_status.equals("0"))
                        {
                            orderHint.setText("预约保留时长: ");
                            statuTv.setText("预约中");
                            cancelBtn.setText("立即支付");
                            cancelBtn.setVisibility(View.VISIBLE);
                            cancelBtn.setEnabled(false);
                            cancelBtn.setBackgroundColor(Color.rgb(191, 191, 191));
                            if(!jsonObject.isNull("payTimeRemain")){
                                time=jsonObject.getString("payTimeRemain");
                                timeTv.setText(getTime(jsonObject.get("payTimeRemain").toString()));
                            }else{
                                time=getTime("0");
                                timeTv.setText(getTime(jsonObject.get("payTimeRemain").toString()));
                            }
                            timer.start();
                            TextView titleTv = (TextView)findViewById(R.id.tv_title_shijifeiyong);
                            titleTv.setText("预计费用");
                            long orderTime = Long.parseLong(jsonObject.getString("appointTime"));
                            Date date = new Date(orderTime);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String dateStr = sdf.format(date);
                            orderTimeTv.setText("下单时间："+dateStr);
                        }
                        else if(order_status.equals("1")) {
                            actualPriceRl.setVisibility(View.VISIBLE);
                            statuTv.setText("换电成功");
                            orderCancelBtn.setVisibility(GONE);
                            secondLineIv.setImageDrawable(getResources().getDrawable(R.mipmap.success_icon));
                            orderHint.setText("换电成功请尽快完成支付");
                            orderHint.setTextColor(getResources().getColor(R.color.red));
                            cancelBtn.setVisibility(View.VISIBLE);
                            cancelBtn.setText("立即支付");
                            cancelBtn.setEnabled(true);
                            cancelBtn.setBackgroundColor(Color.rgb(126, 195, 14));
                            timeTv.setText("");
                            long orderTime = Long.parseLong(jsonObject.getString("appointTime"));
                            Date date = new Date(orderTime);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String dateStr = sdf.format(date);
                            orderTimeTv.setText("下单时间："+dateStr);
                        }
                        else if(order_status.equals("2")) {
                            timeTv.setText("");
                            actualPriceRl.setVisibility(View.VISIBLE);
                            actualTitleTv.setText("预计支付");
                            orderCancelBtn.setVisibility(GONE);
                            statuTv.setText("预约取消");
                            secondLineIv.setImageDrawable(getResources().getDrawable(R.mipmap.cancel_icon));
                            orderHint.setText("已取消请重新预约");
                            orderHint.setTextColor(getResources().getColor(R.color.red));
                            cancelBtn.setVisibility(View.VISIBLE);
                            cancelBtn.setText("重新预约");
//                            RelativeLayout remainRL=(RelativeLayout)findViewById(R.id.rl_battery_remian);
//                            RelativeLayout kexuhangRl=(RelativeLayout)findViewById(R.id.rl_kexuhang);
                            long orderTime = Long.parseLong(jsonObject.getString("appointTime"));
                            Date date = new Date(orderTime);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String dateStr = sdf.format(date);
                            orderTimeTv.setText("下单时间："+dateStr);
                        }
                        else if(order_status.equals("3")) {
                            priceRl.setVisibility(View.VISIBLE);
                            secondLineRl.setVisibility(GONE);
                            orderCancelBtn.setVisibility(GONE);
                            orderMethodRl.setVisibility(View.VISIBLE);
                            orderRl.setVisibility(View.VISIBLE);
                            youhuiquanRl.setVisibility(View.VISIBLE);
                            TextView titleTv = (TextView)findViewById(R.id.tv_title_shijifeiyong);
                            titleTv.setText("实际支付");
                            TextView remainTitleTv = (TextView)findViewById(R.id.tv_title_remain_battery);
                            remainTitleTv.setText("旧电池剩余电量");
                            TextView kexuhangTv = (TextView)findViewById(R.id.tv_kexuhang);
                            kexuhangTv.setText("新电池剩余电量");
                            if(!jsonObject.isNull("electricityOfBefore"))
                                remainBatteryTv.setText(jsonObject.getString("electricityOfBefore")+"%");
                            if(!jsonObject.isNull("electricity"))
                                remainDistanceTv.setText(jsonObject.getString("electricity")+"%");
                            TextView paytypeTv=(TextView)findViewById(R.id.tv_paytype);
                            if(!jsonObject.isNull("payType")){
                                if(jsonObject.getString("payType").equals("ali"))
                                    paytypeTv.setText("支付宝");
                                else
                                    paytypeTv.setText("微信");
                            }
                            TextView orderPriceTv=(TextView)findViewById(R.id.tv_order_price);
                            if(!jsonObject.isNull("actualPrice"))
                                orderPriceTv.setText(jsonObject.getString("actualPrice")+"元");
                            TextView youhuiquanTv=(TextView)findViewById(R.id.tv_youhuiquan);
                            if(!jsonObject.isNull("discount"))
                                youhuiquanTv.setText(jsonObject.getString("discount")+"元");
                            long orderTime = Long.parseLong(jsonObject.getString("tradeTime"));
                            Date date = new Date(orderTime);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String dateStr = sdf.format(date);
                            orderTimeTv.setText("下单时间："+dateStr);
                        }
                        else if(order_status.equals("4")) {
                            priceRl.setVisibility(View.VISIBLE);
                            orderCancelBtn.setVisibility(GONE);
                            secondLineRl.setVisibility(GONE);

                            orderMethodRl.setVisibility(View.VISIBLE);
                            orderRl.setVisibility(View.VISIBLE);
                            youhuiquanRl.setVisibility(View.VISIBLE);
                            TextView titleTv = (TextView)findViewById(R.id.tv_title_shijifeiyong);
                            titleTv.setText("实际支付");
                            TextView remainTitleTv = (TextView)findViewById(R.id.tv_title_remain_battery);
                            remainTitleTv.setText("旧电池剩余电量");
                            TextView kexuhangTv = (TextView)findViewById(R.id.tv_kexuhang);
                            kexuhangTv.setText("新电池剩余电量");
                            TextView paytypeTv=(TextView)findViewById(R.id.tv_paytype);
                            if(!jsonObject.isNull("electricityOfBefore"))
                                remainBatteryTv.setText(jsonObject.getString("electricityOfBefore")+"%");
                            if(!jsonObject.isNull("electricity"))
                                remainDistanceTv.setText(jsonObject.getString("electricity")+"%");
                            if(!jsonObject.isNull("payType")){
                                if(jsonObject.getString("payType").equals("ali"))
                                    paytypeTv.setText("支付宝");
                                else
                                    paytypeTv.setText("微信");
                            }
                            TextView orderPriceTv=(TextView)findViewById(R.id.tv_order_price);
                            if(!jsonObject.isNull("actualPrice"))
                                orderPriceTv.setText(jsonObject.getString("actualPrice")+"元");
                            TextView youhuiquanTv=(TextView)findViewById(R.id.tv_youhuiquan);
                            if(!jsonObject.isNull("discount"))
                                youhuiquanTv.setText(jsonObject.getString("discount")+"元");
                            long orderTime = Long.parseLong(jsonObject.getString("tradeTime"));
                            Date date = new Date(orderTime);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String dateStr = sdf.format(date);
                            orderTimeTv.setText("下单时间："+dateStr);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    System.out.println("界面更新结束");
                    detailPageLl.setVisibility(View.VISIBLE);
                    break;
                case MSG_COUNT_DOWN:
                    Long currentTime=Long.parseLong(time);
                    if(currentTime-1>0){
                        time=(Long.parseLong(time)-1)+"";
                        timeTv.setText(getTime(time));
                    }else{
                        timeTv.setText("00:00");
                        timer.cancel();
                        if(orderNumTv.getText().length()>6){
                            CountDownTimer refreshDetailTimer = new CountDownTimer(3*1000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    getDetail(orderNumTv.getText().subSequence(5,orderNumTv.getText().length()).toString());
                                }
                                @Override
                                public void onFinish() {

                                }
                            };
                            refreshDetailTimer.start();
                            System.out.println("获取详情");
                        }
                        System.out.println("倒计时结束");
                    }
                    break;
                case 3:
                    timeTv.setText("00:00");
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent().getStringExtra("fragment")!=null&&getIntent().getStringExtra("fragment").equals("pay_success")){
            detailPageLl.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setRefreshing(true);
        }
//        getDetail();
    }

    public String getTime(String payTimeRemain){
        //倒计时
        Long time = Long.parseLong(payTimeRemain);
        final String hourStr;
        final String minStr;
        if(time/60<10)
            hourStr="0"+time/60;
        else
            hourStr=time/60+"";
        if(time%60<10)
            minStr="0"+time%60;
        else
            minStr=time%60+"";
        return hourStr+":"+minStr;
    }

    public String getStatus(String code){
        if(code.equals("0"))
            return "预约中";
        else if(code.equals("1"))
            return "待支付";
        else if(code.equals("2"))
            return "已取消";
        else if(code.equals("3"))
            return "已支付";
        else if(code.equals("4"))
            return "已开票";
        return "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(time!=null)
            timer.cancel();
    }

    private void showSingleChoiceDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认取消？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(getIntent().getStringExtra("id")!=null)
                    cancel(getIntent().getStringExtra("id"));
                else if(orderNumTv.getText().length()>6)
                    cancel(orderNumTv.getText().subSequence(5,orderNumTv.getText().length()).toString());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent=new Intent(this, IndentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if(status.equals("3")||status.equals("4"))
                intent.putExtra("fragment","payed");
            else
                intent.putExtra("fragment","unpay");
            startActivity(intent);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

}
