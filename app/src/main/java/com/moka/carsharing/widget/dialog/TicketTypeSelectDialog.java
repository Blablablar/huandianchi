package com.moka.carsharing.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import com.moka.carsharing.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Burgess on 2017/4/16.
 */

public class TicketTypeSelectDialog extends AlertDialog {
    protected TicketTypeSelectDialog(@NonNull Context context) {
        super(context);
    }

    public static class Builder {
        @BindView(R.id.cb_paper)
        RadioButton cbPaper;
        @BindView(R.id.cb_electric)
        RadioButton cbElectric;
        private AlertDialog.Builder mBuilder;
        private AlertDialog mDialog;
        private OnConfirmClickedListener listener;

        public Builder(@NonNull Context context, final OnConfirmClickedListener listener) {
            this.listener = listener;
            mBuilder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_select_ticket, null);
            ButterKnife.bind(this, view);
            mBuilder.setView(view);
            mBuilder.setNegativeButton("取消", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();;
                }
            });
            mBuilder.setPositiveButton("确认", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onClick(cbPaper.isChecked() ? "纸质发票" : "电子发票");
                    dialog.dismiss();
                }
            });
            if (cbPaper.isChecked()){
                cbPaper.setChecked(true);
                cbElectric.setChecked(false);
            }else{
                cbPaper.setChecked(false);
                cbElectric.setChecked(true);
            }
        }

        public void show() {
            mDialog = mBuilder.create();
            mDialog.show();
        }

        @OnClick(R.id.vg_paper_container)
        public void onVgPaperContainerClicked() {
            cbPaper.setChecked(true);
            cbElectric.setChecked(false);
        }

        @OnClick(R.id.vg_electric_container)
        public void onVgElectricContainerClicked() {
            cbPaper.setChecked(false);
            cbElectric.setChecked(true);
        }
    }

    public interface OnConfirmClickedListener {
        void onClick(String result);
    }
}

