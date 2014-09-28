package com.ideabag.playtunes.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.AlbumsOneAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.dialog.SongMenuDialogFragment;
import com.ideabag.playtunes.util.AsyncDrawable;
import com.ideabag.playtunes.util.BitmapWorkerTask;
import com.ideabag.playtunes.util.GAEvent;
import com.ideabag.playtunes.util.IMusicBrowser;
import com.ideabag.playtunes.util.IPlayableList;
import com.ideabag.playtunes.util.TrackerSingleton;

public class AlbumsOneFragment extends SaveScrollListFragment implements IMusicBrowser, IPlayableList {
	
	public static final String TAG = "One Album Fragment";
	
	AlbumsOneAdapter adapter;
	private MainActivity mActivity;
	private Tracker mTracker;
	private ContentResolver mResolver;
	
	private String ALBUM_ID = "";
	
	private View albumArtHeader;
	private ImageView mAlbumArt;
	private ImageView mAlbumArtBackground;
	private TextView mAlbumTitle;
	private TextView mAlbumSubtitle;
	
	@Override public void setMediaID( String media_id ) {
		
		ALBUM_ID = media_id;
		
	}
	
	@Override public String getMediaID() { return ALBUM_ID; }
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		mTracker = TrackerSingleton.getDefaultTracker( mActivity );
		mResolver = activity.getContentResolver();
		
		
		mTracker.setScreenName( TAG );
		
		albumArtHeader = getActivity().getLayoutInflater().inflate( R.layout.list_header_albumart, null, false );
		mAlbumTitle = ( TextView ) albumArtHeader.findViewById( R.id.AlbumArtTitle );
		mAlbumSubtitle = ( TextView ) albumArtHeader.findViewById( R.id.AlbumArtSubtitle );
		
		mAlbumArt = ( ImageView ) albumArtHeader.findViewById( R.id.AlbumArtFull );
		
		mAlbumArtBackground = ( ImageView ) albumArtHeader.findViewById( R.id.AlbumArtBackground );
		
    	MediaQuery albumQuery = new MediaQuery(
    			MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
    			new String[] {
    				
    				MediaStore.Audio.Albums.ALBUM,
    				MediaStore.Audio.Albums.ALBUM_ART,
    				MediaStore.Audio.Albums.ARTIST,
    				MediaStore.Audio.Albums._ID
    				
    			},
    			MediaStore.Audio.Albums._ID + "=?",
				new String[] {
    				
    				ALBUM_ID
    				
    			},
    			null );
    	
