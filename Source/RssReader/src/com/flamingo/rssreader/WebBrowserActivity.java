package com.flamingo.rssreader;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flamingo.dao.ItemDAO;
import com.flamingo.domain.Item;
import com.flamingo.utils.RoundProgressBar;

public class WebBrowserActivity extends Activity
{
	private WebView mWebView;
	private Item mItem;
	private ProgressBar mPb;
	private static final String APP_CACAHE_DIRNAME = "/webcache";  
	private RoundProgressBar mRoundProgressBar;
	private boolean isLoading;
	private int count = 0;
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mItem = (Item) getIntent().getSerializableExtra("item");
		setActionBarLayout(R.layout.actionbar_web_browser_activity);
		setContentView(R.layout.activity_web_browser);
		isLoading = true;
		mWebView = (WebView) findViewById(R.id.web_view);
		mPb = (ProgressBar) findViewById(R.id.pb);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
//		mWebView.getSettings().setUseWideViewPort(true); 
//		mWebView.getSettings().setLoadWithOverviewMode(false); 
		//自适应屏幕大小
//		mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
//		mWebView.getSettings().setUseWideViewPort(true);
//		mWebView.getSettings().setLoadWithOverviewMode(true);
		//进度条用
		mWebView.setWebChromeClient(new WebViewChromeClient());
		 // 开启 DOM storage API 功能  
        mWebView.getSettings().setDomStorageEnabled(true);  
        //开启 database storage API 功能  
        mWebView.getSettings().setDatabaseEnabled(true);   
        String cacheDirPath = getFilesDir().getAbsolutePath() +APP_CACAHE_DIRNAME;  
        //设置数据库缓存路径  
        mWebView.getSettings().setDatabasePath(cacheDirPath);  
        //设置  Application Caches 缓存目录  
        mWebView.getSettings().setAppCachePath(cacheDirPath);  
        //开启 Application Caches 功能  
        mWebView.getSettings().setAppCacheEnabled(true);  
        //如果description足够长，则有可能是全文输出，则直接加载全文
		//判断是否已经有离线下载，如果有，则从本地文件夹删除，否则则加载链接
		ItemDAO itemDAO = new ItemDAO(this);
		boolean isDownLoad = itemDAO.checkIsDownload(mItem);
		if(mItem.getDescription().length()>1000)
		{
			mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
			mWebView.loadDataWithBaseURL(null, mItem.getDescription(), "text/html", "utf-8", null);
		}
		else if(isDownLoad)
		{ 
			
			mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
			mWebView.loadUrl(mItem.getLink());
		}
		else
		{
	        //设置缓存模式为如果有local则加载local否则访问网络
	        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
			mWebView.loadUrl(mItem.getLink());
		}
		mWebView.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				 view.loadUrl(url);  
			     return true;  
			}
		});
		mRoundProgressBar = (RoundProgressBar) findViewById(R.id.pb_round);
		new Thread()
		{
			public void run() 
			{
				while (isLoading)
				{
					
					try
					{
						Thread.sleep(20);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					if((count/100)%2==0)
					{
						mRoundProgressBar.setProgress(count%100+1);
					}
					else
					{
						mRoundProgressBar.setProgress(100 - (count%100+1));
					}
					count++;
				}
			};
		}.start();
	}

	private class WebViewChromeClient extends WebChromeClient
	{
		@Override
		public void onProgressChanged(WebView view, int newProgress)
		{
			mPb.setProgress(newProgress);
			if (newProgress == 100)
			{
				mPb.setVisibility(View.GONE);
				mRoundProgressBar.setVisibility(View.INVISIBLE);
				isLoading = false;
			}
			super.onProgressChanged(view, newProgress);
		}

	}

	public void setActionBarLayout(int layoutId)
	{
		ActionBar actionBar = getActionBar();
		if (null != actionBar)
		{
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayShowCustomEnabled(true);
			LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflator.inflate(layoutId, null);
			((TextView) v.findViewById(R.id.tv_actionabar_web_browser_title))
					.setText(mItem.getTitle());
			ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			actionBar.setCustomView(v, layout);
		}
	}

	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.ib_web_browser_back_up:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
}
