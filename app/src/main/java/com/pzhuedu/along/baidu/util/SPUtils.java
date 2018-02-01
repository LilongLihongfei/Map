package com.pzhuedu.along.baidu.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {
    private static SharedPreferences sp;
    private final static String SP_NAME = "map_config";



    public static void saveBoolean(Context context, String key, boolean value) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        sp.edit().putBoolean(key, value).commit();

    }

    public static boolean getBoolean(Context context, String key, boolean defaultvalue) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getBoolean(key, defaultvalue);
    }

    /**
     * 保存字符串
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveString(Context context, String key, String value) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        sp.edit().putString(key, value).commit();

    }
    public static String getString(Context context, String key, String defaultvalue) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getString(key, defaultvalue);
    }

    public static boolean contain(Context context,String key){
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.contains(key);
    }
}
