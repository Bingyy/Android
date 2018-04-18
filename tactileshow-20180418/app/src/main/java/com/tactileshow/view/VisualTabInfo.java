package com.tactileshow.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.Time;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.tactileshow.main.R;
import com.tactileshow.util.StaticValue;

import java.util.ArrayList;
import java.util.List;

public class VisualTabInfo
{
	public Activity context;
	
	private DefinedViewPager pager;
	
	private View view;
	
	private TabHost tabHost;
	
	private DefinedViewPager viewPager;
	
	private DefinedPagerAdapter pagerAdapter;
	
	private List<View> listViews;
	
	private TempVisualInfo tempVisual;
	
	private PressVisualInfo pressVisual;
	
	public VisualTabInfo(Activity activity, DefinedViewPager pager)
	{
		this.context = activity;
		this.pager = pager;
		
		this.view = context.getLayoutInflater().inflate(R.layout.visual_tab_info, null);
		this.tabHost = (TabHost) view.findViewById(R.id.visual_tab_host);
		this.viewPager = (DefinedViewPager) view.findViewById(R.id.visual_paper);
		
		listViews = new ArrayList<View>();
		tempVisual = new TempVisualInfo(context, pager);
		pressVisual = new PressVisualInfo(context, pager);
		listViews.add(tempVisual.getView());
		listViews.add(pressVisual.getView());
		
		pagerAdapter = new DefinedPagerAdapter(listViews);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setTouchIntercept(false);
		
		tabHost.setup();
		
		tabHost.addTab(tabHost.newTabSpec(StaticValue.temp_visual_info_name).setIndicator(StaticValue.temp_visual_info_name).setContent(R.id.view1));
		tabHost.addTab(tabHost.newTabSpec(StaticValue.press_visual_info_name).setIndicator(StaticValue.press_visual_info_name).setContent(R.id.view1));
		
		viewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				tabHost.setCurrentTab(position);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0)
			{
			}
		});
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener()
		{
			@Override
			public void onTabChanged(String tabId)
			{
				
				if (StaticValue.temp_visual_info_name.equals(tabId))
				{
					viewPager.setCurrentItem(0);
				}
				else if (StaticValue.press_visual_info_name.equals(tabId))
				{
					
					viewPager.setCurrentItem(1);
				}
			}
		});
		
	}
	
	public void setTemp(Time t, double data)
	{
		tempVisual.setTemp(t, data);
	}
	
	public void setPress(Time t, double data)
	{
		pressVisual.setTemp(t, data);
	}
	
	public View getView()
	{
		return this.view;
	}
	
	public void onSaveInstanceState(Bundle outState)
	{
		tempVisual.onSaveInstanceState(outState);
	}
	
	public void onRestoreInstanceState(Bundle savedState)
	{
		pressVisual.onRestoreInstanceState(savedState);
	}
}
