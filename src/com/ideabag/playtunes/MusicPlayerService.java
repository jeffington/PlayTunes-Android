package com.ideabag.playtunes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.media.AudioFocusHelper;
import com.ideabag.playtunes.media.MediaButtonHelper;
import com.ideabag.playtunes.media.MusicFocusable;
import com.ideabag.playtunes.media.MusicIntentReceiver;
import com.ideabag.playtunes.media.PlaylistMediaPlayer;
import com.ideabag.playtunes.media.RemoteControlClientCompat;
import com.ideabag.playtunes.media.RemoteControlHelper;
import com.ideabag.playtunes.media.PlaylistMediaPlayer.LoopState;
import com.ideabag.playtunes.media.PlaylistMediaPlayer.PlaybackListener;
import com.ideabag.playtunes.util.TrackerSingleton;


public class MusicPlayerService extends Service implements MusicFocusable {
	
	public static final String ACTION_PLAY = "com.ideabag.playtunes.PLAY";
	public static final String ACTION_PLAY_OR_PAUSE = "com.ideabag.playtunes.PLAY_PAUSE";
	public static final String ACTION_PAUSE = "com.ideabag.playtunes.PAUSE";
	public static final String ACTION_NEXT = "com.ideabag.playtunes.NEXT";
	public static final String ACTION_BACK = "com.ideabag.playtunes.BACK";
	public static final String ACTION_CLOSE = "com.ideabag.playtunes.CLOSE";
	
	private static final String TAG = "MusicPlayerService";
	
	private PlaylistMediaPlayer MediaPlayer;
	private PlaybackNotification Notification;
	
	// our RemoteControlClient object, which will use remote control APIs available in
    // SDK level >= 14, if they're available.
    RemoteControlClientCompat mRemoteControlClientCompat;

    // Dummy album art we will pass to the remote control (if the APIs are available).
    Bitmap mDummyAlbumArt;

    // The component name of MusicIntentReceiver, for use with media button and remote control
    // APIs
    ComponentName mMediaButtonReceiverComponent;
    
    AudioManager mAudioManager;
    
    AudioFocusHelper mAudioFocusHelper = null;
	
	public String CURRENT_MEDIA_ID = null;
	public Class < ? extends Fragment > mPlaylistFragmentClass;
	public String mPlaylistMediaID;
	public String mPlaylistName;
	private Bitmap mAlbumArtBitmap = null;
	
	private MusicPlayerService self;
	
	private ArrayList< PlaybackListener > ChangedListeners = new ArrayList< PlaybackListener >();

	BroadcastReceiver NotificationActionReceiver = new BroadcastReceiver() {

		@Override public void onReceive( Context context, Intent intent ) {
			
			Tracker tracker = TrackerSingleton.getDefaultTracker( context );
			
			String action = intent.getAction();
			android.util.Log.i( "MusicPlayerService", "Intent received with action " + action );
			
			if ( action.equals( ACTION_PLAY_OR_PAUSE ) ) {
				
				if ( null != MediaPlayer ) {
					
					if ( MediaPlayer.isPlaying() ) {
						
						tracker.send( new HitBuilders.EventBuilder()
			        	.setCategory( "notification button" )
			        	.setAction( "click" )
			        	.setLabel( "pause" )
			        	.build());
						
						pause();
						
					} else {
						
						tracker.send( new HitBuilders.EventBuilder()
			        	.setCategory( "notification button" )
			        	.setAction( "click" )
			        	.setLabel( "play" )
			        	.build());
						
						play();
						
					}
					
				}
				
				
			} else if ( action.equals( ACTION_NEXT ) ) {
			
				tracker.send( new HitBuilders.EventBuilder()
	        	.setCategory( "notification button" )
	        	.setAction( "click" )
	        	.setLabel( "next" )
	        	.build());
				
				next();
				
			} else if ( action.equals( ACTION_CLOSE ) ) {
				
				tracker.send( new HitBuilders.EventBuilder()
	        	.setCategory( "notification button" )
	        	.setAction( "click" )
	        	.setLabel( "close" )
	        	.build());
				
				 
				self.stopSelf();
				
			} else if ( action.equals( ACTION_BACK ) ) {
				
				tracker.send( new HitBuilders.EventBuilder()
	        	.setCategory( "notification button" )
	        	.setAction( "click" )
	        	.setLabel( "back" )
	        	.build());
				
				MediaPlayer.back();
				
			}
			
		}
		
	};
	
