package com.ideabag.playtunes.fragment.search;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.search.SearchAlbumsAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.fragment.AlbumsOneFragment;
import com.ideabag.playtunes.fragment.SaveScrollListFragment;
import com.ideabag.playtunes.util.ISearchableAdapter;
import com.ideabag.playtunes.util.TrackerSingleton;
import com.ideabag.playtunes.util.GAEvent.Categories;
import com.ideabag.playtunes.util.GAEvent.Playlist;

public class SearchAlbumsFragment extends SaveScrollListFragment implements ISearchableAdapter {
	public static final String TAG = "Album Search Fragment";
	
	private static final int SEARCH_RESULT_NO_LIMIT = -1;
	
	private MainActivity mActivity;
	private Tracker mTracker;
	private SearchAlbumsAdapter adapter;
	String mQuery;
	
	public SearchAlbumsFragment() { /* ... */ }
	
	public SearchAlbumsFragment( SearchAlbumsAdapter mAdapter ) {
		
		adapter = mAdapter;
		
	}
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		mTracker = TrackerSingleton.getDefaultTracker( mActivity );
		mTracker.setScreenName( TAG );
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		if ( null != savedInstanceState ) {
			
			mQuery = savedInstanceState.getString( getString( R.string.key_state_query_string ) );
			
		}
		
		if ( null == adapter ) {
			
			adapter = new SearchAlbumsAdapter( getActivity(), mQuery, SEARCH_RESULT_NO_LIMIT, queryCompleted );
			
		} else {
			
			adapter.setTruncateAmount( SEARCH_RESULT_NO_LIMIT );
			adapter.setOnQueryCompletedListener( queryCompleted );
			
		}
		
		setListAdapter( adapter );
		
	}
	
	@Override public void onResume() {
		super.onResume();
		
    	
    	mTracker.send( new HitBuilders.AppViewBuilder().build() );
		
    	
    	mTracker.send( new HitBuilders.EventBuilder()
    	.setCategory( Categories.PLAYLIST )
    	.setAction( Playlist.ACTION_SHOWLIST )
    	.setValue( adapter.getCount() )
    	.build());
		
	}
	
	@Override
	public void setSearchTerms( String queryString ) {
		
		mQuery = queryString;
		
		if ( null != adapter ) {
			
			adapter.setSearchTerms( mQuery );
			
		}
		
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		String albumID = ( String ) v.getTag( R.id.tag_album_id );
		
		AlbumsOneFragment albumFragment = new AlbumsOneFragment( );
		albumFragment.setMediaID( albumID );
		
		mActivity.transactFragment( albumFragment );
		
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
					
				}
            	
            });
            
            super.onChange( selfChange );
            
        }

	};
	
	MediaQuery.OnQueryCompletedListener queryCompleted = new MediaQuery.OnQueryCompletedListener() {
		
		@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
			
			
			
		}
		
	};
	
}
