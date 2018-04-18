package com.tactileshow.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.tactileshow.util.macro;

public class DateEditor extends EditText
{
	
	private Time date;
	
	private Context context;
	
	private DateEditor thisCla;
	
	public DateEditor(Context ccontext, AttributeSet attrs)
	{
		super(ccontext, attrs);
		thisCla = this;
		date = new Time();
		date.setToNow();
		setTime();
		context = ccontext;
		this.setFocusable(false);
		this.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				AlertDialog.Builder ab = new AlertDialog.Builder(context);
				ab.setTitle(macro.DATE_SETTING); // "日期设定"
				final DatePicker te = new DatePicker(context);
				ab.setView(te);
				ab.setPositiveButton("Confirm", new DialogInterface.OnClickListener()
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						date.year = te.getYear();
						date.month = te.getMonth();
						date.monthDay = te.getDayOfMonth();
						setTime();
					}
				});
				ab.create().show();
			}
			
		});
		
	}
	
	private void setTime()
	{
		String str = date.year + "-" + (date.month + 1) + "-" + date.monthDay;
		thisCla.setText(str);
	}
	
	public DateEditor(Context context)
	{
		super(context);
	}
	
}
