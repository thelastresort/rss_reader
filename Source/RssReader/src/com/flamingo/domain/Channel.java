package com.flamingo.domain;

import java.io.Serializable;
import java.util.Date;



public class Channel implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String title;
	private String description = "该源没有更多描述信息";
	private String link;
	private Date pubDate = new Date(System.currentTimeMillis());
	private String rss;
	public String getRss()
	{
		return rss;
	}
	public void setRss(String rss)
	{
		this.rss = rss;
	}
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public String getLink()
	{
		return link;
	}
	public void setLink(String link)
	{
		this.link = link;
	}
	public Date getPubDate()
	{
		return pubDate;
	}
	public void setPubDate(Date pubDate)
	{
		this.pubDate = pubDate;
	}
	
	
}
