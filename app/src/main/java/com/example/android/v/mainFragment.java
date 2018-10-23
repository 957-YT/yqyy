package com.example.android.v;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class mainFragment extends Fragment {

    public  View conTenView;
    private Main_adapter adapter_service;
    private Main_adapter adapter1;
    public mainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initView1();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        conTenView = inflater.inflate(R.layout.main_fragment,container,false);

        RecyclerView recyclerView = conTenView.findViewById(R.id.main_recycle);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        recyclerView.setAdapter(adapter_service);

        RecyclerView recyclerView1 = conTenView.findViewById(R.id.recommend_recycle);
        recyclerView1.setLayoutManager(new GridLayoutManager(getActivity(),3));
        recyclerView1.setAdapter(adapter1);


       return conTenView;
    }

    private void initView(){
        final List<MainItem> list = new ArrayList<>();
        MainItem service_item0 = new MainItem(R.drawable.bsb,"19723756",0);
        list.add(service_item0);
        MainItem service_item1 = new MainItem(R.drawable.xgb,"3779629",1);
        list.add(service_item1);
        MainItem service_item2 = new MainItem(R.drawable.ycb,"2884035",2);
        list.add(service_item2);
        MainItem service_item3 = new MainItem(R.drawable.rgb,"3778678",3);
        list.add(service_item3);
        MainItem service_item4 = new MainItem(R.drawable.dyb,"1978921795",4);
        list.add(service_item4);
        MainItem service_item5 = new MainItem(R.drawable.dy,"2250011882",5);
        list.add(service_item5);
        MainItem service_item6 = new MainItem(R.drawable.max_itunes,"11641012",6);
        list.add(service_item6);
        MainItem service_item7 = new MainItem(R.drawable.billboard,"60198",7);
        list.add(service_item7);
        MainItem service_item8 = new MainItem(R.drawable.oricon,"60131",8);
        list.add(service_item8);
        adapter_service = new Main_adapter(list);
        adapter_service.setmOnItemClickListener(new Main_adapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent musicintent = new Intent(getActivity(),musicList.class);
                musicintent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                musicintent.putExtra("gd_id",list.get(position).getSongListid());
                musicintent.putExtra("what_b",list.get(position).getBgid());
                startActivity(musicintent);
            }
        });

    }

    private void initView1(){
        final List<MainItem> list1 = new ArrayList<>();
        MainItem service_item0 = new MainItem(R.drawable.chinese_min,"635903110",9);
        list1.add(service_item0);
        MainItem service_item1 = new MainItem(R.drawable.english_min,"824754853",10);
        list1.add(service_item1);
        MainItem service_item2 = new MainItem(R.drawable.jpan_min,"454371535",11);
        list1.add(service_item2);
        MainItem service_item3 = new MainItem(R.drawable.korea_min,"972909273",12);
        list1.add(service_item3);
        MainItem service_item4 = new MainItem(R.drawable.cantonese_min,"161864761",13);
        list1.add(service_item4);
        MainItem service_item5 = new MainItem(R.drawable.german_min,"36435493",14);
        list1.add(service_item5);
        adapter1 = new Main_adapter(list1);
        adapter1.setmOnItemClickListener(new Main_adapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent musicintent = new Intent(getActivity(),musicList.class);
                musicintent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                musicintent.putExtra("gd_id",list1.get(position).getSongListid());
                musicintent.putExtra("what_b",list1.get(position).getBgid());
                startActivity(musicintent);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
