package com.moka.carsharing.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import com.moka.carsharing.adapter.MenuItemAdapter;
import com.moka.carsharing.cache.CarInfo;
import com.moka.carsharing.R;
import com.moka.carsharing.cache.SystemConfig;
import com.moka.carsharing.data.UserInfo;
import com.moka.carsharing.ui.Indent.IndentActivity;
import com.moka.carsharing.ui.MyPopupWindow.RescueWindow;
import com.moka.carsharing.ui.account.MyAccountActivity;
import com.moka.carsharing.ui.position.CarPositionActivity;
import com.moka.carsharing.ui.tickets.HistoryTicketsActivity;
import com.moka.carsharing.utils.MenuListView;
import com.moka.carsharing.utils.WaveView.WaveHelper;
import com.moka.carsharing.utils.WaveView.WaveView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by blablabla on 2017/4/11.
 */

public class HomePageActivity extends Activity implements View.OnClickListener,GeocodeSearch.OnGeocodeSearchListener {
    @BindView(R.id.tv_location)
    TextView mLocation;
//    @BindView(R.id.id_nv_menu)
//    NavigationView mNvMenu;
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
    LinearLayout stateLayout;
    FrameLayout frameLayout;
    LinearLayout linearLayout;
    ImageView imageView;
    TextView firstHintTv;
    TextView secondHintTv;
    private static final int MSG_USER_INFO = 1;
    private static final int MSG_REFRESH_UI = 2;
    private static final int MSG_UPDATE_THEME_COLOR = 3;
    private GeocodeSearch geocoderSearch;

    private MenuListView mLvLeftMenu;
    private View headerView;
    private View footerView;
    private View menuView;

    private WaveHelper mWaveHelper;
    private int mBorderColor = Color.parseColor("#FFFFFF");
    private int mBorderWidth = 7;
    MenuItemAdapter menuItemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        mOrderBtn=(Button)findViewById(R.id.btn_order);
        mMenuBtn=(ImageButton)findViewById(R.id.btn_menu);
        mChangeHistoryll=(LinearLayout)findViewById(R.id.ll_change_history);
        mRescuell=(LinearLayout)findViewById(R.id.ll_rescue);
        mOrderBtn.setOnClickListener(this);
        mMenuBtn.setOnClickListener(this);
        mChangeHistoryll.setOnClickListener(this);
        mRescuell.setOnClickListener(this);
        ButterKnife.bind(this);

        mLvLeftMenu = (MenuListView) findViewById(R.id.id_lv_left_menu);
        setUpDrawer();
        init();
        mHandler.sendEmptyMessage(MSG_REFRESH_UI);
    }
    private void setUpDrawer()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        LayoutInflater lif = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerView = lif.inflate(R.layout.header_navigation, mLvLeftMenu, false);
        footerView = lif.inflate(R.layout.navigation_button, mLvLeftMenu, false);
        mLvLeftMenu.addHeaderView(headerView);
        mLvLeftMenu.addFooterView(footerView);
        menuItemAdapter=new MenuItemAdapter(this);
        mLvLeftMenu.setAdapter(menuItemAdapter);
        menuView = inflater.inflate(R.layout.design_drawer_item, mLvLeftMenu, false);

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
        //波浪动画
        final WaveView waveView = (WaveView) findViewById(R.id.wave);
        waveView.setBorder(mBorderWidth, mBorderColor);
        waveView.setWaveColor(
                WaveView.DEFAULT_BEHIND_WAVE_COLOR,
                Color.parseColor("#ffffff"));
        waveView.setActivity(this);
