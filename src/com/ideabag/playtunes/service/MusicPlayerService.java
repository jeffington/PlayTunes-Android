package com.ideabag.playtunes.service;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.Fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.media.AudioFocusHelper;
import com.ideabag.playtunes.media.MusicFocusable;
import com.ideabag.playtunes.media.PlaylistMediaPlayer;
import com.ideabag.playtunes.media.PlaylistMediaPlayer.PlaybackListener;
import com.ideabag.playtunes.util.GAEvent;
import com.ideabag.playtunes.util.GAEvent.AudioControls;
import com.ideabag.playtunes.util.TrackerSingleton;
import com.ideabag.playtunes.util.GAEvent.Categories;


public class MusicPlayerService extends Service implements MusicFocusable {
	
	public static final String ACTION_PLAY = "com.ideabag.playtunes.PLAY";
	public static final String ACTION_PLAY_OR_PAUSE = "com.ideabag.playtunes.PLAY_PAUSE";
	public static final String ACTION_PAUSE = "com.ideabag.playtunes.PAUSE";
	public static final String ACTION_NEXT = "com.ideabag.playtunes.NEXT";
	public static final String ACTION_BACK = "com.ideabag.playtunes.BACK";
	public static final String ACTION_CLOSE = "com.ideabag.playtunes.CLOSE";
	
	private static final String PREF_KEY_NOWPLAYING_CLASS = "now_playing_class";
	private static final String PREF_KEY_NOWPLAYING_NAME = "now_playing_name";
	private static final String PREF_KEY_NOWPLAYING_ID = "now_playing_media_id";
	private static final String PREF_KEY_NOWPLAYING_QUERY = "now_playing_media_query";
	private static final String PREF_KEY_NOWPLAYING_POSITION = "now_playing_position";
	private static final String PREF_KEY_NOWPLAYING_SHUFFLE = "now_playing_shuffle";
	private static final String PREF_KEY_NOWPLAYING_LOOP = "now_playing_loop";
	
	private Tracker mTracker;
	
	@SuppressWarnings("unused")
	private static final String TAG = "MusicPlayerService";
	
	private PlaylistMediaPlayer MediaPlayer;
	private PlaybackNotification Notification;
	private LockscreenManager Lockscreen;
	
	private SharedPreferences mSharedPrefs;
	
    AudioFocusHelper mAudioFocusHelper = null;
	
    // All used to save and restore what the user is playing across app open/close
	public String CURRENT_MEDIA_ID = null;
	public Class < ? extends Fragment > mPlaylistFragmentClass;
	public String mPlaylistMediaID;
	public String mPlaylistName;
	
	boolean isServiceStarted = false;
	
	private MusicPlayerService self;
	
	private ArrayList< PlaybackListener > ChangedListeners = new ArrayList< PlaybackListener >();

	BroadcastReceiver NotificationActionReceiver = new BroadcastReceiver() {

		@Override public void onReceive( Context context, Intent intent ) {
			
			String action = intent.getAction();
			
			if ( action.equals( ACTION_PLAY_OR_PAUSE ) ) {
				
				if ( null != MediaPlayer ) {
					
					if ( MediaPlayer.isPlaying() ) {
						
						mTracker.send( new HitBuilders.EventBuilder()
			        	.setCategory( intent.hasExtra( Categories.LOCKSCREEN ) ? Categories.LOCKSCREEN : Categories.NOTIFICATION )
			        	.setAction( AudioControls.ACTION_PAUSE )
			        	.build());
						
						pause();
						
					} else {
						
						mTracker.send( new HitBuilders.EventBuilder()
				    	.setCategory( intent.hasExtra( Categories.LOCKSCREEN ) ? Categories.LOCKSCREEN : Categories.NOTIFICATION )
				    	.setAction( AudioControls.ACTION_PLAY )
				    	.build());
						
						
						play();
						
					}
					
				}
				
				
			} else if ( action.equals( ACTION_NEXT ) ) {
			
				mTracker.send( new HitBuilders.EventBuilder()
	        	.setCategory( intent.hasExtra( Categories.LOCKSCREEN ) ? Categories.LOCKSCREEN : Categories.NOTIFICATION )
	        	.setAction( AudioControls.ACTION_NEXT )
	        	.build());
				
				next();
				
			} else if ( action.equals( ACTION_CLOSE ) ) {
				
				mTracker.send( new HitBuilders.EventBuilder()
	        	.setCategory( intent.hasExtra( Categories.LOCKSCREEN ) ? Categories.LOCKSCREEN : Categories.NOTIFICATION )
	        	.setAction( GAEvent.Notification.ACTION_CLOSE )
	        	.build());
				
				if ( isServiceStarted ) {
					
					isServiceStarted = false;
					 
					self.stopSelf();
					 
				}
				
				
			} else if ( action.equals( ACTION_BACK ) ) {
				
				mTracker.send( new HitBuilders.EventBuilder()
	        	.setCategory( intent.hasExtra( Categories.LOCKSCREEN ) ? Categories.LOCKSCREEN : Categories.NOTIFICATION )
	        	.setAction( AudioControls.ACTION_PREV )
	        	.build());
				
				MediaPlayer.back();
				
			}
			
		}
		
	};
	
