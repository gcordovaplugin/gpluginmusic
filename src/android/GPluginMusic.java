package cn.yingzhichu.cordova.gmusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;


import androidx.annotation.NonNull;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class echoes a string called from JavaScript.
 */
public class GPluginMusic extends CordovaPlugin {
    private MusicService.MusicController musicController;
    private MusicConnect musicConnect;
    public static Handler handler;
    private CallbackContext timerCallback;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == 333) {
                    if(timerCallback == null){
                        return true;
                    }
                    Bundle data = msg.getData();
                    JSONObject ret = new JSONObject();
                    try {
                        ret.put("action", "data");
                        ret.put("current_index", data.getInt("current_index"));
                        ret.put("hasNext", data.getBoolean("hasNext"));
                        ret.put("hasPre", data.getBoolean("hasPre"));
                        ret.put("isPlay", data.getBoolean("isPlay"));
                        ret.put("duration", data.getInt("duration"));
                        ret.put("postion", data.getInt("postion"));
                        JSONObject item = new JSONObject();
                        item.put("name", data.get("music_name"));
                        item.put("url", data.get("music_url"));
                        item.put("icon", data.get("music_icon"));
                        ret.put("item", item);
                        PluginResult rs = new PluginResult(PluginResult.Status.OK, ret);
                        rs.setKeepCallback(true);
                        timerCallback.sendPluginResult(rs);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
                return true;
            }
        });
        musicConnect = new MusicConnect();
        Intent intent = new Intent(cordova.getActivity(), MusicService.class);
        cordova.getActivity().bindService(intent, musicConnect, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onDestroy() {
        musicController.clean();
        cordova.getActivity().unbindService(musicConnect);
        super.onDestroy();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("play")) {
            musicController.play(args.getInt(0));
        } else if (action.equals("pause")) {
            musicController.pause();
        } else if (action.equals("resume")) {
            musicController.resume();
        } else if (action.equals("isPlay")) {
            callbackContext.success(musicController.isPlay() ? 1 : 0);
            return true;
        } else if (action.equals("hasNext")) {
            callbackContext.success(musicController.hasNext() ? 1 : 0);
            return true;
        } else if (action.equals("hasPre")) {
            callbackContext.success(musicController.hasPre() ? 1 : 0);
            return true;
        } else if (action.equals("seek")) {
            musicController.seekto(args.getInt(0));
        } else if (action.equals("next")) {
            musicController.nextPlay();
        } else if (action.equals("pre")) {
            musicController.prePlay();
        } else if (action.equals("add")) {
            musicController.add(new MusicItem(args.getJSONObject(0)));
        } else if (action.equals("addlist")) {
            JSONArray obj = args.getJSONArray(0);
            List<MusicItem> list = new ArrayList<>();
            for (int i = 0; i < obj.length(); i++) {
                list.add(new MusicItem(obj.getJSONObject(i)));
            }
            musicController.add(list);
        } else if (action.equals("list")) {
            List<MusicItem> list = musicController.list();
            JSONArray ret = new JSONArray();
            for (int i = 0; i < list.size(); i++) {
                ret.put(list.get(i));
            }
            callbackContext.success(ret);
            return true;
        } else if (action.equals("clean")) {
            musicController.clean();
        } else if (action.equals("timer")) {
            timerCallback = callbackContext;
            PluginResult rs = new PluginResult(PluginResult.Status.OK, new JSONObject("{\"action\":\"init\"}"));
            rs.setKeepCallback(true);
            callbackContext.sendPluginResult(rs);
            return true;
        }
        callbackContext.success();
        return true;
    }


    public class MusicConnect implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicController = (MusicService.MusicController) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
