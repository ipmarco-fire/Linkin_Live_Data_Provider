
package com.linkin.live.data.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.linkin.live.data.Config;
import com.linkin.live.data.LiveDataProvider;
import com.linkin.live.data.model.ChannelType;
import com.linkin.live.data.model.IpInfo;
import com.linkin.live.data.parser.ChannelParser;
import com.linkin.live.data.parser.IpInfoParser;
import com.linkin.live.data.provider.R;
import com.linkin.utils.BackgroundExecutor;
import com.linkin.utils.SynHtmlUtil;

import org.json.JSONException;

import java.util.Calendar;
import java.util.List;

public class LiveDataService extends Service {
    public static byte[] mMutex = new byte[1];
    public static final String SHARED_NAME = "com.linkin.live.live_data_service";
    public static final String LAST_UPDATE = "last_update";
    public static final String CHANNELS_NAME = "channels";
    private static final int DAY = 24 * 60 * 60 * 1000;

    public static final String REFRESH_CHANNEL = "refresh_channel";

    Handler handler = new Handler();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Config.TAG, "LiveDataService onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(Config.TAG, "LiveDataService onStartCommand");
        if (intent != null && intent.hasExtra(REFRESH_CHANNEL)) {
            BackgroundExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    refresh();
                }
            });
        } else {
            BackgroundExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    parser();
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void refresh() {
        synchronized (mMutex) {
            Log.i(Config.TAG,"start refresh");
            getChannelJSON();
            SharedPreferences prefs = getSharedPreferences(SHARED_NAME, 0);
            String channelContent = prefs.getString(CHANNELS_NAME, null);
            if (channelContent != null) {
                String localProvince = LiveDataProvider.getShared(getApplicationContext(), LiveDataProvider.LOCAL_PROVINCE);
                final boolean bo = doParser(channelContent, localProvince);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bo) {
                            Toast.makeText(getApplicationContext(), "数据更新成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "数据更新失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        }
    }

    private void parser() {
        synchronized (mMutex) {
            if (!LiveDataProvider.isEmpty()) {
                return;
            }

            getIpInfo();

            SharedPreferences prefs = getSharedPreferences(SHARED_NAME, 0);
            long lastUpdate = prefs.getLong(LAST_UPDATE, 0);
            long curTime = Calendar.getInstance().getTimeInMillis();
            if ((curTime - lastUpdate) >= 10 * DAY) { // 如果 10 天没有更新
                getChannelJSON();
                getIpInfo();
            }

            String channelContent = prefs.getString(CHANNELS_NAME, null);
            if (channelContent != null) {
                String localProvince = LiveDataProvider.getShared(getApplicationContext(), LiveDataProvider.LOCAL_PROVINCE);
                doParser(channelContent, localProvince);
            }

            lastUpdate = prefs.getLong(LAST_UPDATE, 0);
            if ((curTime - lastUpdate) >= 1 * DAY) { // 如果数据更新超过1天
                getChannelJSON();
                getIpInfo();
            }
        }
    }

    private boolean getChannelJSON() {
        String url = getChannelUrl();
        String channelContent = SynHtmlUtil.get(url);
        Log.i(Config.TAG, "end url : " + (channelContent != null && channelContent.indexOf("typeLists") > 0));
        if (channelContent != null && channelContent.indexOf("typeLists") > 0) {
            SharedPreferences prefs = getSharedPreferences(SHARED_NAME, 0);
            long curTime = Calendar.getInstance().getTimeInMillis();
            Editor editor = prefs.edit();
            editor.putString(CHANNELS_NAME, channelContent);
            editor.putLong(LAST_UPDATE, curTime);
            editor.commit();
            return true;
        }
        return false;
    }

    private String getChannelUrl() {
        Context context = getApplicationContext();
        Boolean isAndroid = context.getResources().getBoolean(R.bool.is_android);
        Boolean multiLanguage = context.getResources().getBoolean(R.bool.multi_language);

        String isp = LiveDataProvider.getShared(getApplicationContext(), LiveDataProvider.LOCAL_ISP);
        String url = "";
        if (isAndroid) {
            url = "channelListAndroid_.html";
            if (isp != null) {
                if (Config.ISP_DIANXIN.equals(isp)) { // 如果是电信
                    url = "channelListAndroid_dx.html";
                } else if (Config.ISP_LIANTONG.equals(isp)) { // 如果是联通
                    url = "channelListAndroid_lt.html";
                }
            }
        } else {
            url = "channelListNew_.html";
            if (isp != null) {
                if (Config.ISP_DIANXIN.equals(isp)) { // 如果是电信
                    url = "channelListNew_dx.html";
                } else if (Config.ISP_LIANTONG.equals(isp)) { // 如果是联通
                    url = "channelListNew_lt.html";
                } else if (multiLanguage) { // 如果支持多语言
                    String lang = context.getResources().getConfiguration().locale.getCountry();
                    if (!lang.equals("CN")) { // 如果不在中国
                        url = "channelListNew_en.html";
                    }
                }
            }
        }
        return Config.GOOGLE_CODE + url;
    }

    private boolean doParser(String channelContent, String localProvince) {
        ChannelParser channelParser = new ChannelParser(this);
        List<ChannelType> typeList = null;
        try {
            typeList = channelParser.parser(channelContent, localProvince);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (typeList != null && typeList.size() > 0) {
            LiveDataProvider.setTypeList(typeList);
            Intent it = new Intent(Config.ACTION_AFTER_INIT_LIVE);
            sendBroadcast(it);
            return true;
        } else {
            Toast.makeText(this, "parser error", Toast.LENGTH_LONG).show();
            Log.e(Config.TAG, "parser error");
        }
        return false;
    }

    private void getIpInfo() {
        String ipContent = SynHtmlUtil.get(Config.IP_URL);
        try {
            IpInfo ipInfo = IpInfoParser.parser(ipContent);
            LiveDataProvider.saveShared(getApplicationContext(), LiveDataProvider.LOCAL_PROVINCE, ipInfo.getProvince());
            LiveDataProvider.saveShared(getApplicationContext(), LiveDataProvider.LOCAL_ISP, ipInfo.getIsp());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
