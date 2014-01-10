
package com.linkin.live.data.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ipmacro.utils.aes.AESUtil;
import com.linkin.live.data.Config;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class Channel {

    private String id;
    String name;
    String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String enName;
    String logo;
    int typeId;

    int num;
    String source;
    Bundle bundle;
    boolean recommend;
    String playLists;
    int curSourceId = 0;

    public int getCurSourceId() {
        return curSourceId;
    }

    public void setCurSourceId(int curSourceId) {
        this.curSourceId = curSourceId;
    }

    public String getPlayLists() {
        return playLists;
    }

    public void setPlayLists(String playLists) {
        this.playLists = playLists;
    }

    List<PlayUrl> list;

    public PlayUrl getPlayUrl() {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                PlayUrl p = list.get(i);
                String url = p.getUrl();
                if (url != null) {
                    return p;
                }
            }
        }
        return null;
    }

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public PlayUrl getCurPlayUrl() {
        if (this.curSourceId < 0) {
            this.curSourceId = 0;
        }
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(curSourceId);
    }

    boolean isDecrypt = false;

    public void decrypt(Context context) {
        if (isDecrypt) {
            return;
        }
        isDecrypt = true;
        if (playLists != null && !playLists.equals("")) {
            SharedPreferences settings = context.getSharedPreferences(Config.SHARED_NAME, 0);
            String def = settings.getString(num + "", null);
            try {
                String con = AESUtil.decrypt(playLists); // Log.i(Config.TAG,con);
                JSONArray playUrlLists = new JSONArray(con);
                for (int k = 0; k < playUrlLists.length(); k++) {
                    JSONObject playOjb = playUrlLists
                            .getJSONObject(k);
                    String stbPlayUrl = playOjb
                            .getString("stbPlayUrl");
                    int _type = playOjb.getInt("type");
                    if (stbPlayUrl != null && stbPlayUrl.indexOf(".letv.") > -1) {
                        int index = stbPlayUrl.indexOf("?");
                        if (index == -1) {
                            stbPlayUrl += "?sign=live_ipad";
                        } else {
                            stbPlayUrl += "&sign=live_ipad";
                        }
                    }
                    String id = playOjb.getString("id");
                    addPlayUrl(new PlayUrl(id, _type, stbPlayUrl));

                    if (def != null && def.equals(id)) {
                        setCurSourceId(k);
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public List<PlayUrl> getList2() {
        return list;
    }


    public int getCount(){
        return list!=null?list.size():0;
    }

    public List<PlayUrl> getList() {
        getList2();
        if (list != null && curSourceId > -1) { // 如果已经选择视频源,用选中的视频源
            List list2 = new ArrayList<PlayUrl>();
            list2.add(list.get(curSourceId));
            return list2;
        }
        return list;
    }

    public void setList(List<PlayUrl> list) {
        this.list = list;
    }

    public void addPlayUrl(PlayUrl playUrl) {
        if (list == null) {
            list = new ArrayList<PlayUrl>();
        }
        list.add(playUrl);
    }

    public String getNumStr() {
        int n = num;
        int hundred = n / 100;
        n = n - hundred * 100;
        int ten = n / 10;
        n = n - ten * 10;
        String s = "" + hundred + ten + n;
        return s;
    }

    public String getNumStr(int n) {
        int hundred = n / 100;
        n = n - hundred * 100;
        int ten = n / 10;
        n = n - ten * 10;
        String s = "" + hundred + ten + n;
        return s;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

}
