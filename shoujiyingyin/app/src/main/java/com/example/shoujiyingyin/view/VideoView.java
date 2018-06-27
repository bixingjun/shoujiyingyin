package com.example.shoujiyingyin.view;
/*
 *  包名: com.example.shoujiyingyin.view
 * Created by ASUS on 2017/12/10.
 *  描述: TODO
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class VideoView extends android.widget.VideoView{
    public VideoView(Context context) {
        this(context,null);
    }

    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    public void setVideoSize(int videoWidht,int videoHeight){
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width=videoWidht;
        layoutParams.height=videoHeight;
        setLayoutParams(layoutParams);
    }
}