	@SuppressLint("NewApi")
	@Override public void onCreate() {
		super.onCreate();
		
		self = this;
		
		MediaPlayer = new PlaylistMediaPlayer( getBaseContext() );
		
		Notification = new PlaybackNotification( getBaseContext() );
		
		mAudioManager = ( AudioManager ) getSystemService( AUDIO_SERVICE );
		
		IntentFilter mNotificationIntentFilter = new IntentFilter();
		mNotificationIntentFilter.addAction( ACTION_PLAY_OR_PAUSE );
		mNotificationIntentFilter.addAction( ACTION_NEXT );
		mNotificationIntentFilter.addAction( ACTION_CLOSE );
		mNotificationIntentFilter.addAction( ACTION_BACK );
		
		
		MediaPlayer.setPlaybackListener( MediaPlayerListener );
		
		
		registerReceiver( NotificationActionReceiver, mNotificationIntentFilter );
		
		startService( new Intent( this, MusicPlayerService.class ) );
		
		mAudioFocusHelper = new AudioFocusHelper( getApplicationContext(), this );
		
		if ( android.os.Build.VERSION.SDK_INT >= 14 ) {
			
			mMediaButtonReceiverComponent = new ComponentName( this, MusicIntentReceiver.class );
			MediaButtonHelper.registerMediaButtonEventReceiverCompat( mAudioManager, mMediaButtonReceiverComponent );
			
	        if ( mRemoteControlClientCompat == null ) {
	        	
	            Intent intent = new Intent( Intent.ACTION_MEDIA_BUTTON );
	            //intent.setAction(  );
	            intent.setComponent( mMediaButtonReceiverComponent );
	            mRemoteControlClientCompat = new RemoteControlClientCompat(
	                    PendingIntent.getBroadcast(this /*context*/,
	                            0 /*requestCode, ignored*/, intent /*intent*/, 0 /*flags*/));
	            
	            RemoteControlHelper.registerRemoteControlClient( mAudioManager, mRemoteControlClientCompat );
	            
	        }
	        
		}
		
	}
	
	
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		mAudioFocusHelper.abandonFocus();
		
		unregisterReceiver( NotificationActionReceiver );
		
		ChangedListeners.clear();
		
		if ( null != mRemoteControlClientCompat ) {
			
			RemoteControlHelper.unregisterRemoteControlClient( mAudioManager, mRemoteControlClientCompat );
			MediaButtonHelper.unregisterMediaButtonEventReceiverCompat( mAudioManager, mMediaButtonReceiverComponent );
			
		}
		
		MediaPlayer.pause();
		MediaPlayer.destroy();
		
