package com.ideabag.playtunes.activity;

import java.util.ArrayList;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.dialog.RateAppDialogFragment;
import com.ideabag.playtunes.fragment.BaseNavigationFragment;
import com.ideabag.playtunes.fragment.FooterControlsFragment;
import com.ideabag.playtunes.fragment.NavigationDrawerFragment;
import com.ideabag.playtunes.media.PlaylistMediaPlayer.PlaybackListener;
import com.ideabag.playtunes.service.MusicPlayerService;
import com.ideabag.playtunes.service.PlaybackNotification;
import com.ideabag.playtunes.util.AdmobUtil;
import com.ideabag.playtunes.util.CheckRemoteVersionFileTask;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends ActionBarActivity {
	
	public MusicPlayerService mBoundService;
	public boolean mIsBound = false;
	
	public BaseNavigationFragment NavigationFragment;
	
	public PlaylistManager PlaylistManager;
	
	public boolean mShouldHideActionItems;
	
	// AdView
	private AdView mAdView;
	
	@Override public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		Fabric.with(this, new Crashlytics());
		
		setContentView( R.layout.activity_main );
		
		//
		// Weird fix for bug on Android 2.3 and something about Play Services
		// 
		try {Class.forName("android.os.AsyncTask");} catch(Throwable ignore) {}
		
		
		PlaylistManager = new PlaylistManager( this );
        
	   // mFooterControlsFragment = ( FooterControlsFragment ) getSupportFragmentManager().findFragmentById( R.id.FooterControlsFragment );
        NavigationFragment = ( BaseNavigationFragment ) getSupportFragmentManager().findFragmentById( R.id.left_drawer );
	    
        mAdView = ( AdView ) findViewById( R.id.adView );
	    
		AdRequest.Builder adRequestBuilder = new AdRequest.Builder().addTestDevice( AdRequest.DEVICE_ID_EMULATOR );
	    AdmobUtil.AddTestDevices( this, adRequestBuilder );
	    
	    AdRequest adRequest = adRequestBuilder.build();
		
		// Start loading the ad in the background.
	    mAdView.loadAd( adRequest );
        
	}
	
	@Override protected void onNewIntent( Intent intent ) {
		super.onNewIntent( intent );
		
	    
	    if ( intent.hasExtra( PlaybackNotification.NOW_PLAYING_EXTRA ) 
	    		&& mBoundService != null
	    		&& mBoundService.mPlaylistFragmentClass != null ) {
	    	
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
	
	@Override public void onResume() {
		super.onResume();
		
		mAdView.resume();
		
		if ( !mIsBound || mBoundService == null ) {
			
			doBindService();
			
		}
		
	}
	
	@Override public void onPause() {
		super.onPause();
		
		mAdView.pause();
		if ( mIsBound || mBoundService != null ) {
			
			doUnbindService();
			
		}
	}
	
	@Override public void onStop() {
		super.onStop();
		

		
		
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		mAdView.destroy();
		
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
    
    private ArrayList< PlaybackListener > mListeners = null;
    
    public void addPlaybackListener( PlaybackListener listener ) {
    	
    	if ( null == mListeners ) {
	    	
	    	mListeners = new ArrayList< PlaybackListener >();
	    	
	    }
	    	
	    mListeners.add( listener );
    	
    }
    
    public void removePlaybackListener( PlaybackListener listener ) {
    	
    	if ( mListeners != null && mListeners.contains( listener ) ) {
			
			mListeners.remove( listener );
			
		} else {
			
			mBoundService.removePlaybackListener( listener );
			
		}
    	
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
	
	@Override public boolean onCreateOptionsMenu( Menu menu ) {
		
		
	    return NavigationFragment.onCreateOptionsMenu( menu );
		
	}
	
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
        
        try {
        	
        	( ( NavigationDrawerFragment ) NavigationFragment ).mDrawerToggle.syncState();
        	
        } catch (Exception e) { }
        
    }

    @Override
    public void onConfigurationChanged( Configuration newConfig ) {
        super.onConfigurationChanged( newConfig );
        // Pass any configuration change to the drawer toggls
        try {
        	
        	( ( NavigationDrawerFragment ) NavigationFragment ).mDrawerToggle.onConfigurationChanged( newConfig );
        	
        } catch (Exception e) { }
    }
    
    private PlaybackListener mPlaybackListener = new PlaybackListener() {

		@Override public void onTrackChanged( String media_id ) {
			
			if ( null != mListeners ) {
				
				for ( int i = 0, count = mListeners.size(); i < count; i++ ) {
					
					mListeners.get( i ).onTrackChanged( media_id );
					
				}
				
			}
			
		}

		@Override public void onPlay() {
			
			if ( null != mListeners ) {
				
				for ( int i = 0, count = mListeners.size(); i < count; i++ ) {
					
					mListeners.get( i ).onPlay();
					
				}
				
			}
			
		}

		@Override public void onPause() {
			
			if ( null != mListeners ) {
				
				for ( int i = 0, count = mListeners.size(); i < count; i++ ) {
					
					mListeners.get( i ).onPause();
					
				}
				
			}
			
		}

		@Override public void onPlaylistDone() {
			
			if ( null != mListeners ) {
				
				for ( int i = 0, count = mListeners.size(); i < count; i++ ) {
					
					mListeners.get( i ).onPlaylistDone();
					
				}
				
			}
			
		}

		@Override public void onLoopingChanged( int loop ) {
			
			if ( null != mListeners ) {
				
				for ( int i = 0, count = mListeners.size(); i < count; i++ ) {
					
					mListeners.get( i ).onLoopingChanged( loop );
					
				}
				
			}
			
		}

		@Override public void onShuffleChanged( boolean isShuffling ) {
			
			if ( null != mListeners ) {
				
				for ( int i = 0, count = mListeners.size(); i < count; i++ ) {
					
					mListeners.get( i ).onShuffleChanged( isShuffling );
					
				}
				
			}
			
		}

		@Override public void onDurationChanged( int position, int duration ) {
			
			if ( null != mListeners ) {
				
				for ( int i = 0, count = mListeners.size(); i < count; i++ ) {
					
					mListeners.get( i ).onDurationChanged( position, duration );
					
				}
				
			}
			
		}
    	
    	
    };
	
}
