package com.linkin.live.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.linkin.live.data.model.Channel;
import com.linkin.live.data.model.PlayUrl;
import com.linkin.utils.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class LiveDataUtil {
    Context context;
    public LiveDataUtil(Context context){
        this.context = context;
    }
    public void reportErrorChannel(Channel ch){
        if(ch != null){
            PlayUrl pu = ch.getCurPlayUrl();
            if (pu != null) {
                String id = pu.getId();
                String url = Config.REPORT_ERROR_CHANNEL + id;
                Log.i(Config.TAG,url);
                HttpUtil.get(url, new AsyncHttpResponseHandler(){
                    @Override
                    public void onSuccess(String content) {
                        Toast.makeText(context, "提交成功", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onFailure(Throwable error) {
                        Toast.makeText(context, "提交失败", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
