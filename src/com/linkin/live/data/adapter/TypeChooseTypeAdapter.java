package com.linkin.live.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linkin.live.data.model.ChannelType;
import com.linkin.live.data.provider.R;

import java.util.List;

public class TypeChooseTypeAdapter extends BaseAdapter{
    Context context;
    List<ChannelType> typeList;
    public TypeChooseTypeAdapter(Context context,List<ChannelType> typeList){
        this.context = context;
        this.typeList = typeList;
    }
    
    @Override
    public int getCount() {
        return typeList!=null?typeList.size():0;
    }

    @Override
    public Object getItem(int position) {
        return typeList.get(position);
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
            view=LayoutInflater.from(context).inflate(R.layout.type_choose_typelist_item,null); 
            holder.txtName = (TextView) view.findViewById(R.id.txt_name);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        
        ChannelType type = typeList.get(position);
        holder.txtName.setText(type.getName());
        return view;
    }
    
    static class ViewHolder {
        TextView txtName;
    }
}
