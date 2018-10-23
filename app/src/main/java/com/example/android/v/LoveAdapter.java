package com.example.android.v;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.v.data.DBHelper;
import com.example.android.v.service.SqlService;

import java.util.ArrayList;

public class LoveAdapter extends ArrayAdapter<Music> {

    public LoveAdapter(@NonNull Context context, ArrayList<Music> musicArrayList) {
        super(context,0, musicArrayList);
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rootview = convertView;


        if (rootview == null) {
            rootview = LayoutInflater.from(getContext()).inflate(
                    R.layout.dialog_item, parent, false);
        }
        final Music currentMusic = getItem(position);

        TextView song_nameview = rootview.findViewById(R.id.song_name);
        song_nameview.setText(currentMusic.getSong_name());
        TextView authorview = rootview.findViewById(R.id.author);
        authorview.setText(currentMusic.getAuthor());

        ImageView imageView = rootview.findViewById(R.id.delete_single);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper dbHelper = new DBHelper(getContext());
                SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
                SqlService sqlService = new SqlService(sqLiteDatabase);
                boolean b = sqlService.delete_single_love(currentMusic);
                sqLiteDatabase.close();
                if (b){
                    mvFragment.arrayList.remove(position);
                    Intent intent = new Intent("loveNotifi");
                    getContext().sendBroadcast(intent);
                }else {
                    Toast.makeText(getContext(),"删除失败",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootview;
    }



}
