package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.ArtistSinglesAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.dialog.SongMenuDialogFragment;
import com.ideabag.playtunes.media.PlaylistMediaPlayer.PlaybackListener;
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
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ToggleButton;

public class ArtistSinglesFragment extends SaveScrollListFragment implements IMusicBrowser {
	
	public static final String TAG = "Artist Singles Fragment";
	
	private static final char DASH_SYMBOL = 0x2013;
	
	ArtistSinglesAdapter adapter;
	
	private MainActivity mActivity;
	private Tracker mTracker;
	
	private String ARTIST_ID = "";
	
	@Override public void setMediaID( String media_id ) {
		
		ARTIST_ID = media_id;
		
	}
	
	@Override public String getMediaID() { return ARTIST_ID; }
	
	@Override public void onAttach( Activity activity ) {
			
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		mTracker = TrackerSingleton.getDefaultTracker( mActivity );
		mTracker.setScreenName( TAG );
		
		//mActivity.setActionbarTitle( adapter.ARTIST_NAME );
		MediaQuery mGetArtistName = new MediaQuery(
				MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
				new String[] {
				    	
				    	MediaStore.Audio.Artists.ARTIST,
						MediaStore.Audio.Artists._ID
					
				},
				MediaStore.Audio.Artists._ID + " =?",
				new String[] {
					
						ARTIST_ID
						
				},
				null
			);
    	
		MediaQuery.executeAsync( getActivity(), mGetArtistName, new MediaQuery.OnQueryCompletedListener() {
			
			@Override public void onQueryCompleted(MediaQuery mQuery, Cursor mResult) {
				
				if ( mResult != null && mResult.getCount() > 0 ) {
					
					mResult.moveToFirst();
					
					try {
						
						mActivity.setActionbarTitle( mResult.getString( mResult.getColumnIndexOrThrow( MediaStore.Audio.Artists.ARTIST ) ) );
						//mActivity.setActionbarSubtitle( mResult.getCount() + " " + ( mResult.getCount() == 1 ? getString( R.string.song_singular) : getString( R.string.songs_plural) ) );
								
					} catch( Exception e ) {
						
						mActivity.setActionbarTitle( null );
						//mActivity.setActionbarSubtitle( null );
						
					}
					
					restoreScrollPosition();
					
				}
				
				if ( mResult != null && !mResult.isClosed() ) {
					
					mResult.close();
					
				}
				
			}
			
		});
	}
	
	@Override public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		outState.putString( getString( R.string.key_state_media_id ), ARTIST_ID );
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		if ( null != savedInstanceState ) {
			
			ARTIST_ID = savedInstanceState.getString( getString( R.string.key_state_media_id ) );
			
		}
		
    	adapter = new ArtistSinglesAdapter( getActivity(), ARTIST_ID, songMenuClickListener, new MediaQuery.OnQueryCompletedListener() {
			
			@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
				
				mActivity.setActionbarSubtitle( getString( R.string.artist_singles )
		    			+ " "
		    			+ Character.toString( DASH_SYMBOL )
		    			+ " "
		    			+ mResult.getCount()
		    			+ " "
		    			+ (mResult.getCount() == 1 ? getString( R.string.song_singular ) : getString( R.string.songs_plural ) ) );
				
				mTracker.send( new HitBuilders.EventBuilder()
		    	.setCategory( Categories.PLAYLIST )
		    	.setAction( Playlist.ACTION_SHOWLIST )
		    	.setValue( mResult.getCount() )
		    	.build());
		    	
			}
			
		});
    	adapter.setNowPlayingMedia( mActivity.mBoundService.CURRENT_MEDIA_ID );
    	
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
		getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setOnItemLongClickListener( mSongMenuLongClickListener );
		
    	setListAdapter( adapter );
		
    	getActivity().getContentResolver().registerContentObserver(
    			MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mediaStoreChanged );
    	
    	
	}
	
	@Override public void onResume() {
		super.onResume();
		
		// Send a screen view.
		mTracker.send( new HitBuilders.AppViewBuilder().build() );
		mActivity.addPlaybackListener( mPlaybackListener );
		
	}
		
	@Override public void onPause() {
		super.onPause();
		
		mActivity.removePlaybackListener( mPlaybackListener );
		
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
		
		mActivity.mBoundService.setPlaylist( adapter.getQuery(), playlistName, ArtistSinglesFragment.class, ARTIST_ID );
		
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

	private PlaybackListener mPlaybackListener = new PlaybackListener() {

		@Override public void onTrackChanged( String media_id ) {
			
			adapter.setNowPlayingMedia( media_id );
			
		}

		@Override public void onPlaylistDone() {
			
			adapter.setNowPlayingMedia( null );
			
		}
		
		@Override public void onPlay() {  }
		@Override public void onPause() {  }
		@Override public void onLoopingChanged(int loop) {  }
		@Override public void onShuffleChanged(boolean isShuffling) {  }
		@Override public void onDurationChanged( int position, int duration ) {  }
	};
	
}