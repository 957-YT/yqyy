package com.example.android.v;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.v.data.DBHelper;
import com.example.android.v.data.OpenHelper;
import com.example.android.v.service.SqlService;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class mvFragment extends Fragment {

    public static  ArrayList<Music> arrayList;
    private LoveAdapter loveAdapter;
    private LoveReceiver loveReceiver;
    private playService mplayService;
    public mvFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        loveAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loveReceiver = new LoveReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction("loveNotifi");
        getActivity().registerReceiver(loveReceiver, mFilter);

        DBHelper dbHelper = new DBHelper(getActivity());
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        SqlService sqlService = new SqlService(readableDatabase);
        mvFragment.arrayList = sqlService.query_all_love();
        readableDatabase.close();

        Intent bindIntent = new Intent(getActivity(), playService.class);
        getActivity().bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mv_fragment,container,false);

        ListView loveList = v.findViewById(R.id.love_list);
        loveAdapter = new LoveAdapter(getActivity(),mvFragment.arrayList);
        loveList.setAdapter(loveAdapter);
        loveList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent stopIntent = new Intent("android.intent.action.stopSeekBar");
                getActivity().sendBroadcast(stopIntent);
                Music currentMusic = loveAdapter.getItem(position);
                OpenHelper openHelper = new OpenHelper(getActivity());
                SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();
                SqlService sqlService = new SqlService(sqLiteDatabase);
                long i = sqlService.insert(currentMusic);
                sqLiteDatabase.close();
                if(i == -1){
                    mplayService.play(currentMusic);
                }else {
                    playService.dialogMusicList.add(currentMusic);
                    mplayService.play(currentMusic);
                }

            }
        });

        LinearLayout linearLayout = v.findViewById(R.id.love_playAll);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stopIntent = new Intent("android.intent.action.stopSeekBar");
                getActivity().sendBroadcast(stopIntent);
                playService.dialogMusicList.clear();
                OpenHelper openHelper = new OpenHelper(getActivity());
                SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();
                SqlService sqlService = new SqlService(sqLiteDatabase);
                sqlService.delete_all();
                playService.dialogMusicList.addAll(mvFragment.arrayList);
                sqlService.insert_all(mvFragment.arrayList);
                mplayService.play(mvFragment.arrayList.get(0));
            }
        });
        return v;
    }

    public class LoveReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("loveNotifi"))
            loveAdapter.notifyDataSetChanged();
        }
    }


    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            playService.MyBinder binder = (playService.MyBinder) iBinder;
            mplayService = binder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mplayService = null;
        }
    };

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(loveReceiver);
        getActivity().unbindService(serviceConnection);
        super.onDestroy();
    }
}
