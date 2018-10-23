package com.example.android.v.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OpenHelper extends SQLiteOpenHelper {

    private static final String name = "test.db";
    private static final int version = 1;

    public OpenHelper(Context context) {
        super(context, name,null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+
        "music(_id INTEGER primary key AUTOINCREMENT,"+"music_id INTEGER,"+
        "song_name varchar(32),author varchar(32),tx varchar(64))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
