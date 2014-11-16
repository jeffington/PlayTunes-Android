package com.ideabag.playtunes.activity;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.dialog.RateAppDialogFragment;
import com.ideabag.playtunes.fragment.FooterControlsFragment;
import com.ideabag.playtunes.fragment.NavigationFragment;
import com.ideabag.playtunes.service.MusicPlayerService;
import com.ideabag.playtunes.service.PlaybackNotification;
import com.ideabag.playtunes.util.CheckRemoteVersionFileTask;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;

public class MainActivity extends ActionBarActivity {
	
	public MusicPlayerService mBoundService;
	public boolean mIsBound = false;
	
	private FooterControlsFragment mFooterControlsFragment;
	public NavigationFragment NavigationFragment;
	
	public PlaylistManager PlaylistManager;
	
	public boolean mShouldHideActionItems;
	
	@Override public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_main );
		
		PlaylistManager = new PlaylistManager( this );
        
	    mFooterControlsFragment = ( FooterControlsFragment ) getSupportFragmentManager().findFragmentById( R.id.FooterControlsFragment );
        NavigationFragment = ( NavigationFragment ) getSupportFragmentManager().findFragmentById( R.id.left_drawer );
	    
        doBindService();
        
	}
	
	@Override protected void onNewIntent( Intent intent ) {
		super.onNewIntent( intent );
		
	    
	    if ( intent.hasExtra( PlaybackNotification.NOW_PLAYING_EXTRA ) ) {
	    	
	    	NavigationFragment.showNowPlaying();//loadNowPlayingFragment();
	    	showNowPlayingActivity();
	    	
	    }
		
	}
	
	public void setActionbarTitle( String titleString ) {
		
		if ( null != NavigationFragment ) {
			
			NavigationFragment.setActionbarTitle( titleString );
			
		}
		
	}
	
	public void setActionbarSubtitle( String subtitleString ) {
		if ( null != NavigationFragment ) {
			
			NavigationFragment.setActionbarSubtitle( subtitleString );
			
		}
		
	}
	
	@Override public void onStart() {
		super.onStart();
		
		if ( !mIsBound || mBoundService == null ) {
			
			doBindService();
			
		}
		
	    SharedPreferences prefs = getSharedPreferences( getString( R.string.prefs_file) , Context.MODE_PRIVATE );
	    //SharedPreferences.Editor edit = prefs.edit();
	    
	    int openCount = prefs.getInt( getString( R.string.pref_key_appopen ), 0 );
	    int rateAppPromptCount = getResources().getInteger( R.integer.rate_app_prompt_count );
	    
	    if ( openCount == rateAppPromptCount ) {
	    	
	    	// Show rate dialog
	    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        	
			Fragment prev = getSupportFragmentManager().findFragmentByTag( "dialog" );
			
		    if ( prev != null ) {
		        
		    	ft.remove( prev );
		    	
		    }
			
		    RateAppDialogFragment rateFragment = new RateAppDialogFragment();
			
			rateFragment.show( ft, "dialog" );
			
	    } else {
	    	
	    	// We don't want to show multiple dialogs
	    	
	    	new CheckRemoteVersionFileTask( this ).execute( );
	    	
	    	
	    }
	    
	    if ( openCount <= getResources().getInteger( R.integer.rate_app_prompt_count ) ) {
	    	
	    	openCount++;
	    	SharedPreferences.Editor edit = prefs.edit();
	    	edit.putInt( getString( R.string.pref_key_appopen ), openCount );
	    	edit.commit();
	    	
	    }
	    
		
	}
	
	@Override public void onStop() {
		super.onStop();
		
		if ( mIsBound || mBoundService != null ) {
			
			doUnbindService();
			
		}
		
		
	}
	
	
    // 
    // Now the hardware menu button will toggle the drawer layout
    // 
    @Override public boolean onKeyDown( int keycode, KeyEvent e ) {
    	
        if ( NavigationFragment.onKeyDown( keycode, e ) ) {
        	
        	return true;
        	
        }
        
        return super.onKeyDown( keycode, e );
        
    }
    
    public void transactFragment( Fragment mFragment ) {
    	
    	NavigationFragment.transactFragment( mFragment );
    	
    }
    
	private ServiceConnection mConnection = new ServiceConnection() {
		
	    public void onServiceConnected( ComponentName className, IBinder service ) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	    	mBoundService = ( ( MusicPlayerService.MusicPlayerServiceBinder ) service ).getService();
	    	
	    	mBoundService.addPlaybackListener( mFooterControlsFragment.PlaybackListener );
	        
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
	    	mBoundService.removePlaybackListener( mFooterControlsFragment.PlaybackListener );
	    	//BoundService.doDetachActivity();
	    	//android.util.Log.i("Detached from service", "Main Activity disconnected from service." );
	    	// Detach our existing connection.
	        unbindService( mConnection );
	        mIsBound = false;
	        
	    }
	    
	}
    
	//
	// Used by FooterControlFragment
	//
	
	private void showNowPlayingActivity() {
		
		Intent startNowPlayingActivity = new Intent( this, NowPlayingActivity.class );
		
		startActivityForResult( startNowPlayingActivity, 0 );
		
	}
	
	// 
	// We use the onActivityResult mechanism to return from the NowPlayingActivity
	// and display the Fragment of the currently playing playlist, if it's not already displayed.
	// 
	@Override protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		
		if ( resultCode == RESULT_OK ) {
			
			NavigationFragment.showNowPlaying();
			
		}
		
		
	}
	
	//
	// Menu and MenuItem related code goes here
	//
	//
	//
	/*
	@Override public boolean onCreateOptionsMenu( Menu menu ) {
		
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate( R.menu.menu_search, menu );
	    
	    return true;
		
	}
	*/
    @Override public boolean onOptionsItemSelected( MenuItem item ) {
    	
        if ( NavigationFragment.onOptionsItemSelected( item ) ) {
        	
        	return true;
        	
        }
        
        return super.onOptionsItemSelected( item );
        
    }
	/*
	@Override public boolean onPrepareOptionsMenu( Menu menu ) {

	    // If the nav drawer is open, hide action items related to the content view
	    boolean drawerOpen = mShouldHideActionItems;
	    
	    hideMenuItems( menu, !drawerOpen );
	    
	    return super.onPrepareOptionsMenu( menu );
	    
	}
	/*
	public boolean mShowSearch = true;
	
	private void hideMenuItems( Menu menu, boolean visible ) {
		
	    for ( int i = 0; i < menu.size(); i++ ) {
	    	
	    	MenuItem item = menu.getItem( i );
	    	int id = item.getItemId();
	    	
	    	if ( id == R.id.MenuSearch ) {
	    		
	    		item.setVisible( mShowSearch );
	    		
	    	} else {
	    		
	    		item.setVisible( visible );
	        	
	    	}
	    	
	    }
	    
	}
	*/
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        NavigationFragment.mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        NavigationFragment.mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
}
