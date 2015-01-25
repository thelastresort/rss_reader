package com.flamingo.rssreader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.flamingo.adapter.AddRssAdapter;
import com.flamingo.dao.ChannelDAO;
import com.flamingo.domain.Channel;
import com.flamingo.engine.ChannelEngine;

public class AddRssActivity extends Activity
{
	private ListView mListView;
	private ProgressBar mProgressBar;
	private ArrayList<String> mArrayList;
	private ArrayList<Channel> mChannels;
	private AddRssAdapter mAddRssAdapter;
	public static int mResultCode = 0;
	private EditText mEtRssAddress;
	private Dialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		mResultCode = 0;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_rss);
		setActionBarLayout(R.layout.actionbar_add_rss_activity);
		mArrayList = new ArrayList<String>();
		mChannels = new ArrayList<Channel>();
		mAddRssAdapter = new AddRssAdapter(this, mChannels);
		mListView = (ListView) findViewById(R.id.lv_add_rss);
		mProgressBar = (ProgressBar) findViewById(R.id.pb_add_rss);
	
		new Thread()
		{
			public void run()
			{
				mArrayList = getFromAssets("recommendtext");

				for (int i = 0; i < mArrayList.size(); i += 3)
				{
					Channel channel = new Channel();
					channel.setTitle(mArrayList.get(i));
					channel.setDescription(mArrayList.get(i + 2));
					channel.setRss(mArrayList.get(i + 1));
					mChannels.add(channel);
				}
				mAddRssAdapter.setArrayList(mChannels);
				handler.sendEmptyMessage(0);
			};
		}.start();
		findViewById(R.id.ib_add_define_ress).setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				View dialogView = setDialogLayout(R.layout.dialog_add_define_rss);
				mEtRssAddress = (EditText) dialogView
						.findViewById(R.id.et_rss_address);

				mDialog.show();
			}
		});
		findViewById(R.id.ib_actionbar_add_rss).setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				setResult(mResultCode);
				finish();
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
			}
		});
	}

	@SuppressLint("HandlerLeak") private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			mListView.setAdapter(mAddRssAdapter);
			// mAddRssAdapter.notifyDataSetChanged();
			mProgressBar.setVisibility(View.INVISIBLE);
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

			ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			actionBar.setCustomView(v, layout);
		}
	}


	// 一行一行读取文件，将其保存为String数组
	// 第一行为标题，第二行为地址，第三行为描述
	public ArrayList<String> getFromAssets(String fileName)
	{
		ArrayList<String> arrayList = new ArrayList<String>();
		try
		{
			InputStreamReader inputReader = new InputStreamReader(
					getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			while ((line = bufReader.readLine()) != null)
				arrayList.add(line);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return arrayList;
	}

	@SuppressWarnings("deprecation")
	private View setDialogLayout(int resId)
	{
		mDialog = new Dialog(this, R.style.MyDialog);
		mDialog.setContentView(resId);
		Window dialogWindow = mDialog.getWindow();
		WindowManager.LayoutParams p = dialogWindow.getAttributes();
		p.width = (int) (this.getWindowManager().getDefaultDisplay().getWidth() * 0.8);
		dialogWindow.setAttributes(p);

		View view = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE))
				.inflate(resId, null);

		view.findViewById(R.id.btn_dialog_add_rss).setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0)
					{
						addRss();
					}
				});
		view.findViewById(R.id.btn_dialog_cancel_add).setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0)
					{
						mDialog.dismiss();
					}
				});
		mDialog.setContentView(view);
		return view;
	}

	private void addRss()
	{
		if (mEtRssAddress.getText().toString().trim().equals(""))
		{
			Toast.makeText(AddRssActivity.this, "请输入地址", Toast.LENGTH_SHORT)
					.show();
		} else
		{
			Toast.makeText(AddRssActivity.this, "正在添加源.....",
					Toast.LENGTH_SHORT).show();
			new Thread()
			{
				@Override
				public void run()
				{
					ChannelEngine channelEngine = new ChannelEngine(
							AddRssActivity.this);
					ChannelDAO channelDAO = new ChannelDAO(AddRssActivity.this);
					Channel channel = channelEngine.getChannel(mEtRssAddress
							.getText().toString().trim());
					if (channel != null)
					{
						if (channelDAO.addChannel(channel))
						{
							Looper.prepare();
							Toast.makeText(AddRssActivity.this, "添加源成功",
									Toast.LENGTH_SHORT).show();
							Looper.loop();
						} else
						{
							Looper.prepare();
							Toast.makeText(AddRssActivity.this, "该源已存在",
									Toast.LENGTH_SHORT).show();
							Looper.loop();
						}
					} else
					{
						Looper.prepare();
						Toast.makeText(AddRssActivity.this, "添加源失败",
								Toast.LENGTH_SHORT).show();
						Looper.loop();
					}
				}
			}.start();
		}
	}
}
