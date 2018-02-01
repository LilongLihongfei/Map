package com.pzhuedu.along.baidu.loc;

/**
 * Created by along on 2017/11/22.
 * <p>
 * 定位结果的回调函数
 * </p>
 */

public interface LocationListener {
    /**
     * 定位的结果，loc一定不为空，定位错误码类型为0时定位成功。
     * 判断方法为{@link Loc#getErrorCode()}
     * @param loc 包含结果的参数，不为空
     */
    void onReceiveLocation(Loc loc);
}
