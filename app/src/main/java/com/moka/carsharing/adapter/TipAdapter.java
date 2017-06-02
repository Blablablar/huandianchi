package com.moka.carsharing.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moka.carsharing.R;
import com.moka.carsharing.model.TipModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Burgess on 2017/5/21.
 */

public class TipAdapter extends RecyclerView.Adapter<TipAdapter.TipViewHolder> {
    private Context context;
    private ArrayList<TipModel> models;
    private OnItemClickListener listener;

    public TipAdapter(Context context, ArrayList<TipModel> models) {
        this(context, models, null);
    }

    public TipAdapter(Context context, ArrayList<TipModel> models, OnItemClickListener listener) {
        this.listener = listener;
        this.context = context;
        this.models = models;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public TipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TipViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_tip, parent, false));
    }

    @Override
    public void onBindViewHolder(TipViewHolder holder, int position) {
        holder.bindData(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void update(ArrayList<TipModel> tipModels) {
        this.models = tipModels;
        notifyDataSetChanged();
    }

    class TipViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_poi)
        TextView tvPoi;

        private TipModel model;

        public TipViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(TipModel model) {
            this.model = model;
            tvPoi.setText(model.name + "-" + model.address);
        }

        @OnClick(R.id.vg_root)
        public void onViewClicked() {
            listener.onItemClicked(model);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(TipModel model);
    }
}
