
package com.linkin.live.data.parser;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import com.linkin.live.data.provider.R;
import com.linkin.live.data.db.FavManager;
import com.linkin.live.data.model.Channel;
import com.linkin.live.data.model.ChannelType;
import com.linkin.live.data.model.PlayUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelParser {
    Context context;

    public ChannelParser(Context context) {
        this.context = context;
    }

    public List<ChannelType> parser(String channelContent, String localIp) throws JSONException {
        Resources res = context.getResources();
        boolean addAll = res.getBoolean(R.bool.add_all);
        boolean addFav = res.getBoolean(R.bool.add_fav);
        boolean multiLanguage = res.getBoolean(R.bool.multi_language);
        return parser(channelContent, localIp, addAll, addFav, multiLanguage);
    }

    public List<ChannelType> parser(String channelContent, String localIp, boolean addAll, boolean addFav, boolean multiLanguage) throws JSONException {
        Map<String, Boolean> favMap = null;
        if (addFav) {
            FavManager favManager = new FavManager(context);
            favMap = favManager.getAll();
        }

        String lang = "CN";
        if (multiLanguage) {
            lang = context.getResources().getConfiguration().locale.getCountry();
        }

        List<ChannelType> typeList = new ArrayList<ChannelType>();
        List<Channel> channelList = new ArrayList<Channel>();

        JSONObject root = new JSONObject(channelContent);
        JSONArray typeListArray = root.getJSONArray("typeLists");

        Map<String, Boolean> idMap = new HashMap<String, Boolean>();

        for (int i = 0; i < typeListArray.length(); i++) {
            JSONObject typeObj = typeListArray.getJSONObject(i);
            String id = typeObj.getString("id");
            String name = typeObj.getString("name");
            String py = typeObj.getString("pinyin");
            if (multiLanguage && typeObj.has("enName") && !lang.equals("CN")) {
                String enName = typeObj.getString("enName");
                if (!enName.equals("")) {
                    name = enName;
                }
            }
            ChannelType type = new ChannelType(id, name, py);
            if (typeObj.has("map")) {
                JSONObject map = typeObj.getJSONObject("map");
                if (map.has("channelLists")) {
                    JSONArray channelLists = map.getJSONArray("channelLists");
                    for (int j = 0; j < channelLists.length(); j++) {
                        JSONObject channelObj = channelLists.getJSONObject(j);
                        Channel ch = parserObjToChannel(channelObj, multiLanguage, lang);
                        type.addChild(ch);
                        String cid = ch.getId();
                        if (!idMap.containsKey(cid)) {
                            channelList.add(ch);
                            idMap.put(cid, true);
                        }
                    }
                }

                if (map.has("subTypeLists")) {
                    JSONArray subTypeLists = map.getJSONArray("subTypeLists");
                    String loacalPinyin = "";
                    for (int m = 0; m < subTypeLists.length(); m++) {
                        JSONObject obj = subTypeLists.getJSONObject(m);
                        String pinyin = obj.getString("pinyin");
                        if (localIp != null && pinyin != null && pinyin.toLowerCase().indexOf(localIp.toLowerCase()) > -1) {
                            loacalPinyin = pinyin;
                            JSONObject map2 = obj.getJSONObject("map");
                            JSONArray channelLists = map2.getJSONArray("channelLists");
                            for (int j = 0; j < channelLists.length(); j++) {
                                JSONObject channelObj = channelLists.getJSONObject(j);
                                Channel ch = parserObjToChannel(channelObj, multiLanguage, lang);
                                type.addChild(ch);
                                String cid = ch.getId();
                                if (!idMap.containsKey(cid)) {
                                    channelList.add(ch);
                                    idMap.put(cid, true);
                                }
                            }
                        }
                    }

                    for (int m = 0; m < subTypeLists.length(); m++) {
                        JSONObject obj = subTypeLists.getJSONObject(m);
                        String pinyin = obj.getString("pinyin");
                        if (!pinyin.equals(loacalPinyin)) {
                            JSONObject map2 = obj.getJSONObject("map");
                            JSONArray channelLists = map2.getJSONArray("channelLists");
                            for (int j = 0; j < channelLists.length(); j++) {
                                JSONObject channelObj = channelLists.getJSONObject(j);
                                Channel ch = parserObjToChannel(channelObj, multiLanguage, lang);
                                type.addChild(ch);
                                String cid = ch.getId();
                                if (!idMap.containsKey(cid)) {
                                    channelList.add(ch);
                                    idMap.put(cid, true);
                                }
                            }
                        }
                    }
                }

            }

            if (type.getChannelList() != null && type.getChannelList().size() > 0) {
                typeList.add(type);
            }
        }

        if(addFav){
            List<Channel> favList = getFavList(channelList, favMap);
            ChannelType fav = new ChannelType("0", context.getResources()
                    .getString(R.string.live_favorites),"fav");
            fav.setChannelList(favList);
            typeList.add(0, fav);
        }
        
        if (addAll) {
            ChannelType all = new ChannelType("0", context.getResources()
                    .getString(R.string.all_channels),"all");
            all.setChannelList(channelList);
            typeList.add(0, all);
        }
        
        return typeList;
    }

    private static Channel parserObjToChannel(JSONObject channelObj, boolean multiLanguage, String lang) throws JSONException {
        String cid = channelObj.getString("id");
        String logo = channelObj.getString("image");
        String name = channelObj.getString("name");
        Boolean isRecommend = channelObj.getBoolean("isRecommend");
        int number = channelObj.getInt("number");

        if (!lang.equals("CN")) {
            if (channelObj.has("enName")) {
                String enName = channelObj.getString("enName");
                if (!enName.equals("")) {
                    name = enName;
                }
            }
        }

        Channel ch = new Channel();
        ch.setId(cid);
        ch.setLogo(logo);
        ch.setName(name);
        ch.setNum(number);
        ch.setRecommend(isRecommend);

        if (channelObj.has("map")) {
            JSONObject chMap = channelObj.getJSONObject("map");
            if (chMap.has("playUrlLists")) {
                JSONArray playUrlLists = chMap
                        .getJSONArray("playUrlLists");
                for (int k = 0; k < playUrlLists.length(); k++) {
                    JSONObject playOjb = playUrlLists
                            .getJSONObject(k);
                    String stbPlayUrl = playOjb
                            .getString("stbPlayUrl");
                    int _type = playOjb.getInt("type");
                    String id = playOjb.getString("id");
                    ch.addPlayUrl(new PlayUrl(id, _type, stbPlayUrl));
                }
            } else if (chMap.has("playLists")) {
                ch.setPlayLists(chMap.getString("playLists"));
            }
        }
        if (channelObj.has("playbackSource")) {
            String source = channelObj
                    .getString("playbackSource");
            ch.setSource(source);
            if (channelObj.has("playbackParam")) {
                String playbackParam = channelObj
                        .getString("playbackParam");
                Bundle bundle = new Bundle();
                if (source.equals("cntv")) {
                    JSONObject playbackParamObj = new JSONObject(
                            playbackParam);
                    int bid = playbackParamObj.getInt("id");
                    String keyname = playbackParamObj
                            .getString("keyname");
                    bundle.putInt("id", bid);
                    bundle.putString("keyname", keyname);
                }
                ch.setBundle(bundle);
            }
        }
        return ch;
    }
    
    public static List<Channel> getFavList(List<Channel> channelList,
            Map<String, Boolean> favMap) {
        List<Channel> list = new ArrayList<Channel>();

        if (favMap != null && channelList != null) {
            for (int i = 0; i < channelList.size(); i++) {
                Channel ch = channelList.get(i);
                boolean isFav = favMap.containsKey(ch.getId()); // 
                if (isFav) {
                    list.add(ch);
                }
            }
        }

        return list;
    }

}
