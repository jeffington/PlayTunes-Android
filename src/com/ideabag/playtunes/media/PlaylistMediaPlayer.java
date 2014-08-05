
//
// CustomMediaPlayer adds looping, shuffling, and playlist handling
//

package com.ideabag.playtunes.media;


import java.util.Random;

import com.ideabag.playtunes.database.MediaQuery;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.provider.MediaStore;

public class PlaylistMediaPlayer {
	
	public static final String TAG = "PlaylistMediaPlayer";
	
	protected boolean isShuffling, isPlaying;
	
	protected Cursor mPlaylistCursor;
	
	// Local copy of the playlist Cursor that we keep in case the cursor is closed or changed
	protected MediaQuery mMediaQuery = null;
	
	protected int mPlaylistPosition = -1;
	protected int mPlaylistSize = -1;
	
	protected boolean isPrepared = false;
	
	private int[] mShuffledPlaylist;
	
	protected LoopState mLoopState;
	
	protected MediaPlayer mMediaPlayer;
	
	private boolean isDucking = false;
	
	
	private PlaybackListener PlaybackChanged = null;
	
	protected Context mContext;
	
	private long shuffleRandomNumberSeed = -1;
	
	private PowerManager pm;
	private PowerManager.WakeLock wl;
	
	//
	//
	//
	
	public interface PlaybackListener {
		
		void onTrackChanged( String media_id );
		
		void onPlay( int playbackPositionMilliseconds );
		
		void onPause( int playbackPositionMilliseconds );
		
		void onPlaylistDone();
		
		void onLoopingChanged( LoopState loop );
		
		void onShuffleChanged( boolean isShuffling );
		
	}
	
	public enum LoopState {
		LOOP_NO,
		LOOP_ALL,
		LOOP_ONE
	};
	
