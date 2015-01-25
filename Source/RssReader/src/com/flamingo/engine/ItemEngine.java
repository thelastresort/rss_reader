package com.flamingo.engine;

import java.util.ArrayList;
import java.util.List;

import org.horrabin.horrorss.RssFeed;
import org.horrabin.horrorss.RssItemBean;
import org.horrabin.horrorss.RssParser;

import android.content.Context;

import com.flamingo.dao.ItemDAO;
import com.flamingo.domain.Channel;
import com.flamingo.domain.Item;

public class ItemEngine
{
	private Context mContext;

	public ItemEngine(Context context)
	{
		mContext = context;
	}
	//Hello
	public ArrayList<Item> getItems(Channel channel)
	{
		ArrayList<Item> items = new ArrayList<Item>();
		try
		{
			RssParser parser=new RssParser();
			RssFeed feeds=parser.load(channel.getRss());
			
			List<RssItemBean> entries = feeds.getItems();
			// 循环得到每个子项信息
			for (int i = 0; i < entries.size(); i++)
			{
				RssItemBean entry = (RssItemBean) entries.get(i);
				Item item = new Item();
				item.setAuthor(entry.getAuthor());
				String category = entry.getCategory();
				item.setCategory(category);
				item.setChannel(channel.getId());
				item.setDescription(entry.getDescription());
				item.setLink(entry.getLink());
				item.setPubDate(entry.getPubDate());
				item.setTitle(entry.getTitle());
				ItemDAO itemDAO = new ItemDAO(mContext);
				itemDAO.addItem(item);
				items.add(item);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return items;
	}

}
