package com.linkin.live.data.window;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.linkin.live.data.model.Channel;
import com.linkin.live.data.model.ChannelType;
import com.linkin.live.data.provider.R;
import com.linkin.window.BaseWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChannelChooseWindow extends BaseWindow{
    
    List<ChannelType> typeList;
    ViewPager viewPager;
    ChannelPagerAdapter pagerAdapter;
    List<View> pagerList;
    LayoutInflater mInflater;

    public ChannelChooseWindow(Context context) {
        super(context, R.layout.channel_choose, 346, 720);
        
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        viewPager = (ViewPager) parent.findViewById(R.id.viewPager);
        pagerAdapter = new ChannelPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(onPageChangeListener);
    }

    @Override
    public void setData(Object data) {

        Map map = (Map) data;
        if (map.containsKey("typeList")) {
            typeList = (List<ChannelType>) map.get("typeList");
            int curTypeId = (Integer) map.get("curTypeId");
            pagerList = new ArrayList<View>();

            int size = typeList.size();
            for (int i = 0; i < size; i++) {
                ChannelType type = typeList.get(i);
                LinearLayout par = (LinearLayout) mInflater.inflate(
                        R.layout.channel_choose_pager, null);
                TextView txtTitle = (TextView) par.findViewById(R.id.txtTitle);
                txtTitle.setText(type.getName());

                ListView listView = (ListView) par.findViewById(R.id.listView);
                listView.setAdapter(new ChannelListAdapter(type
                        .getChannelList()));
                listView.setOnItemClickListener(onItemClickListener);
                listView.setOnItemSelectedListener(onItemSelectedListener);
                pagerList.add(par);
            }
            pagerAdapter.update();

            if (typeList.size() > 0) {
                viewPager.setCurrentItem(curTypeId + typeList.size() * 50000);
            }
        }else if(map.containsKey("curItemId") && map.containsKey("curItemId")){  //定位
            int curTypeId = (Integer) map.get("curTypeId");
            int curItemId = (Integer) map.get("curItemId");
            
            View par = pagerList.get(curTypeId);
            ListView listView = (ListView) par.findViewById(R.id.listView);
            listView.setSelectionFromTop(curItemId,230);
            
        //  Log.d("curTypeId:","curTypeId:"+curTypeId+"  curItemId:"+curItemId);
        //  viewPager.setCurrentItem(curTypeId + typeList.size() * 50000);
        }
    }

    OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            mHandler.removeMessages(MESSAGE_HIDE);
            mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE, DELAY_MILLIS);
        }

    };

    OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                int position, long id) {
            if (isShowing()) {
                mHandler.removeMessages(MESSAGE_HIDE);
                mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE, DELAY_MILLIS);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            int typePos = viewPager.getCurrentItem();
            int typeId = typePos % pagerList.size();

            int size = typeList.get(typeId).getChannelList().size();

            Bundle bundle = new Bundle();
            bundle.putInt("typeId", typeId);
            bundle.putInt("itemId", arg2 % size);
            if (onClickListener != null) {
                onClickListener.onClickListener(bundle);
            }
        }
    };

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        mHandler.removeMessages(MESSAGE_HIDE);
        mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE, DELAY_MILLIS);
    }

    class ChannelPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return pagerList != null ? Integer.MAX_VALUE : 0;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            int pos = position % pagerList.size();
            ((ViewPager) container).removeView(pagerList.get(pos));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            int pos = position % pagerList.size();
            ((ViewPager) container).addView(pagerList.get(pos), 0);
            return pagerList.get(pos);
        }
        
        
        
        public void update() {
            notifyDataSetChanged();
        }
    }

    class ChannelListAdapter extends BaseAdapter {
        List<Channel> dataList;
        int SIZE = 0;

        public ChannelListAdapter(List<Channel> list) {
            if (list != null) {
                dataList = list;
                SIZE = dataList.size();
            }
        }

        @Override
        public int getCount() {
            return SIZE > 11 ? Integer.MAX_VALUE : SIZE;
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.channel_choose_item,
                        null);
            }
            Channel ch = dataList.get(position % SIZE);

            TextView txtNum = (TextView) convertView.findViewById(R.id.txtNum);
            txtNum.setText(ch.getNumStr());

            TextView txtName = (TextView) convertView
                    .findViewById(R.id.txtName);
            txtName.setText(ch.getName());
            return convertView;
        }

    }
}
