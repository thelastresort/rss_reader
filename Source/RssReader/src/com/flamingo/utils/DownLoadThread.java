package com.flamingo.utils;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.webkit.WebView;

import com.flamingo.dao.ItemDAO;
import com.flamingo.domain.Channel;
import com.flamingo.domain.Item;
import com.flamingo.engine.ChannelEngine;
import com.flamingo.engine.ItemEngine;

public class DownLoadThread extends Thread
{
	private static final int UPDATE_CHANNEL_COMPLETED = 10;
	private static final int UPDATE_CHANNEL_PROGRESS = 11;
	private static final int UPDATE_CHANNEL_TITLE = 12;
	private static final int UPDATE_FINISH = 13;
	public WebView mWebView;
	public Handler mHanlder;
	public int mChannelsCount = 0;
	public int mItemsCount = 0;
	public int mNowChannelCount = 0;
	public int mNowItemCount = 0;
	public ArrayList<Channel> mChannelsNeedDown;
	public Context mContext;
	public boolean _run = true;
	public Channel mNowChannel;
	public Item mNowItem;
	private static DownLoadThread mDownLoadThread; 
	static public DownLoadThread getInstance(WebView webView, Handler handler, Context context,
			ArrayList<Channel> needDownChannels)
	{
		if(mDownLoadThread != null)
		{
			return mDownLoadThread;
		}
		else
		{
			mDownLoadThread = new DownLoadThread(webView, handler, context, needDownChannels);
			return mDownLoadThread;
		}
		
		
	}
	
	private DownLoadThread(WebView webView, Handler handler, Context context,
			ArrayList<Channel> needDownChannels)
	{
		mWebView = webView;
		mHanlder = handler;
		mContext = context;
		mChannelsNeedDown = needDownChannels;
	}

	public void stopThread(boolean run)
	{
		_run = !run;
	}

	@Override
	public void run()
	{// 初始化必要的工具类
		ItemEngine itemEngine = new ItemEngine(mContext);
		ItemDAO itemDAO = new ItemDAO(mContext);
		ChannelEngine channelEngine = new ChannelEngine(mContext);
		// 对需要更新的 源进行遍历
		for (int j = 0; j < mChannelsNeedDown.size(); j++)
		{
			if (_run)
			{
				// 获取源
				Channel channel = mChannelsNeedDown.get(j);
				mNowChannel = channel;
				// 获取源计数
				mNowChannelCount = j + 1;
				// 发送更新源的进度
				mHanlder.sendEmptyMessage(UPDATE_CHANNEL_TITLE);
				// 根据源获取Rss更新项目并且存进数据库
				itemEngine.getItems(mNowChannel);
				// 再从数据库中取得源
				ArrayList<Item> items = itemDAO
						.getItemsByChannel(mChannelsNeedDown.get(j));
				mItemsCount = items.size();
				// 遍历所有的项目进行下载
				for (int i = 0; i < items.size(); i++)
				{
					if (_run)
					{
						mNowItem = items.get(i);
						mNowItemCount = i;
						mHanlder.sendEmptyMessage(UPDATE_CHANNEL_COMPLETED);
						mHanlder.sendEmptyMessage(UPDATE_CHANNEL_PROGRESS);
						// 下载前先要检查这个ITEM是否已经下载了，通过检查item表的isDownload位是否为0来判断
						if (!itemDAO.checkIsDownload(mNowItem))
						{
							channelEngine.downLoadItem(mWebView, items.get(i));
							itemDAO.setItemDownloaded(mNowItem);
						}
					}
				}
			}
		}
		if (_run)
			mHanlder.sendEmptyMessage(UPDATE_FINISH);

		super.run();
	}
}
