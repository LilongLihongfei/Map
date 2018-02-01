package com.pzhuedu.along.baidu.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.pzhuedu.along.baidu.CarGuideActivity;
import com.pzhuedu.along.baidu.BNEventHandler;
import com.pzhuedu.along.baidu.R;
import com.pzhuedu.along.baidu.overlay.MyDrivingRouteOverlay;
import com.pzhuedu.along.baidu.util.Constants;
import com.pzhuedu.along.baidu.util.DistanceHelper;
import com.pzhuedu.along.baidu.view.LLRoute;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by along on 2017/12/25.
 */

public class CarFragment extends Fragment implements LLRoute.OnLLRouteClickListener {
    private String TAG = this.getClass().getSimpleName();
    @BindView(R.id.map_fragment)
    TextureMapView mapFragment;
    @BindView(R.id.llRoute_1)
    LLRoute llRoute1;
    @BindView(R.id.llRoute_2)
    LLRoute llRoute2;
    @BindView(R.id.llRoute_3)
    LLRoute llRoute3;
    @BindView(R.id.tv_descript)
    TextView tvDescript;
    @BindView(R.id.bt_start_navi)
    LinearLayout tStartNavi;
    private List<DrivingRouteLine> Lines;
    private MyDrivingRouteOverlay carOverlay;
    private OnLoadingFinishListener listener;
    private List<Polyline> polylineList;
    private DrivingRouteResult carResult;
    private BaiduMap mBaidumap;
    private LatLng l1;
    private LatLng l2;
    private String mStName;
    private String mEnName;
    private BNRoutePlanNode sNode = null;
    private BNRoutePlanNode eNode = null;
    private BaiduNaviManager.NavEventListener eventListerner = new BaiduNaviManager.NavEventListener() {

        @Override
        public void onCommonEventCall(int what, int arg1, int arg2, Bundle bundle) {
            BNEventHandler.getInstance().handleNaviEvent(what, arg1, arg2, bundle);
        }
    };



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base_route, null);
        ButterKnife.bind(this, rootView);
        mBaidumap = mapFragment.getMap();
        carResult = (DrivingRouteResult) getArguments().get("result");
        l1 = (LatLng) getArguments().get("l1");
        l2 = (LatLng) getArguments().get("l2");
        mStName = (String)getArguments().get("stname");
        mEnName = (String)getArguments().get("enname");
        Log.d(TAG, "onCreateView: mStName:  "+mStName+"  mEnName:  "+mEnName);


        if (carResult != null && carResult.getRouteLines().size() > 0) {
            Lines = carResult.getRouteLines();
            List llroutes = LLRoute.getRouteArrayList();
            polylineList = new ArrayList<>();
            for (int i = 0; i < Lines.size(); i++) {
                if (i > 2) break;
                DrivingRouteLine line = Lines.get(i);
                LLRoute llroute = (LLRoute) llroutes.get(i);
                llroute.setVisibility(View.VISIBLE);
                llroute.setmRouteTime(DistanceHelper.timeFormatter(line.getDuration() / 60));
                llroute.setmRouteDis(DistanceHelper.distanceFormatter(line.getDistance()));
                if (i == 1) {
                    StringBuffer stringBuffer = new StringBuffer("推荐方案  ");
                    stringBuffer.append("  红绿灯" + line.getLightNum());
                    stringBuffer.append("  拥堵距离 " + line.getCongestionDistance());
                    tvDescript.setText(stringBuffer.toString());
                }
                carOverlay = new MyDrivingRouteOverlay(mapFragment.getMap(), l1, l2);
                //设置点击路标监听器
                mBaidumap.setOnMarkerClickListener(carOverlay);
                carOverlay.setData(line);
                if (getTag().equals("mass"))
                    carOverlay.setSameCity(false);
                carOverlay.addToMap();
                carOverlay.zoomToSpan();
            }
            for (Overlay overlay : carOverlay.myOverlayList) {
                if (overlay instanceof Polyline) {
                    polylineList.add((Polyline) overlay);
                }
            }
            carOverlay.onPolylineClick(polylineList.get(0));
        }
        llRoute1.setOLLRouteClickListener(this);
        llRoute2.setOLLRouteClickListener(this);
        llRoute3.setOLLRouteClickListener(this);
        listener.onFinish();
        return rootView;
    }


    @Override
    public void onClick(LLRoute llRoute) {
        switch ((int) llRoute.getTag()) {
            case 0:
                Log.d("lilong------->", "onClick: 点击了第一个按钮");
                carOverlay.onPolylineClick(polylineList.get(0));
                StringBuffer stringBuffer = new StringBuffer("推荐方案  ");
                stringBuffer.append("  红绿灯" + Lines.get(0).getLightNum());
                stringBuffer.append("  拥堵距离 " + Lines.get(0).getCongestionDistance());
                tvDescript.setText(stringBuffer.toString());
                break;
            case 1:
                Log.d("lilong------->", "onClick: 点击了第二个按钮");
                carOverlay.onPolylineClick(polylineList.get(1));
                StringBuffer stringBuffer1 = new StringBuffer("方案一  ");
                stringBuffer1.append("  红绿灯" + Lines.get(1).getLightNum());
                stringBuffer1.append("  拥堵距离 " + Lines.get(1).getCongestionDistance());
                tvDescript.setText(stringBuffer1.toString());
                break;
            case 2:
                Log.d("lilong------->", "onClick: 点击了第3个按钮");
                carOverlay.onPolylineClick(polylineList.get(2));
                StringBuffer stringBuffer2 = new StringBuffer("方案二  ");
                stringBuffer2.append("  红绿灯" + Lines.get(2).getLightNum());
                stringBuffer2.append("  拥堵距离 " + Lines.get(2).getCongestionDistance());
                tvDescript.setText(stringBuffer2.toString());
                break;
            default:
                break;
        }
    }

    public interface OnLoadingFinishListener {
        void onFinish();
    }

    public void setListener(OnLoadingFinishListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R.id.bt_start_navi)
    public void onViewClicked() {
        if (BaiduNaviManager.isNaviInited()) {
            routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL);
        }
    }

    private void routeplanToNavi(BNRoutePlanNode.CoordinateType coType) {
        switch (coType) {
            case BD09LL: {
                sNode = new BNRoutePlanNode(l1.longitude, l1.latitude, mStName, null, coType);
                eNode = new BNRoutePlanNode(l2.longitude, l2.latitude, mEnName, null, coType);
                break;
            }
        }
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);

            // 开发者可以使用旧的算路接口，也可以使用新的算路接口,可以接收诱导信息等
            // BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
            BaiduNaviManager.getInstance().launchNavigator(getActivity(), list, 1, true,
                    new BaiduNaviManager.RoutePlanListener() {
                        @Override
                        public void onJumpToNavigator() {
                            Intent intent = new Intent(getActivity(), CarGuideActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constants.ROUTE_PLAN_NODE, (BNRoutePlanNode) sNode);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }

                        @Override
                        public void onRoutePlanFailed() {
                            Log.d("lilong------->", "onRoutePlanFailed: ");
                        }
                    }
                    , eventListerner);
        }
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
        LLRoute.removeAll();
        if (carOverlay != null)
            carOverlay.removeAll();
        System.gc();
        mapFragment.onDestroy();
    }
}