		Notification.remove();
		
	}
	 
	public class MusicPlayerServiceBinder extends Binder {
        
		public MusicPlayerService getService() {
			
        	return MusicPlayerService.this;
            
        }
        
	}
	
	
	// 
	// Service Binder & Public APIs
	// 
	// 
	// 
	private final IBinder mBinder = new MusicPlayerServiceBinder();
	
	@Override public IBinder onBind( Intent intent ) {
		
		return mBinder;
	}
	
	public void setPlaylist( Cursor c, String playlistName, Class < ? extends Fragment > fragmentClass, String playlistMediaID ) {
		
		if ( null != MediaPlayer ) {
			
			MediaPlayer.setPlaylistCursor( c );
			this.mPlaylistFragmentClass = fragmentClass;
			this.mPlaylistMediaID = playlistMediaID;
			this.mPlaylistName = playlistName;
			
		}
		
	}
	
	
	public void setPlaylistPosition( int position ) {
		
		if ( null != MediaPlayer ) {
			
			android.util.Log.i("playlist position", "" + position );
			
			MediaPlayer.setPlaylistPosition( position );
			
		}
		
	}
	
	public void setSeekPosition( int position ) {
		
		if ( null != MediaPlayer ) {
			
			
			MediaPlayer.setSeekPosition( position );
			
		}
		
	}
	
	public void next() {
		
		if ( null != MediaPlayer ) {
			
			MediaPlayer.nextTrack();
			
		}
		
	}
	
	public void prev() {
		
		if ( null != MediaPlayer ) {
			
			MediaPlayer.back();
			
		}
		
	}
	
	public void play() {
		
		if ( null != MediaPlayer && !MediaPlayer.isPlaying() ) {
			
			MediaPlayer.play();
			
			if ( mRemoteControlClientCompat != null ) {
	            
				mRemoteControlClientCompat.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
	            
	        }
			
		}
		
	}
	
	public void pause() {
		
		if ( null != MediaPlayer ) {
			
			MediaPlayer.pause();
			
		}
		
	}
	
	public void setLooping( PlaylistMediaPlayer.LoopState repeat ) {
		
		MediaPlayer.setLooping( repeat );
		
	}
	
	public void setShuffle( boolean isShuffling ) {
		
		MediaPlayer.setShuffle( isShuffling );
		
	}
	
	private void updateRemoteControlClientPause() {
		
		if ( null != mRemoteControlClientCompat ) {
			
			mRemoteControlClientCompat.setPlaybackState( RemoteControlClient.PLAYSTATE_PAUSED );
			
		}
		
	}
	
	private void updateRemoteControlClientPlay() {
		
		if ( null != mRemoteControlClientCompat ) {
			
			mRemoteControlClientCompat.setPlaybackState( RemoteControlClient.PLAYSTATE_PLAYING );
			
		}
		
	}
	
	@SuppressLint("InlinedApi")
	private void updateRemoteControlClientMedia( String media_id ) {
		
		if ( null != mRemoteControlClientCompat ) {
				
			
			Cursor mSongCursor = getContentResolver().query(
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
			
			mSongCursor.moveToFirst();
			
			String mSongTitle = mSongCursor.getString( mSongCursor.getColumnIndex(MediaStore.Audio.Media.TITLE ) );
			String mSongAlbum = mSongCursor.getString( mSongCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM ) );
			String mSongArtist = mSongCursor.getString( mSongCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST ) );
			long mSongDuration = mSongCursor.getLong( mSongCursor.getColumnIndex( MediaStore.Audio.Media.DURATION ) );
			String album_id = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM_ID ) );
			//Bitmap mAlbumArt = BitmapFactory.
			
			mSongCursor.close();
			
			
			
			
			Cursor albumCursor = getContentResolver().query(
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
			
			albumCursor.moveToFirst();
			
			String newAlbumUri = albumCursor.getString( albumCursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
			albumCursor.close();
			
			
			if ( null != mAlbumArtBitmap ) {
				
				mAlbumArtBitmap.recycle();
				
			}
			
			
			try {
				
				if ( null == newAlbumUri ) {
					
					mAlbumArtBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_album_art );
					
				} else {
					
					Uri imageUri = Uri.parse( newAlbumUri );
					
					mAlbumArtBitmap = BitmapFactory.decodeFile( newAlbumUri );
					
				}
				//albumArtBitmap = BitmapFactory.decodeStream( getContentResolver().openInputStream( imageUri ) );
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			mAudioFocusHelper.requestFocus();
			
	        mRemoteControlClientCompat.setPlaybackState( RemoteControlClient.PLAYSTATE_PLAYING );
	        
	        mRemoteControlClientCompat.setTransportControlFlags(
	                RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
	                RemoteControlClient.FLAG_KEY_MEDIA_NEXT |
	                RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
	                RemoteControlClient.FLAG_KEY_MEDIA_STOP);
	
	        // Update the remote controls
	        
	        mRemoteControlClientCompat.editMetadata( true )
	                .putString( MediaMetadataRetriever.METADATA_KEY_ARTIST, mSongArtist )
	                .putString( MediaMetadataRetriever.METADATA_KEY_ALBUM, mSongAlbum )
	                .putString( MediaMetadataRetriever.METADATA_KEY_TITLE, mSongTitle )
	                .putLong( MediaMetadataRetriever.METADATA_KEY_DURATION, mSongDuration )
	                // TODO: fetch real item artwork
	                .putBitmap( RemoteControlClientCompat.MetadataEditorCompat.METADATA_KEY_ARTWORK, mAlbumArtBitmap )
	                .apply();
	        
		}
		
	}
	
	// 
	// The PlaylistMediaPlayer creates a PlaybackListener interface and uses it as a callback for changes in playback.
	// In MusicPlayerService, we use that interface both to update the notification and to rebroadcast that
	// callback information to as many clients as are interested.
	// 
	// We use the number of PlaybackListeners as a way to determine if the Service is detached or not.
	//
	// 
	
	public void addPlaybackListener( PlaybackListener listener ) {
		
		this.ChangedListeners.add( listener );
		
		// When we get a PlaybackListener, we immediately fire all the callbacks with the current state information
		
		if ( null != MediaPlayer ) {
			
			listener.onTrackChanged( MediaPlayer.getCurrentMediaID() );
			listener.onLoopingChanged( MediaPlayer.getLoopState() );
			listener.onShuffleChanged( MediaPlayer.isShuffling() );
			
			if ( MediaPlayer.isPlaying() ) {
				
				listener.onPlay( MediaPlayer.getTrackPlaybackPosition() );
				
			} else {
				
				listener.onPause( MediaPlayer.getTrackPlaybackPosition() );
				
			}
			
		}
		
		//
		// Listeners are added when an interested Activity UI is shown, so we remove
		// the notification in this case.
		//
		
		if ( this.ChangedListeners.size() > 0 ) {
			
			Notification.remove();
			
		}
		
	}
	
	public void removePlaybackListener( PlaybackListener listener ) {
		
		this.ChangedListeners.remove( listener );
		
		//
		// Listeners are removed when an interested Activity UI is hidden, so we either
		// show the notification in this case and update it with the currently playing song
		// or destroy the service if no music is playing.
		// 
		
		if ( this.ChangedListeners.size() == 0 ) {
			
			if ( MediaPlayer.isPlaying()  ) {
				
				Notification.showSong( MediaPlayer.getCurrentMediaID() );
				Notification.showPlaying();
				
			} else {
				
				this.stopSelf();
				
			}
			
		}
		
	}
	
	PlaybackListener MediaPlayerListener = new PlaybackListener() {
		
		@Override public void onTrackChanged( String media_id ) {
			
			CURRENT_MEDIA_ID = media_id; // Set even if media_id is null
			
			int count = ChangedListeners.size();
			
			for ( int x = 0; x < count; x++ ) {
				
				ChangedListeners.get( x ).onTrackChanged( media_id );
				
			}
			
			updateRemoteControlClientMedia( media_id );
			
			if ( 0 == count ) {
				
				Notification.showSong( media_id );
				
			}
			
		}


		@Override public void onPlaylistDone() {
			
			int count = ChangedListeners.size();
			
			for ( int x = 0; x < count; x++ ) {
				
				ChangedListeners.get( x ).onPlaylistDone();
				
			}
			
			//if ( android.os.Build.VERSION.SDK_INT >= 14 ) {
				
				//updateRemoteControlClient( media_id );
				
			//}
			
			if ( 0 == count ) {
				
				Notification.remove();
				
			}
			
		}

		@Override public void onLoopingChanged( LoopState loop ) {
			
			int count = ChangedListeners.size();
			
			for ( int x = 0; x < count; x++ ) {
				
				ChangedListeners.get( x ).onLoopingChanged( loop );
				
			}
			
			
			
		}

		@Override public void onShuffleChanged( boolean isShuffling ) {
			
			int count = ChangedListeners.size();
			
			for ( int x = 0; x < count; x++ ) {
				
				ChangedListeners.get( x ).onShuffleChanged( isShuffling );
				
			}
			
		}


		@Override public void onPlay(int playbackPositionMilliseconds ) {
			
			int count = ChangedListeners.size();
			
			for ( int x = 0; x < count; x++ ) {
				
				ChangedListeners.get( x ).onPlay( playbackPositionMilliseconds );
				
			}
			
			updateRemoteControlClientPlay();
			
			if ( count == 0 ) {
				
				Notification.showPlaying();
				
			}
			
		}


		@Override public void onPause( int playbackPositionMilliseconds ) {
			
			int count = ChangedListeners.size();
			
			for ( int x = 0; x < count; x++ ) {
				
				ChangedListeners.get( x ).onPause( playbackPositionMilliseconds );
				
			}
			
			updateRemoteControlClientPause();
			
			if ( count == 0 ) {
				
				Notification.showPaused();
				
			}
			
		}
		
	};

	@Override public void onGainedAudioFocus() {
		// TODO Auto-generated method stub
		
	}
	


	@Override
	public void onLostAudioFocus(boolean canDuck) {
		
		pause();
		
	}
	
}