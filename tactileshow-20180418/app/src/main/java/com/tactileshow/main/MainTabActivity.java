package com.tactileshow.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.tactileshow.util.BroadcastMsg;
import com.tactileshow.util.StaticValue;
import com.tactileshow.util.macro;
import com.tactileshow.view.BodyMap;
import com.tactileshow.view.DefinedPagerAdapter;
import com.tactileshow.view.DefinedViewPager;
import com.tactileshow.view.DetailInfo;
import com.tactileshow.view.GeneralInfo;
import com.tactileshow.view.Settings;
import com.tactileshow.view.VisualTabInfo;

import java.util.ArrayList;
import java.util.List;

public class MainTabActivity extends Activity
{
	private Context context;
	
	private TabHost tabHost;
	
	private DefinedViewPager viewPager;
	
	private DefinedPagerAdapter pagerAdapter;
	
	private List<View> listViews;
	
	private VisualTabInfo visual;
	
	private DetailInfo detail;
	
	private GeneralInfo general;
	
	private Settings set;
	
	private BodyMap bodymap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab);
		getScreenMetrics();
		context = this;
		
		tabHost = (TabHost) findViewById(R.id.tabhost);
		viewPager = (DefinedViewPager) findViewById(R.id.view_pager);
		listViews = new ArrayList<View>();
		visual = new VisualTabInfo(this, viewPager);
		detail = new DetailInfo(this);
		general = new GeneralInfo(this);
		set = new Settings(this);
		bodymap = new BodyMap(this, viewPager);
		
		listViews.add(bodymap.getView());
		listViews.add(general.getView());
		listViews.add(visual.getView());
		listViews.add(detail.getView());
		listViews.add(set.getView());
		
		pagerAdapter = new DefinedPagerAdapter(listViews);
		viewPager.setAdapter(pagerAdapter);
		
		tabHost.setup();
		
		tabHost.addTab(tabHost.newTabSpec(StaticValue.bodymap_info_tab_name).setIndicator(StaticValue.bodymap_info_tab_name).setContent(R.id.view1));
		tabHost.addTab(tabHost.newTabSpec(StaticValue.general_info_tab_name).setIndicator(StaticValue.general_info_tab_name).setContent(R.id.view1));
		tabHost.addTab(tabHost.newTabSpec(StaticValue.visual_info_tab_name).setIndicator(StaticValue.visual_info_tab_name).setContent(R.id.view1));
		tabHost.addTab(tabHost.newTabSpec(StaticValue.detail_info_tab_name).setIndicator(StaticValue.detail_info_tab_name).setContent(R.id.view1));
		tabHost.addTab(tabHost.newTabSpec(StaticValue.set_tab_name).setIndicator(StaticValue.set_tab_name).setContent(R.id.view1));
		
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
				
				if (StaticValue.general_info_tab_name.equals(tabId))
				{
					viewPager.setCurrentItem(1);
				}
				else if (StaticValue.visual_info_tab_name.equals(tabId))
				{
					
					viewPager.setCurrentItem(2);
				}
				else if (StaticValue.detail_info_tab_name.equals(tabId))
				{
					viewPager.setCurrentItem(3);
				}
				else if (StaticValue.bodymap_info_tab_name.equals(tabId))
				{
					
					viewPager.setCurrentItem(0);
				}
				else
				{
					viewPager.setCurrentItem(4);
				}
				
			}
		});
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(macro.BROADCAST_ADDRESS);
		registerReceiver(mGattUpdateReceiver, filter);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			set.ExDialog_Show();
			return true;
		}
		else
			return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		visual.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedState)
	{
		super.onRestoreInstanceState(savedState);
		visual.onRestoreInstanceState(savedState);
	}
	
	private void getScreenMetrics()
	{
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		StaticValue.width = dm.widthPixels;
		StaticValue.height = dm.heightPixels;
	}
	
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(mGattUpdateReceiver);
	}
	
	public void setTemp(Time t, String str)
	{
		try
		{
			double data = Double.parseDouble(str);
			general.setTemp(data);
			general.setGerm(data);
			visual.setTemp(t, data);
			detail.setTemp(data);
		}
		catch (NumberFormatException e)
		{
			Log.e("wshg", "Received an error format data!");
		}
	}
	
	public void setPress(Time t, String str)
	{
		try
		{
			double data = Double.parseDouble(str);
			general.setPress(data);
			visual.setPress(t, data);
			detail.setHum(data);
		}
		catch (NumberFormatException e)
		{
			Log.e("wshg", "Received an error format data!");
		}
	}
	
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
	{
		
		@Override
		public void onReceive(Context arg0, Intent arg1)
		{
			// TODO Auto-generated method stub
			final String action = arg1.getStringExtra("msg");
			if (action == null)
				return;
			BroadcastMsg bm = new BroadcastMsg(action);
			if (bm.getSensor() == null)
			{
				Log.e("wshg", "Receive error msg format or msg is null");
			}
			else
			{
				if (bm.getSensor().equals(StaticValue.PRESS))
				{
					setPress(bm.getTime(), bm.getData());
					StaticValue.data_file.writeData(bm.getTime(), StaticValue.PRESS, bm.getData());
				}
				else if (bm.getSensor().equals(StaticValue.TEMP))
				{
					setTemp(bm.getTime(), bm.getData());
					StaticValue.data_file.writeData(bm.getTime(), StaticValue.TEMP, bm.getData());
				}
			}
		}
	};
}
