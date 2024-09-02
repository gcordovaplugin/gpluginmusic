package cn.yingzhichu.cordova.gmusic;

import org.json.JSONObject;

public class MusicItem {
    private String name;
    private String url;
    private String icon;

    public MusicItem(JSONObject obj){
        try{
            name = obj.getString("name");
            url = obj.getString("url");
            icon = obj.getString("icon");
        }catch (Exception e){

        }

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
