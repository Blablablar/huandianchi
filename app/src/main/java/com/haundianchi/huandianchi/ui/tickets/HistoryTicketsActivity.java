package com.haundianchi.huandianchi.ui.tickets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.adapter.TicketAdapter;
import com.haundianchi.huandianchi.model.TicketModel;
import com.haundianchi.huandianchi.ui.order.OrderToTicketActivity;
import com.haundianchi.huandianchi.utils.ActivityBuilder;
import com.haundianchi.huandianchi.widget.TitleBar;

import java.util.ArrayList;

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

    private TicketAdapter mAdapter;
    private ArrayList<TicketModel> mTicketModels = new ArrayList<>();
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

        mTicketModels.add(new TicketModel("发票", "2017.3.1", "上海", "华东师范大学", 250));
        mTicketModels.add(new TicketModel("发票", "2017.3.1", "上海", "华东师范大学", 250));
        mTicketModels.add(new TicketModel("发票", "2017.3.1", "上海", "华东师范大学", 250));
        mTicketModels.add(new TicketModel("发票", "2017.3.1", "上海", "华东师范大学", 250));
        //init adapter
        mAdapter = new TicketAdapter(this, mTicketModels, new TicketAdapter.OnTicketDetailClickListener() {
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

    public static class Builder extends ActivityBuilder{

        public Builder(Context context) {
            super(context);
        }

        @Override
        public Intent create() {
            return new Intent(getContext(), HistoryTicketsActivity.class);
        }
    }
}

