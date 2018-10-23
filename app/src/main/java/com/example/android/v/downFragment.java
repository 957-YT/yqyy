package com.example.android.v;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

/**
 * A simple {@link Fragment} subclass.
 */
public class downFragment extends Fragment implements View.OnClickListener {

    private ProgressDialog waitingDialog;
    public downFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.down_fragment, container, false);

        TextView used = view.findViewById(R.id.used);
        used.setOnClickListener(this);
        TextView about = view.findViewById(R.id.about);
        about.setOnClickListener(this);
        TextView cache = view.findViewById(R.id.cache);
        cache.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.used:
                Intent usedIntent = new Intent(getActivity(), About.class);
                usedIntent.putExtra("a","used");
                usedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(usedIntent);
                break;
            case R.id.about:
                Intent aboutIntent = new Intent(getActivity(), About.class);
                aboutIntent.putExtra("a","about");
                aboutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(aboutIntent);
                break;
            case R.id.cache:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitingDialog.cancel();
                        Toast.makeText(getContext(), "清除成功", Toast.LENGTH_SHORT).show();
                    }
                },5000);
                showWaitingDialog();
                Glide.get(getContext()).clearMemory();
                Log.e("---------","清除");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(getContext()).clearDiskCache();
                    }
                }).start();
        }

    }

    public  void showWaitingDialog() {
        waitingDialog= new ProgressDialog(getContext());
        waitingDialog.setTitle("");
        waitingDialog.setMessage("等待中...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }
}
