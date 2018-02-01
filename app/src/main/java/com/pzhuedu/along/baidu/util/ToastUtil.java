package com.pzhuedu.along.baidu.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class ToastUtil {
    private static Toast toast;
    public static void show(Context context, String text){
        if(toast == null)
        toast = Toasty.error(context,text,Toast.LENGTH_SHORT);
        toast.show();
    }
}