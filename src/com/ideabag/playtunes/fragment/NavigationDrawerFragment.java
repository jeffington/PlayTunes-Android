package com.ideabag.playtunes.fragment;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.slidinguppanel.SlidingUpPanelLayout;
import com.ideabag.playtunes.slidinguppanel.SlidingUpPanelLayout.PanelState;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

public class NavigationDrawerFragment extends NavigationFragment {
	
	public ActionBarDrawerToggle mDrawerToggle;
	protected DrawerLayout mDrawerLayout;
	
	protected ActionBar mActionBar;
	
	protected SlidingUpPanelLayout mSlidingPanel;
	
	public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated(savedInstanceState);
		
		
		mActionBar = mActivity.getSupportActionBar();
		
        //mActionBar.setLogo( R.drawable.ic_drawer );
		mActionBar.setDisplayHomeAsUpEnabled( true );
        mActionBar.setHomeButtonEnabled( true ); // Makes the drawer icon enabled
        //mActionBar.setDisplayUseLogoEnabled( true ); // Hides the icon
        //mActionBar.setDisplayShowHomeEnabled( true );
        //mActionBar.setIcon( android.R.color.transparent ); 
        mActionBar.setDisplayShowHomeEnabled( false );
		// 
		// Set up navigation drawer ( if we have one )
		// 
		View mDrawerViewContainer = getActivity().findViewById( R.id.drawer_layout );
		//View mDrawerView = ViewHash.get( Integer.valueOf( R.id.drawer_layout ) ).get();
		/*if ( null !=  ) {
			
			View v = ViewHash.get( Integer.valueOf( R.id.NavigationPlaylistsAll ) ).get();
			if ( null != v ) {
				
				v.setOnClickListener( NavigationClickListener );
				
			}
			//getView().findViewById( R.id.NavigationToolbarFeedback ).setOnClickListener( NavigationClickListener );
			
		}*/
		
		mSlidingPanel = ( SlidingUpPanelLayout ) getActivity().findViewById( R.id.sliding_layout );
		
		android.util.Log.i( TAG, "is null? " + ( null == mDrawerViewContainer ) );
		
		if ( null != mDrawerViewContainer ) {
		        
	        mDrawerLayout = ( DrawerLayout ) mDrawerViewContainer;
	        	
		    mDrawerLayout.setDrawerShadow( R.drawable.drawer_shadow, GravityCompat.START );
		        
		        mDrawerToggle = new ActionBarDrawerToggle(
		        		mActivity,
		        		mDrawerLayout,
		                R.drawable.ic_drawer,
		                R.string.drawer_open ) {
		        	
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
		        
	        }
		
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
	
	@Override public boolean onKeyDown( int keycode, KeyEvent e ) {
		
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
        		
        	} else if ( mSlidingPanel.getPanelState() != PanelState.COLLAPSED ) {
        		
        		mSlidingPanel.setPanelState( PanelState.COLLAPSED );
        		
        		return true;
        		
        	}
        	
        	break;
		}
    	
		
		return super.onKeyDown( keycode, e );
		
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
	
	@Override protected void load( int id ) {
		super.load( id );
		
		hideNavigation();
		
		if  ( mSlidingPanel.getPanelState() != PanelState.COLLAPSED ) {
     		
     		mSlidingPanel.setPanelState( PanelState.COLLAPSED );
     		
		}
		
	}
	
}
