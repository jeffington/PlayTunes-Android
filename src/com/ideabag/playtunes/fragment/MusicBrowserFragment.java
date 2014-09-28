package com.ideabag.playtunes.fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.NavigationListAdapter;
import com.ideabag.playtunes.util.AdmobUtil;
import com.ideabag.playtunes.util.IMusicBrowser;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MusicBrowserFragment extends Fragment {
	
	public static final String TAG = "MusicBrowserFragment";
	
	private static final String PREF_KEY_CLASSNAME = "class_name";
	private static final String PREF_KEY_MEDIAID = "media_id";
	
	private MainActivity mActivity;
	
	private AdView mAdView;
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate( R.layout.fragment_music_browser, null, false );
		
	}
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		
		super.onActivityCreated( savedInstanceState );
        
    	mAdView = ( AdView ) getView().findViewById( R.id.adView );
	    
		AdRequest.Builder adRequestBuilder = new AdRequest.Builder().addTestDevice( AdRequest.DEVICE_ID_EMULATOR );
	    AdmobUtil.AddTestDevices( getActivity(), adRequestBuilder );
	    
	    AdRequest adRequest = adRequestBuilder.build();
		
	    //getActivity().getSupportFragmentManager().findFragmentById( R.id.MusicBrowserContainer );
		// Start loading the ad in the background.
	    mAdView.loadAd( adRequest );
	    
	    SharedPreferences prefs = getActivity().getSharedPreferences( getString( R.string.prefs_file ), Context.MODE_PRIVATE );
	    //IMusicBrowser TopFragment = null;
	    
	    if ( prefs.contains( "class_name" ) ) {// else check if we saved it in prefs
	    	
	    	String className = prefs.getString( PREF_KEY_CLASSNAME, "" );
	    	String mediaID = prefs.getString( PREF_KEY_MEDIAID, null );
	    	
	    	Fragment initialFragment = null;
	    	
	    	Class < ? extends Fragment > nowPlayingFragmentClass;
	    	
	    	try {
	    		
				nowPlayingFragmentClass = ( Class< ? extends Fragment > ) Class.forName( className );
				
				initialFragment = nowPlayingFragmentClass.newInstance();
				
				if ( null != initialFragment ) {
					
					IMusicBrowser mBrowserFragment = ( IMusicBrowser ) initialFragment;
					
					mBrowserFragment.setMediaID( mediaID );
					
				}
				
			} catch ( ClassNotFoundException e ) {
				e.printStackTrace();
			} catch ( java.lang.InstantiationException e ) {
				e.printStackTrace();
			} catch ( IllegalAccessException e ) {
				e.printStackTrace();
			}
		    
	    	if ( null == initialFragment ) {
	    		
	    		// Fall back to "All Songs"
	    		//android.util.Log.i( TAG, "Failed to instantiate... falling back to SongsFragment.");
	    		
	    		initialFragment = new SongsFragment();
	    		
	    	}
	    	
		    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
	    	transaction.replace( R.id.MusicBrowserContainer, initialFragment );
	    	// Don't add to back stack
	    	
	    	// Commit the transaction
	    	transaction.commit();
	    	
	    	
	    	
	    } else {
		    
		    SongsFragment initialFragment = new SongsFragment();
		    
		    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
	    	transaction.replace( R.id.MusicBrowserContainer, initialFragment );
	    	// Don't add to back stack
	    	
	    	// Commit the transaction
	    	transaction.commit();
	    	
	    }
	    
	    
	}
	
	@Override public void onResume() {
		super.onResume();
		
		mAdView.resume();
		
	}
	
	@Override public void onPause() {
		
		mAdView.pause();
		
		super.onPause();
		
		// Save current showing state
		
	}
	
	@Override public void onStop() {
		super.onStop();
		
		SharedPreferences prefs = getActivity().getSharedPreferences( getString( R.string.prefs_file ), Context.MODE_PRIVATE );
		SharedPreferences.Editor edit = prefs.edit();
		//android.util.Log.i( TAG, "Class name: " + TopFragment.getClass().getName() );
		
		IMusicBrowser TopFragment = ( IMusicBrowser ) getActivity().getSupportFragmentManager().findFragmentById( R.id.MusicBrowserContainer );
		
		edit.putString( PREF_KEY_CLASSNAME, TopFragment.getClass().getName() );
		edit.putString( PREF_KEY_MEDIAID, TopFragment.getMediaID() );
		
		edit.commit();
		
	}
	
	@Override public void onDestroy() {
		
		mAdView.destroy();
		
		super.onDestroy();
		
	}
	
	
	public void showNowPlaying() {
		
		Class < ? extends Fragment > nowPlayingFragmentClass = mActivity.mBoundService.mPlaylistFragmentClass;
		
		String nowPlayingMediaID = mActivity.mBoundService.mPlaylistMediaID;
		
		try {
			
			// 
			// Check to see if the currently playing Fragment is already showing
			// only create the new fragment if it isn't already showing.
			//
			IMusicBrowser TopFragment = ( IMusicBrowser ) getActivity().getSupportFragmentManager().findFragmentById( R.id.MusicBrowserContainer );
			
			if ( TopFragment != null ) {
				
				String showingMediaID = ( ( IMusicBrowser ) TopFragment ).getMediaID();
				
				boolean isSameClass = TopFragment.getClass().equals( nowPlayingFragmentClass );
				
				boolean isSameMediaID = showingMediaID.equals( nowPlayingMediaID );
				
				if ( !( isSameClass && isSameMediaID ) ) {
					
					Fragment nowPlayingFragment;
					
					nowPlayingFragment = nowPlayingFragmentClass.newInstance();
					( ( IMusicBrowser ) nowPlayingFragment ).setMediaID( nowPlayingMediaID );
					
					FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
			    	
			    	// Replace whatever is in the fragment_container view with this fragment,
			    	// and add the transaction to the back stack
			    	transaction.replace( R.id.MusicBrowserContainer, nowPlayingFragment );
			    	transaction.addToBackStack( null );
			    	
			    	
			    	// Commit the transaction
			    	transaction.commitAllowingStateLoss();
					
				}
				
			}
			

	    	
		} catch ( java.lang.InstantiationException e ) {
			e.printStackTrace();
		} catch ( IllegalAccessException e ) {
			e.printStackTrace();
		} catch ( ClassCastException e ) {
			e.printStackTrace();
		}
		
	}
	
    public void transactFragment( Fragment newFragment ) {
    	
		// 
		// Check to see if the currently playing Fragment is already showing
		// only create the new fragment if it isn't already showing.
		//
    	IMusicBrowser TopFragment = ( IMusicBrowser ) getActivity().getSupportFragmentManager().findFragmentById( R.id.MusicBrowserContainer );
		
		if ( TopFragment != null ) {
			
			String showingMediaID = TopFragment.getMediaID();
			
			boolean isSameClass = TopFragment.getClass().equals( newFragment.getClass() );
			
			boolean isSameMediaID = showingMediaID != null && showingMediaID.equals( ((IMusicBrowser)newFragment).getMediaID() );
			
			if ( !( isSameClass && isSameMediaID ) ) {
				
		    	FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
		    	TopFragment = (IMusicBrowser) newFragment;
		    	// Replace whatever is in the fragment_container view with this fragment,
		    	// and add the transaction to the back stack
		    	transaction.replace( R.id.MusicBrowserContainer, newFragment );
		    	transaction.addToBackStack( null );
		    	
		    	
		    	// Commit the transaction
		    	transaction.commit();
				
			}
			
		} else {
			
	    	FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
	    	TopFragment = (IMusicBrowser) newFragment;
	    	// Replace whatever is in the fragment_container view with this fragment,
	    	// and add the transaction to the back stack
	    	transaction.replace( R.id.MusicBrowserContainer, newFragment );
	    	transaction.addToBackStack( null );
	    	
	    	
	    	// Commit the transaction
	    	transaction.commit();
			
		}
		
		mActivity.NavigationFragment.hideNavigation();
    	
    }
    
}
