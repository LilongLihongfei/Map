package com.pzhuedu.along.baidu.receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.pzhuedu.along.baidu.R;

/**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {
        private Context mContext;
        public SDKReceiver(Context context){
            mContext = context;
        }
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            String content ="未知错误";
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                content = "key 验证出错";
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                content="网络出错";
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(LayoutInflater.from(mContext).inflate(R.layout.dialog_layout,null)).create().show();
        }
    }