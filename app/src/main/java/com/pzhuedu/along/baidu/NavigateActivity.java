package com.pzhuedu.along.baidu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.SuggestAddrInfo;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.pzhuedu.along.baidu.fragment.BikeFragment;
import com.pzhuedu.along.baidu.fragment.CarFragment;
import com.pzhuedu.along.baidu.fragment.MassFragment;
import com.pzhuedu.along.baidu.fragment.TransitFragment;
import com.pzhuedu.along.baidu.fragment.WalkFragment;
import com.pzhuedu.along.baidu.util.Constants;
import com.pzhuedu.along.baidu.util.SPUtils;
import com.pzhuedu.along.baidu.util.ToastUtil;
import com.pzhuedu.along.baidu.util.Utils;
import com.pzhuedu.along.baidu.view.CustomDialog;
import com.pzhuedu.along.baidu.view.LLCheck;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

/**
 * Created by along on 2017/12/9.
 */

public class NavigateActivity extends Activity implements LLCheck.OnLLCheckclickListener, OnGetRoutePlanResultListener, OnGetGeoCoderResultListener {
    @BindView(R.id.llc_recently)
    LLCheck llcRecently;
    @BindView(R.id.llc_car)
    LLCheck llcCar;
    @BindView(R.id.llc_bus)
    LLCheck llcBus;
    @BindView(R.id.llc_bike)
    LLCheck llcBike;
    @BindView(R.id.llc_walk)
    LLCheck llcWalk;
    private String TAG = getClass().getSimpleName();
    private static int STARTLOC_REQ = 101;
    private static int ENDLOC_REQ = 102;
    private PlanNode stNode;//起始点
    private PlanNode enNode;//终止点
    private RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
    private ROUTE_TYPE nowSearchType = ROUTE_TYPE.CAR_SEARCH_TYPE;
    private boolean isAnimationisRunning;
    //标志位  判断开启Activity的是 开始结点  还是终止结点  默认开始结点是当前用户位置
    private boolean settingObjectis_start = true;
    private static final String APP_FOLDER_NAME = "baidumap";
    private String mSDCardPath = null;
    private boolean hasInitSuccess = false;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(mSearch!=null){
                if(mDialog.isShowing())
                    mDialog.dismiss();
                mSearch.destroy();
                flContent.addView(LayoutInflater.from(NavigateActivity.this).inflate(R.layout.layout_request_timeout,null));
                System.gc();
                mSearch = RoutePlanSearch.newInstance();
                mSearch.setOnGetRoutePlanResultListener(NavigateActivity.this);
            }
        }
    };
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    // showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    // showToastMsg("Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };

    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
            // showToastMsg("TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
            // showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };
    FragmentManager manager;
    GeoCoder mGeoCoderSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private int mStartNodeCityCode;
    private int mEndNodeCityCode;
    private CustomDialog mDialog;
    private String mCurrentCityName;
    private LatLng l1;
    private LatLng l2;
    private String mReverseLocName;
    private String mStName;
    private String mEnName;
    @BindView(R.id.iv_goback)
    ImageView ivGoback;
    @BindView(R.id.iv_add_location)
    ImageView ivAddLocation;
    @BindView(R.id.iv_blue_point)
    ImageView ivBluePoint;
    @BindView(R.id.iv_red_point)
    ImageView ivRedPoint;
    @BindView(R.id.tv_start_location)
    TextView tvStartLocation;
    @BindView(R.id.tv_end_location)
    TextView tvEndLocation;
    @BindView(R.id.iv_exchange_point)
    ImageView ivExchangePoint;
    @BindView(R.id.fl_content)
    FrameLayout flContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);
        ButterKnife.bind(this);
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        // 初始化搜索模块，注册事件监听
        mGeoCoderSearch = GeoCoder.newInstance();

        manager = getFragmentManager();
        mGeoCoderSearch.setOnGetGeoCodeResultListener(this);
        l1 = (LatLng) getIntent().getParcelableExtra(Constants.LATLNG);
        mGeoCoderSearch.reverseGeoCode(new ReverseGeoCodeOption()
                //newVersion(0) 控制是否返回最新2016版数据给用户 默认值为0返回旧版
                .location(l1).newVersion(0));
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        mDialog = builder
                .heightdp(100)
                .widthdp(100)
                .cancelTouchout(false)
                .view(R.layout.dialog_progress)
                .build();

        llcBike.setOnLLCheckclickListener(this);
        llcBus.setOnLLCheckclickListener(this);
        llcWalk.setOnLLCheckclickListener(this);
        llcCar.setOnLLCheckclickListener(this);
        mCurrentCityName = SPUtils.getString(this,Constants.CITYNAME,"攀枝花市");
        Log.d(TAG, "onCreate: mCurrentCityName:  "+mCurrentCityName);
        stNode = PlanNode.withLocation((LatLng) getIntent().getParcelableExtra(Constants.LATLNG));
        BNOuterLogUtil.setLogSwitcher(true);
        if (initDirs()) {
            initNavi();
        }
    }

    enum ROUTE_TYPE {
        RECENTLY_TYPE,
        CAR_SEARCH_TYPE,
        BUS_SEARCH_TYPE,
        BIKE_SEARCH_TYPE,
        WALK_SEARCH_TYPE,
    }

    @OnClick({R.id.iv_goback, R.id.iv_add_location, R.id.iv_blue_point, R.id.tv_start_location, R.id.tv_end_location, R.id.iv_exchange_point, R.id.fl_content})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_goback:
                finish();
                break;
            case R.id.iv_add_location:
                //添加中途经过的地点 暂时无实现
                break;
            case R.id.tv_start_location:
                startActivityForResult(new Intent(this, SettingLocActivity.class), STARTLOC_REQ);
                break;
            case R.id.tv_end_location:
                startActivityForResult(new Intent(this, SettingLocActivity.class), ENDLOC_REQ);
                break;
            case R.id.iv_exchange_point:
                //执行上下交换位置动画
                if (!isAnimationisRunning)
                    execAnimat();
                break;
            case R.id.fl_content:
                break;
        }
    }

    private void execAnimat() {
        Animation rotat = AnimationUtils.loadAnimation(this, R.anim.rotat180);
        Animation translation_up = AnimationUtils.loadAnimation(this, R.anim.translation_down_only);
        Animation translation_down = AnimationUtils.loadAnimation(this, R.anim.translation_up_only);
        isAnimationisRunning = true;
        tvStartLocation.startAnimation(translation_down);
        tvEndLocation.startAnimation(translation_up);
        ivBluePoint.startAnimation(translation_down);
        ivRedPoint.startAnimation(translation_up);
        ivExchangePoint.startAnimation(rotat);
        rotat.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                String stCache = tvStartLocation.getText().toString();
                String enCache = tvEndLocation.getText().toString();
                tvStartLocation.setText(enCache.replace("终", "起"));
                tvEndLocation.setText(stCache.replace("起", "终"));
                isAnimationisRunning = false;
                PlanNode cacheNode = stNode;
                stNode = enNode;
                enNode = cacheNode;
                int s = mStartNodeCityCode;
                mStartNodeCityCode =mEndNodeCityCode;
                mEndNodeCityCode =s;
                requestRoute();//如果起点和终点都有值就发送请求
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("NavgateActivity", "onActivityResult: requestCode:" + requestCode + "  resultCode: " + resultCode + " data: " + data);
        if (requestCode == STARTLOC_REQ && data != null) {
            Log.d(TAG, "onActivityResult: 点击 起点 位置回调");
            mStName = data.getStringExtra(Constants.INTENT_NAME);
            tvStartLocation.setText(mStName);
            l1 =(LatLng) data.getParcelableExtra(Constants.LATLNG);
            settingObjectis_start = true;
            stNode = PlanNode.withLocation(l1);
            mGeoCoderSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    //newVersion(0) 控制是否返回最新2016版数据给用户 默认值为0返回旧版
                    .location(l1).newVersion(0));

        }
        if (requestCode == ENDLOC_REQ && data != null) {
            Log.d(TAG, "onActivityResult: 点击 终点 位置回调");
            mEnName = data.getStringExtra(Constants.INTENT_NAME);
            tvEndLocation.setText(mEnName);
            l2 =(LatLng) data.getParcelableExtra(Constants.LATLNG);
            settingObjectis_start = false;
//          enNode = PlanNode.withCityNameAndPlaceName(data.getStringExtra(Constants.CITYNAME),
//                   data.getStringExtra(Constants.INTENT_ADDRESS));
            enNode = PlanNode.withLocation(l2);
            mGeoCoderSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    //newVersion(0) 控制是否返回最新2016版数据给用户 默认值为0返回旧版
                    .location(l2).newVersion(0));
        }
