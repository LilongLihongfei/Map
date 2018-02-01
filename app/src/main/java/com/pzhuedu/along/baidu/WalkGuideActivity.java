package com.pzhuedu.along.baidu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWNaviStatusListener;
import com.baidu.mapapi.walknavi.adapter.IWRouteGuidanceListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.adapter.IWTTSPlayer;
import com.baidu.mapapi.walknavi.model.RouteGuideKind;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.baidu.platform.comapi.walknavi.WalkNaviModeSwitchListener;

/**
 * Created by along on 2018/1/20.
 */

public class WalkGuideActivity extends Activity {
    private static final String TAG = "WalkGuideActivity";
    private WalkNavigateHelper walkHelper;
    private WalkNaviLaunchParam walkParam ;
    private boolean isInitSuccess = false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        walkHelper = WalkNavigateHelper.getInstance();
        walkParam = new WalkNaviLaunchParam();
        Intent intent = getIntent();
        LatLng stlatlng = (LatLng)intent.getParcelableExtra("stlatlng");
        LatLng enlatlng = (LatLng)intent.getParcelableExtra("enlatlng");
        Log.d(TAG, "onCreate: stlatlng: "+stlatlng+"   enlatlng:  "+enlatlng);
        walkParam.stPt(stlatlng);
        walkParam.endPt(enlatlng);
        walkHelper.initNaviEngine(this, new IWEngineInitListener() {
            @Override
            public void engineInitSuccess() {
                Log.d(TAG, "engineInitSuccess: ");
                walkHelper.routePlanWithParams(walkParam, new IWRoutePlanListener() {
                    @Override
                    public void onRoutePlanStart() {
                        Log.d(TAG, "onRoutePlanStart: ");
                    }

                    @Override
                    public void onRoutePlanSuccess() {
                        Log.d(TAG, "onRoutePlanSuccess: ");
                        isInitSuccess = true;
                    }

                    @Override
                    public void onRoutePlanFail(WalkRoutePlanError walkRoutePlanError) {
                        Log.d(TAG, "onRoutePlanFail: ");
                    }
                });
            }

            @Override
            public void engineInitFail() {
                isInitSuccess = false;
                Log.d(TAG, "engineInitFail: ");
            }
        });
        walkHelper.setRouteGuidanceListener(this, new IWRouteGuidanceListener() {
            @Override
            public void onRouteGuideIconUpdate(Drawable drawable) {

            }

            @Override
            public void onRouteGuideKind(RouteGuideKind routeGuideKind) {

            }

            @Override
            public void onRoadGuideTextUpdate(CharSequence charSequence, CharSequence charSequence1) {

            }

            @Override
            public void onRemainDistanceUpdate(CharSequence charSequence) {

            }

            @Override
            public void onRemainTimeUpdate(CharSequence charSequence) {

            }

            @Override
            public void onGpsStatusChange(CharSequence charSequence, Drawable drawable) {

            }

            @Override
            public void onRouteFarAway(CharSequence charSequence, Drawable drawable) {

            }

            @Override
            public void onRoutePlanYawing(CharSequence charSequence, Drawable drawable) {

            }

            @Override
            public void onReRouteComplete() {

            }

            @Override
            public void onArriveDest() {

            }

            @Override
            public void onVibrate() {

            }
        });

        walkHelper.setWalkNaviStatusListener(new IWNaviStatusListener() {
            @Override
            public void onWalkNaviModeChange(int i, WalkNaviModeSwitchListener walkNaviModeSwitchListener) {

            }

            @Override
            public void onNaviExit() {
                WalkGuideActivity.this.finish();
            }
        });

        walkHelper.setTTsPlayer(new IWTTSPlayer() {
            @Override
            public int playTTSText(String s, boolean b) {
                Log.d(TAG, "playTTSText: 语音播报内容： "+s.toString());
                return 0;
            }
        });

        if(isInitSuccess){
            setContentView(walkHelper.onCreate(this));
            walkHelper.startWalkNavi(this);
        }
        else Log.d(TAG, "onCreate: 算路失败 没有设置ContextView");
    }


    @Override
    protected void onResume() {
        super.onResume();
        walkHelper.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        walkHelper.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        walkHelper.quit();
    }
}
