package com.linkin.live.data.db;


import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;


public class DBFavHelper {
	private static final String DATABASE_NAME = "fav.db";
	private static final String DATABASE_TABLE = "table_fav";
	private static final int DATABASE_VERSION = 2;

	public static final String _ID = "_id";
	public static final String CHANNEL_ID = "channel_id"; // ��Ŀid

	private static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE + " (" + _ID + " INTEGER PRIMARY KEY, "
			+ CHANNEL_ID + " TEXT" + ");";
	
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	public DBFavHelper(Context ctx){
		context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	/*
	 * ����ݿ�
	 */
	public SQLiteDatabase open() {
		db = DBHelper.getWritableDatabase();
		return db;
	}

	/*
	 * �ر���ݿ�
	 */
	public void close() {
		DBHelper.close();
	}

	/*
	 *���ղؼ���Ӽ�¼ 
	 */
	public long insert(String channelId){
		ContentValues initialValues = new ContentValues();
		Bundle bundle = getItem(channelId);
		if(bundle==null){
			initialValues.put(CHANNEL_ID, channelId);
			return db.insert(DATABASE_TABLE, null, initialValues);
		}else{
			return bundle.getLong(_ID);
		}
	}
	
	/*
	 * �����ݿ����idɾ�����
	 */
	public boolean delete(int _id) {
		return db.delete(DATABASE_TABLE, _ID + "=" + _id, null) > 0;
	}
	/*
	 * ���channel idɾ�����
	 */
	public boolean deleteByChannelID(String channelId) {
		return db.delete(DATABASE_TABLE, CHANNEL_ID + "='" + channelId+"'", null) > 0;
	}
	
	
	/*
	 * ��ȡ�����ղص�Ƶ��id
	 */
	public Map<String,Boolean> getAll(){
		Map<String,Boolean> favMap = new HashMap<String, Boolean>();
		Cursor cur = db.query(DATABASE_TABLE, null, null, null, null, null,
				null);
		while(cur.moveToNext()){
			String channelId = cur.getString(cur.getColumnIndex(CHANNEL_ID));
			favMap.put(channelId, true);
		}
		return favMap;
	}
	
	/*
	 * 获取收藏信息
	 */
	public Bundle getItem(String channelId){
		Bundle bundle = null; 
		Cursor cur = db.query(true, DATABASE_TABLE, null, CHANNEL_ID + "=?",
				new String[]{channelId}, null, null, null, null); 
		if (cur != null && cur.moveToFirst()) {
			bundle = getDataFromCursor(cur);
		}
		
		return bundle;
	}
	
	private Bundle getDataFromCursor(Cursor cur){
		Bundle bundle = new Bundle();
		long id = cur.getInt(cur.getColumnIndex(_ID));
		bundle.putLong(_ID, id);
		return bundle;
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			db.execSQL(DATABASE_CREATE);
		}
	}
}
