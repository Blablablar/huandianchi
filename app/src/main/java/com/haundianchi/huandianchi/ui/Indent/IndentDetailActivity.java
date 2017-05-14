package com.haundianchi.huandianchi.ui.Indent;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.Text;
import com.android.volley.VolleyError;
import com.haundianchi.huandianchi.Http.VolleyListenerInterface;
import com.haundianchi.huandianchi.Http.VolleyRequest;
import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.ui.MyPopupWindow.CodeWindow;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
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
    private static final int MSG_INDENT_DETAIL= 1;
    private TextView stationTv;
    private TextView orderNumTv;
    private TextView timeTv;
    private TextView statusTv;
    private TextView priceTv;
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
                cancel();
                break;
            default:
                break;
        }
    }
    public void init(){
        ((TextView)findViewById(R.id.tv_title)).setText("订单详情");
        cancelBtn=(Button)findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);
        stationTv=(TextView)findViewById(R.id.tv_station_name);
        orderNumTv=(TextView)findViewById(R.id.tv_order_num);
        timeTv=(TextView)findViewById(R.id.tv_time);
        statusTv=(TextView)findViewById(R.id.tv_status);
        priceTv=(TextView)findViewById(R.id.tv_price);
    }

    public void getDetail(){
        String orderNumStr=getIntent().getStringExtra("orderNum");
        VolleyRequest.RequestGet(getApplicationContext(), "/Order/getByOrderNum?orderNum="+orderNumStr,"4",
                new VolleyListenerInterface(getApplicationContext(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        try{
                            System.out.println(result);
                            JSONObject jsonObject = new JSONObject(result);
                            if(jsonObject.get("code").toString().equals("200")){
                                Message msg=new Message();
                                msg.obj=new JSONObject(jsonObject.get("result").toString());;
                                msg.what=MSG_INDENT_DETAIL;
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

    public void cancel(){
        Map<String, String> params=new HashMap<>();
        params.put("status","2");
        params.put("orderNums",getIntent().getStringExtra("orderNum"));
        VolleyRequest.RequestPost(getApplicationContext(), "/Order/updateStatus","3",params,
                new VolleyListenerInterface(getApplicationContext(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        try{
                            System.out.println(result);
                            JSONObject jsonObject = new JSONObject(result);
                            if(jsonObject.get("code").toString().equals("200")){
                                Toast.makeText(getApplicationContext(), "预约取消成功", Toast.LENGTH_SHORT).show();
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
                        if(!jsonObject.isNull("payTimeRemain"))
                            timeTv.setText(getTime(jsonObject.get("payTimeRemain").toString()));
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
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        getDetail();
    }

    public String getTime(String payTimeRemain){
        //倒计时
        int time = Integer.parseInt(payTimeRemain);
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
}
