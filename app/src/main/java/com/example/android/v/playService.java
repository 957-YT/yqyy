package com.example.android.v;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.android.v.data.OpenHelper;
import com.example.android.v.service.SqlService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class playService extends Service {

    public String url;
    public MediaPlayer mediaPlayer = new MediaPlayer();
    private IBinder binder = new MyBinder();
    public static boolean tag = false;
    private int duration = 0;
    public Music m;
    public static int position = 0;
    public static ArrayList<Music> dialogMusicList;
    private OpenHelper openHelper;
    private SQLiteDatabase sqLiteDatabase;
    private SqlService sqlService;
    public static int playmode = 0;
    private WifiManager.WifiLock wifiLock;

    private  final int CONTENT = 1023;
    private  final int PREVIOUS = 1027;
    private  final int NEXT = 1025;
    private  final int PLAY = 1026;
    private  final int NOTIFICATION_PENDINGINTENT_ID = 1;// 是用来标记Notifaction，可用于更新，删除Notifition
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private RemoteViews views;
    private PlayerReceiver playerReceiver;
    private Notification notification;
    private boolean sf = true;

    public playService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        playerReceiver = new PlayerReceiver();
        IntentFilter mFilter = new IntentFilter();

        mFilter.addAction("previousMusic1");
        mFilter.addAction("nextMusic1");
        mFilter.addAction("playMusic1");
        registerReceiver(playerReceiver, mFilter);


        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(this,MainActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// 关键的一步，设置启动模式，两种情况
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, CONTENT, intent, 0);


        // 自定义布局
        views = new RemoteViews(getPackageName(), R.layout.notification_layout);

        //上一首
        Intent intentPrevious = new Intent("previousMusic1");
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(this, PREVIOUS, intentPrevious, 0);
        views.setOnClickPendingIntent(R.id.notifi_left, previousPendingIntent);

        // 下一首
        Intent intentNext = new Intent("nextMusic1");
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, NEXT, intentNext, 0);
        views.setOnClickPendingIntent(R.id.notifi_next, nextPendingIntent);

        // 暂停/播放
        Intent intentPlay = new Intent("playMusic1");
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, PLAY, intentPlay, 0);
        views.setOnClickPendingIntent(R.id.notifi_pause, playPendingIntent);


        builder = new NotificationCompat.Builder(this)
                // 设置小图标
                .setSmallIcon(R.drawable.ic_bg)
                // 点击通知后自动清除
                .setAutoCancel(false)
                //一直运行
                .setOngoing(true)
                // 设置点击通知效果
                .setContentIntent(contentPendingIntent)
                // 自定义视图
                .setContent(views);

        // 获取NotificationManager实例
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            String channelID = "1";
            String channelName = "音乐通知控制台";
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            builder.setChannelId(channelID);
            notificationManager.createNotificationChannel(channel);
        }



    }

    @Override
    public IBinder onBind(Intent intent) {
        dialogMusicList = new ArrayList<>();
        openHelper = new OpenHelper(getBaseContext());
        sqLiteDatabase = openHelper.getReadableDatabase();
        sqlService = new SqlService(sqLiteDatabase);
        dialogMusicList = sqlService.query_all();
        sqLiteDatabase.close();
        mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "wifilock");
        wifiLock.acquire();
        return binder;
    }

    public void playOrPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playService.tag = false;
            Intent stopIntent = new Intent("android.intent.action.stopSeekBar");
            sendBroadcast(stopIntent);
        } else {
            playService.tag = true;
            mediaPlayer.start();
            Intent starIntent = new Intent("android.intent.action.starSeekBar");
            sendBroadcast(starIntent);
        }
        updateNotification1();
    }


    public void play(Music currentMusic) {
        if (mediaPlayer != null) {
            playService.this.mediaPlayer.stop();
            playService.this.mediaPlayer.release();
            playService.this.mediaPlayer = null;
        }

        url = "http://music.163.com/song/media/outer/url?id=" + currentMusic.getId() + ".mp3";
        m = currentMusic;
        setMediaPlayer();
        for (int i = 0; i < dialogMusicList.size(); i++) {
            if (currentMusic.getId() == dialogMusicList.get(i).getId()) {
                position = i;
                break;
            }
        }
    }

    public boolean next() {
        if (position < dialogMusicList.size() - 1) {
            Music nextmusic = BottomDialog.dialogAdapter.getItem(++position);
            play(nextmusic);
            return true;
        } else
            return false;
    }

    public void random() {
        Random random = new Random();
        position = random.nextInt(dialogMusicList.size());
        Music randomMusic = BottomDialog.dialogAdapter.getItem(position);
        play(randomMusic);
    }

    public void cycle() {
        mediaPlayer.start();
        Intent starIntent = new Intent("android.intent.action.starSeekBar");
        sendBroadcast(starIntent);
        Intent cycleIntent = new Intent("android.intent.action.cycle");
        sendBroadcast(cycleIntent);
    }

    public boolean previous() {
        if (position != 0) {
            Music premusic = BottomDialog.dialogAdapter.getItem(--position);
            play(premusic);
            return true;
        } else
            return false;
    }

    public void setMediaPlayer() {
        try {
            if (url != null) {
                mediaPlayer = new MediaPlayer();
//                    mediaPlayer.reset();
                Uri uri = Uri.parse(url);
                Intent intent = new Intent("android.intent.action.Loading");
                sendBroadcast(intent);
                try {
                    mediaPlayer.setDataSource(playService.this, uri);
                } catch (IllegalStateException e) {
//                        e.printStackTrace();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(playService.this, uri);
                }
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        // 前台服务，只开启一次
                        if(sf) {
                            views.setImageViewResource(R.id.notifi_pause, R.drawable.ic_zt);
                            views.setTextViewText(R.id.notifi_name, m.getSong_name());
                            views.setTextViewText(R.id.notifi_author, m.getAuthor());
                            new getImageCacheAsyncTask(playService.this).execute(m.getBlurPic());
                            notification = builder.build();
                            startForeground(NOTIFICATION_PENDINGINTENT_ID, notification);
                            sf = false;
                        }
                            updateNotification();
                        duration = mediaPlayer.getDuration();
                        Intent intent = new Intent("android.intent.action.UPUI");
                        sendBroadcast(intent);
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Intent intent = new Intent("android.intent.action.UPBUTTON");
                        sendBroadcast(intent);
                        switch (playmode) {
                            case 0:
                                if (position < dialogMusicList.size() - 1) {
                                    next();
                                }
                                break;
                            case 1:
                                random();
                                break;
                            case 2:
                                cycle();
                                break;
                        }

                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                        mediaPlayer.reset();
                        return false;
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public int getDurations() {
        return duration;
    }

    public class MyBinder extends Binder {

        public playService getService() {
            return playService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (notificationManager != null)
            notificationManager.cancel(NOTIFICATION_PENDINGINTENT_ID);
        stopForeground(true);
        stopSelf();
    }

    public class PlayerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case "playMusic1":
                    if (playService.tag) {
                        playOrPause();
                        playService.tag = false;
                        updateNotification1();
                    } else {
                        playOrPause();
                        playService.tag = true;
                        updateNotification1();
                    }
                    break;
                case "nextMusic1":
                    next();
                    updateNotification();
                    break;
                case "previousMusic1":
                    previous();
                    updateNotification();
                    break;
            }
        }
    }

    private void updateNotification() {
        if (views != null) {
            views.setTextViewText(R.id.notifi_name, m.getSong_name());
            views.setTextViewText(R.id.notifi_author, m.getAuthor());

            new getImageCacheAsyncTask(this).execute(m.getBlurPic());
        }

        // 刷新notification
        notificationManager.notify(NOTIFICATION_PENDINGINTENT_ID, builder.build());
    }

    private void updateNotification1() {
        if (playService.tag) {
            views.setImageViewResource(R.id.notifi_pause, R.drawable.ic_zt);
        } else {
            views.setImageViewResource(R.id.notifi_pause, R.drawable.ic_mv);
        }
        // 刷新notification
        notificationManager.notify(NOTIFICATION_PENDINGINTENT_ID, builder.build());
    }


    private class getImageCacheAsyncTask extends AsyncTask<String, Bitmap, File> {
        private final Context context;

        public getImageCacheAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected File doInBackground(String... params) {
            String imgUrl =  params[0];
            try {
                RequestOptions options = new RequestOptions()
                        .error(R.drawable.default_tx)
                        .circleCrop();
                return Glide.with(context)
                        .load(imgUrl)
                        .apply(options)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();
            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(File result) {
            if (result == null) {
                return ;
            }
            //此path就是对应文件的缓存路径
            String path = result.getPath();
            Bitmap bmp= BitmapFactory.decodeFile(path);
            views.setImageViewBitmap(R.id.notifi_tx,bmp);
            notificationManager.notify(NOTIFICATION_PENDINGINTENT_ID, builder.build());
        }

    }

}
