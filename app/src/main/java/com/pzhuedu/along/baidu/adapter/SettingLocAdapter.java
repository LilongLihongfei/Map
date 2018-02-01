package com.pzhuedu.along.baidu.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.pzhuedu.along.baidu.R;
import com.pzhuedu.along.baidu.SettingLocActivity;
import com.pzhuedu.along.baidu.util.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by along on 2017/12/21.
 */

public class SettingLocAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<PoiDetailResult> poiDetailResults;

    public SettingLocAdapter(Context context, ArrayList arrayList) {
        mContext = context;
        poiDetailResults = arrayList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_item_icon)
        ImageView ivItemIcon;
        @BindView(R.id.tv_item_top)
        TextView tvItemTop;
        @BindView(R.id.tv_item_bottom)
        TextView tvItemBottom;
        @BindView(R.id.rl_item_view)
        RelativeLayout rlItemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_rv_settingloc, null);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder newholder = (MyViewHolder)holder;
        final PoiDetailResult result = poiDetailResults.get(position);
        newholder.tvItemTop.setText(result.getName());
        newholder.tvItemBottom.setText(result.getAddress());
        newholder.rlItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent();
                intent.putExtra(Constants.INTENT_NAME,result.name);
                intent.putExtra(Constants.LATLNG,result.location);
                ((Activity)mContext).setResult(0,intent);
                ((Activity)mContext).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return poiDetailResults.size();
    }
}
