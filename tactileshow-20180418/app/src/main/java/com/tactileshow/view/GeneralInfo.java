package com.tactileshow.view;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tactileshow.main.R;
import com.tactileshow.util.macro;

import java.io.IOException;

public class GeneralInfo
{
	
	private Activity context;
	
	private View view;
	
	private Vibrator vibrator;
	
	Uri uri_ring;
	
	MediaPlayer player = new MediaPlayer();
	
	AudioManager audioManager;
	
	private TextView temp, press, germ;
	
	private ImageView iv_hand, iv_needle, iv_alert, iv_germ, iv_fire;
	
	public GeneralInfo(Activity activity)
	{
		context = activity;
		view = context.getLayoutInflater().inflate(R.layout.activity_tab_general, null);
		
		vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
		
		InitRing();
		
		
		temp = (TextView) view.findViewById(R.id.general_temp);
		press = (TextView) view.findViewById(R.id.general_press);
		germ = (TextView) view.findViewById(R.id.general_germ);
		
		iv_germ = (ImageView) view.findViewById(R.id.general_img_germ);
		iv_hand = (ImageView) view.findViewById(R.id.general_img_hand);
		iv_needle = (ImageView) view.findViewById(R.id.general_img_needle);
		iv_alert = (ImageView) view.findViewById(R.id.general_img_alert);
		iv_fire = (ImageView) view.findViewById(R.id.general_img_fire);
		
		iv_germ.setVisibility(View.INVISIBLE);
		iv_needle.setVisibility(View.INVISIBLE);
		iv_alert.setVisibility(View.INVISIBLE);
		iv_fire.setVisibility(View.INVISIBLE);
		
		
		setTemp(20.2);
		setPress(30);
		setGerm(20.2);
	}
	
	private void InitRing()
	{
		uri_ring = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		try
		{
			player.setDataSource(context, uri_ring);
		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalStateException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
	}
	
	private void StartVibrate()
	{
		if (macro.SETTINGS_VIBRA == true)
			vibrator.vibrate(macro.VIBRATION_MODE, -1);
	}
	
	private void StartRing()
	{
		if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0 && macro.SETTINGS_SOUND == true)
		{
			player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
			player.setLooping(false);
			try
			{
				player.prepare();
			}
			catch (IllegalStateException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			player.start();
		}
	}
	
	private String parseTemp(double tp)
	{
		if (tp > macro.SETTING_TEMP_RANGE[2])
		{
			temp.setTextColor(Color.RED);
			iv_fire.setVisibility(View.VISIBLE);
			StartVibrate();
			StartRing();
			return macro.OVER_HEAT; // "过高";
		}
		else if (tp > macro.SETTING_TEMP_RANGE[1])
		{
			temp.setTextColor(Color.GREEN);
			iv_fire.setVisibility(View.INVISIBLE);
			return macro.COMFORTABLE; // "舒适";
		}
		else if (tp > macro.SETTING_TEMP_RANGE[0])
		{
			temp.setTextColor(Color.argb(100, 57, 174, 204));
			iv_fire.setVisibility(View.INVISIBLE);
			return macro.COLD; // "偏冷";
		}
		else
		{
			temp.setTextColor(Color.BLUE);
			iv_fire.setVisibility(View.INVISIBLE);
			return macro.TOO_COLD; // "过低";
		}
	}
	
	private String parsePress(double tp)
	{
		if (tp > macro.SETTING_PRESS_RANGE[2])
		{
			press.setTextColor(Color.BLUE);
			iv_needle.setVisibility(View.VISIBLE);
			StartVibrate();
			StartRing();
			return macro.PRESS_SEVERELY_AND_OFF; // "严重挤压，请离开";
		}
		else if (tp > macro.SETTING_PRESS_RANGE[2])
		{
			press.setTextColor(Color.GREEN);
			iv_needle.setVisibility(View.INVISIBLE);
			return macro.PRESS_HEAVY; // "较重挤压";
		}
		else if (tp > macro.SETTING_PRESS_RANGE[2])
		{
			press.setTextColor(Color.MAGENTA);
			iv_needle.setVisibility(View.INVISIBLE);
			return macro.PRESS_LIGHT; // "轻微挤压";
		}
		else
		{
			press.setTextColor(Color.YELLOW);
			iv_needle.setVisibility(View.INVISIBLE);
			return macro.NO_PRESS; // "无挤压";
		}
	}
	
	private String parseGerm(double tp)
	{
		if (tp >= 38 && tp <= 42)
		{
			germ.setTextColor(Color.RED);
			iv_germ.setVisibility(View.VISIBLE);
			return macro.INFLAMMATION; // "可能出现炎症";
		}
		else
		{
			germ.setTextColor(Color.GREEN);
			iv_germ.setVisibility(View.INVISIBLE);
			return macro.NO_INFLAMMTION; // "没有出现炎症";
		}
		
	}
	
	public void setTemp(double tp)
	{
		temp.setText(parseTemp(tp));
	}
	
	public void setPress(double hm)
	{
		press.setText(parsePress(hm));
	}
	
	public void setGerm(double tp)
	{
		germ.setText(parseGerm(tp));
	}
	
	public View getView()
	{
		return view;
	}
}