    	MediaQuery.executeAsync( getActivity(), albumQuery, new MediaQuery.OnQueryCompletedListener() {
			
			@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
				
				if ( null != mResult && mResult.getCount() > 0 ) {
					
					mResult.moveToFirst();
			    	
					String albumTitle, albumArtist;
			    	
			    	String albumUriString = mResult.getString( mResult.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
			    	
			    	albumTitle = mResult.getString( mResult.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM ) );
			    	albumArtist = mResult.getString( mResult.getColumnIndex( MediaStore.Audio.Albums.ARTIST ) );
			    	
			    	
			    	mActivity.setActionbarTitle( albumTitle );
					
					mAlbumTitle.setText( albumTitle );
					mAlbumSubtitle.setText( albumArtist );
					
					if ( null != albumUriString) {
						
						final BitmapWorkerTask albumThumbTask = new BitmapWorkerTask( mAlbumArt );
						final BitmapWorkerTask albumFullTask = new BitmapWorkerTask( mAlbumArtBackground );
				        final AsyncDrawable asyncThumbDrawable =
				                new AsyncDrawable( getResources(),
				                		null, // BitmapFactory.decodeResource( mContext.getResources(), R.drawable.no_album_art_thumb )
				                		albumThumbTask );
				        final AsyncDrawable asyncFullDrawable =
				                new AsyncDrawable( getResources(),
				                		null, // BitmapFactory.decodeResource( mContext.getResources(), R.drawable.no_album_art_thumb )
				                		albumFullTask );
				        
				        mAlbumArt.setImageDrawable( asyncThumbDrawable );
				        albumThumbTask.execute( albumUriString );
				        
				        mAlbumArtBackground.setImageDrawable( asyncFullDrawable );
				        albumFullTask.execute( albumUriString );
				        
				        
				        
					} else {
						
						mAlbumArtBackground.setImageResource( R.drawable.no_album_art_full );
						mAlbumArt.setImageResource( R.drawable.no_album_art_thumb );
						
					}
			        
				}
				
				if ( null != mResult && !mResult.isClosed() ) {
					
			        mResult.close();
					
				}
					
			}
			
		});
		
	}

	@Override public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		outState.putString( getString( R.string.key_state_media_id ), ALBUM_ID );
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
    	
		if ( null != savedInstanceState ) {
			
			ALBUM_ID = savedInstanceState.getString( getString( R.string.key_state_media_id ) );
			
		}
		
		
		//getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
		getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
		
		adapter = new AlbumsOneAdapter( getActivity(), ALBUM_ID, songMenuClickListener, new MediaQuery.OnQueryCompletedListener() {
			
			@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
				
				mActivity.setActionbarSubtitle( mResult.getCount() + " " + ( mResult.getCount() == 1 ? getString( R.string.song_singular ) : getString( R.string.songs_plural ) ) );
				
				mTracker.send( new HitBuilders.EventBuilder()
		    	.setCategory( GAEvent.Categories.PLAYLIST )
		    	.setAction( GAEvent.Playlist.ACTION_SHOWLIST )
		    	.setValue( mResult.getCount() )
		    	.build());
				
			}
			
		});
		// TODO: A start at showing an indicator next to the song that's playing in the list.
		//adapter.setNowPlayingMedia( mActivity.mBoundService.CURRENT_MEDIA_ID );
		
		getListView().addHeaderView( albumArtHeader, null, false );
		getListView().setOnItemLongClickListener( mSongMenuLongClickListener );
		
		mAlbumArtBackground.setColorFilter( getResources().getColor( R.color.now_playing_background ), PorterDuff.Mode.MULTIPLY );
		
    	setListAdapter( adapter );
    	
    	mResolver.registerContentObserver(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mediaStoreChanged );
    	mResolver.registerContentObserver(
				MediaStore.Audio.Playlists.Members.getContentUri( "external", Long.parseLong( new PlaylistManager( getActivity() ).createStarredIfNotExist() ) ), true, mediaStoreChanged );
    	
		
	}
	
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		String playlistName = mActivity.getSupportActionBar().getTitle().toString();
		
		mActivity.mBoundService.setPlaylist( adapter.getQuery(), playlistName, AlbumsOneFragment.class, ALBUM_ID );
		mActivity.mBoundService.setPlaylistPosition( position - l.getHeaderViewsCount() );
		
		mActivity.mBoundService.play();
		
		mTracker.send( new HitBuilders.EventBuilder()
    	.setCategory( GAEvent.Categories.PLAYLIST )
    	.setAction( GAEvent.Playlist.ACTION_CLICK )
    	.setValue( position )
    	.build());
		
	}
	
	
	
	@Override public void onResume() {
		super.onResume();
		
	        // Set screen name.
	        // Where path is a String representing the screen name.

		//t.set( "_count", ""+adapter.getCount() );
		
	        // Send a screen view.
		mTracker.send( new HitBuilders.AppViewBuilder().build() );
		
		
	}
	
	@Override public void onPause() {
		super.onPause();
		
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		mResolver.unregisterContentObserver( mediaStoreChanged );
		
	}
	
	@Override public void onDestroyView() {
	    super.onDestroyView();
	    /*
	    if ( null != mAlbumArt ) {
		    
		    BitmapDrawable mAlbumArtDrawable = ( BitmapDrawable ) mAlbumArt.getDrawable();
			
			if ( null != mAlbumArtDrawable ) {
				
				mAlbumArtDrawable.getBitmap().recycle();
				mAlbumArt.setImageBitmap( null );
				
			}
			
	    }
	    
	    if ( null != mAlbumArtBackground ) {
		    
		    BitmapDrawable mAlbumArtBackgroundDrawable = ( BitmapDrawable ) mAlbumArtBackground.getDrawable();
			
			if ( null != mAlbumArtBackgroundDrawable ) {
				
				mAlbumArtBackgroundDrawable.getBitmap().recycle();
				mAlbumArtBackground.setImageBitmap( null );
				
			}
			
	    }
	    */
	    setListAdapter( null );
	    
	}
	
	protected AdapterView.OnItemLongClickListener mSongMenuLongClickListener = new AdapterView.OnItemLongClickListener() {

		@Override public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long id) {
			
			
			showSongMenuDialog( "" + id );
			
			mTracker.send( new HitBuilders.EventBuilder()
	    	.setCategory( GAEvent.Categories.PLAYLIST )
	    	.setAction( GAEvent.Playlist.ACTION_LONGCLICK )
	    	.setValue( position )
	    	.build());
			
			return true;
			
		}
		
	};
	
	protected View.OnClickListener songMenuClickListener = new View.OnClickListener() {
		
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
					
					adapter.requery();
					
				}
            	
            });
            
            super.onChange( selfChange );
            
        }

	};

	@Override public void onNowPlayingMediaChanged( String media_id ) {
		
		adapter.setNowPlayingMedia( media_id );
		
	}
	
}