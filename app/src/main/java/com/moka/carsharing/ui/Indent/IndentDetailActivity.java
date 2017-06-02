package com.moka.carsharing.ui.Indent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.moka.carsharing.ui.position.CarPositionActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

/**
 * Created by blablabla on 2017/5/2.
 */

public class IndentDetailActivity extends Activity implements View.OnClickListener{
    private ImageButton backBtn;
    private Button cancelBtn;
    private static final int MSG_INDENT_DETAIL= 1;
    private static final int MSG_COUNT_DOWN= 2;
    private TextView stationTv;
    private TextView orderNumTv;
    private TextView timeTv;
    private TextView statusTv;
    private TextView priceTv;
    TextView batteryTypeTv;
    TextView ischangeTv;
    private TextView batteryStatusTv;
    private TextView remainBatteryTv;
    private TextView remainDistanceTv;
    private String time;
    CountDownTimer timer;
    RelativeLayout secondLineRl;
    ImageView secondLineIv;
    TextView orderHint;
    String status;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indent_detail);
        backBtn = (ImageButton)findViewById(R.id.btn_back);
        backBtn.setOnClickListener(this);
        init();
    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_cancel:
                if(status.equals("0"))
                    cancel();
                else if(status.equals("1"))
                {
                    ArrayList<IndentModel> orderList = new ArrayList<>();
                    int position=getIntent().getIntExtra("position",0);
                    if(getIntent().getStringExtra("type").equals("0"))
                        orderList=Order.getUnPayList();
                    else
                        orderList=Order.getPayedList();
                    finish();
                    Intent intent=new Intent(this, IndentConfirmActivity.class);
                    intent.putExtra("id",orderList.get(position).getId());
                    intent.putExtra("orderNum",orderList.get(position).getOrderNum());
                    intent.putExtra("orderType","0");
                    intent.putExtra("price",orderList.get(position).getPrice());
                    intent.putExtra("stationName",orderList.get(position).getStation());
                    intent.putExtra("batteryModel",orderList.get(position).getBatteryModel());
                    startActivity(intent);
                }
                else if(status.equals("2"))
                {
                    finish();
                    Intent intent=new Intent(this, CarPositionActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }
    public void init(){

        ArrayList<IndentModel> orderList = new ArrayList<>();
        int position=getIntent().getIntExtra("position",0);
        if(getIntent().getStringExtra("type").equals("0"))
            orderList=Order.getUnPayList();
        else
            orderList=Order.getPayedList();
        status=orderList.get(position).getStatus();

        ((TextView)findViewById(R.id.tv_title)).setText("订单详情");
        cancelBtn=(Button)findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);
        stationTv=(TextView)findViewById(R.id.tv_station_name);
        stationTv.setText(orderList.get(position).getStation());

        orderNumTv=(TextView)findViewById(R.id.tv_order_num);
        orderNumTv.setText("订单编号："+orderList.get(position).getOrderNum());
        secondLineRl=(RelativeLayout)findViewById(R.id.rl_second_line);
        secondLineIv=(ImageView)findViewById(R.id.iv_time);
        orderHint=(TextView) findViewById(R.id.tv_time_title);
        timeTv=(TextView)findViewById(R.id.tv_time);

        statusTv=(TextView)findViewById(R.id.tv_status);
        statusTv.setText(getStatus(orderList.get(position).getStatus()));

        priceTv=(TextView)findViewById(R.id.tv_price);
        priceTv.setText(orderList.get(position).getPrice()+" 元");
        batteryTypeTv=(TextView)findViewById(R.id.tv_battery_type);
        if(CarInfo.batteryType!=null)
            batteryTypeTv.setText(CarInfo.batteryType);
        ischangeTv=(TextView)findViewById(R.id.tv_ischange);
        batteryStatusTv=(TextView)findViewById(R.id.tv_battery_status);
        remainBatteryTv=(TextView)findViewById(R.id.tv_remain_battery);
        remainDistanceTv=(TextView)findViewById(R.id.tv_remain_distance);
        TextView isChangeTv=(TextView)findViewById(R.id.tv_ischange_content);
        if(CarInfo.batteryState.equals("1")){
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
            remainDistanceTv.setText(percent*mileage+" km");
        }
        if(status.equals("0"))
        {
            cancelBtn.setVisibility(View.VISIBLE);
            time=getIntent().getStringExtra("time");
            timer = new CountDownTimer(30*60*1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mHandler.sendEmptyMessage(MSG_COUNT_DOWN);
                }
                @Override
                public void onFinish() {

                }
            };
            timer.start();
            TextView titleTv = (TextView)findViewById(R.id.tv_title_shijifeiyong);
            titleTv.setText("预计费用");
            if(Integer.valueOf(orderList.get(position).getValidCount())>0)
                isChangeTv.setText("可更换");
            else
                isChangeTv.setText("不可更换");
        }
        else if(orderList.get(position).getStatus().equals("1")) {
            //mHandler.sendEmptyMessage(3);
            secondLineIv.setImageDrawable(getResources().getDrawable(R.mipmap.second_line_icon));
            orderHint.setText("换电成功请尽快完成支付");
            orderHint.setTextColor(getResources().getColor(R.color.red));
            cancelBtn.setVisibility(View.VISIBLE);
            cancelBtn.setText("确认支付");
            if(Integer.valueOf(orderList.get(position).getValidCount())>0)
                isChangeTv.setText("可更换");
            else
                isChangeTv.setText("不可更换");
        }
        else if(orderList.get(position).getStatus().equals("2")) {
            secondLineIv.setImageDrawable(getResources().getDrawable(R.mipmap.second_line_icon));
            orderHint.setText("订单已取消请重新预约");
            orderHint.setTextColor(getResources().getColor(R.color.red));
            cancelBtn.setVisibility(View.VISIBLE);
            cancelBtn.setText("重新预约");
            RelativeLayout remainRL=(RelativeLayout)findViewById(R.id.rl_battery_remian);
            remainRL.setVisibility(GONE);
            RelativeLayout kexuhangRl=(RelativeLayout)findViewById(R.id.rl_kexuhang);
            kexuhangRl.setVisibility(GONE);
            if(Integer.valueOf(orderList.get(position).getValidCount())>0)
                isChangeTv.setText("可更换");
            else
                isChangeTv.setText("不可更换");
        }
        else if(orderList.get(position).getStatus().equals("3")) {
            secondLineRl.setVisibility(GONE);
            RelativeLayout IsChangeRl=(RelativeLayout)findViewById(R.id.rl_battery_ischange);
            RelativeLayout batteryRl=(RelativeLayout)findViewById(R.id.rl_battery_statu);
            RelativeLayout orderRl=(RelativeLayout)findViewById(R.id.rl_order_sum);
            RelativeLayout youhuiquanRl=(RelativeLayout)findViewById(R.id.rl_youhuiquan);
            RelativeLayout orderMethodRl=(RelativeLayout)findViewById(R.id.rl_order_method);
            orderMethodRl.setVisibility(View.VISIBLE);
            IsChangeRl.setVisibility(GONE);
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
            remainDistanceTv.setText(orderList.get(position).getElectricity()+"%");
            TextView paytypeTv=(TextView)findViewById(R.id.tv_paytype);
            if((orderList.get(position).getPayType()).equals("ali"))
                paytypeTv.setText("支付宝");
            else
                paytypeTv.setText("微信");
            TextView orderPriceTv=(TextView)findViewById(R.id.tv_order_price);
            orderPriceTv.setText(orderList.get(position).getPrice()+"元");
            TextView youhuiquanTv=(TextView)findViewById(R.id.tv_youhuiquan);
            if(orderList.get(position).getYouhuiPrice()!=null)
                youhuiquanTv.setText(orderList.get(position).getYouhuiPrice()+"元");
        }
        else if(orderList.get(position).getStatus().equals("4")) {
            secondLineRl.setVisibility(GONE);
            RelativeLayout IsChangeRl=(RelativeLayout)findViewById(R.id.rl_battery_ischange);
            RelativeLayout batteryRl=(RelativeLayout)findViewById(R.id.rl_battery_statu);
            RelativeLayout orderRl=(RelativeLayout)findViewById(R.id.rl_order_sum);
            RelativeLayout youhuiquanRl=(RelativeLayout)findViewById(R.id.rl_youhuiquan);
            RelativeLayout orderMethodRl=(RelativeLayout)findViewById(R.id.rl_order_method);
            orderMethodRl.setVisibility(View.VISIBLE);
            IsChangeRl.setVisibility(GONE);
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
            remainDistanceTv.setText(orderList.get(position).getElectricity()+"%");
            if((orderList.get(position).getPayType()).equals("ali"))
                paytypeTv.setText("支付宝");
            else
                paytypeTv.setText("微信");
            TextView orderPriceTv=(TextView)findViewById(R.id.tv_order_price);
            orderPriceTv.setText(orderList.get(position).getActualPrice()+"元");
            TextView youhuiquanTv=(TextView)findViewById(R.id.tv_youhuiquan);
            if(orderList.get(position).getYouhuiPrice()!=null)
                youhuiquanTv.setText(orderList.get(position).getYouhuiPrice()+"元");
        }
    }

