package com.ideabag.playtunes.fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.ArtistsAllAdapter;
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
import android.widget.TextView;

public class ArtistsAllFragment extends ListFragment {
	
	public static final String TAG = "All Artists Fragment";

	ArtistsAllAdapter adapter;
	private MainActivity mActivity;
    private AdView adView;
	
	@Override public void onAttach( Activity activity ) {
			
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		ActionBar bar =	mActivity.getSupportActionBar();
    	
    	adapter = new ArtistsAllAdapter( getActivity() );
    	
    	
    	bar.setTitle( "Artists" );
		bar.setSubtitle( adapter.getCount() + " artists" );
		
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
		getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
    	//getListView().addHeaderView( mActivity.AdContainer, null, true );
    	
    	setListAdapter( adapter );
    	
	}
		
	@Override public void onResume() {
		super.onResume();
		
		Tracker t = TrackerSingleton.getDefaultTracker( mActivity );

	        // Set screen name.
	        // Where path is a String representing the screen name.
		t.setScreenName( TAG );
		//t.set( "_count", ""+adapter.getCount() );
		
	        // Send a screen view.
		t.send( new HitBuilders.AppViewBuilder().build() );
		
		t.send( new HitBuilders.EventBuilder()
    	.setCategory( "playlist" )
    	.setAction( "show" )
    	.setLabel( TAG )
    	.setValue( adapter.getCount() )
    	.build());
		
	}
		
	@Override public void onPause() {
		super.onPause();
			
			
			
	}
	
	@Override public void onDestroyView() {
	    super.onDestroyView();
	    
	    setListAdapter( null );
	    
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		String artistID = ( String ) v.getTag( R.id.tag_artist_id );
		
		int albumCount = Integer.parseInt( ( ( TextView ) v.findViewById( R.id.BadgeAlbum ).findViewById( R.id.BadgeCount ) ).getText().toString() );
		
		if ( 0 == albumCount ) {
			
			ArtistAllSongsFragment artistAllFragment = new ArtistAllSongsFragment();
			artistAllFragment.setArtistId( artistID );
			
			mActivity.transactFragment( artistAllFragment );
			
		} else {
			
			ArtistsOneFragment artistFragment = new ArtistsOneFragment();
			artistFragment.setArtistId( artistID );
			
			mActivity.transactFragment( artistFragment );
			
		}
		
	}
	
}