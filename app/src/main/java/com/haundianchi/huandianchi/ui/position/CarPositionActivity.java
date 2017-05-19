package com.haundianchi.huandianchi.ui.position;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.android.volley.VolleyError;
import com.haundianchi.huandianchi.Http.VolleyListenerInterface;
import com.haundianchi.huandianchi.Http.VolleyRequest;
import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.adapter.CarPositionAdapter;
import com.haundianchi.huandianchi.model.CarPositionModel;
import com.haundianchi.huandianchi.model.OrderModel;
import com.haundianchi.huandianchi.model.TicketModel;
import com.haundianchi.huandianchi.ui.tickets.HistoryTicketsActivity;
import com.haundianchi.huandianchi.utils.ActivityBuilder;
import com.haundianchi.huandianchi.utils.SensorEventHelper;
import com.haundianchi.huandianchi.utils.SharedPreferencesHelper;
import com.haundianchi.huandianchi.widget.TitleBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarPositionActivity extends AppCompatActivity implements LocationSource, View.OnTouchListener,
        AMapLocationListener, AMap.OnMarkerClickListener, ActivityCompat.OnRequestPermissionsResultCallback{
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


    @OnClick(R.id.btn_more)
    public void onMoreClicked() {
        scrollList();
    }

    private void scrollList() {
        int deltaHeight = mContainer.getHeight();
        if (mSearchResult.getTranslationY() < deltaHeight) {//向下滑动
            ObjectAnimator.ofFloat(mSearchResult, "translationY", mSearchResult.getTranslationY(), deltaHeight).setDuration(100).start();
        } else if (mSearchResult.getTranslationY() > 0) {//向上滑动
            ObjectAnimator.ofFloat(mSearchResult, "translationY", mSearchResult.getTranslationY(), 0).setDuration(100).start();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        int deltaHeight = mContainer.getHeight();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                //计算移动的距离
                int offY = y - lastY;
                Log.i("offY :", String.valueOf(offY));
                if (offY > 0 && mSearchResult.getTranslationY() < deltaHeight) {//向下滑动
                    ObjectAnimator.ofFloat(mSearchResult, "translationY", mSearchResult.getTranslationY(), deltaHeight).setDuration(100).start();
                }
                if (offY < 0 && mSearchResult.getTranslationY() > 0) {//向上滑动
                    ObjectAnimator.ofFloat(mSearchResult, "translationY", mSearchResult.getTranslationY(), 0).setDuration(100).start();
                }
                break;
        }
        return true;
    }

    @OnClick(R.id.btn_location)
    public void onViewClicked() {
        //函数节流
        newClick = new Date();
        if ((newClick.getTime() - lastClick.getTime()) > 2000){
            mLocationClient.startLocation();
            lastClick = new Date();
        }else {
            Toast.makeText(this, "请勿短时间内连续定位", Toast.LENGTH_SHORT).show();
        }
    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private UiSettings mUiSettings;//定义一个UiSettings对象
    private CarPositionAdapter mAdapter;
    private ArrayList<CarPositionModel> models = new ArrayList<>();
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClientOption mLocationOption;

    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = false;
    private Marker mLocMarker;
    private String locationId;

    private SensorEventHelper mSensorHelper;
    private Circle mCircle;
    public static final String LOCATION_MARKER_FLAG = "mylocation";
    public static final String POSITION_MARKER_FLAG = "car_position";
    private Marker[] markers;


    private int lastX;
    private int lastY;

    private Date lastClick;
    private Date newClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_postion);
        ButterKnife.bind(this);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写

        init();
    }

    private void init() {
        lastClick = new Date();
        mTitleBar.bindActivity(this);
        initSearchView();
        mSearchResult.setOnTouchListener(this);
        getStationModels();
//        models.add(new CarPositionModel("某某电站XX", "距离您275米，武宁路201号"));
//        models.add(new CarPositionModel("某某电站XX", "距离您275米，武宁路201号"));
//        models.add(new CarPositionModel("某某电站XX", "距离您275米，武宁路201号"));
//        models.add(new CarPositionModel("某某电站XX", "距离您275米，武宁路201号"));
        //init adapter
        mAdapter = new CarPositionAdapter(this, models);

        mContainer.setLayoutManager(new LinearLayoutManager(this));
        mContainer.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        mContainer.setAdapter(mAdapter);
        adjustRecycleView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION);
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                initMap();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        }else{
            initMap();
        }

    }

    public void getStationModels() {
        VolleyRequest.RequestGet(getApplicationContext(), "/Station/list", "getStationModels",
                new VolleyListenerInterface(getApplicationContext(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        try{
                            System.out.println(result);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.getString("code").equals("200")){
                                JSONArray arrs = new JSONArray(jsonObject.getString("result"));
                                models.clear();

                                for (int i = 0; i < arrs.length(); ++i){
                                    JSONObject res = arrs.getJSONObject(i);
                                    CarPositionModel model = new CarPositionModel(res.getString("id"), res.getString("name"), res.getString("address"));
                                    model.setLng(res.getString("lat"), res.getString("lng"));
                                    models.add(model);
                                }
                                mAdapter.updateAdapter(models);

                            }else{
                                Toast.makeText(CarPositionActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Toast.makeText(CarPositionActivity.this, "JSON处理失败", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onMyError(VolleyError error) {

                    }
                });
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
                mAdapter.updateAdapter(models);
                if (mSearchResult.getTranslationY() > 0) {//向上滑动
                    ObjectAnimator.ofFloat(mSearchResult, "translationY", mSearchResult.getTranslationY(), 0).setDuration(100).start();
                }
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

    public static boolean setOnCloseListener(final SearchView search, final SearchView.OnCloseListener listener) {
        try {
            //final int id = Resources.getSystem().getIdentifier("search_close_btn", "id", "android");
            final ImageView close = (ImageView) search.findViewById(R.id.search_close_btn);
            close.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (listener == null || !listener.onClose()) {
                        search.setQuery("", false);
                        search.clearFocus();
                    }
                }

            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            setUpMap();
        }
        mSensorHelper = new SensorEventHelper(this);
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }

        aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                //隐藏软键盘
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(CarPositionActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                //隐藏具体列表项
                int deltaHeight = mContainer.getHeight();
                if (mSearchResult.getTranslationY() < deltaHeight) {//向下滑动
                    ObjectAnimator.ofFloat(mSearchResult, "translationY", mSearchResult.getTranslationY(), deltaHeight).setDuration(100).start();
                }
            }
        });
        aMap.setOnMarkerClickListener(this);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    private void startLocation(){
    }

    private void adjustRecycleView(){
        if (mAdapter.getItemCount() >= 3){
            mContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.pos_recycle_height)));
        }else {
            mContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                LatLng location = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());

                SharedPreferencesHelper.getInstance(this).putString("sLat", String.valueOf(amapLocation.getLatitude()));
                SharedPreferencesHelper.getInstance(this).putString("sLon", String.valueOf(amapLocation.getLongitude()));
                if (!mFirstFix) {
                    mFirstFix = true;
                    addCircle(location, amapLocation.getAccuracy());//添加定位精度圆
                    addMarker(location);//添加定位图标
                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                    //标记车站
                    LatLng[] latlngs = new LatLng[3];
                    latlngs[0]= new LatLng(amapLocation.getLatitude() + 0.003, amapLocation.getLongitude() + 0.003);
                    latlngs[1] = new LatLng(amapLocation.getLatitude() - 0.003, amapLocation.getLongitude() - 0.003);
                    latlngs[2] = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude() - 0.003);
                    models.get(0).setLng(latlngs[0]);
                    models.get(1).setLng(latlngs[1]);
                    mAdapter.updateAdapter(models);
                    addPosMarkers(latlngs);
                } else {
                    mCircle.setCenter(location);
                    mCircle.setRadius(amapLocation.getAccuracy());
                    mLocMarker.setPosition(location);
                }
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
                Log.e("AmapErr", errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setOnceLocation(true);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.navi_map_gps_locked);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);

