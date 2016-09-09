package com.tattoos.clientapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tattoos.clientapp.R;

import java.util.ArrayList;

public class PagerViewAdapter extends PagerAdapter {
    private ArrayList<Bitmap> bitmaps;
    private Context mContext;

    public PagerViewAdapter(Context context, ArrayList<Bitmap> bitmaps) {
        this.mContext = context;
        this.bitmaps = bitmaps;
    }

    public void setBitmaps(ArrayList<Bitmap> bm){
        this.bitmaps = bm;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return bitmaps.size();
    }

    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == ((ImageView) obj);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        ImageView mImageView = new ImageView(mContext);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.setImageBitmap(bitmaps.get(i));
        ((ViewPager) container).addView(mImageView, 0);
        return mImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        ((ViewPager) container).removeView((ImageView) obj);
    }
}