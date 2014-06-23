package com.ideabag.playtunes.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.AlbumsAllAdapter;
import com.ideabag.playtunes.util.TrackerSingleton;

public class AlbumsAllFragment extends ListFragment {
	
	public static final String TAG = "All Albums Fragment";

	AlbumsAllAdapter adapter;
	private MainActivity mActivity;
    private AdView adView;
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		ActionBar bar =	( ( ActionBarActivity ) getActivity() ).getSupportActionBar();
    	
    	adapter = new AlbumsAllAdapter( getActivity() );
    	
    	setListAdapter( adapter );
    	
    	bar.setTitle( "Albums" );
    	mActivity.actionbarTitle = bar.getTitle();
		bar.setSubtitle( adapter.getCount() + " albums" );
		mActivity.actionbarSubtitle = bar.getSubtitle();
    	
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
		
		LayoutInflater inflater = mActivity.getLayoutInflater();
    	
    	LinearLayout adContainer = (LinearLayout) inflater.inflate( R.layout.list_header_admob, null, false );
    	
		adView = ( AdView ) adContainer.findViewById( R.id.adView );
	    //adView.setAdSize( AdSize.BANNER );
	    //adView.setAdUnitId( getString( R.string.admob_unit_id_main_activity ) );
	    
	    //ViewGroup.MarginLayoutParams params = ( ViewGroup.MarginLayoutParams ) adView.getLayoutParams();
	    //params.setMargins( 0, 8, 0, 8 );
		
	    
	    //adView.setLayoutParams( params );
	    
	    AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice( AdRequest.DEVICE_ID_EMULATOR )
        .addTestDevice( "7C4F580033D16C5C89E5CD5E5F432004" )
        .build();
		
		
		
		// Start loading the ad in the background.
		adView.loadAd(adRequest);
    	
    	getListView().addHeaderView( adContainer, null, true );
		
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
		
		//convertView.setTag( R.id.tag_album_id, cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM ) ) );
		
		String albumID = ( String ) v.getTag( R.id.tag_album_id );
		
		AlbumsOneFragment albumFragment = new AlbumsOneFragment( );
		albumFragment.setAlbumId( albumID );
		
		mActivity.transactFragment( albumFragment );
		
	}
	
}