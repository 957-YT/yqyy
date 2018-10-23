package com.example.android.v;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;

public class detail extends AppCompatActivity implements View.OnClickListener {

    private playService mplayService;
    private Handler handler = new Handler();
    private SeekBar seekBar;
    private upUiReceiver upUiReceiver;
    private ImageView txView;
    private ImageView stopView;
    private ProgressBar detailBar;

    private TextView detail_gm;
    private TextView detail_au;


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        String pic = intent.getStringExtra("pic");
        String gm = intent.getStringExtra("gm");
        String au = intent.getStringExtra("au");

        Intent bindIntent = new Intent(this, playService.class);
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        txView = findViewById(R.id.detail_tx);
        ImageView leftView = findViewById(R.id.detail_left);
        stopView = findViewById(R.id.detail_stop);
        ImageView rightView = findViewById(R.id.detail_right);
        ImageView playListView = findViewById(R.id.detail_list);
        seekBar = findViewById(R.id.seekBar);
        detailBar = findViewById(R.id.detail_prc);
        detail_gm = findViewById(R.id.detail_gm);
        detail_au = findViewById(R.id.detail_author);
        ImageView upView = findViewById(R.id.up);
        upView.setOnClickListener(this);

        leftView.setOnClickListener(this);
        stopView.setOnClickListener(this);
        rightView.setOnClickListener(this);
        playListView.setOnClickListener(this);

        handler.postDelayed(runnable,300);

        if (playService.tag){
            stopView.setImageResource(R.drawable.ic_zt);
        }else {
            stopView.setImageResource(R.drawable.ic_mv);
        }

        IntentFilter filter = new IntentFilter();
        upUiReceiver = new upUiReceiver();
        filter.addAction("android.intent.action.UPUI");
        filter.addAction("android.intent.action.UPBUTTON");
        filter.addAction("android.intent.action.Loading");
        filter.addAction("android.intent.action.stopSeekBar");
        filter.addAction("android.intent.action.starSeekBar");
        filter.addAction("android.intent.action.cycle");
        registerReceiver(upUiReceiver, filter);

        Glide.with(detail.this)
                .load(pic)
                .apply(RequestOptions.circleCropTransform())
                .into(txView);
        detail_gm.setText(gm);
        detail_gm.setSelected(true);
        detail_au.setText(au);

        seekBar.getThumb().setColorFilter(Color.parseColor("#F0E68C"), PorterDuff.Mode.SRC_ATOP);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mplayService.mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.detail_left:
                boolean bo = mplayService.previous();
                if (!bo){
                    Toast.makeText(detail.this,"当前已是第一首!",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.detail_stop:
                if (playService.tag) {
                    mplayService.playOrPause();
                    stopView.setImageResource(R.drawable.ic_mv);
                    playService.tag = false;
                } else {
                    mplayService.playOrPause();
                    stopView.setImageResource(R.drawable.ic_zt);
                    playService.tag = true;
                }
                break;
            case R.id.detail_right:
                boolean b = mplayService.next();
                if (!b){
                    Toast.makeText(detail.this,"当前已是最后一首!",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.detail_list:
                BottomDialog bottomDialog = new BottomDialog();
                bottomDialog.show(getFragmentManager(), "");
                break;
            case R.id.up:
                finish();
        }

    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
                seekBar.setMax(mplayService.getDurations());
                seekBar.setProgress(mplayService.mediaPlayer.getCurrentPosition());
                handler.postDelayed(runnable, 1000);
        }
    };

    private class upUiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction())
            {
                case "android.intent.action.UPUI":
                    RequestOptions options = new RequestOptions()
                            .error(R.drawable.default_tx)
                            .placeholder(R.drawable.default_tx)
                            .circleCrop();
                    Glide.with(detail.this)
                            .load(mplayService.m.getBlurPic())
                            .apply(options)
                            .into(txView);
                    handler.postDelayed(runnable,300);
                    stopView.setVisibility(View.VISIBLE);
                    detailBar.setVisibility(View.GONE);
                    stopView.setImageResource(R.drawable.ic_zt);
                    mplayService.tag = true;
                    detail_gm.setText(mplayService.m.getSong_name());
                    detail_au.setText(mplayService.m.getAuthor());
                    break;
                case "android.intent.action.UPBUTTON":
                    stopView.setImageResource(R.drawable.ic_mv);
                    mplayService.tag = false;
                    handler.removeCallbacks(runnable);
                    break;
                case "android.intent.action.Loading":
                    stopView.setVisibility(View.INVISIBLE);
                    detailBar.setVisibility(View.VISIBLE);
                    break;
                case "android.intent.action.stopSeekBar":
                    handler.removeCallbacks(runnable);
                    stopView.setImageResource(R.drawable.ic_mv);
                    break;
                case "android.intent.action.starSeekBar":
                    handler.post(runnable);
                    stopView.setImageResource(R.drawable.ic_zt);
                    break;
                case "android.intent.action.cycle":
                    stopView.setImageResource(R.drawable.ic_zt);

            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(upUiReceiver);
        handler.removeCallbacks(runnable);
        unbindService(serviceConnection);
//        Glide.get(this).clearMemory();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Glide.get(detail.this).clearDiskCache();
//            }
//        }).start();
    }
}
