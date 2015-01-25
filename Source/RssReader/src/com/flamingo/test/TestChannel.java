package com.flamingo.test;

import java.util.ArrayList;

import android.test.AndroidTestCase;

import com.flamingo.dao.ChannelDAO;
import com.flamingo.domain.Channel;
import com.flamingo.engine.ChannelEngine;

public class TestChannel extends AndroidTestCase
{
	public void testEngineGetChannels() 
	{
		String[] rsss = {
//				"http://feed.qiushibaike.com/rss",
//				"http://feed.williamlong.info/",
//				"http://feeds.geekpark.net/"
//				"http://feed.williamlong.info/",
//				"http://www.guokr.com/rss/",
//				"http://feed.feedsky.com/my",
				"http://www.alibuybuy.com/feed",
//				"http://www.u148.net/rss/",
//				"http://www.infzm.com/rss/home/rss2.0.xml",
//				"http://www.zaobao.com/yl/yl.xml",
//				"http://www.ftchinese.com/rss/column/007000004",
//				"http://feed.feedsky.com/leica"
		};

		ChannelEngine ce = new ChannelEngine(getContext());
		ChannelDAO cDao = new ChannelDAO(getContext());
		ArrayList<Channel> channels = ce.getChannels(rsss);
		for (Channel channel : channels)
		{
			cDao.addChannel(channel);
		}
	}
//	public void testGetChannelFromDB()
//	{
//		ChannelDAO cDao = new ChannelDAO(getContext());
//		ArrayList<Channel> channels = cDao.getAllChannels();
//		for (Channel channel : channels)
//		{
//			Log.i("my",channel.getTitle());
//		}
//	}
}
