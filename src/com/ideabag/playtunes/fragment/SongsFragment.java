package com.ideabag.playtunes.fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.SongsAllAdapter;
import com.ideabag.playtunes.util.AdmobUtil;
import com.ideabag.playtunes.util.TrackerSingleton;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ToggleButton;

public class SongsFragment extends ListFragment {
	
	public static final String TAG = "All Songs Fragment";
	
    private MainActivity mActivity;
    private AdView adView;
    
	SongsAllAdapter adapter;
	
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		ActionBar bar =	( ( ActionBarActivity ) getActivity() ).getSupportActionBar();
		
    	
    	adapter = new SongsAllAdapter( getActivity(), songMenuClickListener );
    	
    	
    	//getListView().setItemsCanFocus( true );
    	
    	bar.setTitle( "Songs" );
    	mActivity.actionbarTitle = bar.getTitle();
		bar.setSubtitle( adapter.getCount() + " songs" );
		mActivity.actionbarSubtitle = bar.getSubtitle();
    	
    	getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
    	
    	LayoutInflater inflater = mActivity.getLayoutInflater();
    	
    	LinearLayout adContainer = (LinearLayout) inflater.inflate( R.layout.list_header_admob, null, false );
    	
		adView = ( AdView ) adContainer.findViewById( R.id.adView );
	    //adView.setAdSize( AdSize.BANNER );
	    //adView.setAdUnitId( getString( R.string.admob_unit_id_main_activity ) );
	    
	    //ViewGroup.MarginLayoutParams params = ( ViewGroup.MarginLayoutParams ) adView.getLayoutParams();
	    //params.setMargins( 0, 8, 0, 8 );
		
	    
		Builder adRequestBuilder = new AdRequest.Builder().addTestDevice( AdRequest.DEVICE_ID_EMULATOR );
	    AdmobUtil.AddTestDevices( mActivity, adRequestBuilder );
	    
	    AdRequest adRequest = adRequestBuilder.build();
		
		
		// Start loading the ad in the background.
		adView.loadAd(adRequest);
    	
    	getListView().addHeaderView( adContainer, null, true );
    	setListAdapter( adapter );
    	
	}
		
	@Override public void onResume() {
		super.onResume();
		
		Tracker t = TrackerSingleton.getDefaultTracker( mActivity.getBaseContext() );

	        // Set screen name.
	        // Where path is a String representing the screen name.
		t.setScreenName( TAG );
		t.set( "_count", ""+adapter.getCount() );
		
	        // Send a screen view.
		t.send( new HitBuilders.AppViewBuilder().build() );
		
	}
		
	@Override public void onPause() {
		super.onPause();
		
		
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		
		
		mActivity.BoundService.setPlaylistCursor( adapter.getCursor() );
		
		mActivity.BoundService.setPosition( position - l.getHeaderViewsCount() );
		
		mActivity.BoundService.play();
		
		// Set the title of the playlist
		
		// 
		
	}
	
	View.OnClickListener songMenuClickListener = new View.OnClickListener() {
		
		@Override public void onClick( View v ) {
			
			int viewID = v.getId();
			String songID = "" + v.getTag( R.id.tag_song_id );
			
			if ( viewID == R.id.StarButton ) {
				
				ToggleButton starButton = ( ToggleButton ) v;
				
				if ( starButton.isChecked() ) {
					
					mActivity.PlaylistManager.addFavorite( songID );
					android.util.Log.i( "starred", songID );
					
				} else {
					
					mActivity.PlaylistManager.removeFavorite( songID );
					android.util.Log.i( "unstarred", songID );
					
				}
				
			}
			
			
			
		}
		
	};
	
	
}