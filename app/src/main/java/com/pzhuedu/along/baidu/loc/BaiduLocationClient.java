package com.pzhuedu.along.baidu.loc;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;

/**
 * Created by Allen on 2017/11/23.
 */

public class BaiduLocationClient extends LocationClient {
    public static final String SP_NAME = "SPCache";
    private Context mContext;
    private com.baidu.location.LocationClient mBaiduClient;
    private LocationClientOption mBaiduClientOption;
    private BaiDuLocationListener locationListener;
    private long MaxWaitTime = 1000*6;
    private long mStartLocTime;
    private CachePolicy mCachePolicy;
    private boolean isReady;

    public BaiduLocationClient() {
    }

    public BaiduLocationClient(Context context) {
        mContext = context;
        mBaiduClient = new com.baidu.location.LocationClient(context);
    }

    @Override
    public Loc getLastLocation() {
        Log.d("SPCache--------->","--------getLastLocation()---------");
        int index =mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt("INDEX",0);
        Log.d("SPCache--------->","当前SP缓存中 最后一个索引为："+index);
        for (int i = index; i > 0;i--) {
            String LocString = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString("INDEX"+i,null);
            Log.d("SPCache--------->","当前SP缓存中 获取到的数据为："+LocString);
            if(!TextUtils.isEmpty(LocString)){
                String[] loc_spilt =LocString.split("#");
                if(System.currentTimeMillis()- Long.parseLong(loc_spilt[0])>MaxWaitTime) //SP缓存中最近的缓存时间 都 超过了十分钟了 结束 后 强行定位
                    return null;
                Gson gson = new Gson();
                Loc loc = null;
                Log.d("SPCache--------->","loc_spilt："+loc_spilt[1]);
                try {
                    loc = gson.fromJson(loc_spilt[1],Loc.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    Log.d("SPCache--------->","json解析异常");
                }
                if(loc!=null && loc.getErrorCode()!=0){
                    mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().remove("INDEX"+i).apply();
                    mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putInt("INDEX",i).commit();
                    continue;//有效时间内 但此次定位失败 从SP中移除zz  
                }
                setLocating(false);
                return loc;
            }
        }
        return null;
    }

    @Override
    protected void onLocationParamChange() {
        initBaiduClientOption();
//        requestLocationForce();
    }

    private void initBaiduClientOption() {
        if(mBaiduClientOption == null)
        mBaiduClientOption = new LocationClientOption();
        if(settings == null)
            settings = new LocationClientSettings();
            switch(settings.getLocationMode()){
                case Battery_Saving:
                    mBaiduClientOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
                    break;
                case Device_Sensors:
                    mBaiduClientOption.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
                    break;
                case Hight_Accuracy:
                    mBaiduClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
                    break;
            }
            switch(settings.getCoordType()){
                case GCJ02:
                    mBaiduClientOption.setCoorType("gcj02");
                    break;
                case bd09ll:
                    mBaiduClientOption.setCoorType("bd0911");
                    break;
                case WGS84:
                    mBaiduClientOption.setCoorType("wgs84");
                    break;
            }
            mBaiduClientOption.setIsNeedAddress(true);
            mBaiduClientOption.setIsNeedLocationDescribe(true);
            //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            mBaiduClientOption.setIgnoreKillProcess(true);
    }

    @Override
    public boolean isReady() {
        //默认已经准备好了 无需Auth认证
        return isReady;
    }

    @Override
    public void prepare(PrepareListener listener) {
        //Auth认证 认证成功 回调
//        listener.onClientPrepared(true,"认证通过了");
        if(settings == null)
            settings = new LocationClientSettings();
        initBaiduClientOption();//请求前初始化选项参数
        mBaiduClient.setLocOption(mBaiduClientOption);
        locationListener = new BaiDuLocationListener();
        mBaiduClient.registerLocationListener(locationListener);
        isReady = true;
//        LocationFactory.add(this);
        listener.onClientPrepared(isReady,"消息");
    }

    @Override
    public void requestLocationForce() {
        mStartLocTime = System.currentTimeMillis();
        if(locationListener==null) {
            Log.d("SPCache--------->","locationListener.为空  ");
            locationListener = new BaiDuLocationListener();
            mBaiduClient.registerLocationListener(locationListener);
        }
        if(mBaiduClientOption == null){
            initBaiduClientOption();
            mBaiduClient.setLocOption(mBaiduClientOption);
        }
        if(mBaiduClient.isStarted()){
            Log.d("SPCache--------->","mBaiduClient.isStarted()");
            mBaiduClient.requestLocation();
        }else {
            Log.d("SPCache--------->","mBaiduClient 定位SDK没有启动 成功 start");
            mBaiduClient.start();
        }
    }

    @Override
    public void stopLocation() {
        if(mBaiduClient!=null && isLocating())
            mBaiduClient.stop();
    }

    @Override
    public void release() {
        if(mBaiduClient!=null && locationListener!=null){
            stopLocation();
            mBaiduClient.unRegisterLocationListener(locationListener);
            mBaiduClient = null;
            locationListener = null;
        }
        if(mBaiduClientOption!=null) mBaiduClientOption = null;
//        LocationFactory.remove(this.getClass());//删除静态BaiduLocationClient对象
        //删除SP文件
        File file= new File("/data/data/"+mContext.getPackageName().toString()+"/shared_prefs",SP_NAME+".xml");
        if(file.exists())
        {
            file.delete();

        }
    }

    public class BaiDuLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            Log.d("SPCache--------->","获取数据成功 回调了onReceiveLocation()");
            Addr addr = new Addr();
            addr.address=location.getAddrStr();    //获取详细地址信息
            addr.country = location.getCountry();    //获取国家
            addr.province = location.getProvince();    //获取省份
            addr.city = location.getCity();    //获取城市
            addr.district = location.getDistrict();    //获取区县
            addr.street = location.getStreet();    //获取街道信息
            addr.cityCode = location.getCityCode();
            addr.streetNumber = location.getStreetNumber();
            addr.countryCode = location.getCountryCode();

            Loc loc = new Loc();
            loc.setAddr(addr);
            loc.setAltitude(location.getAltitude());
            loc.setLatitude(location.getLatitude());
            loc.setLongitude(location.getLongitude());
            loc.setCoordType(settings.getCoordType());
            switch(location.getLocType()){
                case BDLocation.TypeGpsLocation:
                    loc.setLoctionType(Loc.TYPE_GPS);
                    loc.setSpeed(location.getSpeed());// 速度，仅gps定位结果时有速度信息，单位公里/小时
                    loc.setTime(System.currentTimeMillis()-mStartLocTime);
                    loc.setLocationDescription(location.getLocationDescribe());
                    loc.setErrorDescription("定位成功");
                    loc.setErrorCode(0);//定位成功
                    break;
                case BDLocation.TypeNetWorkLocation:
                    /**在网络定位结果的情况下，获取网络定位结果是通过基站定位得到的还是通过wifi定位得到的还是GPS得结果
                    返回:
                    String : "wf"： wifi定位结果 “cl“； cell定位结果 “ll”：GPS定位结果 null 没有获取到定位结果采用的类型**/
                    switch(location.getNetworkLocationType()){
                        case "wf":
                            loc.setLoctionType(Loc.TYPE_WIFI);
                            break;
                        case "cl":
                            loc.setLoctionType(Loc.TYPE_NET);
                            break;
                        case "ll":
                            loc.setLoctionType(Loc.TYPE_GPS);
                            loc.setSpeed(location.getSpeed());// 速度，仅gps定位结果时有速度信息，单位公里/小时
                            break;
                        default:
                            loc.setLoctionType(Loc.TYPE_OTHER);
                            break;
                    }
                    loc.setTime(System.currentTimeMillis()-mStartLocTime);
                    loc.setLocationDescription(location.getLocationDescribe());
                    loc.setErrorDescription("定位成功");
                    loc.setErrorCode(0);//定位成功
                    break;
                case BDLocation.TypeCriteriaException:
                    loc.setErrorDescription("无法定位结果，一般由于定位SDK内部检测到没有有效的定位依据，比如在飞行模式下就会返回该定位类型， 一般关闭飞行模式或者打开wifi就可以再次定位成功");
                    loc.setErrorCode(BDLocation.TypeCriteriaException);
                    break;
                case BDLocation.TypeNetWorkException:
                    loc.setErrorDescription("网络连接失败，一般由于手机无有效网络连接导致，请检查手机是否能够正常上网");
                    loc.setErrorCode(BDLocation.TypeNetWorkException);
                    break;
                case BDLocation.TypeNone:
                    loc.setErrorDescription("无效定位结果，一般由于定位SDK内部逻辑异常时出现");
                    loc.setErrorCode(BDLocation.TypeNone+1);// BDLocation.TypeNone == 0
                    break;
                case BDLocation.TypeServerCheckKeyError:
                    loc.setErrorDescription("server校验KEY失败，请确认KEY合法");
                    loc.setErrorCode(BDLocation.TypeServerCheckKeyError);
                    break;
                case BDLocation.TypeServerDecryptError:
                    loc.setErrorDescription("server解密定位请求失败，请检查SO文件是否加载正常");
                    loc.setErrorCode(BDLocation.TypeServerDecryptError);
                    break;
                case BDLocation.TypeServerError:
                    loc.setErrorDescription("server定位失败，没有对应的位置信息");
                    loc.setErrorCode(BDLocation.TypeServerError);
                    break;
                case BDLocation.TypeOffLineLocationFail:
                    loc.setErrorDescription("离线定位失败结果 ，一般由于手机网络不通，会请求定位SDK内部的离线定位策略但失败了，这种定位也属于无效的定位结果");
                    loc.setErrorCode(BDLocation.TypeOffLineLocationFail);
                    break;
                case BDLocation.TypeOffLineLocationNetworkFail:
                    loc.setErrorDescription("离线定位成功结果 ，已取消");
                    loc.setErrorCode(BDLocation.TypeOffLineLocationNetworkFail);
                    break;
                default:
                    loc.setErrorDescription("location.getLocType()匹配失败！");
                    loc.setErrorCode(2);
                    break;
            }
            setLocating(false);
            CachePolicy mCachePolicy = settings.getCachePolicy();
            if(mCachePolicy!=null && mCachePolicy.useCache()){
                mCachePolicy.doCache(mContext,loc);//执行缓存策略
            }
            execListener(loc);//执行回调
        }

    }

    @Override
    protected boolean onLocationIntercept() {
        return super.onLocationIntercept();
    }
}
