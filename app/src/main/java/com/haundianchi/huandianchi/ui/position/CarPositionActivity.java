package com.haundianchi.huandianchi.ui.position;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.adapter.CarPositionAdapter;
import com.haundianchi.huandianchi.model.CarPositionModel;
import com.haundianchi.huandianchi.ui.tickets.HistoryTicketsActivity;
import com.haundianchi.huandianchi.utils.ActivityBuilder;
import com.haundianchi.huandianchi.widget.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarPositionActivity extends AppCompatActivity implements LocationSource {
    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.search)
    SearchView mSearch;
    @BindView(R.id.tv_position_car)
    TextView mPositionCar;
    @BindView(R.id.vg_container)
    RecyclerView mContainer;
    @BindView(R.id.vg_root)
    ViewGroup mRoot;
    @BindView(R.id.vg_search_result)
    ViewGroup mSearchResult;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    private UiSettings mUiSettings;//定义一个UiSettings对象
    private CarPositionAdapter mAdapter;
    private ArrayList<CarPositionModel> models = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_postion);
        ButterKnife.bind(this);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写

        init();
    }

    private void init() {
        mTitleBar.bindActivity(this);
        initSearchView();

        models.add(new CarPositionModel("某某电站XX", "距离您275米，武宁路201号"));
        models.add(new CarPositionModel("某某电站XX", "距离您275米，武宁路201号"));
        models.add(new CarPositionModel("某某电站XX", "距离您275米，武宁路201号"));
        models.add(new CarPositionModel("某某电站XX", "距离您275米，武宁路201号"));
        //init adapter
        mAdapter = new CarPositionAdapter(this, models);

        mContainer.setLayoutManager(new LinearLayoutManager(this));
        mContainer.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        mContainer.setAdapter(mAdapter);

        initMap();

    }

    private void initSearchView() {
        mSearch.clearFocus();
        mSearch.setIconified(false);
        SearchView.SearchAutoComplete mEdit = (SearchView.SearchAutoComplete) mSearch.findViewById(R.id.search_src_text);
        mSearch.setIconifiedByDefault(false);
        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (mSearchResult.getVisibility() == View.GONE)
                    mSearchResult.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        setOnCloseListener(mSearch, new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mSearchResult.setVisibility(View.GONE);
                mSearchResult.clearFocus();
                return false;
            }
        });
    }

    public static boolean setOnCloseListener (final SearchView search, final SearchView.OnCloseListener listener) {
        try {
            //final int id = Resources.getSystem().getIdentifier("search_close_btn", "id", "android");
            final ImageView close = (ImageView)search.findViewById(R.id.search_close_btn);
            close.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick (View view) {
                    if (listener == null || !listener.onClose()) {
                        search.setQuery("", false);
                        search.clearFocus();
                    }
                }

            });
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private void initMap() {
        AMap aMap = mMapView.getMap();
        mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
//        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        aMap.setLocationSource(this);//通过aMap对象设置定位数据源的监听

        //mUiSettings.setMyLocationButtonEnabled(true); //显示默认的定位按钮

        aMap.setMyLocationEnabled(true);// 可触发定位并显示当前位置
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
        mRoot.requestFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
    }

    @Override
    public void deactivate() {

    }

    @OnClick(R.id.btn_more)
    public void onMoreClicked() {
        if (mContainer.getVisibility() == View.VISIBLE){
            mContainer.setVisibility(View.GONE);
        }else{
            mContainer.setVisibility(View.VISIBLE);
        }
    }

    public static class Builder extends ActivityBuilder {

        public Builder(Context context) {
            super(context);
        }

        @Override
        public Intent create() {
            return new Intent(getContext(), CarPositionActivity.class);
        }
    }
}
