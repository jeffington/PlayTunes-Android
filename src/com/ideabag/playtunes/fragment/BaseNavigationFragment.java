package com.ideabag.playtunes.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.Menu;

import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.fragment.search.SearchFragment;

public class BaseNavigationFragment extends Fragment {

	public CharSequence mActionbarTitle, mActionbarSubtitle;
	protected MainActivity mActivity;
	protected FragmentManager mFragmentManager;
	
	protected PlaylistManager mPlaylistManager;
	
	protected MusicBrowserFragment MusicBrowserFragment;
	
	// Have we warned the user that pressing Back will close the app?
	protected boolean mCloseWarningOn = false;


	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
		mPlaylistManager = new PlaylistManager( getActivity() );
		
		
        mFragmentManager = mActivity.getSupportFragmentManager();
		
        
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		
		super.onActivityCreated(savedInstanceState);
		
		MusicBrowserFragment = ( MusicBrowserFragment ) getActivity().getSupportFragmentManager().findFragmentById( R.id.MusicBrowserFragment );
		
		
	}
	
    public void showNowPlaying() {
    	
    	MusicBrowserFragment.showNowPlaying();
    	
    }
    
    public void showSearch() {
    	
    	Fragment mSearchFragment = new SearchFragment();
    	
    	transactFragment( mSearchFragment );
    	
    }
    
    public void transactFragment( Fragment mFragment ) {
    	
    	MusicBrowserFragment.transactFragment( mFragment );
    	
    	mCloseWarningOn = false;
    	
    }
    
    public boolean onKeyDown( int keycode, KeyEvent e ) {
    	
    	switch ( keycode ) {
	        
	        case KeyEvent.KEYCODE_SEARCH:
	        	
	        	showSearch();
	        	return true;
	        
	        case KeyEvent.KEYCODE_BACK:
	        	
	        	if ( mFragmentManager.getBackStackEntryCount() == 0 ) {
	        		
	        		if ( !mCloseWarningOn ) {
		        		
		        		android.widget.Toast.makeText( mActivity, getString( R.string.back_warning ), android.widget.Toast.LENGTH_LONG ).show();
		        		mCloseWarningOn = true;
		        		
		        		return true;
		        		
		        	}
	        		
	        	} else {
	        		
	        		mCloseWarningOn = false;
	        		
	        	}
	        	break;
    	}
    	
    	return false;
    	
    }

	public void setActionbarTitle( String titleString ) {
		
		mActionbarTitle = ( CharSequence ) titleString;
		
		//mActionBar.setTitle( mActionbarTitle );
		
	}
	
	public void setActionbarSubtitle( String subtitleString ) {
		
		mActionbarSubtitle = ( CharSequence ) subtitleString;
		
		//mActionBar.setSubtitle( mActionbarSubtitle );
		
	}
	
	public boolean onCreateOptionsMenu( Menu menu ) {
		
		return false;
		
	}
	
}
