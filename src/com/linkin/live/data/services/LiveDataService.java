
package com.linkin.live.data.services;

import android.app.Service;
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
import com.linkin.live.data.parser.ChannelParser;
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

    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Config.TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                parser();
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    private void parser() {
        synchronized (mMutex) {
            if (!LiveDataProvider.isEmpty()) {
                return;
            }
            SharedPreferences prefs = getSharedPreferences(SHARED_NAME, 0);
            long lastUpdate = prefs.getLong(LAST_UPDATE, 0);
            long curTime = Calendar.getInstance().getTimeInMillis();
            if ((curTime - lastUpdate) >= 10 * DAY) { // 如果 10 天没有更新
                getChannelJSON();
            }

            String channelContent = prefs.getString(CHANNELS_NAME, null);
            if (channelContent != null) {
                doParser(channelContent);
            }
            
            lastUpdate = prefs.getLong(LAST_UPDATE, 0);
            if ((curTime - lastUpdate) >= 1 * DAY) { // 如果数据更新超过1天
                getChannelJSON();
            }
        }
    }

    private boolean getChannelJSON() {
        String channelContent = SynHtmlUtil.get(Config.CHANNLE_URL);
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

    private void doParser(String channelContent) {
        ChannelParser channelParser = new ChannelParser(this);
        List<ChannelType> typeList = null;
        try {
            typeList = channelParser.parser(channelContent, null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (typeList != null && typeList.size() > 0) {
            LiveDataProvider.setTypeList(typeList);
            Intent it = new Intent(Config.ACTION_AFTER_INIT_LIVE);
            sendBroadcast(it);
        }else{
            Toast.makeText(this, "parser error", Toast.LENGTH_LONG).show();
            Log.e(Config.TAG,"parser error");
        }
    }
}