//
        if(CarInfo.batteryPercent!=null){
            mWaveHelper = new WaveHelper(waveView,(float)(Integer.parseInt(CarInfo.batteryPercent)/100.0f));
            //mWaveHelper = new WaveHelper(waveView,0.7f);
        }else
            mWaveHelper = new WaveHelper(waveView,0.0f);
        //获取屏幕大小
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        ImageView mCircleOut=(ImageView)findViewById(R.id.circle_out);
        ImageView mCircleIn=(ImageView)findViewById(R.id.circle_in);
        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.ll_baterry_text);
        LinearLayout wavell=(LinearLayout)findViewById(R.id.ll_wave);

        ViewGroup.LayoutParams params = mCircleOut.getLayoutParams();
        params.height=(new Double(370.0/1334*dm.heightPixels)).intValue();
        mCircleOut.setLayoutParams(params);
        mCircleIn.setLayoutParams(params);
        //waveView.setLayoutParams(params);
        ViewGroup.LayoutParams textParams = linearLayout.getLayoutParams();
        textParams.height=(new Double(370.0/1334*dm.heightPixels)).intValue();
        linearLayout.setLayoutParams(textParams);
        ViewGroup.LayoutParams waveParams = wavell.getLayoutParams();
        waveParams.height=(new Double(370.0/1334*dm.heightPixels)).intValue();
        wavell.setLayoutParams(waveParams);
        //两外圈旋转动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_circle_out);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);
        mCircleOut.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.anim_circle_in);
        lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);
        mCircleIn.startAnimation(animation);

        mQueue = Volley.newRequestQueue(this);
        //View header = mLvLeftMenu.getChildAt(1);
        //mNvMenu.setPressed(false);
        userImg= (ImageView) headerView.findViewById(R.id.iv_avatar);
        mUserNameTv=(TextView)headerView.findViewById(R.id.tv_username);
        mPhoneTv=(TextView)headerView.findViewById(R.id.tv_phone_number);
        if(UserInfo.getHeadPortrait()!=null){
            userImg.setImageBitmap(UserInfo.getHeadPortrait());
        }
        if(UserInfo.getNameStr()!=null){
            mUserNameTv.setText(UserInfo.getNameStr());
        }
        if(UserInfo.getMobilePhoneStr()!=null){
            mPhoneTv.setText(UserInfo.getMobilePhoneStr());
        }
        //pastDisTv=(TextView)findViewById(R.id.tv_past_distance);
        //remainDisTv=(TextView)findViewById(R.id.tv_remain_distance);
        //frameLayout=(FrameLayout) findViewById(R.id.fl_homepage_up);
//        linearLayout=(LinearLayout)findViewById(R.id.ll_homepage_down);
//        imageView=(ImageView)findViewById(R.id.iv_hint);
        firstHintTv=(TextView)findViewById(R.id.tv_hint_first);
        secondHintTv=(TextView)findViewById(R.id.tv_hint_second);
        stateLayout=(LinearLayout) findViewById(R.id.ll_state);
        batteryPercentTv=(TextView)findViewById(R.id.tv_batteryPercent);
        if(CarInfo.batteryPercent!=null){
            batteryPercentTv.setText(CarInfo.batteryPercent+"%");
            if(SystemConfig.mileage!=-1){
                int percent=Integer.parseInt(CarInfo.batteryPercent);
                int mileage=SystemConfig.mileage;
                //remainDisTv.setText(percent*mileage+"");
                //pastDisTv.setText(mileage-percent*mileage+"");
            }
        }
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyAccountActivity.Builder(HomePageActivity.this).start();
            }
        });

