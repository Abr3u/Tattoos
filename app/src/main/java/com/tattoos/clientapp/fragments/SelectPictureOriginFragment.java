package com.tattoos.clientapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tattoos.clientapp.R;


public class SelectPictureOriginFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("yyy","createView frag");
        return inflater.inflate(R.layout.picture_origin_fragment, container, false);
    }
}
