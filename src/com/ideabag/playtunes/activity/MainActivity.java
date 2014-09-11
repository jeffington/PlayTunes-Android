package com.ideabag.playtunes.activity;

import com.ideabag.playtunes.MusicPlayerService;
import com.ideabag.playtunes.PlaybackNotification;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.dialog.RateAppDialogFragment;
import com.ideabag.playtunes.fragment.FooterControlsFragment;
import com.ideabag.playtunes.fragment.SearchFragment;
import com.ideabag.playtunes.fragment.SongsFragment;
import com.ideabag.playtunes.media.PlaylistMediaPlayer;
import com.ideabag.playtunes.media.PlaylistMediaPlayer.LoopState;
import com.ideabag.playtunes.util.CheckRemoteVersionFileTask;
import com.ideabag.playtunes.util.PlaylistBrowser;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

public class MainActivity extends ActionBarActivity {
	
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	
	public MusicPlayerService mBoundService;
	private boolean mIsBound = false;
	
	private FooterControlsFragment mFooterControlsFragment;
	
	public PlaylistManager PlaylistManager;
	
	private CharSequence mActionbarTitle, mActionbarSubtitle;
	
	public boolean mShouldHideActionItems;
	
	@Override public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_main );
		
		ActionBar supportBar = getSupportActionBar();
		
		PlaylistManager = new PlaylistManager( this );
		
		
        View mDrawerView = findViewById( R.id.drawer_layout );
        
        if ( null != mDrawerView ) {
	        
        	mDrawerLayout = ( DrawerLayout ) mDrawerView;
        	
	        mDrawerLayout.setDrawerShadow( R.drawable.drawer_shadow, GravityCompat.START );
	        
	        mDrawerToggle = new ActionBarDrawerToggle(
	        		this,
	        		mDrawerLayout,
	                R.drawable.ic_drawer,
	                R.string.drawer_open,
	                R.string.drawer_close ) {
	        	
	        	float mPreviousOffset = 0f;
	        	
	            public void onDrawerClosed( View drawerView ) {
	            	super.onDrawerClosed( drawerView );
	            	
	            	getSupportActionBar().setTitle( mActionbarTitle );
	            	getSupportActionBar().setSubtitle( mActionbarSubtitle );
	            	
	            	mShouldHideActionItems = false;
	            	supportInvalidateOptionsMenu();
	            	
	            }
	            
	            public void onDrawerOpened( View drawerView ) {
	                super.onDrawerOpened( drawerView );
	                
	            	mActionbarTitle = getSupportActionBar().getTitle();
	            	mActionbarSubtitle = getSupportActionBar().getSubtitle();
	            	
	            	getSupportActionBar().setTitle( getString( R.string.app_name ) );
	            	getSupportActionBar().setSubtitle( null );
	                
	            	mShouldHideActionItems = true;
	            	supportInvalidateOptionsMenu();
	            	getSupportActionBar().setDisplayShowCustomEnabled( !mShouldHideActionItems );
	            	
	           }
	            
	            public void onDrawerSlide( View drawerView, float slideOffset ) {
	            	super.onDrawerSlide( drawerView, slideOffset);
	            	
	            	if ( slideOffset > mPreviousOffset && !mShouldHideActionItems ) {
	                	
	                	mShouldHideActionItems = true;
	                	supportInvalidateOptionsMenu();
	                	getSupportActionBar().setDisplayShowCustomEnabled( !mShouldHideActionItems );
	                   
	               } else if( mPreviousOffset > slideOffset && slideOffset < 0.5f && mShouldHideActionItems ) {
	            	   
	            	   mShouldHideActionItems = false;
	            	   supportInvalidateOptionsMenu();
	            	   getSupportActionBar().setDisplayShowCustomEnabled( !mShouldHideActionItems );
	            	   
	               }
	                
	               mPreviousOffset = slideOffset;
	               
	            }
	            
	        };
	        
	        
	        mDrawerLayout.setDrawerListener( mDrawerToggle );
	        
	        
	        
        }
        
        
        
        supportBar.setLogo( R.drawable.ic_drawer );
        supportBar.setHomeButtonEnabled( true ); // Makes the drawer icon enabled
        supportBar.setDisplayUseLogoEnabled( true ); // Hides the icon
        //supportBar.setDisplayShowCustomEnabled( true );
        supportBar.setDisplayShowHomeEnabled( true );
        supportBar.setDisplayHomeAsUpEnabled( false );
        
        
	    mFooterControlsFragment = ( FooterControlsFragment ) getSupportFragmentManager().findFragmentById( R.id.FooterControlsFragment );
        
	    // Load the initial music browser fragment
	    // If the activity is being called upon to do a search, the initial fragment should be the SongSearchFragment
	    
	    
	    if ( null == getSupportFragmentManager().findFragmentById( R.id.MusicBrowserContainer ) ) {
		    
		    SongsFragment initialFragment = new SongsFragment();
		    
		    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	    	transaction.replace( R.id.MusicBrowserContainer, initialFragment );
	    	// Don't add to back stack
	    	
	    	// Commit the transaction
	    	transaction.commit();
	    	
	    }
	    
	    
	    SharedPreferences prefs = getSharedPreferences( getString( R.string.prefs_file) , Context.MODE_PRIVATE );
	    //SharedPreferences.Editor edit = prefs.edit();
	    
	    int openCount = prefs.getInt( getString( R.string.pref_key_appopen ), 0 );
	    int rateAppPromptCount = getResources().getInteger( R.integer.rate_app_prompt_count );
	    
	    if ( openCount == 0 ) {
	    	
	    	toggleDrawer();
	    	
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
	    	
	    	//new CheckRemoteVersionFileTask( this ).execute( new String[]{} );
	    	
	    	
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
		
		android.util.Log.i( "MainActivity", "New Intent");
		
	    
	    if ( intent.hasExtra( PlaybackNotification.NOW_PLAYING_EXTRA ) ) {
	    	
	    	android.util.Log.i( "MainActivity", "Now playing Extra received.");
	    	loadNowPlayingFragment();
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
		
		mActionbarTitle = ( CharSequence ) titleString;
		
		// Set the ActionBar title if the drawer is closed, otherwise just hold onto it for later
		if ( null != mDrawerLayout && !mDrawerLayout.isDrawerOpen( GravityCompat.START ) ) {
			
			getSupportActionBar().setTitle( mActionbarTitle );
			
		}
		
	}
	
	public void setActionbarSubtitle( String subtitleString ) {
		
		mActionbarSubtitle = ( CharSequence ) subtitleString;
		
		// Set the ActionBar title if the drawer is closed, otherwise just hold onto it for later
		if ( null != mDrawerLayout && !mDrawerLayout.isDrawerOpen( GravityCompat.START ) ) {
			
			getSupportActionBar().setSubtitle( mActionbarSubtitle );
			
		}
		
	}
	
	@Override public void onStart() {
		super.onStart();
		
		if ( mIsBound && mBoundService != null ) {
			
			mBoundService.addPlaybackListener( mPlaybackListener );
			
		} else {
			
			doBindService();
			
		}
		
	}
	
	@Override public void onStop() {
		super.onStop();
		
		if ( mIsBound && mBoundService != null ) {
			
			mBoundService.removePlaybackListener( mPlaybackListener );
			
		}
		
		doUnbindService();
		
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		//AdView.destroy();
		
		
		
	}

	
    public void toggleDrawer() {
    	
    	if ( mDrawerLayout != null ) {
	    	
	    	if ( mDrawerLayout.isDrawerOpen( GravityCompat.START ) ) {
	    		
	    		mDrawerLayout.closeDrawer( GravityCompat.START );
	    		//customActionBarToggle.showClose();
	    		getSupportActionBar().setTitle( mActionbarTitle );
	    		
	    	} else {
	    		
	    		mDrawerLayout.openDrawer( GravityCompat.START );
	    		//customActionBarToggle.showOpen();
	    		mActionbarTitle = getSupportActionBar().getTitle();
	    		getSupportActionBar().setTitle( getString( R.string.app_name ) );
	    		//getSupportActionBar().setDisplayUseLogoEnabled( false );
	    		//getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.ic_launcher ) );
	    		
	    		
	    	}
	    	
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
                
            case KeyEvent.KEYCODE_SEARCH:
            	
            	Fragment mSearchFragment = new SearchFragment();
            	transactFragment( mSearchFragment );
            	
            	return true;
                
        }

        return super.onKeyDown( keycode, e );
        
    }
    
    @Override public boolean onOptionsItemSelected( MenuItem item ) {
    	
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if ( mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected( item ) ) {
        	
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
    	
	private ServiceConnection mConnection = new ServiceConnection() {
		
	    public void onServiceConnected( ComponentName className, IBinder service ) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	    	mBoundService = ( ( MusicPlayerService.MusicPlayerServiceBinder ) service ).getService();
	    	
	    	mBoundService.addPlaybackListener( mPlaybackListener );
	        
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
	    	mBoundService.removePlaybackListener( mPlaybackListener );
	    	//BoundService.doDetachActivity();
	    	//android.util.Log.i("Detached from service", "Main Activity disconnected from service." );
	    	// Detach our existing connection.
	        unbindService( mConnection );
	        mIsBound = false;
	        
	    }
	    
	}
     
    private PlaylistMediaPlayer.PlaybackListener mPlaybackListener = new PlaylistMediaPlayer.PlaybackListener() {

		@Override public void onTrackChanged( String media_id ) {
			
			mFooterControlsFragment.setMediaID( media_id );
			
		}


		@Override public void onPlay(int playbackPositionMilliseconds) {
			
			mFooterControlsFragment.showPlaying();
			
		}


		@Override public void onPause(int playbackPositionMilliseconds) {
			
			mFooterControlsFragment.showPaused();
			
		}


		@Override public void onPlaylistDone() {
			
			mFooterControlsFragment.setMediaID( null );
			
		}


		@Override public void onLoopingChanged( LoopState loop ) { /* ... */ }


		@Override public void onShuffleChanged( boolean isShuffling ) { /* ... */ }
		
	};
	
	public void showNowPlayingActivity() {
		
		Intent startNowPlayingActivity = new Intent( this, NowPlayingActivity.class );
		
		startActivityForResult( startNowPlayingActivity, 0 );
		
	}
	
	public void loadNowPlayingFragment() {
		
		Class < ? extends Fragment > nowPlayingFragmentClass = this.mBoundService.mPlaylistFragmentClass;
		
		String nowPlayingMediaID = this.mBoundService.mPlaylistMediaID;
		
		Fragment showingFragment = getSupportFragmentManager().findFragmentById( R.id.MusicBrowserContainer );
		
		try {
			
			// 
			// Check to see if the currently playing Fragment is already showing
			// only create the new fragment if it isn't already showing.
			//
			
			if ( showingFragment != null ) {
				
				String showingMediaID = ( ( PlaylistBrowser ) showingFragment ).getMediaID();
				
				boolean isSameClass = showingFragment.getClass().equals( nowPlayingFragmentClass );
				
				boolean isSameMediaID = showingMediaID.equals( nowPlayingMediaID );
				
				if ( !( isSameClass && isSameMediaID ) ) {
					
					Fragment nowPlayingFragment = nowPlayingFragmentClass.newInstance();
					( ( PlaylistBrowser ) nowPlayingFragment ).setMediaID( nowPlayingMediaID );
					
					FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			    	
			    	// Replace whatever is in the fragment_container view with this fragment,
			    	// and add the transaction to the back stack
			    	transaction.replace( R.id.MusicBrowserContainer, nowPlayingFragment );
			    	transaction.addToBackStack( null );
			    	
			    	
			    	// Commit the transaction
			    	transaction.commitAllowingStateLoss();
					
				}
				
			}
			

	    	
		} catch ( InstantiationException e ) {
			e.printStackTrace();
		} catch ( IllegalAccessException e ) {
			e.printStackTrace();
		} catch ( ClassCastException e ) {
			e.printStackTrace();
		}
		
	}
	
	// 
	// We use the onActivityResult mechanism to return from the NowPlayingActivity
	// and display the Fragment of the currently playing playlist, if it's not already displayed.
	// 
	@Override protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		
		if ( resultCode == RESULT_OK ) {
			
			loadNowPlayingFragment();
			
		}
		
		
	}
	
	@Override public boolean onPrepareOptionsMenu( Menu menu ) {

	    // If the nav drawer is open, hide action items related to the content view
	    boolean drawerOpen = mShouldHideActionItems;
	    
	    hideMenuItems( menu, !drawerOpen );
	    
	    return super.onPrepareOptionsMenu( menu );
	    
	}

	private void hideMenuItems( Menu menu, boolean visible ) {
		
	    for ( int i = 0; i < menu.size(); i++ ) {
	    	
	        menu.getItem( i ).setVisible( visible );
	        
	    }
	    
	}
	
}
