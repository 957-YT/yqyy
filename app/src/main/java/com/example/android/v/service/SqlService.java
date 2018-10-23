package com.example.android.v.service;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import com.example.android.v.Music;
import com.example.android.v.data.OpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SqlService {
    private SQLiteDatabase db;

    public SqlService(SQLiteDatabase sqLiteDatabase) {
        this.db = sqLiteDatabase;
    }

    public long insert(Music music) {
        List<Music> musicList = query_all();
        for (int i = 0; i < musicList.size(); i++) {
            if (musicList.get(i).getId() == music.getId()) {
                return -1;
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("music_id", music.getId());
        contentValues.put("song_name", music.getSong_name());
        contentValues.put("author", music.getAuthor());
        contentValues.put("tx", music.getBlurPic());
        long insertResult = db.insert("music", null, contentValues);
        if(insertResult == -1) {
            return -1;
        }
        return insertResult;
}
    public long insert_love(Music music) {
        List<Music> musicList = query_all_love();
        for (int i = 0; i < musicList.size(); i++) {
            if (musicList.get(i).getId() == music.getId()) {
                return -1;
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("music_id", music.getId());
        contentValues.put("song_name", music.getSong_name());
        contentValues.put("author", music.getAuthor());
        contentValues.put("tx", music.getBlurPic());
        long insertResult = db.insert("love", null, contentValues);
        if(insertResult == -1) {
            return -1;
        }
        return insertResult;
    }

private long insertResult;

    public long insert_all(ArrayList<Music> musicArrayList) {
        for (int i = 0; i < musicArrayList.size(); i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("music_id", musicArrayList.get(i).getId());
                contentValues.put("song_name", musicArrayList.get(i).getSong_name());
                contentValues.put("author", musicArrayList.get(i).getAuthor());
                contentValues.put("tx", musicArrayList.get(i).getBlurPic());
                insertResult = db.insert("music", null, contentValues);
        }
      return insertResult;
    }

    public boolean delete_single(Music music) {
        int deleteResult = db.delete("music", "music_id=?", new String[]{music.getId() + ""});
        if (deleteResult == 0) {
            return false;
        }
        return true;
    }

    public boolean delete_single_love(Music music) {
        int deleteResult = db.delete("love", "music_id=?", new String[]{music.getId() + ""});
        if (deleteResult == 0) {
            return false;
        }
        return true;
    }

    public boolean delete_all() {
        int deleteResult = db.delete("music", null, null);
        if (deleteResult == 0) {
            return false;
        }
        return true;
    }

    public ArrayList<Music> query_all() {
        ArrayList<Music> list = new ArrayList<>();
        Cursor cursor = db.query("music", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Music music = new Music(cursor.getInt(cursor.getColumnIndex("music_id")),
                    cursor.getString(cursor.getColumnIndex("song_name")),
                    cursor.getString(cursor.getColumnIndex("author")),
                    cursor.getString(cursor.getColumnIndex("tx")));
            list.add(music);
        }
        return list;
    }

    public ArrayList<Music> query_all_love() {
        ArrayList<Music> list = new ArrayList<>();
        Cursor cursor = db.query("love", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Music music = new Music(cursor.getInt(cursor.getColumnIndex("music_id")),
                    cursor.getString(cursor.getColumnIndex("song_name")),
                    cursor.getString(cursor.getColumnIndex("author")),
                    cursor.getString(cursor.getColumnIndex("tx")));
            list.add(music);
        }
        return list;
    }
}
