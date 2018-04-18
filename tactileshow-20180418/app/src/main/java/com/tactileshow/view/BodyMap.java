package com.tactileshow.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.tactileshow.main.R;
import com.tactileshow.util.macro;

public class BodyMap
{
	private Activity context;
	
	private View view;
	
	private DefinedViewPager viewPager;
	
	private ImageView iv_head, iv_body, iv_l_thigh, iv_r_thigh, iv_l_arm, iv_r_arm;
	
	private ImageViewClickListener ivcListener = new ImageViewClickListener();
	
	private boolean isAc_head, isAc_body, isAc_l_thigh, isAc_r_thigh, isAc_l_arm, isAc_r_arm;
	
	AlertDialog.Builder builder_dl_notact;
	
	AlertDialog dl_notact;
	
	
	public BodyMap(Activity activity, DefinedViewPager vp)
	{
		context = activity;
		viewPager = vp;
		view = context.getLayoutInflater().inflate(R.layout.activity_tab_bodymap, null);
		
		iv_head = (ImageView) view.findViewById(R.id.bodymap_iv_head);
		iv_body = (ImageView) view.findViewById(R.id.bodymap_iv_body);
		iv_l_arm = (ImageView) view.findViewById(R.id.bodymap_iv_l_arm);
		iv_r_arm = (ImageView) view.findViewById(R.id.bodymap_iv_r_arm);
		iv_l_thigh = (ImageView) view.findViewById(R.id.bodymap_iv_l_thigh);
		iv_r_thigh = (ImageView) view.findViewById(R.id.bodymap_iv_r_thigh);
		
		isAc_head = false;
		isAc_body = false;
		isAc_l_thigh = false;
		isAc_r_thigh = false;
		isAc_l_arm = true;
		isAc_r_arm = false;
		
		
		iv_head.setAlpha(0.3f);
		iv_body.setAlpha(0.3f);
		iv_l_arm.setAlpha(1.0f);
		iv_r_arm.setAlpha(0.3f);
		iv_l_thigh.setAlpha(0.3f);
		iv_r_thigh.setAlpha(0.3f);
		
		iv_head.setOnClickListener(ivcListener);
		iv_body.setOnClickListener(ivcListener);
		iv_l_arm.setOnClickListener(ivcListener);
		iv_r_arm.setOnClickListener(ivcListener);
		iv_l_thigh.setOnClickListener(ivcListener);
		iv_r_thigh.setOnClickListener(ivcListener);
		
	}
	
	class ImageViewClickListener implements OnClickListener
	{
		
		@Override
		public void onClick(View arg0)
		{
			// TODO Auto-generated method stub
			if (arg0.getId() == iv_head.getId())
				if (isAc_head == false)
					NotActDialog();
				else
					viewPager.setCurrentItem(1);
			else if (arg0.getId() == iv_body.getId())
				if (isAc_body == false)
					NotActDialog();
				else
					viewPager.setCurrentItem(1);
			else if (arg0.getId() == iv_l_arm.getId())
				if (isAc_l_arm == false)
					NotActDialog();
				else
					viewPager.setCurrentItem(1);
			else if (arg0.getId() == iv_r_arm.getId())
				if (isAc_r_arm == false)
					NotActDialog();
				else
					viewPager.setCurrentItem(1);
			else if (arg0.getId() == iv_l_thigh.getId())
				if (isAc_l_thigh == false)
					NotActDialog();
				else
					viewPager.setCurrentItem(1);
			else if (arg0.getId() == iv_r_thigh.getId())
				if (isAc_r_thigh == false)
					NotActDialog();
				else
					viewPager.setCurrentItem(1);
			
		}
		
	}
	
	void NotActDialog()
	{
		builder_dl_notact = new AlertDialog.Builder(context);
		builder_dl_notact.setTitle(macro.NO_ELECTRONIC_SKIN); // "该区域无电子皮肤"
		
		builder_dl_notact.setNegativeButton("Confirm",
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						
						dialog.dismiss();
					}
				});
		builder_dl_notact.show();
	}
	
	public View getView()
	{
		return view;
	}
}
