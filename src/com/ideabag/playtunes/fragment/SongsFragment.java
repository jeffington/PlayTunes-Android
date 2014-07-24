package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.SongsAllAdapter;
import com.ideabag.playtunes.dialog.SongMenuDialogFragment;
import com.ideabag.playtunes.util.MergeAdapter;
import com.ideabag.playtunes.util.PlaylistBrowser;
import com.ideabag.playtunes.util.TrackerSingleton;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.ToggleButton;

public class SongsFragment extends ListFragment implements PlaylistBrowser {
	
	public static final String TAG = "All Songs Fragment";
	
    private MainActivity mActivity;
    
	MergeAdapter adapter;
	SongsAllAdapter songAdapter;
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
    	adapter = new MergeAdapter();
		
		songAdapter = new SongsAllAdapter( getActivity(), songMenuClickListener );
    	
		//adapter.addView( mActivity.AdContainer, false );
		adapter.addAdapter( songAdapter );
    	
    	getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
    	
    	getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
    	
		
    	//getListView().addHeaderView( mActivity.AdContainer );
    	setListAdapter( adapter );
    	
    	
	}
	

	@Override public void onResume() {
		super.onResume();
		
		mActivity.setActionbarTitle( getString( R.string.all_songs ) );
    	mActivity.setActionbarSubtitle( adapter.getCount() + " " + ( adapter.getCount() == 1 ? getString( R.string.song_singular ) : getString( R.string.songs_plural ) ) );
		
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
		
		
	}
		
	@Override public void onPause() {
		super.onPause();
		//mActivity.AdView.pause();
		
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		//getListView().removeHeaderView( mActivity.AdContainer );
		
	}
	
	@Override public void onDestroyView() {
		super.onDestroyView();
	    //adapter = null;
	    
	    //getListView().removeHeaderView( mActivity.AdContainer );
	    setListAdapter( null );
	    
	    
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		String playlistName = mActivity.getSupportActionBar().getTitle().toString();
		
		mActivity.mBoundService.setPlaylist( songAdapter.getCursor(), playlistName, SongsFragment.class, null );
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
	
	@Override public void setMediaID(String media_id) { /* ... */ }

	@Override public String getMediaID() { return ""; }
	
}