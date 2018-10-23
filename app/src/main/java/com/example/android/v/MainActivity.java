package com.example.android.v;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.v.data.OpenHelper;
import com.example.android.v.service.NetService;
import com.example.android.v.service.SqlService;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public SeekBar seekBar;
    private playService mplayService;
    private playService.MyBinder binder;;
    public ImageButton playButton;
    private upUiReceiver upUireceiver;
    public Handler handler = new Handler();
    private TextView gmView;
    private TextView authorView;
    private ImageView txImageView;
    private ImageButton playListButton;
    private ProgressBar progressBar;


    @Override
    protected void onStart() {
        super.onStart();
        if (playService.tag){
            playButton.setImageResource(R.drawable.ic_zt);
        }else {
            playButton.setImageResource(R.drawable.ic_mv);
        }


    }

    public  static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isConn = isOnline(this);
        if (!isConn) {
            Toast.makeText(MainActivity.this, "当前没有网络连接!", Toast.LENGTH_SHORT).show();
        }

        ViewPager viewPager = findViewById(R.id.viewpager);
        CategoryAdapter adapter = new CategoryAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        int color = Color.rgb(238, 232, 205);
        tabLayout.setSelectedTabIndicatorColor(color);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_yinyue);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_love);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_search);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_about);

        playButton = findViewById(R.id.play);
        playListButton = findViewById(R.id.play_list);
        playButton.setOnClickListener(this);
        playListButton.setOnClickListener(this);


        seekBar = findViewById(R.id.prc);
        gmView = findViewById(R.id.gm);
        authorView = findViewById(R.id.author);
        txImageView = findViewById(R.id.tx);
        progressBar = findViewById(R.id.loading_spinner);

        gmView.setOnClickListener(this);
        authorView.setOnClickListener(this);
        txImageView.setOnClickListener(this);

        gmView.setClickable(false);
        authorView.setClickable(false);
        txImageView.setClickable(false);

        Intent bindIntent = new Intent(this, playService.class);
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        IntentFilter filter = new IntentFilter();
        upUireceiver = new upUiReceiver();
        filter.addAction("android.intent.action.UPUI");
        filter.addAction("android.intent.action.UPBUTTON");
        filter.addAction("android.intent.action.Loading");
        filter.addAction("android.intent.action.stopSeekBar");
        filter.addAction("android.intent.action.starSeekBar");
        filter.addAction("android.intent.action.cycle");
        registerReceiver(upUireceiver, filter);



    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                    if (!playService.tag) {
                        mplayService.playOrPause();
                        playButton.setImageResource(R.drawable.ic_zt);
                        playService.tag = true;
                    } else {
                        mplayService.playOrPause();
                        playButton.setImageResource(R.drawable.ic_mv);
                        playService.tag = false;
                    }
                    break;
            case R.id.play_list:
                BottomDialog bottomDialog = new BottomDialog();
                bottomDialog.show(getFragmentManager(), "");
                break;
            case R.id.gm:
            case R.id.author:
            case R.id.tx:
                Intent detailIntent = new Intent(this,detail.class);
                detailIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                detailIntent.putExtra("pic",mplayService.m.getBlurPic());
                detailIntent.putExtra("gm",mplayService.m.getSong_name());
                detailIntent.putExtra("au",mplayService.m.getAuthor());
                startActivity(detailIntent);
                break;
        }
    }


    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mplayService.mediaPlayer != null) {
                seekBar.setMax(mplayService.getDurations());
                seekBar.setProgress(mplayService.mediaPlayer.getCurrentPosition());
                handler.postDelayed(runnable, 1000);
            }
        }
    };


    public class upUiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction())
            {
                case "android.intent.action.UPUI":
                    ChangeUi();
                    break;
                case "android.intent.action.UPBUTTON":
                    playButton.setImageResource(R.drawable.ic_mv);
                    playService.tag = false;
                    handler.removeCallbacks(runnable);
                    gmView.setClickable(false);
                    authorView.setClickable(false);
                    txImageView.setClickable(false);
                    break;
                case "android.intent.action.Loading":
                    playButton.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    gmView.setClickable(false);
                    authorView.setClickable(false);
                    txImageView.setClickable(false);
                    break;
                case "android.intent.action.stopSeekBar":
                    handler.removeCallbacks(runnable);
                    playButton.setImageResource(R.drawable.ic_mv);
                    break;
                case "android.intent.action.starSeekBar":
                    handler.post(runnable);
                    gmView.setClickable(true);
                    authorView.setClickable(true);
                    txImageView.setClickable(true);
                    playButton.setImageResource(R.drawable.ic_zt);
                    break;
                case "android.intent.action.cycle":
                    playButton.setImageResource(R.drawable.ic_zt);
                    playService.tag = true;
                    break;
            }
        }
    }


    private void ChangeUi() {
        RequestOptions options = new RequestOptions()
                .error(R.drawable.default_tx)
                .placeholder(R.drawable.default_tx)
                .circleCrop()
                .priority(Priority.IMMEDIATE);
        Glide.with(this)
                .load(mplayService.m.getBlurPic())
                .thumbnail(0.1f)
                .apply(options)
                .into(txImageView);
        gmView.setClickable(true);
        authorView.setClickable(true);
        txImageView.setClickable(true);
        handler.post(runnable);
        playButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        playButton.setImageResource(R.drawable.ic_zt);
        playService.tag = true;
        gmView.setText(mplayService.m.getSong_name());
        authorView.setText(mplayService.m.getAuthor());
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


    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        unbindService(serviceConnection);
        unregisterReceiver(upUireceiver);


        super.onDestroy();
    }
//            获取并设置返回键的点击事件

    @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                moveTaskToBack(false);
                return true;
            }
            return super.onKeyDown(keyCode, event);
    }

}
