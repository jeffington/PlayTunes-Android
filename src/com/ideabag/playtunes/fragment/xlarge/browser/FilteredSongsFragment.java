package com.ideabag.playtunes.fragment.xlarge.browser;

import com.google.android.gms.analytics.HitBuilders;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.AlbumsOneAdapter;
import com.ideabag.playtunes.adapter.ArtistAllSongsAdapter;
import com.ideabag.playtunes.adapter.SongListAdapter;
import com.ideabag.playtunes.adapter.SongsAllAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.fragment.AlbumsOneFragment;
import com.ideabag.playtunes.fragment.ArtistsOneFragment;
import com.ideabag.playtunes.fragment.SaveScrollListFragment;
import com.ideabag.playtunes.fragment.SongsFragment;
import com.ideabag.playtunes.media.PlaylistMediaPlayer.PlaybackListener;
import com.ideabag.playtunes.util.GAEvent.Categories;
import com.ideabag.playtunes.util.GAEvent.Playlist;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;

public class FilteredSongsFragment extends ListFragment implements OnItemClickListener {
	
	MainActivity mActivity;
	MediaQuery mQuery;
	
	SongListAdapter adapter;
	
	String artist_id;
	
	String album_id;
	
	public void setArtistID( String id ) {
		
		artist_id = id;
		
		if ( artist_id != null ) {
			
			adapter = new ArtistAllSongsAdapter( mActivity, artist_id, songMenuClickListener, new MediaQuery.OnQueryCompletedListener() {
				
				@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
					
					int count = mResult.getCount();
					
					mActivity.setActionbarSubtitle( count + " " + ( count == 1 ? getString( R.string.song_singular ) : getString( R.string.songs_plural ) ) );
					
				}
				
			});
			
			setListAdapter( adapter );
			
		}
		
	}
	
	public void setAlbumID( String id ) {
		
		album_id = id;
		
		if ( album_id != null ) {
			
			adapter = new AlbumsOneAdapter( mActivity, album_id, songMenuClickListener, new MediaQuery.OnQueryCompletedListener() {
				
				@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
					
					int count = mResult.getCount();
					
					mActivity.setActionbarSubtitle( count + " " + ( count == 1 ? getString( R.string.song_singular ) : getString( R.string.songs_plural ) ) );
					
				}
				
			});
			
			setListAdapter( adapter );
			
		}
		
	}
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		adapter = new SongsAllAdapter( mActivity, songMenuClickListener, new MediaQuery.OnQueryCompletedListener() {
			
			@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
				
				int count = mResult.getCount();
				
				mActivity.setActionbarSubtitle( count + " " + ( count == 1 ? getString( R.string.song_singular ) : getString( R.string.songs_plural ) ) );
				
			}
			
		});
		
    	getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
    	
    	getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
		
		setListAdapter( adapter );
		
		
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
				
			} else if ( viewID == R.id.SongArtistButton ) {
				
				String artistID = "" + v.getTag( R.id.tag_artist_id );
				
				ArtistsOneFragment mFragment = new ArtistsOneFragment();
				mFragment.setMediaID( artistID );
				
				mActivity.transactFragment( mFragment );
				
			} else if ( viewID == R.id.SongAlbumButton ) {
				
				String albumID = "" + v.getTag( R.id.tag_album_id );
				
				AlbumsOneFragment mFragment = new AlbumsOneFragment();
				mFragment.setMediaID( albumID );
				
				mActivity.transactFragment( mFragment );
				
			}
			
			
			
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

	@Override public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		
		String playlistName = mActivity.getSupportActionBar().getTitle().toString();
		
		mActivity.mBoundService.setPlaylist( adapter.getQuery(), playlistName, BrowseSongsFragment.class, null );
		//mActivity.mBoundService.setPlaylistCursor( adapter.getCursor() );
		
		mActivity.mBoundService.setPlaylistPosition( position );
		
		mActivity.mBoundService.play();
		/*
		mTracker.send( new HitBuilders.EventBuilder()
    	.setCategory( Categories.PLAYLIST )
    	.setAction( Playlist.ACTION_CLICK )
    	.setValue( position )
    	.build());
		*/
	}
	
}
