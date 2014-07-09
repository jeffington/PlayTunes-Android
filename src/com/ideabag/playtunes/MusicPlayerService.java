package com.ideabag.playtunes;

import java.util.ArrayList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.media.PlaylistMediaPlayer;
import com.ideabag.playtunes.media.PlaylistMediaPlayer.PlaybackListener;
import com.ideabag.playtunes.util.TrackerSingleton;


public class MusicPlayerService extends Service {
	
	public static final String ACTION_PLAY = "com.ideabag.playtunes.PLAY";
	public static final String ACTION_PLAY_OR_PAUSE = "com.ideabag.playtunes.PLAY_PAUSE";
	public static final String ACTION_PAUSE = "com.ideabag.playtunes.PAUSE";
	public static final String ACTION_NEXT = "com.ideabag.playtunes.NEXT";
	public static final String ACTION_CLOSE = "com.ideabag.playtunes.CLOSE";
	
	private static final String TAG = "PlayTunesMusicPlayerService";
	
	private PlaylistMediaPlayer MediaPlayer;
	private PlaybackNotification Notification;
	
	public String CURRENT_MEDIA_ID = null;
	public Class < ? extends Fragment > mPlaylistFragmentClass;
	public String mPlaylistMediaID;
	public String mPlaylistName;
	
