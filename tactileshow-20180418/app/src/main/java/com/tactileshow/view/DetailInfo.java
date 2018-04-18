package com.tactileshow.view;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.tactileshow.main.R;

import java.text.DecimalFormat;

public class DetailInfo
{
	private Activity context;
	
	private View view;
	
	private TextView temp, hum;
	
	public DetailInfo(Activity activity)
	{
		context = activity;
		view = context.getLayoutInflater().inflate(R.layout.activity_detail_info, null);
		
		temp = (TextView) view.findViewById(R.id.label_detail_temp);
		hum = (TextView) view.findViewById(R.id.label_detail_hum);
	}
	
	public void setTemp(double b)
	{
		DecimalFormat df = new DecimalFormat("0.00");
		String str = df.format(b); //Double.toString(b);

		temp.setText(str);
	}
	
	public void setHum(double b)
	{
		DecimalFormat df = new DecimalFormat("0.00");
		String str = df.format(b); //Double.toString(b);
		hum.setText(str);
	}
	
	public View getView()
	{
		return view;
	}
}
