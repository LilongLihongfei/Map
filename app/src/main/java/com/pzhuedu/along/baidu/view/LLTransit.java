package com.pzhuedu.along.baidu.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.method.ArrowKeyMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pzhuedu.along.baidu.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by along on 2017/12/29.
 */

public class LLTransit extends LinearLayout {
    private ImageView ivicon;
    private ImageView arrow;
    private TextView num;
    public LLTransit(Context context,int resId,String busname,boolean isVisible) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.lltransit_layout, null);
        ivicon = view.findViewById(R.id.iv_transit_bus);
        arrow = view.findViewById(R.id.iv_right_arrow);
        num = view.findViewById(R.id.tv_lltransit_num);
        if(isVisible) arrow.setVisibility(VISIBLE);
        if(resId>0) {
            setIvicon(resId);
        }
        setNum(busname);
        addView(view);
    }

    private void setIvicon(int res){
        ivicon.setImageResource(res);
    }

    private void setNum(String name){
        num.setText(name);
    }
}
