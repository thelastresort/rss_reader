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
import com.flamingo.rssreader.DownLoadActivity;
import com.flamingo.rssreader.R;

public class DownLoadAdapter extends BaseAdapter
{
	private Context mContext;
	private ArrayList<Channel> mChannels;
	private static TextView mTitle;
	private static CheckBox mCheckBox;
	private ArrayList<Boolean> isShow;
	private int isFirst = 0;
	public void setChannles(ArrayList<Channel> channels)
	{
		mChannels = channels;
		isShow.clear();
		isFirst = 0;
		for (int i = 0; i < mChannels.size(); i++)
		{
			isShow.add(false);
		}
	}

	public DownLoadAdapter(Context context, ArrayList<Channel> channels)
	{
		if (channels != null)
		{
			mChannels = channels;
		} else
		{
			mChannels = new ArrayList<Channel>();
		}
		mContext = context;
		isShow = new ArrayList<Boolean>();
		for (int i = 0; i < mChannels.size(); i++)
		{
			isShow.add(false);
		}
	}

	@Override
	public int getCount()
	{
		return mChannels.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		return mChannels.get(arg0);
	}

	@Override
	public long getItemId(int arg0)
	{
		return arg0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup viewGroup)
	{
		if (view == null)
		{
			view = LayoutInflater.from(mContext).inflate(R.layout.down_item,
					null);
		}
		mTitle = (TextView) view.findViewById(R.id.tv_down_item_title);
		mCheckBox = (CheckBox) view.findViewById(R.id.cb_down);
		mTitle.setText(mChannels.get(position).getTitle());
		
//		if (!DownLoadActivity.isAllChecked)
//		{
//			mCheckBox.setChecked(false);
//		} else
//		{
//			mCheckBox.setChecked(true);
//		}
		if(DownLoadActivity.mChannelsNeedDownTemp.contains(mChannels.get(position)))
		{
			mCheckBox.setChecked(true);
		}
		else
		{
			mCheckBox.setChecked(false);
		}
//		mCheckBox
//				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//				{
//					@Override
//					public void onCheckedChanged(CompoundButton arg0,
//							boolean arg1)
//					{
//						if (arg1)
//						{
//							Log.i("my", "add need DownLoad");
//							DownLoadActivity.mChannelsNeedDown.add(mChannels
//									.get(position));
//							
//						} else
//						{
//							Log.i("my", "remove need DownLoad");
//							DownLoadActivity.mChannelsNeedDown.remove(mChannels
//									.get(position));
//						}
//					}
//				});
		if (!isShow.get(position))
		{
			Animation anin = AnimationUtils.loadAnimation(mContext,
					R.anim.slide_in_right);
			anin.setStartOffset(((position % 8) + 1) * 50);
			view.setAnimation(anin);
		} else
		{
			view.setAnimation(null);
		}
		if(position==0 && isFirst < 1)
		{
			Animation anin = AnimationUtils.loadAnimation(mContext,
					R.anim.slide_in_right);
			anin.setStartOffset(((position % 8) + 1) * 50);
			view.setAnimation(anin);
			isFirst++;
		}
		isShow.remove(position);
		isShow.add(position, true);
		
		return view;
	}

}
