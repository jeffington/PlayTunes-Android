package com.ideabag.playtunes.fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.ArtistsOneAdapter;
import com.ideabag.playtunes.util.TrackerSingleton;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ArtistsOneFragment extends ListFragment {
	
	public static final String TAG = "One Artist Fragment";
	
	private ViewGroup AllSongs;
	private ViewGroup Singles = null;
	
	private TextView albumDivider;
	
	ArtistsOneAdapter adapter;
	
	private MainActivity mActivity;
    private AdView adView;
    
	private String ARTIST_ID;
	
	public void setArtistId( String album_id ) {
		
		ARTIST_ID = album_id;
		
	}
	
	@Override public void onAttach( Activity activity ) {
			
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		ActionBar bar =	( ( ActionBarActivity ) getActivity() ).getSupportActionBar();
		LayoutInflater inflater = mActivity.getLayoutInflater();
		//android.util.Log.i( "ARTIST_ID", ARTIST_ID );
    	adapter = new ArtistsOneAdapter( getActivity(), ARTIST_ID );
    	
    	
    	Cursor songCountCursor = getActivity().getContentResolver().query(
    				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    				new String[] {
    					
    					MediaStore.Audio.Media.ARTIST_ID,
    					MediaStore.Audio.Media._ID
    					
    				},
    				MediaStore.Audio.Media.ARTIST_ID + "=?",
    				new String[] {
    					
    					ARTIST_ID
    					
    				},
    				null
    			);
    	
    	Cursor singlesCountCursor = getActivity().getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] {
					
					MediaStore.Audio.Media.ARTIST_ID,
					MediaStore.Audio.Media._ID
					
				},
				MediaStore.Audio.Media.ARTIST_ID + "=? AND " + MediaStore.Audio.Media.ALBUM + "='Music'",
				new String[] {
					
					ARTIST_ID
					
				},
				null
			);
    	

    	
    	bar.setTitle( adapter.ArtistName );
    	mActivity.actionbarTitle = bar.getTitle();
    	bar.setSubtitle( "Artist" );
    	mActivity.actionbarSubtitle = bar.getSubtitle();
		
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
    	
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
    	
    	int songCount = songCountCursor.getCount();
    	songCountCursor.close();
    	
    	int singlesCount = singlesCountCursor.getCount();
    	singlesCountCursor.close();
    	
    	
    	AllSongs = ( ViewGroup ) inflater.inflate( R.layout.list_item_title_one_badge, null );
    	( ( TextView ) AllSongs.findViewById( R.id.BadgeCount ) ).setText( "" + songCount );
    	( ( TextView ) AllSongs.findViewById( R.id.Title ) ).setText( getString( R.string.artist_all_songs ) );
    	getListView().addHeaderView( AllSongs, null, true );
    	
    	if ( singlesCount > 0) {
    		
    		Singles = ( ViewGroup ) inflater.inflate( R.layout.list_item_title_one_badge, null );
    		( ( TextView ) Singles.findViewById( R.id.BadgeCount ) ).setText( "" + singlesCount );
    		( ( TextView ) Singles.findViewById( R.id.Title ) ).setText( getString( R.string.artist_singles ) );
    		getListView().addHeaderView( Singles, null, true );
    		
    	}
    	albumDivider = ( TextView ) inflater.inflate( R.layout.list_header_albums, null );
    	
    	
    	getListView().addHeaderView( albumDivider, null, false );
    	
    	
    	
    	setListAdapter( adapter );
    	
	}
	
	@Override public void onResume() {
		super.onResume();
		
		Tracker t = TrackerSingleton.getDefaultTracker( mActivity );

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
		
		//android.util.Log.i( "Clicked", "" + position);
		
		if ( v.equals( AllSongs ) ) { // All Songs
			
			ArtistAllSongsFragment allSongsFragment = new ArtistAllSongsFragment( );
			allSongsFragment.setArtistId( ARTIST_ID );
			
			mActivity.transactFragment( allSongsFragment );
			
		} else if ( null != Singles && v.equals( Singles ) ) {
			
			// Load Singles
			
		} else {
			
			String albumID = ( String ) v.getTag( R.id.tag_album_id );
			
			AlbumsOneFragment albumFragment = new AlbumsOneFragment( );
			albumFragment.setAlbumId( albumID );
			
			mActivity.transactFragment( albumFragment );
			
		}
		
	}
	
}