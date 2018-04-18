package com.tactileshow.util;

import android.text.format.Time;

import java.util.Date;

public class StaticValue
{
	
	public static String general_info_tab_name = "General\nInfo";//"一般\n信息";
	
	public static String visual_info_tab_name = "Figure\nInfo";//"图像\n信息";
	
	public static String detail_info_tab_name = "Original\nInfo";//"原始\n信息";
	
	public static String bodymap_info_tab_name = "Body\nMap";//"人体\n地图";  //by yzy
	
	public static String set_tab_name = "Settings";//"设置";
	
	public static int width, height;
	
	public static String PRESS = "PRESS"; // 压力传感器
	
	public static String TEMP = "TEMP"; // 温度传感器
	
	public static boolean temp_real_time = true;
	
	public static boolean press_real_time = true;
	
	public static Time record_time;
	
	public static DataFile data_file;
	
	public static double temp_max_axis = 60;
	
	public static double temp_min_axis = 10;
	
	public static double press_max_axis = 60;
	
	public static double press_min_axis = 10;

	public static int max_points = 100;
	
	public static String temp_visual_info_name = "Temperature Info";//"温度信息";
	
	public static String press_visual_info_name = "Humidity Info";//"湿度信息";
	
	//	public static String time_formart = "yyy-mm-dd HH:MM:ss";
	
	public static Date TimetoDate(Time t)
	{
		Date d = new Date(t.toMillis(false));
		return d;
	}
	
	public Time TimeAddOneHour(Time t)
	{
		t.hour += 1;
		t.normalize(false);
		return t;
	}
	
}
