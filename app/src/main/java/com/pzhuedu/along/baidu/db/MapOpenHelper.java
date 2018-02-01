package com.pzhuedu.along.baidu.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by along on 2018/1/29.
 */

public class MapOpenHelper extends SQLiteOpenHelper {
    private final String CREATE_TABLE_SQL =
            "create table chachetable (_id integer primary key autoincrement,";

    public MapOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
