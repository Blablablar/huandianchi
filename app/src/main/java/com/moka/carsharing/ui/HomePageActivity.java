package com.moka.carsharing.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moka.carsharing.cache.CarInfo;
import com.moka.carsharing.R;
import com.moka.carsharing.cache.SystemConfig;
import com.moka.carsharing.data.UserInfo;
import com.moka.carsharing.ui.Indent.IndentActivity;
import com.moka.carsharing.ui.MyPopupWindow.RescueWindow;
import com.moka.carsharing.ui.account.MyAccountActivity;
import com.moka.carsharing.ui.position.CarPositionActivity;
import com.moka.carsharing.ui.tickets.HistoryTicketsActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by blablabla on 2017/4/11.
 */

public class HomePageActivity extends Activity implements View.OnClickListener,GeocodeSearch.OnGeocodeSearchListener {
    @BindView(R.id.location)
    ImageView mImgLocation;
    @BindView(R.id.tv_location)
    TextView mLocation;
    @BindView(R.id.imageView)
    ImageView mImageView;
    @BindView(R.id.id_nv_menu)
    NavigationView mNvMenu;
    @BindView(R.id.id_drawer_layout)
    DrawerLayout mDrawerLayout;
    ImageButton mMenuBtn;
    Button mOrderBtn;
    LinearLayout mChangeHistoryll;
    LinearLayout mRescuell;
    RescueWindow rescueWindow;
    Activity mActivity;
    RequestQueue mQueue;
    String phoneNumberStr;
    String tokenStr;
    TextView mUserNameTv;
    TextView mPhoneTv;
    ImageView userImg;//用户头像
    TextView batteryPercentTv;
    TextView pastDisTv;
    TextView remainDisTv;
    RelativeLayout relativeLayout;
    FrameLayout frameLayout;
    LinearLayout linearLayout;
    ImageView imageView;
    TextView hintTv;
    private static final int MSG_USER_INFO = 1;
    private static final int MSG_REFRESH_UI = 2;
    private GeocodeSearch geocoderSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        mOrderBtn=(Button)findViewById(R.id.btn_order);
        mDrawerLayout=(DrawerLayout) findViewById(R.id.id_drawer_layout);
        mMenuBtn=(ImageButton)findViewById(R.id.btn_menu);
        mChangeHistoryll=(LinearLayout)findViewById(R.id.ll_change_history);
        mRescuell=(LinearLayout)findViewById(R.id.ll_rescue);
        mOrderBtn.setOnClickListener(this);
        mMenuBtn.setOnClickListener(this);
        mChangeHistoryll.setOnClickListener(this);
        mRescuell.setOnClickListener(this);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_order:
                new CarPositionActivity.Builder(HomePageActivity.this).start();
                break;
            case R.id.btn_menu:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.ll_rescue:
                if(SystemConfig.rescueTel==null){
                    Toast.makeText(getApplicationContext(), "救援电话获取失败", Toast.LENGTH_SHORT).show();
                    break;
                }
                rescueWindow = new RescueWindow(this,itemsOnClick,SystemConfig.rescueTel);
                rescueWindow.showAtLocation(findViewById(R.id.id_drawer_layout),
                        Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.ll_change_history:
                Intent intent=new Intent(this,IndentActivity.class);
                intent.putExtra("fragment","payed");
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void init() {
        mQueue = Volley.newRequestQueue(this);
        View header = mNvMenu.getHeaderView(0);

        userImg= (ImageView) header.findViewById(R.id.iv_avatar);
        mUserNameTv=(TextView)header.findViewById(R.id.tv_username);
        mPhoneTv=(TextView)header.findViewById(R.id.tv_phone_number);
        batteryPercentTv=(TextView)findViewById(R.id.tv_batteryPercent);
        pastDisTv=(TextView)findViewById(R.id.tv_past_distance);
        remainDisTv=(TextView)findViewById(R.id.tv_remain_distance);
        frameLayout=(FrameLayout) findViewById(R.id.fl_homepage_up);
        linearLayout=(LinearLayout)findViewById(R.id.ll_homepage_down);
        imageView=(ImageView)findViewById(R.id.iv_hint);
        hintTv=(TextView)findViewById(R.id.tv_drive_hint);
        relativeLayout=(RelativeLayout)findViewById(R.id.rl_header);
        if(CarInfo.batteryPercent!=null){
            batteryPercentTv.setText(CarInfo.batteryPercent+"%");
            if(SystemConfig.mileage!=-1){
                int percent=Integer.parseInt(CarInfo.batteryPercent);
                int mileage=SystemConfig.mileage;
                remainDisTv.setText(percent*mileage+"");
                pastDisTv.setText(mileage-percent*mileage+"");
            }
        }
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyAccountActivity.Builder(HomePageActivity.this).start();
            }
        });
        mNvMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //在这里处理item的点击事件
                switch (item.getItemId()){
                    case R.id.nav_ticket:
                        new HistoryTicketsActivity.Builder(HomePageActivity.this).start();
                        break;
                    case R.id.nav_home:
                        new CarPositionActivity.Builder(HomePageActivity.this).start();
                        break;
//                    case R.id.nav_position:
//                        Intent intent1=new Intent(getApplicationContext(),PhoneActivity.class);
//                        startActivity(intent1);
                    case R.id.nav_order:
                        Intent intent1=new Intent(getApplicationContext(),IndentActivity.class);
                        intent1.putExtra("fragment","unpay");
                        startActivity(intent1);
                        break;
                    case R.id.nav_phone:
                        Intent intent2=new Intent(getApplicationContext(),PhoneActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.nav_info:
                        Intent intent3=new Intent(getApplicationContext(),CarInfoActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.nav_instruction:
                        Intent intent4=new Intent(getApplicationContext(),InstructionsActivity.class);
                        startActivity(intent4);
                        break;
                    case R.id.nav_problem:
                        Intent intent5=new Intent(getApplicationContext(),FaqActivity.class);
                        startActivity(intent5);
                        break;
                }
                return true;
            }
        });
        
        Handler popupHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        if (getIntent().getBooleanExtra("callForHelp", false)){
                            mRescuell.performClick();
                        }
                        break;
                }
            }

        };
        popupHandler.sendEmptyMessageDelayed(0, 100);
        //汽车定位
        if(CarInfo.position!=null){
            TextView titleTv=(TextView)findViewById(R.id.tv_location);
            titleTv.setText(CarInfo.position);
            ImageView imageView=(ImageView)findViewById(R.id.location);
            imageView.setVisibility(View.VISIBLE);
        }
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        LatLng location = new LatLng(Double.parseDouble(CarInfo.endAddressX), Double.parseDouble(CarInfo.endAddressY));//new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(location.latitude, location.longitude), 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener(){
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_call:
                    if(SystemConfig.rescueTel==null)
                        break;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(getApplicationContext().checkSelfPermission(Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:"+SystemConfig.rescueTel));
                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(callIntent);
                        }else{
                            //
                            getPermossion();
                        }
                    }else{
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+SystemConfig.rescueTel));
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callIntent);
                    }
                    break;
                case R.id.btn_cancel:
                    rescueWindow.dismiss();
                    break;
                default:
                    break;
            }
        }

    };

    public void getPermossion(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
    }

    public void getUserInfo(){
        if(!getToken())
            return;
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST,
                "http://116.62.56.64/test/Member/info", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject userInfoJson;
                    if(jsonObject.get("code").toString().equals("200")){
                        userInfoJson=new JSONObject(jsonObject.get("result").toString());
                        Message msg=new Message();
                        msg.obj=userInfoJson;
                        msg.what=MSG_USER_INFO;
                        mHandler.sendMessage(msg);
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("MK-AUTH", phoneNumberStr+"-"+tokenStr);

                return headers;
            }
        };
        mQueue.add(stringRequest1);
    }

    public Boolean getToken(){
        SharedPreferences share=getSharedPreferences("user",Activity.MODE_PRIVATE);
        phoneNumberStr = share.getString("phoneNumber","");
        tokenStr = share.getString("token","");
        if (phoneNumberStr.equals(""))
            return false;
        return true;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what) {
                case MSG_USER_INFO:
                    JSONObject jsonObject=(JSONObject)msg.obj;
                    try{
                        if(!jsonObject.isNull("mobile")){
                            mUserNameTv.setText(jsonObject.get("mobile").toString());
                            UserInfo.setMobilePhoneStr(jsonObject.get("mobile").toString());
                        }
                        if(!jsonObject.isNull("name")){
                            mPhoneTv.setText(jsonObject.get("name").toString());
                            UserInfo.setNameStr(jsonObject.get("name").toString());
                        }
                        if(!jsonObject.isNull("headPortrait")){
                            userImg.setImageBitmap(stringtoBitmap(jsonObject.get("headPortrait").toString()));
                            UserInfo.setHeadPortrait(stringtoBitmap(jsonObject.get("headPortrait").toString()));
                        }
                        if(!jsonObject.isNull("carId")){
                            UserInfo.setCarIdStr(jsonObject.get("carId").toString());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case MSG_REFRESH_UI:
                    if(CarInfo.batteryPercent!=null){
                        batteryPercentTv.setText(CarInfo.batteryPercent+"%");
                        if(SystemConfig.mileage!=-1){
                            int percent=Integer.parseInt(CarInfo.batteryPercent);
                            int mileage=SystemConfig.mileage;
                            remainDisTv.setText(percent*mileage+"");
                            pastDisTv.setText(mileage-percent*mileage+"");
                        }
                    }
                    if(Integer.parseInt(CarInfo.batteryPercent)>=60){
                        relativeLayout.setBackgroundColor(getResources().getColor(R.color.battery_good));
                        frameLayout.setBackgroundColor(getResources().getColor(R.color.battery_good));
                        linearLayout.setBackgroundColor(getResources().getColor(R.color.battery_good));
                        imageView.setImageDrawable(getResources().getDrawable(R.mipmap.homepage_icon));
                        hintTv.setText("电量健康，祝您驾驶愉快！");
                    }
                    else if(Integer.parseInt(CarInfo.batteryPercent)>=30){
                        relativeLayout.setBackgroundColor(getResources().getColor(R.color.battery_medium));
                        frameLayout.setBackgroundColor(getResources().getColor(R.color.battery_medium));
                        linearLayout.setBackgroundColor(getResources().getColor(R.color.battery_medium));
                        imageView.setImageDrawable(getResources().getDrawable(R.mipmap.homepage_60));
                        hintTv.setText("电量不足，请您即使预约电站！");
                    }
                    else{
                        relativeLayout.setBackgroundColor(getResources().getColor(R.color.battery_bad));
                        frameLayout.setBackgroundColor(getResources().getColor(R.color.battery_bad));
                        linearLayout.setBackgroundColor(getResources().getColor(R.color.battery_bad));
                        imageView.setImageDrawable(getResources().getDrawable(R.mipmap.homepage_30));
                        hintTv.setText("电量过低，请您及时更换电池！");
                    }
                    break;
            }
        }
    };

    public Bitmap stringtoBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Resume");
        mHandler.sendEmptyMessage(MSG_REFRESH_UI);
        getUserInfo();
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        TextView titleTv=(TextView)findViewById(R.id.tv_location);
        titleTv.setText(regeocodeResult.getRegeocodeAddress().getDistrict()+regeocodeResult.getRegeocodeAddress().getTownship());
        ImageView imageView=(ImageView)findViewById(R.id.location);
        imageView.setVisibility(View.VISIBLE);
        CarInfo.position=(regeocodeResult.getRegeocodeAddress().getDistrict()+regeocodeResult.getRegeocodeAddress().getTownship());
    }
}