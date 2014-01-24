package com.linkin.live.data.parser;

import com.linkin.live.data.model.IpInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class IpInfoParser {
    public static IpInfo parser(String con) throws JSONException{
        
        JSONObject root = new JSONObject(con);
        
        int ret = root.getInt("ret");
        String start = root.getString("start");
        String end = root.getString("end");
        String country = root.getString("country");
        String province = root.getString("province");
        String city = root.getString("city");
        String district = root.getString("district");
        String isp = root.getString("isp");
        
        IpInfo ipInfo = new IpInfo();
        ipInfo.setRet(ret);
        ipInfo.setCity(city);
        ipInfo.setCountry(country);
        ipInfo.setDistrict(district);
        ipInfo.setEnd(end);
        ipInfo.setIsp(isp);
        ipInfo.setProvince(province);
        ipInfo.setStart(start);
        return ipInfo;
    }
}
