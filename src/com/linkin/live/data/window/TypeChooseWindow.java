
package com.linkin.live.data.window;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.linkin.live.data.Config;
import com.linkin.live.data.LiveDataProvider;
import com.linkin.live.data.adapter.TypeChooseChannelAdapter;
import com.linkin.live.data.adapter.TypeChooseEpgAdapter;
import com.linkin.live.data.adapter.TypeChooseTypeAdapter;
import com.linkin.live.data.model.Channel;
import com.linkin.live.data.model.ChannelType;
import com.linkin.live.data.model.EPG;
import com.linkin.live.data.provider.R;
import com.linkin.window.BaseWindow;

import java.util.List;
import java.util.Map;

public class TypeChooseWindow extends BaseWindow {

    ListView listViewType, listViewChannel, listViewEpg;
    LinearLayout layoutEpg;
    TypeChooseTypeAdapter adapterType;
    TypeChooseChannelAdapter adapterChannel;
    TypeChooseEpgAdapter adapterEpg;
    TextView txtTypeName;
    
    View viewFocus,viewShowFocus;
    int curTypeId ,curItemId;
    public TypeChooseWindow(Context context) {
        super(context, R.layout.type_choose, 1280, 720);
        
        layoutEpg = (LinearLayout) parent.findViewById(R.id.layout_epg);
        
        listViewType = (ListView) parent.findViewById(R.id.listView_type);
        listViewChannel = (ListView) parent.findViewById(R.id.listView_channel);
        listViewEpg = (ListView) parent.findViewById(R.id.listView_epg);
        txtTypeName = (TextView) parent.findViewById(R.id.txt_type_name);
        viewFocus = parent.findViewById(R.id.view_focus);
        viewShowFocus = parent.findViewById(R.id.view_show_focus);
        
        adapterType = new TypeChooseTypeAdapter(context, LiveDataProvider.getTypeList());
        listViewType.setAdapter(adapterType);
        adapterChannel = new TypeChooseChannelAdapter(context);
        listViewChannel.setAdapter(adapterChannel);
        adapterEpg = new TypeChooseEpgAdapter(context);
        listViewEpg.setAdapter(adapterEpg);
        
        listViewType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ChannelType type = (ChannelType) adapterType.getItem(position);
                adapterChannel.update(type.getChannelList());
                txtTypeName.setText(type.getName());
                
                if (isShowing()) {
                    mHandler.removeMessages(MESSAGE_HIDE);
                    mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE, DELAY_MILLIS);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        listViewType.setOnFocusChangeListener(new OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    layoutEpg.setVisibility(View.GONE);
                    adapterType.setSelectId(-1);
                    adapterEpg.update(null);
                }else{
                    adapterType.setSelectId(listViewType.getSelectedItemPosition());
                }
            }
            
        });
        
        listViewChannel.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Channel ch = (Channel) adapterChannel.getItem(position);
                List<EPG> epgList = LiveDataProvider.getEpgList(ch);
                adapterEpg.update(epgList);
                if (isShowing()) {
                    mHandler.removeMessages(MESSAGE_HIDE);
                    mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE, DELAY_MILLIS);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        listViewChannel.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int typeId = listViewType.getSelectedItemPosition();
                Bundle bundle = new Bundle();
                bundle.putInt("typeId", typeId);
                bundle.putInt("itemId", position);
                if (onClickListener != null) {
                    onClickListener.onClickListener(bundle);
                }
            }
        });
        
        listViewChannel.setOnFocusChangeListener(new OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    layoutEpg.setVisibility(View.VISIBLE);
                }
            }
            
        });
        
        viewShowFocus.setOnFocusChangeListener(new OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i(Config.TAG,"hasFocus:"+hasFocus);
                if(hasFocus){
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listViewChannel.setSelectionFromTop(curItemId, 230);
                            listViewChannel.requestFocus();
                            viewShowFocus.setFocusable(false);
                        }
                    }, 500);
                }
            }
        });
        
        viewFocus.setOnFocusChangeListener(new OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    listViewType.requestFocus();
                }else{
                    
                }
            }
        });
    }

    @Override
    public void setData(Object data) {
        Map map = (Map) data;
        if (map.containsKey("curTypeId") && map.containsKey("curItemId")) { // 定位
            curTypeId = (Integer) map.get("curTypeId");
            curItemId = (Integer) map.get("curItemId");
            listViewType.setSelectionFromTop(curTypeId, 230);
            Log.i(Config.TAG, "curTypeId:" + curTypeId + "  curItemId:" + curItemId);
        }
        Log.i(Config.TAG,"setData");
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        viewShowFocus.setFocusable(true);
        viewShowFocus.requestFocus();
        
        mHandler.removeMessages(MESSAGE_HIDE);
        mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE, DELAY_MILLIS);
    }
}
