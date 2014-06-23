package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.PlaylistsOneAdapter;
import com.ideabag.playtunes.util.TrackerSingleton;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class PlaylistsOneFragment extends ListFragment {
	
	public static final String TAG = "One Playlist Fragment";
	
	MainActivity mActivity;
	PlaylistsOneAdapter adapter;
	ViewGroup starredPlaylist;
	private String PLAYLIST_ID;
	
	public void setPlaylistId( String playlist_id ) {
		
		PLAYLIST_ID = playlist_id;
		
	}
	
	@Override public void onAttach( Activity activity ) {
			
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		ActionBar bar =	( ( ActionBarActivity ) getActivity() ).getSupportActionBar();
    	
    	adapter = new PlaylistsOneAdapter( getActivity(), PLAYLIST_ID );

    	
    	setListAdapter( adapter );
    	
    	bar.setTitle( "Playlist" );
		bar.setSubtitle( adapter.getCount() + " songs" );
		
    	getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
    	
	}
	
	@Override public void onResume() {
		super.onResume();
		

		Tracker t = TrackerSingleton.getDefaultTracker( mActivity );

	    // Set screen name.
	    // Where path is a String representing the screen name.
		t.setScreenName( TAG );
		t.set( "_count", ""+adapter.getCount() );
		
	    // Send a screen view.
		t.send( new HitBuilders.AppViewBuilder().build() );
		
	}
		
	@Override public void onPause() {
		super.onPause();
			
			
			
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		//mActivity send command to service
		Log.i("clicked", "" + position );
			
	}
	
}