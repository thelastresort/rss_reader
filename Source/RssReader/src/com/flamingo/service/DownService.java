package com.flamingo.service;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;
import android.webkit.WebView;

import com.flamingo.dao.ItemDAO;
import com.flamingo.domain.Channel;
import com.flamingo.domain.ChannelParcel;
import com.flamingo.domain.Item;
import com.flamingo.domain.ProgressInfo;
import com.flamingo.engine.ChannelEngine;
import com.flamingo.engine.ItemEngine;
import com.flamingo.rssreader.DownLoadActivity;

public class DownService extends Service
{

	// 扩展一个具有标志位的线程用以停止线程
	private class MyThread extends Thread
	{
		protected boolean _run = true;

		public void stopThread(boolean run)
		{
			_run = !run;
		}

	}

	private WebView mWebView;
	private ArrayList<Channel> mChannelsNeedDown = new ArrayList<Channel>();
	private MyThread mDownLoadThread;

	// 用于和DownloadActivity交换信息
	private Intent mIntent = new Intent("com.flamingo.rssreader.download");
	private ProgressInfo mPP = new ProgressInfo();

	@Override
	public IBinder onBind(Intent arg0)
	{ // 无用
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate()
	{
		// TODO Auto-generated method stub
		super.onCreate();
		mWebView = new WebView(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO Auto-generated method stub
		int cmd = intent.getIntExtra("cmd", 0);

		if (cmd == 1) // 下载命令
		{
			ArrayList<Parcelable> ps = intent
					.getParcelableArrayListExtra("channels");
			mChannelsNeedDown.clear();
			ChannelParcel cp = null;
			for (Parcelable p : ps)
			{
				cp = (ChannelParcel) p;
				mChannelsNeedDown.add(cp.getChannel());
			}
			beginToDownLoad();

			// 更新进度
			mPP.setmDownload(1);
			mPP.toIntent(mIntent);
			sendStickyBroadcast(mIntent);
		} else if (cmd == 0) // 停止命令
		{
			if (mDownLoadThread != null && mDownLoadThread.isAlive())
			{
				mDownLoadThread.stopThread(true);
			}

			// 更新进度
			mPP.setmDownload(0);
			mPP.toIntent(mIntent);
			sendStickyBroadcast(mIntent);
			stopSelf();
		}

		return super.onStartCommand(intent, flags, startId);
	}

	private void beginToDownLoad()
	{

		mDownLoadThread = new MyThread()
		{
			@Override
			public void run()
			{
				Item mNowItem = null;
				Channel mNowChannel = null;
				int mNowItemCount = 0;
				int mNowChannelCount = 0;
				int mNowItemsCount = 0;

				// 初始化必要的工具类
				ItemEngine itemEngine = new ItemEngine(DownService.this);
				ItemDAO itemDAO = new ItemDAO(DownService.this);
				ChannelEngine channelEngine = new ChannelEngine(
						DownService.this);

				// 源遍历
				for (int j = 0; j < mChannelsNeedDown.size(); j++)
				{
					if (!_run)
						break;

					// 获取源
					Channel channel = mChannelsNeedDown.get(j);
					mNowChannel = channel;
					// 获取源计数
					mNowChannelCount = j + 1;

					// 发送更新源的进度
					mPP.setmStatus(DownLoadActivity.UPDATE_CHANNEL_TITLE);
					mPP.setmNowChannel(mNowChannel.getTitle());
					mPP.toIntent(mIntent);
					sendStickyBroadcast(mIntent);

					// 根据源获取Rss更新项目并且存进数据库
					itemEngine.getItems(mNowChannel);
					// 再从数据库中取得源
					ArrayList<Item> items = itemDAO
							.getItemsByChannel(mChannelsNeedDown.get(j));
					mNowItemsCount = items.size();
					// 遍历所有的项目进行下载
					for (int i = 0; i < items.size(); i++)
					{
						if (!_run)
							break;

						mNowItem = items.get(i);
						mNowItemCount = i;
						// 下载前先要检查这个ITEM是否已经下载了，通过检查item表的isDownload位是否为0来判断
						if (!itemDAO.checkIsDownload(mNowItem))
						{
							channelEngine.downLoadItem(mWebView, items.get(i));
							itemDAO.setItemDownloaded(mNowItem);
						}

						// 有item下载完成了
						mPP.setmStatus(DownLoadActivity.UPDATE_CHANNEL_PROGRESS);
						mPP.setmNowItemCount(mNowItemCount);
						mPP.setmNowItemsCount(mNowItemsCount);
						mPP.toIntent(mIntent);
						sendStickyBroadcast(mIntent);
					}
					// 有源下载完成了
					mPP.setmStatus(DownLoadActivity.UPDATE_CHANNEL_COMPLETED);
					mPP.setmNowChannelCount(mNowChannelCount+1);
					mPP.toIntent(mIntent);
					sendStickyBroadcast(mIntent);

				}

				if (_run)
				{
					mPP.setmStatus(DownLoadActivity.UPDATE_FINISH);
					mPP.setmDownload(0);
					mPP.toIntent(mIntent);
					sendStickyBroadcast(mIntent);
				}

				super.run();

				stopSelf();
			}
		};

		mDownLoadThread.start();
	}

	@Override
	public void onDestroy()
	{
		
		if (mDownLoadThread != null && mDownLoadThread.isAlive())
		{
			mDownLoadThread.stopThread(true);
		}

		// 更新进度
		mPP.setmDownload(0);
		mPP.toIntent(mIntent);
		sendStickyBroadcast(mIntent);

		super.onDestroy();
	}

}
