package com.haundianchi.huandianchi.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.adapter.CarPositionViewHolder_ViewBinding;
import com.haundianchi.huandianchi.ui.account.MyAccountActivity;
import com.haundianchi.huandianchi.ui.position.CarPositionActivity;
import com.haundianchi.huandianchi.ui.tickets.HistoryTicketsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by blablabla on 2017/4/11.
 */

public class HomePageActivity extends Activity implements View.OnClickListener {
    @BindView(R.id.location)
    ImageView mImgLocation;
    @BindView(R.id.tv_location)
    TextView mLocation;
    @BindView(R.id.imageView)
    ImageView mImageView;
    @BindView(R.id.id_nv_menu)
    NavigationView mNvMenu;
    @BindView(R.id.id_drawer_layout)
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }

    public void init() {
        View header = mNvMenu.getHeaderView(0);
        ImageView userImg = (ImageView) header.findViewById(R.id.iv_avatar);
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyAccountActivity.Builder(HomePageActivity.this).start();
            }
        });
        mNvMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //在这里处理item的点击事件
                switch (item.getItemId()){
                    case R.id.nav_ticket:
                        new HistoryTicketsActivity.Builder(HomePageActivity.this).start();
                        break;
                    case R.id.nav_position:
                        new CarPositionActivity.Builder(HomePageActivity.this).start();
                        break;
                }
                return true;
            }
        });
    }
}