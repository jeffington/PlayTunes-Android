package com.ideabag.playtunes.fragment.search;

import android.app.Activity;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.search.SearchAlbumsAdapter;
import com.ideabag.playtunes.adapter.search.SearchSongsAdapter;
import com.ideabag.playtunes.dialog.SongMenuDialogFragment;
import com.ideabag.playtunes.fragment.SaveScrollListFragment;
import com.ideabag.playtunes.fragment.SongsFragment;
import com.ideabag.playtunes.util.ISearchable;
import com.ideabag.playtunes.util.TrackerSingleton;
import com.ideabag.playtunes.util.GAEvent.Categories;
import com.ideabag.playtunes.util.GAEvent.Playlist;

public class SearchAlbumsFragment extends SaveScrollListFragment implements ISearchable {
	public static final String TAG = "Album Search Fragment";
	
	private static final int SEARCH_RESULT_NO_LIMIT = -1;
	
	private MainActivity mActivity;
	private Tracker mTracker;
	private SearchAlbumsAdapter adapter;
	String mQuery;
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		mTracker = TrackerSingleton.getDefaultTracker( mActivity );
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		if ( null != savedInstanceState ) {
			
			mQuery = savedInstanceState.getString( getString( R.string.key_state_query_string ) );
			
		}
		
		adapter = new SearchAlbumsAdapter( getActivity(), mQuery, SEARCH_RESULT_NO_LIMIT );
		
		setListAdapter( adapter );
		
	}
	
	@Override public void onResume() {
		super.onResume();
		
    	mTracker.setScreenName( TAG );
    	mTracker.send( new HitBuilders.AppViewBuilder().build() );
		
    	
    	mTracker.send( new HitBuilders.EventBuilder()
    	.setCategory( Categories.PLAYLIST )
    	.setAction( Playlist.ACTION_SHOWLIST )
    	.setValue( adapter.getCount() )
    	.build());
		
	}
	
	@Override
	public void setQuery( String queryString ) {
		// TODO Auto-generated method stub
		mQuery = queryString;
		
		if ( null != adapter ) {
			
			adapter.setQuery( mQuery );
			
		}
		
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		String playlistName = mActivity.getSupportActionBar().getTitle().toString();
		
		mActivity.mBoundService.setPlaylist( adapter.getQuery(), playlistName, SearchFragment.class, mQuery );
		//mActivity.mBoundService.setPlaylistCursor( adapter.getCursor() );
		
		mActivity.mBoundService.setPlaylistPosition( position );
		
		mActivity.mBoundService.play();
		
		mTracker.send( new HitBuilders.EventBuilder()
    	.setCategory( Categories.PLAYLIST )
    	.setAction( Playlist.ACTION_CLICK )
    	.setValue( position )
    	.build());
		
	}
	
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
