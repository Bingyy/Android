package com.tactileshow.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class DefinedScrollView extends ScrollView
{
	
	private Context context;
	
	private boolean willIntercept = true;
	
	public DefinedScrollView(Context context)
	{
		super(context);
		this.context = context;
	}
	
	public DefinedScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0)
	{
		if (willIntercept)
		{
			//这个地方直接返回true会很卡
			return super.onInterceptTouchEvent(arg0);
		}
		else
		{
			return false;
		}
		
	}
	
	public void setTouchIntercept(boolean value)
	{
		willIntercept = value;
	}
	
}
