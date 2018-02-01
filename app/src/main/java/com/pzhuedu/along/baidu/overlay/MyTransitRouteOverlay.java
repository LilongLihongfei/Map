package com.pzhuedu.along.baidu.overlay;

import android.graphics.Color;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.pzhuedu.along.baidu.R;
import com.pzhuedu.along.baidu.util.DistanceHelper;

public class MyTransitRouteOverlay extends DrivingRouteOverlay {

        private LatLng l1,l2;

        public MyTransitRouteOverlay(BaiduMap baiduMap, LatLng l1,LatLng l2) {
                super(baiduMap);
                this.l1 = l1;
                this.l2 = l2;
        }

        @Override
        public BitmapDescriptor getStartMarker() {
                return BitmapDescriptorFactory.fromResource(R.color.transparent);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
                return BitmapDescriptorFactory.fromResource(R.color.transparent);
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

        @Override
        public boolean onPolylineClick(Polyline polyline) {
                for (Overlay mPolyline : mOverlayList) {
                        if(mPolyline instanceof Polyline){
                                //改变路线颜色为未选中状态
                                ((Polyline) mPolyline).setColor(Color.argb(178, 151, 133, 54));
                                //改变路线未被选中
                                ((Polyline) mPolyline).setFocus(false);
                                //设置路线的层级 注意要比选中路线的层级低
                                ((Polyline) mPolyline).setZIndex(10);
                                if(mPolyline.equals(polyline)){
                                        ((Polyline) mPolyline).setFocus(true);
                                        //设置选中的路线颜色为高亮状态
                                        polyline.setColor(Color.argb(178, 115, 74, 5));
                                        ///设置选中的路线的层级 一定要比上面的高
                                        polyline.setZIndex(15);
                                }
                        }

                }
                return true;
        }

        @Override
        public int getLineColor() {
                return Color.argb(178, 151, 133, 54);
        }
}