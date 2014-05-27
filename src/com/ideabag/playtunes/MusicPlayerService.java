package com.ideabag.playtunes;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
//import android.util.Log;
import android.widget.RemoteViews;
import android.media.MediaPlayer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;


public class MusicPlayerService extends Service {
	
	private static final String TAG = "PlayTunesMusicPlayerService";
	
	private MediaPlayer mp;
	private int isLooping; // Three states: no loop, loop all, loop one
	private boolean isShuffling, isPlaying;
	
	
	Cursor playlist;
	private int currentCommand;
	private int food = 1; // If the service was created, assume 1
	
	//private RemoteViews contentView; // Used for custom display of notification
	//private Notification notification; // The individual notification
	private int[] shufflePlaylist = null;
	
	PowerManager pm;
	PowerManager.WakeLock wl;
	
	private static final int PLAY_NOTIFICATION_ID = 1;
	//private static final int PAUSE_NOTIFICATION_ID = 2;
	
	PhoneStateListener phoneListener = new PhoneStateListener() {
		
		public void onCallStateChanged( int state, String incomingNumber ) {
			
			if ( state == TelephonyManager.CALL_STATE_OFFHOOK || state == TelephonyManager.CALL_STATE_RINGING ) {
				
				pause();
				
			}
			
		}
		
	};
	
	// android.intent.action.HEADSET_PLUG when headphones are plugged in or out
	public BroadcastReceiver MusicPlayerServiceReceiver = new BroadcastReceiver() {
		
		@Override public void onReceive( Context context, Intent intent ) {
			
			String action = intent.getAction();
			
			if ( action.equals( Intent.ACTION_MEDIA_EJECT )
					|| action.equals( Intent.ACTION_MEDIA_UNMOUNTED )
					|| action.equals( Intent.ACTION_MEDIA_REMOVED ) ) {
				
				stopSelf();
				
			} else if ( action.equals( getString( R.string.action_play ) ) ) {
				
				loadCommand( intent.getIntExtra( "command", -1 ) );
				
				if (intent.hasExtra("track") && intent.getIntExtra("track", -1) != -1) {
					
					setPosition(intent.getIntExtra("track", 0));
					
				}
				
				play();
				
			} else if ( action.equals( getString( R.string.action_next ) ) ) {
				
				next();
				
			} else if ( action.equals( getString( R.string.action_prev ) ) ) {
				
				prev();
				
			} else if ( action.equals( getString( R.string.action_pause ) ) ) {
				
				pause();
				
			} else if ( action.equals( getString( R.string.action_shuffle ) ) ) {
				
				Log.i(TAG, "Shuffle received.");
				setShuffle( intent.getBooleanExtra( "shuffle", false ) );
				
			} else if ( action.equals( getString( R.string.action_repeat ) ) ) {
				
				setRepeat( intent.getIntExtra( "repeat", getResources().getInteger( R.integer.looping_no ) ) );
				
			} else if ( action.equals( getString( R.string.action_update ) ) ) {
				
				updateClient();
				
			} else if ( action.equals( "com.ideabag.playtunes.RELEASE" ) ) {
				
				Log.i( TAG, "Received RELEASE broadcast." );
				releaseFood();
				
			} else if ( action.equals( "com.ideabag.playtunes.CONSUME" ) ) {
				
				Log.i( TAG, "Received CONSUME broadcast." );
				consumeFood();
				
			}
			
		}
		
	};
	
	MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
		
