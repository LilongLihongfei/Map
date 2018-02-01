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

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentRecentAdapter extends RecyclerView.Adapter {
    TextView tvFragmentEndloc;
    private int TYPE_NORMOL = 101;
    private int TYPE_END = 102;
    private Context context;
    private ArrayList arrayList;

    public FragmentRecentAdapter(Context context, ArrayList<HashMap<String,String>> arrayList) {
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
        //

        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_recent, null);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder newholder = (MyViewHolder)holder;
       /* newholder.tvFragmentStloc.setText(((HashMap<String,String>)arrayList.get(position)).get("startloc"));
        newholder.tvFragmentEndloc.setText(((HashMap<String,String>)arrayList.get(position)).get("endloc"));*/
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1)
            return TYPE_END;
        return TYPE_NORMOL;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_fragment_stloc)
        TextView tvFragmentStloc;
        @BindView(R.id.tv_fragment_endloc)
        TextView tvFragmentEndloc;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}