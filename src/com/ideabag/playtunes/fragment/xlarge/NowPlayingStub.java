package com.ideabag.playtunes.fragment.xlarge;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.dialog.SongMenuDialogFragment;
import com.ideabag.playtunes.media.PlaylistMediaPlayer.PlaybackListener;
import com.ideabag.playtunes.util.AsyncDrawable;
import com.ideabag.playtunes.util.BitmapWorkerTask;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class NowPlayingStub extends Fragment implements View.OnClickListener {
	
	private MainActivity mActivity;
	
	private String songID;
	
	TextView mTitle;
	TextView mSubtitle;
	ImageView mCompactAlbumArt;
	
	String lastAlbumUri;
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
	
	@Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		
		return inflater.inflate( R.layout.fragment_nowplaying_stub, container, false );
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		getView().findViewById( R.id.MenuButton ).setOnClickListener( this );
		getView().findViewById( R.id.StarButton ).setOnClickListener( this );
		
		mTitle = ( TextView ) getView().findViewById( R.id.ControlsTitle );
		mSubtitle = ( TextView ) getView().findViewById( R.id.ControlsSubtitle );
		mCompactAlbumArt = ( ImageView ) getView().findViewById( R.id.CompactAlbumArt );
	}
	
	@Override public void onResume() {
		super.onResume();
		
		mActivity.addPlaybackListener( mPlaybackListener );
		
	}
	
	@Override public void onPause() {
		super.onStart();
		
		mActivity.removePlaybackListener( mPlaybackListener );
		
	}

	@Override public void onClick( View v ) {
		
		int id = v.getId();
		
		if ( id == R.id.MenuButton ) {
			
			FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	    	
			SongMenuDialogFragment newFragment = new SongMenuDialogFragment();
			newFragment.setMediaID( songID );
	    	
	        newFragment.show( ft, "dialog" );
			
		} else if ( id == R.id.StarButton ) {
			
			
			
		}
			 
		
	}
	PlaybackListener mPlaybackListener = new PlaybackListener() {
		
		@Override public void onTrackChanged( String media_id ) {
			
			if ( media_id == null ) {
				
				getView().setVisibility( View.GONE );
				songID = null;
				
			} else {
				
				getView().setVisibility( View.VISIBLE );

				songID = media_id;
				
	    		MediaQuery mSongQuery = new MediaQuery(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						new String[] {
							
							MediaStore.Audio.Media.ALBUM,
							MediaStore.Audio.Media.ARTIST,
							MediaStore.Audio.Media.TITLE,
							MediaStore.Audio.Media.ALBUM_ID,
							MediaStore.Audio.Media.DURATION,
							MediaStore.Audio.Media._ID
							
						},
						MediaStore.Audio.Media._ID + "=?",
						new String[] {
							
								media_id
							
						},
						null
					);
				
	    		MediaQuery.executeAsync( mActivity, mSongQuery, new MediaQuery.OnQueryCompletedListener() {
					
					@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
						

						mResult.moveToFirst();
						
						String title = mResult.getString( mResult.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
						String artist = mResult.getString( mResult.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
						
						String album_id = mResult.getString( mResult.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM_ID ) );
						mResult.close();
						
						// 
						// This tests if we loaded previous album art and that it wasn't null
						// If the nextAlbumUri is null, it means there's no album art and 
						// we load from an image resource.
						// 
						
						
						
						MediaQuery albumQuery = new MediaQuery(
								MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
							    new String[] {
							    	
							    	MediaStore.Audio.Albums.ALBUM_ART,
							    	MediaStore.Audio.Albums._ID
							    	
							    },
							    MediaStore.Audio.Albums._ID + "=?",
								new String[] {
									
									album_id
									
								},
								null
							);
						
						MediaQuery.executeAsync(getActivity(), albumQuery, new MediaQuery.OnQueryCompletedListener() {
							
							@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
								
								mResult.moveToFirst();
								
								String nextAlbumUri = mResult.getString( mResult.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
								
								if ( null != nextAlbumUri) {
									
									if ( !nextAlbumUri.equals( lastAlbumUri ) ) {
										
										lastAlbumUri = nextAlbumUri;
										
										final BitmapWorkerTask albumThumbTask = new BitmapWorkerTask( mCompactAlbumArt, getResources().getDimensionPixelSize( R.dimen.footer_height ) );
										
										final AsyncDrawable asyncThumbDrawable =
								                new AsyncDrawable( getResources(),
								                		null, // BitmapFactory.decodeResource( mContext.getResources(), R.drawable.no_album_art_thumb )
								                		albumThumbTask );
								        
										mCompactAlbumArt.setImageDrawable( asyncThumbDrawable );
								        albumThumbTask.execute( nextAlbumUri );
								        
									}
							        
								} else {
									
									mCompactAlbumArt.setImageResource( R.drawable.no_album_art_thumb );
									
								}
								
							}
							
						});
				        
						
						mTitle.setText( title );
						mSubtitle.setText( artist );
						
					}
					
				}); // End of song query async callback
	    		
			}
			
		}
		
		@Override public void onPlay() { }
		@Override public void onPause() { }
		@Override public void onPlaylistDone() {
			
			getView().setVisibility( View.GONE );
			
		}
		@Override public void onLoopingChanged( int loop ) { }
		@Override public void onShuffleChanged( boolean isShuffling ) { }
		@Override public void onDurationChanged( int position, int duration ) { }
		
	};
	
}
