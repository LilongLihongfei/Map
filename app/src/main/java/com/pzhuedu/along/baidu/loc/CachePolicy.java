package com.pzhuedu.along.baidu.loc;

import android.content.Context;

/**
 * Created by along on 2017/11/23.<br/>
 *
 * 缓存策略，在定位的时候如何使用上次的定位信息进行缓存
 */

public abstract class CachePolicy {
    /**
     * 是否应该使用缓存
     * @return true 表示应该使用缓存
     */
    public abstract boolean useCache();
    public abstract void doCache(Context context,Loc loc);
}
