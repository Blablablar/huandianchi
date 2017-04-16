package com.haundianchi.huandianchi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.model.TicketModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Burgess on 2017/4/16.
 */

public class TicketAdapter extends RecyclerView.Adapter<TicketViewHolder> {
    private Context context;
    private ArrayList<TicketModel> models;

    public TicketAdapter(Context context, ArrayList<TicketModel> models) {
        this.context = context;
        this.models = models;
    }

    @Override
    public TicketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TicketViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_ticket, parent, false));
    }

    @Override
    public void onBindViewHolder(TicketViewHolder holder, int position) {
        holder.bindData(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}

class TicketViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.ticket_name)
    TextView mTicketName;
    @BindView(R.id.ticket_time)
    TextView mTicketTime;
    @BindView(R.id.ticket_city)
    TextView mTicketCity;
    @BindView(R.id.ticket_property)
    TextView mTicketProperty;
    @BindView(R.id.price)
    TextView mPrice;
    @BindView(R.id.btn_ticket)
    ImageView mTicket;

    @OnClick(R.id.btn_ticket)
    void ticketClick(){

    }

    public TicketViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindData(TicketModel model) {
        mTicketName.setText(model.name);
        mTicketTime.setText(model.date);
        mTicketCity.setText(model.city);
        mTicketProperty.setText(model.property);
        mPrice.setText(String.valueOf(model.price));
    }
}