package com.haundianchi.huandianchi.ui.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.utils.ActivityBuilder;
import com.haundianchi.huandianchi.widget.TitleBar;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyAccountActivity extends AppCompatActivity {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.user_img)
    RoundedImageView userImg;
    @BindView(R.id.vg_edit_img)
    ViewGroup vgEditImg;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.vg_edit_username)
    ViewGroup vgEditUsername;
    @BindView(R.id.user_phone)
    TextView userPhone;
    @BindView(R.id.vg_edit_phone)
    ViewGroup vgEditPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        titleBar.bindActivity(this);
    }

    @OnClick({R.id.vg_edit_img, R.id.vg_edit_username, R.id.vg_edit_phone})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.vg_edit_img:
                break;
            case R.id.vg_edit_username:
                new MyUserNameEditActivity.Builder(this).start();
                break;
            case R.id.vg_edit_phone:
                break;
        }
    }

    public static class Builder extends ActivityBuilder {
        public Builder(Context context) {
            super(context);
        }

        @Override
        public Intent create() {
            return new Intent(getContext(), MyAccountActivity.class);
        }
    }
}
