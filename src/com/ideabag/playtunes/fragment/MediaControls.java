package com.ideabag.playtunes.fragment;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class MediaControls extends Fragment implements View.OnTouchListener {
	
	private MainActivity mActivity;
	
	private boolean mDragMode = false;
	
	int mStartPosition;
	int mEndPosition;
	int mDragPointOffset;		//Used to adjust drag view location
	
	private int mMaxHeight;
	private int mMinHeight;
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate( R.layout.fragment_footer_controls_expanded, container, false );
		
	}
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		
		super.onActivityCreated( savedInstanceState );
		
		Resources r = getResources();
		
		mMinHeight = r.getDimensionPixelSize( R.dimen.footer_height );
		
		getView().setOnTouchListener( this );
		
	}

	@Override public boolean onTouch( View v, MotionEvent ev ) {
		
		final int action = ev.getAction();
		final int x = ( int ) ev.getX();
		final int y = ( int ) ev.getY();
		
		
		
		switch ( action ) {
			
			case MotionEvent.ACTION_DOWN:
				
				mDragMode = true;
				mStartPosition = y;
				
				break;
				
			case MotionEvent.ACTION_MOVE:
				
				drag( y );
				
				break;
				
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
			default:
				
				drop();
				
				break;
				
		}
		
		return true;
		
	}
	
	private void drag( int yOffset ) {
		
		final int height = getView().getHeight();
		
		int mNewHeight = height + yOffset;
		
		if ( mNewHeight > mMaxHeight ) {
			
			mNewHeight = mMaxHeight;
			
		}
		
		if ( mNewHeight < mMinHeight ) {
			
			mNewHeight = mMinHeight;
			
		}
		
		LayoutParams mAdjustedParams = new LayoutParams( LayoutParams.MATCH_PARENT, mNewHeight );
		
		if ( height < mMaxHeight ) {
			
			getView().setLayoutParams( mAdjustedParams );
			
		}
		
	}
	
	private void drop() {
		
		mDragMode = false;
		
	}
	
	
}
