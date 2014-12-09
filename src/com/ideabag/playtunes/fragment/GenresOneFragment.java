package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.GenresOneAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.dialog.SongMenuDialogFragment;
import com.ideabag.playtunes.util.IMusicBrowser;
import com.ideabag.playtunes.util.TrackerSingleton;
import com.ideabag.playtunes.util.GAEvent.Categories;
import com.ideabag.playtunes.util.GAEvent.Playlist;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ToggleButton;

public class GenresOneFragment extends SaveScrollListFragment implements IMusicBrowser {
	
	public static final String TAG = "One Genre Fragment";
	
	GenresOneAdapter adapter;
	private MainActivity mActivity;
	private Tracker mTracker;
	
	private String GENRE_ID = "";
	
	@Override public void setMediaID(String media_id) {
		
		GENRE_ID = media_id;
		
	}
	
	@Override public String getMediaID() { return GENRE_ID; }
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		mTracker = TrackerSingleton.getDefaultTracker( mActivity );
		
		mTracker.setScreenName( TAG );
		mActivity.setActionbarSubtitle( getString( R.string.genre_singular ) );
		
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
		
		adapter = new GenresOneAdapter( getActivity(), GENRE_ID, songMenuClickListener, new MediaQuery.OnQueryCompletedListener() {
			
			@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
				
				restoreScrollPosition();
				
				mTracker.send( new HitBuilders.EventBuilder()
		    	.setCategory( Categories.PLAYLIST )
		    	.setAction( Playlist.ACTION_SHOWLIST )
		    	.setValue( mResult.getCount() )
		    	.build());
				
			}
			
		});
		
		MediaQuery mGenreQuery = new MediaQuery(
				MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
    			new String[] {
					
					MediaStore.Audio.Genres.NAME
					
				},
				MediaStore.Audio.Genres._ID + "=?",
				new String[] {
					
						GENRE_ID
					
				},
				null);
		
		MediaQuery.executeAsync( getActivity(), mGenreQuery, new MediaQuery.OnQueryCompletedListener() {
			
			@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
				
				mResult.moveToFirst();
				String mGenreName = mResult.getString( mResult.getColumnIndex( MediaStore.Audio.Genres.NAME ) );
				
				mActivity.setActionbarTitle( mGenreName );
				
			}
			
		});
    	
    	
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
    	
		getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
		getListView().setOnItemLongClickListener( mSongMenuLongClickListener );
		//getListView().addHeaderView( mActivity.AdContainer, null, true );
		
		setListAdapter( adapter );
    	
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Genres.Members.getContentUri( "external", Long.parseLong( GENRE_ID ) ), true, mediaStoreChanged );
    	
		
	}
		
	@Override public void onResume() {
		super.onResume();
		
		
		mActivity.setActionbarSubtitle( getString( R.string.genre_singular ) );

	    // Set screen name.
	    // Where path is a String representing the screen name.
		
	    // Send a screen view.
		mTracker.send( new HitBuilders.AppViewBuilder().build() );
		
	}
		
	@Override public void onPause() {
		super.onPause();
			
			
			
	}
	
	@Override public void onDestroyView() {
	    super.onDestroyView();
	    
	    //setListAdapter( null );
	    
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		getActivity().getContentResolver().unregisterContentObserver( mediaStoreChanged );
		
	}
	
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		String playlistName = mActivity.getSupportActionBar().getTitle().toString();
		
		mActivity.mBoundService.setPlaylist( adapter.getQuery(), playlistName, GenresOneFragment.class, GENRE_ID );
		
		mActivity.mBoundService.setPlaylistPosition( position - l.getHeaderViewsCount() );
		
		mActivity.mBoundService.play();
		
		mTracker.send( new HitBuilders.EventBuilder()
    	.setCategory( Categories.PLAYLIST )
    	.setAction( Playlist.ACTION_CLICK )
    	.setValue( position )
    	.build());
			
	}
	
	protected AdapterView.OnItemLongClickListener mSongMenuLongClickListener = new AdapterView.OnItemLongClickListener() {

		@Override public boolean onItemLongClick( AdapterView<?> arg0, View v, int position, long id ) {
			
			showSongMenuDialog( "" + id );
			
			mTracker.send( new HitBuilders.EventBuilder()
	    	.setCategory( Categories.PLAYLIST )
	    	.setAction( Playlist.ACTION_LONGCLICK )
	    	.setValue( position )
	    	.build());
			
			return true;
			
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
				
				showSongMenuDialog( songID );
				
			}
			
			
			
		}
		
	};
	
	protected void showSongMenuDialog( String songID ) {
		
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
    	
		SongMenuDialogFragment newFragment = new SongMenuDialogFragment();
		newFragment.setMediaID( songID );
    	
        newFragment.show( ft, "dialog" );
		
	}
	
	ContentObserver mediaStoreChanged = new ContentObserver(new Handler()) {

        @Override public void onChange( boolean selfChange ) {
            
            mActivity.runOnUiThread( new Runnable() {

				@Override public void run() {
					
					saveScrollPosition();
					adapter.requery();
				
				}
            	
            });
            
            super.onChange( selfChange );
            
        }

	};

}