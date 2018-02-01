package com.pzhuedu.along.baidu.loc;

import java.io.Serializable;

/**
 * Created by long on 2017/11/22.
 */

public class Loc implements Serializable {
    /**
     * {@link #getLoctionType()} 的值，GPS定位结果 <br>
     *
     * 通过设备GPS定位模块返回的定位结果，精度较高，在10米－100米左右
     */
    public static final int TYPE_GPS = 1;

    /**
     * {@link #getLoctionType()} 的值，缓存定位结果 <br>
     *
     * 返回一段时间前设备缓存下来的网络定位结果
     */
    public static final int TYPE_CACHE = 2;

    /**
     * {@link #getLoctionType()} 的值，Wifi定位结果 <br>
     *
     * 属于网络定位，定位精度相对基站定位会更好，定位精度较高，在5米－200米之间。
     */
    public static final int TYPE_WIFI = 3;

    /**
     * {@link #getLoctionType()} 的值，基站定位结果 <br>
     *
     * 属于网络定位，定位精度相对基站定位会更好，定位精度较高，在5米－200米之间。
     */
    public static final int TYPE_NET = 4;

    /**
     * {@link #getLoctionType()} 的值，其它定位结果
     */
    public static final int TYPE_OTHER = 5;



    // 错误码，0表示正常，其它数字表示各种错误
    private int mErrorCode = 0;
    // 错误描述
    private String mErrorDescription = "";
    // 实际最终定位的类型
    private int loctionType = TYPE_OTHER;
    // 定位时间，ms
    private long mTime;
    // 地址信息，定位成功时不可以为null
    private Addr mAddr;
    // 高度
    private double mAltitude;
    // 坐标类型
    private CoordType mCoordType = CoordType.bd09ll;
    // 位置语义化信息
    private String mLocationDescription = "";
    // 速度，仅gps定位结果时有速度信息，单位公里/小时
    private float mSpeed;
    // 纬度坐标
    private double mLatitude;
    // 经度坐标
    private double mLongitude;

    public Loc() { }

    // 生成id
    // 简单的取md5


    /**
     * 错误码，0为成功，其它为各种错误 <br/>
     *
     * <p>
     *      getErrorCode() == 0 可以用来判断是否定位成功
     * </p>
     *
     * @return 错误码
     */
    public int getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(int code) {
        mErrorCode = code;
    }

    public String getErrorDescription() {
        return mErrorDescription;
    }

    public void setErrorDescription(String error) {
        this.mErrorDescription = error;
    }

    public int getLoctionType() {
        return loctionType;
    }

    public void setLoctionType(int loctionType) {
        this.loctionType = loctionType;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long mTime) {
        this.mTime = mTime;
    }

    public Addr getAddr() {
        return mAddr;
    }

    public void setAddr(Addr mAddr) {
        this.mAddr = mAddr;
    }

    public double getAltitude() {
        return mAltitude;
    }

    public void setAltitude(double mAltitude) {
        this.mAltitude = mAltitude;
    }

    public CoordType getCoordType() {
        return mCoordType;
    }

    public void setCoordType(CoordType mCoordType) {
        this.mCoordType = mCoordType;
    }

    public String getLocationDescription() {
        return mLocationDescription;
    }

    public void setLocationDescription(String mLocationDescription) {
        this.mLocationDescription = mLocationDescription;
    }

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float mSpeed) {
        this.mSpeed = mSpeed;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }
}
