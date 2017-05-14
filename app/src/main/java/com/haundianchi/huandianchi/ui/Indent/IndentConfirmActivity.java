package com.haundianchi.huandianchi.ui.Indent;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.haundianchi.huandianchi.Http.VolleyListenerInterface;
import com.haundianchi.huandianchi.Http.VolleyRequest;
import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.ui.MyPopupWindow.CodeWindow;
import com.haundianchi.huandianchi.ui.MyPopupWindow.MyPopupWindow;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by blablabla on 2017/4/24.
 */

public class IndentConfirmActivity extends Activity implements View.OnClickListener{
    private ImageButton backBtn;
    private Button mSubmitBtn;
    private TextView mTicketTv;
    private RelativeLayout mCodeRl;
    CodeWindow codeWindow;
    private Activity mActivity;
    private static final int MSG_TICKET_CONFIRMED = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indent_confirm);
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
            case R.id.rl_code:
                codeWindow = new CodeWindow(this,itemsOnClick);
                codeWindow.showAtLocation(findViewById(R.id.rl_indent_confirm),
                        Gravity.CENTER, 0, 0);
                break;
            case R.id.btn_pay_confirm:
                Intent intent=new Intent(this,PayMethodActivity.class);
                intent.putExtra("id",getIntent().getStringExtra("id"));
                intent.putExtra("orderNum",getIntent().getStringExtra("orderNum"));
                intent.putExtra("orderType","0");
                intent.putExtra("price",getIntent().getStringExtra("price"));
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    public void init(){
        ((TextView)findViewById(R.id.tv_title)).setText("确认订单");
        mTicketTv=(TextView)findViewById(R.id.tv_ticket_money);
        mCodeRl=(RelativeLayout) findViewById(R.id.rl_code);
        mCodeRl.setOnClickListener(this);
        mSubmitBtn=(Button)findViewById(R.id.btn_pay_confirm);
        mSubmitBtn.setOnClickListener(this);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener(){
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_quit:
                    codeWindow.dismiss();
                    break;
                case R.id.btn_submit:
                    String code = codeWindow.getCode();
                    VolleyRequest.RequestGet(getApplicationContext(), "/PromotionCode/check?body="+code,"1",
                            new VolleyListenerInterface(getApplicationContext(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
                        @Override
                        public void onMySuccess(String result) {
                            try{
                                System.out.println(result);
                                JSONObject jsonObject = new JSONObject(result);
                                if(jsonObject.get("code").toString().equals("200")){
                                    Message msg=new Message();
                                    msg.obj=jsonObject;
                                    msg.what=MSG_TICKET_CONFIRMED;
                                    mHandler.sendMessage(msg);
                                    codeWindow.dismiss();
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
                    break;
                default:
                    break;
            }
        }

    };

    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what) {
                case MSG_TICKET_CONFIRMED:
                    JSONObject jsonObject=(JSONObject)msg.obj;
                    try{
                        if(!jsonObject.isNull("result"))
                            mTicketTv.setText(jsonObject.get("result").toString());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
}
