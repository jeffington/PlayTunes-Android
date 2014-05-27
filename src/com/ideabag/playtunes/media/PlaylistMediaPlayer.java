
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
	
	private LoopState loopingState; // Three states: no loop, loop all, loop one
	private boolean isShuffling, isPlaying;
	
	private Cursor Playlist;
	private Context mContext;
	
	PowerManager pm;
	PowerManager.WakeLock wl;
	
	public PlaylistMediaPlayer( Context context ) {
		super();
		
		( ( TelephonyManager ) mContext.getSystemService( Context.TELEPHONY_SERVICE ) ).listen( phoneListener, PhoneStateListener.LISTEN_CALL_STATE );
		
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
			
			if ( loopingState == LoopState.LoopOne ) {// MediaPlayer will repeat, do nothing not even update the client
				
				return;
				
			} else if ( loopingState == LoopState.LoopAll ) {
				
				setPosition( ( Playlist.getPosition() + 1 ) % Playlist.getCount() ); // Will always loop the whole thing
				
			} else {
				
				setPosition( Playlist.getPosition() + 1 );
				
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
			
			// TODO: Take food when starting to play
			if ( !isPlaying ) {
				
				//consumeFood();
				
			}
				
			start();
			isPlaying = true;
			//playNotification();
			//updateClient();
			/*
			if ( !wl.isHeld() ) {
				
				wl.acquire(); // TODO: Wake lock acquired
				
			}
			*/
		}
		
	}
	
	public void pause() {
		
			
			if ( isPlaying() ) {
				
				super.pause();
				
			}
			
			//removeNotification();
			//updateClient();
			isPlaying = false;
			// TODO: Release food when no longer playing
			/*
			releaseFood();
			
			if ( wl.isHeld() ) {
				
				wl.release(); // TODO: Wake lock released
				
			}
			*/
		
		
	}
	
	public void previousTrack() {
		
		if ( null != Playlist ) {
			
			setPosition( Playlist.getPosition() - 1 );
			
		}
		
	}
	
	public void nextTrack() {
		
		if ( null != Playlist ) {
			
			setPosition( Playlist.getPosition() + 1 );
			
		}
		
	}
	
	// 
	// Abstracting out collecting information on the current song
	// 
	// 
	// 
	
	public class SongInformation {
		
		private String NAME;
		private String ARTIST;
		private String ALBUM;
		private String GENRE;
		
		
	}
	
	private SongInformation currentSong = null;
	
	public SongInformation getCurrentSongInformation() {
		
		return currentSong;
		
	}
	
	
	// 
	// Private Methods
	// 
	// 
	
	private void setPosition( int position ) {
	
		try {
			
			Playlist.moveToPosition(position);
			
			if (Playlist.isBeforeFirst() || Playlist.isAfterLast()) {
				//shufflePlaylist = null;
				pause();
				return;
			}
			
			if ( isShuffling ) {
				//TODO:
				//generateRandomPlaylist();
				
			}
			
			reset();
			
			if ( !isShuffling ) {
				
				setDataSource( Playlist.getString( Playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.DATA ) ) );
				
			} else {
				
				int tmp = Playlist.getPosition();
				
				Random random = new Random( shuffleRandomNumberSeed );
				
				Playlist.moveToPosition( random.nextInt( tmp ) );
				
				setDataSource( Playlist.getString( Playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.DATA ) ) );
				
				Playlist.moveToPosition( tmp );
				
			}
			
			prepare();
			
			if ( isPlaying ) {
				//TODO:
				//play();
				
			}
			
			//clientAlert.putExtra( "songName", playlist.getString( playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) ) );
			//clientAlert.putExtra( "songArtist", playlist.getString( playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) ) );
			//clientAlert.putExtra( "songAlbum", playlist.getString( playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) ) );
			
/*
		try {
			
			albumArtCursor = getBaseContext().getContentResolver().query(
					MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, 
					new String[] {
							MediaStore.Audio.Albums.ALBUM_ART,
							MediaStore.Audio.Albums._ID
					}, 
					MediaStore.Audio.Albums._ID + "=" + playlist.getString( playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM_ID ) ),
					null,
					null
					);
			
			if ( albumArtCursor.moveToFirst() ) {
				
				//albumURI = albumArtCursor.getString( albumArtCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM_ART ) );
				
			}
			
		} catch ( Exception e ) {
			
			e.printStackTrace();
			
		}
		*/
		//clientAlert.putExtra("songAlbumArt", albumURI);
		//playlist.moveToPosition(tmp);
			
			//updateClient();
			
		} catch ( Exception e ) {
			
			e.printStackTrace();
		
		}
	
	}
	
	
	// 
	// Looping / Repeating
	//
	// The standard media player has a built-in loop function, but it only loops the currently loaded song
	// To represent a looping playlist, we have a third state called LoopState.LoopAll
	// 
	
	public enum LoopState {
		NoLoop,
		LoopAll,
		LoopOne
	};
	
	public void setLoopState( LoopState state ) {
		
		
		if ( state != LoopState.LoopOne ) {
			
			setLooping( false );
			
		} else {
			
			setLooping( true );
			
		}
		
		loopingState = state;
		
	}
	
	public LoopState getLoopState() {
		
		return loopingState;
		
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
	
	private long shuffleRandomNumberSeed = -1;
	
	public void setShuffle( boolean shouldShuffle ) {
		
		isShuffling = shouldShuffle;
		
		if ( !isShuffling ) {
			
			if ( null != Playlist
					&& !Playlist.isBeforeFirst()
					&& !Playlist.isAfterLast() ) {
			
				//playlist.moveToPosition( shufflePlaylist[ playlist.getPosition() ] );
				setPosition( Playlist.getPosition() );
				
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
		
		Random gen = new Random();
		
		shuffleRandomNumberSeed = gen.nextLong();
		
	}
	
	
	//
	// Responding to Hardware
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

