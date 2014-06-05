package com.ideabag.playtunes.fragment;

import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.PlaylistsOneAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;

public class PlaylistsOneFragment extends ListFragment {
	
	private static final char MUSIC_NOTE = (char) 9834;
	
	MainActivity mActivity;
	PlaylistsOneAdapter adapter;
	
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
    	
    	bar.setTitle( "" + Character.toString(MUSIC_NOTE) + "Dark Side of the Moon" );
		//bar.setSubtitle( cursor.getCount() + " songs" );
    	getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
    	
	}
	
	@Override public void onResume() {
		super.onResume();
		
		
	}
		
	@Override public void onPause() {
		super.onPause();
			
			
			
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		//mActivity send command to service
			
	}
	
}