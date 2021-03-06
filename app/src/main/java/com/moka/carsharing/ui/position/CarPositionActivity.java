package com.moka.carsharing.ui.position;

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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
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
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.android.volley.VolleyError;
import com.moka.carsharing.Http.VolleyRequest;
import com.moka.carsharing.adapter.CarPositionAdapter;
import com.moka.carsharing.adapter.TipAdapter;
import com.moka.carsharing.cache.CarInfo;
import com.moka.carsharing.model.CarPositionModel;
import com.moka.carsharing.model.TipModel;
import com.moka.carsharing.ui.HomePageActivity;
import com.moka.carsharing.utils.ActivityBuilder;
import com.moka.carsharing.utils.SharedPreferencesHelper;
import com.moka.carsharing.widget.TitleBar;
import com.moka.carsharing.Http.VolleyListenerInterface;
import com.moka.carsharing.R;
import com.moka.carsharing.utils.SensorEventHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarPositionActivity extends AppCompatActivity implements LocationSource, View.OnTouchListener,
        AMapLocationListener, AMap.OnMarkerClickListener, ActivityCompat.OnRequestPermissionsResultCallback,
        GeocodeSearch.OnGeocodeSearchListener, Inputtips.InputtipsListener {
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
    @BindView(R.id.vg_search_poi)
    RecyclerView vgSearchPoi;
    @BindView(R.id.btn_more)
    LinearLayout btnMore;
    @BindView(R.id.btn_arrow)
    ImageButton arrowBtn;
    @BindView(R.id.rl_arrow)
    RelativeLayout arrowRl;


    @OnClick(R.id.btn_more)
    public void onMoreClicked() {
        scrollList();
    }
    @OnClick(R.id.rl_arrow)
    public void onArrowClicked() {
        scrollList();
    }

    private void scrollList() {
        int deltaHeight = mContainer.getHeight();
        if (mSearchResult.getTranslationY() < deltaHeight) {//向下滑动
            ObjectAnimator.ofFloat(mSearchResult, "translationY", mSearchResult.getTranslationY(), deltaHeight).setDuration(100).start();
            arrowBtn.setImageDrawable(getResources().getDrawable(R.mipmap.arrow_up));

        } else if (mSearchResult.getTranslationY() > 0) {//向上滑动
            ObjectAnimator.ofFloat(mSearchResult, "translationY", mSearchResult.getTranslationY(), 0).setDuration(100).start();
            arrowBtn.setImageDrawable(getResources().getDrawable(R.mipmap.arrow_down));
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
        if ((newClick.getTime() - lastClick.getTime()) > 2000) {
            mLocationClient.startLocation();
            lastClick = new Date();
        } else {
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
    private GeocodeSearch geocoderSearch;

    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = false;
    private String locationId;

    private SensorEventHelper mSensorHelper;
    private Circle mCircle;

    private Marker mLocMarker;
    private Marker[] markers;
    private Marker tipMarker;


    private int lastX;
    private int lastY;

    private Date lastClick;
    private Date newClick;
    private String cityCode;

    private ArrayList<TipModel> tipModels = new ArrayList<>();
    private TipAdapter mTipAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_postion);
        ButterKnife.bind(this);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        if(CarInfo.endAddressX!=null&&CarInfo.endAddressY!=null)
            init();
        else
            Toast.makeText(getApplicationContext(), "车辆位置获取失败", Toast.LENGTH_SHORT).show();
    }

    private void init() {
        lastClick = new Date();
        mTitleBar.bindActivity(this);
        initSearchView();
        mSearchResult.setOnTouchListener(this);

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        //init adapter
        mAdapter = new CarPositionAdapter(this, models);

        mContainer.setLayoutManager(new LinearLayoutManager(this));
        mContainer.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        mContainer.setAdapter(mAdapter);
        adjustRecycleView();

        mTipAdapter = new TipAdapter(this, tipModels, new TipAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(TipModel model) {
                //隐藏软键盘
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(CarPositionActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                //标记地点，搜索附近直线距离30公里内站点
                addTipMarker(model.latLng);

                ArrayList<CarPositionModel> filterModels = searchFilter(model.latLng);
                if (filterModels.size() == 0){
                    Toast.makeText(CarPositionActivity.this, "很抱歉，附近没有电站点", Toast.LENGTH_SHORT).show();
                }
                mAdapter.updateAdapter(filterModels);
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(model.latLng, 14));
                vgSearchPoi.setVisibility(View.GONE);
            }
        });

        vgSearchPoi.setLayoutManager(new LinearLayoutManager(this));
        vgSearchPoi.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        vgSearchPoi.setAdapter(mTipAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) | ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        } else {
            initMap();
        }

    }

    public void getStationModels() {
        VolleyRequest.RequestGet(getApplicationContext(), "/Station/list", "getStationModels",
                new VolleyListenerInterface(getApplicationContext(), VolleyListenerInterface.mListener, VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        try {
                            System.out.println(result);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                            JSONObject jsonObject = new JSONObject(result);

                            if (jsonObject.getString("code").equals("200")) {
                                JSONArray arrs = new JSONArray(jsonObject.getString("result"));
                                models.clear();
                                //清除markers
                                clearMarkers();
                                markers = new Marker[arrs.length()];

                                for (int i = 0; i < arrs.length(); ++i) {
                                    JSONObject res = arrs.getJSONObject(i);
                                    CarPositionModel model = new CarPositionModel(res.getString("id"), res.getString("name"), res.getString("address"), res.getString("validity"), res.getString("model"));
                                    model.setLng(res.getString("lat"), res.getString("lng"));
                                    models.add(model);
                                }
                                mAdapter.updateAdapter(models);
                                //动态设置高度
                                adjustRecycleView();
                            } else {
                                Toast.makeText(CarPositionActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
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
        mSearch.setIconifiedByDefault(false);
        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                if (mSearchResult.getVisibility() == View.GONE)
//                    mSearchResult.setVisibility(View.VISIBLE);
//                ArrayList<CarPositionModel> filterModels = searchFilter(models, query);
//                mAdapter.updateAdapter(filterModels);
//                if (mSearchResult.getTranslationY() > 0) {//向上滑动
//                    ObjectAnimator.ofFloat(mSearchResult, "translationY", mSearchResult.getTranslationY(), 0).setDuration(100).start();
//                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                vgSearchPoi.setVisibility(View.VISIBLE);
                InputtipsQuery inputquery = new InputtipsQuery(newText, cityCode);
                inputquery.setCityLimit(true);//限制在当前城市
                Inputtips inputTips = new Inputtips(CarPositionActivity.this, inputquery);
                inputTips.setInputtipsListener(CarPositionActivity.this);

                inputTips.requestInputtipsAsyn();
                if (newText.equals("")){
                    vgSearchPoi.setVisibility(View.GONE);
                }

                return false;
            }
        });
        mSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    vgSearchPoi.setVisibility(View.VISIBLE);
                    InputtipsQuery inputquery = new InputtipsQuery(mSearch.getQuery().toString(), cityCode);
                    inputquery.setCityLimit(true);//限制在当前城市
                    Inputtips inputTips = new Inputtips(CarPositionActivity.this, inputquery);
                    inputTips.setInputtipsListener(CarPositionActivity.this);

                    inputTips.requestInputtipsAsyn();
                }else{
                    vgSearchPoi.setVisibility(View.GONE);
                }
            }
        });
        setOnCloseListener(mSearch, new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mAdapter.updateAdapter(models);
                vgSearchPoi.setVisibility(View.GONE);
                return false;
            }
        });
    }

    private ArrayList<CarPositionModel> searchFilter(LatLng latLng) {
        ArrayList<CarPositionModel> filterModels = new ArrayList<>();
        for (CarPositionModel model : models) {
            if (AMapUtils.calculateLineDistance(model.getLng(), latLng) / 1000 < 30) {
                filterModels.add(model);
            }
        }
        return filterModels;
    }

    public static boolean setOnCloseListener(final SearchView search, final SearchView.OnCloseListener listener) {
        try {
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
        getStationModels();
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

    private void startLocation() {
    }

    private void adjustRecycleView() {
        if (mAdapter.getItemCount() >= 3) {
            mContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.pos_recycle_height)));
        } else {
            mContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            LatLng location = new LatLng(Double.parseDouble(CarInfo.endAddressX), Double.parseDouble(CarInfo.endAddressY));//new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
            LatLng navLocation = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
            // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
            RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(location.latitude, location.longitude), 200, GeocodeSearch.AMAP);
            geocoderSearch.getFromLocationAsyn(query);

            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                //清除标记
                aMap.clear();

                cityCode = amapLocation.getCityCode();
                SharedPreferencesHelper.getInstance(this).putString("sLat", String.valueOf(navLocation.latitude));
                SharedPreferencesHelper.getInstance(this).putString("sLon", String.valueOf(navLocation.longitude));
                if (!mFirstFix) {
                    mFirstFix = true;
                    addCircle(location, amapLocation.getAccuracy());//添加定位精度圆
                    addMarker(location);//添加定位图标
                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                    //标记车站
                    LatLng[] latlngs = getLatLngs();
                    addPosMarkers(latlngs);
                } else {
                    mCircle.setCenter(location);
                    mCircle.setRadius(amapLocation.getAccuracy());
                    mLocMarker.setPosition(location);
                }
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
                Log.e("AmapErr", errText);
            }
        }
    }

    public LatLng[] getLatLngs() {
        LatLng[] latlngs = new LatLng[models.size()];
        int i = 0;
        for (CarPositionModel model : models) {
            latlngs[i++] = model.getLng();
        }
        return latlngs;
    }

    public void clearMarkers() {
        if (markers == null || markers.length == 0)
            return;
        for (Marker marker : markers) {
            marker.remove();
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

    private void addTipMarker(LatLng latlng) {
        if (tipMarker != null) {
            tipMarker.remove();
        }
        //Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
        //        R.mipmap.navi_map_gps_locked);
        //BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);

        MarkerOptions options = new MarkerOptions();
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        tipMarker = aMap.addMarker(options);
        tipMarker.setZIndex(5);
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
            if (aMap != null) {
                markers[i] = aMap.addMarker(options);
                markers[i++].setZIndex(10);
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (Objects.equals(marker.getId(), locationId)) return false;
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
//                    Toast.makeText(CarPositionActivity.this, "Denied", Toast.LENGTH_SHORT)
//                            .show();
                        Intent intent=new Intent(this,HomePageActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        mPositionCar.setText(regeocodeResult.getRegeocodeAddress().getFormatAddress());
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        if (1000 == i){
            tipModels.clear();
            for (Tip tip : list){
                if (tip.getPoiID() != null && tip.getPoint() != null)
                    tipModels.add(new TipModel(tip.getName(), tip.getAddress(), tip.getDistrict(), new LatLng(tip.getPoint().getLatitude(), tip.getPoint().getLongitude())));
            }
            mTipAdapter.update(tipModels);
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent=new Intent(this, HomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Activity","CarPosition");
            startActivity(intent);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
