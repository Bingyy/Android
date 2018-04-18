package com.tactileshow.view;

import android.app.Activity;
import android.view.View;

import com.tactileshow.main.R.layout;

public class Set
{
	
	private Activity context;
	
	private View view;
	
	public Set(Activity activity)
	{
		context = activity;
		view = context.getLayoutInflater().inflate(layout.activity_set, null);
	}
	
	public View getView()
	{
		return view;
	}
}
