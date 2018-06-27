package com.example.shoujiyingyin.view;
/*
 *  包名: com.example.shoujiyingyin.view
 * Created by ASUS on 2017/12/6.
 *  描述: TODO
 */

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.shoujiyingyin.R;

public class TitleBar extends LinearLayout implements View.OnClickListener{

    private View tv_search;

    private View rl_game;

    private View iv_record;
    private Context context;

    public TitleBar(Context context) {
        this(context,null);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        tv_search = getChildAt(1);
        rl_game = getChildAt(2);
        iv_record = getChildAt(3);


        tv_search.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_search://搜索
                Toast.makeText(context, "搜索", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_game://游戏
                Toast.makeText(context, "游戏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_record://播放历史
                Toast.makeText(context, "播放历史", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
