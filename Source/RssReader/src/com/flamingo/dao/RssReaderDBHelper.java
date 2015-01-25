package com.flamingo.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RssReaderDBHelper extends SQLiteOpenHelper
{
	
	public RssReaderDBHelper(Context context)
	{
		super(context, "rssreader", null, 1);
	}
	@Override
	public void onCreate(SQLiteDatabase sqliteDB)
	{
		sqliteDB.execSQL("CREATE TABLE channel(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
				"rss TEXT NOT NULL DEFAULT aaa, " +
				"title TEXT NOT NULL," +
				"link TEXT, " +
				"description TEXT NOT NULL, " +
				"pubDate String)");
		sqliteDB.execSQL("CREATE TABLE item(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				"title TEXT NOT NULL, " +
				"link TEXT, " +
				"description TEXT NOT NULL, " +
				"pubDate TEXT, category TEXT, " +
				"author TEXT, channel INTEGER," +
				"isDownLoad INTEGER DEFAULT 0)");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2)
	{
		 System.out.println("update a sqlite database");
	}
	
}
