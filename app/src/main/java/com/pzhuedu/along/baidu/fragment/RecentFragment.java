package com.pzhuedu.along.baidu.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pzhuedu.along.baidu.R;
import com.pzhuedu.along.baidu.adapter.FragmentRecentAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by along on 2017/12/28.
 */

public class RecentFragment extends Fragment {
    @BindView(R.id.ll_back_to_home)
    LinearLayout llBackToHome;
    @BindView(R.id.ll_go_to_work)
    LinearLayout llGoToWork;
    @BindView(R.id.rv_fragment_recent)
    RecyclerView rvFragmentRecent;
    Unbinder unbinder;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recent, null);
        mContext = container.getContext();
        rvFragmentRecent = rootView.findViewById(R.id.rv_fragment_recent);
        rvFragmentRecent.setLayoutManager(new LinearLayoutManager(mContext));
        rvFragmentRecent.setAdapter(new FragmentRecentAdapter(mContext, new ArrayList<HashMap<String, String>>()));
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
