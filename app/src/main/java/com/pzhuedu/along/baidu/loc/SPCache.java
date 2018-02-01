package com.pzhuedu.along.baidu.loc;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by along on 2017/11/23.
 */

public class SPCache extends CachePolicy{
    private static final String SP_NAME ="";
    @Override
    public boolean useCache() {
        return true;
    }

    @Override
    public void doCache(Context context,Loc loc) {
        //使用缓存策略 把获取到的位置信息对象 存储在SP中
        Log.d("SPCache--------->","获取数据成功 有缓存策略 进行一次SP缓存");
        Gson gson = new Gson();
        int index =context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt("INDEX",0);
        index++;
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putString("INDEX"+index, System.currentTimeMillis()+"#"+gson.toJson(loc)).commit();
        Log.d("SPCache--------->","将要存储的Json数据: "+ System.currentTimeMillis()+":"+gson.toJson(loc));
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putInt("INDEX", index).commit();
        Log.d("SPCache--------->","当前缓存中最后一条缓存信息的索引: "+index);
    }
}
