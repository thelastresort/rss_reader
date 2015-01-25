package com.flamingo.dao;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.flamingo.domain.Channel;
import com.flamingo.domain.Item;
import com.flamingo.utils.DataUtil;

public class ItemDAO
{
	private Context mContext;
	public ItemDAO(Context context)
	{
		mContext  = context;
	}
	public ArrayList<Item> getItemsByChannel(Channel channel)
	{
		ArrayList<Item> items = new ArrayList<Item>();
		RssReaderDBHelper dbHelper = new RssReaderDBHelper(mContext);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query("item", new String[]{"id","title","link",
				"description","pubDate","category","author","channel"}, 
				"channel=?", new String[]{channel.getId()+""}, null, null, "id asc");
		while (cursor.moveToNext())
		{
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String title = cursor.getString(cursor.getColumnIndex("title"));
			String link = cursor.getString(cursor.getColumnIndex("link"));
			String description = cursor.getString(cursor.getColumnIndex("description"));
			Date pubDate = DataUtil.stringtoUtilDate(cursor.getString(cursor.getColumnIndex("pubDate")));
			String category = cursor.getString(cursor.getColumnIndex("category"));
			String author = cursor.getString(cursor.getColumnIndex("author"));
			int channelId = cursor.getInt(cursor.getColumnIndex("channel"));
			
			Item item = new Item();
			item.setAuthor(author);
			item.setCategory(category);
			item.setChannel(channelId);
			item.setDescription(description);
			item.setId(id);
			item.setLink(link);
			item.setPubDate(pubDate);
			item.setTitle(title);
			
			items.add(item);			
		}
		db.close();
		return items;
	}
	
	public void addItem(Item item)
	{
		RssReaderDBHelper dbHelper = new RssReaderDBHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(!db.query("item", new String[]{"title"}, "title=?", new String[]{item.getTitle()}, null, null, null).moveToNext())
		{
			ContentValues values = new ContentValues();
			values.put("author", item.getAuthor());
			values.put("category", item.getCategory());
			values.put("description", item.getDescription());
			values.put("link", item.getLink());
			values.put("title", item.getTitle());
			values.put("channel", item.getChannel());
			values.put("pubDate", DataUtil.utilDateToString(item.getPubDate()));
			
			db.insert("item", null, values);
		}
		else
		{
		}
		db.close();
	}
	
	public boolean checkIsDownload(Item item)
	{
		RssReaderDBHelper dbHelper = new RssReaderDBHelper(mContext);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query("item", new String[]{"isDownLoad"}, "id=?", new String[]{item.getId()+""}, null, null, null);
		if(cursor.moveToNext())
		{
			int isDownLoad = cursor.getInt(cursor.getColumnIndex("isDownLoad"));
			if(isDownLoad == 0)
			{
				db.close();
				return false;
			}
			else {
				db.close();
				return true;
			}
		}
		db.close();
		return false;
	}
	
	public void setItemDownloaded(Item item)
	{
		RssReaderDBHelper dbHelper = new RssReaderDBHelper(mContext);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("isDownLoad", 1);
		db.update("item", values, "id=?", new String[]{item.getId()+""});
		db.close();
	}
	
	public void deleteAllItem()
	{
		RssReaderDBHelper dbHelper = new RssReaderDBHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete("item", null, null);
	}
}
