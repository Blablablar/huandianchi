package com.haundianchi.huandianchi.ui.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.haundianchi.huandianchi.Http.VolleyListenerInterface;
import com.haundianchi.huandianchi.Http.VolleyRequest;
import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.ui.tickets.CreateNewTicketActivity;
import com.haundianchi.huandianchi.utils.ActivityBuilder;
import com.haundianchi.huandianchi.widget.TitleBar;
import com.haundianchi.huandianchi.widget.dialog.OrderConfirmDialog;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderConfirmActivity extends AppCompatActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mTitleBar.bindActivity(this);
    }

    @OnClick(R.id.btn_order)
    public void onViewClicked() {
        Map<String, String> params = new HashMap<>();
        params.put("stationId", "111359bb-4cbb-4b4f-a407-d0148468140d");
        params.put("orderNum", String.valueOf(new Date().getTime()));
        params.put("price", "123");
        VolleyRequest.RequestPost(getApplicationContext(), "/Order/create", "getOrderModels", params,
                new VolleyListenerInterface(getApplicationContext(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        try{
                            JSONObject obj = new JSONObject(result);
                            if (obj.getString("code").equals("200")){
                                System.out.println(result);
                                OrderConfirmDialog.Builder builder = new OrderConfirmDialog.Builder(OrderConfirmActivity.this);
                                builder.show();
                            }else{
                                Toast.makeText(OrderConfirmActivity.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onMyError(VolleyError error) {
                        Toast.makeText(OrderConfirmActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class Builder extends ActivityBuilder {

        public Builder(Context context) {
            super(context);
        }

        @Override
        public Intent create() {
            return new Intent(getContext(), OrderConfirmActivity.class);
        }
    }
}
