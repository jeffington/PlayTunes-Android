package com.ideabag.playtunes.fragment;

import com.ideabag.playtunes.R;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ListView;

public class SaveScrollListFragment extends ListFragment {
	
	private int scrollPosition = 0;
	
	@Override public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		
		outState.putInt( getString( R.string.key_state_scroll ), scrollPosition );
		try {
			
			ListView lv = getListView();
			
			if ( null != lv ) {
				
				outState.putInt( getString( R.string.key_state_scroll ), getListView().getScrollY() );
				
			}
			
		} catch( Exception e ) { /* ... */ }
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
    	
		if ( null != savedInstanceState ) {
			
			scrollPosition = savedInstanceState.getInt( getString( R.string.key_state_scroll ), 0 );
			
		}
		
	}
	
	@Override public void onResume() {
		super.onResume();
		
		getListView().scrollTo( 0, scrollPosition );
		
	}
	
	@Override public void onPause() {
		super.onPause();
		scrollPosition = getListView().getScrollY();
		
	}
	
	
}
