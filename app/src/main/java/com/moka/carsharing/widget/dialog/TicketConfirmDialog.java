package com.moka.carsharing.widget.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.moka.carsharing.R;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Burgess on 2017/4/16.
 */

public class TicketConfirmDialog extends AlertDialog {
    protected TicketConfirmDialog(@NonNull Context context) {
        super(context);
    }

    public static class Builder {
        private final HashMap<String, String> params;
        @BindView(R.id.tv_ticket_type)
        TextView tvTicketType;
        @BindView(R.id.tv_ticket_property)
        TextView tvTicketProperty;
        @BindView(R.id.tv_ticket_receiver_info)
        TextView tvTicketReceiverInfo;
        @BindView(R.id.tv_ticket_address)
        TextView tvTicketAddress;
        @BindView(R.id.tv_ticket_detail_address)
        TextView tvTicketDetailAddress;
        @BindView(R.id.tv_ticket_money)
        TextView tvTicketMoney;


        private AlertDialog.Builder mBuilder;
        private AlertDialog mDialog;
        private OnConfirmClickedListener listener;

        public Builder(@NonNull Context context, HashMap<String, String> params, OnConfirmClickedListener listener) {
            this.listener = listener;
            this.params = params;
            mBuilder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_ticket, null);
            view.setBackgroundColor(Color.TRANSPARENT);
            ButterKnife.bind(this, view);
            mBuilder.setView(view);
            tvTicketType.setText(params.get("type"));
            tvTicketProperty.setText(params.get("title"));
            tvTicketReceiverInfo.setText(params.get("receiver") + " " + params.get("mobile"));
            tvTicketDetailAddress.setText(params.get("address"));
        }

        public void show() {
            mDialog = mBuilder.create();
            mDialog.show();
            ColorDrawable dw = new ColorDrawable(0x00000000);
            mDialog.getWindow().setBackgroundDrawable(dw);
        }

        @OnClick(R.id.btn_cancel)
        public void onBtnCancelClicked() {
            mDialog.dismiss();
        }

        @OnClick(R.id.btn_confirm)
        public void onBtnConfirmClicked() {
            listener.onClick();
            mDialog.dismiss();
        }
    }

    public interface OnConfirmClickedListener{
        void onClick();
    }
}

