package com.flamingo.rssreader;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flamingo.adapter.DownLoadAdapter;
import com.flamingo.dao.ChannelDAO;
import com.flamingo.domain.Channel;
import com.flamingo.domain.ChannelParcel;
import com.flamingo.domain.ProgressInfo;
import com.flamingo.service.DownService;
import com.flamingo.utils.DataUtil;

public class DownLoadActivity extends Activity
{
	public static final int UPDATE_CHANNEL_COMPLETED = 10;
	public static final int UPDATE_CHANNEL_PROGRESS = 11;
	public static final int UPDATE_CHANNEL_TITLE = 12;
	public static final int UPDATE_FINISH = 13;

	private ListView mListView;
	private ArrayList<Channel> mChannels;
	private DownLoadAdapter mDownLoadAdapter;
	public static boolean isAllChecked = false;
	private Button mBtnDownload, mBtnCheck;
	private TextView mTvChannelTitle, mTvChannelProgress, mTvChannelCompleted;
	public static ArrayList<Channel> mChannelsNeedDown = new ArrayList<Channel>();
	public static ArrayList<Channel> mChannelsNeedDownTemp = new ArrayList<Channel>();;
	private ProgressInfo mPP = new ProgressInfo();

	private ProgressBar mPbDownLoad;
	private MsgReceiver msgReceiver;

