package com.flamingo.test;

import java.util.ArrayList;

import com.flamingo.domain.Channel;
import com.flamingo.domain.Item;
import com.flamingo.engine.ChannelEngine;
import com.flamingo.engine.ItemEngine;

import android.test.AndroidTestCase;
import android.util.Log;

public class TestItem extends AndroidTestCase
{
	public void testAddItem()
	{
		ChannelEngine channelEngine = new ChannelEngine(getContext());
		ItemEngine itemEngine = new ItemEngine(getContext());
		Channel channel = channelEngine.getChannel("http://www.guokr.com/rss/");
		Log.i("my","channel:"+ channel.getTitle());
		Log.i("my","begin to add item");
		channel.setId(2);
		
		ArrayList<Item> items = itemEngine.getItems(channel);
		for (Item item : items)
		{
			Log.i("my", item.getTitle());
		}	
		Log.i("my","end get item");
	}
}
