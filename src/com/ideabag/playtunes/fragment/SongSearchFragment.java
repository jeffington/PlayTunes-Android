package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.SongSearchAdapter;
import com.ideabag.playtunes.dialog.SongMenuDialogFragment;
import com.ideabag.playtunes.util.PlaylistBrowser;
import com.ideabag.playtunes.util.TrackerSingleton;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ToggleButton;

public class SongSearchFragment extends ListFragment implements PlaylistBrowser {
	
	public static final String TAG = "Song Search Fragment";
	
    private MainActivity mActivity;
	private AutoCompleteTextView mQueryTextView;
    
	SongSearchAdapter adapter;
	
	protected String mSearchQuery = null;
	
	public SongSearchFragment() { }
	
	public SongSearchFragment( String query ) {
		
		setMediaID( query );
		
		
		
	}
	
	
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		setHasOptionsMenu( true );
		
		//adapter = new SongSearchAdapter( getActivity(), songMenuClickListener, mSearchQuery );
    	
    	
    	getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
    	
    	getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
    	
		
    	//setListAdapter( adapter );
    	
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mediaStoreChanged );
    	
		ActionBar mBar = mActivity.getSupportActionBar();
			
		mBar.setCustomView( R.layout.view_search_compat );
		mBar.setDisplayShowCustomEnabled( true );
		
		mQueryTextView = ( AutoCompleteTextView ) mBar.getCustomView().findViewById( R.id.SearchQuery );
		
		if ( null != mSearchQuery ) {
			
			mQueryTextView.setText( mSearchQuery );
			
		}
		
		mBar.getCustomView().findViewById( R.id.SearchButton ).setOnClickListener( new OnClickListener() {

			@Override public void onClick( View v ) {
				// TODO Auto-generated method stub
				setMediaID( mQueryTextView.getEditableText().toString() );
				
			}
			
		});
		
		
		
		mQueryTextView.setOnEditorActionListener( new OnEditorActionListener() {

			@Override public boolean onEditorAction( TextView view, int actionId, KeyEvent event ) {
				
				if ( actionId == EditorInfo.IME_ACTION_SEARCH || event.getKeyCode() == KeyEvent.KEYCODE_ENTER ) {
					
					// Do search
					setMediaID( mQueryTextView.getEditableText().toString() );
					
					return true;
					
				}
				
				// TODO Auto-generated method stub
				return false;
				
			}
			
		});
		
	}
	
	
	@Override public void onResume() {
		super.onResume();
		
		mActivity.setActionbarTitle( null );
    	mActivity.setActionbarSubtitle( null );
		/*
		Tracker tracker = TrackerSingleton.getDefaultTracker( mActivity.getBaseContext() );
		
		tracker.setScreenName( TAG );
		tracker.send( new HitBuilders.AppViewBuilder().build() );
		
		//t.set( "_count", ""+adapter.getCount() );
		tracker.send( new HitBuilders.EventBuilder()
    	.setCategory( "playlist" )
    	.setAction( "show" )
    	.setLabel( TAG )
    	.setValue( adapter.getCount() )
    	.build());
	        // Send a screen view.
		
		*/
	}
		
	@Override public void onPause() {
		super.onPause();
		//mActivity.AdView.pause();
		
	}
	
	@Override public void onDestroyView() {
		super.onDestroyView();
	    
	    setListAdapter( null );
	    mActivity.getSupportActionBar().getCustomView().setVisibility( View.GONE );
		mActivity.getSupportActionBar().setCustomView( null );
		
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		setHasOptionsMenu( false );
		
		
		
		getActivity().getContentResolver().unregisterContentObserver( mediaStoreChanged );
		
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		String playlistName = mActivity.getSupportActionBar().getTitle().toString();
		
		mActivity.mBoundService.setPlaylist( adapter.getQuery(), playlistName, SongsFragment.class, null );
		//mActivity.mBoundService.setPlaylistCursor( adapter.getCursor() );
		
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


	// PlaylistBrowser interface methods
	
	@Override public void setMediaID(String media_id) {
		
		mSearchQuery = media_id;
		
		if ( null == adapter ) {
			
			adapter = new SongSearchAdapter( getActivity(), songMenuClickListener, mSearchQuery );
			
			setListAdapter( adapter );
			
		} else {
			
			adapter.setQuery( mSearchQuery );
			
		}
		
		adapter.notifyDataSetChanged();
		
	}

	@Override public String getMediaID() {
		
		return mSearchQuery;
		
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