	public class MsgReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent == null)
				return;
			mPP.fromIntent(intent);
			handler.sendEmptyMessage(mPP.getmStatus());
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setActionBarLayout(R.layout.actionbar_web_browser_activity);
		setContentView(R.layout.activity_download);
		
		mListView = (ListView) findViewById(R.id.lv_download);
		mBtnDownload = (Button) findViewById(R.id.btn_download);
		mBtnCheck = (Button) findViewById(R.id.btn_check_book);
		mTvChannelCompleted = (TextView) findViewById(R.id.tv_down_channel_commplete);
		mTvChannelProgress = (TextView) findViewById(R.id.tv_down_channel);
		mTvChannelTitle = (TextView) findViewById(R.id.tv_down_channel_title);
		mPbDownLoad = (ProgressBar) findViewById(R.id.pb_download);
		isAllChecked = false;
		// 加载源
		new Thread()
		{
			public void run()
			{
				ChannelDAO channelDAO = new ChannelDAO(DownLoadActivity.this);
				mChannels = channelDAO.getAllChannels();
				mDownLoadAdapter = new DownLoadAdapter(DownLoadActivity.this,
						mChannels);
				handler.sendEmptyMessage(0);
			};
		}.start();

		if (isAllChecked)
		{
			mBtnCheck.setText("取消全选");
		}

		// 哪些源需要下载
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				CheckBox cbBox = (CheckBox) arg1.findViewById(R.id.cb_down);
				if (cbBox.isChecked())
				{
					cbBox.setChecked(false);
					mChannelsNeedDownTemp.remove(mChannels.get(arg2));
				} else
				{
					cbBox.setChecked(true);
					mChannelsNeedDownTemp.add(mChannels.get(arg2));
				}
			}
		});

		// 动态注册广播接收器
		msgReceiver = new MsgReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.flamingo.rssreader.download");
		registerReceiver(msgReceiver, intentFilter);

		// 从service接收进度
		if (!getIsDownload())
		{
			mBtnDownload.setText("开始下载");
			mTvChannelCompleted.setText("");
			mTvChannelProgress.setText("");
			mPbDownLoad.setProgress(0);
			mBtnCheck.setClickable(true);
		} else
		{
			mBtnDownload.setText("取消下载");
			mBtnCheck.setClickable(true);

			IntentFilter ifilter = new IntentFilter(
					"com.flamingo.rssreader.download");
			Intent status = this.registerReceiver(null, ifilter);
			mPP.fromIntent(status);
			mTvChannelCompleted.setText(mPP.getmNowChannelCount() + "/"
					+ mChannelsNeedDown.size());
			mTvChannelProgress
					.setText((int) ((mPP.getmNowItemCount() * 1.0 + 1)
							/ mPP.getmNowItemsCount() * 100)
							+ "%");
			mPbDownLoad.setProgress((int) ((mPP.getmNowItemCount() * 1.0 + 1)
					/ mPP.getmNowItemsCount() * 100));
			mTvChannelTitle.setText("正在下载：" + mPP.getmNowChannel());
		}
	}

	public void setActionBarLayout(int layoutId)
	{
		ActionBar actionBar = getActionBar();
		if (null != actionBar)
		{
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayShowCustomEnabled(true);
			LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflator.inflate(layoutId, null);
			((TextView) v.findViewById(R.id.tv_actionabar_web_browser_title))
					.setText("离线下载");
			ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			actionBar.setCustomView(v, layout);
		}
	}

	// 发命令
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.ib_web_browser_back_up: // 直接返回
			finish();
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			break;
		case R.id.btn_check_book:
			if (isAllChecked)
			{
				mListView.setAdapter(mDownLoadAdapter);
				mDownLoadAdapter.notifyDataSetChanged();
				isAllChecked = false;
				mChannelsNeedDownTemp.clear();
				mBtnCheck.setText("全选");
			} else
			{
				mListView.setAdapter(mDownLoadAdapter);
				mDownLoadAdapter.notifyDataSetChanged();
				isAllChecked = true;
				for (int i = 0; i < mChannels.size(); i++)
				{
					if (!mChannelsNeedDownTemp.contains(mChannels.get(i)))
					{
						mChannelsNeedDownTemp.add(mChannels.get(i));
					}
				}
				mBtnCheck.setText("取消全选");
			}
			break;
		case R.id.btn_download: // 核心
			if (getIsDownload())
			{
				Intent in = new Intent(DownLoadActivity.this, DownService.class);
				in.putExtra("cmd", 0);
				startService(in);

				mBtnDownload.setText("开始下载");
				mTvChannelCompleted.setText("");
				mTvChannelProgress.setText("");
				mTvChannelTitle.setText("下载取消");
				mPbDownLoad.setProgress(0);
				mBtnCheck.setClickable(true);

			} else
			{
				if(DataUtil.getAPNType(this) == DataUtil.WIFI)
				{
					mBtnDownload.setText("取消下载");

					mChannelsNeedDown.clear();
					ArrayList<ChannelParcel> ps = new ArrayList<ChannelParcel>();
					mChannelsNeedDown.clear();
					for (Channel channel : mChannelsNeedDownTemp)
					{
						mChannelsNeedDown.add(channel);
						ps.add(new ChannelParcel(channel));
					}
					mChannelsNeedDownTemp.clear();
					Intent in = new Intent(DownLoadActivity.this, DownService.class);
					in.putExtra("cmd", 1);
					in.putParcelableArrayListExtra("channels", ps);
					startService(in);
					mBtnCheck.setClickable(true);
					mBtnCheck.setText("全选");
					isAllChecked =false;
				}
				else
				{
					Toast.makeText(this, "只能在Wifi环境下进行下载", Toast.LENGTH_SHORT).show();
				}
				
			}
			break;
		}
	}

	@SuppressLint("HandlerLeak") private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			//下载完成
			if(mPP.getmNowChannelCount()==mChannelsNeedDown.size()+1)
			{
				mTvChannelCompleted.setText("");
				mTvChannelProgress.setText("");
				mTvChannelTitle.setText("下载完成");
				mPbDownLoad.setProgress(0);
				mBtnDownload.setText("开始下载");
			}
			switch (msg.what)
			{
			case UPDATE_CHANNEL_COMPLETED:
				if (!getIsDownload())
					return;
				mTvChannelCompleted.setText(mPP.getmNowChannelCount() + "/"
						+ mChannelsNeedDown.size());
				break;
			case UPDATE_CHANNEL_PROGRESS:
				
//				if(mPP.getmNowChannelCount()==mChannelsNeedDown.size()+1)
//				{
//					mTvChannelCompleted.setText("");
//					mTvChannelProgress.setText("");
//					mTvChannelTitle.setText("下载完成");
//					mPbDownLoad.setProgress(0);
//					mBtnDownload.setText("开始下载");
//				}
				if (!getIsDownload())
					return;
				mTvChannelProgress
						.setText((int) ((mPP.getmNowItemCount() * 1.0 + 1)
								/ mPP.getmNowItemsCount() * 100)
								+ "%");
				mPbDownLoad
						.setProgress((int) ((mPP.getmNowItemCount() * 1.0 + 1)
								/ mPP.getmNowItemsCount() * 100));
				mTvChannelCompleted.setText(mPP.getmNowChannelCount() + "/"
						+ mChannelsNeedDown.size());
				
				break;
			case UPDATE_CHANNEL_TITLE:
				if (!getIsDownload())
					return;
				mTvChannelCompleted.setText("");
				mPbDownLoad.setProgress(0);
				mTvChannelProgress.setText("");
				mTvChannelTitle.setText("正在下载：" + mPP.getmNowChannel());
				break;
			case UPDATE_FINISH:
				if (!getIsDownload())
					return;
				mTvChannelCompleted.setText("");
				mTvChannelProgress.setText("");
				mTvChannelTitle.setText("下载完成");
				mPbDownLoad.setProgress(0);
				mBtnDownload.setText("开始下载");
				break;
			case 0:
				mListView.setAdapter(mDownLoadAdapter);
				break;
			}
		};
	};

	private boolean getIsDownload()
	{
		IntentFilter ifilter = new IntentFilter(
				"com.flamingo.rssreader.download");
		Intent status = this.registerReceiver(null, ifilter);
		if (status == null)
			return false;
		mPP.fromIntent(status);
		return mPP.getmDownload() == 0 ? false : true;
	}

	@Override
	protected void onDestroy()
	{
		// 注销广播
		unregisterReceiver(msgReceiver);

		super.onDestroy();
	}
}
