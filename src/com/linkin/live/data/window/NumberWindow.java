package com.linkin.live.data.window;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.linkin.live.data.Config;
import com.linkin.live.data.model.Channel;
import com.linkin.live.data.provider.R;
import com.linkin.window.BaseWindow;

import java.util.ArrayList;
import java.util.List;


public class NumberWindow extends BaseWindow {
	LinearLayout layoutBox;
	TextView txtNum, txtName;
	ListView listView;
	NumberListAdapter adapter;
	LinearLayout layoutFocus;
	List<Channel> dataList;
	List<Channel> channelList;
	LayoutInflater mInflater;

	String disNum = "";

	public static boolean isNumberKey(int key) {
		boolean bo = KeyEvent.KEYCODE_NUMPAD_0 == key
				|| KeyEvent.KEYCODE_NUMPAD_1 == key
				|| KeyEvent.KEYCODE_NUMPAD_2 == key
				|| KeyEvent.KEYCODE_NUMPAD_3 == key
				|| KeyEvent.KEYCODE_NUMPAD_4 == key
				|| KeyEvent.KEYCODE_NUMPAD_5 == key
				|| KeyEvent.KEYCODE_NUMPAD_6 == key
				|| KeyEvent.KEYCODE_NUMPAD_7 == key
				|| KeyEvent.KEYCODE_NUMPAD_8 == key
				|| KeyEvent.KEYCODE_NUMPAD_9 == key
		        || KeyEvent.KEYCODE_0 == key
		        || KeyEvent.KEYCODE_1 == key
		        || KeyEvent.KEYCODE_2 == key
		        || KeyEvent.KEYCODE_3 == key
		        || KeyEvent.KEYCODE_4 == key
		        || KeyEvent.KEYCODE_5 == key
		        || KeyEvent.KEYCODE_6 == key
		        || KeyEvent.KEYCODE_7 == key
		        || KeyEvent.KEYCODE_8 == key
		        || KeyEvent.KEYCODE_9 == key;
		
		return bo;
	}

	public NumberWindow(Context context) {
		super(context, R.layout.number_choose, 346, 603);
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dataList = new ArrayList<Channel>();

		layoutBox = (LinearLayout) parent.findViewById(R.id.layoutBox);
		// layoutBox.setOnKeyListener(new OnKeyListener() {
		// @Override
		// public boolean onKey(View v, int keyCode, KeyEvent event)
		// {Log.i(Config.TAG,"keyCode:"+keyCode);
		// if(isNumberKey(keyCode)){
		// changeKey(keyCode);
		// return false;
		// }
		// return true;
		// }
		// });
		layoutFocus = (LinearLayout) parent.findViewById(R.id.layoutFocus);
		layoutFocus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(dataList.size() > 0){
					int pos = findPostion(dataList.get(0));
					if(onClickListener!=null){
						onClickListener.onClickListener(pos);
					}
				}
			}
		});
		layoutFocus.setOnKeyListener(onKeyListener);
		txtNum = (TextView) parent.findViewById(R.id.txtNum);
		txtName = (TextView) parent.findViewById(R.id.txtName);
		listView = (ListView) parent.findViewById(R.id.listView);
		adapter = new NumberListAdapter();
		listView.setAdapter(adapter);
		listView.setOnKeyListener(onKeyListener);
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int pos = findPostion(dataList.get(arg2));
				if(onClickListener!=null){
					onClickListener.onClickListener(pos);
				}
			}
		});
	}

	private int findPostion(Channel ch){
		for(int i = 0;i<channelList.size();i++){
			if(channelList.get(i) == ch){
				return i;
			}
		}
		return 0;
	}
	
	OnKeyListener onKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				Log.i(Config.TAG, "keyCode:" + keyCode);
				if (isNumberKey(keyCode)) {
					changeKey(keyCode);
					return true;
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
					if (!layoutFocus.hasFocus()) {
						layoutFocus.requestFocus();
					}
					if (disNum.length() > 0) {
						disNum = disNum.substring(0, disNum.length() - 1);
					}
					show();
					return true;
				}
			}
			return false;
		}

	};

	@Override
	public void setData(Object data) {
		if (data instanceof List) {
			channelList = (List<Channel>) data;
		} else {
			disNum = "";
			dataList.clear();
			changeKey((Integer) data);
		}
	}

	private void changeKey(int key) {
		if (disNum.length() >= 4) {
			return;
		}
		int k = getKeyNum(key);
		disNum += k;
		show();
	}

	private void show() {
		txtNum.setText(disNum);
		findKyeList();
		if (dataList.size() > 0) {
			txtName.setText(dataList.get(0).getName());
		} else {
			txtName.setText("");
		}

	}

	private void findKyeList() {
		dataList.clear();
		if (channelList == null) {
			return;
		}
		for (Channel ch : channelList) {
			String s = "" + ch.getNum();
			if (s.indexOf(disNum) == 0) {
				dataList.add(ch);
			}
		}
		adapter.notifyDataSetChanged();
	};

	private int getKeyNum(int key) {
		int k = 0;
		switch (key) {
		case KeyEvent.KEYCODE_NUMPAD_0:
			k = 0;
			break;
		case KeyEvent.KEYCODE_0:
            k = 0;
            break;
		case KeyEvent.KEYCODE_NUMPAD_1:
			k = 1;
			break;
		case KeyEvent.KEYCODE_1:
            k = 1;
            break;
		case KeyEvent.KEYCODE_NUMPAD_2:
			k = 2;
			break;
		case KeyEvent.KEYCODE_2:
		    k = 2;
		    break;
		case KeyEvent.KEYCODE_NUMPAD_3:
			k = 3;
			break;
		case KeyEvent.KEYCODE_3:
		    k = 3;
		    break;
		case KeyEvent.KEYCODE_NUMPAD_4:
			k = 4;
			break;
		case KeyEvent.KEYCODE_4:
		    k = 4;
		    break;
		case KeyEvent.KEYCODE_NUMPAD_5:
			k = 5;
			break;
		case KeyEvent.KEYCODE_5:
            k = 5;
            break;
		case KeyEvent.KEYCODE_NUMPAD_6:
			k = 6;
			break;
		case KeyEvent.KEYCODE_6:
		    k = 6;
		    break;
		case KeyEvent.KEYCODE_NUMPAD_7:
			k = 7;
			break;
		case KeyEvent.KEYCODE_7:
		    k = 7;
		    break;
		case KeyEvent.KEYCODE_NUMPAD_8:
			k = 8;
			break;
		case KeyEvent.KEYCODE_8:
		    k = 8;
		    break;
		case KeyEvent.KEYCODE_NUMPAD_9:
			k = 9;
			break;
		case KeyEvent.KEYCODE_9:
		    k = 9;
		    break;

		default:
			break;
		}
		return k;
	}

	class NumberListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return dataList != null ? dataList.size() : 0;
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
			Channel ch = dataList.get(position);

			TextView txtNum = (TextView) convertView.findViewById(R.id.txtNum);
			txtNum.setText(ch.getNumStr());

			TextView txtName = (TextView) convertView
					.findViewById(R.id.txtName);
			txtName.setText(ch.getName());
			return convertView;
		}

	}
}
