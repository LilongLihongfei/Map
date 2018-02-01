package com.pzhuedu.along.baidu.overlay;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.BikingRouteOverlay;
import com.pzhuedu.along.baidu.R;
import com.pzhuedu.along.baidu.util.DistanceHelper;

public class MyBikingRouteOverlay extends BikingRouteOverlay {
        private LatLng l1,l2;

        public MyBikingRouteOverlay(BaiduMap baiduMap, LatLng l1,LatLng l2) {
                super(baiduMap);
                this.l1 = l1;
                this.l2 = l2;
        }

        @Override
        public BitmapDescriptor getStartMarker() {
                return BitmapDescriptorFactory.fromResource(R.drawable.blue_st);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
        }

        @Override
        public void zoomToSpan() {
                super.zoomToSpan();
                MapStatus.Builder builder1 = new MapStatus.Builder();
                int zoom = DistanceHelper.getDistance2zoom(l1,l2);
                if(zoom>0){
                        builder1.zoom(zoom);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
                }
        }

}