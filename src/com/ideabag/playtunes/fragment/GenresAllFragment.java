package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.GenresAllAdapter;
import com.ideabag.playtunes.util.TrackerSingleton;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;

public class GenresAllFragment extends ListFragment {
	
	public static final String TAG = "All Genres Fragment";
	
	GenresAllAdapter adapter;
	private MainActivity mActivity;
	
	@Override public void onAttach( Activity activity ) {
			
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		ActionBar bar =	( ( ActionBarActivity ) getActivity()).getSupportActionBar();
		
		adapter = new GenresAllAdapter( getActivity() );
    	
    	setListAdapter( adapter );
    	
    	bar.setTitle( "Genres" );
    	mActivity.actionbarTitle = bar.getTitle();
		bar.setSubtitle( adapter.getCount() + " genres" );
		mActivity.actionbarSubtitle = bar.getSubtitle();
    	
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
		
		String genre_id = (String) v.getTag( R.id.tag_genre_id );
		
		GenresOneFragment genreFragment = new GenresOneFragment();
		
		genreFragment.setGenreId( genre_id );
		
		mActivity.transactFragment( genreFragment );
		
	}
	
}