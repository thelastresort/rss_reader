package com.flamingo.fragment;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flamingo.dao.ItemDAO;
import com.flamingo.rssreader.InfoActivity;
import com.flamingo.rssreader.R;

public class SettingFragment extends Fragment
{
	private LinearLayout mLlInfo, mLlDeleteCache;
	private WebView webView;
	private Dialog mDialog;
	private static final int CLEAN_DATABASE = 22;
	private static final int CLEAN_FINISH = 23;
	private TextView mTvClean;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		// 设置ActionBar
		setActionBarLayout(R.layout.actionbar_setting_activity);
		return inflater.inflate(R.layout.fragment_setting, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		mLlDeleteCache = (LinearLayout) view.findViewById(R.id.ll_delete_cache);
		mLlInfo = (LinearLayout) view.findViewById(R.id.ll_info);
		mLlDeleteCache.setOnClickListener(listener);
		mLlInfo.setOnClickListener(listener);
	}

	private View.OnClickListener listener = new View.OnClickListener()
	{

		@Override
		public void onClick(View arg0)
		{
			switch (arg0.getId())
			{
			case R.id.ll_delete_cache:
				webView = new WebView(getActivity());
				View view = setDialogLayout(R.layout.dialog_clean_cache);
				mTvClean = (TextView) view.findViewById(R.id.tv_clean_cache);
				mDialog.show();
				new Thread()
				{
					public void run()
					{
						webView.clearCache(true);
						handler.sendEmptyMessage(CLEAN_DATABASE);
						ItemDAO itemDAO = new ItemDAO(getActivity());
						itemDAO.deleteAllItem();
						handler.sendEmptyMessage(CLEAN_FINISH);
					};

				}.start();
				break;
			case R.id.ll_info:
				Intent intent = new Intent(SettingFragment.this.getActivity(),
						InfoActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	};
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == CLEAN_DATABASE)
			{
				mTvClean.setText("正在整理数据库数据.....");
			}else if(msg.what == CLEAN_FINISH)
			{
				mDialog.dismiss();
				Toast.makeText(getActivity(), "缓存清理完毕", Toast.LENGTH_SHORT).show();
			}
		};
	};
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
	@SuppressWarnings("deprecation")
	private View setDialogLayout(int resId)
	{
		mDialog = new Dialog(getActivity(), R.style.MyDialog);
		mDialog.setContentView(resId);
		mDialog.setCancelable(false);
		Window dialogWindow = mDialog.getWindow();
		WindowManager.LayoutParams p = dialogWindow.getAttributes();
		p.width = (int) (getActivity().getWindowManager().getDefaultDisplay().getWidth() * 0.8);
		dialogWindow.setAttributes(p);
		
		return LayoutInflater.from(getActivity()).inflate(resId, null);
	}
}