	public PlaylistMediaPlayer( Context context ) {
		super();
		
		//( ( TelephonyManager ) context.getSystemService( Context.TELEPHONY_SERVICE ) ).listen( phoneListener, PhoneStateListener.LISTEN_CALL_STATE );
		
		mContext = context;
		
		
		mMediaPlayer = new MediaPlayer();
		
		mMediaPlayer.setOnCompletionListener( loopOnCompletionListener );
		mMediaPlayer.setOnPreparedListener( onPreparedListener );
		
		mLoopState = LoopState.LOOP_NO;
		
		// 
		// Intents for loss of connection to media
		// 
		//hardwareStopIntents.addAction( Intent.ACTION_MEDIA_EJECT );
		//hardwareStopIntents.addAction( Intent.ACTION_MEDIA_UNMOUNTED );
		//hardwareStopIntents.addAction( Intent.ACTION_MEDIA_REMOVED );
		
		//mContext.registerReceiver(HardwareStopReceiver, hardwareStopIntents);
		
		pm = ( PowerManager ) mContext.getSystemService( Context.POWER_SERVICE );
		wl = pm.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK, TAG );
		
	}
	
	public void setPlaybackListener( PlaybackListener pbl ) {
		
		this.PlaybackChanged = pbl;
		
	}
	
	
	MediaPlayer.OnCompletionListener loopOnCompletionListener = new MediaPlayer.OnCompletionListener() {
		
		public void onCompletion( MediaPlayer mp ) {
			
			
			
			if ( null != mPlaylistCursor ) {
				
				
				
				if ( mLoopState == LoopState.LOOP_ALL ) {
					
					//android.util.Log.i( TAG, "LOOP_ALL");
					
					setPlaylistPosition( ( mPlaylistPosition + 1 ) % mPlaylistSize ); // Will always loop the whole thing
					
				} else if ( mLoopState == LoopState.LOOP_NO ) {
					
					//android.util.Log.i( TAG, "LOOP_NO");
					
					setPlaylistPosition( mPlaylistPosition + 1 );
					
				} else if ( mLoopState == LoopState.LOOP_ONE ) { // We don't need to change the media on LOOP_ONE, but we need to alert the client
					
					//android.util.Log.i( TAG, "LOOP_ONE");
					
					setPlaylistPosition( mPlaylistPosition );
					
				}
				
			}
			
		}
		
	};
	
	MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
		
		@Override public void onPrepared( MediaPlayer mp ) {
			
			isPrepared = true;
			
			if ( isPlaying ) {
				
				if ( null != PlaybackChanged ) {
					
					PlaybackChanged.onPlay( 0 );
				
				}
				
				mp.start();
				
			} else {
				
				if ( null != PlaybackChanged ) {
					
					PlaybackChanged.onPause( 0 );
				
				}
				
			}
			
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
		
		mMediaPlayer.reset();
		mMediaPlayer.release();
		
		if ( wl.isHeld() ) {
			
			wl.release();
			
		}
		
		//mContext.unregisterReceiver( HardwareStopReceiver );
		
	}
	
	public void setPlaylistQuery( MediaQuery query ) {
		
		if ( null == mMediaQuery || !mMediaQuery.equals( query ) ) {
			
			mMediaQuery = query;
			
			if ( mPlaylistCursor != null && !mPlaylistCursor.isClosed() ) {
				
				mPlaylistCursor.close();
				
			}
			
			mPlaylistCursor = MediaQuery.execute( mContext, mMediaQuery );
			
			//mPlaylistCursor.registerDataSetObserver( mPlaylistCursorObserver );
			
			mPlaylistSize = mPlaylistCursor.getCount();
			mPlaylistPosition = 0;
			
			if ( isShuffling ) {
				
				generateShuffledPlaylist();
				
			}
			
		}
		
	}
	
	public boolean isPlaying() {
		
		return isPlaying;
		
	}
	
	public void play() {
		
		if ( null != mPlaylistCursor
				&& !mPlaylistCursor.isBeforeFirst()
				&& !mPlaylistCursor.isAfterLast() ) {
			
			isPlaying = true;
			
			if ( null != PlaybackChanged ) {
				
				PlaybackChanged.onPlay( mMediaPlayer.getCurrentPosition() );
				
			}
			
			if ( isPrepared ) {
				
				mMediaPlayer.start();
				
			}
			
			if ( !wl.isHeld() ) {
				
				wl.acquire(); // Wake lock acquired
				
			}
			
		}
		
	}
	
	public void pause() {
		
		if ( mMediaPlayer.isPlaying() ) {
			
			isPlaying = false;
			
			mMediaPlayer.pause();
			
			if ( null != PlaybackChanged ) {
				
				PlaybackChanged.onPause( mMediaPlayer.getCurrentPosition() );
				
			}
			
		}
		
	}
	
	public void back() {
		
		if ( null != mPlaylistCursor && null != mMediaPlayer ) {
			
			int position = mMediaPlayer.getCurrentPosition();
			int duration = mMediaPlayer.getDuration();
			
			if ( position >= ( 0.08 * duration ) ) {
				
				setSeekPosition( 0 );
				
			} else {
				
				previousTrack();
				
			}
			
		}
		
	}

	public void previousTrack() {
		
		if ( null != mPlaylistCursor ) {
			
			setPlaylistPosition( mPlaylistPosition - 1 );
			
		}
		
	}
	
	
	public void nextTrack() {
		
		if ( null != mPlaylistCursor ) {
			
			setPlaylistPosition( mPlaylistPosition + 1 );
			
		}
		
	}
	
	// 
	// Abstracting out collecting information on the current song
	// 
	// NOTE: PlaylistMediaPlayer expects column 0 of the Cursor to be the ID of the media
	// 
	
	public boolean isPlaylistReady() {
		
		return !( mPlaylistCursor == null
				|| mPlaylistPosition < 0 || mPlaylistPosition >= mPlaylistSize 
				//|| mPlaylistCursor.isAfterLast()
				//|| mPlaylistCursor.isBeforeFirst()
				|| mPlaylistCursor.isClosed()
				);
		
	}
	
	public String getCurrentMediaID() {
		
		String media_id = null;
		
		if ( isPlaylistReady() ) {
			
			if ( isShuffling ) {
				
				int shufflePosition = mShuffledPlaylist[ mPlaylistPosition ];
				
				mPlaylistCursor.moveToPosition( shufflePosition );
				
				media_id = mPlaylistCursor.getString( 0 );
				
			} else {
				
				mPlaylistCursor.moveToPosition( mPlaylistPosition );
				
				media_id = mPlaylistCursor.getString( 0 );
				
			}
			
		}
		
		return media_id;
		
	}
	
	public int getTrackPlaybackPosition() {
		
		return mMediaPlayer.getCurrentPosition();
		
	}
	
	public void setPlaylistPosition( int position ) {
		
		try {
			
			mPlaylistPosition = position;
			
			// Is the new position out of bounds?
			if ( mPlaylistPosition < 0 || mPlaylistPosition >= mPlaylistSize) {
				
				// Out of bounds, but looping, so we bring the position back
				if ( mLoopState == LoopState.LOOP_ALL ) {
					
					mPlaylistPosition = mPlaylistPosition % mPlaylistSize;
					
				} else { // Not looping and out of bounds
					
					pause();
					
					// Send stopped event
					
					if ( null != PlaybackChanged ) {
						
						PlaybackChanged.onPlaylistDone();
						
					}
					
					return;
					
				}
				
			}
			
			
			//
			// NOTE: Calling reset clears the looping, this only affects the LOOP_ONE state.
			// Nonetheless, we need to set the loop state again after calling reset()
			// 
			
			mMediaPlayer.reset();
			isPrepared = false;
			//setLooping( mLoopState );
			
			if ( !isShuffling ) {
				
				mPlaylistCursor.moveToPosition( mPlaylistPosition );
				
				mMediaPlayer.setDataSource( mPlaylistCursor.getString( mPlaylistCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.DATA ) ) );
				
				if ( null != PlaybackChanged ) {
					
					PlaybackChanged.onTrackChanged( getCurrentMediaID() );
					
				}
				
			} else { // Shuffle!
				
				int nextInt = mShuffledPlaylist[ mPlaylistPosition ];
				
				mPlaylistCursor.moveToPosition( nextInt );
				
				mMediaPlayer.setDataSource( mPlaylistCursor.getString( mPlaylistCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.DATA ) ) );
				
				if ( null != PlaybackChanged ) {
					
					PlaybackChanged.onTrackChanged( getCurrentMediaID() );
					
				}
				
			}
			
			mMediaPlayer.prepareAsync();
			
		} catch ( Exception e ) {
			
			e.printStackTrace();
		
		}
	
	}
	
	public void setSeekPosition( int msecs ) {
		
		if ( this.isPlaying && mMediaPlayer.isPlaying() ) {
			
			mMediaPlayer.pause();
			
		}
		
		mMediaPlayer.seekTo( msecs );
		
		if ( isPlaying ) {
			
			play();
			
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
	
	public void setLooping( LoopState state ) {
		
		mLoopState = state;
		/*
		if ( state != LoopState.LOOP_ONE ) {
			
			mMediaPlayer.setLooping( false );
			
		} else {
			
			mMediaPlayer.setLooping( true );
			
		}
		*/
		if ( null != PlaybackChanged ) {
			
			PlaybackChanged.onLoopingChanged( mLoopState );
			
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
	
	
	// 
	// 
	// Updated: Should not change the play position when turning shuffle on or off
	// 
	public void setShuffle( boolean shouldShuffle ) {
		
		isShuffling = shouldShuffle;
		
		if ( isShuffling ) {
			
			generateShuffledPlaylist();
			
		} else {
			
			mShuffledPlaylist = null;
			
		}
		
		if ( null != PlaybackChanged ) {
			
			PlaybackChanged.onShuffleChanged( shouldShuffle );//( mMediaPlayer.getCurrentPosition() );
			
		}
	
	}
	
	public boolean isShuffling() {
		
		return isShuffling;
		
	}
	
	private void generateShuffledPlaylist() {
		
		int size = mPlaylistSize;
		
		mShuffledPlaylist = new int[ mPlaylistSize ];
		
		for ( int i = 0; i < size; i++ ) {
			
			mShuffledPlaylist[ i ] = i;
			
		}
		
		shuffleArray( mShuffledPlaylist );
		
	}
	
	// 
	// Implementing FisherÐYates shuffle
	void shuffleArray( int[] ar ) {
		
	    Random rnd = new Random();
	    
	    for ( int i = ar.length - 1; i > 0; i-- ) {
	    	
	    	if ( i == mPlaylistPosition) {
	    		
	    		continue;
	    		
	    	}
	    	
	    	int index = rnd.nextInt( i + 1 );
	    	
	    	while ( index == mPlaylistPosition) {
	    		
	    		index = rnd.nextInt( i + 1 );
	    		
	    	}
	    	
	    	// Simple swap
	    	int a = ar[ index ];
	    	ar[ index ] = ar[ i ];
	    	ar[ i ] = a;
	    	
	    }
	    
	}
	
// Volume Ducking
	
	public void startVolumeDucking() {
		
		if ( !isDucking ) {
			
			mMediaPlayer.setVolume( 0.4f, 0.4f );
			
			isDucking = true;
			
		}
		
	}
	
	public void stopVolumeDucking() {
		
		if ( isDucking ) {
			
			mMediaPlayer.setVolume( 1.0f, 1.0f );
			
		}
		
	}
	
	
}

