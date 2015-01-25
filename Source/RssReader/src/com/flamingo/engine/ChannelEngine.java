package com.flamingo.engine;

import java.io.IOException;
import java.util.ArrayList;

import org.horrabin.horrorss.RssChannelBean;
import org.horrabin.horrorss.RssFeed;
import org.horrabin.horrorss.RssParser;

import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.flamingo.domain.Channel;
import com.flamingo.domain.Item;

public class ChannelEngine
{
	private Context mContext;
	private boolean isFinish;
	public ChannelEngine(Context context)
	{
		mContext = context;
	}
	//HELLO
	public Channel getChannel(String rss)
	{
		Channel channel = new Channel();
		try
		{
			RssParser parser = new RssParser();
			RssFeed feeds = parser.load(rss);
			RssChannelBean feed = feeds.getChannel();

			channel.setDescription(feed.getDescription());
			channel.setLink(feed.getLink());
			channel.setPubDate(feed.getPubDate());
			channel.setRss(rss);
			channel.setTitle(feed.getTitle());

		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			return null;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

		return channel;
	}

	public ArrayList<Channel> getChannels(String[] rsss)
	{
		ArrayList<Channel> channels = new ArrayList<Channel>();
		for (String rss : rsss)
		{
			Channel channel = getChannel(rss);
			channels.add(channel);
		}
		return channels;
	}

	public void deleteChannel(Channel channel)
	{

	}

	public void deleteChannels(ArrayList<Channel> channels)
	{

	}

	public void addChannels(ArrayList<Channel> channels)
	{

	}

	public void addChannel(Channel channel)
	{

	}

//	public boolean downLoadItem(Item item)
//	{
//		DownFile down = new DownFile(mContext, Environment
//				.getExternalStorageDirectory().getPath()
//				+ "/"
//				+ "RssDownFile"
//				+ "/" + item.getId());
////		DownFile down = new DownFile(mContext, "file:///android_asset/"+item.getId());
//		down.download(item.getLink());
//		
//		return true;
//	}
	public boolean downLoadItem(WebView webView, Item item)
	{
		isFinish = false;
		// 开启 DOM storage API 功能  
		webView.getSettings().setDomStorageEnabled(true);  
        //开启 database storage API 功能  
		webView.getSettings().setDatabaseEnabled(true);   
        String cacheDirPath = mContext.getFilesDir().getAbsolutePath() + "/webcache";  
        //设置数据库缓存路径  
        webView.getSettings().setDatabasePath(cacheDirPath);  
        //设置  Application Caches 缓存目录  
        webView.getSettings().setAppCachePath(cacheDirPath);  
        //开启 Application Caches 功能  
        webView.getSettings().setAppCacheEnabled(true);  
        //设置缓存模式为如果有local则加载local否则访问网络
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        webView.getSettings().setAppCacheMaxSize(appCacheMaxSize);
        if(item.getDescription().length()>1000)
        {
        	webView.loadDataWithBaseURL(null, item.getDescription(), "text/html", "utf-8", null);
        }
        else 
        {
        	webView.loadUrl(item.getLink());
		}
        
        webView.setWebViewClient(new WebViewClient()
        {
        	@Override
        	public void onPageFinished(WebView view, String url)
        	{
        		isFinish = true;
        		super.onPageFinished(view, url);
        	}
        });
        while(!isFinish)
        {
        	
        }
        return true;
	}
}