//		BitmapDescriptor des = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
        mLocMarker.setZIndex(1);
        locationId = mLocMarker.getId();
    }

    private void addPosMarkers(LatLng[] latlngs) {
        markers = new Marker[latlngs.length];
        int i = 0;
        for (LatLng latlng : latlngs) {
            Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.pos_marker);
            BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);

            MarkerOptions options = new MarkerOptions();
            options.icon(des);
            options.anchor(0.5f, 0.5f);
            options.position(latlng);
            markers[i] = aMap.addMarker(options);
            markers[i++].setZIndex(10);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(Objects.equals(marker.getId(), locationId)) return false;
        ArrayList<CarPositionModel> modelList = new ArrayList<>();
        modelList.add(models.get(0));
        mAdapter.updateAdapter(modelList);
        adjustRecycleView();
        if (mSearchResult.getVisibility() == View.GONE)
            mSearchResult.setVisibility(View.VISIBLE);
        if (mSearchResult.getTranslationY() > 0) {//向上滑动
            ObjectAnimator.ofFloat(mSearchResult, "translationY", mSearchResult.getTranslationY(), 0).setDuration(100).start();
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
        mRoot.requestFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    initMap();
                } else {
                    // Permission Denied
                    Toast.makeText(CarPositionActivity.this, "Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
