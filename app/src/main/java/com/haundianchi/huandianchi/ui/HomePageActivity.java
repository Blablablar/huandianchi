package com.haundianchi.huandianchi.ui;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.ui.Indent.IndentActivity;
import com.haundianchi.huandianchi.ui.MyPopupWindow.MyPopupWindow;
import com.haundianchi.huandianchi.ui.MyPopupWindow.RescueWindow;
import com.haundianchi.huandianchi.ui.account.MyAccountActivity;
import com.haundianchi.huandianchi.ui.position.CarPositionActivity;
import com.haundianchi.huandianchi.ui.tickets.HistoryTicketsActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.phoneNumber;

/**
 * Created by blablabla on 2017/4/11.
 */

public class HomePageActivity extends Activity implements View.OnClickListener {
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
    private static final int MSG_USER_INFO = 1;
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
                rescueWindow = new RescueWindow(this,itemsOnClick);
                rescueWindow.showAtLocation(findViewById(R.id.id_drawer_layout),
                        Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.ll_change_history:
                new HistoryTicketsActivity.Builder(HomePageActivity.this).start();
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
                    case R.id.nav_messages:
                        Intent intent1=new Intent(getApplicationContext(),IndentActivity.class);
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
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener(){
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_call:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(getApplicationContext().checkSelfPermission(Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:10086"));
                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(callIntent);
                        }else{
                            //
                            getPermossion();
                        }
                    }else{
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:10086"));
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
                        if(!jsonObject.get("mobile").toString().equals(""))
                            mUserNameTv.setText(jsonObject.get("mobile").toString()+"");
                        if(!jsonObject.get("name").toString().equals(""))
                            mPhoneTv.setText(jsonObject.get("name").toString());
                        if(jsonObject.get("headPortrait").toString()!=null)
                            System.out.println(jsonObject.get("headPortrait").toString()==null);
                        //    userImg.setImageBitmap(stringtoBitmap(jsonObject.get("headPortrait").toString()));
                    }catch (Exception e){
                        e.printStackTrace();
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
        getUserInfo();
    }
}