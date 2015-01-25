package com.flamingo.rssreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.ImageView;

import com.flamingo.fragment.MainFragment;
import com.flamingo.fragment.SettingFragment;

public class MainActivity extends FragmentActivity
{

	public FragmentTabHost mTabHost = null;;
	private View mIndicator = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.fl_tab_content);

		// 添加tab名称和图标
		mIndicator = getLayoutInflater().inflate(R.layout.tab_indicator, null);
		mTabHost.addTab(mTabHost.newTabSpec("home").setIndicator(mIndicator),
				MainFragment.class, null);
		
		mIndicator = getLayoutInflater().inflate(R.layout.tab_indicator, null);
		((ImageView) mIndicator.findViewById(R.id.tab_iv))
				.setImageResource(R.drawable.tab_setting);
		mTabHost.addTab(
				mTabHost.newTabSpec("setting").setIndicator(mIndicator),
				SettingFragment.class, null);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		mTabHost = null;
	}

	// 响应ActionBar按钮
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.ib_add_source:
		{
			Intent intent = new Intent(this, AddRssActivity.class);
			startActivityForResult(intent, 101);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		}
			break;
		case R.id.ib_download:
		{
			Intent intent = new Intent(this, DownLoadActivity.class);
			startActivityForResult(intent, 101);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		}
			break;
		default:
		{
		}
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);
		if(requestCode == 101 && resultCode == 1)
		{
			
		}
	}
	
	
}
