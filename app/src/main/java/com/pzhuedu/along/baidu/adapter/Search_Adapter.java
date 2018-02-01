package com.pzhuedu.along.baidu.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.pzhuedu.along.baidu.LauncherActivity;
import com.pzhuedu.along.baidu.NavigateActivity;
import com.pzhuedu.along.baidu.R;
import com.pzhuedu.along.baidu.SearchActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Search_Adapter extends RecyclerView.Adapter implements View.OnClickListener{
        private String searchkey;
        private Context context;
        private ArrayList<PoiDetailResult> poiDetailResultArrayList;
        private SpannableString spannableString;
        public Search_Adapter(Context context, ArrayList arrayList,String key) {
            poiDetailResultArrayList = arrayList;
            this.context = context;
            searchkey = key;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.ll_sug_ll)
            LinearLayout llSugll;
            @BindView(R.id.tv_poi_pingfen)
            TextView tvPoiPingfen;
            @BindView(R.id.iv_item_icon)
            ImageView ivItemIcon;
            @BindView(R.id.tv_item_top)
            TextView tvItemTop;
            @BindView(R.id.tv_poi_type)
            TextView tvPoiType;
            @BindView(R.id.tv_poi_add)
            TextView tvPoiAdd;
            @BindView(R.id.rb_start_grade)
            RatingBar rbStartGrade;
            @BindView(R.id.tv_visit_num)
            TextView tvVisitNum;
            @BindView(R.id.ll_grade_and_visit_num)
            LinearLayout llGradeAndVisitNum;
            @BindView(R.id.ll_poi_tohere)
            LinearLayout llPoiTohere;

            public MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sug_search_recycle, null);
            return new MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder newholder = (MyViewHolder) holder;
            PoiDetailResult res = poiDetailResultArrayList.get(position);
            newholder.llPoiTohere.setOnClickListener(this);
            newholder.llSugll.setOnClickListener(this);
            String name = res.getName();
            if (!TextUtils.isEmpty(name)) {
                spannableString = new SpannableString(name);
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.glod));
                int index = name.indexOf(searchkey);
                if (index >= 0)
                    spannableString.setSpan(colorSpan, index, index + searchkey.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                newholder.tvItemTop.setText(spannableString);
                newholder.tvPoiAdd.setText(res.getAddress());
            }
            String addr = res.getAddress();
            String type = res.getType();
            if (addr.contains(";")) {
                newholder.tvPoiType.setText("公交");
                int start = 0;
                String[] addrs = addr.split(";");
                SpannableString spannableString = new SpannableString(res.getAddress());
                for (String ar : addrs) {
                    start = addr.indexOf(ar);
                    BackgroundColorSpan colorSpan = new BackgroundColorSpan(context.getResources().getColor(R.color.colorPrimary));
                    spannableString.setSpan(colorSpan, start, start + ar.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                newholder.tvPoiAdd.setText(spannableString);
                newholder.llGradeAndVisitNum.setVisibility(View.GONE);
                return;
            }
            if (!TextUtils.isEmpty(type))
                switch (type) {
                    case "house":
                        newholder.ivItemIcon.setImageResource(R.drawable.home);
                        newholder.tvPoiType.setText("居家");
                        newholder.llGradeAndVisitNum.setVisibility(View.GONE);
                        break;
                    case "hospital":
                        newholder.ivItemIcon.setImageResource(R.drawable.hospital);
                        newholder.tvPoiType.setText("医院");
                        newholder.llGradeAndVisitNum.setVisibility(View.GONE);
                        break;
                    case "enterprise":
                        newholder.ivItemIcon.setImageResource(R.drawable.handbag);
                        newholder.tvPoiType.setText("公司");
                        newholder.llGradeAndVisitNum.setVisibility(View.GONE);
                        break;
                    case "education":
                        newholder.ivItemIcon.setImageResource(R.drawable.school);
                        newholder.tvPoiType.setText("学校");
                        newholder.llGradeAndVisitNum.setVisibility(View.GONE);
                        break;
                    case "life":
                        newholder.ivItemIcon.setImageResource(R.drawable.life);
                        newholder.tvPoiType.setText("生活");
                        newholder.llGradeAndVisitNum.setVisibility(View.GONE);
                        break;
                    case "shopping":
                    case "scope":
                        newholder.ivItemIcon.setImageResource(R.drawable.shopping);
                        newholder.tvPoiType.setText("购物");
                        newholder.llGradeAndVisitNum.setVisibility(View.GONE);
                        break;
                    case "hotel":
                        newholder.ivItemIcon.setImageResource(R.drawable.hotel);
                        if (res.getPrice() > 300)
                            newholder.tvPoiType.setText("舒适型");
                        else
                            newholder.tvPoiType.setText("经济型");
                        if (res.getOverallRating() < 1) {
                            newholder.tvPoiPingfen.setText("暂无评分");
                            newholder.rbStartGrade.setVisibility(View.GONE);
                        }
                        newholder.rbStartGrade.setRating((float) res.getOverallRating());
                        newholder.tvVisitNum.setText("tel：" + res.getTelephone());
                        break;
                    case "cater":
                        newholder.ivItemIcon.setImageResource(R.drawable.catering);
                        newholder.tvPoiType.setText("饮食");
                        if (res.getOverallRating() < 1) {
                            newholder.tvPoiPingfen.setText("暂无评分");
                            newholder.rbStartGrade.setVisibility(View.GONE);
                        }
                        newholder.rbStartGrade.setRating((float) res.getOverallRating());
                        if (TextUtils.isEmpty(res.getShopHours()))
                            newholder.tvVisitNum.setText("营业时间：8:00-22:00");
                        else newholder.tvVisitNum.setText("营业时间：" + res.getShopHours());
                        break;
                    default:
                        break;
                }
            else {
                newholder.tvPoiType.setText("其他");
                newholder.llGradeAndVisitNum.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return poiDetailResultArrayList.size();
        }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_poi_tohere:
                context.startActivity(new Intent(context, NavigateActivity.class));
                break;
            case R.id.ll_sug_ll:
                context.startActivity(new Intent(context, LauncherActivity.class));
                break;
            case R.id.ll_clearall:

                break;
        }
    }
}