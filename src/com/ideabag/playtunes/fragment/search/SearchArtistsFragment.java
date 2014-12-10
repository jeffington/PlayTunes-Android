package com.ideabag.playtunes.fragment.search;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.search.SearchAlbumsAdapter;
import com.ideabag.playtunes.adapter.search.SearchArtistsAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.dialog.SongMenuDialogFragment;
import com.ideabag.playtunes.fragment.ArtistsOneFragment;
import com.ideabag.playtunes.fragment.SaveScrollListFragment;
import com.ideabag.playtunes.fragment.SongsFragment;
import com.ideabag.playtunes.util.ISearchableAdapter;
import com.ideabag.playtunes.util.TrackerSingleton;

public class SearchArtistsFragment extends SaveScrollListFragment implements ISearchableAdapter {
	public static final String TAG = "Artist Search Fragment";
	
	private static final int SEARCH_RESULT_NO_LIMIT = -1;
	
	private MainActivity mActivity;
	private Tracker mTracker;
	private SearchArtistsAdapter adapter;
	String mQuery;
	
	public SearchArtistsFragment() { }
	
	public SearchArtistsFragment( SearchArtistsAdapter mAdapter ) {
		
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
			
			adapter = new SearchArtistsAdapter( getActivity(), mQuery, SEARCH_RESULT_NO_LIMIT, queryCompleted );
			
		} else {
			
			adapter.setTruncateAmount( SEARCH_RESULT_NO_LIMIT );
			adapter.setOnQueryCompletedListener( queryCompleted );
			
		}
		
		
		setListAdapter( adapter );
		
	}
	
	@Override
	public void setSearchTerms( String queryString ) {
		
		mQuery = queryString;
		
		if ( null != adapter ) {
			
			adapter.setSearchTerms( mQuery );
			
		}
		
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		// Show Artist
		String artistID = ( String ) v.getTag( R.id.tag_artist_id );
		
		ArtistsOneFragment artistFragment = new ArtistsOneFragment();
		artistFragment.setMediaID( artistID );
		
		mActivity.NavigationFragment.transactFragment( artistFragment );
		
	}
/*
	View.OnClickListener songMenuClickListener = new View.OnClickListener() {
		
		@Override public void onClick( View v ) {
			
			int viewID = v.getId();
			String songID = "" + v.getTag( R.id.tag_song_id );
			
			if ( viewID == R.id.StarButton ) {
				
				ToggleButton starButton = ( ToggleButton ) v;
				
				if ( starButton.isChecked() ) {
					
					mActivity.PlaylistManager.addFavorite( songID );
					//android.util.Log.i( "starred", songID );
					
				} else {
					
					mActivity.PlaylistManager.removeFavorite( songID );
					//android.util.Log.i( "unstarred", songID );
					
				}
				
			} else if ( viewID == R.id.MenuButton ) {
				
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	        	
				SongMenuDialogFragment newFragment = new SongMenuDialogFragment();
				newFragment.setMediaID( songID );
	        	
	            newFragment.show( ft, "dialog" );
				
			}
			
		}
		
	};
*/
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
