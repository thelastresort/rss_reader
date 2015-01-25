package com.flamingo.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.flamingo.domain.Channel;

import android.os.Parcel;
import android.os.Parcelable;

public class ChannelParcel implements Parcelable
{
	private Channel channel;

	public ChannelParcel(Channel channel)
	{
		super();
		this.channel = channel;
	}

	public Channel getChannel()
	{
		return channel;
	}

	public void setChannel(Channel channel)
	{
		this.channel = channel;
	}

	@Override
	public int describeContents()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag)
	{
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		dest.writeInt(channel.getId());
		dest.writeString(channel.getTitle());
		dest.writeString(channel.getDescription());
		dest.writeString(channel.getLink());
		dest.writeString(sdf.format(channel.getPubDate()));
		dest.writeString(channel.getRss());
	}

	// 添加一个静态成员,名为CREATOR,该对象实现了Parcelable.Creator接口
	public static final Parcelable.Creator<ChannelParcel> CREATOR = new Parcelable.Creator<ChannelParcel>()
	{
		@Override
		public ChannelParcel createFromParcel(Parcel src)
		{
			// 从Parcel中读取数据，返回person对象
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Channel c = new Channel();

			c.setId(src.readInt());
			c.setTitle(src.readString());
			c.setDescription(src.readString());
			c.setLink(src.readString());
			try
			{
				c.setPubDate(sdf.parse(src.readString()));
			} catch (ParseException e)
			{
				e.printStackTrace();
			}
			c.setRss(src.readString());
			return new ChannelParcel(c);
		}

		@Override
		public ChannelParcel[] newArray(int size)
		{
			return new ChannelParcel[size];
		}
	};
}
