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
import com.ideabag.playtunes.media.PlaylistMediaPlayer.LoopState;
import com.ideabag.playtunes.media.PlaylistMediaPlayer.PlaybackListener;
import com.ideabag.playtunes.util.TrackerSingleton;


public class MusicPlayerService extends Service {
	
	public static final String ACTION_PLAY = "com.ideabag.playtunes.PLAY";
	public static final String ACTION_PLAY_OR_PAUSE = "com.ideabag.playtunes.PLAY_PAUSE";
	public static final String ACTION_PAUSE = "com.ideabag.playtunes.PAUSE";
	public static final String ACTION_NEXT = "com.ideabag.playtunes.NEXT";
	public static final String ACTION_CLOSE = "com.ideabag.playtunes.CLOSE";
	
	private static final String TAG = "MusicPlayerService";
	
	private PlaylistMediaPlayer MediaPlayer;
	private PlaybackNotification Notification;
	
	public String CURRENT_MEDIA_ID = null;
	public Class < ? extends Fragment > mPlaylistFragmentClass;
	public String mPlaylistMediaID;
	public String mPlaylistName;
	
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
			
			if ( 0 == count ) {
				
				Notification.showSong( media_id );
				
			}
			
		}


		@Override public void onPlaylistDone() {
			
			int count = ChangedListeners.size();
			
			for ( int x = 0; x < count; x++ ) {
				
				ChangedListeners.get( x ).onPlaylistDone();
				
			}
			
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
			
			if ( count == 0 ) {
				
				Notification.showPlaying();
				
			}
			
		}


		@Override public void onPause( int playbackPositionMilliseconds ) {
			
			int count = ChangedListeners.size();
			
			for ( int x = 0; x < count; x++ ) {
				
				ChangedListeners.get( x ).onPause( playbackPositionMilliseconds );
				
			}
			
			if ( count == 0 ) {
				
				Notification.showPaused();
				
			}
			
		}
		
	};
	
}