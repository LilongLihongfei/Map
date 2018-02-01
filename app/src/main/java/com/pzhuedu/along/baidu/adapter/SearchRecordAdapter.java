package com.pzhuedu.along.baidu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.search.sug.SuggestionResult;
import com.pzhuedu.along.baidu.R;
import com.pzhuedu.along.baidu.SearchActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchRecordAdapter extends RecyclerView.Adapter {
    private int TYPE_NORMOL = 101;
    private int TYPE_END = 102;
    private Context context;
    private ArrayList arrayList;
    public SearchRecordAdapter(Context context, ArrayList<SuggestionResult.SuggestionInfo> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_END) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_end, null);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //删除所有记录
                }
            });
            return new RecyclerView.ViewHolder(rootView) {
            };
        }

        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_recycle, null);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1)
            return TYPE_END;
        return TYPE_NORMOL;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_item_icon)
        ImageView ivItemIcon;
        @BindView(R.id.tv_item_top)
        TextView tvItemTop;
        @BindView(R.id.tv_item_bottom)
        TextView tvItemBottom;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}