//        requestRoute();//如果起点和终点都有值就发送请求
    }


    private void initNavi() {

        BNOuterTTSPlayerCallback ttsCallback = null;
        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                String authinfo;
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                Log.d("lilong-------->","onAuthResult:  "+ authinfo);
            }

            @Override
            public void initSuccess() {
                Log.d("lilong---->", "initSuccess: ");
                hasInitSuccess = true;
                initSetting();
            }

            @Override
            public void initStart() {
                Log.d("lilong---->", "initStart: ");
            }
            @Override
            public void initFailed() {
                Log.d("lilong---->", "initFailed: ");
            }

        }, null, ttsHandler, ttsPlayStateListener);

    }


    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            Log.d(TAG, "initDirs: mSDCardPath:   "+mSDCardPath);
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void initSetting() {
        // BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager
                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
        BNaviSettingManager.setIsAutoQuitWhenArrived(true);
        Bundle bundle = new Bundle();
        // 必须设置APPID，否则会静音
        bundle.putString(BNCommonSettingParam.TTS_APP_ID, "10464663");
        BNaviSettingManager.setNaviSdkParam(bundle);
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    @Override
    public void onclick(LLCheck llCheck) {
        switch (llCheck.getId()) {
            case R.id.llc_recently:
                //显示一个ListView 从SP中获取搜索记录 不用去请求网络 切换Fragment
                nowSearchType = ROUTE_TYPE.RECENTLY_TYPE;
                return;
            case R.id.llc_car:
                if (nowSearchType == ROUTE_TYPE.CAR_SEARCH_TYPE)
                    return;
                nowSearchType = ROUTE_TYPE.CAR_SEARCH_TYPE;
                break;
            case R.id.llc_bus:
                if (nowSearchType == ROUTE_TYPE.BUS_SEARCH_TYPE)
                    return;
                nowSearchType = ROUTE_TYPE.BUS_SEARCH_TYPE;
                break;
            case R.id.llc_bike:
                if (nowSearchType == ROUTE_TYPE.BIKE_SEARCH_TYPE)
                    return;
                nowSearchType = ROUTE_TYPE.BIKE_SEARCH_TYPE;
                break;
            case R.id.llc_walk:
                if (nowSearchType == ROUTE_TYPE.WALK_SEARCH_TYPE)
                    return;
                nowSearchType = ROUTE_TYPE.WALK_SEARCH_TYPE;
                break;
        }
        requestRoute();
    }

    /**
     * 去请求路线
     */
    private void requestRoute() {
        flContent.removeAllViews();
        if(!Utils.isNetAvailable(this)){
            Toasty.error(this,"网络异常").show();
            return;
        }
        Log.d(TAG, "requestRoute（）" );
        Log.d(TAG, "onTextChanged: stNode == null " + (stNode == null));
        Log.d(TAG, "onTextChanged: enNode == null " + (enNode == null));
        if ((stNode != null) && (enNode != null)) {
            mDialog.show();
            if(!mHandler.hasMessages(521))
            mHandler.sendEmptyMessageDelayed(521,6000);
            switch (nowSearchType) {
                case CAR_SEARCH_TYPE:
                        Log.d(TAG, " 开始搜索路线  MASS_SEARCH_TYPE  ");
                        mSearch.drivingSearch((new DrivingRoutePlanOption())
                                .from(stNode).to(enNode));
                        nowSearchType = ROUTE_TYPE.CAR_SEARCH_TYPE;
                    break;
                case BUS_SEARCH_TYPE:
                    if (mStartNodeCityCode != mEndNodeCityCode) {
                        ToastUtil.show(this,"跨城路线 BUS_SEARCH_TYPE ");
                        mSearch.masstransitSearch(new MassTransitRoutePlanOption().from(stNode).to(enNode));
                        nowSearchType = ROUTE_TYPE.BUS_SEARCH_TYPE;
                        return;
                    }
                    Log.d(TAG, " 开始搜索路线  BUS_SEARCH_TYPE  ");
                    mSearch.transitSearch((new TransitRoutePlanOption())
                            .from(stNode).city(mCurrentCityName).to(enNode));
                    nowSearchType = ROUTE_TYPE.BUS_SEARCH_TYPE;
                    break;
                case BIKE_SEARCH_TYPE:
                    if (mStartNodeCityCode != mEndNodeCityCode) {
                        ToastUtil.show(this,"跨城路线 规划失败");
                        return;
                    }
                    Log.d(TAG, " 开始搜索路线  BIKE_SEARCH_TYPE  ");
                    mSearch.bikingSearch((new BikingRoutePlanOption())
                            .from(stNode).to(enNode));
                    nowSearchType = ROUTE_TYPE.BIKE_SEARCH_TYPE;
                    break;
                case WALK_SEARCH_TYPE:
                    if (mStartNodeCityCode != mEndNodeCityCode) {
                        ToastUtil.show(this,"跨城路线 规划失败");
                        return;
                    }
                    if (mStartNodeCityCode != mEndNodeCityCode) {
                        ToastUtil.show(this,"跨城路线 规划失败");
                        return;
                    }
                    Log.d(TAG, " 开始搜索路线  WALK_SEARCH_TYPE  ");
                    mSearch.walkingSearch((new WalkingRoutePlanOption())
                            .from(stNode).to(enNode));
                    nowSearchType = ROUTE_TYPE.WALK_SEARCH_TYPE;
                    break;
            }

        }else{
            ToastUtil.show(NavigateActivity.this, "请选择起点位置");
        }

    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Log.d(TAG, "Walking   抱歉未找到  ");
            if(mDialog.isShowing())
                mDialog.dismiss();
            Toasty.warning(this,"抱歉 路线规划失败");
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            if(mDialog.isShowing())
                mDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("检索地址有歧义，请重新设置。");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            Log.d(TAG, "接收到 走路 路线回调");
            if(mHandler.hasMessages(521))
                mHandler.removeMessages(521);
            Bundle bundle = new Bundle();
            bundle.putParcelable("result",result);
            bundle.putParcelable("l1",l1);
            bundle.putParcelable("l2",l2);
            WalkFragment fragment = new WalkFragment();
            fragment.setListener(new WalkFragment.OnLoadingFinishListener() {
                @Override
                public void onFinish() {
                    mDialog.dismiss();
                }
            });
            fragment.setArguments(bundle);
            /*FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.animator.alpha_in,R.animator.alpha_out);*/
            manager.beginTransaction().replace(R.id.fl_content,fragment,"car").commit();
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Log.d(TAG, "Transit   抱歉未找到  ");
            if(mDialog.isShowing())
                mDialog.dismiss();
            Toasty.warning(this,"抱歉 路线规划失败").show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            if(mDialog.isShowing())
                mDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("检索地址有歧义，请重新设置。");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            Log.d(TAG,  "接收到 公交 路线回调");
            if(mHandler.hasMessages(521))
                mHandler.removeMessages(521);
            Bundle bundle = new Bundle();
            bundle.putParcelable("result",result);
            bundle.putParcelable("l1",l1);
            bundle.putParcelable("l2",l2);
            TransitFragment fragment = new TransitFragment();
            fragment.setLoadingFinishListener(new TransitFragment.OnLoadingFinishListener() {
                @Override
                public void onFinish() {
                    mDialog.dismiss();
                }
            });
            fragment.setArguments(bundle);
           /* FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.animator.alpha_in,R.animator.alpha_out);*/
            manager.beginTransaction().replace(R.id.fl_content,fragment,"transit").commit();
        }
    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Log.d(TAG, "MassTransit   抱歉未找到  ");
            if(mDialog.isShowing())
                mDialog.dismiss();
            Toasty.warning(this,"抱歉 路线规划失败");
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            if(mDialog.isShowing())
                mDialog.dismiss();
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("检索地址有歧义，请重新设置。");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            Log.d(TAG,  "接收到 跨城市 路线回调");
            if(mHandler.hasMessages(521))
            mHandler.removeMessages(521);
            Bundle bundle = new Bundle();
            bundle.putParcelable("result",result);
            bundle.putParcelable("l1",l1);
            bundle.putParcelable("l2",l2);
            MassFragment fragment = new MassFragment();
            fragment.setLoadingFinishListener(new MassFragment.OnLoadingFinishListener() {
                @Override
                public void onFinish() {
                    mDialog.dismiss();
                }
            });
            fragment.setArguments(bundle);
           /* FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.animator.alpha_in,R.animator.alpha_out);*/
            manager.beginTransaction().replace(R.id.fl_content,fragment,"mass").commit();
        }
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        Log.d(TAG, "onGetDrivingRouteResult() ");
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Log.d(TAG, "Driving   抱歉未找到  ");
            if(mDialog.isShowing())
                mDialog.dismiss();
            Toasty.warning(this,"抱歉 路线规划失败");
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            if(mDialog.isShowing())
                mDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("检索地址有歧义，请重新设置。");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            /*SuggestAddrInfo info = result.getSuggestAddrInfo();
            if(info!=null)
            for(PoiInfo info1 : info.getSuggestEndNode()){
                if(info1 != null)
                Log.d(TAG, "EndNode() city:  "+info1.city+"EndNode() address: "+info1.address);
            }*/
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            Log.d(TAG,  "接收到 开车 路线回调");
            if(mHandler.hasMessages(521))
                mHandler.removeMessages(521);
            Bundle bundle = new Bundle();
            bundle.putParcelable("result",result);
            bundle.putParcelable("l1",l1);
            bundle.putParcelable("l2",l2);
            bundle.putString("stname",mStName);
            bundle.putString("enname",mEnName);
            CarFragment fragment = new CarFragment();
            fragment.setArguments(bundle);
            fragment.setListener(new CarFragment.OnLoadingFinishListener() {
                @Override
                public void onFinish() {
                    mDialog.dismiss();
                }
            });
            /*FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.animator.alpha_in,R.animator.alpha_out);*/
            if (mStartNodeCityCode == mEndNodeCityCode)
                manager.beginTransaction().replace(R.id.fl_content, fragment, "car").commit();
            else
                manager.beginTransaction().replace(R.id.fl_content, fragment, "mass").commit();
        }
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Log.d(TAG, "Indoor 抱歉未找到结果");
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("检索地址有歧义，请重新设置。");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            Log.d(TAG,  "接收到 室内导航 路线回调");
        }
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Log.d(TAG, "Bikeing 抱歉，未找到结果");
            if(mDialog.isShowing())
                mDialog.dismiss();
            Toasty.warning(this,"抱歉 路线规划失败");
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            if(mDialog.isShowing())
                mDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("检索地址有歧义，请重新设置。");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            Log.d(TAG, "接收到 骑车 路线回调");
            if(mHandler.hasMessages(521))
            mHandler.removeMessages(521);
            Bundle bundle = new Bundle();
            bundle.putParcelable("result",result);
            bundle.putParcelable("l1",l1);
            bundle.putParcelable("l2",l2);
            BikeFragment fragment = new BikeFragment();
            fragment.setArguments(bundle);
            fragment.setListener(new BikeFragment.OnLoadingFinishListener() {
                @Override
                public void onFinish() {
                    mDialog.dismiss();
                }
            });
            /*FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.animator.alpha_in,R.animator.alpha_out);*/
            manager.beginTransaction().replace(R.id.fl_content,fragment,"bike").commit();
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    //经纬度转换成地址时回调该方法
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        Log.d(TAG, "onGetReverseGeoCodeResult: ");
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            ToastUtil.show(this, "抱歉坐标转换成地址失败");
            return;
        }
        if (settingObjectis_start) {
            if(TextUtils.isEmpty(mReverseLocName)){
                mReverseLocName = reverseGeoCodeResult.getAddress();
            }
            mStartNodeCityCode = reverseGeoCodeResult.getCityCode();
            Log.d(TAG, "onGetReverseGeoCodeResult:mStartNodeCityCode  " + mStartNodeCityCode);
        } else {
            mEndNodeCityCode = reverseGeoCodeResult.getCityCode();
            Log.d(TAG, "onGetReverseGeoCodeResult:mEndNodeCityCode  " + mEndNodeCityCode);
        }
        requestRoute();//如果起点和终点都有值就发送请求
    }
}
