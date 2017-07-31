package com.moka.carsharing.ui.position;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.moka.carsharing.ui.Indent.IndentActivity;
import com.moka.carsharing.ui.Indent.IndentDetailActivity;
import com.moka.carsharing.utils.ActivityBuilder;
import com.moka.carsharing.utils.SharedPreferencesHelper;
import com.moka.carsharing.R;

/**
 * Created by Burgess on 2017/4/30.
 */

public class NavigationActivity extends BaseNavActivity implements AMapNaviViewListener {
    private static String orderNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        AMapNaviViewOptions options = new AMapNaviViewOptions();
        options.setTilt(0);
        mAMapNaviView.setViewOptions(options);
        NaviLatLng sLng = new NaviLatLng(Double.valueOf(SharedPreferencesHelper.getInstance(this).getString("sLat")),
                Double.valueOf(SharedPreferencesHelper.getInstance(this).getString("sLon")));
        NaviLatLng eLng = new NaviLatLng(Double.valueOf(SharedPreferencesHelper.getInstance(this).getString("tLat")),
                Double.valueOf(SharedPreferencesHelper.getInstance(this).getString("tLon")));
        setStartEnd(sLng, eLng);
    }

    private void setStartEnd(NaviLatLng sLng, NaviLatLng eLng){
        sList.clear();
        eList.clear();
        sList.add(sLng);
        eList.add(eLng);
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        /**
         * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
         *
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
         */
        int strategy = 0;
        try {
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);

    }

    @Override
    public void onCalculateRouteSuccess() {
        super.onCalculateRouteSuccess();
        mAMapNavi.startNavi(NaviType.GPS);
    }

    public static class Builder extends ActivityBuilder {

        public Builder(Context context,String id) {
            super(context);
            orderNum=id;
        }

        @Override
        public Intent create() {
            return new Intent(getContext(), NavigationActivity.class);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
    }
    @Override
    public boolean onNaviBackClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认退出导航？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(getApplicationContext(), IndentDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("id",orderNum);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent=new Intent(this, IndentDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("id",orderNum);
            startActivity(intent);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
