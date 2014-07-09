package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.PlaylistsOneAdapter;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ToggleButton;

public class PlaylistsOneFragment extends ListFragment {
	
	public static final String TAG = "One Playlist Fragment";
	
	MainActivity mActivity;
	PlaylistsOneAdapter adapter;
	ViewGroup starredPlaylist;
	private String PLAYLIST_ID;
	
	//private MenuItem menuItemEdit, menuItemDoneEditing;
	
	//private DragNDropListView ListView;
	
	public void setPlaylistId( String playlist_id ) {
		
		PLAYLIST_ID = playlist_id;
		
	}
	
	@Override public void onAttach( Activity activity ) {
			
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		ActionBar bar =	( ( ActionBarActivity ) getActivity() ).getSupportActionBar();
		setHasOptionsMenu( true );
		
    	adapter = new PlaylistsOneAdapter( mActivity, PLAYLIST_ID, songMenuClickListener );
    	
    	
    	bar.setTitle( "Playlist" );
		bar.setSubtitle( "Playlist " + adapter.getCount() + " songs" );
		
		//getListView().addHeaderView( mActivity.AdContainer, null, true );
    	getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
		//getListView().setAdapter( adapter );
    	
		getListView().setDivider( mActivity.getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
		
		setListAdapter( adapter );
		
	}

	@Override public void onResume() {
		super.onResume();
		

		Tracker tracker = TrackerSingleton.getDefaultTracker( mActivity );

	    // Set screen name.
	    // Where path is a String representing the screen name.
		tracker.setScreenName( TAG );
		tracker.send( new HitBuilders.AppViewBuilder().build() );
		
		//t.set( "_count", ""+adapter.getCount() );
		tracker.send( new HitBuilders.EventBuilder()
    	.setCategory( "playlist" )
    	.setAction( "show" )
    	.setLabel( TAG )
    	.setValue( adapter.getCount() )
    	.build());
		
		//mActivity.AdView.resume();
		
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Playlists.Members.getContentUri( "external", Long.parseLong( PLAYLIST_ID ) ), true, playlistsChanged );
		
		
	}
	
	@Override public void onPause() {
		super.onPause();
		
		//mActivity.AdView.pause();
		
		getActivity().getContentResolver().unregisterContentObserver( playlistsChanged );
		
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		//getListView().removeHeaderView( mActivity.AdContainer );
		
	}
	
	@Override public void onDestroyView() {
	    super.onDestroyView();
	    
	    setListAdapter( null );
	    
	}

	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		mActivity.mBoundService.setPlaylistCursor( adapter.getCursor() );
		
		mActivity.mBoundService.setPlaylistPosition( position - l.getHeaderViewsCount() );
		
		mActivity.mBoundService.play();
		
	}
	
	@Override public void onCreateOptionsMenu( Menu menu, MenuInflater inflater) {
		
	   inflater.inflate( R.menu.menu_playlist_one, menu );
	   
	}
	
	
	@Override public boolean onOptionsItemSelected( MenuItem item ) {
		
	   switch ( item.getItemId() ) {
	   
	      case R.id.MenuPlaylistEdit:
	         
	    	  ActionBar bar = mActivity.getSupportActionBar();
	    	  
	    	  bar.setTitle("Editing Playlist");
	    	  mActivity.actionbarTitle = bar.getTitle();
	    	  bar.setSubtitle( null );
	    	  mActivity.actionbarSubtitle = bar.getSubtitle();
	    	  adapter.setEditing( true );
	    	  //ListView.invalidate();
	    	  
	         return true;
	         
	      case R.id.MenuPlaylistDone:
	    	  
	    	  adapter.setEditing( false );
	    	  //ListView.invalidate();
	    	  
	    	  return true;
	      default:
	         return super.onOptionsItemSelected(item);
	         
	   }
	   
	}

	ContentObserver playlistsChanged = new ContentObserver(new Handler()) {

        @Override public void onChange( boolean selfChange ) {
            
            Log.i("onChange", "?" + selfChange);
            
            mActivity.runOnUiThread( new Runnable() {

				@Override public void run() {
					
					adapter.requery();
					adapter.notifyDataSetChanged();
					//getListView().invalidate();
				}
            	
            });
            
            super.onChange( selfChange );
            
        }

	};
	
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
				
				
				
			}
			
			
			
		}
		
	};
	
	
}