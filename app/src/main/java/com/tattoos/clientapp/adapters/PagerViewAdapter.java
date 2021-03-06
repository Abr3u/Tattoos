package com.tattoos.clientapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tattoos.clientapp.R;

import java.util.ArrayList;

public class PagerViewAdapter extends PagerAdapter {
    private ArrayList<String> urls;
    private Context mContext;

    public PagerViewAdapter(Context context, ArrayList<String> urls) {
        this.mContext = context;
        this.urls = urls;
    }

    public void setUrls(ArrayList<String> urls){
        this.urls = urls;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == ((ImageView) obj);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        ImageView mImageView = new ImageView(mContext);

        Glide
                .with(mContext)
                .load(urls.get(i))
                .centerCrop()
                .crossFade()
                .into(mImageView);

        ((ViewPager) container).addView(mImageView, 0);
        return mImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        ((ViewPager) container).removeView((ImageView) obj);
    }
}