package com.haundianchi.huandianchi.ui.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.utils.ActivityBuilder;
import com.haundianchi.huandianchi.widget.TitleBar;
import com.haundianchi.huandianchi.widget.dialog.OrderConfirmDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderConfirmActivity extends AppCompatActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mTitleBar.bindActivity(this);
    }

    @OnClick(R.id.btn_order)
    public void onViewClicked() {
        OrderConfirmDialog.Builder builder = new OrderConfirmDialog.Builder(this);
        builder.show();
    }

    public static class Builder extends ActivityBuilder {

        public Builder(Context context) {
            super(context);
        }

        @Override
        public Intent create() {
            return new Intent(getContext(), OrderConfirmActivity.class);
        }
    }
}
