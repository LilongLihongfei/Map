package com.pzhuedu.along.baidu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
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
import com.pzhuedu.along.baidu.util.ToastUtil;
import com.pzhuedu.along.baidu.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by along on 2017/12/7.
 */

public class SearchActivity extends Activity implements OnGetPoiSearchResultListener, OnGetSuggestionResultListener, View.OnClickListener {
    String TAG = getClass().getSimpleName();
    @BindView(R.id.pb_is_search)
    ProgressBar pbIsSearch;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;
    private boolean isSuggVisibility;
    private SuggestionSearch mSuggestionSearch = null;
    private PoiSearch mPoiSearch = null;
    private String mCurrentCity;
    private int mEndIndex;
    private String searchkey;
    private Search_Adapter mAdatper;
    private ArrayList<PoiDetailResult> poiDetailResultArrayList;
    @BindView(R.id.rv_suggestion)
    RecyclerView rvSuggestion;

    @BindView(R.id.iv_back_to_home)
    ImageView ivBackToHome;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.ll_back_to_home)
    LinearLayout llBackToHome;
    @BindView(R.id.ll_go_to_work)
    LinearLayout llGoToWork;
    @BindView(R.id.tv_food)
    TextView tvFood;
    @BindView(R.id.tv_hotel)
    TextView tvHotel;
    @BindView(R.id.tv_bank)
    TextView tvBank;
    @BindView(R.id.tv_market)
    TextView tvMarket;
    @BindView(R.id.tv_more)
    TextView tvMore;
    @BindView(R.id.rv_recycler)
    RecyclerView rvRecycler;
    @BindView(R.id.ll_footer)
    LinearLayout ll_footer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        mCurrentCity = getIntent().getStringExtra("city");
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        rvRecycler.setLayoutManager(new LinearLayoutManager(this));
        rvSuggestion.setLayoutManager(new LinearLayoutManager(this));
        //TODO:应当传入一个ArrayList中的数据
        rvRecycler.setAdapter(new SearchRecordAdapter(this,new ArrayList<SuggestionResult.SuggestionInfo>()));
        poiDetailResultArrayList = new ArrayList<>();
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    ll_footer.setVisibility(View.VISIBLE);
                    rvSuggestion.setVisibility(View.GONE);
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
                Log.d(TAG, "onTextChanged: searchkey:  " + searchkey);
                requestSearch(searchkey);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if(!TextUtils.isEmpty(getIntent().getStringExtra("content"))){
           etSearch.setText(getIntent().getStringExtra("content"));
        }
    }

    private void requestSearch(CharSequence s) {
        if (Utils.isNetAvailable(SearchActivity.this))
            mSuggestionSearch
                    .requestSuggestion((new SuggestionSearchOption())
                            .keyword(s.toString()).city(mCurrentCity));
        else ToastUtil.show(SearchActivity.this, "老铁 ~ 网络异常");
    }

    @Override
    public void onBackPressed() {
        Intent intent  = new Intent();
        intent.putExtra("content",etSearch.getText().toString());
        Log.d("么么哒", "onPause："+etSearch.getText().toString());
        setResult(22,intent);
        overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
        super.onBackPressed();
    }


    @OnClick({R.id.iv_delete,R.id.iv_back_to_home, R.id.ll_back_to_home, R.id.ll_go_to_work, R.id.tv_food, R.id.tv_hotel, R.id.tv_bank, R.id.tv_market, R.id.tv_more})
    public void onViewClicked(View view) {
        Intent intent = new Intent(this,NavigateActivity.class);
        switch (view.getId()) {
            case R.id.iv_back_to_home:
                onBackPressed();
                break;
            case R.id.ll_back_to_home:
                intent.putExtra("home_latin","");
                intent.putExtra("home_lontin","");
                startActivity(intent);
                break;
            case R.id.ll_go_to_work:
                intent.putExtra("comp_latin","");
                intent.putExtra("comp_lontin","");
                startActivity(intent);
                break;
            case R.id.tv_food:
                etSearch.setText("食物");
                break;
            case R.id.tv_hotel:
                etSearch.setText("酒店");
                break;
            case R.id.tv_bank:
                etSearch.setText("银行");
                break;
            case R.id.tv_market:
                etSearch.setText("超市");
                break;
            case R.id.tv_more:
                ToastUtil.show(this,"暂无更多内容");
                break;
            case R.id.iv_delete:
                etSearch.setText("");
                break;
        }
    }




    


    @Override
    public void onGetPoiResult(PoiResult poiResult) {
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
            Log.d(TAG, "onGetPoiDetailResult: getName为空了");
        }else        poiDetailResultArrayList.add(poiDetailResult);
        Log.d(TAG, "poiDetailResultArrayList.size(): " + poiDetailResultArrayList.size() + "  mEndIndex: " + mEndIndex);
        if (mEndIndex == poiDetailResultArrayList.size() && isSuggVisibility) {
            Log.d(TAG, "mEndIndex == poiDetailResultArrayList.size()");
            if(mAdatper == null){
                mAdatper =new Search_Adapter(SearchActivity.this,poiDetailResultArrayList,searchkey);
                rvSuggestion.setAdapter(mAdatper);
            }else mAdatper.notifyDataSetChanged();
            ll_footer.setVisibility(View.GONE);
            rvSuggestion.setVisibility(View.VISIBLE);
            pbIsSearch.setVisibility(View.INVISIBLE);
            ivDelete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            ToastUtil.show(this, "老铁 查无此类信息");
            Log.d(TAG, "onGetSuggestionResult: 为空了");
            ivDelete.setVisibility(View.VISIBLE);
            return;
        }
        if (isSuggVisibility) {
            Log.d(TAG, "res.getAllSuggestions().size()" + res.getAllSuggestions().size());
            mEndIndex = res.getAllSuggestions().size();
            ll_footer.setVisibility(View.GONE);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_poi_tohere:
                startActivity(new Intent(this, NavigateActivity.class));
                break;
            case R.id.ll_sug_ll:
                Log.d(TAG, "onClick: 执行了itemview点击事件");
                startActivity(new Intent(this, LauncherActivity.class));
                break;
            case R.id.ll_clearall:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSuggestionSearch!=null)
        mSuggestionSearch.destroy();
    }
}
