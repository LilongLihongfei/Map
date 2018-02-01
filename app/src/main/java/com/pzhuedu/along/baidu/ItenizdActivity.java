package com.pzhuedu.along.baidu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.pzhuedu.along.baidu.util.Constants;
import com.pzhuedu.along.baidu.util.ToastUtil;

public class ItenizdActivity extends Activity {
    private MapView mapView = null;
    private BaiduMap map;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private double latitude;  
    private double longitude;  
    private GeoCoder mSearch;
    private LatLng mCurrentLatLng;
    private View rootView;
    private TextView tv_add;
    private TextView tv_name;
    private Button confirm;
    private String mCurrentAdress;
    private MotionEvent mEvent;
    @Override  
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub  
        super.onCreate(savedInstanceState);  
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_itenied);
        mapView = (MapView) findViewById(R.id.map);
        initView();
        getLocation();  
        getGeo();
    }

    private void initView() {
        rootView = LayoutInflater.from(ItenizdActivity.this).inflate(R.layout.layout_overlay_selectloc,null);
        tv_add = rootView.findViewById(R.id.tv_overlay_address);
        tv_name = rootView.findViewById(R.id.tv_overlay_name);
        confirm = rootView.findViewById(R.id.btn_overlay_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItenizdActivity.this.finish();
                Intent intent = new Intent();
                intent.putExtra(Constants.LATLNG,mCurrentLatLng);
                intent.putExtra(Constants.INTENT_NAME,mCurrentAdress);
                setResult(0,intent);
            }
        });
    }

    @SuppressLint("NewApi")
    private void getMap(){  
          
        //获取屏幕高度宽度  
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics dm = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(dm);  
                int width = dm.widthPixels;  
                int height = dm.heightPixels;  
        //去掉logo  
        mapView.removeViewAt(1);  
        map = mapView.getMap();  
        // 设置地图模式为交通地图  
        map.setTrafficEnabled(true);  
        // 设置启用内置的缩放控件  
        mapView.showZoomControls(true);  
        //获得百度地图状态  
        MapStatus.Builder builder = new MapStatus.Builder();
//      builder.targetScreen(new Point(width/2,height/2));  
        //定位的坐标点  
        LatLng latLng = new LatLng(latitude, longitude);
//      BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);  
//      OverlayOptions overlay = new MarkerOptions().position(latLng).icon(bitmap);  
//      map.addOverlay(overlay);  
        //设置地图中心为定位点  
        builder.target(latLng);  
        //设置缩放级别 18对应比例尺50米  
        builder.zoom(18);  
        MapStatus mapStatus = builder.build();  
        MapStatusUpdate m = MapStatusUpdateFactory.newMapStatus(mapStatus);
        map.setMapStatus(m);  
        map.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override  
            public void onMapStatusChangeStart(MapStatus arg0) {  
                // TODO Auto-generated method stub
            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
                if(map!=null)
                    map.clear();
            }

            @Override  
            public void onMapStatusChangeFinish(MapStatus arg0) {  
                // TODO Auto-generated method stub  
                mCurrentLatLng= map.getMapStatus().target;
                System.out.println("当前的经纬度： "+mCurrentLatLng.toString());
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(mCurrentLatLng));
            }
              
            @Override  
            public void onMapStatusChange(MapStatus arg0) {  
                // TODO Auto-generated method stub
            }  
        });  
    }  
    private void getLocation(){  
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类  
          
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02  
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms  
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息  
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向  
        mLocationClient.setLocOption(option);  
        mLocationClient.registerLocationListener( myListener );    //注册监听函数  
          
        mLocationClient.start();  
        mLocationClient.requestLocation();  
    }  
    private void getGeo(){  
        mSearch = GeoCoder.newInstance();  
  
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                }
                //获取地理编码结果
            }
           
            @Override    
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {    
                    //没有找到检索结果
                }    
                //获取反向地理编码结果
                mCurrentAdress = result.getAddress();
                tv_add.setText(mCurrentAdress);
                map.clear();
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(rootView);
                OverlayOptions overlay = new MarkerOptions().position(mCurrentLatLng).icon(bitmap).anchor(0.22f,0.81f);
                map.addOverlay(overlay);
            }
        };
        mSearch.setOnGetGeoCodeResultListener(listener);  
    }  
    @Override  
    protected void onPause() {  
        // TODO Auto-generated method stub  
        super.onPause();  
        mapView.onPause();  
    }  
    @Override  
    protected void onResume() {  
        // TODO Auto-generated method stub  
        super.onResume();  
        mapView.onResume();  
    }  
    @Override  
    protected void onDestroy() {  
        // TODO Auto-generated method stub  
        super.onDestroy();  
        mapView.onDestroy();  
        mLocationClient.stop();  
        mSearch.destroy();  
    }  
    class MyLocationListener implements BDLocationListener {  
        @Override  
        public void onReceiveLocation(BDLocation location) {
              
            if (location == null)  
                    return ;  
            latitude = location.getLatitude();  
            longitude = location.getLongitude();  
            getMap();
            map.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.LATLNG,mCurrentLatLng);
                    Log.d("lilong------>","mCurrentLatLng: "+mCurrentLatLng.toString()+"  mCurrentAdress: "+mCurrentAdress);
                    intent.putExtra(Constants.INTENT_NAME,mCurrentAdress);
                    ItenizdActivity.this.setResult(1,intent);
                    ItenizdActivity.this.finish();
                    return true;
                }
            });
            StringBuffer sb = new StringBuffer(256);  
            sb.append("time : ");  
            sb.append(location.getTime());  
            sb.append("\nerror code : ");  
            sb.append(location.getLocType());  
            sb.append("\nlatitude : ");  
            sb.append(location.getLatitude());  
            sb.append("\nlontitude : ");  
            sb.append(location.getLongitude());  
            sb.append("\nradius : ");  
            sb.append(location.getRadius());  
            if (location.getLocType() == BDLocation.TypeGpsLocation){  
                sb.append("\nspeed : ");  
                sb.append(location.getSpeed());  
                sb.append("\nsatellite : ");  
                sb.append(location.getSatelliteNumber());  
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){  
                sb.append("\naddr : ");  
                sb.append(location.getAddrStr());  
            }
            System.out.println(sb.toString());  
                        mLocationClient.stop();  
    }  
    }
}