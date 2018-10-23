package com.example.android.v;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.v.data.OpenHelper;
import com.example.android.v.service.NetService;
import com.example.android.v.service.SqlService;

import java.util.ArrayList;

public class musicList extends AppCompatActivity {
    private playService mplayService;
    private playService.MyBinder binder;
    private musicAdapter musicAdapter;
    private SQLiteDatabase sqLiteDatabase;
    private ProgressDialog waitingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_list);


        boolean isConn = MainActivity.isOnline(this);
        if (!isConn) {
            Toast.makeText(musicList.this, "当前没有网络连接!", Toast.LENGTH_SHORT).show();
        }

        ListView listView = findViewById(R.id.list);
        final ArrayList<Music> musicArrayList = new ArrayList<>();
        musicAdapter = new musicAdapter(this, musicArrayList);
        listView.setAdapter(musicAdapter);

        Intent bindIntent = new Intent(this, playService.class);
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        ImageView clearView = findViewById(R.id.what_b);
        ImageView fuzzView = findViewById(R.id.what_b_fuzzy);

        TextView play_all = findViewById(R.id.play_all);

        Intent intent_accept = getIntent();
        Bundle bundle = intent_accept.getExtras();
        String gd_id = bundle.getString("gd_id");
        int what_b = bundle.getInt("what_b");

        switch (what_b) {
            case 0:
                clearView.setImageResource(R.drawable.max_bsy);
                fuzzView.setImageResource(R.drawable.max_bsy_fuzzy);
                break;
            case 1:
                clearView.setImageResource(R.drawable.max_xgb);
                fuzzView.setImageResource(R.drawable.max_xgb_fuzzy);
                break;
            case 2:
                clearView.setImageResource(R.drawable.max_ycb);
                fuzzView.setImageResource(R.drawable.max_ycb_fuzzy);
                break;
            case 3:
                clearView.setImageResource(R.drawable.max_rgb);
                fuzzView.setImageResource(R.drawable.max_rgb_fuzzy);
                break;
            case 4:
                clearView.setImageResource(R.drawable.max_dyb);
                fuzzView.setImageResource(R.drawable.max_dyb_fuzzy);
                break;
            case 5:
                clearView.setImageResource(R.drawable.max_dy);
                fuzzView.setImageResource(R.drawable.max_dy_fuzzy);
                break;
            case 6:
                fuzzView.setImageResource(R.drawable.max_itunes);
                break;
            case 7:
                fuzzView.setImageResource(R.drawable.max_billboard);
                break;
            case 8:
                fuzzView.setImageResource(R.drawable.max_oricon);
                break;
            case 9:
                fuzzView.setImageResource(R.drawable.chinese);
                break;
            case 10:
                fuzzView.setImageResource(R.drawable.english);
                break;
            case 11:

                fuzzView.setImageResource(R.drawable.jpan);
                break;
            case 12:
                fuzzView.setImageResource(R.drawable.korea);
                break;
            case 13:
                fuzzView.setImageResource(R.drawable.cantonese);
                break;
            case 14:
                fuzzView.setImageResource(R.drawable.german);
                break;
        }

        OpenHelper openHelper = new OpenHelper(this);
        sqLiteDatabase = openHelper.getWritableDatabase();
        final SqlService sqlService = new SqlService(sqLiteDatabase);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Music currentMusic = musicAdapter.getItem(position);
                long i =sqlService.insert(currentMusic);
                if(i == -1){
                    mplayService.play(currentMusic);
                }else {
                }
                for (int a = 0; a < playService.dialogMusicList.size(); a++) {
                    if (playService.dialogMusicList.get(a).getId() == currentMusic.getId()) {
                        return;
                    }
                }
                playService.dialogMusicList.add(currentMusic);
                Intent intent = new Intent("android.intent.action.insert");
                sendBroadcast(intent);
                Intent stopIntent = new Intent("android.intent.action.stopSeekBar");
                sendBroadcast(stopIntent);
                mplayService.play(currentMusic);
            }
        });


        play_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicArrayList.isEmpty()){
                    Toast.makeText(musicList.this,"还没加载完成哦,请等待!",Toast.LENGTH_SHORT).show();
                    return;
                }
                playService.dialogMusicList.clear();
                playService.dialogMusicList.addAll(musicArrayList);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitingDialog.cancel();
                        Toast.makeText(musicList.this, "添加成功", Toast.LENGTH_SHORT).show();
                    }
                },5000);
                showWaitingDialog();
                sqlService.delete_all();
                sqlService.insert_all(musicArrayList);

                mplayService.play(musicArrayList.get(0));
            }
        });

        ImageView upView = findViewById(R.id.list_up);
        upView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        NetService.getNetUrl(musicArrayList, musicAdapter, gd_id);
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (playService.MyBinder) iBinder;
            mplayService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mplayService = null;
        }
    };

    public  void showWaitingDialog() {
        /* 等待Dialog具有屏蔽其他控件的交互能力
         * @setCancelable 为使屏幕不可点击，设置为不可取消(false)
         * 下载等事件完成后，主动调用函数关闭该Dialog
         */
        waitingDialog= new ProgressDialog(musicList.this);
        waitingDialog.setTitle("");
        waitingDialog.setMessage("等待中...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }

    @Override
    protected void onDestroy() {
        musicAdapter.sqLiteDatabase.close();
        musicAdapter.sqLiteDatabase1.close();
        this.sqLiteDatabase.close();
        unbindService(serviceConnection);
        super.onDestroy();
    }
}
