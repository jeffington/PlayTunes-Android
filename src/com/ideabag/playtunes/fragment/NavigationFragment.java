package com.ideabag.playtunes.fragment;

import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.activity.SettingsActivity;
import com.ideabag.playtunes.adapter.NavigationListAdapter;
import com.ideabag.playtunes.fragment.search.SearchFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class NavigationFragment extends Fragment implements OnItemClickListener {
	
	public static final String TAG = "NavigationFragment";
	
	private MainActivity mActivity;
	private FragmentManager mFragmentManager;
	private DrawerLayout mDrawerLayout;
	public ActionBarDrawerToggle mDrawerToggle;
	private ActionBar mActionBar;
	
	private PlaylistManager mPlaylistManager;
	
	private MusicBrowserFragment MusicBrowserFragment;
	
	// Have we warned the user that pressing Back will close the app?
	private boolean mCloseWarningOn = false;
	
	NavigationListAdapter adapter;
	
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
        
		adapter = new NavigationListAdapter( getActivity() );
		
		ListView lv = ( ListView ) getView().findViewById( R.id.NavigationListView );
		lv.setAdapter( adapter );
		
		
		
		lv.setOnItemClickListener( this );
		
		// Set up the toolbar
		if ( null != getView().findViewById( R.id.SettingsButton ) ) {
			
			getView().findViewById( R.id.SettingsButton ).setOnClickListener( NavigationClickListener );
			//getView().findViewById( R.id.NavigationToolbarFeedback ).setOnClickListener( NavigationClickListener );
			
		} else {
			
			// Add extra options into the app overflow menu
			
		}
		
		lv.setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		lv.setDividerHeight( 1 );
		lv.setSelector( R.drawable.list_item_background );
		
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mediaStoreChanged );
		
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Playlists.Members.getContentUri( "external",
						Long.parseLong( mPlaylistManager.createStarredIfNotExist() ) ), true, mediaStoreChanged );
		
		// 
		// Set up navigation drawer ( if we have one )
		// 
		
		View mDrawerView = getActivity().findViewById( R.id.drawer_layout );
	    
		if ( null != mDrawerView ) {
		        
	        mDrawerLayout = ( DrawerLayout ) mDrawerView;
	        	
		    mDrawerLayout.setDrawerShadow( R.drawable.drawer_shadow, GravityCompat.START );
		        
		        mDrawerToggle = new ActionBarDrawerToggle(
		        		mActivity,
		        		mDrawerLayout,
		                R.drawable.ic_drawer,
		                R.string.drawer_open,
		                R.string.drawer_close ) {
		        	
		        	float mPreviousOffset = 0f;
		        	
		        	
		            public void onDrawerClosed( View drawerView ) {
		            	super.onDrawerClosed( drawerView );
		            	
		            	configureActionBarDrawerClosed();
		            	
		            }
		            
		            public void onDrawerOpened( View drawerView ) {
		                super.onDrawerOpened( drawerView );
		                
		                configureActionBarDrawerOpen();
		            	
		           }
		            
		            public void onDrawerSlide( View drawerView, float slideOffset ) {
		            	super.onDrawerSlide( drawerView, slideOffset);
		            	
		            	if ( slideOffset > mPreviousOffset ) {
		                	
		            		configureActionBarDrawerOpen();
		                	
		               } else if ( mPreviousOffset > slideOffset && slideOffset < 0.5f ) {
		            	   
		            	   configureActionBarDrawerClosed();
		            	   
		               }
		                
		               mPreviousOffset = slideOffset;
		               
		            }
		            
		        };
		        
		        
		        mDrawerLayout.setDrawerListener( mDrawerToggle );
		        
	        } else { // No Navigation drawer
	        	
	        	//mActivity.getSupportActionBar().setDisplayUseLogoEnabled( false );
	        	
	        }
		 
		 MusicBrowserFragment = ( MusicBrowserFragment ) getActivity().getSupportFragmentManager().findFragmentById( R.id.MusicBrowserFragment );
		
		 getView().findViewById( R.id.SettingsButton ).setOnClickListener( new OnClickListener() {

			@Override public void onClick( View v ) {
				
				Intent mShowSettings = new Intent( getActivity(), SettingsActivity.class );
				getActivity().startActivity( mShowSettings );
				
			}
			
		 });
		 
		 SharedPreferences prefs = mActivity.getSharedPreferences( getString( R.string.prefs_file) , Context.MODE_PRIVATE );
	    //SharedPreferences.Editor edit = prefs.edit();
	    
	    int openCount = prefs.getInt( getString( R.string.pref_key_appopen ), 0 );
	    
	    if ( openCount == 0 ) {
	    	
	    	showNavigation();
	    	
	    }
		 
	}
    
    private void configureActionBarDrawerOpen() {
    	
    	//mActivity.mShouldHideActionItems = true;
    	
		//mActivity.setActionbarTitle( (String) mActionBar.getTitle() );
		//mActivity.setActionbarSubtitle( (String) mActionBar.getSubtitle() );
    	
		mActionBar.setTitle( getString( R.string.app_name ) );
    	mActionBar.setSubtitle( null );
    	
    	mActionBar.setDisplayShowCustomEnabled( false );
    	mActionBar.setDisplayShowHomeEnabled( true );
    	//mActionBar.setDisplayUseLogoEnabled( false );
    	//mActionBar.setDisplayShowHomeEnabled( true );
    	mActivity.supportInvalidateOptionsMenu();
    	
    }
    
    private void configureActionBarDrawerClosed() {
    	
    	//mActivity.mShouldHideActionItems = false;
    	
    	if ( mActionbarTitle != null && mActionbarTitle.equals( "*" ) ) {
    		
    		mActionBar.setDisplayShowHomeEnabled( true );
    		mActionBar.setTitle( null );
    		
    	} else {
    		
    		//mActionBar.setDisplayShowHomeEnabled( false );
    		mActionBar.setTitle( mActionbarTitle );
    		
    	}
    	mActionBar.setSubtitle( mActionbarSubtitle );
    	
    	mActivity.getSupportActionBar().setDisplayShowCustomEnabled( true );
    	//mActionBar.setDisplayUseLogoEnabled( true );
    	
    	mActivity.supportInvalidateOptionsMenu();
    }
    
	
	@Override public void onResume() {
		super.onResume();
		
		if ( mDrawerLayout != null ) {
			
			if ( mDrawerLayout.isDrawerOpen( GravityCompat.START ) ) {
	    		
				configureActionBarDrawerClosed();
				
			} else {
				
				configureActionBarDrawerOpen();
				
			}
			
		}
		
	}
	
	@Override public void onPause() {
		super.onPause();
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		getActivity().getContentResolver().unregisterContentObserver( mediaStoreChanged );
		
	}
	
	
	private View.OnClickListener NavigationClickListener = new View.OnClickListener() {
		
		@Override public void onClick( View v ) {
			
			int id = v.getId();
			
			if ( id == R.id.SettingsButton ) {
				
				Intent launchSettingsIntent = new Intent( getActivity(), SettingsActivity.class);
				
				getActivity().startActivity( launchSettingsIntent );
				
			}
			
		}
		
	};

	@Override public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		
		load( position, false );
		
	}
	
	private void load( int position, boolean silent ) {
		
		Fragment mNewFragment = null;
		
		switch ( position ) {
		
		case NavigationListAdapter.ARTISTS:
			mNewFragment = new ArtistsAllFragment();
			break;
			
		case NavigationListAdapter.ALBUMS:
			mNewFragment = new AlbumsAllFragment();
			break;
			
		case NavigationListAdapter.GENRES:
	    	mNewFragment = new GenresAllFragment();
			break;
			
		case NavigationListAdapter.SONGS:
			
	    	mNewFragment = new SongsFragment();
			break;
		
		case NavigationListAdapter.STARRED:
			
			mNewFragment = new PlaylistsOneFragment();
			((PlaylistsOneFragment)mNewFragment).setMediaID( mPlaylistManager.createStarredIfNotExist() );
			
			break;
		
		case NavigationListAdapter.SEARCH:
			
			mNewFragment = new SearchFragment();
			
			break;
		
		default:
			
			mNewFragment = new PlaylistsAllFragment();
			break;
			
		}
		
		if ( null != mNewFragment ) {
			
			transactFragment( mNewFragment );
			
		}
		
		this.configureActionBarDrawerOpen();
		
	}
	
	ContentObserver mediaStoreChanged = new ContentObserver(new Handler()) {

        @Override public void onChange( boolean selfChange ) {
            
            mActivity.runOnUiThread( new Runnable() {

				@Override public void run() {
					
					adapter.notifyDataSetChanged();
					
				}
            	
            });
            
            super.onChange( selfChange );
            
        }

	};
	
	public void showNavigation() {
		
		if ( mDrawerLayout != null && !mDrawerLayout.isDrawerOpen( GravityCompat.START ) ) {
			
			configureActionBarDrawerOpen();
			
			mDrawerLayout.openDrawer( GravityCompat.START );
			//customActionBarToggle.showOpen();
			//mActionbarTitle = mActivity.getSupportActionBar().getTitle();
			//mActivity.getSupportActionBar().setTitle( getString( R.string.app_name ) );
			//getSupportActionBar().setDisplayUseLogoEnabled( false );
			//getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.ic_launcher ) );
			
		}
		
	}
	
	public void hideNavigation() {
		
		if ( mDrawerLayout != null && mDrawerLayout.isDrawerOpen( GravityCompat.START ) ) {
    		
			configureActionBarDrawerClosed();
			
    		mDrawerLayout.closeDrawer( GravityCompat.START );
    		
    	}
		
	}
	
	public void toggleNavigation() {
		
    	if ( mDrawerLayout != null ) {
	    	
	    	if ( mDrawerLayout.isDrawerOpen( GravityCompat.START ) ) {
	    		
	    		hideNavigation();
	    		
	    	} else {
	    		
	    		showNavigation();
	    		
	    	}
	    	
    	}
		
	}
	
	public void setActionbarTitle( String titleString ) {
		
		mActionbarTitle = ( CharSequence ) titleString;
		
		// Set the ActionBar title if the drawer is closed, otherwise just hold onto it for later
		if ( null != mDrawerLayout && !mDrawerLayout.isDrawerOpen( GravityCompat.START ) ) {
			
			if ( null != titleString && !titleString.equals( "*" ) ) {
				
				mActionBar.setTitle( mActionbarTitle );
				
			}
			
		}
		
	}
	
	public void setActionbarSubtitle( String subtitleString ) {
		
		mActionbarSubtitle = ( CharSequence ) subtitleString;
		
		// Set the ActionBar title if the drawer is closed, otherwise just hold onto it for later
		if ( null != mDrawerLayout && !mDrawerLayout.isDrawerOpen( GravityCompat.START ) ) {
			
			mActionBar.setSubtitle( mActionbarSubtitle );
			
		}
		
	}
	
    @Override public boolean onOptionsItemSelected( MenuItem item ) {
		
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
    	if ( null != mDrawerToggle && mDrawerToggle.onOptionsItemSelected( item ) ) {
    		
    		return true;
    		
    	}
    	/*
    	// Search button!
        if ( item.getItemId() == R.id.MenuSearch ) {
        	
        	SearchFragment mSearchFragment = new SearchFragment();
        	
        	transactFragment( mSearchFragment );
        	
	    	hideNavigation();
	    	
	    	return true;
        	
        }
    	*/
    	return false;
    	
    }
	
    
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
	        
	        case KeyEvent.KEYCODE_MENU:
	        	
	        	toggleNavigation();
	            return true;
	            
	        case KeyEvent.KEYCODE_SEARCH:
	        	
	        	showSearch();
	        	return true;
	        
	        case KeyEvent.KEYCODE_BACK:
	        	
	        	if ( mDrawerLayout != null && mDrawerLayout.isDrawerOpen( GravityCompat.START ) ) {
	        		
	        		hideNavigation();
	        		
	        		return true;
	        		
	        	}
	        	
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
    
}
