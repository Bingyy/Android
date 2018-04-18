package com.tactileshow.util;

import android.text.format.Time;
import android.util.Log;

import com.tactileshow.view.LineChartBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Record
{
	public Time t;
	
	public double data;
	
	public Record(String line)
	{
		String[] pars = line.split(" ");
		t = new Time();
		t.parse(pars[0]);
		data = Double.parseDouble(pars[1]);
	}
	
	public Record()
	{
		t = new Time();
	}
}

public class HistoryDataComputing
{
	
	private LineChartBuilder view;
	
	public HistoryDataComputing(LineChartBuilder view)
	{
		this.view = view;
	}
	
	private void addData(List<Record> ls)
	{//根据数据量不同采取不同大小的分组，保证总共的数据点不大于max_points；
		int size = ls.size();
		if (size > StaticValue.max_points)
		{
			long team = size / StaticValue.max_points;
			Log.e("wshg", "size = " + size + "; team = " + team);
			double all_data = 0;
			for (int i = 0; i < size; )
			{
				all_data = 0;
				int j = i;
				for (; j - i < team && j < size; j++)
				{
					all_data += ls.get(j).data;
				}
				view.addHistoryData(ls.get(i).t, all_data / (j - i));
				i = j;
			}
		}
		else
		{
			for (int i = 0; i < size; i++)
			{
				view.addHistoryData(ls.get(i).t, ls.get(i).data);
			}
		}
	}
	
	public boolean getHoursHistory(Time from, Time to, String sensor)
	{
		if (Time.compare(from, StaticValue.record_time) > 0 || Time.compare(from, to) > 0)
			return false;
		Time t = new Time();
		t.setToNow();
		t.hour = from.hour;
		t.monthDay = from.monthDay;
		t.month = from.month;
		
		List<Record> ls = new ArrayList<Record>();
		try
		{
			for (; Time.compare(t, to) <= 0; t.hour++)
			{
				t.normalize(false);
				BufferedReader in = StaticValue.data_file.getTimeBufferedReader(t, sensor, "data");
				if (in != null)
				{
					String line = in.readLine();
					while (line != null)
					{
						Record one = new Record(line);
						if (Time.compare(one.t, from) < 0)
						{
							line = in.readLine();
							continue;
						}
						if (Time.compare(one.t, to) > 0)
							break;
						ls.add(one);
						line = in.readLine();
					}
					in.close();
				}
			}
			addData(ls);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean getDaysHistory(Time from, Time to, String sensor)
	{
		if (Time.compare(from, StaticValue.record_time) > 0 || Time.compare(from, to) > 0)
			return false;
		
		Time t = new Time();
		t.setToNow();
		t.hour = from.hour;
		t.monthDay = from.monthDay;
		t.month = from.month;
		
		List<Record> ls = new ArrayList<Record>();
		try
		{
			for (; Time.compare(t, to) <= 0; t.hour++)
			{
				t.normalize(false);
				BufferedReader in = StaticValue.data_file.getTimeBufferedReader(t, sensor, "avg");
				if (in != null)
				{
					String line = in.readLine();
					if (line != null)
					{
						Record one = new Record();
						one.t.set(t);
						one.data = Double.parseDouble(line);
						ls.add(one);
						Log.e("wshg", "add: " + t.format2445());
					}
					in.close();
				}
				
			}
			addData(ls);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return true;
		
	}
	
	/*public boolean getDayHistory(Time from, Time to, String sensor){
		if( Time.compare(from, StaticValue.record_time) > 0 || Time.compare(from, to) > 0)
			return false;
		return true;
	}
	*/
}
