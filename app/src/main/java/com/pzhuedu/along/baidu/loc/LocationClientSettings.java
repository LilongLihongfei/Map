package com.pzhuedu.along.baidu.loc;

import java.util.HashMap;

/**
 * Created by along on 2017/11/22.
 */

public class LocationClientSettings {
    private static final LocationMode DEFAULT_LOCATION_MODE =
            LocationMode.Hight_Accuracy;
    private static final CoordType DEFAULT_COOR_TYPE = CoordType.bd09ll;
    private static final boolean DEFAULT_MOCK_ENABLE = true;

    private LocationMode mLocationMode; // 定位模式
    private CoordType mCoordType; // 坐标系类型
    private boolean mockEnable; // 是否允许模拟位置
    // 一些扩展属性，以key - value形式设置，以兼容不同的sdk
    private HashMap<String, String> otherParams;
    private CachePolicy mCachePolicy; // 缓存

    public LocationClientSettings() {
        reset();
    }

    public void reset() {
        mLocationMode = DEFAULT_LOCATION_MODE;
        mCoordType = DEFAULT_COOR_TYPE;
        mockEnable = DEFAULT_MOCK_ENABLE;
        if (otherParams == null) {
            otherParams = new HashMap<>();
        } else {
            otherParams.clear();
        }
    }

    public void setExtra(String key, String value) {
        if (otherParams == null) {
            otherParams = new HashMap<>();
        }
        otherParams.put(key, value);
    }

    // 不会为空
    public HashMap<String, String> getExtra() {
        if (otherParams == null) {
            otherParams = new HashMap<>();
        }
        return otherParams;
    }

    public CachePolicy getCachePolicy() {
        return mCachePolicy;
    }

    public void setCachePolicy(CachePolicy cachePolicy) {
        this.mCachePolicy = cachePolicy;
    }

    public LocationMode getLocationMode() {
        return mLocationMode;
    }

    public void setLocationMode(LocationMode mLocationMode) {
        this.mLocationMode = mLocationMode;
    }

    public CoordType getCoordType() {
        return mCoordType;
    }

    public void setCoordType(CoordType mCoordType) {
        this.mCoordType = mCoordType;
    }

    public boolean isMockEnable() {
        return mockEnable;
    }

    public void setMockEnable(boolean mockEnable) {
        this.mockEnable = mockEnable;
    }
}
