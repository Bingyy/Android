package com.tactileshow.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

public class TimeEditor extends EditText
{
	
	private Context context;
	
	private Time t;
	
	private TimeEditor thisCla;
	
	public TimeEditor(Context ccontext, AttributeSet attrs)
	{
		super(ccontext, attrs);
		context = ccontext;
		thisCla = this;
		this.setFocusable(false);
		t = new Time();
		t.setToNow();
		setTime();
		this.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				AlertDialog.Builder ab = new AlertDialog.Builder(context);
				ab.setTitle("Time Setting");
				final TimePicker te = new TimePicker(context);
				ab.setView(te);
				ab.setPositiveButton("Confirm", new DialogInterface.OnClickListener()
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						t.hour = te.getCurrentHour();
						t.minute = te.getCurrentMinute();
						setTime();
					}
				});
				ab.create().show();
			}
			
		});
	}
	
	private void setTime()
	{
		String str = "";
		if (t.hour < 10)
			str += "0" + t.hour;
		else
			str += t.hour;
		str += " : ";
		if (t.minute < 10)
			str += "0" + t.minute;
		else
			str += t.minute;
		thisCla.setText(str);
	}
	
	public TimeEditor(Context context)
	{
		super(context);
	}
}
