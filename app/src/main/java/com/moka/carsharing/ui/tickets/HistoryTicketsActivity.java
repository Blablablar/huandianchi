package com.moka.carsharing.ui.tickets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.moka.carsharing.adapter.TicketAdapter;
import com.moka.carsharing.utils.ActivityBuilder;
import com.moka.carsharing.widget.TitleBar;
import com.moka.carsharing.Http.VolleyListenerInterface;
import com.moka.carsharing.Http.VolleyRequest;
import com.moka.carsharing.R;
import com.moka.carsharing.model.OrderModel;
import com.moka.carsharing.model.TicketModel;
import com.moka.carsharing.ui.order.OrderToTicketActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryTicketsActivity extends AppCompatActivity {
    @BindView(R.id.vg_container)
    RecyclerView mContainer;
    @BindView(R.id.btn_create_ticket)
    Button mCreateTicket;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.loading)
    TextView tvLoading;
    @BindView(R.id.ll_hint)
    LinearLayout hintll;

    private TicketAdapter mAdapter;
    private ArrayList<TicketModel> mTicketModels = new ArrayList<>();
    private ArrayList<OrderModel>[] orderModels;
    private LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);

    private Boolean move = false;
    private int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_tickets);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mTitleBar.bindActivity(this);

        getTicketModels();
        //init adapter
        orderModels = new ArrayList[mTicketModels.size()];
        for (int i = 0; i < mTicketModels.size(); ++i){
            orderModels[i] = new ArrayList<OrderModel>();
        }

        mAdapter = new TicketAdapter(this, mTicketModels, orderModels, new TicketAdapter.OnTicketDetailClickListener() {
            @Override
            public void onClick(int position) {
                mAdapter.setVisibilityInverse(position);
                mIndex = position;
                //Log.i("HistoryTicketsActivity", "Scroll To " + position);
                int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
                if (position <= lastItem && position >= lastItem - 1){
//                    mAdapter.notifyDataSetChanged();
                    int top = -400;
                    mContainer.scrollBy(0, 300);
                }
            }
        });

        mContainer.setLayoutManager(mLinearLayoutManager);
        mContainer.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        mContainer.setAdapter(mAdapter);

    }

    @OnClick(R.id.btn_create_ticket)
    public void onCreateClicked() {
        new OrderToTicketActivity.Builder(this).start();
    }

    public void getTicketModels() {
        VolleyRequest.RequestGet(getApplicationContext(), "/Invoice/list", "getTicketModels",
                new VolleyListenerInterface(getApplicationContext(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        try{
                            System.out.println(result);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.getString("code").equals("200")){
                                JSONArray arrs = new JSONArray(jsonObject.getString("result"));
                                orderModels = new ArrayList[arrs.length()];

                                for (int i = 0; i < arrs.length(); ++i){
                                    JSONObject res = arrs.getJSONObject(i);
                                    Date date = new Date(Long.parseLong(res.getString("createTime")));
                                    mTicketModels.add(new TicketModel(res.getString("id"), res.getString("body"), sdf.format(date),
                                            res.getString("address"), res.getString("title"), res.getString("amount")));

                                    JSONArray orderArr = new JSONArray(res.getString("orderList"));
                                    orderModels[i] = new ArrayList<OrderModel>();

                                    for(int j = 0; j < orderArr.length(); ++j){
                                        JSONObject orderObj = orderArr.getJSONObject(j),
                                            stationObj = orderObj.getJSONObject("station");
                                        Date date2 = new Date(Long.parseLong(orderObj.getString("appointTime")));
                                        orderModels[i].add(new OrderModel(orderObj.getString("id"), orderObj.getString("orderNum"), sdf.format(date2), stationObj.getString("name"),
                                                "消费电量" + (Double.valueOf(orderObj.getString("electricity")) - Double.valueOf(orderObj.getString("electricityOfBefore"))) + "%"
                                                , Double.parseDouble(orderObj.getString("price"))));
                                    }
                                }
                                hintll.setVisibility(View.INVISIBLE);
                                if(orderModels.length==0){
                                    hintll.setVisibility(View.VISIBLE);
                                }
                            }else{
                                Toast.makeText(HistoryTicketsActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                            }
                            tvLoading.setVisibility(View.GONE);
                        }catch (Exception e){
                            Toast.makeText(HistoryTicketsActivity.this, "JSON处理失败", Toast.LENGTH_SHORT).show();
                            tvLoading.setVisibility(View.GONE);
                            e.printStackTrace();
                        }finally {
                            mAdapter.update(mTicketModels, orderModels);
                        }

                    }
                    @Override
                    public void onMyError(VolleyError error) {

                    }
                });
    }

    public static class Builder extends ActivityBuilder {

        public Builder(Context context) {
            super(context);
        }

        @Override
        public Intent create() {
            return new Intent(getContext(), HistoryTicketsActivity.class);
        }
    }
}

