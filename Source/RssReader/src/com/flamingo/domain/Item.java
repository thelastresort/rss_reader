package com.flamingo.domain;

import java.io.Serializable;
import java.util.Date;

public class Item implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private int id;
	private String link;
	private String description;
	private String title;
	private Date pubDate;
	private String author;
	private String category;
	private int channel;
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getLink()
	{
		return link;
	}
	public void setLink(String link)
	{
		this.link = link;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public Date getPubDate()
	{
		return pubDate;
	}
	public void setPubDate(Date pubDate)
	{
		this.pubDate = pubDate;
	}
	public String getAuthor()
	{
		return author;
	}
	public void setAuthor(String author)
	{
		this.author = author;
	}
	public String getCategory()
	{
		return category;
	}
	public void setCategory(String category)
	{
		this.category = category;
	}
	public int getChannel()
	{
		return channel;
	}
	public void setChannel(int channel)
	{
		this.channel = channel;
	}
	
	
}
