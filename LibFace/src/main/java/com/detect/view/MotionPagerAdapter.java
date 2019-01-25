package com.detect.view;

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facecore.bh.R;
import com.libface.bh.library.LibLiveDetect;

public class MotionPagerAdapter extends PagerAdapter{

	private int[] mMotions;
	private AnimationDrawable aDrawable;

	@SuppressLint("NewApi")
	public MotionPagerAdapter(int[] motions){
		mMotions = Arrays.copyOf(motions, motions.length);
	}	

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Context context = container.getContext();
		View view = View.inflate(context, R.layout.layout_view_motion, null);
		int motion = mMotions[position];
		try {
			switch(motion) {
				case LibLiveDetect.EYEBLINK:
//					((TextView)view.findViewById(R.id.txt_title)).setText(context.getString(R.string.common_blink));
					((ImageView)view.findViewById(R.id.img_image)).setImageResource(R.drawable.motion_img_blink);
					break;
				case LibLiveDetect.OPENMOUTH:
//					((TextView)view.findViewById(R.id.txt_title)).setText(context.getString(R.string.common_mouth));
					((ImageView)view.findViewById(R.id.img_image)).setImageResource(R.drawable.motion_img_mouth);
					break;
				case LibLiveDetect.DOWN_PITCH:
//					((TextView)view.findViewById(R.id.txt_title)).setText(context.getString(R.string.common_nod));
					((ImageView)view.findViewById(R.id.img_image)).setImageResource(R.drawable.motion_img_nod);
					break;
				case LibLiveDetect.YAW:
//					((TextView)view.findViewById(R.id.txt_title)).setText(context.getString(R.string.common_yaw));
					((ImageView)view.findViewById(R.id.img_image)).setImageResource(R.drawable.motion_img_yaw);
					break;
			}
		} catch(OutOfMemoryError e) {
			e.printStackTrace();
		}
		aDrawable = (AnimationDrawable)
						((((ImageView)view.findViewById(R.id.img_image))).getDrawable());
		aDrawable.start();

		container.addView(view);
		
		return view;
	}

	@Override
	public int getCount() {
		return mMotions == null ? 0 : mMotions.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}
	
	public void stopAnimation(){
		aDrawable.stop();
	}
	
}