	private MusicPlayerService self;
	

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
			        	.build()
								);
						
						pause();
						
					} else {
						
						tracker.send( new HitBuilders.EventBuilder()
			        	.setCategory( "notification button" )
			        	.setAction( "click" )
			        	.setLabel( "play" )
			        	.build()
								);
						
						play();
						
					}
					
				}
				
				
			} else if ( action.equals( ACTION_NEXT ) ) {
			
				tracker.send( new HitBuilders.EventBuilder()
	        	.setCategory( "notification button" )
	        	.setAction( "click" )
	        	.setLabel( "next" )
	        	.build()
						);
				
				next();
				
			} else if ( action.equals( ACTION_CLOSE ) ) {
				
				tracker.send( new HitBuilders.EventBuilder()
	        	.setCategory( "notification button" )
	        	.setAction( "click" )
	        	.setLabel( "close" )
	        	.build()
						);
				
				 
				self.stopSelf();
				
			}
			
		}
		
	};
	
	@Override public void onCreate() {
		super.onCreate();
		
		self = this;
		
		MediaPlayer = new PlaylistMediaPlayer( getBaseContext() );
		
		Notification = new PlaybackNotification( getBaseContext() );
		
		
		IntentFilter mNotificationIntentFilter = new IntentFilter();
		mNotificationIntentFilter.addAction( ACTION_PLAY_OR_PAUSE );
		mNotificationIntentFilter.addAction( ACTION_NEXT );
		mNotificationIntentFilter.addAction( ACTION_CLOSE );
		
		
		MediaPlayer.setPlaybackListener( MediaPlayerListener );
		
		android.util.Log.i(TAG, "About to register broadcast receiver." );
		
		registerReceiver( NotificationActionReceiver, mNotificationIntentFilter );
		
		startService( new Intent( this, MusicPlayerService.class ) );
		
		
	}
	
	
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver( NotificationActionReceiver );
		
		ChangedListeners.clear();
		
		MediaPlayer.pause();
		MediaPlayer.destroy();
		
		Notification.remove();
		
		Log.i(TAG, "Service destroyed.");
		
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
	
	private ArrayList< SongInfoChangedListener > ChangedListeners = new ArrayList< SongInfoChangedListener >();
	
	@Override public IBinder onBind( Intent intent ) {
		
		return mBinder;
	}
	
	public interface SongInfoChangedListener {
		
		public void songInfoChanged( String media_content_id );
		
		public void musicStarted( int position_milliseconds );
		
		public void musicPaused( int position_milliseconds );
		
		public void musicDone();
		
	}
	
	public void setPlaylistCursor( Cursor c ) {
		
		if ( null != MediaPlayer ) {
			
			MediaPlayer.setPlaylistCursor( c );
			
		}
		
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
			
			MediaPlayer.previousTrack();
			
		}
		
	}
	
	public void play() {
		
		if ( null != MediaPlayer && !MediaPlayer.isPlaying() ) {
			
			MediaPlayer.play();
			
		}
		
	}
	
	public void pause() {
		
		if ( null != MediaPlayer && MediaPlayer.isPlaying() ) {
			
			MediaPlayer.pause();
			
		}
		
	}
	
	public void setRepeat( PlaylistMediaPlayer.LoopState repeat ) {
		
		MediaPlayer.setLooping( repeat );
		
	}
	
	public void setShuffle( boolean isShuffling ) {
		
		MediaPlayer.setShuffle( isShuffling );
		
	}
	
	public void addOnSongInfoChangedListener( SongInfoChangedListener listener ) {
		
		this.ChangedListeners.add( listener );
		
		// When we get a SongInfoChangedListener, we immediately fire back with the current state data
		
		
		listener.songInfoChanged( MediaPlayer.getCurrentMediaID() );
		
		if ( null != MediaPlayer && MediaPlayer.isPlaying() ) {
			
			listener.musicStarted( MediaPlayer.getCurrentPosition() );
			
		} else {
			
			listener.musicPaused( MediaPlayer.getCurrentPosition() );
			
		}
		
		if ( this.ChangedListeners.size() > 0 ) {
			
			Notification.remove();
			
		}
		
	}
	
	public void removeOnSongInfoChangedListener( SongInfoChangedListener listener ) {
		
		this.ChangedListeners.remove( listener );
		
		if ( this.ChangedListeners.size() == 0 ) {
			
			if ( MediaPlayer.isPlaying()  ) {
				
				Notification.showSong( CURRENT_MEDIA_ID );
				Notification.showPlaying();
				
			} else {
				
				this.stopSelf();
				
			}
			
		}
		
	}
	
	private void triggerSongInfoChanged( String media_id ) {
		
		int count = this.ChangedListeners.size();
		
		for ( int x = 0; x < count; x++ ) {
			
			this.ChangedListeners.get( x ).songInfoChanged( media_id );
			
		}
		
		if ( 0 == count ) {
			
			Notification.showSong( media_id );
			
		}
		
	}
	
	private void triggerMusicStarted( int position ) {
		
		int count = this.ChangedListeners.size();
		
		for ( int x = 0; x < count; x++ ) {
			
			this.ChangedListeners.get( x ).musicStarted( position );
			
		}
		
		if ( 0 == count ) {
			
			Notification.showPlaying();
			
		}
		
	}
	
	private void triggerMusicPaused( int position ) {
		
		int count = this.ChangedListeners.size();
		
		for ( int x = 0; x < count; x++ ) {
			
			this.ChangedListeners.get( x ).musicPaused( position );
			
		}
		
		if ( 0 == count ) {
			
			Notification.showPaused();
			
		}
		
	}
	
	
	private void triggerMusicDone() {
		
		int count = this.ChangedListeners.size();
		
		for ( int x = 0; x < count; x++ ) {
			
			this.ChangedListeners.get( x ).musicDone();
			
		}
		
		if ( 0 == count ) {
			
			Notification.remove();
			
		}
		
	}
	
	PlaybackListener MediaPlayerListener = new PlaybackListener() {
		
		@Override public void onTrackChanged( String media_id ) {
			
			CURRENT_MEDIA_ID = media_id; // Set even if media_id is null
			
			triggerSongInfoChanged( media_id );
			
			
			
		}

		@Override public void onPlay() {
			
			triggerMusicStarted( MediaPlayer.getCurrentPosition() );
			
		}

		@Override public void onPause() {
			
			triggerMusicPaused( MediaPlayer.getCurrentPosition() );
			
		}

		@Override public void onDone() {
			
			triggerMusicDone();
			
		}
		
	};
	/*
	public void doAttachActivity() {
		
		mBinderCount = mBinderCount + 1;
		
	}
	
	public void doDetachActivity() {
		
		mBinderCount = mBinderCount - 1;
		
		android.util.Log.i(TAG, "Binder Count: " + mBinderCount );
		
		if ( null != MediaPlayer ) {
			
			if ( !MediaPlayer.isPlaying() ) {
				
				this.stopSelf();
				
			} else {
				
				//Notification.showPlaying();
				
			}
			
		}
		
	}
	*/
}