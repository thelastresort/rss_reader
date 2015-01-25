package com.flamingo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.flamingo.domain.Channel;
import com.flamingo.fragment.MainFragment;
import com.flamingo.rssreader.R;

public class MyFeedAdapter extends BaseAdapter
{
	private ArrayList<Channel> mChannelList = new ArrayList<Channel>();
	private Context mContext;
	private static TextView mTvTitle;
	private static TextView mTvDescription;
	private static CheckBox mCbDelete;
	private ArrayList<Boolean> isShow;
	private int isFirst = 0;
	public MyFeedAdapter(ArrayList<Channel> feeds, Context context)
	{
		if (feeds != null)
		{
			mChannelList = feeds;
		} else
		{
			mChannelList = new ArrayList<Channel>();
		}
		mContext = context;
		isShow = new ArrayList<Boolean>();
		for (int i = 0; i < mChannelList.size(); i++)
		{
			isShow.add(false);
		}
	}

	@Override
	public int getCount()
	{
		return mChannelList.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		return mChannelList.get(arg0);
	}

	@Override
	public long getItemId(int arg0)
	{
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup)
	{
		Channel channel = new Channel();
		channel = mChannelList.get(position);
		if (view == null)
		{
			view = (LayoutInflater.from(mContext)).inflate(R.layout.feed_item,
					null);
		}
		mTvDescription = (TextView) view
				.findViewById(R.id.tv_feed_item_description);
		mTvTitle = (TextView) view.findViewById(R.id.tv_feed_item_title);
		mCbDelete = (CheckBox) view.findViewById(R.id.cb_channel);
		if (!isShow.get(position))
		{
			Animation anin = AnimationUtils.loadAnimation(mContext,
					R.anim.popup_window_in);
			anin.setStartOffset(((position % 6) + 1) * 50);
			view.setAnimation(anin);
		} else
		{
			view.setAnimation(null);
		}
		if(position==0 && isFirst <= 1)
		{
			Animation anin = AnimationUtils.loadAnimation(mContext,
					R.anim.popup_window_in);
			anin.setStartOffset(((position % 6) + 1) * 50);
			view.setAnimation(anin);
			isFirst++;
		}
		isShow.remove(position);
		isShow.add(position, true);
		if (channel.getDescription() != null)
		{
			mTvDescription.setText(channel.getDescription());
		} else
		{
			mTvDescription.setText("该源没有更多信息了 T T");
		}
		if(MainFragment.mNeedToDelete.contains(mChannelList.get(position)))
		{
			((CheckBox)view.findViewById(R.id.cb_channel)).setChecked(true);
		}else {
			((CheckBox)view.findViewById(R.id.cb_channel)).setChecked(false);
		}
		mTvTitle.setText(channel.getTitle());
		// 如果进入了删除状态则显示checkbox，否则，则不显示
		if (MainFragment.isDeleting)
		{
			mCbDelete.setVisibility(View.VISIBLE);
		} else
		{
			mCbDelete.setVisibility(View.INVISIBLE);
		}

		return view;
	}

	public void setChannels(ArrayList<Channel> channels)
	{
		mChannelList = channels;
		isShow.clear();
		isFirst = 0;
		for (int i = 0; i < mChannelList.size(); i++)
		{
			isShow.add(false);
		}
	}
}
