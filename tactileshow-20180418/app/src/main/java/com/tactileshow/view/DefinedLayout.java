package com.tactileshow.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class DefinedLayout extends RelativeLayout
{
	
	public DefinedLayout(Context context, AttributeSet attr)
	{
		super(context, attr);
	}
	
	public DefinedLayout(Context context)
	{
		super(context);
	}
	
	private void setVisible(int visible)
	{
		this.setVisibility(visible);
	}
	
}
