package com.flamingo.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.flamingo.domain.Item;
import com.flamingo.rssreader.R;

public class ItemAdapter extends BaseAdapter
{
	ArrayList<Item> mItems;
	Context mContext;
	private static TextView mTvTitle;
	private static TextView mTvDescription;
	private static TextView mTvTime;
	private ArrayList<Boolean> isShow;
	private int isFirst = 0;
	public ItemAdapter(Context context, ArrayList<Item> items)
	{
		if (items != null)
		{
			mItems = items;
		} else
		{
			mItems = new ArrayList<Item>();
		}
		mContext = context;
		isShow = new ArrayList<Boolean>();
		for (int i = 0; i < mItems.size(); i++)
		{
			isShow.add(false);
		}
	}
	@Override
	public int getCount()
	{
		return mItems.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		return mItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0)
	{
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup)
	{
		Item item = new Item();
		item = mItems.get(position);
		if(view == null)
		{
			view = (LayoutInflater.from(mContext)).inflate(R.layout.item_item, null);
			
		}
		mTvDescription = (TextView) view.findViewById(R.id.tv_item_description);
		mTvTime = (TextView) view.findViewById(R.id.tv_item_pubDate);
		mTvTitle = (TextView) view.findViewById(R.id.tv_item_title);
		if(item.getDescription()!=null)
		{
			if(item.getDescription().length()>500)
			{
				mTvDescription.setText(Html.fromHtml(item.getDescription().substring(0,400)));
			}
			else
			{
				mTvDescription.setText(Html.fromHtml(item.getDescription()));
			}
			
		}
		else 
		{
			mTvDescription.setText("");
		}
		if(item.getPubDate()!=null)
		{
			SimpleDateFormat fm = new SimpleDateFormat(
					"MMMd HH:mm", Locale.CHINA);
			String dateString = fm.format(item.getPubDate());
			mTvTime.setText(dateString);
		}
		mTvTitle.setText(Html.fromHtml(item.getTitle()));
		if (!isShow.get(position))
		{
			Animation anin = AnimationUtils.loadAnimation(mContext,
					R.anim.popup_window_out);
			anin.setStartOffset(((position % 5) + 1) * 50);
			view.setAnimation(anin);
		} else
		{
			view.setAnimation(null);
		}
		if(position==0 && isFirst < 1)
		{
			Animation anin = AnimationUtils.loadAnimation(mContext,
					R.anim.popup_window_out);
			anin.setStartOffset(((position % 5) + 1) * 50);
			view.setAnimation(anin);
			isFirst++;
		}
		isShow.remove(position);
		isShow.add(position, true);
		return view;
	}
	public void setItems(ArrayList<Item> items)
	{
		mItems = items;
		isShow.clear();
		isFirst = 0;
		for (int i = 0; i < mItems.size(); i++)
		{
			isShow.add(false);
		}
	}

}
