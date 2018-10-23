package com.example.android.v;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.v.data.OpenHelper;
import com.example.android.v.service.SqlService;

import java.util.ArrayList;

public class BottomDialog extends DialogFragment {
    public static DialogAdapter dialogAdapter;
    private playService mplayService;
    private playService.MyBinder binder;
    private DeleteReceiver deletereceiver;
    private SqlService sqlService;
    private ListView listView;
    private OpenHelper openHelper;
    private static int flag =1;
    public   TextView songNum;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomDialogStyle);

        Intent bindIntent = new Intent(getActivity(), playService.class);
        getActivity().bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        IntentFilter filter = new IntentFilter();
        deletereceiver = new DeleteReceiver();
        filter.addAction("android.intent.action.delete");
        filter.addAction("android.intent.action.insert");
        getActivity().registerReceiver(deletereceiver, filter);

    }


    @Override
    public void onStart() {
        super.onStart();
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        int height = (int) (d.getHeight() * 0.61);
        //设置 dialog 的宽高
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);
        //设置 dialog 的背景为 null
        getDialog().getWindow().setBackgroundDrawable(null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_list, container, false);
        //去除标题栏
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        Window dialogWindow = getDialog().getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.gravity = Gravity.BOTTOM; //底部
        dialogWindow.setAttributes(lp);

        listView = rootView.findViewById(R.id.dialog_play);

        songNum = rootView.findViewById(R.id.song_num);
        CharSequence s = "("+String.valueOf(playService.dialogMusicList.size())+"首)";
        songNum.setText(s);

        openHelper = new OpenHelper(getActivity());
//        final SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
//        sqlService = new SqlService(sqLiteDatabase);

        dialogAdapter = new DialogAdapter(getActivity(), playService.dialogMusicList);
        listView.setAdapter(dialogAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Music currentMusic = dialogAdapter.getItem(position);
                Intent stopIntent = new Intent("android.intent.action.stopSeekBar");
                getActivity().sendBroadcast(stopIntent);
                //改变字体颜色
                dialogAdapter.setCurrentItem(position);
                dialogAdapter.setClick(true);
                dialogAdapter.notifyDataSetChanged();
                mplayService.play(currentMusic);
            }
        });

        dialogAdapter.setCurrentItem(playService.position);
        dialogAdapter.setClick(true);
        listView.setSelection(playService.position);
        dialogAdapter.notifyDataSetChanged();

        final LinearLayout modeView = rootView.findViewById(R.id.play_mode);
        final TextView modeText = rootView.findViewById(R.id.mode_text);
        final ImageView modeImage = rootView.findViewById(R.id.play_style);
        switch (playService.playmode){
            case 0:
                modeText.setText("顺序播放");
                modeImage.setImageResource(R.drawable.ic_order_play);
                break;
            case 1:
                modeText.setText("随机播放");
                modeImage.setImageResource(R.drawable.ic_random);
                break;
            case 2:
                modeText.setText("单曲循环");
                modeImage.setImageResource(R.drawable.ic_cycle);
                break;
        }

        modeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (flag){
                    case 0:
                        modeText.setText("顺序播放");
                        playService.playmode = 0;
                        modeImage.setImageResource(R.drawable.ic_order_play);
                        flag = 1;
                        break;
                    case 1:
                        modeText.setText("随机播放");
                        playService.playmode = 1;
                        modeImage.setImageResource(R.drawable.ic_random);
                        flag = 2;
                        break;
                    case 2:
                        modeText.setText("单曲循环");
                        modeImage.setImageResource(R.drawable.ic_cycle);
                        playService.playmode = 2;
                        flag = 0;
                        break;
                }
            }
        });

        ImageView imageView = rootView.findViewById(R.id.delete_dialog_list);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("警告")
                        .setMessage("确定清空列表吗?")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final SQLiteDatabase writableDatabase = openHelper.getWritableDatabase();
                                sqlService = new SqlService(writableDatabase);
                                boolean b = sqlService.delete_all();
                                writableDatabase.close();
                                if (b) {
                                    mplayService.dialogMusicList.clear();
                                    dialogAdapter.notifyDataSetChanged();
                                    CharSequence s = "("+String.valueOf(playService.dialogMusicList.size())+"首)";
                                    songNum.setText(s);
                                    Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNegativeButton("否",null)
                        .create();
                dialog.show();
            }
        });

        return rootView;
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

    public class DeleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case "android.intent.action.delete":
                    dialogAdapter.notifyDataSetChanged();
                    break;
                case "android.intent.action.insert":
                    dialogAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(deletereceiver);
        getActivity().unbindService(serviceConnection);
        super.onDestroy();
    }

}
