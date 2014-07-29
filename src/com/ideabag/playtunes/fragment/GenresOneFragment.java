package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.GenresAllAdapter;
import com.ideabag.playtunes.adapter.GenresOneAdapter;
import com.ideabag.playtunes.dialog.SongMenuDialogFragment;
import com.ideabag.playtunes.util.PlaylistBrowser;
import com.ideabag.playtunes.util.TrackerSingleton;

import android.app.Activity;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ToggleButton;

public class GenresOneFragment extends ListFragment implements PlaylistBrowser {
	
	public static final String TAG = "One Genre Fragment";
	
	GenresOneAdapter adapter;
	private MainActivity mActivity;
	
	private String GENRE_ID = "";
	
	@Override public void setMediaID(String media_id) {
		
		GENRE_ID = media_id;
		
	}
	
	@Override public String getMediaID() { return GENRE_ID; }
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		outState.putString( getString( R.string.key_state_media_id ), GENRE_ID );
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		if ( null != savedInstanceState ) {
			
			GENRE_ID = savedInstanceState.getString( getString( R.string.key_state_media_id ) );
			
		}
		
		adapter = new GenresOneAdapter( getActivity(), GENRE_ID, songMenuClickListener );
    	
    	
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
    	
		getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
		//getListView().addHeaderView( mActivity.AdContainer, null, true );
		
		setListAdapter( adapter );
    	
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Genres.Members.getContentUri( "external", Long.parseLong( GENRE_ID ) ), true, mediaStoreChanged );
		
	}
		
	@Override public void onResume() {
		super.onResume();
		
		mActivity.setActionbarTitle( "Genres" );
		mActivity.setActionbarSubtitle( getString( R.string.genre_singular ) );
		
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
		
	}
		
	@Override public void onPause() {
		super.onPause();
			
			
			
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
		
		String playlistName = mActivity.getSupportActionBar().getTitle().toString();
		
		mActivity.mBoundService.setPlaylist( adapter.getCursor(), playlistName, GenresOneFragment.class, GENRE_ID );
		
		mActivity.mBoundService.setPlaylistPosition( position - l.getHeaderViewsCount() );
		
		mActivity.mBoundService.play();
		
		// Set the title of the playlist
		
		// 
			
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