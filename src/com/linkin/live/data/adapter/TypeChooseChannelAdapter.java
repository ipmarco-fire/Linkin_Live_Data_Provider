package com.linkin.live.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linkin.live.data.model.Channel;
import com.linkin.live.data.provider.R;

import java.util.List;

public class TypeChooseChannelAdapter extends BaseAdapter{
    Context context;
    List<Channel> channelList;
    public TypeChooseChannelAdapter(Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        return channelList!=null?channelList.size():0;
    }

    @Override
    public Object getItem(int position) {
        return channelList.get(position);
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
            view=LayoutInflater.from(context).inflate(R.layout.type_choose_channellist_item,null); 
            holder.txtNum = (TextView) view.findViewById(R.id.txt_num);
            holder.txtName = (TextView) view.findViewById(R.id.txt_name);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        Channel ch = channelList.get(position);
        holder.txtNum.setText(ch.getNumStr());
        holder.txtName.setText(ch.getName());
        return view;
    }
    
    static class ViewHolder {
        TextView txtNum;
        TextView txtName;
    }

    public void update(List<Channel> channelList){
        this.channelList = channelList;
        notifyDataSetChanged();
    }
}
