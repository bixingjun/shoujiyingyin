package com.example.shoujiyingyin.adapter;
/*
 *  包名: com.example.shoujiyingyin.adapter
 * Created by ASUS on 2017/12/6.
 *  描述: TODO
 */

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shoujiyingyin.R;
import com.example.shoujiyingyin.base.BasePager;
import com.example.shoujiyingyin.bean.MediaItem;
import com.example.shoujiyingyin.utils.Utils;

import java.util.ArrayList;

public class VideoPagerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MediaItem> mediaItems;
    private Utils utils;
    private final boolean isVideo;
    public VideoPagerAdapter(Context context,ArrayList<MediaItem> mediaItems,boolean isVideo) {
        this.context = context;
        this.mediaItems=mediaItems;
        utils = new Utils();
        this.isVideo=isVideo;
    }


    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHoder;
        if(convertView==null) {
            convertView = View.inflate(context, R.layout.item_video_pager, null);
            viewHoder=new ViewHolder();
            viewHoder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHoder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHoder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHoder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(viewHoder);

        }else {
            viewHoder= (ViewHolder) convertView.getTag();
        }

        MediaItem mediaItem=mediaItems.get(position);
        viewHoder.tv_name.setText(mediaItem.getName());
        viewHoder.tv_size.setText(Formatter.formatFileSize(context,mediaItem.getSize()));
        viewHoder.tv_time.setText(utils.stringForTime((int) mediaItem.getDuration()));

        if(!isVideo){
            viewHoder.iv_icon.setImageResource(R.drawable.music_default_bg);
        }
        return convertView;
    }

    class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;
    }


}
