package com.example.shoujiyingyin.service;
/*
 *  包名: com.example.shoujiyingyin.service
 * Created by ASUS on 2017/12/13.
 *  描述: TODO
 */


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.shoujiyingyin.IMusicPlayerService;
import com.example.shoujiyingyin.R;
import com.example.shoujiyingyin.bean.MediaItem;
import com.example.shoujiyingyin.ui.AudioPlayerActivity;
import com.example.shoujiyingyin.utils.CacheUtils;

import java.io.IOException;
import java.util.ArrayList;



public class MusicPlayerService extends Service {

    public static final String OPENAUDIO = "com.example.mobileplayer_OPENAUDIO";
    private ArrayList<MediaItem> mediaItems;
    private int position;
    private MediaItem mediaItem;

    private MediaPlayer mediaPlayer;


    public static final int REPEAT_NORMAL=1;
    public static final int REPEAT_SINGLE=2;
    public static final int REPEAT_ALL=3;

    private int playmode=REPEAT_NORMAL;

    @Override
    public void onCreate() {
        super.onCreate();

        playmode= CacheUtils.getInt(this,"playmode");

        getDataFromLocal();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private IMusicPlayerService.Stub stub=new IMusicPlayerService.Stub() {
        MusicPlayerService service=MusicPlayerService.this;

        @Override
        public void openAudio(int position) throws RemoteException {
                service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
                service.start();
        }

        @Override
        public void pause() throws RemoteException {
                service.pause();
        }

        @Override
        public void stop() throws RemoteException {
                service.stop();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            return service.getName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public void next() throws RemoteException {
                service.next();
        }

        @Override
        public void pre() throws RemoteException {
                service.pre();
        }

        @Override
        public void setPlayMode(int playmode) throws RemoteException {
                service.setPlayMode(playmode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
             mediaPlayer.seekTo(position);
        }
    };



    private void openAudio(int position){
        this.position=position;
        if(mediaItems!=null&&mediaItems.size()>0){
            mediaItem = mediaItems.get(position);
            if(mediaPlayer!=null){

                mediaPlayer.reset();
            }


            try {
                mediaPlayer=new MediaPlayer();
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                mediaPlayer.setDataSource(mediaItem.getData());
                mediaPlayer.prepareAsync();

                if(playmode==MusicPlayerService.REPEAT_SINGLE){
                    mediaPlayer.setLooping(true);
                }else {
                    mediaPlayer.setLooping(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {

        }
    }

    private NotificationManager manager;



    private void start(){
            mediaPlayer.start();
            manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra("notification",true);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);


        Notification notification = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.notification_music_playing)
                    .setContentTitle("321音乐")
                    .setContentText("正在播放:"+getName())
                    .setContentIntent(pendingIntent)
                    .build();
        }
        manager.notify(1, notification);

    }

    private void pause(){
        mediaPlayer.pause();
        manager.cancel(1);
    }

    private void stop(){

    }

    private int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    private int getDuration(){
        return mediaPlayer.getDuration();
    }

    private String getName(){

        return mediaItem.getName();
    }

    private String getArtist(){

        return mediaItem.getArtist();
    }

    private String getAudioPath(){

        return mediaItem.getData();
    }

    private void next(){

        setNextPosition();
        openNextAudio();
    }

    private void openNextAudio() {
        if(playmode==MusicPlayerService.REPEAT_NORMAL){
            if(position<mediaItems.size()){
                openAudio(position);
            }else {
                position=mediaItems.size()-1;
            }
        }else if(playmode==MusicPlayerService.REPEAT_SINGLE){
            openAudio(position);
        }else if(playmode==MusicPlayerService.REPEAT_ALL){
            openAudio(position);
        }else {
            if(position<mediaItems.size()){
                openAudio(position);
            }else {
                position=mediaItems.size()-1;
            }
        }



    }

    private void setNextPosition() {
        if(playmode==MusicPlayerService.REPEAT_NORMAL){
            position++;
        }else if(playmode==MusicPlayerService.REPEAT_SINGLE){
            position++;
            if(position>=mediaItems.size()){
                position=0;
            }
        }else if(playmode==MusicPlayerService.REPEAT_ALL){
            position++;
            if(position>=mediaItems.size()){
                position=0;
            }
        }else {
            position++;
        }
    }

    private void pre(){
            setPrePosition();
            openPreAudio();
    }

    private void setPrePosition() {
        if(playmode==MusicPlayerService.REPEAT_NORMAL){
            position--;
        }else if(playmode==MusicPlayerService.REPEAT_SINGLE){
            position--;
            if(position<0){
                position=mediaItems.size()-1;
            }
        }else if(playmode==MusicPlayerService.REPEAT_ALL){
            position--;
            if(position<0){
                position=mediaItems.size()-1;
            }
        }else {
            position--;
        }
    }

    private void openPreAudio() {

        if(playmode==MusicPlayerService.REPEAT_NORMAL){
            if(position>=0){
                openAudio(position);
            }else {
                position=0;
            }
        }else if(playmode==MusicPlayerService.REPEAT_SINGLE){
            openAudio(position);
        }else if(playmode==MusicPlayerService.REPEAT_ALL){
            openAudio(position);
        }else {
            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                if (position >= 0) {
                    openAudio(position);
                } else {
                    position = 0;
                }
            }
        }
    }


    private void setPlayMode(int playmode){
        this.playmode=playmode;
        CacheUtils.putInt(this,"playmode",playmode);

        if(playmode==MusicPlayerService.REPEAT_SINGLE){
            mediaPlayer.setLooping(true);
        }else {
            mediaPlayer.setLooping(false);
        }
    }

    private int getPlayMode(){

        return playmode;
    }

    private boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }



    private void getDataFromLocal() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                mediaItems = new ArrayList<>();
                ContentResolver resolver=getContentResolver();
                Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs={
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST
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



               

            }
        }.start();
    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {

           notifyChange(OPENAUDIO);
            start();
        }
    }

    private void notifyChange(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return true;
        }
    }
}

