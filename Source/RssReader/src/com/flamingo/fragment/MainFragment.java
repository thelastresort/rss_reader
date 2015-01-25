package com.flamingo.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.flamingo.adapter.MyFeedAdapter;
import com.flamingo.dao.ChannelDAO;
import com.flamingo.domain.Channel;
import com.flamingo.rssreader.ItemListActivity;
import com.flamingo.rssreader.MainActivity;
import com.flamingo.rssreader.R;

public class MainFragment extends Fragment
{
	private GridView mGvFeedList;
	private ChannelDAO mChannelDAO;
	ArrayList<Channel> mChannels;
	private MyFeedAdapter mMyFeedAdapter;
	private SharedPreferences mSharedPreferences;
	public static boolean isDeleting = false;
	public static ArrayList<Channel> mNeedToDelete;
	private LinearLayout mLlPopView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		// 设置Actionbar布局
		setActionBarLayout(R.layout.actionbar_main_activity);

		return inflater.inflate(R.layout.fragment_main, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		mGvFeedList = (GridView) view.findViewById(R.id.gv_main_fragment);
		mNeedToDelete = new ArrayList<Channel>();
		mMyFeedAdapter = new MyFeedAdapter(mChannels, getActivity());
		mSharedPreferences = getActivity().getSharedPreferences("rssreader",
				Context.MODE_PRIVATE);
		mLlPopView = (LinearLayout) getActivity()
				.findViewById(R.id.ll_pop_view);

		mGvFeedList
				.setOnItemClickListener(new AdapterView.OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> views, View view,
							int position, long arg3)
					{
						// 如果不是在删除状态下则进入点击进入itemlist逻辑
						if (!isDeleting)
						{
							Intent intent = new Intent(getActivity(),
									ItemListActivity.class);
							intent.putExtra("channel", mChannels.get(position));
							startActivity(intent);
							getActivity().overridePendingTransition(
									R.anim.slide_in_right,
									R.anim.slide_out_left);
						} else
						{
//							View myView = mMyFeedAdapter.getView(position,
//									view, null);
							if (mNeedToDelete.contains(mChannels.get(position)))
							{
								mNeedToDelete.remove(mChannels.get(position));
								mMyFeedAdapter.notifyDataSetChanged();
							} else
							{
								mNeedToDelete.add(mChannels.get(position));
								mMyFeedAdapter.notifyDataSetChanged();
							}
							
						}
					}
				});
		mGvFeedList
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
				{

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3)
					{
						// 如果不是在删除状态下长按
						if (!isDeleting)
						{
							// 初始化数据
							mNeedToDelete.clear();
							// 进入删除状态
							isDeleting = true;
							// 更新列表
							mGvFeedList.setAdapter(mMyFeedAdapter);
							// 显示操作栏
							showOperationWindow();
						}
						return false;
					}
				});

	}

	// 弹出popwindow
	private void showOperationWindow()
	{
//		mLlPopView.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.popup_window_out));
		mLlPopView.setVisibility(View.VISIBLE);
		((MainActivity) getActivity()).mTabHost.setVisibility(View.GONE);
		Button btnDelete = (Button) getActivity().findViewById(R.id.btn_delete);
		Button btnCancelDelete = (Button) getActivity().findViewById(
				R.id.btn_cancel_delete);
		btnDelete.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				mLlPopView.setVisibility(View.GONE);
				((MainActivity) getActivity()).mTabHost
						.setVisibility(View.VISIBLE);
				isDeleting = false;
				// 删除数据channel数据
				for (Channel channel : mNeedToDelete)
				{
					mChannelDAO.deleteChannel(channel);
					mChannels.remove(channel);
				}
				mMyFeedAdapter.setChannels(mChannels);
				mMyFeedAdapter.notifyDataSetChanged();
			}
		});
		btnCancelDelete.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				mLlPopView.setVisibility(View.GONE);
				((MainActivity) getActivity()).mTabHost
						.setVisibility(View.VISIBLE);
				isDeleting = false;
				mGvFeedList.setAdapter(mMyFeedAdapter);
			}
		});
	}

	public void setActionBarLayout(int layoutId)
	{
		ActionBar actionBar = this.getActivity().getActionBar();
		if (null != actionBar)
		{
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayShowCustomEnabled(true);
			LayoutInflater inflator = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflator.inflate(layoutId, null);
			ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			actionBar.setCustomView(v, layout);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			mGvFeedList.setAdapter(mMyFeedAdapter);
//			mMyFeedAdapter.notifyDataSetChanged();
		}
	};

	@Override
	public void onStart()
	{
		super.onStart();
		// 更新列表
		new Thread()
		{
			@Override
			public void run()
			{
				// 需要初始化一些源
				if (mSharedPreferences.getBoolean("isFirstIn", true))
				{
					ChannelDAO cDao = new ChannelDAO(getActivity());
					Channel channel1 = new Channel();
					channel1.setTitle("果壳网 guokr.com");
					channel1.setRss("http://www.guokr.com/rss/");
					channel1.setDescription("果壳网是一个泛科技主题网站，提供负责任、有智趣、贴近生活的内容，你可以在这里阅读、分享、交流、提问。果壳网致力于让科技兴趣成为人们文化生活和娱乐生活的重要元素。");
					Channel channel2 = new Channel();
					channel2.setTitle("南方周末");
					channel2.setRss("http://www.infzm.com/rss/home/rss2.0.xml");
					channel2.setDescription("《南方周末》由南方报业传媒集团主办，创刊于1984年2月11日，以“反映社会，服务改革，贴近生活，激浊扬清”为特色；以“关注民生，彰显爱心，维护正义，坚守良知”为己责；将思想性、知识性和趣味性熔于一炉，寓思想教育于谈天说地之中");
					ArrayList<Channel> channels = new ArrayList<Channel>();
					channels.add(channel1);
					channels.add(channel2);
					for (Channel channel : channels)
					{
						cDao.addChannel(channel);
					}
					mSharedPreferences.edit().putBoolean("isFirstIn", false)
							.commit();
				}
				mChannelDAO = new ChannelDAO(getActivity());
				mChannels = mChannelDAO.getAllChannels();
				mMyFeedAdapter.setChannels(mChannels);
				handler.sendEmptyMessage(0);
			}
		}.start();

	}
}
