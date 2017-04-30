package com.haundianchi.huandianchi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.model.CarPositionModel;
import com.haundianchi.huandianchi.ui.order.OrderConfirmActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Burgess on 2017/4/17.
 */

public class CarPositionAdapter extends RecyclerView.Adapter<CarPositionViewHolder> {
    private Context context;
    private ArrayList<CarPositionModel> models;

    public CarPositionAdapter(Context context, ArrayList<CarPositionModel> models) {
        this.context = context;
        this.models = models;
    }

    @Override
    public CarPositionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CarPositionViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_car_position, parent, false), context);
    }

    @Override
    public void onBindViewHolder(CarPositionViewHolder holder, int position) {
        holder.bindData(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void updateAdapter(ArrayList<CarPositionModel> models){
        this.models = models;
        notifyDataSetChanged();
    }
}

class CarPositionViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_position_name)
    TextView mPositionName;
    @BindView(R.id.tv_position_detail)
    TextView mPositionDetail;

    private Context context;

    public CarPositionViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
    }

    public void bindData(CarPositionModel model) {
    }

    @OnClick(R.id.btn_view_more)
    public void onViewMoreClicked() {
    }

    @OnClick(R.id.vg_container)
    public void onContainerClicked() {
        new OrderConfirmActivity.Builder(context).start();
    }
}