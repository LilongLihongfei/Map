package com.pzhuedu.along.baidu.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.pzhuedu.along.baidu.R;
import com.pzhuedu.along.baidu.WalkGuideActivity;
import com.pzhuedu.along.baidu.overlay.MyDrivingRouteOverlay;
import com.pzhuedu.along.baidu.overlay.MyWalkingRouteOverlay;
import com.pzhuedu.along.baidu.util.DistanceHelper;
import com.pzhuedu.along.baidu.util.ToastUtil;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by along on 2017/12/25.
 */

public class WalkFragment extends Fragment {
    @BindView(R.id.map_fragment)
    TextureMapView mapFragment;
    @BindView(R.id.bt_start_navi)
    LinearLayout tStartNavi;
    @BindView(R.id.tv_transit_top)
    TextView tvTransitTop;
    @BindView(R.id.tv_transit_descript)
    TextView tvTransitDescript;
    private OnLoadingFinishListener listener;
    private WalkingRouteResult result;
    private BaiduMap mBaidumap;
    private LatLng l1;
    private LatLng l2;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_walk_route, null);
        ButterKnife.bind(this, rootView);
        mBaidumap = mapFragment.getMap();
        result = (WalkingRouteResult) getArguments().get("result");
        l1 = (LatLng) getArguments().get("l1");
        l2 = (LatLng) getArguments().get("l2");
        WalkingRouteLine route = result.getRouteLines().get(0);
        MyWalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mapFragment.getMap(), l1, l2);
        tvTransitTop.setText(DistanceHelper.timeFormatter(route.getDuration()/60)+"  "
                +DistanceHelper.distanceFormatter(route.getDistance()));
        DecimalFormat df = new DecimalFormat("0.0");
        tvTransitDescript.setText("消费"+(int)(0.064*route.getDistance())+"大卡  节约碳排放"+df.format(0.00022*route.getDistance())+"kg");
        mBaidumap.setOnMarkerClickListener(overlay);
        listener.onFinish();
        overlay.setData(route);
        overlay.addToMap();
        overlay.zoomToSpan();
        return rootView;
    }

    public interface OnLoadingFinishListener{
        void onFinish();
    }

    public void setListener(OnLoadingFinishListener listener){
        this.listener = listener;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapFragment.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapFragment.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapFragment.onDestroy();
    }

    @OnClick(R.id.bt_start_navi)
    public void onViewClicked() {
        //点击开始导航按钮的
        Intent intent = new Intent(getActivity(),WalkGuideActivity.class);
        intent.putExtra("stlatlng",l1);
        intent.putExtra("enlatlng",l2);
        startActivity(intent);
    }
}
