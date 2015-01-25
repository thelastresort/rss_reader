package com.flamingo.rssreader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flamingo.adapter.ItemAdapter;
import com.flamingo.dao.ItemDAO;
import com.flamingo.domain.Channel;
import com.flamingo.domain.Item;
import com.flamingo.engine.ItemEngine;
import com.flamingo.utils.DataUtil;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class ItemListActivity extends Activity
{
	private static final int FINISH_UPDATE_ITEM = 1;
	protected static final int FINISH_INIT_ITEM = 2;
	PullToRefreshListView mListView;
	Channel mChannel;
	ArrayList<Item> mItems = new ArrayList<Item>();
	ItemAdapter mItemAdapter;
	// RefreshableView mRefreshableView;
	LinearLayout mLlLoading;

	final ItemEngine itemEngine = new ItemEngine(this);
	final ItemDAO itemDAO = new ItemDAO(this);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// 获取传递进来的CHANNEL对象
		mChannel = (Channel) getIntent().getSerializableExtra("channel");
		// 设置actionbar
		setActionBarLayout(R.layout.actionbar_item_list_activity);
		setContentView(R.layout.activity_item_list);

		// mRefreshableView = (RefreshableView)
		// findViewById(R.id.refreshable_view);
		mListView = (PullToRefreshListView) findViewById(R.id.refresh_view);
		mListView.setOnRefreshListener(new OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				int NetType = DataUtil.getAPNType(ItemListActivity.this);
				// Do work to refresh the list here.
				// new GetDataTask().execute();
				if (NetType == DataUtil.NONET)
				{
					Toast.makeText(ItemListActivity.this, "无网络访问",
							Toast.LENGTH_SHORT).show();
				} else if (NetType == DataUtil.WIFI)
				{
					Toast.makeText(ItemListActivity.this, "正在使用Wifi进行更新",
							Toast.LENGTH_SHORT).show();
				} else
				{
					Toast.makeText(ItemListActivity.this, "正在使用移动网络进行更新",
							Toast.LENGTH_SHORT).show();
				}
				new Thread()
				{
					public void run()
					{
						itemEngine.getItems(mChannel);
						mItems = itemDAO.getItemsByChannel(mChannel);
						sortItems();
						mItemAdapter.setItems(mItems);
						handler.sendEmptyMessage(FINISH_UPDATE_ITEM);
					};
				}.start();
			}
		});

		mLlLoading = (LinearLayout) findViewById(R.id.ll_loading);

		mItemAdapter = new ItemAdapter(this, mItems);
		mListView.setAdapter(mItemAdapter);
		mItems = itemDAO.getItemsByChannel(mChannel);
		// 需要对mItems进行排序
		sortItems();
		mItemAdapter.setItems(mItems);
		mItemAdapter.notifyDataSetChanged();
		// new Thread()
		// {
		// @Override
		// public void run()
		// {
		// mItems = itemDAO.getItemsByChannel(mChannel);
		// if (mItems.size() == 0)
		// {
		// mLlLoading.setVisibility(View.VISIBLE);
		// itemEngine.getItems(mChannel);
		// mItems = itemDAO.getItemsByChannel(mChannel);
		// }
		// //需要对mItems进行排序
		// sortItems();
		// mItemAdapter.setItems(mItems);
		// handler.sendEmptyMessage(FINISH_INIT_ITEM);
		// }
		//
		// }.start();
		// mRefreshableView.setOnRefreshListener(
		// new RefreshableView.PullToRefreshListener()
		// {
		// @Override
		// public void onRefresh()
		// {
		// itemEngine.getItems(mChannel);
		// mItems = itemDAO.getItemsByChannel(mChannel);
		// mItemAdapter.setItems(mItems);
		// handler.sendEmptyMessage(FINISH_UPDATE_ITEM);
		// }
		// }, 0);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3)
			{
				Intent intent = new Intent(ItemListActivity.this,
						WebBrowserActivity.class);
				intent.putExtra("item", mItems.get(position-1));
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

	}

	// 对items进行排序
	private void sortItems()
	{
		Collections.sort(mItems, new Comparator<Item>()
		{
			@Override
			public int compare(Item item1, Item item2)
			{
				int flag = 0;
				if (item1.getPubDate() != null && item2.getPubDate() != null)
				{
					flag = (item2.getPubDate()).compareTo(item1.getPubDate());
				}
				return flag;
			}
		});
	}

	@SuppressLint("HandlerLeak") private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what == FINISH_UPDATE_ITEM)
			{
				mListView.onRefreshComplete();
				mListView.setAdapter(mItemAdapter);
				mItemAdapter.notifyDataSetChanged();
			}
			// if (msg.what == FINISH_INIT_ITEM)
			// {
			// mListView.setAdapter(mItemAdapter);
			// mItemAdapter.notifyDataSetChanged();
			// mLlLoading.setVisibility(View.INVISIBLE);
			// }
		};
	};

	public void setActionBarLayout(int layoutId)
	{
		ActionBar actionBar = getActionBar();
		if (null != actionBar)
		{
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayShowCustomEnabled(true);
			LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflator.inflate(layoutId, null);
			((TextView) v.findViewById(R.id.tv_actionabar_item_list_title))
					.setText(mChannel.getTitle());
			ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			actionBar.setCustomView(v, layout);
		}
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.ib_back_up:
		{
			finish();
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
		}
			break;

		default:
			break;
		}
	}

	// private class GetDataTask extends AsyncTask<Void, Void, Void> {
	//
	// @Override
	// protected Void doInBackground(Void... params) {
	// itemEngine.getItems(mChannel);
	// mItems = itemDAO.getItemsByChannel(mChannel);
	// mItemAdapter.setItems(mItems);
	//
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Void result) {
	//
	// // Call onRefreshComplete when the list has been refreshed.
	// mListView.onRefreshComplete();
	// handler.sendEmptyMessage(FINISH_UPDATE_ITEM);
	// super.onPostExecute(result);
	//
	// }
	// }

	// @Override
	// protected void onPause()
	// {
	// super.onPause();
	// overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	// }
}