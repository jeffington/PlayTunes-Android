package com.ideabag.playtunes.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

public class SaveScrollListFragment extends ListFragment {
	
	private static final String KEY_POSTION = "position_y";
	private static final String KEY_OFFSET = "offset_y";
	
	protected int mSavedScrollOffset = 0;
	protected int mSavedScrollListPosition = 0;
	
	@Override public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		
		outState.putInt( KEY_POSTION, mSavedScrollListPosition );
		outState.putInt( KEY_OFFSET, mSavedScrollOffset );
	}
	
	
	@Override public void onActivityCreated( Bundle inState ) {
		super.onActivityCreated( inState );
		
		if ( inState != null ) {
			
			mSavedScrollListPosition = inState.getInt( KEY_POSTION );
			mSavedScrollOffset = inState.getInt( KEY_OFFSET );
			
		}
		
		
	}
	
	protected void restoreScrollPosition() {
		
		getListView().setSelectionFromTop( mSavedScrollListPosition, mSavedScrollOffset );
		
	}
	
	@Override public void onPause() {
		super.onPause();
		
		if ( null != getListView() ) {
			
			mSavedScrollListPosition = getListView().getFirstVisiblePosition();
			mSavedScrollOffset = getListView().getChildAt( 0 ).getTop();
			
		}
		
	}
	
	
}
