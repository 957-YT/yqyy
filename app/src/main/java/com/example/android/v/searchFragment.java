package com.example.android.v;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.v.data.OpenHelper;
import com.example.android.v.service.NetService;
import com.example.android.v.service.SqlService;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class searchFragment extends Fragment {

    private ArrayList<Music> searchArrayList;
    private musicAdapter searchAdapter;
    private EditText editText;
    private playService mplayService;
    private ProgressBar search_prc;


    public searchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent bindIntent = new Intent(getActivity(), playService.class);
        getActivity().bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        boolean isConn = MainActivity.isOnline(getContext());
        if (!isConn) {
            Toast.makeText(getContext(), "当前没有网络连接!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_fragment, container, false);

        editText = rootView.findViewById(R.id.search_content);
        ImageView searchView = rootView.findViewById(R.id.search);
        ListView listView = rootView.findViewById(R.id.search_list);
        searchArrayList = new ArrayList<>();
        searchAdapter = new musicAdapter(getContext(), searchArrayList);
        listView.setAdapter(searchAdapter);
        search_prc = rootView.findViewById(R.id.search_prc);


        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    searchArrayList.clear();
                    NetService.getSearchResult(getActivity(),searchArrayList, searchAdapter, editText.getText().toString().trim(),search_prc);
                    search_prc.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchArrayList.clear();
                NetService.getSearchResult(getActivity(),searchArrayList, searchAdapter, editText.getText().toString().trim(),search_prc);

                search_prc.setVisibility(View.VISIBLE);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Music currentMusic = searchAdapter.getItem(position);
                OpenHelper openHelper = new OpenHelper(getActivity());
                SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();
                SqlService sqlService = new SqlService(sqLiteDatabase);
                long i = sqlService.insert(currentMusic);
                if (i == -1) {
                    mplayService.play(currentMusic);
                } else {
                }
                for (int a = 0; a < playService.dialogMusicList.size(); a++) {
                    if (playService.dialogMusicList.get(a).getId() == currentMusic.getId()) {
                        return;
                    }
                }
                playService.dialogMusicList.add(currentMusic);
                Intent intent = new Intent("android.intent.action.insert");
                getActivity().sendBroadcast(intent);
                Intent stopIntent = new Intent("android.intent.action.stopSeekBar");
                getActivity().sendBroadcast(stopIntent);
                mplayService.play(currentMusic);
            }
        });
        return rootView;

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
        super.onDestroy();
    }
}
