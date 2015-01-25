package com.flamingo.rssreader;

import com.flamingo.service.DownService;

import android.app.Application;
import android.content.Intent;

public class MainApp extends Application {

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		Intent intent=new Intent(MainApp.this,DownService.class);
		stopService(intent);
		
		super.onTerminate();
	}

}
