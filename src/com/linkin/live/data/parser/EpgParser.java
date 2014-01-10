package com.linkin.live.data.parser;


import com.linkin.live.data.model.EPG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EpgParser {
    public static Map<String, List<EPG>> parser(String con) throws JSONException{
        Map<String, List<EPG>> epgMap = new HashMap<String, List<EPG>>();
        JSONObject root = new JSONObject(con);
        Iterator keys = root.keys();
        while(keys.hasNext()){
            String key = (String) keys.next();
            JSONArray objArray = root.getJSONArray(key);
            List list = new ArrayList<EPG>();
            for(int i = 0;i<objArray.length();i++){
                JSONObject obj = objArray.getJSONObject(i);
                String startTime = obj.getString("startTime"); 
                String name = obj.getString("name");
                
                EPG epg = new EPG();
                epg.setStartTime(startTime); 
                epg.setName(name);
                list.add(epg);
            }
            epgMap.put(key, list);
        }
        
        return epgMap;

    }
    
    public static int  getCurPositionEPGList(List<EPG> list){
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
