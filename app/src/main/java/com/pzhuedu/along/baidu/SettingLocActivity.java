package com.pzhuedu.along.baidu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.baidu.mapapi.cloud.CloudRgcResult;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.pzhuedu.along.baidu.adapter.SearchRecordAdapter;
import com.pzhuedu.along.baidu.adapter.Search_Adapter;
import com.pzhuedu.along.baidu.adapter.SettingLocAdapter;
import com.pzhuedu.along.baidu.util.Constants;
import com.pzhuedu.along.baidu.util.SPUtils;
import com.pzhuedu.along.baidu.util.ToastUtil;
import com.pzhuedu.along.baidu.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by along on 2017/12/21.
 */

public class SettingLocActivity extends Activity implements OnGetPoiSearchResultListener, OnGetSuggestionResultListener {
    @BindView(R.id.iv_back_to_home)
    ImageView ivBackToHome;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;
    @BindView(R.id.pb_is_search)
    ProgressBar pbIsSearch;
    @BindView(R.id.ll_map_sele_loc)
    LinearLayout llMapSeleLoc;
    @BindView(R.id.ll_collect_sele_loc)
    LinearLayout llCollectSeleLoc;
    @BindView(R.id.ll_home_loc)
    LinearLayout llHomeLoc;
    @BindView(R.id.ll_company_loc)
    LinearLayout llCompanyLoc;
    @BindView(R.id.rv_settingloc)
    RecyclerView rvSettingloc;
    @BindView(R.id.rv_setting_recoder)
    RecyclerView rvSettingrec;
    private SuggestionSearch mSuggestionSearch = null;
    private PoiSearch mPoiSearch = null;
    private String mCurrentCity;
    private int mEndIndex;
    private boolean isSuggVisibility;
    private String searchkey;
    private SettingLocAdapter mAdapter;
    private ArrayList<PoiDetailResult> poiDetailResultArrayList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingloc);
        ButterKnife.bind(this);
        mCurrentCity = SPUtils.getString(this, Constants.CITYNAME,"攀枝花");
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        rvSettingloc.setLayoutManager(new LinearLayoutManager(this));
        rvSettingrec.setLayoutManager(new LinearLayoutManager(this));
        rvSettingrec.setAdapter(new SearchRecordAdapter(this,new ArrayList<SuggestionResult.SuggestionInfo>()));
        //TODO:应当传入一个ArrayList中的数据
        poiDetailResultArrayList = new ArrayList<>();
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    rvSettingloc.setVisibility(View.GONE);
                    rvSettingrec.setVisibility(View.VISIBLE);
                    pbIsSearch.setVisibility(View.INVISIBLE);
                    ivDelete.setVisibility(View.INVISIBLE);
                    isSuggVisibility = false;
                    return;
                } else isSuggVisibility = true;

                /*if(count>0 && !isSuggVisibility){
                    isSuggVisibility = true;
                }*/
                searchkey = s.toString();
                etSearch.setSelection(s.length());//设置游标位置
                requestSearch(searchkey);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private void requestSearch(CharSequence s) {
        if (Utils.isNetAvailable(SettingLocActivity.this) && isSuggVisibility){
            mSuggestionSearch
                    .requestSuggestion((new SuggestionSearchOption())
                            .keyword(s.toString()).city(mCurrentCity));
        }
        else ToastUtil.show(SettingLocActivity.this, "老铁 ~ 网络异常");
    }
    @OnClick({R.id.iv_back_to_home, R.id.et_search, R.id.iv_delete, R.id.pb_is_search, R.id.ll_map_sele_loc, R.id.ll_collect_sele_loc, R.id.ll_home_loc, R.id.ll_company_loc, R.id.rv_settingloc})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back_to_home:
                finish();
                break;
            case R.id.iv_delete:
                etSearch.setText("");
                break;
            case R.id.ll_map_sele_loc:
                startActivityForResult(new Intent(this,ItenizdActivity.class),202);
                //开启地图选点
                break;
            case R.id.ll_collect_sele_loc:
                //开启收藏选点 显示一个ListView 让用户点击选择
                break;
            case R.id.ll_home_loc:
                //设置位置为家庭地址 从存储在SP中读取信息
                break;
            case R.id.ll_company_loc:
                //设置位置为公司地址 从存储的SP中读取信息
                break;

        }
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            ToastUtil.show(this, "老铁 查无此类信息");
            ivDelete.setVisibility(View.VISIBLE);
            return;
        }
        if (isSuggVisibility) {
            mEndIndex = res.getAllSuggestions().size();
            poiDetailResultArrayList.clear();
            pbIsSearch.setVisibility(View.VISIBLE);
            ivDelete.setVisibility(View.INVISIBLE);
            for (int i = 0; i < res.getAllSuggestions().size(); i++) {
                SuggestionResult.SuggestionInfo info = res.getAllSuggestions().get(i);
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUid(info.uid));

            }
        }
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        /*Log.d("lilong-------->", "onGetPoiResult: ");
        if(poiResult.getAllPoi()==null) return;
        poiInfoArrayList = poiResult.getAllPoi();
        if(mAdapter==null){
            mAdapter = new SettingLocAdapter(this,poiInfoArrayList);
            rvSettingloc.setAdapter(mAdapter);
        }else            mAdapter.notifyDataSetChanged();
        rvSettingloc.setVisibility(View.VISIBLE);
        pbIsSearch.setVisibility(View.INVISIBLE);
        ivDelete.setVisibility(View.VISIBLE);
        for(PoiInfo info : poiResult.getAllPoi()){
            Log.d("Navigate-------->", "info.address: "+info.address
                    +"info.name: "+info.name
                    +"info.city: "+info.city
                    +"info.phoneNum: "+info.phoneNum
                    +"info.postCode: "+info.postCode
            );
        }*/
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        Log.d("lilong---->", "getGrouponNum: " + poiDetailResult.getGrouponNum() +
                " getEnvironmentRating: " + poiDetailResult.getEnvironmentRating() +
                " getAddress: " + poiDetailResult.getAddress() +
                " getDetailUrl: " + poiDetailResult.getDetailUrl() +
                " getName: " + poiDetailResult.getName() +
                " getType: " + poiDetailResult.getType() +
                " getOverallRating: " + poiDetailResult.getOverallRating() +
                " getCheckinNum: " + poiDetailResult.getCheckinNum() +
                " getShopHours: " + poiDetailResult.getShopHours() +
                " getTelephone: " + poiDetailResult.getTelephone() +
                " getServiceRating: " + poiDetailResult.getServiceRating() +
                " getTasteRating: " + poiDetailResult.getTasteRating() +
                " getPrice: " + poiDetailResult.getPrice() +
                " getEnvironmentRating: " + poiDetailResult.getEnvironmentRating());
        if (TextUtils.isEmpty(poiDetailResult.getName()) || TextUtils.isEmpty(poiDetailResult.getAddress())) {
            mEndIndex--;
        }else        poiDetailResultArrayList.add(poiDetailResult);
        if (mEndIndex == poiDetailResultArrayList.size() && isSuggVisibility) {
            if(mAdapter==null){
                mAdapter = new SettingLocAdapter(this,poiDetailResultArrayList);
                rvSettingloc.setAdapter(mAdapter);
            }else            mAdapter.notifyDataSetChanged();
            rvSettingloc.setVisibility(View.VISIBLE);
            rvSettingrec.setVisibility(View.INVISIBLE);
            pbIsSearch.setVisibility(View.INVISIBLE);
            ivDelete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("lilong-------->","onActivityResult(): requestCode:"+requestCode+"  resultCode: "+ resultCode +" data: "+data);
        if(data!=null && requestCode==202){
            Intent intent  = new Intent();
            Log.d("lilong--------->", "onActivityResult:address:  "+data.getParcelableExtra(Constants.INTENT_NAME)+"  ：latlng: "
                    +((LatLng)data.getParcelableExtra(Constants.LATLNG)).toString());
            intent.putExtra(Constants.INTENT_NAME,data.getStringExtra(Constants.INTENT_NAME));
            intent.putExtra(Constants.LATLNG,(LatLng)data.getParcelableExtra(Constants.LATLNG));
            setResult(0,intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSuggestionSearch!=null)
            mSuggestionSearch.destroy();
    }
}
