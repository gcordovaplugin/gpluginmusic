package cn.yingzhichu.cordova.gmusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {

    private MediaPlayer player;
    private List<MusicItem> playList = new ArrayList<>();
    private int currentIndex = 0;
    private Timer timer;

//    private int loopModel = ;

    public MusicService() {
    }
    public void startTimer(){
        if(timer == null){
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    updateData();
                }
            },0,500);
        }
    }

    /**
     * 更新数据
     */
    public void updateData(){
        Message msg = new Message();
        msg.what = 333;
        Bundle data = new Bundle();
        data.putInt("current_index",currentIndex);
        if(player != null){
            data.putBoolean("hasNext",hasNextService());
            data.putBoolean("hasPre",hasPreService());
            data.putBoolean("isPlay",isPlayService());
            data.putInt("duration",player.getDuration());
            data.putInt("postion",player.getCurrentPosition());
            MusicItem item = playList.get(currentIndex);
            data.putString("music_name",item.getName());
            data.putString("music_url",item.getUrl());
            data.putString("music_icon",item.getIcon());
        }else{
            data.putBoolean("hasNext",false);
            data.putBoolean("hasPre",false);
            data.putBoolean("isPlay",false);
            data.putInt("duration",0);
            data.putInt("postion",0);
            data.putString("music_name","");
            data.putString("music_url","");
            data.putString("music_icon","");
        }

        msg.setData(data);
        GPluginMusic.handler.sendMessage(msg);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicController();
    }

    public  class MusicController extends Binder{
        //清空播放列表
        public void clean(){
            stop();
            playList.clear();
        }
        //添加歌曲
        public void add(MusicItem item){
            playList.add(item);
        }
        //添加列表
        public void add(List<MusicItem> ulist){
            clean();
            playList.addAll(ulist);
        }
        private void stop(){
            if(timer != null){
                timer.cancel();
                timer = null;
            }
            if(player != null){
                player.pause();
                player.release();
            }
        }
        //播放
        public void play(int i){
            stop();
            MusicItem item = playList.get(i);
            player = MediaPlayer.create(getApplicationContext(), Uri.parse(item.getUrl()));
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){

                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(hasNext()){
                        nextPlay();
                    }
                }
            });
            startTimer();
            player.start();

        }
        //暂停
        public void pause(){
            if(player == null){
                return;
            }
            player.pause();
            timer.cancel();
        }
        //播放
        public void resume(){
            if(isPlay()){
                return;
            }
            if(player != null){
                return;
            }
            player.start();
            startTimer();
        }
        //是否增长播放
        public boolean isPlay(){
            return isPlayService();
        }
        //是否有哦下一曲
        public boolean hasNext(){
            return hasNextService();
        }
        //是否可以上一曲
        public boolean hasPre(){
            return hasPreService();
        }
        //播放下一首
        public void nextPlay(){
            if(hasNext()){
                currentIndex++;
                play(currentIndex);
            }
        }
        //播放上一首
        public void prePlay(){
            if(hasPre()){
                currentIndex--;
                play(currentIndex);
            }
        }
        //列表
        public List<MusicItem> list(){
            return playList;
        }
        //拖动
        public void seekto(int to){
            if(isPlay()){
                player.seekTo(player.getDuration() * to / 100);
            }
        }

    }
    //是否增长播放
    public boolean isPlayService(){
        if(player == null){
            return false;
        }
        return player.isPlaying();
    }
    //是否有哦下一曲
    public boolean hasNextService(){
        if(playList.size() == 0){
            return false;
        }
        return currentIndex + 1  < playList.size();
    }
    //是否可以上一曲
    public boolean hasPreService(){
        if(playList.size() == 0){
            return false;
        }
        return currentIndex - 1  >= 0;
    }
}