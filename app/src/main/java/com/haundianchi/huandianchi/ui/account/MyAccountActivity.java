package com.haundianchi.huandianchi.ui.account;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.haundianchi.huandianchi.R;
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
    @BindView(R.id.btn_edit_img)
    ImageView btnEditImg;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.btn_edit_username)
    ImageView btnEditUsername;
    @BindView(R.id.user_phone)
    TextView userPhone;
    @BindView(R.id.btn_edit_phone)
    ImageView btnEditPhone;

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

    @OnClick({R.id.btn_edit_img, R.id.btn_edit_username, R.id.btn_edit_phone})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_edit_img:
                break;
            case R.id.btn_edit_username:
                new MyUserNameEditActivityBuilder(this).start();
                break;
            case R.id.btn_edit_phone:
                break;
        }
    }
}
