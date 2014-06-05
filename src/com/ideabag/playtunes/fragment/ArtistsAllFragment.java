package com.ideabag.playtunes.fragment;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.ArtistsAllAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;

public class ArtistsAllFragment extends ListFragment {
	
	private static final char MUSIC_NOTE = (char) 9834;

	ArtistsAllAdapter adapter;
	private MainActivity mActivity;
	
	@Override public void onAttach( Activity activity ) {
			
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		ActionBar bar =	( ( ActionBarActivity ) getActivity() ).getSupportActionBar();
    	
    	adapter = new ArtistsAllAdapter( getActivity() );
    	
    	setListAdapter( adapter );
    	
    	bar.setTitle( "Artists" );
		bar.setSubtitle( adapter.getCount() + " artists" );
		
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
    	
	}
		
	@Override public void onResume() {
		super.onResume();
			
		
	}
		
	@Override public void onPause() {
		super.onPause();
			
			
			
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		String albumID = ( String ) v.getTag( R.id.tag_artist_id );
		
		ArtistsOneFragment artistFragment = new ArtistsOneFragment();
		artistFragment.setArtistId( albumID );
		
		mActivity.transactFragment( artistFragment );
		
		
	}
	
}