package com.haundianchi.huandianchi.ui.tickets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.adapter.TicketAdapter;
import com.haundianchi.huandianchi.model.TicketModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryTicketsActivity extends AppCompatActivity {

    @BindView(R.id.vg_container)
    RecyclerView mContainer;
    @BindView(R.id.btn_create_ticket)
    Button mCreateTicket;

    private TicketAdapter mAdapter;
    private ArrayList<TicketModel> mTicketModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_tickets);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mTicketModels.add(new TicketModel("发票", "2017.3.1", "上海", "华东师范大学", 250));
        mTicketModels.add(new TicketModel("发票", "2017.3.1", "上海", "华东师范大学", 250));
        mTicketModels.add(new TicketModel("发票", "2017.3.1", "上海", "华东师范大学", 250));
        mTicketModels.add(new TicketModel("发票", "2017.3.1", "上海", "华东师范大学", 250));
        //init adapter
        mAdapter = new TicketAdapter(this, mTicketModels);

        mContainer.setLayoutManager(new LinearLayoutManager(this));
        mContainer.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        mContainer.setAdapter(mAdapter);
    }

    @OnClick(R.id.btn_create_ticket)
    public void onCreateClicked() {
    }
}
