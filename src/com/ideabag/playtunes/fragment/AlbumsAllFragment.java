package com.ideabag.playtunes.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.AlbumsAllAdapter;
import com.ideabag.playtunes.util.PlaylistBrowser;
import com.ideabag.playtunes.util.TrackerSingleton;

public class AlbumsAllFragment extends ListFragment implements PlaylistBrowser  {
	
	public static final String TAG = "All Albums Fragment";

	AlbumsAllAdapter adapter;
	private MainActivity mActivity;
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
    	adapter = new AlbumsAllAdapter( getActivity() );
    	
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
		getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
		
		setListAdapter( adapter );
    	
	}
	
	@Override public void onResume() {
		super.onResume();
		
    	mActivity.setActionbarTitle( getString( R.string.albums_plural) );
    	mActivity.setActionbarSubtitle( adapter.getCount() + " " + ( adapter.getCount() == 1 ? getString( R.string.album_singular ) : getString( R.string.albums_plural ) ) );
		
		Tracker t = TrackerSingleton.getDefaultTracker( mActivity.getBaseContext() );

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
		
		//mActivity.AdView.resume();
		
	}
	
	@Override public void onPause() {
		super.onPause();
		
		//mActivity.AdView.pause();
		
	}
	
	@Override public void onDestroyView() {
	    super.onDestroyView();
	    
	    setListAdapter( null );
	    
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		//convertView.setTag( R.id.tag_album_id, cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM ) ) );
		
		String albumID = ( String ) v.getTag( R.id.tag_album_id );
		
		AlbumsOneFragment albumFragment = new AlbumsOneFragment( );
		albumFragment.setMediaID( albumID );
		
		mActivity.transactFragment( albumFragment );
		
	}
	
	// PlaylistBrowser interface methods
	
	@Override public void setMediaID(String media_id) { /* ... */ }

	@Override public String getMediaID() { return ""; }
	
}