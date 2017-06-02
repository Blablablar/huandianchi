package com.moka.carsharing.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moka.carsharing.ui.MyPopupWindow.MyPopupWindow;
import com.moka.carsharing.R;


/**
 * Created by blablabla on 2017/4/20.
 */

public class PhoneActivity extends Activity implements View.OnClickListener{
    private ImageButton backBtn;
    private Button mCallBtn;
    private TextView mTelOneTv;
    private TextView mTelTwoTv;
    private LinearLayout mPhonell;
    MyPopupWindow popupwindow;
    private Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_phone);
        mActivity=this;
        backBtn = (ImageButton)findViewById(R.id.btn_back);
        backBtn.setOnClickListener(this);

        mTelOneTv=(TextView)findViewById(R.id.tv_1);
        mTelTwoTv=(TextView)findViewById(R.id.tv_2);

        mCallBtn=(Button)findViewById(R.id.btn_call);
        mCallBtn.setOnClickListener(this);

        mPhonell=(LinearLayout)findViewById(R.id.ll_call);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_call:
                popupwindow = new MyPopupWindow(this,itemsOnClick);
                popupwindow.showAtLocation(findViewById(R.id.rl_call_page),
                        Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            default:
                break;
        }
    }
    public void init(){
        ((TextView)findViewById(R.id.tv_title)).setText("客服电话");
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener(){
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.phone_number_1:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(getApplicationContext().checkSelfPermission(Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:021-11111111"));
                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(callIntent);
                        }else{
                            //
                        }
                    }else{
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:021-11111111"));
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callIntent);
                    }
                    break;
                case R.id.phone_number_2:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(getApplicationContext().checkSelfPermission(Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:021-11111111"));
                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(callIntent);
                        }else{
                            //
                        }
                    }else{
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:021-11111111"));
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callIntent);
                    }
                    break;
                case R.id.btn_cancel:
                    popupwindow.dismiss();
                    break;
                default:
                    break;
            }
        }

    };
}
