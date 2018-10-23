package com.example.android.v;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.example.android.v.service.NetService;

public class CategoryAdapter extends FragmentPagerAdapter {

    private Context mContext;
    public CategoryAdapter(FragmentManager fm,Context context) {
        super(fm);
        mContext = context;

    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new mainFragment();
        } else if (position == 1) {
            return new mvFragment();
        } else if (position == 2) {
            return new searchFragment();
        } else {
            return new downFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

}
