package com.tactileshow.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabWidget;

public class DefinedTabWidget extends TabWidget
{
	Resources res;
	
	
	public DefinedTabWidget(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		res = context.getResources();
		setOrientation(LinearLayout.VERTICAL);
	}
	
	@Override
	public void addView(View child)
	{
		LayoutParams lp = new LayoutParams(
				LayoutParams.MATCH_PARENT, 120, 1.0f);
		
		lp.setMargins(0, 0, 0, 0);
		child.setLayoutParams(lp);
		
		super.addView(child);
	}
}
