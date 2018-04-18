package com.tactileshow.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;

import com.tactileshow.main.R;
import com.tactileshow.util.HistoryDataComputing;
import com.tactileshow.util.StaticValue;
import com.tactileshow.util.macro;

public class PressVisualInfo
{
	
	private LineChartBuilder pressMap;
	
	private Activity context;
	
	private View view;
	
	private LinearLayout layout;
	
	private DefinedScrollView scroll;
	
	private DefinedViewPager pager;
	
	private RelativeLayout history_layout;
	
	private TabHost queryHost;
	
	private HistoryDataComputing history;
	
	private String sensor;
	
	@SuppressLint("InflateParams")
	public PressVisualInfo(Activity activity, DefinedViewPager pager)
	{
		this.context = activity;
		this.pager = pager;
		
		sensor = StaticValue.PRESS;
		view = context.getLayoutInflater().inflate(R.layout.activity_visual_press_info, null);
		scroll = (DefinedScrollView) view.findViewById(R.id.scroll_press);
		layout = (LinearLayout) view.findViewById(R.id.visual_press_chart_layout);
		if (layout == null)
		{
			Log.e("wshg", "Null");
			return;
		}
		
		pressMap = new LineChartBuilder(context, layout, macro.HUMIDITY_TREND, this.pager, scroll, sensor);
		pressMap.setYRange(StaticValue.temp_min_axis, StaticValue.temp_max_axis);
		
		history_layout = (RelativeLayout) view.findViewById(R.id.visual_press_history_layout);
		
		history = new HistoryDataComputing(pressMap);
		initQueryHost();
		
		final Button btn = (Button) view.findViewById(R.id.button_press_history_area);
		btn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				pressMap.changeMode();
				if (history_layout.getVisibility() == View.INVISIBLE)
				{//当前显示的是实时信息，变成现实历史信息
					btn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.history_area_visible, 0);
					history_layout.setVisibility(View.VISIBLE);
					btn.setText(R.string.label_realtime_area_str);
				}
				else
				{//当前显示的是历史信息，变成显示实时信息
					history_layout.setVisibility(View.INVISIBLE);
					btn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.history_area_unvisible, 0);
					btn.setText(R.string.label_history_area_str);
					StaticValue.press_real_time = true;
					pressMap.changeMode();
					pressMap.setTitle(macro.HUMIDITY_TREND);
				}
			}
			
		});
		
		historyListen();
		
	}
	
	private String dateFormat(int val)
	{
		if (val >= 10)
			return "" + val;
		else
			return "0" + val;
	}
	
	private void historyListen()
	{
		
		Button query_one_hour = (Button) view.findViewById(R.id.button_press_one_hour);
		query_one_hour.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				pressMap.clearHistory();
				Time from = new Time();
				Time to = new Time();
				from.setToNow();
				to.setToNow();
				from.minute -= 60;
				from.normalize(false);
				history.getHoursHistory(from, to, sensor);
				StaticValue.press_real_time = false;
				pressMap.changeMode();
				pressMap.setTitle(macro.HUMIDITY_HISTORY_RECORDS + "(" + dateFormat(from.hour) + " : " + dateFormat(from.minute) + " - " + dateFormat(to.hour) + " : " + dateFormat(to.minute) + ")");
				
				pressMap.setRange(from.toMillis(false), to.toMillis(false));
			}
			
		});
		
		Button query_one_day = (Button) view.findViewById(R.id.button_press_one_day);
		query_one_day.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				pressMap.clearHistory();
				Time from = new Time();
				Time to = new Time();
				from.setToNow();
				to.setToNow();
				from.hour = from.minute = from.second = 0;
				from.normalize(false);
				history.getHoursHistory(from, to, sensor);
				StaticValue.press_real_time = false;
				pressMap.changeMode();
				pressMap.setTitle(macro.HUMIDITY_HISTORY_RECORDS + "(" + dateFormat(from.hour) + " : " + dateFormat(from.minute) + " - " + dateFormat(to.hour) + " : " + dateFormat(to.minute) + ")");
				
				pressMap.setRange(from.toMillis(false), to.toMillis(false));
			}
			
		});
		
		Button query_one_month = (Button) view.findViewById(R.id.button_press_one_month);
		query_one_month.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				pressMap.clearHistory();
				Time from = new Time();
				Time to = new Time();
				from.setToNow();
				to.setToNow();
				from.monthDay = 1;
				from.hour = from.minute = from.second = 0;
				from.normalize(false);
				history.getDaysHistory(from, to, sensor);
				StaticValue.press_real_time = false;
				pressMap.changeMode();
				pressMap.setTitle(macro.HUMIDITY_HISTORY_RECORDS + "(" + dateFormat(from.month + 1) + "-" + dateFormat(from.monthDay) + " - " + dateFormat(to.month + 1) + "-" + dateFormat(to.monthDay) + ")");
				pressMap.setRange(from.toMillis(false), to.toMillis(false));
			}
			
		});
		
		Button query_hour = (Button) view.findViewById(R.id.button_press_query_hour);
		query_hour.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				pressMap.clearHistory();
				TimeEditor fr = (TimeEditor) view.findViewById(R.id.edit_press_from_hour);
				TimeEditor to = (TimeEditor) view.findViewById(R.id.edit_press_to_hour);
				String from_str = fr.getText().toString(), to_str = to.getText().toString();
				Time from = new Time(), tot = new Time();
				from.setToNow();
				tot.setToNow();
				String[] pars = from_str.split(" : ");
				from.hour = Integer.parseInt(pars[0]);
				from.minute = Integer.parseInt(pars[1]);
				pars = to_str.split(" : ");
				tot.hour = Integer.parseInt(pars[0]);
				tot.minute = Integer.parseInt(pars[1]);
				history.getHoursHistory(from, tot, sensor);
				StaticValue.press_real_time = false;
				pressMap.changeMode();
				pressMap.setTitle(macro.HUMIDITY_HISTORY_RECORDS + "(" + from_str + " - " + to_str + ")");
				
				pressMap.setRange(from.toMillis(false), tot.toMillis(false));
			}
			
		});
		
		Button query_day = (Button) view.findViewById(R.id.button_press_query_day);
		query_day.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				pressMap.clearHistory();
				DateEditor fr = (DateEditor) view.findViewById(R.id.edit_press_from_day);
				DateEditor to = (DateEditor) view.findViewById(R.id.edit_press_to_day);
				String from_str = fr.getText().toString(), to_str = to.getText().toString();
				Time from = new Time(), tot = new Time();
				from.setToNow();
				tot.setToNow();
				from.hour = 0;
				tot.hour = 23;
				String[] pars = from_str.split("-");
				from.month = Integer.parseInt(pars[1]) - 1;
				from.monthDay = Integer.parseInt(pars[2]);
				pars = to_str.split("-");
				tot.month = Integer.parseInt(pars[1]) - 1;
				tot.monthDay = Integer.parseInt(pars[2]);
				history.getDaysHistory(from, tot, sensor);
				StaticValue.press_real_time = false;
				pressMap.changeMode();
				pressMap.setTitle(macro.HUMIDITY_HISTORY_RECORDS + "(" + from_str + " - " + to_str + ")");
				
				pressMap.setRange(from.toMillis(false), tot.toMillis(false));
				Log.e("wshg", "from: " + from_str + "; to: " + to_str);
			}
			
		});
	}
	
	private void initQueryHost()
	{
		queryHost = (TabHost) view.findViewById(R.id.history_press_query_host);
		queryHost.setup();
		queryHost.addTab(queryHost.newTabSpec(macro.SEARCH_BY_HOUR).setIndicator(macro.SEARCH_BY_HOUR).setContent(R.id.one_hour_query_press_layout));
		queryHost.addTab(queryHost.newTabSpec(macro.SEARCH_BY_DAY).setIndicator(macro.SEARCH_BY_DAY).setContent(R.id.one_day_query_press_layout));
		queryHost.setCurrentTab(0);
	}
	
	public View getView()
	{
		return view;
	}
	
	public void repaint()
	{
		pressMap.init();
	}
	
	public void setTemp(Time t, double data)
	{
		pressMap.addData(t, data);//Log.e("wshg", "set temp visual. data = " + data);
	}
	
	public void setMaxPoints(int maxPoints)
	{
		pressMap.setMaxPoints(maxPoints);
	}
	
	public void onSaveInstanceState(Bundle outState)
	{
		pressMap.onSaveInstanceState(outState);
		//	humMap.onSaveInstanceState(outState);
	}
	
	public void onRestoreInstanceState(Bundle savedState)
	{
		pressMap.onRestoreInstanceState(savedState);
		//	humMap.onRestoreInstanceState(savedState);
	}
	
}
