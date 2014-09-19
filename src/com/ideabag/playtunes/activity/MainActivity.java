package com.ideabag.playtunes.activity;

import com.ideabag.playtunes.MusicPlayerService;
import com.ideabag.playtunes.PlaybackNotification;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.dialog.RateAppDialogFragment;
import com.ideabag.playtunes.fragment.FooterControlsFragment;
import com.ideabag.playtunes.fragment.NavigationFragment;
import com.ideabag.playtunes.fragment.SongsFragment;
import com.ideabag.playtunes.fragment.search.SearchFragment;
import com.ideabag.playtunes.util.CheckRemoteVersionFileTask;
import com.ideabag.playtunes.util.IMusicBrowser;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
		
		ActionBar supportBar = getSupportActionBar();
		
		PlaylistManager = new PlaylistManager( this );
        
        
        
        supportBar.setLogo( R.drawable.ic_drawer );
        supportBar.setHomeButtonEnabled( true ); // Makes the drawer icon enabled
        supportBar.setDisplayUseLogoEnabled( true ); // Hides the icon
        //supportBar.setDisplayShowCustomEnabled( true );
        supportBar.setDisplayShowHomeEnabled( true );
        supportBar.setDisplayHomeAsUpEnabled( false );
        
        
	    mFooterControlsFragment = ( FooterControlsFragment ) getSupportFragmentManager().findFragmentById( R.id.FooterControlsFragment );
        NavigationFragment = ( NavigationFragment ) getSupportFragmentManager().findFragmentById( R.id.left_drawer );
	    // Load the initial music browser fragment
	    // If the activity is being called upon to do a search, the initial fragment should be the SongSearchFragment
	    
	    if ( null != savedInstanceState ) {
	    	
	    	
	    	
	    }
	    /*
	    if ( null == getSupportFragmentManager().findFragmentById( R.id.MusicBrowserContainer ) ) {
		    
		    SongsFragment initialFragment = new SongsFragment();
		    
		    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	    	transaction.replace( R.id.MusicBrowserContainer, initialFragment );
	    	// Don't add to back stack
	    	
	    	// Commit the transaction
	    	transaction.commit();
	    	
	    }
	    */
	    
	    SharedPreferences prefs = getSharedPreferences( getString( R.string.prefs_file) , Context.MODE_PRIVATE );
	    //SharedPreferences.Editor edit = prefs.edit();
	    
	    int openCount = prefs.getInt( getString( R.string.pref_key_appopen ), 0 );
	    int rateAppPromptCount = getResources().getInteger( R.integer.rate_app_prompt_count );
	    
	    if ( openCount == 0 ) {
	    	/*
	    	if ( null != mDrawerLayout && !mDrawerLayout.isDrawerOpen( GravityCompat.START ) ) {
	    		
	    		mDrawerLayout.openDrawer( GravityCompat.START );
	    		
	    	}
	    	*/
	    } else if ( openCount == rateAppPromptCount ) {
	    	
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
	    	
	    	new CheckRemoteVersionFileTask( this ).execute( new String[]{} );
	    	
	    	
	    }
	    
	    if ( openCount <= getResources().getInteger( R.integer.rate_app_prompt_count ) ) {
	    	
	    	openCount++;
	    	SharedPreferences.Editor edit = prefs.edit();
	    	edit.putInt( getString( R.string.pref_key_appopen ), openCount );
	    	edit.commit();
	    	
	    }
	    
	    
	    
	}
	
	@Override protected void onNewIntent( Intent intent ) {
		super.onNewIntent( intent );
		
	    
	    if ( intent.hasExtra( PlaybackNotification.NOW_PLAYING_EXTRA ) ) {
	    	
	    	NavigationFragment.showNowPlaying();//loadNowPlayingFragment();
	    	showNowPlayingActivity();
	    	
	    }
		
		/*
		if ( Intent.ACTION_SEARCH.equals( intent.getAction() ) ) {
	    	
	    	String query = intent.getStringExtra( SearchManager.QUERY );
	    	
	    	SearchFragment initialFragment = new SearchFragment( query );
		    
		    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	    	transaction.replace( R.id.MusicBrowserContainer, initialFragment );
	    	transaction.addToBackStack( null );
	    	// Don't add to back stack
	    	
	    	// Commit the transaction
	    	transaction.commit();
	    	
	    }
	    */
		
	}
	
	public void setActionbarTitle( String titleString ) {
		
		NavigationFragment.setActionbarTitle( titleString );
		
	}
	
	public void setActionbarSubtitle( String subtitleString ) {
		
		NavigationFragment.setActionbarSubtitle( subtitleString );
		
	}
	
	@Override public void onStart() {
		super.onStart();
		
		if ( !mIsBound || mBoundService == null ) {
			
			doBindService();
			
		}
		
	}
	
	@Override public void onStop() {
		super.onStop();
		
		if ( mIsBound || mBoundService != null ) {
			
			doUnbindService();
			
		}
		
		
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		//AdView.destroy();
		
		
		
	}
	
    // 
    // Now the hardware menu button will toggle the drawer layout
    // 
    @Override public boolean onKeyDown( int keycode, KeyEvent e ) {
    	
        switch ( keycode ) {
        
            case KeyEvent.KEYCODE_MENU:
            	
            	NavigationFragment.toggleNavigation();
                return true;
                
            case KeyEvent.KEYCODE_SEARCH:
            	
            	NavigationFragment.showSearch();
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
    
	public void showNowPlayingActivity() {
		
		Intent startNowPlayingActivity = new Intent( this, NowPlayingActivity.class );
		
		startActivityForResult( startNowPlayingActivity, 0 );
		
	}
	
	// 
	// We use the onActivityResult mechanism to return from the NowPlayingActivity
	// and display the Fragment of the currently playing playlist, if it's not already displayed.
	// 
	@Override protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		
		if ( resultCode == RESULT_OK ) {
			
			//loadNowPlayingFragment();
			//NavigationFragment.showNowPlaying();
			
		}
		
		
	}
	
	//
	// Menu and MenuItem related code goes here
	//
	//
	//
	
	@Override public boolean onCreateOptionsMenu( Menu menu ) {
		
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate( R.menu.menu_search, menu );
	    
	    return true;
		
	}
	
    @Override public boolean onOptionsItemSelected( MenuItem item ) {
    	
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if ( NavigationFragment.onOptionsItemSelected( item ) ) {
        	
        	return true;
        	
        }
        
        
        
        if ( item.getItemId() == R.id.MenuSearch ) {
        	
        	SearchFragment mSearchFragment = new SearchFragment();
        	
        	transactFragment( mSearchFragment );
        	
	    	NavigationFragment.hideNavigation();
        	
        }
        // Handle your other action bar items...
        
        return super.onOptionsItemSelected( item );
        
    }
	
	@Override public boolean onPrepareOptionsMenu( Menu menu ) {

	    // If the nav drawer is open, hide action items related to the content view
	    boolean drawerOpen = mShouldHideActionItems;
	    
	    hideMenuItems( menu, !drawerOpen );
	    
	    return super.onPrepareOptionsMenu( menu );
	    
	}
	
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
	
}