//        mLvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                for(int i=1;i<9;i++){
//                    if(i==5)
//                        continue;
//                    if(parent.getChildAt(i)!=null){
//                        parent.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.white));
//                    }
//                }
//                switch (position){
//                    case 1:
//                        //parent.getChildAt(1).setBackgroundColor(getResources().getColor(R.color.navigation_select));
//                        Intent intent1=new Intent(getApplicationContext(),CarPositionActivity.class);
//                        startActivity(intent1);
//                        break;
//                    case 2:
//                        //parent.getChildAt(2).setBackgroundColor(getResources().getColor(R.color.navigation_select));
//                        Intent intent2=new Intent(getApplicationContext(),IndentActivity.class);
//                        intent2.putExtra("fragment","unpay");
//                        startActivity(intent2);
//                        break;
//                    case 3:
//                        //parent.getChildAt(3).setBackgroundColor(getResources().getColor(R.color.navigation_select));
//                        Intent intent3=new Intent(getApplicationContext(),HistoryTicketsActivity.class);
//                        startActivity(intent3);
//                        break;
//                    case 4:
//                        //parent.getChildAt(4).setBackgroundColor(getResources().getColor(R.color.navigation_select));
//                        Intent intent4=new Intent(getApplicationContext(),CarInfoActivity.class);
//                        startActivity(intent4);
//                        break;
//                    case 6:
//                        //parent.getChildAt(6).setBackgroundColor(getResources().getColor(R.color.navigation_select));
//                        Intent intent5=new Intent(getApplicationContext(),InstructionsActivity.class);
//                        startActivity(intent5);
//                        break;
//                    case 7:
//                        //parent.getChildAt(7).setBackgroundColor(getResources().getColor(R.color.navigation_select));
//                        Intent intent6=new Intent(getApplicationContext(),FaqActivity.class);
//                        startActivity(intent6);
//                        break;
//                    case 8:
//                        //parent.getChildAt(8).setBackgroundColor(getResources().getColor(R.color.navigation_select));
//                        Intent intent7=new Intent(getApplicationContext(),PhoneActivity.class);
//                        startActivity(intent7);
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
        Button quitButton=(Button) footerView.findViewById(R.id.btn_logout);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                UserInfo.clear();
                /////
                SharedPreferences pref = getSharedPreferences("first", Activity.MODE_PRIVATE);
                editor = pref.edit();
                editor.putBoolean("status",false);
                editor.commit();
                ///////
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
        }
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        if(CarInfo.endAddressX!=null&&CarInfo.endAddressY!=null){
            LatLng location = new LatLng(Double.parseDouble(CarInfo.endAddressX), Double.parseDouble(CarInfo.endAddressY));//new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
            // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
            RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(location.latitude, location.longitude), 200, GeocodeSearch.AMAP);
            geocoderSearch.getFromLocationAsyn(query);
        }else
            getCarInfo();
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
                            ActivityCompat.requestPermissions(HomePageActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
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
                getUserInfo();
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

    public Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what) {
                case MSG_USER_INFO:
                    JSONObject jsonObject=(JSONObject)msg.obj;
                    try{
                        if(!jsonObject.isNull("mobile")){
                            mPhoneTv.setText(jsonObject.get("mobile").toString());
                            UserInfo.setMobilePhoneStr(jsonObject.get("mobile").toString());
                        }
                        if(!jsonObject.isNull("name")){
                            mUserNameTv.setText(jsonObject.get("name").toString());
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
                            //remainDisTv.setText(percent*mileage+"");
                            //pastDisTv.setText(mileage-percent*mileage+"");
                        }
                    }else{
                        firstHintTv.setText("汽车信息获取失败！");
                        secondHintTv.setText("");
                        break;
                    }
                    if(Integer.parseInt(CarInfo.batteryPercent)>=60){
                        stateLayout.setBackgroundColor(getResources().getColor(R.color.battery_good));
                        //frameLayout.setBackgroundColor(getResources().getColor(R.color.battery_good));
                        //linearLayout.setBackgroundColor(getResources().getColor(R.color.battery_good));
                        mOrderBtn.setBackground(getResources().getDrawable(R.drawable.button_good));
                        firstHintTv.setText("当前汽车剩余电量充足");
                        secondHintTv.setText("（不建议预约）");
                    }
                    else if(Integer.parseInt(CarInfo.batteryPercent)>=30){
                        stateLayout.setBackgroundColor(getResources().getColor(R.color.battery_medium));
                        //frameLayout.setBackgroundColor(getResources().getColor(R.color.battery_medium));
                        //linearLayout.setBackgroundColor(getResources().getColor(R.color.battery_medium));
                        mOrderBtn.setBackground(getResources().getDrawable(R.drawable.button_medium));
                        firstHintTv.setText("当前汽车剩余电量不足");
                        secondHintTv.setText("（请尽快预约）");
                    }
                    else{
                        stateLayout.setBackgroundColor(getResources().getColor(R.color.battery_bad));
                        //frameLayout.setBackgroundColor(getResources().getColor(R.color.battery_bad));
                        //linearLayout.setBackgroundColor(getResources().getColor(R.color.battery_bad));
                        //imageView.setImageDrawable(getResources().getDrawable(R.mipmap.homepage_30));
                        mOrderBtn.setBackground(getResources().getDrawable(R.drawable.button_bad));
                        firstHintTv.setText("当前汽车剩余电量过低");
                        secondHintTv.setText("（请尽快预约）");
                    }
                    break;
                case MSG_UPDATE_THEME_COLOR:
                    int status=(int) msg.obj;
                    if(status==3){
                        stateLayout.setBackgroundColor(getResources().getColor(R.color.battery_good));
                        mOrderBtn.setBackground(getResources().getDrawable(R.drawable.button_good));
                    }else if(status==2){
                        stateLayout.setBackgroundColor(getResources().getColor(R.color.battery_medium));
                        mOrderBtn.setBackground(getResources().getDrawable(R.drawable.button_medium));
                    }else{
                        stateLayout.setBackgroundColor(getResources().getColor(R.color.battery_bad));
                        mOrderBtn.setBackground(getResources().getDrawable(R.drawable.button_bad));
                    }

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
        mWaveHelper.start();
//        setUpDrawer();
        getCarInfo();
        getUserInfo();
//        if(getIntent().getStringExtra("Activity")!=null)
//        {
//            if(getIntent().getStringExtra("Activity").equals("CarPosition")){
//                mDrawerLayout.openDrawer(Gravity.LEFT);
//                mLvLeftMenu = (ListView) findViewById(R.id.id_lv_left_menu);
//                menuItemAdapter.getView(1,null,mLvLeftMenu).setBackgroundColor(getResources().getColor(R.color.black));
//                menuItemAdapter.getView(2,null,mLvLeftMenu).setBackgroundColor(getResources().getColor(R.color.black));
//                menuItemAdapter.getView(3,null,mLvLeftMenu).setBackgroundColor(getResources().getColor(R.color.black));
//                menuItemAdapter.getView(4,null,mLvLeftMenu).setBackgroundColor(getResources().getColor(R.color.black));
//                menuItemAdapter.getView(6,null,mLvLeftMenu).setBackgroundColor(getResources().getColor(R.color.black));
//                mLvLeftMenu.setAdapter(menuItemAdapter);
//                menuItemAdapter.notifyDataSetChanged();
//                menuItemAdapter.notifyDataSetInvalidated();
                //View view=(View)menuItemAdapter.getView(1,menuView,mLvLeftMenu);
                //view.setBackgroundColor(getResources().getColor(R.color.navigation_select));
//                view=(View)listAdapter.getView(2,menuView,mLvLeftMenu);
//                view.setBackgroundColor(getResources().getColor(R.color.navigation_select));
//                AdapterView.OnItemClickListener onItemClickListener = mLvLeftMenu.getOnItemClickListener();
//                if(onItemClickListener!=null){
//                    onItemClickListener.onItemClick(mLvLeftMenu,null,1,0);
//                }
           // }
//            else if(getIntent().getStringExtra("Activity").equals("Indent")){
//                mDrawerLayout.openDrawer(Gravity.LEFT);
//                ListAdapter listAdapter = mLvLeftMenu.getAdapter();
//                View view=(View)listAdapter.getItem(2);
//                view.setBackgroundColor(getResources().getColor(R.color.navigation_select));
//                AdapterView.OnItemClickListener onItemClickListener = mLvLeftMenu.getOnItemClickListener();
//                if(onItemClickListener!=null){
//                    onItemClickListener.onItemClick(mLvLeftMenu,null,2,0);
//                }
           // }
       // }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        TextView titleTv=(TextView)findViewById(R.id.tv_location);
        titleTv.setText(regeocodeResult.getRegeocodeAddress().getDistrict()+regeocodeResult.getRegeocodeAddress().getTownship());
//        ImageView imageView=(ImageView)findViewById(R.id.location);
//        imageView.setVisibility(View.VISIBLE);
        CarInfo.position=(regeocodeResult.getRegeocodeAddress().getDistrict()+regeocodeResult.getRegeocodeAddress().getTownship());
    }

    public void getCarInfo(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://116.62.56.64/test/Car/position", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.get("code").toString().equals("200")){
                        JSONObject result=new JSONObject(jsonObject.get("result").toString());
                        CarInfo.batteryPercent=result.get("batteryPercent").toString();
                        CarInfo.batteryType=result.get("batteryType").toString();
                        CarInfo.endAddressX=result.get("endAddressX").toString();
                        CarInfo.endAddressY=result.get("endAddressY").toString();
                        CarInfo.speed=result.get("speed").toString();
                        CarInfo.batteryState=result.get("batteryState").toString();
                        CarInfo.vehicleNumber=result.get("vehicleNumber").toString();
                        //刷新UI
                        mHandler.sendEmptyMessage(MSG_REFRESH_UI);
                        //获取已完成订单
                        LatLng location = new LatLng(Double.parseDouble(CarInfo.endAddressX), Double.parseDouble(CarInfo.endAddressY));//new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(location.latitude, location.longitude), 200, GeocodeSearch.AMAP);
                        geocoderSearch.getFromLocationAsyn(query);
                    }else if(jsonObject.get("code").toString().equals("400")) {
                        if(!jsonObject.isNull("status"))
                            Toast.makeText(getApplicationContext(), jsonObject.getString("status"), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "车辆信息获取失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                Toast.makeText(getApplicationContext(), "车辆信息获取失败", Toast.LENGTH_SHORT).show();
                getCarInfo();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("MK-AUTH", phoneNumberStr+"-"+tokenStr);
                return headers;
            }
        };
        mQueue.add(stringRequest);
    }
    public void setThemeColor(int status){
        Message msg=new Message();
        msg.what=MSG_UPDATE_THEME_COLOR;
        msg.obj=status;
        mHandler.sendMessage(msg);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mWaveHelper.cancel();
    }
}