package com.flamingo.domain;

import android.content.Intent;

public class ProgressInfo
{
	private int mDownload;
	private int mNowChannelCount;
	private int mNowItemCount;
	private int mNowItemsCount;
	private String mNowChannel;
	private int mStatus;

	public ProgressInfo()
	{
		this.mDownload = 0;
		this.mNowChannelCount = 1;
		this.mNowItemCount = 0;
		this.mNowItemsCount = 1;
		this.mNowChannel = "";
		this.mStatus = 0;
	}

	public ProgressInfo(int mDownload, int mNowChannelCount, int mNowItemCount,
			int mNowItemsCount, String mNowChannel, int mStatus)
	{
		this.mDownload = mDownload;
		this.mNowChannelCount = mNowChannelCount;
		this.mNowItemCount = mNowItemCount;
		this.mNowItemsCount = mNowItemsCount;
		this.mNowChannel = mNowChannel;
		this.mStatus = mStatus;
	}

	public void toIntent(Intent intent)
	{
		intent.putExtra("mDownload", mDownload);
		intent.putExtra("mNowChannelCount", mNowChannelCount);
		intent.putExtra("mNowItemCount", mNowItemCount);
		intent.putExtra("mNowItemsCount", mNowItemsCount);
		intent.putExtra("mNowChannel", mNowChannel);
		intent.putExtra("mStatus", mStatus);
	}

	public void fromIntent(Intent intent)
	{
		this.mDownload = intent.getIntExtra("mDownload", 0);
		this.mNowChannelCount = intent.getIntExtra("mNowChannelCount", 0);
		this.mNowItemCount = intent.getIntExtra("mNowItemCount", 0);
		this.mNowItemsCount = intent.getIntExtra("mNowItemsCount", 1);
		this.mNowChannel = intent.getStringExtra("mNowChannel");
		this.mStatus = intent.getIntExtra("mStatus", 0);
	}

	public int getmDownload()
	{
		return mDownload;
	}

	public void setmDownload(int mDownload)
	{
		this.mDownload = mDownload;
	}

	public int getmNowChannelCount()
	{
		return mNowChannelCount;
	}

	public void setmNowChannelCount(int mNowChannelCount)
	{
		this.mNowChannelCount = mNowChannelCount;
	}

	public int getmNowItemCount()
	{
		return mNowItemCount;
	}

	public void setmNowItemCount(int mNowItemCount)
	{
		this.mNowItemCount = mNowItemCount;
	}

	public int getmNowItemsCount()
	{
		return mNowItemsCount;
	}

	public void setmNowItemsCount(int mNowItemsCount)
	{
		this.mNowItemsCount = mNowItemsCount;
	}

	public String getmNowChannel()
	{
		return mNowChannel;
	}

	public void setmNowChannel(String mNowChannel)
	{
		this.mNowChannel = mNowChannel;
	}

	public int getmStatus()
	{
		return mStatus;
	}

	public void setmStatus(int mStatus)
	{
		this.mStatus = mStatus;
	}

}
