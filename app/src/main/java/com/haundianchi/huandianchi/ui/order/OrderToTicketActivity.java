package com.haundianchi.huandianchi.ui.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.haundianchi.huandianchi.Http.VolleyListenerInterface;
import com.haundianchi.huandianchi.Http.VolleyRequest;
import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.adapter.OrderAdapter;
import com.haundianchi.huandianchi.model.OrderModel;
import com.haundianchi.huandianchi.model.TicketModel;
import com.haundianchi.huandianchi.ui.tickets.CreateNewTicketActivity;
import com.haundianchi.huandianchi.utils.ActivityBuilder;
import com.haundianchi.huandianchi.widget.TitleBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderToTicketActivity extends AppCompatActivity {

    @BindView(R.id.vg_container)
    RecyclerView mContainer;
    @BindView(R.id.cb_select_all)
    CheckBox mSelectAll;
    @BindView(R.id.tv_select_info)
    TextView mSelectInfo;
    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.loading)
    TextView tvLoading;

    private OrderAdapter mOrderAdapter;
    private ArrayList<OrderModel> mOrderModels = new ArrayList<>();
    private int checkedOrder = 0;
    private double totalPrice = 0.00;
    private DecimalFormat df = new DecimalFormat("#.00");
    private String orderString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_to_ticket);

        ButterKnife.bind(this);
        init();
    }

    private void init() {
        titleBar.bindActivity(this);
        mSelectInfo.setText("");

        getOrderModels();

        mOrderAdapter = new OrderAdapter(this, mOrderModels, 1, new OrderAdapter.OnItemCheckChangeListener() {
            @Override
            public void onItemCheckedChanged(OrderModel model, boolean isChecked) {
                if (isChecked) {
                    checkedOrder++;
                    totalPrice += model.price;
                } else {
                    checkedOrder--;
                    totalPrice -= model.price;
                }
                if (checkedOrder <= 0) {
                    mSelectInfo. setText("");
                } else {
                    mSelectInfo.setText(checkedOrder + "个订单 共计费用" + df.format(totalPrice) + "元");
                }
            }
        });

        mSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mOrderAdapter.checkedAll();
                } else {
                    mOrderAdapter.unCheckedAll();
                }
            }
        });

        mContainer.setLayoutManager(new LinearLayoutManager(this));
        mContainer.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        mContainer.setAdapter(mOrderAdapter);
    }

    public void getOrderModels() {
        VolleyRequest.RequestGet(getApplicationContext(), "/Order/list?status=3", "getOrderModels",
            new VolleyListenerInterface(getApplicationContext(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
                @Override
                public void onMySuccess(String result) {
                    try{
                        System.out.println(result);
                        String id, date, name, content;
                        double money;
                        Date objDate;

                        mOrderModels.clear();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 E");
                        JSONObject jsonObject = new JSONObject(result), stationObj;
                        if (jsonObject.getString("code").equals("200")){
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("result"));
                            JSONObject obj = null;
                            for (int i = 0; i < jsonArray.length(); ++i){
                                obj = jsonArray.getJSONObject(i);
                                objDate = new Date(Long.parseLong(obj.getString("appointTime")));
                                stationObj = obj.getJSONObject("station");
                                mOrderModels.add(new OrderModel(obj.getString("id"), obj.getString("orderNum"), sdf.format(objDate), stationObj.getString("name"), "消费电量20%", 200.00));
                            }
                        }else {
                            Toast.makeText(OrderToTicketActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                        }
                        tvLoading.setVisibility(View.GONE);
                        mOrderAdapter.update(mOrderModels);
                    }catch (Exception e){
                        Toast.makeText(OrderToTicketActivity.this, "JSON解析失败", Toast.LENGTH_SHORT).show();
                        tvLoading.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
                @Override
                public void onMyError(VolleyError error) {

                }
            }
        );
    }

    @OnClick(R.id.btn_next_step)
    public void onNextStepClicked() {
        if (checkedOrder <= 0){
            Toast.makeText(this, "至少要选择一个订单哦～", Toast.LENGTH_SHORT).show();
        }else {
            orderString = "";
            ArrayList<OrderModel> models = mOrderAdapter.getCheckedOrders();
            for(int i = 0; i < models.size(); ++i){
                orderString += models.get(i).orderNum + ",";
            }
            orderString = orderString.substring(0, orderString.length() - 1);

            new CreateNewTicketActivity.Builder(this, orderString, String.valueOf(df.format(totalPrice))).start();
        }
    }

    @OnClick(R.id.vg_checkContainer)
    public void onViewClicked() {
        mSelectAll.setChecked(!mSelectAll.isChecked());
    }

    public static class Builder extends ActivityBuilder {

        public Builder(Context context) {
            super(context);
        }

        @Override
        public Intent create() {
            return new Intent(getContext(), OrderToTicketActivity.class);
        }
    }
}
