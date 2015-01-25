package com.flamingo.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.flamingo.domain.Channel;
import com.flamingo.utils.DataUtil;

public class ChannelDAO
{
	private Context mContext;

	public ChannelDAO(Context context)
	{
		mContext = context;
	}

	public ArrayList<Channel> getAllChannels()
	{
		ArrayList<Channel> channels = new ArrayList<Channel>();
		RssReaderDBHelper dbHelper = new RssReaderDBHelper(mContext);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query("channel", new String[]
		{ "id", "rss", "title", "link", "description", "pubDate" }, null, null,
				null, null, "id");
		while (cursor.moveToNext())
		{
			Channel channel = new Channel();
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String rss = cursor.getString(cursor.getColumnIndex("rss"));
			String title = cursor.getString(cursor.getColumnIndex("title"));
			String link = cursor.getString(cursor.getColumnIndex("link"));
			String description = cursor.getString(cursor
					.getColumnIndex("description"));
			String pubDate = cursor.getString(cursor.getColumnIndex("pubDate"));
			channel.setId(id);
			channel.setDescription(description);
			channel.setLink(link);
			channel.setPubDate(DataUtil.stringtoUtilDate(pubDate));
			channel.setRss(rss);
			channel.setTitle(title);
			channels.add(channel);
		}
		db.close();
		return channels;
	}

	public boolean addChannel(Channel channel)
	{
		RssReaderDBHelper dbHelper = new RssReaderDBHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues contents = new ContentValues();
		Cursor cursor = db.query("channel", new String[]
		{ "rss" }, "rss=?", new String[]
		{ channel.getRss() }, null, null, null, null);
		if (!cursor.moveToNext())
		{
			contents.put("description", channel.getDescription());
			contents.put("link", channel.getLink());
			contents.put("rss", channel.getRss());
			contents.put("title", channel.getTitle());
			contents.put("pubDate",
					DataUtil.utilDateToString(channel.getPubDate()));
			db.insert("channel", null, contents);
			db.close();
			return true;
		} 
		db.close();
		return false;
	}
	public boolean channelIsExited(Channel channel)
	{
		RssReaderDBHelper dbHelper = new RssReaderDBHelper(mContext);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query("channel", new String[]
				{ "rss" }, "rss=?", new String[]
				{ channel.getRss() }, null, null, null, null);
		if (cursor.moveToNext())
		{
			return true;
		}
		else {
			return false;
		}
	}
	public void deleteChannel(Channel channel)
	{
		//首先先删除channel的item
		RssReaderDBHelper dbHelper = new RssReaderDBHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete("item", "channel=?", new String[]{channel.getId()+""});
		//再删除channel
		db.delete("channel", "id=?", new String[]{channel.getId()+""});
	}
	
}
