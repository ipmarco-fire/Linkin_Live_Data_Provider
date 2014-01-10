package com.linkin.live.data;


import com.linkin.live.data.model.ChannelType;
import com.linkin.live.data.model.EPG;

import java.util.List;
import java.util.Map;

public class LiveDataProvider {
    private static  List<ChannelType> typeList = null;
    private static Map<String, List<EPG>> epgMap = null;

    public static List<ChannelType> getTypeList() {
        return typeList;
    }

    public static void setTypeList(List<ChannelType> typeList) {
        LiveDataProvider.typeList = typeList;
    }
    
    public static boolean isEmpty(){
        return typeList==null || typeList.size() == 0;
    }

    public static Map<String, List<EPG>> getEpgMap() {
        return epgMap;
    }

    public static void setEpgMap(Map<String, List<EPG>> epgMap) {
        LiveDataProvider.epgMap = epgMap;
    }
    
    public static boolean isEpgEmpty(){
        return epgMap==null || epgMap.size() == 0;
    }
}
