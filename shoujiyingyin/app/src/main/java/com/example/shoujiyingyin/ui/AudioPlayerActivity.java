package com.example.shoujiyingyin.ui;
/*
 *  包名: com.example.shoujiyingyin.ui
 * Created by ASUS on 2017/12/12.
 *  描述: TODO
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.constraint.solver.Cache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoujiyingyin.IMusicPlayerService;
import com.example.shoujiyingyin.R;
import com.example.shoujiyingyin.service.MusicPlayerService;
import com.example.shoujiyingyin.utils.CacheUtils;
import com.example.shoujiyingyin.utils.LyricUtils;
import com.example.shoujiyingyin.utils.Utils;
import com.example.shoujiyingyin.view.ShowLyricView;

import java.io.File;

public class AudioPlayerActivity extends Activity implements View.OnClickListener {

    /**
     * 进度更新
     */
    private static final int PROGRESS = 1;
    private int position;
    /**
     * true:从状态栏进入的，不需要重新播放
     * false:从播放列表进入的
     */
    private boolean notification;
   private IMusicPlayerService service;//服务的代理类，通过它可以调用服务的方法
    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnAudioPlaymode;
    private Button btnAudioPre;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnLyrc;
    private static final int SHOW_LYRIC=2;
    private MyReceiver receiver;
    private ShowLyricView showLyricView;
    //private MyReceiver receiver;
    private Utils utils;
    private boolean isShowLyric=true;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PROGRESS:
                    try {
                        int currentPosition=service.getCurrentPosition();
                        seekbarAudio.setProgress(currentPosition);

                        tvTime.setText(utils.stringForTime(currentPosition)+"/"+utils.stringForTime(service.getDuration()));

                        handler.removeMessages(PROGRESS);
                        handler.sendEmptyMessageDelayed(PROGRESS,1000);

                    } catch (RemoteException e) {
                        e.printStackTrace();

                    }
                    break;

                case SHOW_LYRIC:

                    try {
                        int currentPosition = service.getCurrentPosition();

                        showLyricView.setshowNextLyric(currentPosition);

                        handler.removeMessages(SHOW_LYRIC);
                        handler.sendEmptyMessage(SHOW_LYRIC);
                    }catch (RemoteException e) {
                        e.printStackTrace();
                    }


                    break;
            }
        }
    };


    private ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service=IMusicPlayerService.Stub.asInterface(iBinder);
            if(service!=null){
                try {
                    if(!notification) {
                        service.openAudio(position);
                    }else {
                        showViewData();
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                service.stop();
                service=null;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audioplayer);
                initData();
                initView();
                getData();
                bindAndStartService();
    }

    private void initData() {
        utils=new Utils();
        receiver=new MyReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPENAUDIO);
        registerReceiver(receiver,intentFilter);
    }

    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {


                showLyric();
                showViewData();
                checkPlaymode();
        }
    }


    private void showLyric() {
        //解析歌词
        LyricUtils lyricUtils = new LyricUtils();

        try {
            String path = service.getAudioPath();//得到歌曲的绝对路径

            //传歌词文件
            //mnt/sdcard/audio/beijingbeijing.mp3
            //mnt/sdcard/audio/beijingbeijing.lrc
            path = path.substring(0,path.lastIndexOf("."));
            File file = new File(path + ".lrc");
            if(!file.exists()){
                file = new File(path + ".txt");
            }
            lyricUtils.readLyricFile(file);//解析歌词

            showLyricView.setLyrics(lyricUtils.getLyrics());

        } catch (RemoteException e) {
            e.printStackTrace();
        }



        if(lyricUtils.isExistsLyric()){
            handler.sendEmptyMessage(SHOW_LYRIC);
        }

    }



    private void showViewData() {
        try {
            tvArtist.setText(service.getArtist());
            tvName.setText(service.getName());
            seekbarAudio.setMax(service.getDuration());

            handler.sendEmptyMessage(PROGRESS);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void bindAndStartService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.example.mobileplayer_OPENAUDIO");
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    private void getData() {
        notification= getIntent().getBooleanExtra("notification", false);
        if(!notification) {

            position = getIntent().getIntExtra("position", 0);
        }
    }

    private void initView() {
        ivIcon = (ImageView)findViewById( R.id.iv_icon );
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable rocketAnimation = (AnimationDrawable) ivIcon.getBackground();
        rocketAnimation.start();
        tvArtist = (TextView)findViewById( R.id.tv_artist );
        tvName = (TextView)findViewById( R.id.tv_name );
        tvTime = (TextView)findViewById( R.id.tv_time );
        seekbarAudio = (SeekBar)findViewById( R.id.seekbar_audio );
        btnAudioPlaymode = (Button)findViewById( R.id.btn_audio_playmode );
        btnAudioPre = (Button)findViewById( R.id.btn_audio_pre );
        btnAudioStartPause = (Button)findViewById( R.id.btn_audio_start_pause );
        btnAudioNext = (Button)findViewById( R.id.btn_audio_next );
        btnLyrc = (Button)findViewById( R.id.btn_lyrc );
        showLyricView=(ShowLyricView)findViewById(R.id.showLyricView);


        btnAudioPlaymode.setOnClickListener( this );
        btnAudioPre.setOnClickListener( this );
        btnAudioStartPause.setOnClickListener( this );
        btnAudioNext.setOnClickListener( this );
        btnLyrc.setOnClickListener( this );

        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
    }

    @Override
    public void onClick(View v) {

        if ( v == btnAudioPlaymode ) {
            // Handle clicks for btnAudioPlaymode
            setPlaymode();
        } else if ( v == btnAudioPre ) {
            // Handle clicks for btnAudioPre
            if(service!=null){
                try {
                    service.pre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if ( v == btnAudioStartPause ) {
            if(service != null){
                try {
                    if(service.isPlaying()){
                        //暂停
                        service.pause();
                        //按钮-播放
                        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                    }else{
                        //播放
                        service.start();
                        //按钮-暂停
                        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            // Handle clicks for btnAudioStartPause
        } else if ( v == btnAudioNext ) {
            // Handle clicks for btnAudioNext
            if(service!=null){
                try {
                    service.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if ( v == btnLyrc ) {
            // Handle clicks for btnLyrc
           boolean is= CacheUtils.getBoolean(AudioPlayerActivity.this,"isShowLyric");
            isShowLyric=!is;
            CacheUtils.putBoolean(AudioPlayerActivity.this,"isShowLyric",isShowLyric);

            if(isShowLyric){
                showLyricView.setVisibility(View.VISIBLE);
            }else {
                showLyricView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setPlaymode() {
        try {
            int playmode=service.getPlayMode();
            if(playmode==MusicPlayerService.REPEAT_NORMAL){
                playmode=MusicPlayerService.REPEAT_SINGLE;
            }else if(playmode==MusicPlayerService.REPEAT_SINGLE){
                playmode=MusicPlayerService.REPEAT_ALL;
            }else if(playmode==MusicPlayerService.REPEAT_ALL){
                playmode=MusicPlayerService.REPEAT_NORMAL;
            }else{
                playmode = MusicPlayerService.REPEAT_NORMAL;
            }

            service.setPlayMode(playmode);

            showPlaymode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showPlaymode() {
        try {
            int playmode = service.getPlayMode();

            if(playmode==MusicPlayerService.REPEAT_NORMAL){
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            }else if(playmode == MusicPlayerService.REPEAT_SINGLE){
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
                Toast.makeText(AudioPlayerActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
            }else if(playmode ==MusicPlayerService.REPEAT_ALL){
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
                Toast.makeText(AudioPlayerActivity.this, "全部循环", Toast.LENGTH_SHORT).show();
            }else{
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void checkPlaymode() {
        try {
            int playmode = service.getPlayMode();

            if(playmode==MusicPlayerService.REPEAT_NORMAL){
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            }else if(playmode == MusicPlayerService.REPEAT_SINGLE){
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
            }else if(playmode ==MusicPlayerService.REPEAT_ALL){
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
            }else{
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            }


            //校验播放和暂停的按钮
            if(service.isPlaying()){
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
            }else{
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }




    }

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);
        if(receiver!=null){
            unregisterReceiver(receiver);
            receiver=null;
        }

        if(conn!=null){
            unbindService(conn);
            conn=null;
        }


        super.onDestroy();
    }
}
