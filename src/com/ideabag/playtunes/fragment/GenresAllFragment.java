package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.GenresAllAdapter;
import com.ideabag.playtunes.util.PlaylistBrowser;
import com.ideabag.playtunes.util.TrackerSingleton;

import android.app.Activity;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;

public class GenresAllFragment extends ListFragment implements PlaylistBrowser {
	
	public static final String TAG = "All Genres Fragment";
	
	GenresAllAdapter adapter;
	
	private MainActivity mActivity;
	
	@Override public void onAttach( Activity activity ) {
			
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		adapter = new GenresAllAdapter( getActivity() );
		
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
		getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
		
		
    	setListAdapter( adapter );
		
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, true, mediaStoreChanged );
    	
    	// Restore scroll position of ListView
    	if ( null != savedInstanceState ) {
    		
    		getListView().scrollTo( 0, savedInstanceState.getInt( getString( R.string.key_state_scroll ) ) );
    		
    	}
    	
	}
	
	@Override public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		outState.putInt( getString( R.string.key_state_scroll ), getListView().getScrollY() );
		
	}
		
	@Override public void onResume() {
		super.onResume();
		
    	mActivity.setActionbarTitle( getString( R.string.genres_plural) );
    	mActivity.setActionbarSubtitle( adapter.getCount() + " " + ( adapter.getCount() == 1 ? getString( R.string.genre_singular ) : getString( R.string.genres_plural ) ) );
		
		Tracker t = TrackerSingleton.getDefaultTracker( mActivity );

	    // Set screen name.
	    // Where path is a String representing the screen name.
		t.setScreenName( TAG );
		//t.set( "_count", ""+adapter.getCount() );
		
	    // Send a screen view.
		t.send( new HitBuilders.AppViewBuilder().build() );
		
		t.send( new HitBuilders.EventBuilder()
    	.setCategory( "playlist" )
    	.setAction( "show" )
    	.setLabel( TAG )
    	.setValue( adapter.getCount() )
    	.build());
		
	}
		
	@Override public void onPause() {
		super.onPause();
		//mActivity.AdView.pause();
		
		
	}
	
	@Override public void onDestroyView() {
	    super.onDestroyView();
	    
	    setListAdapter( null );
	    
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		getActivity().getContentResolver().unregisterContentObserver( mediaStoreChanged );
		
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		String genre_id = (String) v.getTag( R.id.tag_genre_id );
		
		GenresOneFragment genreFragment = new GenresOneFragment();
		
		genreFragment.setMediaID( genre_id );
		
		mActivity.transactFragment( genreFragment );
		
	}

	// PlaylistBrowser interface methods
	
	@Override public void setMediaID(String media_id) { /* ... */ }

	@Override public String getMediaID() { return ""; }
	
	
	ContentObserver mediaStoreChanged = new ContentObserver(new Handler()) {

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
	
}