package com.moka.carsharing.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moka.carsharing.R;
import com.moka.carsharing.model.OrderModel;
import com.moka.carsharing.model.TicketModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Burgess on 2017/4/16.
 */

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    private Context context;
    private ArrayList<TicketModel> models;
    private ArrayList<OrderModel>[] orderModels;
    private OnTicketDetailClickListener listener;
    private Boolean[] visible;

    public TicketAdapter(Context context, ArrayList<TicketModel> models, ArrayList<OrderModel>[] orderModels, OnTicketDetailClickListener listener) {
        this.context = context;
        this.models = models;
        this.orderModels = orderModels;
        this.listener = listener;
        visible = new Boolean[models.size()];
        for (int i = 0; i < models.size(); ++i) {
            visible[i] = false;
        }
    }

    @Override
    public TicketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TicketViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_ticket, parent, false), context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(TicketViewHolder holder, int position) {
        holder.bindData(models.get(position), position, orderModels[position]);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void setVisibilityInverse(int position){
        for (int i = 0; i < models.size(); ++i) {
            if (i != position)
                visible[i] = false;
        }
        visible[position] = !visible[position];
        notifyDataSetChanged();
    }

    public void update(ArrayList<TicketModel> models, ArrayList<OrderModel>[] orderModels) {
        this.models = models;
        this.orderModels = orderModels;
        visible = new Boolean[models.size()];
        for (int i = 0; i < models.size(); ++i) {
            visible[i] = false;
        }
        notifyDataSetChanged();
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
        ViewGroup mTicket;
        @BindView(R.id.vg_orders)
        RecyclerView mOrderContainer;
        @BindView(R.id.img_tickect)
        ImageView mTicketImage;

        private int position;
        //private boolean visible = false;
        private OrderAdapter mOrderAdapter;
        private ArrayList<OrderModel> mOrderModels = new ArrayList<>();

        @OnClick(R.id.vg_root)
        void ticketClick() {
           //visible = !visible;
            listener.onClick(position);
        }

        public TicketViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mOrderAdapter = new OrderAdapter(context, mOrderModels);

            mOrderContainer.setLayoutManager(new LinearLayoutManager(context));
            mOrderContainer.addItemDecoration(new DividerItemDecoration(context,
                    DividerItemDecoration.VERTICAL));
            mOrderContainer.setAdapter(mOrderAdapter);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void bindData(TicketModel model, int position, ArrayList<OrderModel> mOrderModels) {
            this.mOrderModels = mOrderModels;
            mOrderAdapter.update(mOrderModels);

            mTicketName.setText(model.name);
            mTicketTime.setText(model.date);
            mTicketCity.setText(model.city);
            mTicketProperty.setText(model.property);
            mPrice.setText("¥" + String.valueOf(model.price) + "元");
            mOrderContainer.setVisibility(visible[position] ? View.VISIBLE : View.GONE);
            mTicketImage.setBackground(visible[position] ? context.getDrawable(R.mipmap.arrow_down) : context.getDrawable(R.mipmap.arrow_grey));

            this.position = position;
        }
    }

    public interface OnTicketDetailClickListener{
        void onClick(int position);
    }
}
