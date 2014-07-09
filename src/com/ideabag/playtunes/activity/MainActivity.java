package com.ideabag.playtunes.activity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ideabag.playtunes.MusicPlayerService;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.MusicPlayerService.SongInfoChangedListener;
import com.ideabag.playtunes.fragment.AlbumsAllFragment;
import com.ideabag.playtunes.fragment.ArtistsAllFragment;
import com.ideabag.playtunes.fragment.FooterControlsFragment;
import com.ideabag.playtunes.fragment.GenresAllFragment;
import com.ideabag.playtunes.fragment.PlaylistsAllFragment;
import com.ideabag.playtunes.fragment.SongsFragment;
import com.ideabag.playtunes.util.AdmobUtil;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends ActionBarActivity {
	
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	
	public MusicPlayerService mBoundService;
	private boolean mIsBound = false;
	
	private FooterControlsFragment mFooterControlsFragment;
	
	public PlaylistManager PlaylistManager;
	
	public LinearLayout AdContainer;
	public AdView AdView;
	
	public CharSequence actionbarTitle, actionbarSubtitle;
	
	
	
	@Override public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_main );
		
		PlaylistManager = new PlaylistManager( this );
		
		
        mDrawerLayout = ( DrawerLayout ) findViewById( R.id.drawer_layout );
        
        mDrawerLayout.setDrawerShadow( R.drawable.drawer_shadow, GravityCompat.START );
        
        mDrawerToggle = new ActionBarDrawerToggle(
        		this,
        		mDrawerLayout,
                android.R.color.transparent,
                R.string.drawer_open,
                R.string.drawer_close ) {
        	
        	//boolean open = false;
        	
            public void onDrawerClosed( View view ) {
            	
            	//this.open = false;
            	//customActionBarToggle.showClose();
            	//transactFragment();
            	
            	getSupportActionBar().setTitle( actionbarTitle );
            	getSupportActionBar().setSubtitle( actionbarSubtitle );
            	//open = false;
            	
            }
            
            public void onDrawerOpened( View drawerView ) {
                
            	//customActionBarToggle.showOpen();
            	actionbarTitle = getSupportActionBar().getTitle();
            	actionbarSubtitle = getSupportActionBar().getSubtitle();
            	
            	getSupportActionBar().setTitle( "PlayTunes" );
            	getSupportActionBar().setSubtitle( null );
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            	//this.open = true;
            	//open = true;
           }
            
            public void onDrawerSlide( View drawerView, float slideOffset ) {
            	
            	
            	
            }
            
        };
        
        
        mDrawerLayout.setDrawerListener( mDrawerToggle );
        ActionBar supportBar = getSupportActionBar();
        //supportBar.setIcon( R.drawable.ic_drawer );
        supportBar.setLogo( R.drawable.ic_drawer );
        //supportBar.setDisplayShowCustomEnabled( true );
        supportBar.setDisplayShowHomeEnabled( true );
        supportBar.setDisplayHomeAsUpEnabled( false );
        supportBar.setHomeButtonEnabled( true );
        supportBar.setDisplayUseLogoEnabled( true );
        /*
        AdContainer = (LinearLayout) getLayoutInflater().inflate( R.layout.list_header_admob, null, false );
    	AdView = ( AdView ) AdContainer.findViewById( R.id.adView );
	    
		AdRequest.Builder adRequestBuilder = new AdRequest.Builder().addTestDevice( AdRequest.DEVICE_ID_EMULATOR );
	    AdmobUtil.AddTestDevices( this, adRequestBuilder );
	    
	    AdRequest adRequest = adRequestBuilder.build();
		
		
		// Start loading the ad in the background.
	    AdView.loadAd(adRequest);
	    */
	    mFooterControlsFragment = ( FooterControlsFragment ) getSupportFragmentManager().findFragmentById( R.id.FooterControlsFragment );
        
		
		//startService( new Intent( this, MusicPlayerService.class ) );
		
		
	    
	}
	
	@Override public void onStart() {
		super.onStart();
		
		doBindService();
		
	}
	
	@Override public void onPause() {
		super.onPause();
		
		//AdView.pause();
		//doUnbindService();
		
		if ( mIsBound && mBoundService != null ) {
			
			mBoundService.removeOnSongInfoChangedListener( MusicStateChanged );
			
		}
		
	}
	
	@Override public void onResume() {
		super.onResume();
		
		//AdView.resume();
		
		if ( mIsBound && mBoundService != null ) {
			
			mBoundService.addOnSongInfoChangedListener( MusicStateChanged );
			
		}
		
	}
	
	@Override public void onStop() {
		super.onStop();
		
		//Intent playService = new Intent( this, MusicPlayerService.class );
		//playService.setAction( getString( R.string.action_play ) );
		
		//stopService( playService );
		doUnbindService();
		
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		//AdView.destroy();
		
		
		
	}

	
    public void toggleDrawer() {
    	
    	if ( mDrawerLayout.isDrawerOpen( GravityCompat.START ) ) {
    		
    		mDrawerLayout.closeDrawer( GravityCompat.START );
    		//customActionBarToggle.showClose();
    		getSupportActionBar().setTitle( actionbarTitle );
    		
    	} else {
    		
    		mDrawerLayout.openDrawer( GravityCompat.START );
    		//customActionBarToggle.showOpen();
    		actionbarTitle = getSupportActionBar().getTitle();
    		getSupportActionBar().setTitle( "PlayTunes" );
    		
    	}
    	
    }
    
    // 
    // Now the hardware menu button will toggle the drawer layout
    // 
    @Override public boolean onKeyDown( int keycode, KeyEvent e ) {
    	
        switch ( keycode ) {
        
            case KeyEvent.KEYCODE_MENU:
            	
            	toggleDrawer();
                return true;
                
        }

        return super.onKeyDown( keycode, e );
        
    }
    
    @Override public boolean onOptionsItemSelected( MenuItem item ) {
    	
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if ( mDrawerToggle.onOptionsItemSelected( item ) ) {
        	
        	return true;
        	
        }
        // Handle your other action bar items...
        
        return super.onOptionsItemSelected( item );
        
    }
    
    public void transactFragment( Fragment newFragment ) {
    	
    	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    	
    	// Replace whatever is in the fragment_container view with this fragment,
    	// and add the transaction to the back stack
    	transaction.replace( R.id.MusicBrowserContainer, newFragment );
    	transaction.addToBackStack( null );
    	
    	// Commit the transaction
    	transaction.commit();
    	
    }
    
    //public static class Navigator {
    	
    	public void showArtists() {
    		
        	// Create new fragment and transaction
        	Fragment newFragment = new ArtistsAllFragment();
        	transactFragment( newFragment );
    		
    	}
    	
    	public void showAlbums() {
    		
        	Fragment newFragment = new AlbumsAllFragment();
        	transactFragment( newFragment );
    		
    	}
    	
    	public void showGenres() {
    		
        	Fragment newFragment = new GenresAllFragment();
        	transactFragment( newFragment );
    	
    	}
    	
    	public void showSongs() {
    		
        	Fragment newFragment = new SongsFragment();
        	transactFragment( newFragment );
    		
    	}
    	
    	public void showPlaylists() {
    		
        	Fragment newFragment = new PlaylistsAllFragment();
        	transactFragment( newFragment );
    		
    	}
    	
    //}
    	
	private ServiceConnection mConnection = new ServiceConnection() {
		
	    public void onServiceConnected( ComponentName className, IBinder service ) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	    	mBoundService = ( ( MusicPlayerService.MusicPlayerServiceBinder ) service ).getService();
	    	android.util.Log.i("Attached to service", "Main Activity connected to service." );
	    	//BoundService.doAttachActivity();
	    	mBoundService.addOnSongInfoChangedListener( MusicStateChanged );
	        
	    	mIsBound = true;
	    	
	    }

	    public void onServiceDisconnected( ComponentName className ) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	    	
	    	mBoundService = null;
	        
	    	mIsBound = false;
	    	
	    }
	    
	};
	
	void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because we want a specific service implementation that
	    // we know will be running in our own process (and thus won't be
	    // supporting component replacement by other applications).
	    bindService( new Intent( MainActivity.this, MusicPlayerService.class ), mConnection, Context.BIND_AUTO_CREATE );
	    
	    
	    
	}
	
	void doUnbindService() {
		
	    if ( mIsBound ) {
	        
	    	
	    	// Remove service's reference to local object
	    	mBoundService.removeOnSongInfoChangedListener( MusicStateChanged );
	    	//BoundService.doDetachActivity();
	    	//android.util.Log.i("Detached from service", "Main Activity disconnected from service." );
	    	// Detach our existing connection.
	        unbindService( mConnection );
	        mIsBound = false;
	        
	    }
	    
	}
     
    private SongInfoChangedListener MusicStateChanged = new SongInfoChangedListener() {
		
		
		@Override public void songInfoChanged( String media_content_id ) {
			
			mFooterControlsFragment.setMediaID( media_content_id );
			
		}


		@Override public void musicDone() {
			
			mFooterControlsFragment.setMediaID( null );
			
		} 

		@Override public void musicStarted( int position_milliseconds ) {
			
			mFooterControlsFragment.showPlaying();
			
		}
		
		@Override public void musicPaused( int position_milliseconds ) {
			
			mFooterControlsFragment.showPaused();
			
		}
		
	};
	
	@Override protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		
		if ( resultCode == RESULT_OK ) {
			
			Class < ? extends Fragment > nowPlayingFragmentClass = this.mBoundService.mPlaylistFragmentClass;
			
			String nowPlayingMediaID = this.mBoundService.mPlaylistMediaID;
			
			try {
				
				Fragment nowPlayingFragment = nowPlayingFragmentClass.newInstance();
				
				if ( null != nowPlayingMediaID ) {
					
					
					
				}
				
				transactFragment( nowPlayingFragment );
				
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//this.transactFragment(newFragment)
			//this.mBoundService.mPlaylistFragmentClass
			
		}
		
		
	}
	
}
