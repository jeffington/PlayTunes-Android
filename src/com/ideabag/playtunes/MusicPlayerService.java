package com.ideabag.playtunes;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ideabag.playtunes.media.PlaylistMediaPlayer;
import com.ideabag.playtunes.media.PlaylistMediaPlayer.PlaybackListener;


public class MusicPlayerService extends Service {
	
	public static final String INTENT_ACTION_PLAY = "com.ideabag.playtunes.PLAY";
	public static final String INTENT_ACTION_PAUSE = "com.ideabag.playtunes.PAUSE";
	public static final String INTENT_ACTION_NEXT = "com.ideabag.playtunes.NEXT";
	public static final String INTENT_ACTION_CLOSE = "com.ideabag.playtunes.CLOSE";
	
	private static final String TAG = "PlayTunesMusicPlayerService";
	
	private PlaylistMediaPlayer MediaPlayer;
	private PlaybackNotification Notification;
	
	public String CURRENT_MEDIA_ID = null;
	
	@Override public void onCreate() {
		super.onCreate();
		
		MediaPlayer = new PlaylistMediaPlayer( getBaseContext() );
		
		Notification = new PlaybackNotification( getBaseContext() );
		
		NotificationActionIntentFilter.addAction( INTENT_ACTION_PLAY );
		NotificationActionIntentFilter.addAction( INTENT_ACTION_PAUSE );
		NotificationActionIntentFilter.addAction( INTENT_ACTION_NEXT );
		NotificationActionIntentFilter.addAction( INTENT_ACTION_CLOSE );
		
		registerReceiver( NotificationActionReceiver, NotificationActionIntentFilter );
		
		
		MediaPlayer.setPlaybackListener( new PlaybackListener() {

			@Override public void onTrackChanged( String media_id ) {
				
				CURRENT_MEDIA_ID = media_id; // Set even if media_id is null
				
				if ( null != media_id ) {
					
					Notification.showSong( media_id );
					
					if ( null != ChangedListener ) {
						
						ChangedListener.songInfoChanged( media_id );
						
					}
					
				}
				
				
				
			}

			@Override public void onPlay() {
				// TODO Auto-generated method stub
				
				Notification.showPlaying();
				
			}

			@Override public void onPause() {
				// TODO Auto-generated method stub
				
			}

			@Override public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override public void onEnd() {
				// TODO Auto-generated method stub
				
			}
			
			
		});
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver( NotificationActionReceiver );
		
		Notification.remove();
		MediaPlayer.destroy();
		
		Log.i(TAG, "Service destroyed.");
		
	}
	
	public class MusicPlayerServiceBinder extends Binder {
        
		public MusicPlayerService getService() {
            
        	return MusicPlayerService.this;
            
        }
        
    }
	
	private IntentFilter NotificationActionIntentFilter = new IntentFilter();
	
	private BroadcastReceiver NotificationActionReceiver = new BroadcastReceiver() {

		@Override public void onReceive( Context context, Intent intent ) {
			
			String action = intent.getAction();
			
			if ( action.equals( INTENT_ACTION_PLAY ) ) {
				
				play();
				
			} else if ( action.equals( INTENT_ACTION_PAUSE ) ) {
				
				pause();
				
			} else if ( action.equals( INTENT_ACTION_NEXT ) ) {
				
				next();
				
			} else if ( action.equals( INTENT_ACTION_CLOSE ) ) {
				
				stopSelf();
				
			}
			
		}
		
	};
	
	
	// 
	// Service Binder & Public APIs
	// 
	// 
	// 
	private final IBinder mBinder = new MusicPlayerServiceBinder();
	
	private SongInfoChangedListener ChangedListener = null;
	
	@Override public IBinder onBind( Intent intent ) {
		return mBinder;
	}
	
	public interface SongInfoChangedListener {
		
		public void songInfoChanged( String media_content_id );
		
	}
	
	public void setPlaylistCursor( Cursor c ) {
		
		MediaPlayer.setPlaylistCursor( c );
		
	}
	
	public void setPosition( int pos ) {
		
		MediaPlayer.setPosition( pos );
		
	}
	
	public void next() {
		
		MediaPlayer.nextTrack();
		
	}
	
	public void prev() {
		
		MediaPlayer.previousTrack();
		
	}
	
	public void play() {
		
		MediaPlayer.play();
		
	}
	
	public void pause() {
		
		MediaPlayer.pause();
		
	}
	
	public void setOnSongInfoChangedListener( SongInfoChangedListener listener ) {
		
		ChangedListener = listener;
		
	}
	
	public void removeOnSongInfoChangedListener() {
		
		ChangedListener = null;
		
	}
	
}