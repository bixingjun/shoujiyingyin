package com.example.shoujiyingyin.pager;
/*
 *  包名: com.example.shoujiyingyin.pager
 * Created by ASUS on 2017/12/6.
 *  描述: TODO
 */

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.shoujiyingyin.R;
import com.example.shoujiyingyin.adapter.VideoPagerAdapter;
import com.example.shoujiyingyin.base.BasePager;
import com.example.shoujiyingyin.bean.MediaItem;
import com.example.shoujiyingyin.ui.SystemVideoPlayer;

import android.os.Handler;
import java.util.ArrayList;


public class VideoPager extends BasePager{
    private TextView tv_nomedia;
    private ProgressBar pb_loading;
    private ListView listview;
    private ArrayList<MediaItem> mediaItems;
    private VideoPagerAdapter videoPagerAdapter;

    public VideoPager(Context context) {
        super(context);
    }

   private Handler handler=new Handler(){
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);

           if(mediaItems!=null&&mediaItems.size()>0){
               videoPagerAdapter=new VideoPagerAdapter(context,mediaItems,true);
               listview.setAdapter(videoPagerAdapter);
                tv_nomedia.setVisibility(View.GONE);
           }else {
               tv_nomedia.setVisibility(View.VISIBLE);
           }

           pb_loading.setVisibility(View.GONE);

       }
   };



    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager, null);
        listview=(ListView) view.findViewById(R.id.listview);
        tv_nomedia = (TextView) view.findViewById(R.id.tv_nomedia);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);

        listview.setOnItemClickListener(new MyOnItemClickListener());

        return view;
    }

    @Override
    public void initData() {
        super.initData();

        getDataFromLocal();

    }

    private void getDataFromLocal() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                mediaItems = new ArrayList<>();
                ContentResolver resolver=context.getContentResolver();
                Uri uri= MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs={
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.ARTIST
                };

                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if(cursor!=null){
                    while (cursor.moveToNext()){

                        MediaItem mediaItem = new MediaItem();

                        mediaItems.add(mediaItem);//写在上面

                        String name = cursor.getString(0);//视频的名称
                        mediaItem.setName(name);

                        long duration = cursor.getLong(1);//视频的时长
                        mediaItem.setDuration(duration);

                        long size = cursor.getLong(2);//视频的文件大小
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);//视频的播放地址
                        mediaItem.setData(data);

                        String artist = cursor.getString(4);//艺术家
                        mediaItem.setArtist(artist);

                    }

                    cursor.close();
                }



                handler.sendEmptyMessage(10);

            }
        }.start();
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem mediaItem = mediaItems.get(position);

            Intent intent = new Intent(context,SystemVideoPlayer.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);

            context.startActivity(intent);

        }
    }
}
