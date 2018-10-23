package com.example.android.v;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class Main_adapter extends RecyclerView.Adapter {

    private List<MainItem> List;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view ,int position);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener;

    public void setmOnItemClickListener(OnRecyclerViewItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public Main_adapter(List<MainItem> list){
        this.List = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MyHolder myHolder = new MyHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_item,viewGroup,false));
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final MyHolder myHolder = (MyHolder) viewHolder;
        myHolder.imageView.setImageResource(List.get(position).getPic());
        if (mOnItemClickListener != null){
            myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = myHolder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(myHolder.itemView,pos);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return List.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.icon_bd);
        }
    }



}
