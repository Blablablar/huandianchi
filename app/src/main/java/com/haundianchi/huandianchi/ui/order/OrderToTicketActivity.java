package com.haundianchi.huandianchi.ui.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.adapter.OrderAdapter;
import com.haundianchi.huandianchi.model.OrderModel;
import com.haundianchi.huandianchi.ui.tickets.CreateNewTicketActivity;
import com.haundianchi.huandianchi.utils.ActivityBuilder;
import com.haundianchi.huandianchi.widget.TitleBar;

import java.text.DecimalFormat;
import java.util.ArrayList;

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

    private OrderAdapter mOrderAdapter;
    private ArrayList<OrderModel> mOrderModels = new ArrayList<>();
    private int checkedOrder = 0;
    private double totalPrice = 0.00;
    private DecimalFormat df = new DecimalFormat("#.00");

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

        mOrderModels.add(new OrderModel("2017.02.03 周五 19：31", "曹阳路500号电站", "消费电量20%", 200.00));
        mOrderModels.add(new OrderModel("2017.02.03 周五 19：31", "曹阳路500号电站", "消费电量20%", 200.00));
        mOrderModels.add(new OrderModel("2017.02.03 周五 19：31", "曹阳路500号电站", "消费电量20%", 200.00));
        mOrderModels.add(new OrderModel("2017.02.03 周五 19：31", "曹阳路500号电站", "消费电量20%", 200.00));

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

    @OnClick(R.id.btn_next_step)
    public void onNextStepClicked() {
        new CreateNewTicketActivity.Builder(this).start();
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
