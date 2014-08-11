package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.SongSearchAdapter;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ToggleButton;

public class SongSearchFragment extends ListFragment implements PlaylistBrowser {
	
	public static final String TAG = "Song Search Fragment";
	
    private MainActivity mActivity;
    
	SongSearchAdapter adapter;
	
	String mSearchTerms = null;
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		setHasOptionsMenu( true );
		
		if ( null != savedInstanceState ) {
			
			mSearchTerms = savedInstanceState.getString( "search" );
			
		}
		
		adapter = new SongSearchAdapter( getActivity(), songMenuClickListener, mSearchTerms );
    	
    	
    	getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
    	
    	getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
    	
		
    	setListAdapter( adapter );
    	
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mediaStoreChanged );
    	
	}
	

	@Override public void onResume() {
		super.onResume();
		
		//mActivity.setActionbarTitle( getString( R.string.all_songs ) );
    	//mActivity.setActionbarSubtitle( adapter.getCount() + " " + ( adapter.getCount() == 1 ? getString( R.string.song_singular ) : getString( R.string.songs_plural ) ) );
		
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
	
	@Override public void onDestroyView() {
		super.onDestroyView();
	    
	    setListAdapter( null );
	    
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		setHasOptionsMenu( false );
		getActivity().getContentResolver().unregisterContentObserver( mediaStoreChanged );
		
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		String playlistName = mActivity.getSupportActionBar().getTitle().toString();
		
		mActivity.mBoundService.setPlaylist( adapter.getQuery(), playlistName, SongSearchFragment.class, null );
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
	// For the search fragment, we use the media_id in a different way than it was originally intended
	// In this case, we use the media_id as the search terms
	// 
	
	@Override public void setMediaID(String media_id) {
		
		mSearchTerms = media_id;
		
		adapter.buildSearchQuery( mSearchTerms );
		
	}

	@Override public String getMediaID() {
		
		return mSearchTerms;
		
	}
	
	@Override public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		
		outState.putString( "search_terms", mSearchTerms );
		
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
	
	
	@Override public void onCreateOptionsMenu( Menu menu, MenuInflater inflater) {
		
	   inflater.inflate( R.menu.menu_playlist_one, menu );
	   
	}
	/*
	@Override public void onPrepareOptionsMenu( Menu menu ) {
		
		super.onPrepareOptionsMenu( menu );
		
		if ( !mActivity.mShouldHideActionItems ) {
			
			menuItemDoneEditing.setVisible( isEditing );
			
			menuItemEdit.setVisible( !isEditing );
	    	
		}
	    
	}
	*/
	
	@Override public boolean onOptionsItemSelected( MenuItem item ) {
		
	   switch ( item.getItemId() ) {
	   
	      case R.id.MenuPlaylistEdit:
	         
	    	  isEditing = true;
	    	  
	    	  menuItemDoneEditing.setVisible( isEditing );
	    	  menuItemEdit.setVisible( !isEditing );
	    	  
	    	  adapter.setEditing( isEditing );
	    	  mListView.setDraggingEnabled( isEditing );
	    	  
	    	  mTitle = (String) mActivity.getSupportActionBar().getTitle();
	    	  mSubtitle = (String) mActivity.getSupportActionBar().getSubtitle();
	    	  mActivity.setActionbarSubtitle( mTitle );
	    	  mActivity.setActionbarTitle( getActivity().getString( R.string.playlist_editing ) );
	    	  
	         return true;
	         
	      case R.id.MenuPlaylistDone:
	    	  
	    	  isEditing = false;
	    	  
	    	  menuItemDoneEditing.setVisible( isEditing );
	    	  menuItemEdit.setVisible( !isEditing );
	    	  adapter.setEditing( isEditing );
	    	  mListView.setDraggingEnabled( isEditing );
	    	  
	    	  mActivity.setActionbarTitle( mTitle );
	    	  mActivity.setActionbarSubtitle( mSubtitle );
	    	  
	    	  
	    	  return true;
	    	  
	      default:
	         return super.onOptionsItemSelected(item);
	         
	   }
	   
	}
	
	
}