
package com.linkin.live.data;

import com.linkin.live.data.model.Channel;
import com.linkin.live.data.model.ChannelType;
import com.linkin.live.data.model.EPG;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class LiveDataProvider {
    private static List<ChannelType> typeList = null;
    private static Map<String, List<EPG>> epgMap = null;

    public static List<ChannelType> getTypeList() {
        return typeList;
    }

    public static void setTypeList(List<ChannelType> typeList) {
        LiveDataProvider.typeList = typeList;
    }

    public static boolean isEmpty() {
        return typeList == null || typeList.size() == 0;
    }

    public static Map<String, List<EPG>> getEpgMap() {
        return epgMap;
    }

    public static void setEpgMap(Map<String, List<EPG>> epgMap) {
        LiveDataProvider.epgMap = epgMap;
    }

    public static boolean isEpgEmpty() {
        return epgMap == null || epgMap.size() == 0;
    }

    public static List<EPG> getEpgList(Channel ch){
        if(!isEpgEmpty()){
            if(epgMap.containsKey(ch.getId())){
                List<EPG> epgList = epgMap.get(ch.getId());
                if( epgList != null ){
                    int pos= getCurPositionEPGList(epgList);
                    if(pos > -1){
                        List<EPG> list = new ArrayList<EPG>();
                        for(int i = pos;i<epgList.size();i++){
                            list.add(epgList.get(i));
                        }
                        return list;
                    }
                }
            }
        }
        return null;
    }

    public static int getCurPositionEPGList(List<EPG> list) {
        if (list == null || list.size() == 0) {
            return -1;
        }
        int m24 = 24 * 60;

        int pos = 0;
        Calendar mCalendar = Calendar.getInstance();
        int curTime = mCalendar.get(Calendar.HOUR_OF_DAY) * 60
                + mCalendar.get(Calendar.MINUTE);
        for (int i = 0; i < list.size(); i++) {
            EPG e = list.get(i);
            String st = e.getStartTime().substring(11);
            String[] arr = st.split(":");
            int h = Integer.parseInt(arr[0]) * 60 + Integer.parseInt(arr[1]);
            if (h > curTime) {
                if (i > 0) {
                    return i - 1;
                } else {
                    return 0;
                }
            }
        }
        return pos;
    }
}
