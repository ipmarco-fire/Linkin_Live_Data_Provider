
package com.linkin.live.data.window;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

    public TypeChooseWindow(Context context) {
        super(context, R.layout.type_choose, 1280, 720);

        listViewType = (ListView) parent.findViewById(R.id.listView_type);
        listViewChannel = (ListView) parent.findViewById(R.id.listView_channel);
        listViewEpg = (ListView) parent.findViewById(R.id.listView_epg);
        txtTypeName = (TextView) parent.findViewById(R.id.txt_type_name);

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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        listViewChannel.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Channel ch = (Channel) adapterChannel.getItem(position);
                List<EPG> epgList = LiveDataProvider.getEpgList(ch);
                adapterEpg.update(epgList);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void setData(Object data) {
        Map map = (Map) data;
        if (map.containsKey("curTypeId") && map.containsKey("curItemId")) { // 定位
            int curTypeId = (Integer) map.get("curTypeId");
            int curItemId = (Integer) map.get("curItemId");
            listViewType.setSelectionFromTop(curTypeId, 230);
            Log.i(Config.TAG, "curTypeId:" + curTypeId + "  curItemId:" + curItemId);
            listViewChannel.setSelectionFromTop(curItemId, 230);
        }
        Log.i(Config.TAG,"setData");
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        listViewChannel.requestFocus();
        Log.i(Config.TAG,"showAtLocation");
    }
}
