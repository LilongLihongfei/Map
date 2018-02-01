package com.pzhuedu.along.baidu.util;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by along on 2017/12/8.
 */

public class DistanceHelper {
    public static String TAG = "lilong----->";
    public static Map zoomrank ;

    //距离格式化
    public static String double2String(double distance) {
        String s_dis = String.valueOf(distance).split("\\.")[0];
        if (s_dis.length() >=4) {
            double d_dis = Double.parseDouble(s_dis) / 1000;
            String s = String.valueOf(d_dis);
            int index = s.lastIndexOf(".");
            if(index > 3)
                return  s.substring(0,index)+"km";
            return  s.substring(0,index+2)+"km";
        }
        if (s_dis.length() > 1 && s_dis.length() <4) {
            return s_dis + "m";
        }
        return null;
    }

    //距离格式化
    public static String distanceFormatter(int distance) {
        if (distance < 1000) {
            return distance + "米";
        } else if (distance % 1000 == 0) {
            return distance / 1000 + "公里";
        } else {
            DecimalFormat df = new DecimalFormat("0.0");
            int a1 = distance / 1000; // 十位

            double a2 = distance % 1000;
            double a3 = a2 / 1000; // 得到个位

            String result = df.format(a3);
            double total = Double.parseDouble(result) + a1;
            return total + "公里";
        }
    }

    //根据了两点之间的距离 返回百度地图缩放级别
    public static int getDistance2zoom(LatLng l1,LatLng l2){
        if(zoomrank == null){
            zoomrank = new TreeMap();
            zoomrank.put(20,19);//比例尺  缩放级别
            zoomrank.put(50,18);
            zoomrank.put(100,17);
            zoomrank.put(200,16);
            zoomrank.put(500,15);
            zoomrank.put(1000,14);
            zoomrank.put(2000,13);
            zoomrank.put(5000,12);
            zoomrank.put(10000,11);
            zoomrank.put(20000,10);
            zoomrank.put(25000,9);
            zoomrank.put(50000,8);
            zoomrank.put(100000,7);
            zoomrank.put(200000,6);
            zoomrank.put(500000,5);
            zoomrank.put(1000000,4);
        }
        double dis = DistanceUtil.getDistance(l1,l2);
        Log.d(TAG, "getDistance2zoom: "+dis);
        Iterator iterator = zoomrank.keySet().iterator();
        while (iterator.hasNext()){
            int key = (int)iterator.next();
            if(key*5.5>dis){
                return (int)zoomrank.get(key);
            }
        }
        return  -1;
    }
/**
 *  步行1分钟 0.06公里
    驾车1分钟 0.55公里
    骑行1分钟 0.9公里
    公交1分钟 0.15公里

    路线规划时  运行时间 根据 距离/速度 = 时间 获得
* */
    // 时间转换
    public static String timeFormatter(int minute) {
        if (minute < 60) {
            return minute + "分钟";
        } else if (minute % 60 == 0) {
            return minute / 60 + "小时";
        } else {
            int hour = minute / 60;
            int minute1 = minute % 60;
            return hour + "小时" + minute1 + "分钟";
        }

    }
}
