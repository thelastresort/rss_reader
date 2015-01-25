package com.flamingo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

@SuppressLint("DefaultLocale") public class DataUtil
{
	public static String utilDateToString(Date date)
	{
		String string = null;
		if (date != null)
		{
			SimpleDateFormat fm = new SimpleDateFormat(
					"EEE,d MMM yyyy HH:mm:ss Z", Locale.CHINA);
			string = fm.format(date);
		}

		return string;
	}

	public static Date stringtoUtilDate(String string)
	{
		SimpleDateFormat fm = new SimpleDateFormat("EEE,d MMM yyyy HH:mm:ss Z",
				Locale.CHINA);
		if (string != null)
		{
			Date date = null;
			try
			{
				date = fm.parse(string);
			} catch (ParseException e)
			{
				e.printStackTrace();
			}
			return date;
		} else
		{
			return null;
		}

	}
	public static int CMNET = 3;
	public static int CMWAP = 2;
	public static int WIFI = 1;
	public static int NONET = -1;

	@SuppressLint("DefaultLocale") public static int getAPNType(Context context)
	{

		int netType = -1;

		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null)
		{
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE)
		{
			Log.e("networkInfo.getExtraInfo()",
					"networkInfo.getExtraInfo() is "
							+ networkInfo.getExtraInfo());
			if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet"))
			{
				netType = CMNET;
			}
			else
			{
				netType = CMWAP;
			}
		}
		else if (nType == ConnectivityManager.TYPE_WIFI)
		{
			netType = WIFI;
		}
		return netType;
	}
}
