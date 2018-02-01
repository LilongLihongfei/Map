package com.pzhuedu.along.baidu.loc;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by along on 2017/11/22. <br/>
 *
 * 定位的核心类，客户端，如果需要定位，需要按照以下简单代码步骤
 * <P>
 *     code:
 * </P>
 * <code>
 *     LocationClient client = new LocationClient(context);
 *     client.addLocationListener(this);
 *     if (!client.isReady()) {
 *         client.prepare(new PrepareListener() {
 *             public void onClientPrepared(boolean success, String msg) {
 *                 if (success) {
 *                     client.requestLocation();
 *                 }
 *             }
 *         });
 *     }
 * </code>
 *
 */

public abstract class LocationClient {
    private final String LOG_TAG = getClass().getSimpleName();
    // 定位回调接口（多个）
    protected List<LocationListener> listeners;
    // 定位参数选项，必须要有一个，不可以为空
    protected LocationClientSettings settings;
    // 是否正在定位
    protected boolean mLocating;

    public LocationClient() {}

    public LocationClient(Context context) {}

    /**
     * 获取上一次成功的定位的位置，
     * 如果上一次定位失败，则继续向前查找，直到成功的那一次
     * 如果查找失败或是app第一次定位，则返回null
     * @return
     */
    public abstract Loc getLastLocation();

    /**
     * 添加定位结果监听器
     * @param listener
     */
    public final void addLocationListener(LocationListener listener) {
        if (listener == null)
            return;
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * 删除指定的定位结果监听器
     * @param listener
     */
    public final void removeLocationListener(LocationListener listener) {
        if (listener == null)
            return;
        listeners.remove(listener);
    }

    /**
     * 删除所有的定位结果监听器
     */
    public final void removeAllLocationListener() {
        if (listeners != null)
            listeners.clear();
    }

    /**
     * 子类用于执行回调方法
     * @param loc
     * @return true 表示回调执行成功
     */
    protected final boolean execListener(Loc loc) {
        if (listeners != null && loc != null) {
            for (LocationListener listener : listeners) {
                listener.onReceiveLocation(loc);
            }
            return true;
        }
        return false;
    }

    /**
     * 设置定位的参数
     * @param param
     */
    public final void setLocationParam(LocationClientSettings param) {
        if (param == null) return;
        settings = param;
        onLocationParamChange();
    }

    /**
     * 获取定位的参数，每个客户端都有一个默认的 {@link LocationClientSettings} 对象，不会为null
     * @return 相关联的 {@link LocationClientSettings} 对象
     */
    public LocationClientSettings getLocationParam() {
        return settings;
    }

    /**
     * 定位参数变化时的回调函数
     * 由 {@link #setLocationParam(LocationClientSettings)} 调用
     */
    protected abstract void onLocationParamChange();

    /**
     * 客户端是否做好所有的初始化、认证等工作，即可以开始定位了 <br/>
     *
     * @see #prepare(PrepareListener)
     * @see PrepareListener
     * @return true 表示已经就绪
     */
    public abstract boolean isReady();

    /**
     * 进行所有的初始化操作
     * @see #isReady()
     * @param listener 初始化结果
     */
    public abstract void prepare(PrepareListener listener);


    /**
     * 开始异步定位，定位的结果通过 {@link LocationListener} 获得
     *
     * @see #addLocationListener(LocationListener)
     */
    public final void requestLocation() {
        if (isLocating()){
            Log.d("SPCache-------->", "正在定位中 请稍后。。。。。。");
            return;
        }
        if (!isReady()) {
            Log.d(LOG_TAG, "Client not ready yet, please call prepare() first");
            return;
        }
        if (onLocationIntercept()) {
            return;
        }

        setLocating(true);

        if (settings.getCachePolicy() != null && settings.getCachePolicy().useCache()) {
            Loc loc = getLastLocation();
            if (loc != null && loc.getErrorCode() == 0) {
                execListener(loc);
            }
            else {
                requestLocationForce();
            }
        }
        else {
            requestLocationForce();
        }
    }

    /**
     * 定位前的拦截方法，每次定位前先经过这个函数过滤
     * @return true 将拦截这次定位，定位停止
     */
    protected boolean onLocationIntercept() {
        /**
         * 可以在这里执行一些简单的逻辑操作（如果有需要的话）
         *
         * 比如如果没有准备好，可以在这里执行prepare()来再次准备
         */
        return false;
    }

    /**
     * 强制重新定位，不使用缓存策略，直接进行真实定位。
     * 定位的结果通过 {@link LocationListener} 获得
     *
     * @see #addLocationListener(LocationListener)
     */
    public abstract void requestLocationForce();

    /**
     * 停止当前的定位行为
     */
    public abstract void stopLocation();

    /**
     * 是否正在执行定位动作
     * @return true 表示正在定位
     */
    public final boolean isLocating() {
        return mLocating;
    }

    /**
     * 设置定位状态
     * @param locating true 表示正在定位
     */
    public final void setLocating(boolean locating) {
        mLocating = locating;
    }

    /**
     * 清除所有内容，释放资源，此客户端不可以再次使用
     */
    public abstract void release();

    /**
     * 客户端初始化的结果
     */
    public interface PrepareListener {
        /**
         *
         * @param success true表示初始化成功
         * @param msg 失败时的错误信息，可能为null
         */
        void onClientPrepared(boolean success, String msg);
    }
}
