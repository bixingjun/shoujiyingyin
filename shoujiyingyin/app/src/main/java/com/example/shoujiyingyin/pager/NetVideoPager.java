package com.example.shoujiyingyin.pager;
/*
 *  包名: com.example.shoujiyingyin.pager
 * Created by ASUS on 2017/12/6.
 *  描述: TODO
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.shoujiyingyin.R;
import com.example.shoujiyingyin.adapter.NetVideoPagerAdapter;
import com.example.shoujiyingyin.base.BasePager;
import com.example.shoujiyingyin.bean.MediaItem;
import com.example.shoujiyingyin.ui.SystemVideoPlayer;
import com.example.shoujiyingyin.utils.CacheUtils;
import com.example.shoujiyingyin.utils.Constants;
import com.example.shoujiyingyin.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NetVideoPager extends BasePager{

    @ViewInject(R.id.listview)
    private XListView mListview;

    @ViewInject(R.id.tv_nonet)
    private TextView mTv_nonet;

    @ViewInject(R.id.pb_loading)
    private ProgressBar mProgressBar;

    public NetVideoPager(Context context) {
        super(context);
    }

    ArrayList<MediaItem> mediaItems=new ArrayList<>();
    private NetVideoPagerAdapter adapter;

    private boolean isLoadMore=false;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.netvideo_pager,null);
        x.view().inject(this,view);
        mListview.setOnItemClickListener(new MyOnItemClickListener());
        mListview.setPullLoadEnable(true);
        mListview.setXListViewListener(new MyIXListViewListener());
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        String saveJson = CacheUtils.getString(context, Constants.NET_URL);

        if(!TextUtils.isEmpty(saveJson)){
        processData(saveJson);
        }
        getDataFromNet();
    }

    private void processData(String json){
        if(!isLoadMore) {
            mediaItems = parseJson(json);
            showData();
        }else {
            isLoadMore=false;
            mediaItems.addAll(parseJson(json));
            adapter.notifyDataSetChanged();
            onLoad();
        }
    }

    private void onLoad() {
        mListview.stopRefresh();
        mListview.stopLoadMore();
        mListview.setRefreshTime("更新时间:"+getSysteTime());
    }

    private String getSysteTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    private void showData() {
        if(mediaItems!=null&&mediaItems.size()>0){
             adapter = new NetVideoPagerAdapter(context, mediaItems);
            mListview.setAdapter(adapter);
            onLoad();
            mTv_nonet.setVisibility(View.GONE);
        }else {
            mTv_nonet.setVisibility(View.VISIBLE);
        }

        mProgressBar.setVisibility(View.GONE);
    }


    private void getMoreDataFromNet() {
        RequestParams params = new RequestParams(Constants.NET_URL);

        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                isLoadMore=true;
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                isLoadMore=false;
            }

            @Override
            public void onCancelled(CancelledException cex) {
                isLoadMore=false;
            }

            @Override
            public void onFinished() {
                isLoadMore = false;
            }
        });
    }

    private ArrayList<MediaItem> parseJson(String json){

        ArrayList<MediaItem> mediaItems=new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");

            if(jsonArray!=null&&jsonArray.length()>0){
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
                    if(jsonObjectItem!=null){

                        MediaItem mediaItem = new MediaItem();


                        String movieName = jsonObjectItem.optString("movieName");//name
                        mediaItem.setName(movieName);

                        String videoTitle = jsonObjectItem.optString("videoTitle");//desc
                        mediaItem.setDesc(videoTitle);

                        String imageUrl = jsonObjectItem.optString("coverImg");//imageUrl
                        mediaItem.setImageUrl(imageUrl);

                        String hightUrl = jsonObjectItem.optString("hightUrl");//data
                        mediaItem.setData(hightUrl);

                        //把数据添加到集合
                        mediaItems.add(mediaItem);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mediaItems;
    }

     class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(context, SystemVideoPlayer.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position-1);
            context.startActivity(intent);

        }
    }

    private class MyIXListViewListener implements XListView.IXListViewListener {
        @Override
        public void onRefresh() {
            getDataFromNet();
        }

        @Override
        public void onLoadMore() {
            getMoreDataFromNet();
        }
    }

    private void getDataFromNet() {

        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {


            @Override
            public void onSuccess(String result) {
                CacheUtils.putString(context,Constants.NET_URL,result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                showData();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }
}
