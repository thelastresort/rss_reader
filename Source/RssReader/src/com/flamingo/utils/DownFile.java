package com.flamingo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class DownFile
{
	private final static String CRLF = System.getProperty("line.separator");
	private String downloadPath;
	private AQuery aq;
	private Integer iFinish;

	public DownFile(Context ctx, String downloadPath)
	{
		super();
		aq = new AQuery(ctx);
		setiFinish(0);

		this.downloadPath = downloadPath;
		File dir = new File(downloadPath);
		if (!dir.exists())
		{
			dir.mkdirs();
		}
	}

	private synchronized Integer getiFinish() {
		return iFinish;
	}

	private synchronized void setiFinish(Integer iFinish) {
		this.iFinish = iFinish;
	}

	public String getDownloadPath()
	{
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath)
	{
		this.downloadPath = downloadPath;
		File dir = new File(downloadPath);
		if (!dir.exists())
		{
			dir.mkdirs();
		}
	}

	public void download(String url)
	{
		String html = "";
		FileOutputStream fos = null;
		try
		{
			html = getInfoFromUrl(url, null);

			Pattern pattern = Pattern.compile("(?<=src=\")[^\"]+(?=\")");
			Matcher matcher = pattern.matcher(html);
			StringBuffer sb = new StringBuffer();
			while (matcher.find())
			{
				setiFinish(getiFinish()+1); // 添加一个异步任务
				downloadPic(downloadPath, matcher.group(0));
				matcher.appendReplacement(sb, formatPath(matcher.group(0)));
			}
			matcher.appendTail(sb);

			fos = new FileOutputStream(downloadPath + "/" + "index.html");
			fos.write(sb.toString().getBytes());
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			if (fos != null)
				try
				{
					fos.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
		}
		
		while(true)
			if(getiFinish()<=0)
				break;
	}

	/**
	 * 若编码格式未确定，则encoding=null.
	 */
	private static String getInfoFromUrl(String urlInfo, String encoding)
			throws IOException
	{
		String method = "GET";
		int timeout = 30000;
		URL url = new URL(urlInfo);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setConnectTimeout(timeout);
		conn.setDoOutput(true);
		InputStream urlStream = conn.getInputStream();
		String encoding2 = encoding;
		if (encoding2 == null)
		{
			String contentType = conn.getContentType();
			encoding2 = getEncoding(contentType);
			if (encoding2 == null)
			{
				String encodingRegex = "<meta \\s*http-equiv=\"?content-type\"? |<meta'>\\s*content=\"?(.*?)\"?\\s*/?>|<meta\\s*content=\"?(.*?)\"? |<meta'>\\s*http-equiv=\"?content-type\"?\\s*/?>|<meta\\s*charset=\"?(.*?)\"?\\s*/?>";
				encoding2 = getEncoding(getInfo(encodingRegex,
						getInfoFromUrl(urlInfo, "UTF-8"), 3));
				if (encoding2 == null)
				{
					encoding2 = "GBK";
				}
			}
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				urlStream, encoding2));
		String line = "";
		StringBuffer buffer = new StringBuffer();
		while ((line = reader.readLine()) != null)
		{
			buffer.append(line);
			buffer.append(CRLF);
		}
		if (urlStream != null)
		{
			urlStream.close();
		}
		conn.disconnect();

		return buffer.toString();
	}

	private static String getInfo(String regex, String html, int group)
	{
		if (regex == null || html == null)
		{
			return null;
		}
		Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
				.matcher(html);
		StringBuffer encodingInfo = new StringBuffer("");
		boolean found = false;
		while (matcher.find())
		{
			for (int i = 1; i <= group; i++)
			{
				String str = matcher.group(i);
				if (str != null)
				{
					encodingInfo.append(str);
				}
			}
			encodingInfo.append("   ");
			found = true;
		}
		if (found)
		{
			return encodingInfo.toString();
		} else
		{
			return null;
		}
	}

	private static String getEncoding(String encodingInfo)
	{
		String encoding = null;
		if (encodingInfo != null)
		{
			String temp = encodingInfo.toLowerCase(Locale.US);
			if (temp.contains("gb2312") || temp.contains("gbk"))
			{
				encoding = "GBK";
			} else if (temp.contains("utf-8"))
			{
				encoding = "UTF-8";
			} else if (temp.contains("iso-8859-1"))
			{
				encoding = "ISO-8859-1";
			}
		}
		return encoding;

	}

	private static String formatPath(String path)
	{
		if (path != null && path.length() > 0)
		{
			path = path.replace("\\", "_");
			path = path.replace("/", "_");
			path = path.replace(":", "_");
			path = path.replace("*", "_");
			path = path.replace("?", "_");
			path = path.replace("\"", "_");
			path = path.replace("<", "_");
			path = path.replace("|", "_");
			path = path.replace(">", "_");
		}
		return path;
	}

	private void downloadPic(String path, String httpUrl)
	{
		File pic_file = new File(path + "/" + formatPath(httpUrl));
		aq.download(httpUrl, pic_file, new AjaxCallback<File>()
		{
			@Override
			public void callback(String url, final File file, AjaxStatus status)
			{
				setiFinish(getiFinish()-1); // 完成一个异步任务
			}
		});
	}

}