		public void onCompletion( MediaPlayer mp ) {
			
			if ( isLooping == getResources().getInteger( R.integer.looping_one ) ) {// MediaPlayer will repeat, do nothing not even update the client
				
				return;
				
			} else if ( isLooping == getResources().getInteger( R.integer.looping_all ) ) {
				
				setPosition( ( playlist.getPosition() + 1 ) % playlist.getCount() ); // Will always loop the whole thing
				
			} else {
				
				setPosition( playlist.getPosition() + 1 );
				
			}
			
		}
		
	};
	
	private void loadCommand(int cmd) {
		
		if ( cmd == currentCommand ) {
			
			return;
			
		}
		
		currentCommand = cmd;
		
		playlist = MusicUtils.getCursor( getBaseContext(), cmd );
		
		if ( isShuffling ) {
			
			if ( playlist != null ) {
			
				generateRandomPlaylist();
				
			}
			
		}
		
	}
	
	private void next() {
		
		if ( null != playlist) {
			
			setPosition( playlist.getPosition() + 1 );
			
		}
		
	}
	
	private void play() {
		
		if ( null != playlist
				&& !playlist.isBeforeFirst()
				&& !playlist.isAfterLast() ) {
			
			// TODO: Take food when starting to play
			if ( !isPlaying ) {
				
				consumeFood();
				
			}
				
			mp.start();
			isPlaying = true;
			playNotification();
			updateClient();
			
			if ( !wl.isHeld() ) {
				
				wl.acquire(); // TODO: Wake lock acquired
				
			}
			
		}
		
	}
	
	private void pause() {
		
		if ( null != mp ) {
			
			if ( mp.isPlaying() ) {
				
				mp.pause();
				
			}
			
			removeNotification();
			updateClient();
			isPlaying = false;
			// TODO: Release food when no longer playing
			releaseFood();
			
			if ( wl.isHeld() ) {
				
				wl.release(); // TODO: Wake lock released
				
			}
			
		}
		
	}
	
	private void prev() {
		
		if (null != playlist) {
			
			setPosition( playlist.getPosition() - 1 );
			
		}
		
	}
	
	private void setPosition( int position ) {
		
		try {
			
			playlist.moveToPosition(position);
			if (playlist.isBeforeFirst() || playlist.isAfterLast()) {
				shufflePlaylist = null;
				pause();
				return;
			}
			
			if (isShuffling && shufflePlaylist == null)
				generateRandomPlaylist();
			
			mp.reset();
			
			if ( !isShuffling ) {
				
				mp.setDataSource( playlist.getString( playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.DATA ) ) );
				
			} else {
				
				int tmp = playlist.getPosition();
				
				playlist.moveToPosition( shufflePlaylist[ tmp ] );
				
				mp.setDataSource( playlist.getString( playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.DATA ) ) );
				
				playlist.moveToPosition( tmp );
				
			}
			
			mp.prepare();
			
			if ( isPlaying ) {
				
				play();
				
			}
			
			updateClient();
			
		} catch ( Exception e ) {
			
			e.printStackTrace();
			
		}
		
	}
	
	private void setRepeat(int repeat) {
		
		if ( repeat != getResources().getInteger( R.integer.looping_one ) ) {
			
			mp.setLooping(false);
			
		} else {
			
			mp.setLooping(true);
			
		}
		
		isLooping = repeat;
		
	}
	
	private void setShuffle( boolean s ) {
		
		isShuffling = s;
		
		if ( !isShuffling ) {
			
			if ( null != playlist
					&& !playlist.isBeforeFirst()
					&& !playlist.isAfterLast() ) {
				
				playlist.moveToPosition( shufflePlaylist[ playlist.getPosition() ] );
				
			}
			
			shufflePlaylist = null;
			
		} else {
			
			if ( null != playlist
					&& null == shufflePlaylist ) {
				
				generateRandomPlaylist();
				
			}
			
		}
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mp = new MediaPlayer();
		mp.setOnCompletionListener( completionListener );
		
		Log.i( "PlayTunesService", "Service created." );
		currentCommand = -1; // Restore the previous command from the prefs file
		
		//notificationIntent.putExtra("command", getResources().getInteger(R.integer.now_playing_code));
		//PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(getBaseContext(), PlayTunes.class), PendingIntent.FLAG_UPDATE_CURRENT);

		
		( ( TelephonyManager ) getBaseContext().getSystemService( Context.TELEPHONY_SERVICE ) ).listen( phoneListener, PhoneStateListener.LISTEN_CALL_STATE );
		pm = ( PowerManager ) getSystemService( Context.POWER_SERVICE );
		wl = pm.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK, TAG );
		
		IntentFilter intfil = new IntentFilter();
		intfil.addAction( Intent.ACTION_MEDIA_EJECT );
		intfil.addAction( Intent.ACTION_MEDIA_UNMOUNTED );
		intfil.addAction( Intent.ACTION_MEDIA_REMOVED );
		intfil.addAction( getString( R.string.action_play ) );
		intfil.addAction( getString( R.string.action_pause ) );
		intfil.addAction( getString( R.string.action_next ) );
		intfil.addAction( getString( R.string.action_prev ) );
		intfil.addAction( getString( R.string.action_repeat ) );
		intfil.addAction( getString( R.string.action_shuffle ) );
		intfil.addAction( getString( R.string.action_update ) );
		intfil.addAction( "com.ideabag.playtunes.RELEASE" );
		intfil.addAction( "com.ideabag.playtunes.CONSUME" );
		registerReceiver( MusicPlayerServiceReceiver, intfil);
		
	}
	
	private void playNotification() {
		
		Notification notification = new Notification( R.drawable.ic_stat_notify_play, null, System.currentTimeMillis() );
		Intent openIntent = new Intent( getBaseContext(), PlayTunes.class );
		
		openIntent.putExtra( "command", currentCommand );
		
		PendingIntent contentIntent = PendingIntent.getActivity( getBaseContext(), 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT );
		
		if ( isShuffling ) {
			
			int tmp = playlist.getPosition();
			playlist.moveToPosition( shufflePlaylist[ tmp ] );
			notification.setLatestEventInfo( getApplicationContext(), playlist.getString( playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) ), playlist.getString( playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) ), contentIntent );
			playlist.moveToPosition( tmp );
			
		} else {
			
			notification.setLatestEventInfo( getApplicationContext(), playlist.getString( playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) ), playlist.getString( playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) ), contentIntent );
			
		}
		
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		
		( ( NotificationManager ) getSystemService( Context.NOTIFICATION_SERVICE ) ).notify( PLAY_NOTIFICATION_ID, notification );
		
	}
	
	private void removeNotification() {
		
		( ( NotificationManager ) getSystemService( Context.NOTIFICATION_SERVICE ) ).cancel( PLAY_NOTIFICATION_ID) ;
		
	}
	
	private void updateClient() {
		
		Intent clientAlert = new Intent();
		clientAlert.setAction( PlayTunes.PLAYTUNES_UI_UPDATE );
		String albumURI = "";
		Cursor albumArtCursor;
		int tmp;
		
		if ( null != playlist 
				&& !playlist.isBeforeFirst()
				&& !playlist.isAfterLast() ) {
			
			tmp = playlist.getPosition();
			
			if ( isShuffling && null != shufflePlaylist ) {
				
				playlist.moveToPosition( shufflePlaylist[ tmp ] );
				
			}
			
			clientAlert.putExtra( "songName", playlist.getString( playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) ) );
			clientAlert.putExtra( "songArtist", playlist.getString( playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) ) );
			clientAlert.putExtra( "songAlbum", playlist.getString( playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) ) );
			
			try {
				
				albumArtCursor = getBaseContext().getContentResolver().query( MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, 
					new String[] { MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums._ID}, 
					MediaStore.Audio.Albums._ID + "=" + playlist.getString( playlist.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM_ID ) ), null, null);
				
				if ( albumArtCursor.moveToFirst() ) {
					
					albumURI = albumArtCursor.getString( albumArtCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM_ART ) );
					
				}
				
			} catch ( Exception e ) {
				
				e.printStackTrace();
				
			}
			
			clientAlert.putExtra("songAlbumArt", albumURI);
			playlist.moveToPosition(tmp); // This is done to ensure that the correct info is displayed while shuffling
		
		} else {
			
			clientAlert.putExtra( "songName", "" );
			clientAlert.putExtra( "songArtist", "" );
			clientAlert.putExtra( "songAlbum", "" );
			clientAlert.putExtra( "songAlbumArt", "" );
			
		}
		
		clientAlert.putExtra( "shuffle", isShuffling );
		clientAlert.putExtra( "loop", isLooping );
		clientAlert.putExtra( "isPlaying", mp.isPlaying() );
		
		sendBroadcast( clientAlert );
		
	}
	
	/* This function creates a randomly ordered non-repeating array of integers.
	 * The function of this array is to tell the player what order
	 * to play the songs in when in shuffle/random.
	 * The current track is used in this method so that the currently playing song
	 * is the top of the list.
	 */
	private void generateRandomPlaylist() {
		
		int size = playlist.getCount();
		Random gen = new Random();
		ArrayList< Integer > numbers = new ArrayList< Integer >( size );
			
		shufflePlaylist = new int[ size ];
		
		for ( int x = 0; x < size; x++ ) {
			
			numbers.add( x, new Integer( x ) );
			
		}
		
// Set the current song to the first of the playlist
		if ( !playlist.isFirst() && !playlist.isAfterLast() ) {
			
			shufflePlaylist[ 0 ] = playlist.getPosition();
			
			numbers.remove( playlist.getPosition() );
			playlist.moveToFirst();
			
			for ( int x = 1; x < size; x++ ) {
				
				int pick = gen.nextInt( numbers.size() );
				
				shufflePlaylist[ x ] = numbers.get( pick ).intValue();
				numbers.remove( pick );
				
			}
			
		} else {
			
			for ( int x = 0; x < size; x++ ) {
				
				int pick = gen.nextInt( numbers.size() );
				shufflePlaylist[ x ] = numbers.get( pick ).intValue();
				numbers.remove( pick );
				
			}
			
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		mp.reset();
		mp.release();
		mp = null;
		
		if ( wl.isHeld() ) {
			
			wl.release(); // TODO: Wake lock released
			
		}
		
		unregisterReceiver( MusicPlayerServiceReceiver );
		removeNotification();
		
		Log.i(TAG, "Service destroyed.");
		
	}
	
	private void consumeFood() {
		
		food++;
		
		Log.i(TAG, "Service consumed food. Count: "+food);
		
	}
	
	private void releaseFood() {
		
		food--;
		Log.i(TAG, "Service released food. Count:"+food);
		
		if (food == 0) {
			
			stopSelf();
			
		}
		
	}
	
	@Override
	public IBinder onBind( Intent intent ) {
		return null;
	}
}