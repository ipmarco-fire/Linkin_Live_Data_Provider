
package com.linkin.live.data.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.linkin.live.data.Config;
import com.linkin.live.data.LiveDataProvider;
import com.linkin.live.data.model.Info;
import com.linkin.utils.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class CollectPlayUrlService extends Service {
    private static final String TAG = "CollectPlayUrlService";
    private CollectBinder binder = new CollectBinder();
    private List<Info> infoList;
    private static final int MAX_SIZE = 10;

    @Override
    public IBinder onBind(Intent intent) {
        infoList = new ArrayList<Info>();
        return binder;
    }

    public class CollectBinder extends Binder {
        public void add(Info info){
            infoList.add(info);
            if (infoList.size() >= MAX_SIZE || info.getChecked()==3) {
                post();
            }
        }
        
        public void post(){
            if (infoList == null || infoList.size() == 0) {
                return;
            }
            
            String str = "";
            String operator = "3";    //运营商
            String isp = LiveDataProvider.getShared(getApplicationContext(), LiveDataProvider.LOCAL_ISP);
            if (isp != null) {
                if (Config.ISP_DIANXIN.equals(isp)) { // 如果是电信
                    operator = "1";
                } else if (Config.ISP_LIANTONG.equals(isp)) {  //如果是联通
                    operator = "2";
                }
            }
           
            
            for (int i = 0; i < infoList.size(); i++) {
                if (i > 0) {
                    str += "\r\n";
                }
                Info info = infoList.get(i);
                String str2= info.getId() + " " + operator + " " + info.getSn() + " " + info.getBuffer()
                        + " " + info.getTime() + " " + info.getTestTime() + " " + info.getChecked();
                str += str2;
                Log.e(TAG, str2);
            }
            infoList.clear();
            
            String url = Config.WEB_URL + Config.REPORT_CHANNELS;
            RequestParams params = new RequestParams();
            params.add("result", str);
            HttpUtil.post(url, params, new AsyncHttpResponseHandler(){
                @Override
                public void onFailure(Throwable error) {
                    super.onFailure(error);
                    Log.i(Config.TAG,"onFailure:");
                }
                @Override
                public void onSuccess(String content) {
                    super.onSuccess(content);
                    Log.i(Config.TAG,"onSuccess:"+content);
                }
                @Override
                public void onFinish() {
                    super.onFinish();
                    Log.i(Config.TAG,"onFinish:");
                }
            });
        }
    }
    
    
}
