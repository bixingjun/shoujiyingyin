package com.example.shoujiyingyin.pager;
/*
 *  包名: com.example.shoujiyingyin.pager
 * Created by ASUS on 2017/12/6.
 *  描述: TODO
 */

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.shoujiyingyin.R;
import com.example.shoujiyingyin.adapter.NetAudioPagerAdapter;
import com.example.shoujiyingyin.base.BasePager;
import com.example.shoujiyingyin.bean.NetAudioPagerData;
import com.example.shoujiyingyin.utils.CacheUtils;
import com.example.shoujiyingyin.utils.Constants;
import com.example.shoujiyingyin.utils.LogUtil;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class NetAudioPager extends BasePager{

    @ViewInject(R.id.listview)
    private ListView mListView;

    @ViewInject(R.id.tv_nonet)
    private TextView tv_nonet;

    @ViewInject(R.id.pb_loading)
    private ProgressBar pb_loading;
    private List<NetAudioPagerData.ListBean> datas;
    private NetAudioPagerAdapter adapter;


    public NetAudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view=View.inflate(context, R.layout.netvaudio_pager,null);
        x.view().inject(this,view);

        return view;
    }

    @Override
    public void initData() {
        super.initData();

        String savejson= CacheUtils.getString(context,Constants.ALL_RES_URL);
        if(!TextUtils.isEmpty(savejson)){
            processData(savejson);
        }


        getDataFromNet();
    }

    private void getDataFromNet() {
        RequestParams params=new RequestParams(Constants.ALL_RES_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("请求数据成功==" + result);
                CacheUtils.putString(context, Constants.ALL_RES_URL, result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("请求数据失败==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");

            }
        });
    }

    private void processData(String json) {
        NetAudioPagerData data=parsedJson(json);
        datas = data.getList();
        if(datas!=null&&datas.size()>0){
                tv_nonet.setVisibility(View.GONE);
             adapter = new NetAudioPagerAdapter(context,datas);
            mListView.setAdapter(adapter);
        }else {
            tv_nonet.setVisibility(View.VISIBLE);
            tv_nonet.setText("没有对应的数据");
        }

        pb_loading.setVisibility(View.GONE);
    }

    private NetAudioPagerData parsedJson(String json){
        return new Gson().fromJson(json,NetAudioPagerData.class);
    }
}
