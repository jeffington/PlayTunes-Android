package com.ideabag.playtunes.activity;

import com.ideabag.playtunes.MusicPlayerService;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.MusicPlayerService.SongInfoChangedListener;
import com.ideabag.playtunes.util.TrackerSingleton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class NowPlayingActivity extends ActionBarActivity {
	
	public static final String TAG = "Now Playing Activity";
	
	public boolean mIsBound = false;
	public MusicPlayerService BoundService;
	
	private String lastAlbumUri;
	private AdView adView;
	
	private String current_media_id;
	
	@SuppressLint("NewApi")
	@Override public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_nowplaying );
		
		ActionBar bar = getSupportActionBar();
		
		bar.setTitle( "Crosby, Stills, Nash & Young" );
		//bar.setSubtitle( "Now Playing" );
		
        ActionBar supportBar = getSupportActionBar();
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
		
		// Show Playlist
		
		
		// Set up ad banner
		
		adView = new AdView( this );
	    adView.setAdSize( AdSize.BANNER );
	    adView.setAdUnitId( getString( R.string.admob_unit_id_nowplaying_activity_id ) );
		
		FrameLayout adFrame = ( FrameLayout ) findViewById( R.id.NowPlayingAdContainer );
		adFrame.addView( adView );
	    
		AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice( AdRequest.DEVICE_ID_EMULATOR )
        .addTestDevice( "7C4F580033D16C5C89E5CD5E5F432004" )
        .build();
		
		
		
		// Start loading the ad in the background.
		adView.loadAd(adRequest);
		
	}
	
	// Connect and disconnect from the player service
	
	@Override public void onStart() {
		super.onStart();
		
		doBindService();
	}
	
	@Override public void onResume() {
		super.onResume();
		
		Tracker t = TrackerSingleton.getDefaultTracker( this );

	        // Set screen name.
	        // Where path is a String representing the screen name.
		t.setScreenName( TAG );
		//t.set( "_count", ""+adapter.getCount() );
		
	        // Send a screen view.
		t.send( new HitBuilders.AppViewBuilder().build() );
		
		
	}
	
	@Override public void onStop() {
		super.onStop();
		
		doUnbindService();
	}
	
	
	View.OnClickListener controlsClickListener = new View.OnClickListener() {
		
		@Override public void onClick( View v ) {
			
			
			
		}
		
	};
	
	@Override public boolean onCreateOptionsMenu( Menu menu ) {
	    
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate( R.menu.nowplaying, menu );
	    
	    return super.onCreateOptionsMenu( menu );
	    
	}
	
    @Override public boolean onOptionsItemSelected( MenuItem item ) {
    	
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if ( item.getItemId() == R.id.NowPlayingShowButton ) {
        	
        	//Intent showNowPlaying = new Intent( getBaseContext(), MainActivity.class );
        	setResult( RESULT_OK );
        	finish();
        	
        	return true;
        	
        }
        // Handle your other action bar items...
        
        return super.onOptionsItemSelected( item );
        
    }
    
    public void setMediaID( String media_id ) {
    	
    	if ( media_id != this.current_media_id ) {
    		
    		this.current_media_id = media_id;
    		
    		Cursor c = this.getContentResolver().query(
    				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    				new String[]{
    						MediaStore.Audio.Media.ALBUM,
    						MediaStore.Audio.Media.ARTIST,
    						MediaStore.Audio.Media.TITLE,
    						MediaStore.Audio.Media.DURATION
    				},
    				MediaStore.Audio.Media._ID + "=?",
    				new String[] {
    					media_id	
    				},
    				null
    			);
    		
    	}
    	
    }
    
	private ServiceConnection mConnection = new ServiceConnection() {
		
	    public void onServiceConnected( ComponentName className, IBinder service ) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	    	BoundService = ( ( MusicPlayerService.MusicPlayerServiceBinder ) service ).getService();
	        
	    	setMediaID( BoundService.CURRENT_MEDIA_ID );
	    	
	    	BoundService.setOnSongInfoChangedListener( new SongInfoChangedListener() {
	    		
	    		
	    		@Override public void songInfoChanged( String media_content_id ) {
	    		
	    			android.util.Log.i( "content_id", media_content_id );
	    			
	    		Cursor mSongCursor = getContentResolver().query(
	    				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
	    				new String[] {
	    					
	    					MediaStore.Audio.Media.ALBUM,
	    					MediaStore.Audio.Media.ARTIST,
	    					MediaStore.Audio.Media.TITLE,
	    					MediaStore.Audio.Media.ALBUM_ID,
	    					MediaStore.Audio.Media._ID
	    					
	    				},
	    				MediaStore.Audio.Media._ID + "=?",
	    				new String[] {
	    					
	    					media_content_id
	    					
	    				},
	    				null
	    			);
	    		
	    		mSongCursor.moveToFirst();
	    		
	    		String title = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
	    		String artist = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
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
	    			
	    			lastAlbumUri = newAlbumUri;
	    			
	    			Uri albumArtUri = Uri.parse( newAlbumUri );
	    			
	    			( ( ImageView ) findViewById( R.id.FooterControlsAlbumArt )).setImageURI( albumArtUri );
	    		}
	    		
	    		( ( TextView ) findViewById( R.id.FooterControlsSongTitle )).setText( title );
	    		( ( TextView ) findViewById( R.id.FooterControlsArtistName )).setText( artist );
	    		
	    		
	    		
	    		}
	    		
	    	});
	        
	        //mBoundService.next();
	        
	    }

	    public void onServiceDisconnected( ComponentName className ) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	    	BoundService = null;
	        
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
	        
	    	// Detach our existing connection.
	        unbindService( mConnection );
	        mIsBound = false;
	        
	    }
	    
	}
	
}