package com.linkin.live.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linkin.live.data.adapter.TypeChooseChannelAdapter.ViewHolder;
import com.linkin.live.data.model.Channel;
import com.linkin.live.data.model.EPG;
import com.linkin.live.data.provider.R;

import java.util.List;

public class TypeChooseEpgAdapter extends BaseAdapter{
    Context context;
    List<EPG> epgList;
    int focusColor,defaultColor;
    public TypeChooseEpgAdapter(Context context){
        this.context = context;
        focusColor = context.getResources().getColor(R.color.live_data_blue);
        defaultColor = context.getResources().getColor(R.color.live_data_white);
    }
    
    @Override
    public int getCount() {
        return epgList!=null?epgList.size():0;
    }

    @Override
    public Object getItem(int position) {
        return epgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            view=LayoutInflater.from(context).inflate(R.layout.type_choose_epglist_item,null); 
            holder.txtName = (TextView) view.findViewById(R.id.txt_name);
            holder.txtTime = (TextView) view.findViewById(R.id.txt_time);
            view.setTag(holder);
            view.setFocusable(false);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        EPG epg = epgList.get(position);
        holder.txtTime.setText(epg.getShortStartTime());
        holder.txtName.setText(epg.getName());
        
        if(position == 0){
            holder.txtTime.setTextColor(focusColor);
            holder.txtName.setTextColor(focusColor);
        }else{
            holder.txtTime.setTextColor(defaultColor);
            holder.txtName.setTextColor(defaultColor);
        }
        
        return view;
    }

    static class ViewHolder {
        TextView txtTime;
        TextView txtName;
    }

    public void update(List<EPG> epgList){
        this.epgList = epgList;
        notifyDataSetChanged();
    }
}
