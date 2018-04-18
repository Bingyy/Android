package com.tactileshow.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.tactileshow.main.R;
import com.tactileshow.util.macro;

import java.util.List;
import java.util.Map;

public class Settings
{
	
	private Activity context;
	
	private View view;
	
	private List<Map<String, Object>> mData;
	
	private ListView lv_set;
	
	private CheckBox cb_sound = null;
	
	private CheckBox cb_vibration = null;
	
	private CheckBox cb_bcast_on = null;
	
	private Button bt_bcset_ok = null;
	
	private Button bt_thset_ok = null;
	
	private Dialog di_setbc = null;
	
	private CheckBox cb_di_isRange_temp;
	
	private EditText et_di_min_temp;
	
	private EditText et_di_max_temp;
	
	private EditText et_di_per_temp;
	
	private CheckBox cb_di_enable_temp;
	
	private CheckBox cb_di_isRange_press;
	
	private EditText et_di_min_press;
	
	private EditText et_di_max_press;
	
	private EditText et_di_per_press;
	
	private CheckBox cb_di_enable_press;
	
	private Dialog di_setth = null;
	
	private EditText et_di_th_min_temp;
	
	private EditText et_di_th_mid_temp;
	
	private EditText et_di_th_max_temp;
	
	private EditText et_di_th_min_press;
	
	private EditText et_di_th_mid_press;
	
	private EditText et_di_th_max_press;
	
	private EditText et_di_th_min_germ;
	
	private EditText et_di_th_max_germ;
	
	
	//for BroadCast
	boolean isRange_temp = false;
	
	double value_temp = 20;
	
	double min_temp = 20;
	
	double max_temp = 50;
	
	int period_temp = 1;
	
	boolean enable_temp = false;
	
	boolean isRange_press = false;
	
	double value_press = 0;
	
	double min_press = 0;
	
	double max_press = 100;
	
	int period_press = 1;
	
	boolean enable_press = false;
	
	//for Threshold
	double th_min_temp = macro.SETTING_TEMP_RANGE[0];
	
	double th_mid_temp = macro.SETTING_TEMP_RANGE[1];
	
	double th_max_temp = macro.SETTING_TEMP_RANGE[2];
	
	double th_min_press = macro.SETTING_PRESS_RANGE[0];
	
	double th_mid_press = macro.SETTING_PRESS_RANGE[1];
	
	double th_max_press = macro.SETTING_PRESS_RANGE[2];
	
	double th_min_germ = macro.SETTING_GERM_RANGE[0];
	
	double th_max_germ = macro.SETTING_GERM_RANGE[1];
	
	SendTempBroadCast sendTemp;
	
	SendPressBroadCast sendPress;
	
	AlertDialog.Builder builder_dl_exit;
	