//    public void getDetail(){
//        String orderNumStr=getIntent().getStringExtra("orderNum");
//        VolleyRequest.RequestGet(getApplicationContext(), "/Order/getByOrderNum?orderNum="+orderNumStr,"4",
//                new VolleyListenerInterface(getApplicationContext(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
//                    @Override
//                    public void onMySuccess(String result) {
//                        try{
//                            System.out.println(result);
//                            JSONObject jsonObject = new JSONObject(result);
//                            if(jsonObject.get("code").toString().equals("200")){
//                                Message msg=new Message();
//                                msg.obj=new JSONObject(jsonObject.get("result").toString());;
//                                msg.what=MSG_INDENT_DETAIL;
//                                mHandler.sendMessage(msg);
//                            }else if(jsonObject.get("code").toString().equals("400"))
//                                Toast.makeText(getApplicationContext(), jsonObject.get("error").toString(), Toast.LENGTH_SHORT).show();
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                    @Override
//                    public void onMyError(VolleyError error) {
//
//                    }
//                });
//    }

    public void cancel(){
        Map<String, String> params=new HashMap<>();
        int position=getIntent().getIntExtra("position",0);
        params.put("status","2");
        params.put("orderNum", Order.getUnPayList().get(position).getOrderNum());
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
                                finish();
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
                        if(!jsonObject.isNull("station"))
                            stationTv.setText((new JSONObject(jsonObject.get("station").toString())).get("address").toString());
                        if(!jsonObject.isNull("orderNum"))
                            orderNumTv.setText("订单编号："+jsonObject.get("orderNum").toString());
                        if(!jsonObject.isNull("payTimeRemain")){
                            time=getTime(jsonObject.get("payTimeRemain").toString());
                            timeTv.setText(getTime(jsonObject.get("payTimeRemain").toString()));
                        }
                        if(!jsonObject.isNull("status")){
                            statusTv.setText(getStatus(jsonObject.get("status").toString()));
                        }
                        if(!jsonObject.isNull("price"))
                            priceTv.setText(jsonObject.get("price").toString()+"元");
                        if(!jsonObject.get("status").toString().equals("0")){
                            cancelBtn.setEnabled(false);
                            cancelBtn.setBackgroundColor(Color.rgb(191, 191, 191));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case MSG_COUNT_DOWN:
                    Long currentTime=Long.parseLong(time);
                    if(currentTime-1>0){
                        time=(Long.parseLong(time)-1)+"";
                        timeTv.setText(getTime(time));
                    }else
                        timeTv.setText("00:00");
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
        ArrayList<IndentModel> orderList = new ArrayList<>();
        int position=getIntent().getIntExtra("position",0);
        if(time!=null)
            timer.cancel();
        super.onDestroy();
    }
}
