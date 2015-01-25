package com.flamingo.rssreader;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class InfoActivity extends Activity
{
	@Override
	protected void onCreate(Bundle arg0)
	{
		super.onCreate(arg0);
		setContentView(R.layout.activity_info);
		setActionBarLayout(R.layout.actionbar_info_activity);
		
		View vWeibo=findViewById(R.id.ll_info_friend);
		View vEmail=findViewById(R.id.ll_info_email);
		View vHome=findViewById(R.id.ll_info_home);
		
		
		vWeibo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//Toast.makeText(ctx, "weibo", Toast.LENGTH_SHORT).show();
				
				String addr="http://weibo.com/u/1583987107";
				Intent intent=new Intent(Intent.ACTION_SEND);   
		        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(addr));    
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
				
				Context context = null;
				try{
					context=createPackageContext("com.sina.weibo", Context.CONTEXT_IGNORE_SECURITY);
					intent.setClassName(context, "com.sina.weibo.EditActivity");  
					startActivity(intent);
				}catch (Exception  e){
					Toast.makeText(getApplicationContext(), "您的手机没有安装新浪微博客户端，改用浏览器打开", Toast.LENGTH_SHORT).show();
					
					Uri uri = Uri.parse(addr);
					Intent intent2 = new Intent(Intent.ACTION_VIEW,uri);
					startActivity(intent2);
				}
				
			}
		});
		
		vEmail.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//Toast.makeText(ctx, "email", Toast.LENGTH_SHORT).show();
				
				Uri uri=Uri.parse("mailto:rosejames029@gmail.com");
		        Intent emailIntent=new Intent(Intent.ACTION_SENDTO,uri);
		        startActivity(emailIntent);
			}
		});
		
		vHome.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//Toast.makeText(ctx, "home", Toast.LENGTH_SHORT).show();
				
				String addr="http://www.baidu.com";
				Uri uri = Uri.parse(addr);
				Intent intent2 = new Intent(Intent.ACTION_VIEW,uri);
				startActivity(intent2);
			}
		});
		
		
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
			ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			actionBar.setCustomView(v, layout);
		}
	}
	
}
