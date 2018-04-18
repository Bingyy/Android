package com.tactileshow.main;

import android.os.Handler;
import android.os.Message;

public class MyHandler extends Handler
{
	
	private MainTabActivity tab;
	
	public MyHandler(MainTabActivity tab)
	{
		super();
		this.tab = tab;
	}
	
	public void handlerMessage(Message msg)
	{
		
	}
}
