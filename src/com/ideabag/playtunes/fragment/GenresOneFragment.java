package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.GenresAllAdapter;
import com.ideabag.playtunes.adapter.GenresOneAdapter;
import com.ideabag.playtunes.util.TrackerSingleton;

import android.app.Activity;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class GenresOneFragment extends ListFragment {
	
	public static final String TAG = "One Genre Fragment";
	
	GenresOneAdapter adapter;
	private MainActivity mActivity;
	
	private String GENRE_ID;
	
	public void setGenreId( String id ) {
		
		this.GENRE_ID = id;
		
	}
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		ActionBar bar =	( ( ActionBarActivity ) getActivity()).getSupportActionBar();
		
		adapter = new GenresOneAdapter( getActivity(), GENRE_ID );
    	
    	
    	bar.setTitle( "Genres" );
		bar.setSubtitle( "Genre" );
		mActivity.actionbarSubtitle = bar.getSubtitle();
    	
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
    	
		getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
		//getListView().addHeaderView( mActivity.AdContainer, null, true );
		
		setListAdapter( adapter );
    	
	}
		
	@Override public void onResume() {
		super.onResume();
		
		Tracker t = TrackerSingleton.getDefaultTracker( mActivity );

	    // Set screen name.
	    // Where path is a String representing the screen name.
		t.setScreenName( TAG );
	    // Send a screen view.
		t.send( new HitBuilders.AppViewBuilder().build() );
		
		t.send( new HitBuilders.EventBuilder()
    	.setCategory( "playlist" )
    	.setAction( "show" )
    	.setLabel( TAG )
    	.setValue( adapter.getCount() )
    	.build());
		/*
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Genres.Members.getContentUri( "external", Long.parseLong( GENRE_ID ) ),
				true,
				playlistsChanged
			);
		*/
	}
		
	@Override public void onPause() {
		super.onPause();
			
			
			
	}
	
	@Override public void onDestroyView() {
	    super.onDestroyView();
	    
	    setListAdapter( null );
	    
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		mActivity.mBoundService.setPlaylistCursor( adapter.getCursor() );
		
		android.util.Log.i("List Header Count", "" + l.getHeaderViewsCount() + " position " + position );
		
		mActivity.mBoundService.setPlaylistPosition( position - l.getHeaderViewsCount() );
		
		mActivity.mBoundService.play();
		
		// Set the title of the playlist
		
		// 
			
	}
	/*
	ContentObserver playlistsChanged = new ContentObserver(new Handler()) {

        @Override public void onChange( boolean selfChange ) {
            
            mActivity.runOnUiThread( new Runnable() {

				@Override public void run() {
					
					adapter.requery();
					adapter.notifyDataSetChanged();
					
				}
            	
            });
            
            super.onChange( selfChange );
            
        }

	};
	*/
}