package com.example.shoujiyingyin.pager;
/*
 *  包名: com.example.shoujiyingyin.pager
 * Created by ASUS on 2017/12/6.
 *  描述: TODO
 */

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shoujiyingyin.base.BasePager;

@SuppressLint("ValidFragment")
public class ReplaceFragment extends Fragment {
    private BasePager currPager;

    public ReplaceFragment(BasePager pager) {
        this.currPager=pager;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return currPager.rootView;
    }
}
