
//
// CustomMediaPlayer adds looping, shuffling, and playlist handling
//

package com.ideabag.playtunes.media;


import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PlaylistMediaPlayer extends MediaPlayer {
	
	public static final String TAG = "PlaylistMediaPlayer";
	
	private boolean isShuffling, isPlaying;
	
	private Cursor Playlist;
	
	private PlaybackListener PlaybackChanged = null;
	
	public interface PlaybackListener {
		
		void onTrackChanged( String media_id );
		void onPlay();
		void onPause();
		void onDone();
		
	}
	
	public void setPlaybackListener( PlaybackListener pbl ) {
		
		this.PlaybackChanged = pbl;
		
	}
	
	private Context mContext;
	
	PowerManager pm;
	PowerManager.WakeLock wl;
	
	public PlaylistMediaPlayer( Context context ) {
		super();
		
		( ( TelephonyManager ) context.getSystemService( Context.TELEPHONY_SERVICE ) ).listen( phoneListener, PhoneStateListener.LISTEN_CALL_STATE );
		
		mContext = context;
		
		setOnCompletionListener( loopOnCompletionListener );
		
		// 
		// Intents for loss of connection to media
		// 
		hardwareStopIntents.addAction( Intent.ACTION_MEDIA_EJECT );
		hardwareStopIntents.addAction( Intent.ACTION_MEDIA_UNMOUNTED );
		hardwareStopIntents.addAction( Intent.ACTION_MEDIA_REMOVED );
		
		mContext.registerReceiver(HardwareStopReceiver, hardwareStopIntents);
		
		pm = ( PowerManager ) mContext.getSystemService( Context.POWER_SERVICE );
		wl = pm.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK, TAG );
		
	}
	
	
	MediaPlayer.OnCompletionListener loopOnCompletionListener = new MediaPlayer.OnCompletionListener() {
		
		public void onCompletion( MediaPlayer mp ) {
			
			if ( null != Playlist ) {
				
				if ( mLoopState == LoopState.LOOP_ALL ) {
					
					setPlaylistPosition( ( Playlist.getPosition() + 1 ) % Playlist.getCount() ); // Will always loop the whole thing
					
				} else if ( mLoopState == LoopState.LOOP_NO ){
					
					setPlaylistPosition( Playlist.getPosition() + 1 );
					
				}
				
				// Do nothing for LOOP_ONE, the MediaPlayer takes care of it on its own
				
			}
			
		}
		
	};
	
	MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
		
		@Override public void onPrepared( MediaPlayer mp ) {
			
			// Alert the client if the track has changed
			
		}
		
	};
	
	// 
	// Public Methods
	// 
	// 
	
	//
	// Clean up the media player and associated acts when you're done
	//
	public void destroy() {
		
		reset();
		release();
		
		if ( wl.isHeld() ) {
			
			wl.release();
			
		}
		
		mContext.unregisterReceiver( HardwareStopReceiver );
		
	}
	
	public void setPlaylistCursor( Cursor c ) {
		
		Playlist = c;
		
	}
	
	public void play() {
		
		if ( null != Playlist
				&& !Playlist.isBeforeFirst()
				&& !Playlist.isAfterLast() ) {
			
			start();
			isPlaying = true;
			
			if ( null != PlaybackChanged ) {
				
				PlaybackChanged.onPlay();
				
			}
			
			if ( !wl.isHeld() ) {
				
				wl.acquire(); // Wake lock acquired
				
			}
			
		}
		
	}
	
	public void pause() {
		
		if ( isPlaying() ) {
			
			super.pause();
			
			if ( null != PlaybackChanged ) {
				
				PlaybackChanged.onPause();
				
			}
			
		}
		
	}
	
	public void previousTrack() {
		
		if ( null != Playlist ) {
			
			setPlaylistPosition( Playlist.getPosition() - 1 );
			
		}
		
	}
	
	
	public void nextTrack() {
		
		if ( null != Playlist ) {
			
			setPlaylistPosition( Playlist.getPosition() + 1 );
			
		}
		
	}
	
	// 
	// Abstracting out collecting information on the current song
	// 
	// NOTE: PlaylistMediaPlayer expects column 0 of the Cursor to be the ID of the media
	// 
	
	private boolean playlistReady() {
		
		return !(Playlist == null || Playlist.isAfterLast() || Playlist.isBeforeFirst() || Playlist.isClosed() );
		
	}
	
	public String getCurrentMediaID() {
		
		if ( playlistReady() ) {
			
			String media_id = Playlist.getString( 0 );
			
			//Playlist.getColumnName( 0 )
			
			android.util.Log.i("Media ID for media player", media_id );
			
			return media_id;
			
		} else {
			
			return null;
			
		}
		
	}
	
	// TODO: Move to next shuffled position (if shuffling) 
	public void setPlaylistPosition( int position ) {
		
		try {
			
			Playlist.moveToPosition( position );
			
			if ( Playlist.isBeforeFirst() || Playlist.isAfterLast() ) {
				
				pause();
				
				// Send stopped event
				
				if ( null != PlaybackChanged ) {
					
					PlaybackChanged.onDone();
					
				}
				
				return;
				
			}
			
			reset();
			
			if ( !isShuffling ) {
				
				setDataSource( Playlist.getString( Playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.DATA ) ) );
				
				if ( null != PlaybackChanged ) {
					
					PlaybackChanged.onTrackChanged( getCurrentMediaID() );
					
				}
				
			} else { // Shuffle!
				
				int tmp = Playlist.getPosition();
				
				int nextInt = getNthInt( tmp );
				
				Playlist.moveToPosition( nextInt );
				
				setDataSource( Playlist.getString( Playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.DATA ) ) );
				
				if ( null != PlaybackChanged ) {
					
					PlaybackChanged.onTrackChanged( getCurrentMediaID() );
					
				}
				
				Playlist.moveToPosition( tmp );
				
			}
			
			prepare();
			
			if ( isPlaying ) {
				
				play();
				
			}
			
		} catch ( Exception e ) {
			
			e.printStackTrace();
		
		}
	
	}
	
	public void setSeekPosition( int msecs ) {
		
		if ( this.isPlaying && this.isPlaying() ) {
			
			this.pause();
			
		}
		
		this.seekTo( msecs );
		
		if ( this.isPlaying ) {
			
			this.play();
			
		}
		
	}
	
	
	// 
	// Looping / Repeating
	//
	// The standard media player has a built-in setLooping function that sets whether the current media will loop or not.
	// We want to be able to loop the entire playlist. To represent this third state of looping, the LoopState enum
	// is used. MediaPlayer's setLooping() method still accepts a boolean and PlaylistMediaPlayer's mLoopState is update
	// with the appropriate LoopState.LOOP_NO or LoopState.LOOP_ONE value. The setLooping() method is overloaded with a 
	// version that accepts a LoopState enum and through this you can set any of the three loop states.
	// 
	
	public enum LoopState {
		LOOP_NO,
		LOOP_ALL,
		LOOP_ONE
	};
	
	protected LoopState mLoopState;
	
	public void setLooping( LoopState state ) {
		
		if ( state != LoopState.LOOP_ONE ) {
			
			setLooping( false );
			
		} else {
			
			setLooping( true );
			
		}
		
		mLoopState = state;
		
	}
	
	//
	// Overridden to maintain MediaPlayer API and sync local state
	//
	@Override public void setLooping( boolean shouldLoop ) {
		super.setLooping( shouldLoop );
		
		if ( !shouldLoop ) {
			
			mLoopState = LoopState.LOOP_NO;
			
		} else {
			
			mLoopState = LoopState.LOOP_ONE;
			
		}
		
	}
	
	public LoopState getLoopState() {
		
		return mLoopState;
		
	}
	

	//
	// Shuffling
	// 
	// To avoid altering the structure of the playlist Cursor, we generate 
	// a list of numbers where the numbers represent the indices of songs
	// in the cursor.
	//
	// For shuffling, we generate a seed number whenever shuffle is turned on
	// That seed number is used by the algorithm to 
	//
	
	private long shuffleRandomNumberSeed = -1;
	
	public void setShuffle( boolean shouldShuffle ) {
		
		isShuffling = shouldShuffle;
		
		if ( !isShuffling ) {
			
			if ( null != Playlist
					&& !Playlist.isBeforeFirst()
					&& !Playlist.isAfterLast() ) {
			
				//playlist.moveToPosition( shufflePlaylist[ playlist.getPosition() ] );
				setPlaylistPosition( Playlist.getPosition() );
				
			}
		
			//shufflePlaylist = null;
		
		} else {
			
			if ( null != Playlist ) {
				
				//generateRandomPlaylist();
				generateRandomSeed();
				
			}
		
		}
	

	}
	
	private void generateRandomSeed() {
		
		Random gen = new Random( System.currentTimeMillis() );
		
		shuffleRandomNumberSeed = gen.nextLong();
		
	}
	
	private int getNthInt( int n ) {
		
		Random mRandom = new Random( shuffleRandomNumberSeed );
		
		int mSize = Playlist.getCount();
		
		int value = 0;
		
		for ( int i = 0; i < n; i++ ) {
			
			value = mRandom.nextInt( mSize + 1 );
			
		}
		
		return value;
		
	}
	
	//
	// Responding to Hardware Events
	// 
	// There are some hardware conditions where we want the media player to emergency stop/pause
	// This includes:
	// receiving a phone call, headphones being unplugged, and the SD card being disconnected
	// 
	
	private IntentFilter hardwareStopIntents = new IntentFilter();
	
	private BroadcastReceiver HardwareStopReceiver = new BroadcastReceiver() {
		
		@Override public void onReceive( Context context, Intent intent ) {
			
			// android.intent.action.HEADSET_PLUG when headphones are plugged in or out
			
			// Shut down when either the media is ejected or when the headphones are unplugged
			
			pause();
			
		}
		
	};
	
	PhoneStateListener phoneListener = new PhoneStateListener() {
		
		public void onCallStateChanged( int state, String incomingNumber ) {
			
			if ( state == TelephonyManager.CALL_STATE_OFFHOOK || state == TelephonyManager.CALL_STATE_RINGING ) {
				
				pause();
				
			}
			
		}
		
	};
	
}

