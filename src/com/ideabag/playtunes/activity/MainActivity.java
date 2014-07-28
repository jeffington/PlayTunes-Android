package com.ideabag.playtunes.activity;

import com.ideabag.playtunes.MusicPlayerService;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.fragment.FooterControlsFragment;
import com.ideabag.playtunes.fragment.SongsFragment;
import com.ideabag.playtunes.media.PlaylistMediaPlayer;
import com.ideabag.playtunes.media.PlaylistMediaPlayer.LoopState;
import com.ideabag.playtunes.util.PlaylistBrowser;

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
	
	
	
	@Override public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_main );
		
		PlaylistManager = new PlaylistManager( this );
		
		
        mDrawerLayout = ( DrawerLayout ) findViewById( R.id.drawer_layout );
        
        mDrawerLayout.setDrawerShadow( R.drawable.drawer_shadow, GravityCompat.START );
        
        mDrawerToggle = new ActionBarDrawerToggle(
        		this,
        		mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close ) {
        	
            public void onDrawerClosed( View view ) {
            	
            	getSupportActionBar().setTitle( mActionbarTitle );
            	getSupportActionBar().setSubtitle( mActionbarSubtitle );
            	
            }
            
            public void onDrawerOpened( View drawerView ) {
                
            	mActionbarTitle = getSupportActionBar().getTitle();
            	mActionbarSubtitle = getSupportActionBar().getSubtitle();
            	
            	getSupportActionBar().setTitle( getString( R.string.app_name ) );
            	getSupportActionBar().setSubtitle( null );
                
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
        
	    mFooterControlsFragment = ( FooterControlsFragment ) getSupportFragmentManager().findFragmentById( R.id.FooterControlsFragment );
        
	    // Load the initial music browser fragment
	    
	    SongsFragment initialFragment = new SongsFragment();
	    
	    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    	transaction.replace( R.id.MusicBrowserContainer, initialFragment );
    	// Don't add to back stack
    	
    	// Commit the transaction
    	transaction.commit();
	    
	}
	
	public void setActionbarTitle( String titleString ) {
		
		mActionbarTitle = ( CharSequence ) titleString;
		
		// Set the ActionBar title if the drawer is closed, otherwise just hold onto it for later
		if ( !mDrawerLayout.isDrawerOpen( GravityCompat.START ) ) {
			
			getSupportActionBar().setTitle( mActionbarTitle );
			
		}
		
	}
	
	public void setActionbarSubtitle( String subtitleString ) {
		
		mActionbarSubtitle = ( CharSequence ) subtitleString;
		
		// Set the ActionBar title if the drawer is closed, otherwise just hold onto it for later
		if ( !mDrawerLayout.isDrawerOpen( GravityCompat.START ) ) {
			
			getSupportActionBar().setSubtitle( mActionbarSubtitle );
			
		}
		
	}
	
	@Override public void onStart() {
		super.onStart();
		
		doBindService();
		
		if ( mIsBound && mBoundService != null ) {
			
			mBoundService.addPlaybackListener( mPlaybackListener );
			
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
    	
    	if ( mDrawerLayout.isDrawerOpen( GravityCompat.START ) ) {
    		
    		mDrawerLayout.closeDrawer( GravityCompat.START );
    		//customActionBarToggle.showClose();
    		getSupportActionBar().setTitle( mActionbarTitle );
    		
    	} else {
    		
    		mDrawerLayout.openDrawer( GravityCompat.START );
    		//customActionBarToggle.showOpen();
    		mActionbarTitle = getSupportActionBar().getTitle();
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
	
	// 
	// We use the onActivityResult mechanism to return from the NowPlayingActivity
	// and display the Fragment of the currently playing playlist, if it's not already displayed.
	// 
/*	
	@Override protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		
		if ( resultCode == RESULT_OK ) {
			
			Class < ? extends Fragment > nowPlayingFragmentClass = this.mBoundService.mPlaylistFragmentClass;
			
			String nowPlayingMediaID = this.mBoundService.mPlaylistMediaID;
			
			Fragment showingFragment = getSupportFragmentManager().findFragmentById( R.id.MusicBrowserContainer );
			
			android.util.Log.i( "MainActivity", "Showing Fragment: " + ( showingFragment == null ? "Is Null" : "Is Not Null" ) );
			
			try {
				
				// 
				// Check to see if the currently playing Fragment is already showing
				// only create the new fragment if it isn't already showing.
				//
				
				boolean mClassEquals = showingFragment != null && showingFragment.getClass().equals( nowPlayingFragmentClass );
				boolean mMediaIDEquals = showingFragment != null && ( ( PlaylistBrowser ) showingFragment ).getMediaID().equals( nowPlayingMediaID );
				
				if ( !( mClassEquals && mMediaIDEquals ) ) {
					
					Fragment nowPlayingFragment = nowPlayingFragmentClass.newInstance();
					
					if ( null != nowPlayingMediaID ) {
						
						( ( PlaylistBrowser ) nowPlayingFragment ).setMediaID( nowPlayingMediaID );
						
					}
					
					FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			    	
			    	// Replace whatever is in the fragment_container view with this fragment,
			    	// and add the transaction to the back stack
			    	transaction.replace( R.id.MusicBrowserContainer, nowPlayingFragment );
			    	transaction.addToBackStack( null );
			    	
			    	
			    	// Commit the transaction
			    	transaction.commitAllowingStateLoss();
			    	
					
				}
				
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch ( ClassCastException e ) {
				
				
			}
			
			//this.transactFragment(newFragment)
			//this.mBoundService.mPlaylistFragmentClass
			
		}
		
		
	}
*/
	@Override protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		
		if ( resultCode == RESULT_OK ) {
			
			Class < ? extends Fragment > nowPlayingFragmentClass = this.mBoundService.mPlaylistFragmentClass;
			
			String nowPlayingMediaID = this.mBoundService.mPlaylistMediaID;
			
			Fragment showingFragment = getSupportFragmentManager().findFragmentById( R.id.MusicBrowserContainer );
			
			android.util.Log.i( "MainActivity", "Showing Fragment: " + ( showingFragment == null ? "Is Null" : "Is Not Null" ) );
			
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
		
		
	}
	
}
