
package com.linkin.live.data.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.linkin.live.data.Config;
import com.linkin.live.data.LiveDataProvider;
import com.linkin.live.data.model.EPG;
import com.linkin.live.data.parser.EpgParser;
import com.linkin.utils.BackgroundExecutor;
import com.linkin.utils.SynHtmlUtil;

import org.json.JSONException;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EpgDataService extends Service {
    public static final String SHARED_NAME = "com.linkin.live.live_epg_service";
    public static final String SHARED_DATE_NAME = "com.linkin.live.live_epg_date";
    private static final String DATE_FORMAT = "yyyyMMdd";
    public static byte[] mMutex = new byte[1];
    private static final int DAY = 24 * 60 * 60 * 1000;
    private static final int MINUTE = 60 * 1000;
    public static final int MSG_PARSER_EPG = 1;

    private String lastParserDate = "";

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSG_PARSER_EPG) {
                BackgroundExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        parser();
                    }
                });
            }
        };
    };

    @Override
    public void onCreate() {
        Log.i(Config.TAG, "EpgDataService onCreate");
    };
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(Config.TAG, "EpgDataService onStartCommand");
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
            Log.i(Config.TAG, "hasMessages:" + mHandler.hasMessages(MSG_PARSER_EPG));
            if (mHandler.hasMessages(MSG_PARSER_EPG)) {
                return;
            }

            doParser();
        }
    }

    private void doParser() {
        SharedPreferences prefs = getSharedPreferences(SHARED_NAME, 0);

        Calendar rightNow = Calendar.getInstance();
        String curDate = (String) DateFormat.format(DATE_FORMAT, rightNow);
        if (curDate.equals(lastParserDate)) { // 如果
            long delay = (DAY - rightNow.getTimeInMillis() % DAY + 1);
            mHandler.sendEmptyMessageDelayed(MSG_PARSER_EPG, delay);
        } else {
            if (!prefs.contains(curDate)) { // 如果当天epg没有有下载
                boolean bo = getEpgJSON(curDate);
                if (!bo) {
                    mHandler.sendEmptyMessageDelayed(MSG_PARSER_EPG, 5 * MINUTE);
                    return;
                }
            }
            Log.i(Config.TAG, curDate);
            String epgContent = prefs.getString(curDate, "");
            doParser(epgContent);

            lastParserDate = curDate;
            long delay = (DAY - rightNow.getTimeInMillis() % DAY + 1);
            mHandler.sendEmptyMessageDelayed(MSG_PARSER_EPG, delay);   //下一次解析时间
            
            removeOldData(Integer.parseInt(curDate));

            for (int i = 1; i <= 7; i++) {     //下载最近7天的数据
                rightNow.add(Calendar.DATE, 1);
                String d = (String) DateFormat.format(DATE_FORMAT, rightNow);
                if (!prefs.contains(d)) {
                    boolean bo = getEpgJSON(d);
                    
                }
            }
        }
    }

    private void removeOldData(int curDate) {
        SharedPreferences prefs = getSharedPreferences(SHARED_NAME, 0);
        SharedPreferences datePrefs = getSharedPreferences(SHARED_DATE_NAME, 0);

        Editor editor = prefs.edit();
        Editor dateEditor = datePrefs.edit();

        Map<String, Integer> all = (Map<String, Integer>) datePrefs.getAll();
        if (all != null) {
            Set<String> keySet = all.keySet();
            for (Iterator it = keySet.iterator(); it.hasNext();) {
                String key = (String) it.next();
                int d = Integer.parseInt(key);
                if (d < curDate) {
                    editor.remove(key);
                    dateEditor.remove(key);
                }
            }
        }

        editor.commit();
        dateEditor.commit();
    }

    public void doParser(String epgContent) {
        Map<String, List<EPG>> epgMap = null;
        try {
            epgMap = EpgParser.parser(epgContent);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (epgMap != null && epgMap.size() > 0) {
            LiveDataProvider.setEpgMap(epgMap);
            Intent it = new Intent(Config.ACTION_AFTER_INIT_EPG);
            sendBroadcast(it);
        } else {
            Toast.makeText(this, "parser epg error", Toast.LENGTH_LONG).show();
        }
    }

    private boolean getEpgJSON(String date) {
        String url = Config.EPG_URL + date + ".html";
        Log.i(Config.TAG, url);
        String epgContent = SynHtmlUtil.get(url);
        if (epgContent != null && epgContent.indexOf("{\"") > -1) {
            SharedPreferences prefs = getSharedPreferences(SHARED_NAME, 0);
            Editor editor = prefs.edit();
            editor.putString(date, epgContent);
            editor.commit();

            addDate(date);
            return true;
        }
        return false;
    }

    private void addDate(String date) {
        SharedPreferences prefs = getSharedPreferences(SHARED_DATE_NAME, 0);
        Editor editor = prefs.edit();
        editor.putInt(date, Integer.parseInt(date));
        editor.commit();
    }
}
