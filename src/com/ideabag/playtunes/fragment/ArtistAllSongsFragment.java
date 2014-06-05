package com.ideabag.playtunes.fragment;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.ArtistAllSongsAdapter;
import com.ideabag.playtunes.adapter.ArtistsOneAdapter;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class ArtistAllSongsFragment extends ListFragment {
	
	ArtistAllSongsAdapter adapter;
	
	private MainActivity mActivity;
	
	private String ARTIST_ID;
	
	public void setArtistId( String album_id ) {
		
		ARTIST_ID = album_id;
		
	}
	
	@Override public void onAttach( Activity activity ) {
			
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		ActionBar bar =	( ( ActionBarActivity ) getActivity() ).getSupportActionBar();
		//android.util.Log.i( "ARTIST_ID", ARTIST_ID );
    	adapter = new ArtistAllSongsAdapter( getActivity(), ARTIST_ID );
    	
    	
    	setListAdapter( adapter );
    	
    	//bar.setTitle( adapter.ArtistName );
		
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
    	
	}
	
	@Override public void onResume() {
		super.onResume();
		
		
	}
		
	@Override public void onPause() {
		super.onPause();
		
		
		
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		
		
	}
	
}