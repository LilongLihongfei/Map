package com.pzhuedu.along.baidu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pzhuedu.along.baidu.R;

import java.util.ArrayList;

/**
 * Created by along on 2017/12/25.
 */

public class LLRoute extends LinearLayout implements View.OnClickListener {
    private View mRootView;
    private TextView mRouteName;
    private TextView mRouteTime;
    private TextView mRouteDis;

    public boolean isChecked() {
        return isChecked;
    }

    private boolean isChecked;
    private String name;
    private OnLLRouteClickListener llRouteClickListener;

    public static ArrayList<LLRoute> getRouteArrayList() {
        return routeArrayList;
    }

    private static ArrayList<LLRoute> routeArrayList = new ArrayList();
    private static int mCurrentSelectedIndex = 0;
    private static int tagIndex = 0;

    public LLRoute(Context context) {
        this(context, null);
    }


    public LLRoute(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LLRoute);
        isChecked = typedArray.getBoolean(R.styleable.LLRoute_LLRoute_checked, false);
        name = typedArray.getString(R.styleable.LLRoute_LLRoute_name);
        mRootView = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.layou_llroute, this);
        mRouteName = mRootView.findViewById(R.id.tv_route1_name);
        mRouteTime = mRootView.findViewById(R.id.tv_rout1_time);
        mRouteDis = mRootView.findViewById(R.id.tv_rout1_distance);
        if(name!=null) setmRouteName(name);
        if (isChecked) setChecked();
        setClickable(true);
        setOnClickListener(this);
        if (!routeArrayList.contains(this)) {
            routeArrayList.add(this);
            tagIndex = (tagIndex)%3;
            setTag(tagIndex);
            tagIndex++;
            Log.d("lilong-------->", "LLRoute: "+"添加了一个LLRoute后：下一个LLRoute的索引为："+tagIndex);
        }
    }

    public void setmRouteName(String name) {
        mRouteName.setText(name);
    }

    public void setmRouteTime(String name) {
        mRouteTime.setText(name);
    }

    public void setmRouteDis(String name) {
        mRouteDis.setText(name);
    }

    public void setOLLRouteClickListener(OnLLRouteClickListener listener) {
        llRouteClickListener = listener;
    }

    public void setChecked() {
        isChecked = true;
        refreshState();
    }
    private void refreshState() {
        if(isChecked){
            mRouteDis.setTextColor(getResources().getColor(R.color.blue));
            mRouteTime.setTextColor(getResources().getColor(R.color.blue));
            mRouteName.setTextColor(getResources().getColor(R.color.blue));
            setBackground(getResources().getDrawable(R.drawable.rectangle_background_selector));
        }
        if(!isChecked){
            mRouteDis.setTextColor(getResources().getColor(R.color.black));
            mRouteTime.setTextColor(getResources().getColor(R.color.black));
            mRouteName.setTextColor(getResources().getColor(R.color.black));
            setBackground(getResources().getDrawable(R.drawable.rectangle_background));
        }
    }
    public void setNotChecked() {
        isChecked = false;
        refreshState();
    }

        private void setOtherNotChecked() {
            for (int i = 0; i < routeArrayList.size(); i++) {
                if(mCurrentSelectedIndex == i)
                    continue;
                ((LLRoute)routeArrayList.get(i)).setNotChecked();
            }
        }

    public static void removeAll(){
        routeArrayList.clear();
    }

    public interface OnLLRouteClickListener {
        void onClick(LLRoute llRoute);
    }

    @Override
    public void onClick(View v) {
        if (!isChecked) {
            setChecked();
            mCurrentSelectedIndex = (Integer) v.getTag();
            setOtherNotChecked();
        }
        if (llRouteClickListener != null)
            llRouteClickListener.onClick(this);
    }
}
