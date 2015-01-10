package com.ideabag.playtunes.fragment;

import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.activity.SettingsActivity;
import com.ideabag.playtunes.fragment.search.SearchFragment;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class NavigationFragment extends Fragment implements View.OnClickListener {
	
	public static final String TAG = "NavigationFragment";
	
	protected MainActivity mActivity;
	protected FragmentManager mFragmentManager;
	protected ActionBar mActionBar;
	
	protected PlaylistManager mPlaylistManager;
	
	protected MusicBrowserFragment MusicBrowserFragment;
	
	// Have we warned the user that pressing Back will close the app?
	protected boolean mCloseWarningOn = false;
	
	public CharSequence mActionbarTitle, mActionbarSubtitle;
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate( R.layout.fragment_navigation, container, false );
		
	}
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
		mPlaylistManager = new PlaylistManager( getActivity() );
		
		mActionBar = mActivity.getSupportActionBar();
		
        //mActionBar.setLogo( R.drawable.ic_drawer );
		mActionBar.setDisplayHomeAsUpEnabled( true );
        mActionBar.setHomeButtonEnabled( true ); // Makes the drawer icon enabled
        //mActionBar.setDisplayUseLogoEnabled( true ); // Hides the icon
        //mActionBar.setDisplayShowHomeEnabled( true );
        //mActionBar.setIcon( android.R.color.transparent ); 
        mActionBar.setDisplayShowHomeEnabled( false );
        mFragmentManager = mActivity.getSupportFragmentManager();
		
        
	}
	
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		
		super.onActivityCreated( savedInstanceState );
		
		getView().findViewById( R.id.NavigationArtistsAll ).setOnClickListener( this );
		getView().findViewById( R.id.NavigationAlbumsAll ).setOnClickListener( this );
		getView().findViewById( R.id.NavigationGenresAll ).setOnClickListener( this );
		getView().findViewById( R.id.NavigationPlaylistsAll ).setOnClickListener( this );
		getView().findViewById( R.id.NavigationSearch ).setOnClickListener( this );
		getView().findViewById( R.id.NavigationStarred ).setOnClickListener( this );
		getView().findViewById( R.id.NavigationSongsAll ).setOnClickListener( this );
        
		
		View settingsButton = getView().findViewById( R.id.SettingsButton );
		if ( null != settingsButton ) {
			
			settingsButton.setOnClickListener( this );
			
		} else {
			
			// Add extra options into the app overflow menu
			
		}
		
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mediaStoreChanged );
		
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Playlists.Members.getContentUri( "external",
						Long.parseLong( mPlaylistManager.createStarredIfNotExist() ) ), true, mediaStoreChanged );
		
		 
		 MusicBrowserFragment = ( MusicBrowserFragment ) getActivity().getSupportFragmentManager().findFragmentById( R.id.MusicBrowserFragment );
		 
		 
	}
    
	
	@Override public void onPause() {
		super.onPause();
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		getActivity().getContentResolver().unregisterContentObserver( mediaStoreChanged );
		
	}
	
	@Override public void onClick( View v ) {
		
		int id = v.getId();
		load( id );
		
	}
	
	protected void load( int id ) {
		
		Fragment mNewFragment = null;
		
		switch ( id ) {
		
		case R.id.NavigationArtistsAll:
			mNewFragment = new ArtistsAllFragment();
			break;
			
		case R.id.NavigationAlbumsAll:
			mNewFragment = new AlbumsAllFragment();
			break;
			
		case R.id.NavigationGenresAll:
	    	mNewFragment = new GenresAllFragment();
			break;
			
		case R.id.NavigationSongsAll:
			
	    	mNewFragment = new SongsFragment();
			break;
		
		case R.id.NavigationStarred:
			
			mNewFragment = new PlaylistsOneFragment();
			((PlaylistsOneFragment)mNewFragment).setMediaID( mPlaylistManager.createStarredIfNotExist() );
			
			break;
		
		case R.id.NavigationSearch:
			
			mNewFragment = new SearchFragment();
			
			break;
		case R.id.SettingsButton:
				
				Intent launchSettingsIntent = new Intent( getActivity(), SettingsActivity.class);
				getActivity().startActivity( launchSettingsIntent );
				
			break;
		default:
			
			mNewFragment = new PlaylistsAllFragment();
			break;
			
		}
		
		if ( null != mNewFragment ) {
			
			transactFragment( mNewFragment );
			
		}
		
	}
	
	ContentObserver mediaStoreChanged = new ContentObserver(new Handler()) {

        @Override public void onChange( boolean selfChange ) {
            
            mActivity.runOnUiThread( new Runnable() {

				@Override public void run() {
					
					//adapter.notifyDataSetChanged();
					
				}
            	
            });
            
            super.onChange( selfChange );
            
        }

	};
	
    
    public void showNowPlaying() {
    	
    	MusicBrowserFragment.showNowPlaying();
    	
    }
    
    public void showSearch() {
    	
    	Fragment mSearchFragment = new SearchFragment();
    	
    	transactFragment( mSearchFragment );
    	
    }
    
    public void transactFragment( Fragment mFragment ) {
    	
    	MusicBrowserFragment.transactFragment( mFragment );
    	
    	mCloseWarningOn = false;
    	
    }
    
    public boolean onKeyDown( int keycode, KeyEvent e ) {
    	
    	switch ( keycode ) {
	        
	        case KeyEvent.KEYCODE_SEARCH:
	        	
	        	showSearch();
	        	return true;
	        
	        case KeyEvent.KEYCODE_BACK:
	        	
	        	if ( mFragmentManager.getBackStackEntryCount() == 0 ) {
	        		
	        		if ( !mCloseWarningOn ) {
		        		
		        		android.widget.Toast.makeText( mActivity, getString( R.string.back_warning ), android.widget.Toast.LENGTH_LONG ).show();
		        		mCloseWarningOn = true;
		        		
		        		return true;
		        		
		        	}
	        		
	        	} else {
	        		
	        		mCloseWarningOn = false;
	        		
	        	}
	        	break;
    	}
    	
    	return false;
    	
    }
	
	public void setActionbarTitle( String titleString ) {
		
		mActionbarTitle = ( CharSequence ) titleString;
		
		mActionBar.setTitle( mActionbarTitle );
		
	}
	
	public void setActionbarSubtitle( String subtitleString ) {
		
		mActionbarSubtitle = ( CharSequence ) subtitleString;
		
		mActionBar.setSubtitle( mActionbarSubtitle );
		
	}
    
}
