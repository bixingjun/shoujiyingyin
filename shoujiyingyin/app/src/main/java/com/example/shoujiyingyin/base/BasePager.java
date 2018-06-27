package com.example.shoujiyingyin.base;
/*
 *  包名: com.example.shoujiyingyin.base
 * Created by ASUS on 2017/12/6.
 *  描述: TODO
 */

import android.content.Context;
import android.view.View;

public abstract class BasePager {

    public final Context context;

    public View rootView;
    public boolean isInitData;

    public BasePager(Context context) {
        this.context=context;
        rootView=initView();
    }

    public abstract View initView();

    public void initData(){

    }

}
