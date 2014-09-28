package com.ideabag.playtunes.fragment.search;

import android.app.Activity;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.search.SearchSongsAdapter;
import com.ideabag.playtunes.dialog.SongMenuDialogFragment;
import com.ideabag.playtunes.fragment.SaveScrollListFragment;
import com.ideabag.playtunes.fragment.SongsFragment;
import com.ideabag.playtunes.util.ISearchableAdapter;

public class SearchSongsFragment extends SaveScrollListFragment implements ISearchableAdapter {
	
	public static final String TAG = "SearchSongsFragment";
	
	private static final int SEARCH_RESULT_NO_LIMIT = -1;
	
	private MainActivity mActivity;
	private SearchSongsAdapter adapter;
	String mQuery;
	
	public SearchSongsFragment() { }
	
	public SearchSongsFragment( SearchSongsAdapter mAdapter ) {
		
		adapter = mAdapter;
		
	}
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		if ( null != savedInstanceState ) {
			
			mQuery = savedInstanceState.getString( getString( R.string.key_state_query_string ) );
			
		}
		
		if ( adapter == null ) {
			
			adapter = new SearchSongsAdapter( getActivity(), songMenuClickListener, mQuery, SEARCH_RESULT_NO_LIMIT );
			
		} else {
			
			adapter.setTruncateAmount( SEARCH_RESULT_NO_LIMIT );
			
		}
		
		setListAdapter( adapter );
		
	}
	
	@Override
	public void setSearchTerms( String queryString ) {
		// TODO Auto-generated method stub
		mQuery = queryString;
		
		if ( null != adapter ) {
			
			adapter.setSearchTerms( mQuery );
			
		}
		
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		String playlistName = ( String ) mActivity.getSupportActionBar().getTitle();
		
		mActivity.mBoundService.setPlaylist( adapter.getQuery(), playlistName, SearchFragment.class, mQuery );
		
		mActivity.mBoundService.setPlaylistPosition( position );
		
		mActivity.mBoundService.play();
		
	}

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
