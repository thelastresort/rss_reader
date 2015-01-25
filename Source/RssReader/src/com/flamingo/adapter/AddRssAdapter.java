package com.flamingo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flamingo.dao.ChannelDAO;
import com.flamingo.domain.Channel;
import com.flamingo.rssreader.AddRssActivity;
import com.flamingo.rssreader.R;

public class AddRssAdapter extends BaseAdapter
{
	private Context mContext;
	private ArrayList<Channel> mArrayList;
	private static TextView mTitle;
	private static TextView mDescription;
	private static ImageView mImageView;
	private ArrayList<Boolean> isShow;
	private int isFirst = 0;
	public AddRssAdapter(Context context, ArrayList<Channel> arrayList)
	{
		if (arrayList != null)
		{
			mArrayList = arrayList;
		} else
		{
			mArrayList = new ArrayList<Channel>();
		}
		mContext = context;
		isShow = new ArrayList<Boolean>();
		for (int i = 0; i < arrayList.size(); i++)
		{
			isShow.add(false);
		}
	}

	public void setArrayList(ArrayList<Channel> arrayList)
	{
		mArrayList = arrayList;
		isShow.clear();
		isFirst = 0;
		for (int i = 0; i < mArrayList.size(); i++)
		{
			isShow.add(false);
		}
	}

	@Override
	public int getCount()
	{
		return mArrayList.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		return mArrayList.get(arg0);
	}

	@Override
	public long getItemId(int arg0)
	{
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup)
	{
		final Channel channel = mArrayList.get(position);
		if (view == null)
		{
			view = LayoutInflater.from(mContext).inflate(R.layout.add_rss_item,
					null);
		}
		mTitle = (TextView) view.findViewById(R.id.tv_add_title);
		mDescription = (TextView) view.findViewById(R.id.tv_add_description);
		mImageView = (ImageView) view.findViewById(R.id.iv_btn_add);
		
		//设置动画
		if (!isShow.get(position))
		{
			Animation anin = AnimationUtils.loadAnimation(mContext,
					R.anim.slide_in_right);
			anin.setStartOffset(((position % 9) + 1) * 50);
			view.setAnimation(anin);
		} else
		{
			view.setAnimation(null);
		}
		if(position==0 && isFirst < 1)
		{
			Animation anin = AnimationUtils.loadAnimation(mContext,
					R.anim.slide_in_right);
			anin.setStartOffset(((position % 9) + 1) * 50);
			view.setAnimation(anin);
			isFirst++;
		}
		isShow.remove(position);
		isShow.add(position, true);
		
		
		ChannelDAO channelDAO = new ChannelDAO(mContext);
		if (channelDAO.channelIsExited(channel))
		{
			mImageView.setBackgroundResource(R.drawable.btn_add_rss);
		} else
		{
			mImageView.setBackgroundResource(R.drawable.btn_un_add);
		}
		mTitle.setText(channel.getTitle());
		mDescription.setText(channel.getDescription());
		// 当点击收藏按钮时，将此源添加到数据库中
		mImageView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{

				ChannelDAO channelDAO = new ChannelDAO(mContext);
				if (channelDAO.addChannel(channel))
				{
					AddRssActivity.mResultCode = 1;
					AddRssAdapter.this.notifyDataSetChanged();
					Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
				} else
				{
					Toast.makeText(mContext, "该源已收藏", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
		return view;
	}

}
