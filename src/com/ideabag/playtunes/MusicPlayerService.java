package com.ideabag.playtunes;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.media.AudioFocusHelper;
import com.ideabag.playtunes.media.MediaButtonHelper;
import com.ideabag.playtunes.media.MusicFocusable;
import com.ideabag.playtunes.media.MusicIntentReceiver;
import com.ideabag.playtunes.media.PlaylistMediaPlayer;
import com.ideabag.playtunes.media.RemoteControlClientCompat;
import com.ideabag.playtunes.media.RemoteControlHelper;
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
	
	private SharedPreferences mSharedPrefs;
	
	// our RemoteControlClient object, which will use remote control APIs available in
    // SDK level >= 14, if they're available.
    RemoteControlClientCompat mRemoteControlClientCompat;

    // Dummy album art we will pass to the remote control (if the APIs are available).
    //Bitmap mDummyAlbumArt;

    // The component name of MusicIntentReceiver, for use with media button and remote control
    // APIs
    ComponentName mMediaButtonReceiverComponent;
    
    AudioManager mAudioManager;
    
    AudioFocusHelper mAudioFocusHelper = null;
	
	public String CURRENT_MEDIA_ID = null;
	public Class < ? extends Fragment > mPlaylistFragmentClass;
	public String mPlaylistMediaID;
	public String mPlaylistName;
	private String mAlbumArtUri = null;
	
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
		
		mSharedPrefs = this.getSharedPreferences( getString( R.string.prefs_file ), Context.MODE_PRIVATE );
		mTracker = TrackerSingleton.getDefaultTracker( this );
		mAudioManager = ( AudioManager ) getSystemService( AUDIO_SERVICE );
		
		IntentFilter mNotificationIntentFilter = new IntentFilter();
		mNotificationIntentFilter.addAction( ACTION_PLAY_OR_PAUSE );
		mNotificationIntentFilter.addAction( ACTION_NEXT );
		mNotificationIntentFilter.addAction( ACTION_CLOSE );
		mNotificationIntentFilter.addAction( ACTION_BACK );
		
		
		registerReceiver( NotificationActionReceiver, mNotificationIntentFilter );
		
		mAudioFocusHelper = new AudioFocusHelper( getApplicationContext(), this );
		
		//private static final String PREF_KEY_NOWPLAYING_CLASS = "now_playing_class";
		//private static final String PREF_KEY_NOWPLAYING_NAME = "now_playing_name";
		//private static final String PREF_KEY_NOWPLAYING_ID = "now_playing_media_id";
		
		//String className = mSharedPrefs.getString( PREF_KEY_NOWPLAYING_CLASS, "" );
		//String nowPlayingName, nowPlayingMediaID;
		
		//if ( null != className && className.length() > 0 ) {
			
			//nowPlayingName = mSharedPrefs.getString( PREF_KEY_NOWPLAYING_NAME, "" );
			//nowPlayingMediaID = mSharedPrefs.getString( PREF_KEY_NOWPLAYING_ID, "" );
			
			//this.setP
			
		//}
		
		//Gson gson = new Gson();
		
		String mMediaQueryJSONString = mSharedPrefs.getString( PREF_KEY_NOWPLAYING_QUERY, null );
		int mPlaylistPosition = mSharedPrefs.getInt( PREF_KEY_NOWPLAYING_POSITION, 0 );
		
		boolean mIsShuffling = mSharedPrefs.getBoolean( PREF_KEY_NOWPLAYING_SHUFFLE, false );
		int looping = mSharedPrefs.getInt( PREF_KEY_NOWPLAYING_LOOP, PlaylistMediaPlayer.LOOP_NO );
		
		if ( mMediaQueryJSONString != null ) {
			
			MediaQuery mMediaQuery = new MediaQuery( mMediaQueryJSONString );
			
			if ( mMediaQuery != null ) {
				
				MediaPlayer.setPlaylistQuery( mMediaQuery );
				
				MediaPlayer.setPlaylistPosition( mPlaylistPosition );
				
				MediaPlayer.setLooping( looping );
				
				MediaPlayer.setShuffle( mIsShuffling );
				
			}
			
		}
		
		mPlaylistMediaID = mSharedPrefs.getString( PREF_KEY_NOWPLAYING_ID, null );
		mPlaylistName = mSharedPrefs.getString( PREF_KEY_NOWPLAYING_NAME, "" );
		String classNameString = mSharedPrefs.getString( PREF_KEY_NOWPLAYING_CLASS, "" );
		try {
			mPlaylistFragmentClass = (Class<? extends Fragment>) Class.forName( classNameString );
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
		
		MediaPlayer.setPlaybackListener( MediaPlayerListener );
		
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
		
		destroyRemoteControlClient();
		
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
	
	@SuppressLint("InlinedApi")
	public void play() {
		
		if ( null != MediaPlayer && !MediaPlayer.isPlaying() ) {
			
			MediaPlayer.play();
			
			if ( mRemoteControlClientCompat != null ) {
	            
				mRemoteControlClientCompat.setPlaybackState( RemoteControlClient.PLAYSTATE_PLAYING );
	            
	        }
			
		}
		
	}
	
	public void pause() {
		
		if ( null != MediaPlayer ) {
			
			MediaPlayer.pause();
			
		}
		
	}
	
	public void setLooping( int repeat ) {
		
		MediaPlayer.setLooping( repeat );
		
		if ( MediaPlayer.isPlaying() ) {
			
			updateRemoteControlClientPlay();
			
		} else {
			
			updateRemoteControlClientPause();
			
		}
		
	}
	
	public void setShuffle( boolean isShuffling ) {
		
		MediaPlayer.setShuffle( isShuffling );
		
	}
	
	
	private void createRemoteControlClient() {
		
		if ( android.os.Build.VERSION.SDK_INT >= 14 ) {
			
	        if ( mRemoteControlClientCompat == null ) {
	        	
				mMediaButtonReceiverComponent = new ComponentName( this, MusicIntentReceiver.class );
				MediaButtonHelper.registerMediaButtonEventReceiverCompat( mAudioManager, mMediaButtonReceiverComponent );
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
	
	private void destroyRemoteControlClient() {
		
		if ( null != mRemoteControlClientCompat ) {
			
			RemoteControlHelper.unregisterRemoteControlClient( mAudioManager, mRemoteControlClientCompat );
			MediaButtonHelper.unregisterMediaButtonEventReceiverCompat( mAudioManager, mMediaButtonReceiverComponent );
			
			mRemoteControlClientCompat = null;
			
		}
		
	}
	
	@SuppressLint("InlinedApi")
	private void updateRemoteControlClientPause() {
		
		createRemoteControlClient();
		
		if ( null != mRemoteControlClientCompat ) {
			
	        int mFlags = RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE;
	        
	        if ( MediaPlayer.hasNextTrack() ) {
	        	
	        	mFlags |= RemoteControlClient.FLAG_KEY_MEDIA_NEXT;
	        	
	        }
	        
	        if ( MediaPlayer.hasPreviousTrack() ) {
	        	
	        	mFlags |= RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS;
	        	
	        }
	        
	        mRemoteControlClientCompat.setTransportControlFlags( mFlags );
	        
			mRemoteControlClientCompat.setPlaybackState( RemoteControlClient.PLAYSTATE_PAUSED );
			
		}
		
	}
	
	@SuppressLint("InlinedApi")
	private void updateRemoteControlClientPlay() {
		
		createRemoteControlClient();
		
		if ( null != mRemoteControlClientCompat ) {
			
	        int mFlags = RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE;
	        
	        if ( MediaPlayer.hasNextTrack() ) {
	        	
	        	mFlags |= RemoteControlClient.FLAG_KEY_MEDIA_NEXT;
	        	
	        }
	        
	        if ( MediaPlayer.hasPreviousTrack() ) {
	        	
	        	mFlags |= RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS;
	        	
	        }
	        
	        mRemoteControlClientCompat.setTransportControlFlags( mFlags );
	        
			mRemoteControlClientCompat.setPlaybackState( RemoteControlClient.PLAYSTATE_PLAYING );
			
		}
		
	}
	
	@SuppressLint("InlinedApi")
	private void updateRemoteControlClientMedia( String media_id ) {
		
		createRemoteControlClient();
		
		if ( null != mRemoteControlClientCompat ) {
			
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
			
			MediaQuery.executeAsync( this, mSongQuery, new MediaQuery.OnQueryCompletedListener() {
				
				@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {

					mResult.moveToFirst();
					
					String mSongTitle = mResult.getString( mResult.getColumnIndex(MediaStore.Audio.Media.TITLE ) );
					String mSongAlbum = mResult.getString( mResult.getColumnIndex(MediaStore.Audio.Media.ALBUM ) );
					String mSongArtist = mResult.getString( mResult.getColumnIndex(MediaStore.Audio.Media.ARTIST ) );
					long mSongDuration = mResult.getLong( mResult.getColumnIndex( MediaStore.Audio.Media.DURATION ) );
					String album_id = mResult.getString( mResult.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM_ID ) );
					//Bitmap mAlbumArt = BitmapFactory.
					
					mResult.close();
					
			        mRemoteControlClientCompat.editMetadata( true )
	                .putString( MediaMetadataRetriever.METADATA_KEY_ARTIST, mSongArtist )
	                .putString( MediaMetadataRetriever.METADATA_KEY_ALBUM, mSongAlbum )
	                .putString( MediaMetadataRetriever.METADATA_KEY_TITLE, mSongTitle )
	                .putLong( MediaMetadataRetriever.METADATA_KEY_DURATION, mSongDuration )
	                //.putBitmap( RemoteControlClientCompat.MetadataEditorCompat.METADATA_KEY_ARTWORK, mAlbumArtBitmap )
	                .apply();
			        
					MediaQuery mAlbumQuery = new MediaQuery(
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
					
					MediaQuery.executeAsync(self, mAlbumQuery, new MediaQuery.OnQueryCompletedListener() {
						
						@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
							
							mResult.moveToFirst();
							
							String newAlbumUri = mResult.getString( mResult.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
							mResult.close();
							
							try {
								
								Bitmap mAlbumArtBitmap = null;
								
								if ( null == newAlbumUri ) {
									
									mAlbumArtBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_album_art_full );
									
								} else if ( !newAlbumUri.equals( mAlbumArtUri ) ){
									
									mAlbumArtBitmap = BitmapFactory.decodeFile( newAlbumUri );
									
								}
								/*
								mRemoteControlClientCompat.editMetadata( true )
								.putBitmap( RemoteControlClientCompat.MetadataEditorCompat.METADATA_KEY_ARTWORK, mAlbumArtBitmap )
								.apply();
								*/
							} catch (Exception e) {
								e.printStackTrace();
							}
							
						}
						
					});
					
			        mRemoteControlClientCompat.setPlaybackState( RemoteControlClient.PLAYSTATE_PLAYING );
			        
			        int mFlags = RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE;
			        
			        if ( MediaPlayer.hasNextTrack() ) {
			        	
			        	mFlags |= RemoteControlClient.FLAG_KEY_MEDIA_NEXT;
			        	
			        }
			        
			        if ( MediaPlayer.hasPreviousTrack() ) {
			        	
			        	mFlags |= RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS;
			        	
			        }
			        
			        mRemoteControlClientCompat.setTransportControlFlags( mFlags );
					
				}
				
			});
			
	
	        // Update the remote controls
	        
	        
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
		// Listeners are removed when an interested Activity or Fragment UI is hidden, so we either
		// show the notification in this case and update it with the currently playing song
		// or destroy the service if no music is playing.
		// 
		
		if ( this.ChangedListeners.size() == 0 ) {
			
			//android.util.Log.i( TAG, "" + MediaPlayer.isPlaying() );
			
			if ( MediaPlayer.isPlaying()  ) {
				
				Notification.showSong( MediaPlayer.getCurrentMediaID() );
				Notification.showPlaying();
				
			} else if ( isServiceStarted ) {
				
				isServiceStarted = false;
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
			
			if ( 0 == count ) {
				
				Notification.remove();
				
			}
			
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
		
		// TODO:
		MediaPlayer.stopVolumeDucking();
		
	}
	


	@Override public void onLostAudioFocus( boolean canDuck ) {
		
		if ( !canDuck ) {
			
			destroyRemoteControlClient();
			pause();
			
		} else {
			
			MediaPlayer.startVolumeDucking();
			
		}
		
	}
	
}