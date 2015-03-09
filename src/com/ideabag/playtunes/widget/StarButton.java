package com.ideabag.playtunes.widget;

import com.ideabag.playtunes.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class StarButton extends ImageButton {
	
	private static final int STAR_OFF_DRAWABLE_RES = R.drawable.ic_action_star_0;
	private static final int STAR_ON_DRAWABLE_RES = R.drawable.ic_action_star_10;
	
	protected boolean mIsChecked;
	
	public StarButton( Context context ) {
		super( context );
		// TODO Auto-generated constructor stub
		//setChecked( false );
	}
	
	public StarButton( Context context, AttributeSet attrs ) {
		super(context, attrs );
		// TODO Auto-generated constructor stub
		//setChecked( false );
	}
	
	public StarButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		//setChecked( false );
	}
	
	public boolean isChecked() { return mIsChecked; }
	
	public void setChecked( boolean isChecked ) {
		
		if ( isChecked ) {
			
			setImageResource( STAR_ON_DRAWABLE_RES );
			
		} else {
			
			setImageResource( STAR_OFF_DRAWABLE_RES );
			
		}
		
		
	}
	
}