	@SuppressWarnings("unchecked")
	@SuppressLint("NewApi")
	@Override public void onCreate() {
		super.onCreate();
		
		self = this;
		
		MediaPlayer = new PlaylistMediaPlayer( getBaseContext() );
		
		Notification = new PlaybackNotification( getBaseContext() );
		
		Lockscreen = new LockscreenManager( getBaseContext() );
		
		mSharedPrefs = this.getSharedPreferences( getString( R.string.prefs_file ), Context.MODE_PRIVATE );
		mTracker = TrackerSingleton.getDefaultTracker( this );
		//
		
		IntentFilter mNotificationIntentFilter = new IntentFilter();
		mNotificationIntentFilter.addAction( ACTION_PLAY_OR_PAUSE );
		mNotificationIntentFilter.addAction( ACTION_NEXT );
		mNotificationIntentFilter.addAction( ACTION_CLOSE );
		mNotificationIntentFilter.addAction( ACTION_BACK );
		
		
		registerReceiver( NotificationActionReceiver, mNotificationIntentFilter );
		
		mAudioFocusHelper = new AudioFocusHelper( getApplicationContext(), this );
		
		String mMediaQueryJSONString = mSharedPrefs.getString( PREF_KEY_NOWPLAYING_QUERY, null );
		int mPlaylistPosition = mSharedPrefs.getInt( PREF_KEY_NOWPLAYING_POSITION, 0 );
		
		boolean mIsShuffling = mSharedPrefs.getBoolean( PREF_KEY_NOWPLAYING_SHUFFLE, false );
		int looping = mSharedPrefs.getInt( PREF_KEY_NOWPLAYING_LOOP, PlaylistMediaPlayer.LOOP_NO );
		
		mPlaylistMediaID = mSharedPrefs.getString( PREF_KEY_NOWPLAYING_ID, null );
		mPlaylistName = mSharedPrefs.getString( PREF_KEY_NOWPLAYING_NAME, "" );
		String classNameString = mSharedPrefs.getString( PREF_KEY_NOWPLAYING_CLASS, "" );
		try {
			mPlaylistFragmentClass = (Class<? extends Fragment>) Class.forName( classNameString );
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
		
		MediaPlayer.setPlaybackListener( MediaPlayerListener );
		
		if ( mMediaQueryJSONString != null ) {
			
			MediaQuery mMediaQuery = new MediaQuery( mMediaQueryJSONString );
			
			if ( mMediaQuery != null ) {
				
				MediaPlayer.setPlaylistQuery( mMediaQuery );
				
				MediaPlayer.setPlaylistPosition( mPlaylistPosition );
				
				MediaPlayer.setLooping( looping );
				
				MediaPlayer.setShuffle( mIsShuffling );
				
			}
			
		}
		
		if ( !isServiceStarted ) {
			
			startService( new Intent( this, MusicPlayerService.class ) );
			isServiceStarted = true;
			
		}
		
	}
	
	
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		mAudioFocusHelper.abandonFocus();
		
		unregisterReceiver( NotificationActionReceiver );
		
		ChangedListeners.clear();
		
		Lockscreen.remove();
		
		// Save state
		
		
		if ( MediaPlayer.getPlaylistQuery() != null ) { 
			
			SharedPreferences.Editor edit = mSharedPrefs.edit();
			edit.putString( PREF_KEY_NOWPLAYING_QUERY, MediaPlayer.getPlaylistQuery().toJSONString() );
			
			edit.putInt( PREF_KEY_NOWPLAYING_POSITION, MediaPlayer.getPlaylistPosition() );
			
			edit.putString( PREF_KEY_NOWPLAYING_CLASS, mPlaylistFragmentClass.getName() );
			edit.putString( PREF_KEY_NOWPLAYING_NAME, mPlaylistName );
			edit.putString( PREF_KEY_NOWPLAYING_ID, mPlaylistMediaID );
			edit.putInt( PREF_KEY_NOWPLAYING_LOOP, MediaPlayer.getLoopState() );
			edit.putBoolean( PREF_KEY_NOWPLAYING_SHUFFLE, MediaPlayer.isShuffling() );
			
			edit.commit();
			
		}
		// Clean up Media Player
		
		MediaPlayer.pause();
		MediaPlayer.destroy();
		
		// Remove Notification (if showing)
		
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
	
	public void setPlaylist( MediaQuery query, String playlistName, Class < ? extends Fragment > fragmentClass, String playlistMediaID ) {
		
		if ( null != MediaPlayer ) {
			
			MediaPlayer.setPlaylistQuery( query );
			this.mPlaylistFragmentClass = fragmentClass;
			this.mPlaylistMediaID = playlistMediaID;
			this.mPlaylistName = playlistName;
			
			SharedPreferences.Editor edit = mSharedPrefs.edit();
			edit.putString( PREF_KEY_NOWPLAYING_ID, playlistMediaID );
			edit.putString( PREF_KEY_NOWPLAYING_NAME, playlistName );
			edit.putString( PREF_KEY_NOWPLAYING_CLASS, fragmentClass.getName() );
			
			edit.commit();
			
			
		}
		
	}
	
	// 
	// rectifyPlaylist is called when the content of the query changes
	// for instance, when a song is removed from a playlist or deleted from the device.
	// 
	// - The goal is to switch to the new query cursor, but to retain play position 
	// - It is possible that the currently playing song is not in the new query
	// - It is possible that the currently playing song is in a new position in the query
	// 
	// 
	
	public void rectifyPlaylist( MediaQuery query, String playlistName, Class < ? extends Fragment > fragmentClass, String playlistMediaID ) {
		
		if ( null != MediaPlayer ) {
			
			MediaPlayer.setPlaylistQuery( query );
			this.mPlaylistFragmentClass = fragmentClass;
			this.mPlaylistMediaID = playlistMediaID;
			this.mPlaylistName = playlistName;
			
			SharedPreferences.Editor edit = mSharedPrefs.edit();
			edit.putString( PREF_KEY_NOWPLAYING_ID, playlistMediaID );
			edit.putString( PREF_KEY_NOWPLAYING_NAME, playlistName );
			edit.putString( PREF_KEY_NOWPLAYING_CLASS, fragmentClass.getName() );
			
			//edit.
			//android.util.Log.i( TAG, "" + playlistMediaID + " " + fragmentClass.getName() );
			edit.commit();
			
		}
		
	}
	
	
	public void setPlaylistPosition( int position ) {
		
		if ( null != MediaPlayer ) {
			
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
	
	public void setLooping( int repeat ) {
		
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
		
		Notification.remove();
		
	}
	
	public void removePlaybackListener( PlaybackListener listener ) {
		
		this.ChangedListeners.remove( listener );
		
		//
		// Listeners are removed when an interested Activity or Fragment UI is hidden, so we either
		// show the notification in this case and update it with the currently playing song
		// or destroy the service if no music is playing.
		// 
		
		if ( this.ChangedListeners.size() == 0 ) {
			
			if ( MediaPlayer.isPlaying()  ) {
				
				Notification.setMediaID( CURRENT_MEDIA_ID );
				Notification.play();
				Notification.show();
				
			} else if ( isServiceStarted ) {
				
				isServiceStarted = false;
				this.stopSelf();
				
			}
			
		}
		
	}
	
	PlaybackListener MediaPlayerListener = new PlaybackListener() {
		
		// 
		// No null check because null is a meaningful value in this situation
		// 
		
		@Override public void onTrackChanged( String media_id ) {
			
			CURRENT_MEDIA_ID = media_id; 
			
			android.util.Log.i( TAG, "" + media_id );
			
			int count = ChangedListeners.size();
			
			for ( int x = 0; x < count; x++ ) {
				
				ChangedListeners.get( x ).onTrackChanged( media_id );
				
			}
			
			Lockscreen.setMediaID( media_id, MediaPlayer.hasPreviousTrack(), MediaPlayer.hasNextTrack()  );
			
			Notification.setMediaID( media_id );
			
			if ( MediaPlayer.isPlaying() ) {
				
				mAudioFocusHelper.requestFocus();
				
			}
			
		}


		@Override public void onPlaylistDone() {
			
			mAudioFocusHelper.abandonFocus();
			
			int count = ChangedListeners.size();
			
			for ( int x = 0; x < count; x++ ) {
				
				ChangedListeners.get( x ).onPlaylistDone();
				
			}
			
			Lockscreen.remove();
			
			Notification.remove();
			
		}

		@Override public void onLoopingChanged( int loop ) {
			
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
			
			mAudioFocusHelper.requestFocus();
			
			for ( int x = 0; x < count; x++ ) {
				
				ChangedListeners.get( x ).onPlay( playbackPositionMilliseconds );
				
			}
			
			Lockscreen.play();
			
			Notification.play();
			
		}


		@Override public void onPause( int playbackPositionMilliseconds ) {
			
			int count = ChangedListeners.size();
			
			for ( int x = 0; x < count; x++ ) {
				
				ChangedListeners.get( x ).onPause( playbackPositionMilliseconds );
				
			}
			
			Lockscreen.pause();
			
			Notification.pause();
			
		}
		
	};

	@Override public void onGainedAudioFocus() {
		
		// TODO:
		MediaPlayer.stopVolumeDucking();
		
	}
	


	@Override public void onLostAudioFocus( boolean canDuck ) {
		
		if ( !canDuck ) {
			
			Lockscreen.remove();
			//destroyRemoteControlClient();
			pause();
			
		} else {
			
			MediaPlayer.startVolumeDucking();
			
		}
		
	}
	
}