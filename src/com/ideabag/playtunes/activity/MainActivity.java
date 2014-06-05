package com.ideabag.playtunes.activity;

import com.ideabag.playtunes.MusicPlayerService;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.fragment.AlbumsAllFragment;
import com.ideabag.playtunes.fragment.ArtistsAllFragment;
import com.ideabag.playtunes.fragment.GenresAllFragment;
import com.ideabag.playtunes.fragment.PlaylistsAllFragment;
import com.ideabag.playtunes.fragment.SongsFragment;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

public class MainActivity extends ActionBarActivity {
	
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	
	private MusicPlayerService mBoundService;
	
	public PlaylistManager PlaylistManager;
	
	private String NOW_PLAYING_MEDIA_ID;
	
	private boolean mIsBound = false;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
	    public void onServiceConnected( ComponentName className, IBinder service ) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        mBoundService = ( ( MusicPlayerService.MusicPlayerServiceBinder ) service ).getService();
	        
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
	    bindService( new Intent( MainActivity.this, MusicPlayerService.class ), mConnection, Context.BIND_AUTO_CREATE );
	    
	    mIsBound = true;
	    
	}
	
	void doUnbindService() {
		
	    if ( mIsBound ) {
	        
	    	// Detach our existing connection.
	        unbindService( mConnection );
	        mIsBound = false;
	        
	    }
	    
	}

	
	@Override public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_main );
		
		PlaylistManager = new PlaylistManager( this );
		
		//doBindService();
		getSupportActionBar().setCustomView( R.layout.view_actionbar );
		
        mDrawerLayout = ( DrawerLayout ) findViewById( R.id.drawer_layout );
        
        mDrawerLayout.setDrawerShadow( R.drawable.drawer_shadow, GravityCompat.START );
        
        mDrawerToggle = new ActionBarDrawerToggle(
        		this,
        		mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close) {
        	
        	
            public void onDrawerClosed( View view ) {
            	
            	//this.open = false;
            	//customActionBarToggle.showClose();
            	//transactFragment();
            }
            
            public void onDrawerOpened( View drawerView ) {
                
            	//customActionBarToggle.showOpen();
            	//getSupportActionBar().setTitle( "");
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            	//this.open = true;
            	
           }
            
            public void onDrawerSlide( View drawerView, float slideOffset ) {
            	
            	
            	
            }
            
        };
        
        
        mDrawerLayout.setDrawerListener( mDrawerToggle );
        
        //getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeButtonEnabled( true );
        
	}
	
	@Override public void onStart() {
		super.onStart();
		
		Intent playService = new Intent( this, MusicPlayerService.class );
		//playService.setAction( getString( R.string.action_play ) );
		
		startService( playService );
		
		
	}
	
	@Override public void onStop() {
		super.onStop();
		
		Intent playService = new Intent( this, MusicPlayerService.class );
		//playService.setAction( getString( R.string.action_play ) );
		
		stopService( playService );
		
	}

	
    public void toggleDrawer() {
    	
    	if ( mDrawerLayout.isDrawerOpen( GravityCompat.START ) ) {
    		
    		mDrawerLayout.closeDrawer( GravityCompat.START );
    		//customActionBarToggle.showClose();
    		
    	} else {
    		
    		mDrawerLayout.openDrawer( GravityCompat.START );
    		//customActionBarToggle.showOpen();
    		
    	}
    	
    }
    
    // 
    // Now the hardware menu button will toggle the drawer layout
    // 
    @Override public boolean onKeyDown(int keycode, KeyEvent e) {
    	
        switch ( keycode ) {
        
            case KeyEvent.KEYCODE_MENU:
            	
            	toggleDrawer();
                return true;
                
        }

        return super.onKeyDown(keycode, e);
        
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
    
}
