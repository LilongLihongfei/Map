package com.pzhuedu.along.baidu.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.pzhuedu.along.baidu.R;
import com.pzhuedu.along.baidu.adapter.TransitFragmentAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by along on 2017/12/29.
 */

public class TransitFragment extends Fragment {
    @BindView(R.id.rv_fragment_transit)
    RecyclerView rvFragmentTransit;

    private OnLoadingFinishListener loadingFinishListener;
    private TransitRouteResult result;
    //

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transit, null);
        ButterKnife.bind(this, rootView);
        result = (TransitRouteResult) getArguments().get("result");
        rvFragmentTransit.setLayoutManager(new LinearLayoutManager(container.getContext()));
        rvFragmentTransit.setAdapter(new TransitFragmentAdapter(container.getContext(),result.getRouteLines()));
        if(loadingFinishListener!=null)
            loadingFinishListener.onFinish();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void setLoadingFinishListener(OnLoadingFinishListener listener){
        loadingFinishListener = listener;
    }

    public interface OnLoadingFinishListener {
        void onFinish();
    }
}
