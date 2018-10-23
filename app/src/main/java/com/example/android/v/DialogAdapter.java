package com.example.android.v;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.v.data.OpenHelper;
import com.example.android.v.service.SqlService;

import java.util.ArrayList;
import java.util.List;

public class DialogAdapter extends ArrayAdapter<Music> {

    private int mCurrentItem=0;
    private boolean isClick=false;

    public DialogAdapter(@NonNull Context context, ArrayList<Music> musicArrayList) {
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

        ImageView hornView = rootview.findViewById(R.id.horn);

        TextView song_nameview = rootview.findViewById(R.id.song_name);
        song_nameview.setText(currentMusic.getSong_name());
        TextView authorview = rootview.findViewById(R.id.author);
        authorview.setText(currentMusic.getAuthor());

        ImageView imageView = rootview.findViewById(R.id.delete_single);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenHelper openHelper = new OpenHelper(getContext());
                SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();
                SqlService sqlService = new SqlService(sqLiteDatabase);
                boolean b = sqlService.delete_single(currentMusic);
                sqLiteDatabase.close();
                if (b){
                    playService.dialogMusicList.remove(position);
                    Intent intent = new Intent("android.intent.action.delete");
                    getContext().sendBroadcast(intent);
                }else {
                    Toast.makeText(getContext(),"删除失败",Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (mCurrentItem== position &&isClick){
            song_nameview.setTextColor(Color.parseColor("#F0E68C"));
            authorview.setTextColor(Color.parseColor("#F0E68C"));
            hornView.setVisibility(View.VISIBLE);
        }else {
            song_nameview.setTextColor(Color.parseColor("#99000000"));
            authorview.setTextColor(Color.parseColor("#80808080"));
            hornView.setVisibility(View.GONE);
        }



        return rootview;
    }



    public void setCurrentItem(int currentItem){
        this.mCurrentItem=currentItem;
    }

    public void setClick(boolean click){
        this.isClick=click;
    }


}

