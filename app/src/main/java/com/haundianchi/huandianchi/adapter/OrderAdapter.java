package com.haundianchi.huandianchi.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.model.OrderModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Burgess on 2017/4/27.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private ArrayList<OrderModel> models;
    private int type = 0;//0不显示选择框
    private OnItemCheckChangeListener listener;
    private Boolean[] isChecked;

    public OrderAdapter(Context context, ArrayList<OrderModel> models) {
        this(context, models, 0, null);
    }

    public OrderAdapter(Context context, ArrayList<OrderModel> models, int type, OnItemCheckChangeListener listener) {
        this.type = type;
        this.listener = listener;
        this.context = context;
        this.models = models;
        isChecked = new Boolean[models.size()];
        for (int i = 0; i < models.size(); ++i) {
            isChecked[i] = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_order, parent, false));
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        holder.bindData(models.get(position), position);
    }

    public void update(ArrayList<OrderModel> models){
        this.models = models;
        isChecked = new Boolean[models.size()];
        for (int i = 0; i < models.size(); ++i) {
            isChecked[i] = false;
        }
        notifyDataSetChanged();
    }

    public double getTotalPrice() {
        double total = 0;
        for (int i = 0; i < models.size(); ++i) {
            total += models.get(i).price;
        }
        return total;
    }

    public void unCheckedAll() {
        for (int i = 0; i < models.size(); ++i) {
            isChecked[i] = false;
        }
        notifyDataSetChanged();
    }

    public void checkedAll() {
        for (int i = 0; i < models.size(); ++i) {
            isChecked[i] = true;
        }
        notifyDataSetChanged();
    }

    public ArrayList<OrderModel> getCheckedOrders() {
        ArrayList<OrderModel> tmpModels = new ArrayList<>();
        for (int i = 0; i < models.size(); ++i) {
            if (isChecked[i]){
                tmpModels.add(models.get(i));
            }
        }
        return  tmpModels;
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cb_check)
        CheckBox mCheck;
        @BindView(R.id.vg_root)
        LinearLayout vgRoot;
        @BindView(R.id.order_time)
        TextView mTime;
        @BindView(R.id.order_position)
        TextView mPosition;
        @BindView(R.id.order_info)
        TextView mInfo;
        @BindView(R.id.price)
        TextView mPrice;

        private int position;
        private OrderModel model;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public OrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (type != 0) {
                vgRoot.setBackground(context.getDrawable(R.color.white));
                mCheck.setVisibility(View.VISIBLE);
                mCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        listener.onItemCheckedChanged(model, isChecked);
                    }
                });
            }
        }

        public void bindData(OrderModel model, int position) {
            this.position = position;
            this.model = model;

            mTime.setText(model.date);
            mPosition.setText(model.position);
            mInfo.setText(model.content);
            mPrice.setText(String.valueOf(model.price));
            mCheck.setChecked(isChecked[position]);
        }

        @OnClick(R.id.vg_root)
        public void onViewClicked() {
            isChecked[position] = !mCheck.isChecked();
            mCheck.setChecked(isChecked[position]);
        }
    }

    public interface OnItemCheckChangeListener {
        void onItemCheckedChanged(OrderModel model, boolean isChecked);
    }
}

