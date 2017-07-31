package com.moka.carsharing.widget.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.moka.carsharing.ui.Indent.IndentActivity;
import com.moka.carsharing.R;
import com.moka.carsharing.ui.Indent.IndentDetailActivity;
import com.moka.carsharing.ui.position.NavigationActivity;

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
        private int width;
        private int height;
        private String orderId;
        @BindView(R.id.iv_car_gif)
        ImageView carGifIv;
        public Builder(@NonNull Context context,int width,int height,String id) {
            mBuilder = new AlertDialog.Builder(context);
            mContext = context;
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_order, null);
            ButterKnife.bind(this, view);
            mBuilder.setView(view);
            this.width=width;
            orderId=id;
            carGifIv.setImageResource(R.drawable.anim_car_gif);
            AnimationDrawable animationDrawable=(AnimationDrawable) carGifIv.getDrawable();
            animationDrawable.start();
        }

        public void show() {
            mDialog = mBuilder.create();
            //mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            mDialog.show();
            ColorDrawable dw = new ColorDrawable(0x00000000);
            mDialog.getWindow().setBackgroundDrawable(dw);
//            WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
//            params.width = width*9/10;
//            mDialog.getWindow().setAttributes(params);
        }

        @OnClick(R.id.btn_cancel)
        public void onBtnCancelClicked() {
            mDialog.dismiss();
            Intent intent = new Intent(mContext, IndentDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("id",orderId);
            mContext.startActivity(intent);
        }

        @OnClick(R.id.btn_confirm)
        public void onBtnConfirmClicked() {
            mDialog.dismiss();
            //new RestRouteShowActivity.Builder(mContext).start();
            new NavigationActivity.Builder(mContext,orderId).start();
        }
    }
}