	AlertDialog dl_exit;
	
	
	public Settings(Activity activity)
	{
		context = activity;
		view = context.getLayoutInflater().inflate(R.layout.activity_set, null);
		
		MyAdapter adapter = new MyAdapter(context);
		lv_set = (ListView) view.findViewById(R.id.settings_listview);
		if (lv_set == null)
			Log.e("TAG", "NULL");
		lv_set.setAdapter(adapter);
		lv_set.setClickable(true);
		lv_set.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			                        long arg3)
			{
				// TODO Auto-generated method stub
				if (arg2 == 3) //set broadcast
				{
					BCDialog_SetValue();
					di_setbc.show();
				}
				else if (arg2 == 4)
				{
					THDialog_SetValue();
					di_setth.show();
				}
				else if (arg2 == 5) //exit
				{
					ExDialog_Show();
				}
			}
		});
		
		BCDialog_Init();
		THDialog_Init();
		ExDialog_Init();
		
		
	}
	
	public View getView()
	{
		return view;
	}
	
	private void ExDialog_Init()
	{
		builder_dl_exit = new AlertDialog.Builder(context);
		builder_dl_exit.setTitle("Exit");
		
		builder_dl_exit.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						
						dialog.dismiss();
					}
				});
		builder_dl_exit.setPositiveButton("Disconnect",
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1)
					{
						// TODO Auto-generated method stub
						context.finish();
					}
				});
		
		builder_dl_exit.setNeutralButton("Exit directly",
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1)
					{
						// TODO Auto-generated method stub
						macro.SETTING_EXIT_DIRECTLY = true;
						context.finish();
					}
				});
		
	}
	
	public void ExDialog_Show()
	{
		dl_exit = builder_dl_exit.show();
	}
	
	private void THDialog_Init()
	{
		di_setth = new Dialog(context);
		di_setth.setContentView(R.layout.activity_tab_settings_dialog_thresset);
		di_setth.setTitle(macro.SETTING_THRESHOLD_INFO); // "设定阈值信息"
		bt_thset_ok = (Button) di_setth.findViewById(R.id.setting_th_bt_ok);
		bt_thset_ok.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				THDialog_GetValue();
				di_setth.dismiss();
			}
		});
		
		et_di_th_min_temp = (EditText) di_setth.findViewById(R.id.setting_th_et_temp_min);
		et_di_th_mid_temp = (EditText) di_setth.findViewById(R.id.setting_th_et_temp_mid);
		et_di_th_max_temp = (EditText) di_setth.findViewById(R.id.setting_th_et_temp_max);
		
		et_di_th_min_press = (EditText) di_setth.findViewById(R.id.setting_th_et_press_min);
		et_di_th_mid_press = (EditText) di_setth.findViewById(R.id.setting_th_et_press_mid);
		et_di_th_max_press = (EditText) di_setth.findViewById(R.id.setting_th_et_press_max);
		
		et_di_th_min_germ = (EditText) di_setth.findViewById(R.id.setting_th_et_germ_min);
		et_di_th_max_germ = (EditText) di_setth.findViewById(R.id.setting_th_et_germ_max);
	}
	
	private void THDialog_SetValue()
	{
		et_di_th_min_temp.setText("" + th_min_temp);
		et_di_th_mid_temp.setText("" + th_mid_temp);
		et_di_th_max_temp.setText("" + th_max_temp);
		
		et_di_th_min_press.setText("" + th_min_press);
		et_di_th_mid_press.setText("" + th_mid_press);
		et_di_th_max_press.setText("" + th_max_press);
		
		et_di_th_min_germ.setText("" + th_min_germ);
		et_di_th_max_germ.setText("" + th_max_germ);
		
	}
	
	private void THDialog_GetValue()
	{
		macro.SETTING_TEMP_RANGE[0] = th_min_temp = Double.parseDouble(et_di_th_min_temp.getText().toString());
		macro.SETTING_TEMP_RANGE[1] = th_mid_temp = Double.parseDouble(et_di_th_mid_temp.getText().toString());
		macro.SETTING_TEMP_RANGE[2] = th_max_temp = Double.parseDouble(et_di_th_max_temp.getText().toString());
		
		macro.SETTING_PRESS_RANGE[0] = th_min_press = Double.parseDouble(et_di_th_min_press.getText().toString());
		macro.SETTING_PRESS_RANGE[1] = th_mid_press = Double.parseDouble(et_di_th_mid_press.getText().toString());
		macro.SETTING_PRESS_RANGE[2] = th_max_press = Double.parseDouble(et_di_th_max_press.getText().toString());
		
		macro.SETTING_GERM_RANGE[0] = th_min_germ = Double.parseDouble(et_di_th_min_germ.getText().toString());
		macro.SETTING_GERM_RANGE[1] = th_max_germ = Double.parseDouble(et_di_th_max_germ.getText().toString());
	}
	
	
	private void BCDialog_Init()
	{
		di_setbc = new Dialog(context);
		di_setbc.setContentView(R.layout.activity_tab_settings_dialog_bcset);
		di_setbc.setTitle(macro.SETTING_BROADCAST_INFO); // "设定广播信息"
		bt_bcset_ok = (Button) di_setbc.findViewById(R.id.setting_bc_bt_ok);
		bt_bcset_ok.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				BCDialog_GetValue();
				di_setbc.dismiss();
			}
		});
		cb_di_isRange_temp = (CheckBox) di_setbc.findViewById(R.id.setting_bc_cb_temprange);
		et_di_min_temp = (EditText) di_setbc.findViewById(R.id.setting_bc_et_tempmin);
		et_di_max_temp = (EditText) di_setbc.findViewById(R.id.setting_bc_et_tempmax);
		et_di_per_temp = (EditText) di_setbc.findViewById(R.id.setting_bc_et_tempper);
		cb_di_enable_temp = (CheckBox) di_setbc.findViewById(R.id.setting_bc_cb_tempenable);
		
		cb_di_isRange_press = (CheckBox) di_setbc.findViewById(R.id.setting_bc_cb_pressrange);
		et_di_min_press = (EditText) di_setbc.findViewById(R.id.setting_bc_et_pressmin);
		et_di_max_press = (EditText) di_setbc.findViewById(R.id.setting_bc_et_pressmax);
		et_di_per_press = (EditText) di_setbc.findViewById(R.id.setting_bc_et_pressper);
		cb_di_enable_press = (CheckBox) di_setbc.findViewById(R.id.setting_bc_cb_pressenable);
	}
	
	private void BCDialog_GetValue()
	{
		isRange_temp = cb_di_isRange_temp.isChecked();
		value_temp = min_temp = Double.parseDouble(et_di_min_temp.getText().toString());
		max_temp = Double.parseDouble(et_di_max_temp.getText().toString());
		period_temp = Integer.parseInt(et_di_per_temp.getText().toString());
		enable_temp = cb_di_enable_temp.isChecked();
		
		isRange_press = cb_di_isRange_press.isChecked();
		value_press = min_press = Double.parseDouble(et_di_min_press.getText().toString());
		max_press = Double.parseDouble(et_di_max_press.getText().toString());
		period_press = Integer.parseInt(et_di_per_press.getText().toString());
		enable_press = cb_di_enable_press.isChecked();
	}
	
	private void BCDialog_SetValue()
	{
		cb_di_isRange_temp.setChecked(isRange_temp);
		et_di_min_temp.setText("" + min_temp);
		et_di_max_temp.setText("" + max_temp);
		et_di_per_temp.setText("" + period_temp);
		cb_di_enable_temp.setChecked(enable_temp);
		
		cb_di_isRange_press.setChecked(isRange_press);
		et_di_min_press.setText("" + min_press);
		et_di_max_press.setText("" + max_press);
		et_di_per_press.setText("" + period_press);
		cb_di_enable_press.setChecked(enable_press);
	}
	
	
	private void broadcastUpdate(String str_intent)
	{
		Log.w("TAG", "发送广播中");
		Intent intent = new Intent(macro.BROADCAST_ADDRESS);
		intent.putExtra("msg", str_intent);
		context.sendBroadcast(intent);
	}
	
	
	public class SendTempBroadCast extends Thread
	{
		
		public void run()
		{
			double temp_value = 20;
			
			while (enable_temp == true && macro.SETTINGS_BCAST == true)
			{
				
				Time t = new Time();
				t.setToNow();
				String str_time = t.format2445();
				if (isRange_temp == true)
					temp_value = (double) (min_temp + Math.random() * (max_temp - min_temp + 1));
				else
					temp_value = min_temp;
				
				broadcastUpdate("#" + "TEMP" + "#" + str_time + "#" + temp_value);
				
				try
				{
					Thread.sleep(period_temp * 1000);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	
	public class SendPressBroadCast extends Thread
	{
		
		public void run()
		{
			double press_value = 20;
			
			while (enable_press == true && macro.SETTINGS_BCAST == true)
			{
				Time t = new Time();
				t.setToNow();
				String str_time = t.format2445();
				if (isRange_press == true)
					press_value = (double) (min_press + Math.random() * (max_press - min_press + 1));
				else
					press_value = min_press;
				
				broadcastUpdate("#" + "PRESS" + "#" + str_time + "#" + press_value);
				
				try
				{
					Thread.sleep(period_press * 1000);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	
	
	public class MyAdapter extends BaseAdapter
	{
		private LayoutInflater mInflater;
		
		public MyAdapter(Context context)
		{
			this.mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return 6;
		}
		
		@Override
		public Object getItem(int arg0)
		{
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public long getItemId(int arg0)
		{
			// TODO Auto-generated method stub
			return 0;
		}
		
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// TODO Auto-generated method stub
			if (position == 0) //sound
			{
				if (convertView == null)
				{
					convertView = mInflater.inflate(R.layout.activity_tab_settings_listitem_sound, null);
					
					if (cb_sound == null)
					{
						cb_sound = (CheckBox) convertView.findViewById(R.id.settings_cb_sound);
						cb_sound.setFocusable(false); //如果不取消聚焦，listview将不响应click
						cb_sound.setOnCheckedChangeListener(new OnCheckedChangeListener()
						{
							@Override
							public void onCheckedChanged(CompoundButton arg0, boolean arg1)
							{
								// TODO Auto-generated method stub
								if (macro.SETTINGS_SOUND == true)
								{
									macro.SETTINGS_SOUND = false;
									Toast.makeText(context.getApplicationContext(), macro.SOUND_REMINDING_CLOSED, Toast.LENGTH_SHORT).show();
								}
								else
								{
									macro.SETTINGS_SOUND = true;
									Toast.makeText(context.getApplicationContext(), macro.SOUND_REMINDING_OPENED, Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				}
			}
			else if (position == 1) //vibration
			{
				if (convertView == null)
				{
					convertView = mInflater.inflate(R.layout.activity_tab_settings_listitem_vibra, null);
					
					if (cb_vibration == null)
					{
						cb_vibration = (CheckBox) convertView.findViewById(R.id.settings_cb_vibra);
						cb_vibration.setFocusable(false);
						cb_vibration.setOnCheckedChangeListener(new OnCheckedChangeListener()
						{
							@Override
							public void onCheckedChanged(CompoundButton arg0, boolean arg1)
							{
								// TODO Auto-generated method stub
								if (macro.SETTINGS_VIBRA == true)
								{
									macro.SETTINGS_VIBRA = false;
									Toast.makeText(context.getApplicationContext(), macro.VIBRATING_REMINDING_CLOSED, Toast.LENGTH_SHORT).show();
								}
								else
								{
									macro.SETTINGS_VIBRA = true;
									Toast.makeText(context.getApplicationContext(), macro.SOUND_REMINDING_OPENED, Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				}
			}
			else if (position == 2) //broadcast open
			{
				if (convertView == null)
				{
					convertView = mInflater.inflate(R.layout.activity_tab_settings_listitem_bcast_on, null);
					
					if (cb_bcast_on == null)
					{
						cb_bcast_on = (CheckBox) convertView.findViewById(R.id.settings_cb_bcast_on);
						cb_bcast_on.setFocusable(false);
						cb_bcast_on.setOnCheckedChangeListener(new OnCheckedChangeListener()
						{
							@Override
							public void onCheckedChanged(CompoundButton arg0, boolean arg1)
							{
								// TODO Auto-generated method stub
								if (macro.SETTINGS_BCAST == true)
								{
									macro.SETTINGS_BCAST = false;
									Toast.makeText(context.getApplicationContext(), macro.TEST_BROADCAST_CLOSED, Toast.LENGTH_SHORT).show();
									
								}
								else
								{
									macro.SETTINGS_BCAST = true;
									Toast.makeText(context.getApplicationContext(), macro.TEST_BROADCAST_OPENED, Toast.LENGTH_SHORT).show();
									sendTemp = new SendTempBroadCast();
									sendTemp.start();
									sendPress = new SendPressBroadCast();
									sendPress.start();
								}
							}
						});
					}
				}
			}
			else if (position == 3) //set broadcast
			{
				if (convertView == null)
				{
					convertView = mInflater.inflate(R.layout.activity_tab_settings_listitem_bcvalue, null);
				}
			}
			else if (position == 4) //set threshold
			{
				if (convertView == null)
				{
					convertView = mInflater.inflate(R.layout.activity_tab_settings_listitem_threshold, null);
				}
			}
			else if (position == 5) //exit
			{
				if (convertView == null)
				{
					convertView = mInflater.inflate(R.layout.activity_tab_settings_listitem_exit, null);
				}
			}
			
			return convertView;
		}
		
	}
	
}
	