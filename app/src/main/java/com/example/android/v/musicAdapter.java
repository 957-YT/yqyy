package com.example.android.v;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.v.data.DBHelper;
import com.example.android.v.data.OpenHelper;
import com.example.android.v.service.SqlService;

import java.util.ArrayList;

public class musicAdapter extends ArrayAdapter<Music> {

    private OpenHelper openHelper = new OpenHelper(getContext());
    public SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();
    private DBHelper dbHelper = new DBHelper(getContext());
    public SQLiteDatabase sqLiteDatabase1 = dbHelper.getWritableDatabase();


    public musicAdapter(@NonNull Context context, ArrayList<Music> musicArrayList) {
        super(context,0, musicArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.music_item, parent, false);
        }
        final Music currentMusic = getItem(position);

        TextView idView = listItemView.findViewById(R.id.song_name);
        idView.setText(currentMusic.getSong_name());
        TextView authorview = listItemView.findViewById(R.id.author);
        authorview.setText(currentMusic.getAuthor());
        ImageView addView = listItemView.findViewById(R.id.add);
        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               SqlService sqlService = new SqlService(sqLiteDatabase);
                long i =sqlService.insert(currentMusic);
                if(i == -1){
                    Toast.makeText(getContext(), "播放列表已存在该歌曲!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(),"添加成功",Toast.LENGTH_SHORT).show();
                }
                for (int a = 0; a < playService.dialogMusicList.size(); a++) {
                    if (playService.dialogMusicList.get(a).getId() == currentMusic.getId()) {
                        return;
                    }
                }
                playService.dialogMusicList.add(currentMusic);
                Intent intent = new Intent("android.intent.action.insert");
                getContext().sendBroadcast(intent);
            }
        });

        ImageView loveView = listItemView.findViewById(R.id.love);
        loveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SqlService sqlService = new SqlService(sqLiteDatabase1);
                long i = sqlService.insert_love(currentMusic);
                if(i == -1){
                    Toast.makeText(getContext(), "喜爱列表已存在该歌曲!", Toast.LENGTH_SHORT).show();
                }else {
                    mvFragment.arrayList.add(currentMusic);
                    Intent intent = new Intent("loveNotifi");
                    getContext().sendBroadcast(intent);
                    Toast.makeText(getContext(),"添加成功",Toast.LENGTH_SHORT).show();
                }
            }
        });


        return listItemView;
    }


}
