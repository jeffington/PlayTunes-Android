package com.ideabag.playtunes.activity;

import com.ideabag.playtunes.MusicPlayerService;
import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.dialog.AddToPlaylistDialogFragment;
import com.ideabag.playtunes.fragment.TrackProgressFragment;
import com.ideabag.playtunes.media.PlaylistMediaPlayer;
import com.ideabag.playtunes.media.PlaylistMediaPlayer.LoopState;
import com.ideabag.playtunes.util.AdmobUtil;
import com.ideabag.playtunes.util.TrackerSingleton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class NowPlayingActivity extends ActionBarActivity {
	
	public static final String TAG = "Now Playing Activity";
	
	public boolean mIsBound = false;
	public MusicPlayerService mBoundService;
	
	private PlaylistManager mPlaylistManager;
	
	private TrackProgressFragment mProgressFragment;
	
	private boolean isPlaying = false;
	//private int currentPlayback = 0;
	
	private String lastAlbumUri = null;
	private AdView adView;
	
	private String current_media_id;
	
	Tracker tracker;
	
	@SuppressLint("NewApi")
	@Override public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_nowplaying );
		
		mPlaylistManager = new PlaylistManager( this );
		
		//ActionBar bar = getSupportActionBar();
		
		//bar.setTitle( "Crosby, Stills, Nash & Young" );
		//bar.setSubtitle( "Now Playing" );
		
		//Bundle extras = getIntent().getExtras();
		
        ActionBar supportBar = getSupportActionBar();
        
		//supportBar.setTitle( extras.getCharSequence( "title", "Now Playing" ) );
        
        supportBar.setSubtitle( "Now Playing" );
        
        supportBar.setIcon( android.R.color.transparent );
        //supportBar.setLogo( R.drawable.ic_action_cancel );
        //supportBar.setDisplayShowCustomEnabled( true );
        supportBar.setDisplayShowHomeEnabled( true );
        supportBar.setDisplayHomeAsUpEnabled( true );
        supportBar.setHomeButtonEnabled( true );
        //supportBar.setDisplayUseLogoEnabled( true );
		
		findViewById( R.id.NowPlayingPrevButton ).setOnClickListener( controlsClickListener );
		findViewById( R.id.NowPlayingPlayPauseButton ).setOnClickListener( controlsClickListener );
		findViewById( R.id.NowPlayingNextButton ).setOnClickListener( controlsClickListener );
		findViewById( R.id.StarButton ).setOnClickListener( controlsClickListener );
		findViewById( R.id.MenuButton ).setOnClickListener( controlsClickListener );
		findViewById( R.id.NowPlayingShuffleButton ).setOnClickListener( controlsClickListener );
		findViewById( R.id.NowPlayingRepeatButton ).setOnClickListener( controlsClickListener );
		
		
		mProgressFragment = ( TrackProgressFragment ) getSupportFragmentManager().findFragmentById( R.id.TrackProgressFragment );
		
		// Show Playlist
		
		
		// Set up ad banner
		
		adView = new AdView( this );
	    adView.setAdSize( AdSize.BANNER );
	    adView.setAdUnitId( getString( R.string.admob_unit_id_nowplaying_activity_id ) );
		
		FrameLayout adFrame = ( FrameLayout ) findViewById( R.id.NowPlayingAdContainer );
		adFrame.addView( adView );
	    
		Builder adRequestBuilder = new AdRequest.Builder().addTestDevice( AdRequest.DEVICE_ID_EMULATOR );
	    AdmobUtil.AddTestDevices( this, adRequestBuilder );
	    
	    //adRequestBuilder.
	    
	    AdRequest adRequest = adRequestBuilder.build();
		
		
		// Start loading the ad in the background.
		adView.loadAd(adRequest);
		
		doBindService();
		
		tracker = TrackerSingleton.getDefaultTracker( this );
		
		//Intent nextTest = new Intent( MusicPlayerService.ACTION_NEXT );
		//nextTest.setAction(  );
		
		//this.sendBroadcast( nextTest );
		
		//android.util.Log.i( TAG, "Sent broadcast" );
		
	}
	
	// Connect and disconnect from the player service
	
	@Override public void onStart() {
		super.onStart();
		
		if ( mIsBound && mBoundService != null ) {
			
			mBoundService.addPlaybackListener( mPlaybackListener );
			getSupportActionBar().setTitle( mBoundService.mPlaylistName );
			
		}
		
	}
	
	@Override public void onResume() {
		super.onResume();
		
		//Tracker tracker = TrackerSingleton.getDefaultTracker( this );

	        // Set screen name.
	        // Where path is a String representing the screen name.
		tracker.setScreenName( TAG );
		//t.set( "_count", ""+adapter.getCount() );
		
	        // Send a screen view.
		tracker.send( new HitBuilders.AppViewBuilder().build() );
		
		adView.resume();
		
		
	}
	
	@Override public void onPause() {
		super.onPause();
		
		adView.pause();
		
	}
	
	@Override public void onStop() {
		super.onStop();
		
		if ( mIsBound && mBoundService != null ) {
			
			mBoundService.removePlaybackListener( mPlaybackListener );
			
		}
		
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		doUnbindService();
		
		
	}
	
	View.OnClickListener controlsClickListener = new View.OnClickListener() {
		
		@Override public void onClick( View v ) {
			
			int id = v.getId();
			
			if ( id == R.id.NowPlayingPlayPauseButton ) {
				
				if ( isPlaying ) {
					
					tracker.send( new HitBuilders.EventBuilder()
		        	.setCategory( "now playing button" )
		        	.setAction( "click" )
		        	.setLabel( "pause" )
		        	.build());
					
					mBoundService.pause();
					
				} else {
					
					tracker.send( new HitBuilders.EventBuilder()
		        	.setCategory( "now playing button" )
		        	.setAction( "click" )
		        	.setLabel( "play" )
		        	.build());
					
					mBoundService.play();
					
				}
				
			} else if ( id == R.id.NowPlayingNextButton ) {
				
				tracker.send( new HitBuilders.EventBuilder()
	        	.setCategory( "now playing button" )
	        	.setAction( "click" )
	        	.setLabel( "next" )
	        	.build());
				
				mBoundService.next();
				
			} else if ( id == R.id.NowPlayingPrevButton ) {
				
				tracker.send( new HitBuilders.EventBuilder()
	        	.setCategory( "now playing button" )
	        	.setAction( "click" )
	        	.setLabel( "previous" )
	        	.build());
				
				mBoundService.prev();
				
			} else if ( id == R.id.StarButton ) {
				
				//current_media_id
				ToggleButton starButton = (ToggleButton) v;
				
				if ( starButton.isChecked() ) {
					
					tracker.send( new HitBuilders.EventBuilder()
		        	.setCategory( "now playing button" )
		        	.setAction( "click" )
		        	.setLabel( "star" )
		        	.setValue( 1 )
		        	.build());
					
					mPlaylistManager.addFavorite( current_media_id );
					
				} else {
					
					tracker.send( new HitBuilders.EventBuilder()
		        	.setCategory( "now playing button" )
		        	.setAction( "click" )
		        	.setLabel( "star" )
		        	.setValue( 0 )
		        	.build());
					
					mPlaylistManager.removeFavorite( current_media_id );
					
				}
				
			} else if ( id == R.id.MenuButton ) {
				
				tracker.send( new HitBuilders.EventBuilder()
	        	.setCategory( "now playing button" )
	        	.setAction( "click" )
	        	.setLabel( "song menu" )
	        	.build());
				
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	        	
				Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
				
			    if ( prev != null ) {
			        
			    	ft.remove( prev );
			    	
			    }
				
				AddToPlaylistDialogFragment newFragment = new AddToPlaylistDialogFragment();
				newFragment.setMediaID( current_media_id );
	        	
	            newFragment.show( ft, "dialog" );
				
			} else if ( id == R.id.NowPlayingRepeatButton ) {
				
				String repeatState = (String) v.getTag( R.id.tag_repeat_state );
				
				if ( null == repeatState || repeatState.equals( "0" ) ) {
					
					mBoundService.setLooping( PlaylistMediaPlayer.LoopState.LOOP_ALL );
					
					tracker.send( new HitBuilders.EventBuilder()
		        	.setCategory( "now playing button" )
		        	.setAction( "click" )
		        	.setLabel( "repeat" )
		        	.setValue( 1 )
		        	.build());
					
				} else if ( repeatState.equals( "1" ) ) {
					
					mBoundService.setLooping( PlaylistMediaPlayer.LoopState.LOOP_ONE );
					
					tracker.send( new HitBuilders.EventBuilder()
		        	.setCategory( "now playing button" )
		        	.setAction( "click" )
		        	.setLabel( "repeat" )
		        	.setValue( 2 )
		        	.build());
					
				} else {
					
					mBoundService.setLooping( PlaylistMediaPlayer.LoopState.LOOP_NO );
					
					tracker.send( new HitBuilders.EventBuilder()
		        	.setCategory( "now playing button" )
		        	.setAction( "click" )
		        	.setLabel( "repeat" )
		        	.setValue( 0 )
		        	.build());
					
				}
				
			} else if ( id == R.id.NowPlayingShuffleButton ) {
				
				//ImageButton ib = ( ImageButton ) v;
				
				String shuffleState = (String) v.getTag( R.id.tag_shuffle_state );
				
				if ( null == shuffleState || shuffleState.equals( "0" ) ) {
					
					mBoundService.setShuffle( true );
					
					tracker.send( new HitBuilders.EventBuilder()
		        	.setCategory( "now playing button" )
		        	.setAction( "click" )
		        	.setLabel( "shuffle" )
		        	.setValue( 1 )
		        	.build());
					
				} else {
					
					mBoundService.setShuffle( false );
					
					tracker.send( new HitBuilders.EventBuilder()
		        	.setCategory( "now playing button" )
		        	.setAction( "click" )
		        	.setLabel( "shuffle" )
		        	.setValue( 0 )
		        	.build());
					
				}
				
			}
			
		}
		
	};
	
	@Override public boolean onCreateOptionsMenu( Menu menu ) {
	    
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate( R.menu.nowplaying, menu );
	    
	    return super.onCreateOptionsMenu( menu );
	    
	}
	
    @Override public boolean onOptionsItemSelected( MenuItem item ) {
    	
        if ( item.getItemId() == R.id.NowPlayingShowButton ) {
        	
        	tracker.send( new HitBuilders.EventBuilder()
        	.setCategory( "now playing action button" )
        	.setAction( "click" )
        	.setLabel( "now playing" )
        	.build());
        	
        	//Intent showNowPlaying = new Intent( getBaseContext(), MainActivity.class );
        	setResult( RESULT_OK );
        	finish();
        	
        	return true;
        	
        }
        
        return super.onOptionsItemSelected( item );
        
    }
    
    public void setMediaID( String media_id ) {
    	
    	//android.util.Log.i("now playing media_id", ( media_id == null ? "Is Null" : media_id ) );
    	
    	if ( null == media_id ) {
    		
    		// Close the activitiy
    		finish();
    		
    	} else if ( media_id != this.current_media_id ) {
    		
    		this.current_media_id = media_id;
    		
    		
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
			
			String title = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
			String artist = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
			String album = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) );
			int duration = mSongCursor.getInt( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.DURATION ) );
			
			String album_id = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM_ID ) );
			
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
			
			if ( !newAlbumUri.equals( lastAlbumUri ) ) {
				
				ImageView mAlbumCover = ( ImageView ) findViewById( R.id.NowPlayingAlbumCover );
				
				if ( null != lastAlbumUri ) {
					
					BitmapDrawable bd = ( BitmapDrawable ) mAlbumCover.getDrawable();
					
					if ( null != bd ) {
						
						bd.getBitmap().recycle();
						mAlbumCover.setImageBitmap( null );
						
					}
					
				}
				
				lastAlbumUri = newAlbumUri;
				
				Uri albumArtUri = Uri.parse( newAlbumUri );
				
				mAlbumCover.setImageURI( albumArtUri );
				
			}
			
			albumCursor.close();
			mSongCursor.close();
			
			( ( TextView ) findViewById( R.id.SongTitle ) ).setText( title );
			( ( TextView ) findViewById( R.id.SongArtist ) ).setText( artist );
			( ( TextView ) findViewById( R.id.SongAlbum ) ).setText( album );
			
			mProgressFragment.setDuration( duration );
    		
			ToggleButton starButton = (ToggleButton) findViewById( R.id.StarButton );
			
			starButton.setChecked( mPlaylistManager.isStarred( media_id ) );
			
    	}
    	
    }
    
    private PlaylistMediaPlayer.PlaybackListener mPlaybackListener = new PlaylistMediaPlayer.PlaybackListener() {

		@Override public void onTrackChanged( String media_id ) {
			
			setMediaID( media_id );
			
		}

		@Override public void onPlay(int playbackPositionMilliseconds) {
			
			isPlaying = true;
			mProgressFragment.setProgress( playbackPositionMilliseconds );
			mProgressFragment.startProgress();
			
			( ( ImageButton ) findViewById( R.id.NowPlayingPlayPauseButton ) ).setImageResource( R.drawable.ic_action_playback_pause_white );
			
			
		}

		@Override public void onPause( int playbackPositionMilliseconds ) {
			
			isPlaying = false;
			mProgressFragment.setProgress( playbackPositionMilliseconds );
			mProgressFragment.stopProgress();
			( ( ImageButton ) findViewById( R.id.NowPlayingPlayPauseButton ) ).setImageResource( R.drawable.ic_action_playback_play_white );
			
		}

		@Override public void onPlaylistDone() {
			
			isPlaying = false;
			setMediaID( null );
			
		}

		@Override public void onLoopingChanged( LoopState loopState ) {
			
			ImageButton mRepeatButton = ( (ImageButton) findViewById( R.id.NowPlayingRepeatButton ) );
			
			if ( loopState == LoopState.LOOP_ALL ) {
				
				mRepeatButton.setTag( R.id.tag_repeat_state, "1" );
				mRepeatButton.setImageResource( R.drawable.ic_action_playback_repeat_orange_dark );
				
			} else if ( loopState == LoopState.LOOP_ONE ) {
				
				mRepeatButton.setTag( R.id.tag_repeat_state, "2" );
				mRepeatButton.setImageResource( R.drawable.ic_action_playback_repeat_1_orange_dark );
				
			} else {
				
				mRepeatButton.setTag( R.id.tag_repeat_state, "0" );
				mRepeatButton.setImageResource( R.drawable.ic_action_playback_repeat_white );
				
			}
			
		}

		@Override public void onShuffleChanged(boolean isShuffling) {
			
			ImageButton mShuffleButton = ( (ImageButton) findViewById( R.id.NowPlayingShuffleButton ) );
			
			if ( !isShuffling ) {
				
				mShuffleButton.setTag( R.id.tag_shuffle_state, "0" );
				mShuffleButton.setImageResource( R.drawable.ic_action_playback_schuffle_white );
				
			} else {
				
				mShuffleButton.setTag( R.id.tag_shuffle_state, "1" );
				mShuffleButton.setImageResource( R.drawable.ic_action_playback_schuffle_orange_dark );
				
			}
			
		}
		
	};
    
	private ServiceConnection mConnection = new ServiceConnection() {
		
	    public void onServiceConnected( ComponentName className, IBinder service ) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	    	mBoundService = ( ( MusicPlayerService.MusicPlayerServiceBinder ) service ).getService();
	        
	    	//android.util.Log.i("Attached to service", "Now Playing Activity connected to service." );
	    	
	    	//setMediaID( mBoundService.CURRENT_MEDIA_ID );
	    	//mBoundService.doAttachActivity();
	    	mBoundService.addPlaybackListener( mPlaybackListener );
	        
	    	getSupportActionBar().setTitle( mBoundService.mPlaylistName );
	    	
	        //mmBoundService.next();
	        
	    }

	    public void onServiceDisconnected( ComponentName className ) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	    	
	    	mBoundService = null;
	        
	    }
	    
	};
	
	void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because we want a specific service implementation that
	    // we know will be running in our own process (and thus won't be
	    // supporting component replacement by other applications).
	    bindService( new Intent( NowPlayingActivity.this, MusicPlayerService.class ), mConnection, Context.BIND_AUTO_CREATE );
	    
	    mIsBound = true;
	    
	}
	
	void doUnbindService() {
		
	    if ( mIsBound ) {
	        
	    	// Remove service's reference to local object
	    	//mBoundService.doDetachActivity();
	    	//android.util.Log.i("Detached from service", "Now Playing Activity disconnected from service." );
	    	// Detach our existing connection.
	        unbindService( mConnection );
	        mIsBound = false;
	        
	    }
	    
	}
	
}