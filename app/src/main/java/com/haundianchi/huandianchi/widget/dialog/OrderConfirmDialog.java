package com.haundianchi.huandianchi.widget.dialog;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.ui.Indent.IndentActivity;
import com.haundianchi.huandianchi.ui.position.NavigationActivity;
import com.haundianchi.huandianchi.ui.tickets.CreateNewTicketActivity;
import com.haundianchi.huandianchi.ui.tickets.HistoryTicketsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Burgess on 2017/4/20.
 */

public class OrderConfirmDialog extends AlertDialog {
    protected OrderConfirmDialog(@NonNull Context context) {
        super(context);
    }

    public static class Builder {
        private AlertDialog.Builder mBuilder;
        private AlertDialog mDialog;
        private Context mContext;

        public Builder(@NonNull Context context) {
            mBuilder = new AlertDialog.Builder(context);
            mContext = context;
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_order, null);
            ButterKnife.bind(this, view);
            mBuilder.setView(view);
        }

        public void show() {
            mDialog = mBuilder.create();
            //mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            mDialog.show();
        }

        @OnClick(R.id.btn_cancel)
        public void onBtnCancelClicked() {
            mDialog.dismiss();
            Intent intent = new Intent(mContext, IndentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
        }

        @OnClick(R.id.btn_confirm)
        public void onBtnConfirmClicked() {
            mDialog.dismiss();
            new NavigationActivity.Builder(mContext).start();
        }
    }
}


