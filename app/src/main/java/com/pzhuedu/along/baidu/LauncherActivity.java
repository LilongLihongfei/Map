package com.pzhuedu.along.baidu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.utils.DistanceUtil;
import com.pzhuedu.along.baidu.loc.BaiduLocationClient;
import com.pzhuedu.along.baidu.loc.SPCache;
import com.pzhuedu.along.baidu.receiver.SDKReceiver;
import com.pzhuedu.along.baidu.util.Constants;
import com.pzhuedu.along.baidu.util.DistanceHelper;
import com.pzhuedu.along.baidu.util.SPUtils;
import com.pzhuedu.along.baidu.util.ToastUtil;
import com.pzhuedu.along.baidu.view.Switch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.baidu.mapapi.map.BaiduMap.MAP_TYPE_NORMAL;
import static com.baidu.mapapi.map.BaiduMap.MAP_TYPE_SATELLITE;
import static com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.COMPASS;
import static com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.FOLLOWING;
import static com.pzhuedu.along.baidu.loc.Loc.TYPE_OTHER;
import static com.pzhuedu.along.baidu.loc.Loc.TYPE_WIFI;

public class LauncherActivity extends Activity implements SensorEventListener, View.OnClickListener, OnGetGeoCoderResultListener {
    private String TAG = this.getClass().getSimpleName();
    // 定位相关
    LocationClient mLocClient;
    SDKReceiver mReceiver;
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    BaiduLocationClient mClient;
    private int SEARCH_ACTIVITY = 101;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private String mCurrentCity = "攀枝花市";
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    /*private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;*/
    private LatLng mCurrentLatLng = new LatLng(0,0);
    private float mCurrentAccracy;
    private LinearLayout mLl_route_condition;
    private LinearLayout mLl_converage;
    private LinearLayout mLl_route;
    private LinearLayout mLl_imform;
    private DrawerLayout mdrawerLayout;
    private boolean isAnimFinished = true;
    private boolean isCanVisiable = true;
    private ImageView iv_locatin;
    private ImageView iv_rgb;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private ImageView iv_route;
    private TextView tv_route;
    private TextView tv_search;
    private TextView tv_address;
    private TextView tv_location1;
    private LinearLayout ll_location2;
    private TextView tv_distance;
    private TextView tv_street;
    private LinearLayout mLl_search;
    //三种地图类型
    private LinearLayout mLl_satellite_map;
    private LinearLayout mLl_2D_map;
    private LinearLayout mLl_3D_map;
    //三个Switch开关
    private Switch mSwitch_collect;
    private Switch mSwitch_route_condition;
    private Switch mSwitch_hot_pic;
    // UI相关
    private LinearLayout requestLocButton;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private MapType mCurrentMapType = MapType.map_2D;
    private String disA2B;
    private String mPoiClickName;
    private String mCurrentAddr;
    // "custom_config_dark.json"
    private static String PATH = "custom_config_black.json";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_launcher);
        setMapCustomFile(this, PATH);
        initUI();
        MapView.setMapCustomEnable(true);
        registerListener();
        initBindEvent();
    }

    private void registerListener() {
        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver(this);
        registerReceiver(mReceiver, iFilter);
    }


    private void initUI() {
        requestLocButton = (LinearLayout) findViewById(R.id.ll_location);//获取UI控件
        mMapView = findViewById(R.id.bmapView);        // 地图初始化
        mLl_converage = findViewById(R.id.ll_converage);
        mLl_route = findViewById(R.id.ll_route);
        mLl_route_condition = findViewById(R.id.ll_route_conditon);
        mLl_search = findViewById(R.id.ll_search);
        mLl_imform = findViewById(R.id.ll_inform);
        mdrawerLayout = findViewById(R.id.dl_drawer);
        iv_locatin = findViewById(R.id.iv_location);
        iv_rgb = findViewById(R.id.iv_rgb);
        tv_street = findViewById(R.id.tv_street);
        tv_search = findViewById(R.id.tv_search);
        tv_distance = findViewById(R.id.tv_distance);
        tv_location1 = findViewById(R.id.tv_location1);
        tv_address = findViewById(R.id.tv_address);
        tv_route = findViewById(R.id.tv_route);
        iv_route = findViewById(R.id.iv_route);
        ll_location2 = findViewById(R.id.ll_location2);
        mLl_satellite_map = findViewById(R.id.ll_satellite_map);
        mLl_2D_map = findViewById(R.id.ll_2D_map);
        mLl_3D_map = findViewById(R.id.ll_3D_map);
        mSwitch_collect = findViewById(R.id.switch_collect);
        mSwitch_hot_pic = findViewById(R.id.switch_hot_pic);
        mSwitch_route_condition = findViewById(R.id.switch_route_condition);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;//初始化设置为跟随模式
        mBaiduMap = mMapView.getMap();//创建BaiduMap对象
        mBaiduMap.setMyLocationEnabled(true);//设置可以定位
        //设置侧边栏弹出之后也无法使用滑动来关闭，只能通过点击空白区域来关闭侧边栏
        mdrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //这里设置clickable(true)  必须动态设置  静态设置没有效果
        //解决问题   侧滑菜单出来的时候 点击菜单上的区域会有点击穿透问题
        mdrawerLayout.setClickable(true);
        mdrawerLayout.setScrollbarFadingEnabled(false);
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        setMapType(MapType.map_2D);
    }

    private void initBindEvent() {
        // 定位初始化
        requestLoc();

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //设置缩放控件的位置，在 onMapLoadFinish 后生效
                mMapView.setZoomControlsPosition(new Point(40, 800));
                //设置比例尺控件的位置，在 onMapLoadFinish 后生效
                mMapView.setScaleControlPosition(new Point(48, 1125));
            }
        });

        //定位按钮点击事件
        requestLocButton.setOnClickListener(this);
        //路况点击事件
        mLl_route_condition.setOnClickListener(this);
        //图层点击事件
        mLl_converage.setOnClickListener(this);
        //设置搜索点击事件
        mLl_search.setOnClickListener(this);
        //设置路线点击事件
        mLl_route.setOnClickListener(this);
        //设置三种地图点击事件
        mLl_3D_map.setOnClickListener(this);
        mLl_2D_map.setOnClickListener(this);
        mLl_satellite_map.setOnClickListener(this);
        //设置Switch点击开关监听事件
        mSwitch_route_condition.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch s, boolean isChecked) {
                //点击路况Switcher 开关
//                mBaiduMap.setTrafficEnabled(isChecked);
            }
        });
        mSwitch_hot_pic.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch s, boolean isChecked) {
                if(mBaiduMap.isSupportBaiduHeatMap()){
                    mdrawerLayout.closeDrawer(Gravity.RIGHT);
                    mBaiduMap.setBaiduHeatMapEnabled(isChecked);
                }
                else{
                    ToastUtil.show(LauncherActivity.this,"当前地图缩放级别不支持热力图");
                    mSwitch_hot_pic.setChecked(false);
                }
            }
        });
        mSwitch_collect.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch s, boolean isChecked) {
            }
        });
        mdrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mdrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mdrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        /**地图触摸事件监听*/
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                iv_locatin.setBackground(getResources().getDrawable(R.drawable.location));
            }
        });
        /**地图点击事件监听*/
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (isAnimFinished) {
                    isAnimFinished = false;
                    iv_locatin.setBackground(getResources().getDrawable(R.drawable.location));
                    if (!isCanVisiable) {
                        //不可见 ——>  可见状态
                        setClickable(true);
                        animationIn();//执行进入动画
                        iv_route.setImageResource(R.drawable.route);
                        tv_address.setText(mCurrentAddr);
                        tv_route.setText("路线");
                        tv_location1.setVisibility(View.VISIBLE);
                        ll_location2.setVisibility(View.GONE);


                    } else {
                        setClickable(false);
                        //移除mapView上的覆盖物
                        mBaiduMap.clear();
                        Animation alpha_out = AnimationUtils.loadAnimation(LauncherActivity.this, R.anim.alpha_out);
                        Animation translation_up = AnimationUtils.loadAnimation(LauncherActivity.this, R.anim.translation_up);
                        Animation translation_down_inform = AnimationUtils.loadAnimation(LauncherActivity.this, R.anim.translation_down_inform);
                        mLl_converage.startAnimation(alpha_out);
                        mLl_route.startAnimation(translation_down_inform);
                        mLl_route_condition.startAnimation(alpha_out);
                        mLl_search.startAnimation(translation_up);
                        mLl_imform.startAnimation(translation_down_inform);
                        alpha_out.setFillAfter(true);
                        translation_down_inform.setFillAfter(true);
                        translation_up.setFillAfter(true);
                        translation_up.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                isAnimFinished = true;
                                isCanVisiable = false;
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                    }
                }

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                tv_location1.setVisibility(View.GONE);
                ll_location2.setVisibility(View.VISIBLE);
                double distance = DistanceUtil.getDistance(mCurrentLatLng, mapPoi.getPosition());
                mPoiClickName = mapPoi.getName().replace("\\", "");
                disA2B = DistanceHelper.double2String(distance);
                Log.d(TAG, "distance : " + disA2B + "   mPoiClickName : " + mPoiClickName);
                mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(mapPoi.getPosition()).newVersion(0));
                if(isAnimFinished && !isCanVisiable)//如果不可见执行进入动画
                    animationIn();
                return false;
            }
        });
    }

    private void animationIn() {
        Animation alpha_in = AnimationUtils.loadAnimation(LauncherActivity.this, R.anim.alpha_in);
        Animation translation_down = AnimationUtils.loadAnimation(LauncherActivity.this, R.anim.translation_down);
        Animation translation_up_inform = AnimationUtils.loadAnimation(LauncherActivity.this, R.anim.translation_up_inform);
        mLl_converage.startAnimation(alpha_in);
        mLl_route.startAnimation(translation_up_inform);
        mLl_route_condition.startAnimation(alpha_in);
        mLl_imform.startAnimation(translation_up_inform);
        mLl_search.startAnimation(translation_down);
        alpha_in.setFillAfter(true);
        translation_down.setFillAfter(true);
        translation_up_inform.setFillAfter(true);
        translation_down.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimFinished = true;
                isCanVisiable = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void requestLoc() {
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(2000);//定时定位 时间间隔
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        mLocClient.setLocOption(option);
        if(mLocClient.isStarted()){
            mLocClient.requestLocation();
        } else  mLocClient.start();
    }

    private void setClickable(boolean clickable) {
        mLl_imform.setClickable(clickable);
        mLl_route_condition.setClickable(clickable);
        mLl_search.setClickable(clickable);
        mLl_converage.setClickable(clickable);
        mLl_route.setClickable(clickable);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection)//GPS定位时的方向角度
                    .latitude(mCurrentLatLng.latitude)
                    .longitude(mCurrentLatLng.longitude).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    //地址转换成经纬度的时候 回调该方法
    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    //经纬度转换成地址时回调该方法
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            ToastUtil.show(this, "抱歉坐标转换成地址失败");
            return;
        }
        iv_route.setImageResource(R.drawable.people);
        tv_route.setText("到这去");
        tv_address.setText(mPoiClickName);
        tv_distance.setText(disA2B);
        mBaiduMap.clear();//添加覆盖物前 应该清楚地图上原有的覆盖物
        mBaiduMap.addOverlay(new MarkerOptions().position(reverseGeoCodeResult.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.red_location)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(reverseGeoCodeResult.getLocation()));
        tv_street.setText(reverseGeoCodeResult.getAddress());
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                Log.d(TAG, "onReceiveLocation: " + "location == null");
                return;
            }
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                if(mLl_imform.getVisibility() == View.INVISIBLE)
                    mLl_imform.setVisibility(View.VISIBLE);
                isFirstLoc = false;

                mCurrentLatLng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(mCurrentLatLng).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                String address = location.getAddrStr().toString();
                if (address.length() > 11) {
                    tv_address.setText("在" + address.substring(0, 11));
                } else {
                    tv_address.setText("在" + address);
                }
                mCurrentAddr = tv_address.getText().toString();
                mCurrentCity = location.getCity();
                if(!SPUtils.contain(LauncherActivity.this,Constants.CITYNAME))
                SPUtils.saveString(LauncherActivity.this, Constants.CITYNAME,mCurrentCity);
                int accuRange = 100;
                switch (location.getLocType()) {
                    case BDLocation.TypeGpsLocation:
                        accuRange = 40;
                        break;
                    case TYPE_WIFI:
                        accuRange = 60;
                        break;
                    case BDLocation.TypeNetWorkLocation:
                        switch(location.getNetworkLocationType()){
                            case "wf":
                                accuRange = 60;
                                break;
                            case "cl":
                                accuRange = 80;
                                break;
                            case "ll":
                                accuRange = 40;
                                break;
                            default:
                                accuRange = 100;
                                break;
                        }
                        accuRange = 80;
                        break;
                    case TYPE_OTHER:
                        accuRange = 100;
                        break;
                }
                /**在网络定位结果的情况下，获取网络定位结果是通过基站定位得到的还是通过wifi定位得到的还是GPS得结果
                 返回:
                 String : "wf"： wifi定位结果 “cl“； cell定位结果 “ll”：GPS定位结果 null 没有获取到定位结果采用的类型**/
                mBaiduMap.clear();
                iv_route.setImageResource(R.drawable.route);
                tv_route.setText("路线");
                tv_location1.setText("精确到" + accuRange + "米，海拔" + location.getAltitude() + "米");
                ll_location2.setVisibility(View.GONE);
                tv_location1.setVisibility(View.VISIBLE);
            }

        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_location:
                /*if(mCurrentMapType == MapType.map_atellite){
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(new LatLng(mCurrentLat,mCurrentLon));
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    return;
                }*/
                LatLng latLng = mMapView.getMap().getMapStatus().target;
                //当定位地点 不在屏幕中央时 先把定位地点移动至屏幕中央
                if (Math.abs(latLng.latitude - mCurrentLatLng.latitude) > 0.0001 || Math.abs(latLng.longitude - mCurrentLatLng.longitude) > 0.0001) {
                    isFirstLoc = true;
                    refreshMode(false);
                } else {
                    //如果定位地点 已经在屏幕中央了 点击时 切换定位模式 状态 在跟随态 和 罗盘态中更换
                    if(mCurrentMapType == MapType.map_2D)
                        setMapType(MapType.map_3D);
                    else if(mCurrentMapType == MapType.map_3D)
                        setMapType(MapType.map_2D);
                    refreshMode(true);
                }
                break;
            case R.id.ll_route_conditon:
                if (mLl_route_condition.isFocusable()) {
                    mLl_route_condition.setFocusable(false);
                    mBaiduMap.setTrafficEnabled(false);
                    iv_rgb.setImageResource(R.drawable.rgb_normal);
                } else {
                    mLl_route_condition.setFocusable(true);
                    mBaiduMap.setTrafficEnabled(true);
                    iv_rgb.setImageResource(R.drawable.rgb_selected);
                }
                break;
            case R.id.ll_converage:
                if (mdrawerLayout.isDrawerOpen(Gravity.RIGHT))
                    mdrawerLayout.closeDrawer(Gravity.RIGHT);
                else
                    mdrawerLayout.openDrawer(Gravity.RIGHT);
                break;
            case R.id.ll_search:
                Intent intent = new Intent(this,SearchActivity.class);
                intent.putExtra("city",mCurrentCity);
                if(!tv_search.getText().toString().equals("搜地点、查公交、找路线"))
                intent.putExtra("content",tv_search.getText().toString());
                startActivityForResult(intent,SEARCH_ACTIVITY);
                overridePendingTransition(R.anim.scale_in,R.anim.scale_out);
                break;
            case R.id.ll_route:
                Intent intent1 = new Intent(this, NavigateActivity.class);
                intent1.putExtra(Constants.LATLNG,mCurrentLatLng);
                startActivity(intent1);
                break;
            case R.id.ll_2D_map:
                mBaiduMap.setMapType(MAP_TYPE_NORMAL);
                setMapType(MapType.map_2D);
                mCurrentMode = FOLLOWING;
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.overlook(0);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                break;
            case R.id.ll_3D_map:
                mBaiduMap.setMapType(MAP_TYPE_NORMAL);
                setMapType(MapType.map_3D);
                mCurrentMode = COMPASS;
                mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));
                break;
            case R.id.ll_satellite_map:
                setMapType(MapType.map_atellite);
                mBaiduMap.setMapType(MAP_TYPE_SATELLITE);
                break;
        }
    }

    private void refreshMode(boolean change) {
        switch (mCurrentMode) {
            case COMPASS:
                //跟随态，保持定位图标在地图中心 LocationMode.FOLLOWING
                if (change) {
                    mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                    iv_locatin.setBackground(getResources().getDrawable(R.drawable.location_1));
                } else {
                    mCurrentMode = COMPASS;
                    iv_locatin.setBackground(getResources().getDrawable(R.drawable.location_2));
                }

                mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));
                MapStatus.Builder builder = new MapStatus.Builder();
                if(change)
                builder.overlook(0);
                builder.zoom(18);
                builder.target(mCurrentLatLng);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                break;
            case FOLLOWING:
                //罗盘态，显示定位方向圈，保持定位图标在地图中心 LocationMode.COMPASS
                if (change) {
                    mCurrentMode = COMPASS;
                    iv_locatin.setBackground(getResources().getDrawable(R.drawable.location_2));
                } else {
                    mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                    iv_locatin.setBackground(getResources().getDrawable(R.drawable.location_1));
                }
                mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));
                break;
            default:
                break;
        }
    }

    enum MapType{
        map_atellite(1),map_2D(2),map_3D(3);
        private int type;
        MapType(int i){
            type = i;
        }

    }
    private void setMapType(MapType type){
        mCurrentMapType = type;
        mLl_satellite_map.setEnabled(type.type == 1?false:true);
        mLl_2D_map.setEnabled(type.type == 2?false:true);
        mLl_3D_map.setEnabled(type.type == 3?false:true);
        mdrawerLayout.closeDrawer(Gravity.RIGHT);
    }

    // 设置个性化地图config文件路径
    private void setMapCustomFile(Context context, String PATH) {
        FileOutputStream out = null;
        InputStream inputStream = null;
        String moduleName = null;
        try {
            inputStream = context.getAssets()
                    .open("customConfigdir/" + PATH);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            moduleName = context.getFilesDir().getAbsolutePath();
            File f = new File(moduleName + "/" + PATH);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            out = new FileOutputStream(f);
            out.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MapView.setCustomMapStylePath(moduleName + "/" + PATH);

    }
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if(requestCode == SEARCH_ACTIVITY && data!=null){
             if(TextUtils.isEmpty(data.getStringExtra("content")))
                 tv_search.setText("搜地点、查公交、找路线");
            else tv_search.setText(data.getStringExtra("content"));
         }

    }
}
