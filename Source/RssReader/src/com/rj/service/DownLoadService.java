package com.rj.service;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;

import com.flamingo.dao.ItemDAO;
import com.flamingo.domain.Channel;
import com.flamingo.domain.Item;
import com.flamingo.engine.ChannelEngine;
import com.flamingo.engine.ItemEngine;

public class DownLoadService extends Service
{
	private Item mNowItem = null;
	private Channel mNowChannel = null;
	private int mNowItemCount = 0;
	private int mNowChannelCount = 0;
	private int mNowItemsCount = 0;
	private WebView mWebView;
	private static final int UPDATE_ITEM = 12;
	private static final int UPDATE_CHANNEL = 13;
	private static final int FINISH_DOWN_LOAD = 14;
	public static ArrayList<Channel> mChannelsNeedDown = new ArrayList<Channel>();
	
	 //定义浮动窗口布局  
    WindowManager.LayoutParams wmParams;  
    //创建浮动窗口设置布局参数的对象  
    WindowManager mWindowManager;  

	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}
	@Override
	public void onCreate()
	{
		super.onCreate();
		createFloatView();
	}
	@SuppressWarnings("unchecked")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		mChannelsNeedDown = (ArrayList<Channel>) (intent.getParcelableArrayListExtra("downChannels").get(0));
		mWebView = new WebView(this);
		beginToDownLoad();
		
		return super.onStartCommand(intent, flags, startId);
	}
	private MyThread downLoadThread = new MyThread();

	private void beginToDownLoad()
	{
		downLoadThread = new MyThread()
		{
			@Override
			public void run()
			{
				//初始化必要的工具类
				ItemEngine itemEngine = new ItemEngine(DownLoadService.this);
				ItemDAO itemDAO = new ItemDAO(DownLoadService.this);
				ChannelEngine channelEngine = new ChannelEngine(
						DownLoadService.this);
				Log.i("my","need download : " +mChannelsNeedDown.size());
				//对需要更新的 源进行遍历
				for (int j = 0; j < mChannelsNeedDown.size(); j++)
				{
					if (_run)
					{
						//获取源
						Channel channel = mChannelsNeedDown.get(j);
						Log.i("my", "DownLoadChannel:" + channel.getTitle());
						mNowChannel = channel;
						//获取源计数
						mNowChannelCount = j + 1;
						//根据源获取Rss更新项目并且存进数据库
						itemEngine.getItems(mNowChannel);
						//再从数据库中取得源
						ArrayList<Item> items = itemDAO
								.getItemsByChannel(mChannelsNeedDown.get(j));
						mNowItemsCount = items.size();
						handler.sendEmptyMessage(UPDATE_CHANNEL);
						Log.i("my", "ItemsCount:" + items.size());
						//遍历所有的项目进行下载
						for (int i = 0; i < items.size(); i++)
						{
							if (_run)
							{
								mNowItem = items.get(i);
								Log.i("my", "DownLoadItem:" + mNowItem.getTitle());
								mNowItemCount = i;
								handler.sendEmptyMessage(UPDATE_ITEM);
								//下载前先要检查这个ITEM是否已经下载了，通过检查item表的isDownload位是否为0来判断
								if(!itemDAO.checkIsDownload(mNowItem))
								{
									channelEngine.downLoadItem(mWebView,items.get(i));
									itemDAO.setItemDownloaded(mNowItem);
								}
							} 
						}
					} 
				}
				//下载结束
				if(_run)
				{
					handler.sendEmptyMessage(FINISH_DOWN_LOAD);
				}			
			}
		};
		downLoadThread.start();
	}

	@SuppressLint("HandlerLeak") private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
			case UPDATE_CHANNEL:
				Intent intent = new Intent();
				intent.setAction("DOWNLOAD_INFORMATION");
				intent.putExtra("MSG", "UPDATE_CHANNEL");
				intent.putExtra("mNowChannel", mNowChannel);
				intent.putExtra("mNowChannelCount", mNowChannelCount);
				intent.putExtra("mNowItemsCount", mNowItemsCount);
				intent.putExtra("mNowChannelsCount", mChannelsNeedDown.size());
				sendBroadcast(intent);
				break;
			case UPDATE_ITEM:
				Intent intent2 = new Intent();
				intent2.setAction("DOWNLOAD_INFORMATION");
				intent2.putExtra("MSG", "UPDATE_ITEM");
				intent2.putExtra("mNowItemCount", mNowItemCount);
				sendBroadcast(intent2);
			case FINISH_DOWN_LOAD:
				Intent intent3 = new Intent();
				intent3.setAction("DOWNLOAD_INFORMATION");
				intent3.putExtra("MSG", "FINISH_DOWN_LOAD");
				sendBroadcast(intent3);
				break;
			default:
				break;
			}
		};
	};
	
	//扩展一个具有标志位的线程用以停止线程
	private class MyThread extends Thread
	{
		protected boolean _run = true;

		public void stopThread(boolean run)
		{
			_run = !run;
		}

	}
	
	@SuppressWarnings("static-access")
	private void createFloatView()  
    {  
        wmParams = new WindowManager.LayoutParams();  
        //获取的是WindowManagerImpl.CompatModeWrapper  
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);  
        //设置window type  
        wmParams.type = LayoutParams.TYPE_PHONE;   
        //设置图片格式，效果为背景透明  
        wmParams.format = PixelFormat.RGBA_8888;   
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）  
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;        
        //调整悬浮窗显示的停靠位置为左侧置顶  
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;         
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity  
        wmParams.x = 0;  
        wmParams.y = 0;  
  
        //设置悬浮窗口长宽数据    
//        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;  
//        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;  
        wmParams.width = 200;  
        wmParams.height = 200;  
         /*// 设置悬浮窗口长宽数据 
        wmParams.width = 200; 
        wmParams.height = 80;*/  
     
        //添加mFloatLayout  
        mWindowManager.addView(mWebView, wmParams);  
   
          
       
       
    }  
      
	@Override
	public void onDestroy()
	{
		Log.i("my","Service Stop");
		downLoadThread.stopThread(true);
		super.onDestroy();
	}
}
