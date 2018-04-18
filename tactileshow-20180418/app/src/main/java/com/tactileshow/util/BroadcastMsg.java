package com.tactileshow.util;

import android.text.format.Time;

/*
 * 对接收到的广播数据（格式为：#Sensor#Time#data#），进行解析。
 */
public class BroadcastMsg
{
	private String sensor;
	
	private Time time;
	
	private String data;
	
	public BroadcastMsg(String msg)
	{
		time = new Time();
		parseString(msg);
	}
	
	public String getSensor()
	{
		return sensor;
	}
	
	public Time getTime()
	{
		return time;
	}
	
	public String getData()
	{
		return data;
	}
	
	private void parseString(String msg)
	{
		if (msg.startsWith("#"))
		{
			String[] pars = msg.substring(1).split("#");
			if (pars.length >= 3)
			{
				sensor = pars[0];
				time.parse(pars[1]);
				data = pars[2];
			}
		}
	}
}
