package com.pzhuedu.along.baidu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pzhuedu.along.baidu.R;

import java.util.ArrayList;

/**
 * Created by along on 2017/12/12.
 */

public class LLCheck extends FrameLayout implements View.OnClickListener{
    private int mBackgroundColor;
    private int mTextColor;
    private boolean isChecked;
    private int mResId;
    private String mText;
    private OnLLCheckclickListener onLLCheckclickListener;
    private static int mCurrent_select_index = 1;
    private static int mTag = 0;
    private LinearLayout mView_background;
    private LinearLayout mView_forgend;
    private ImageView mImageView;
    private TextView mTextView;
    private static ArrayList arrayList_views = new ArrayList();
    public LLCheck(Context context) {
        this(context,null);
    }

    public LLCheck(Context context, @Nullable AttributeSet attrs) {
        super(context,attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LLCheck);
        mBackgroundColor = typedArray.getColor(R.styleable.LLCheck_LLCheck_backgroundColor,getResources().getColor(R.color.route_fill));
        mTextColor = typedArray.getColor(R.styleable.LLCheck_LLCheck_textColor,getResources().getColor(R.color.white));
        isChecked = typedArray.getBoolean(R.styleable.LLCheck_LLCheck_checked,false);
        mResId = typedArray.getResourceId(R.styleable.LLCheck_LLCheck_imageSrc,R.drawable.add);
        mText = typedArray.getString(R.styleable.LLCheck_LLCheck_Text);
        setClickable(true);
        mView_background =(LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.llcheck_checked,null);
        mView_forgend =(LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.llcheck_notchecked,null);
        mImageView  = (ImageView) mView_forgend.findViewById(R.id.iv_llcheck_source);
        mTextView = (TextView) mView_background.findViewById(R.id.tv_text);
        mImageView.setImageResource(mResId);
        mTextView.setText(mText);
        mTextView.setTextColor(mTextColor);
        addView(mView_background);
        addView(mView_forgend);
        refreshState();
        setOnClickListener(this);
        setTag(mTag++);
        arrayList_views.add(this);
    }

    private void refreshState() {
        if(isChecked){
            mView_background.setVisibility(VISIBLE);
            mView_forgend.setVisibility(GONE);
        }
        if(!isChecked){
            mView_background.setVisibility(GONE);
            mView_forgend.setVisibility(VISIBLE);
        }
    }


    private void setChecked(){
        isChecked = true;
        refreshState();
    }

    private void setNotChecked(){
        isChecked = false;
        refreshState();
    }

    @Override
    public void onClick(View v) {
        if(!isChecked){
            setChecked();
            mCurrent_select_index = (Integer) v.getTag();
            setOtherNotChecked();
        }
        if(onLLCheckclickListener!=null)
            onLLCheckclickListener.onclick(this);
    }

    private void setOtherNotChecked() {
        for (int i = 0; i < arrayList_views.size(); i++) {
            if(mCurrent_select_index == i)
                continue;
            ((LLCheck)arrayList_views.get(i)).setNotChecked();
        }
    }

    public void setOnLLCheckclickListener(OnLLCheckclickListener listener){
        onLLCheckclickListener = listener;
    }

    public interface OnLLCheckclickListener{
         void onclick(LLCheck llCheck);
    }
}
