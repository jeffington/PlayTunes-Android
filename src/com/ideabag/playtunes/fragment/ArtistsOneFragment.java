package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.ArtistsOneAdapter;
import com.ideabag.playtunes.util.PlaylistBrowser;
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

public class ArtistsOneFragment extends ListFragment implements PlaylistBrowser {
	
	public static final String TAG = "One Artist Fragment";
	
	private ViewGroup AllSongs;
	private ViewGroup Singles = null;
	
	private TextView albumDivider;
	
	ArtistsOneAdapter adapter;
	
	private MainActivity mActivity;
    
	private String ARTIST_ID = "";
	
	@Override public void setMediaID( String media_id ) {
		
		ARTIST_ID = media_id;
		
	}
	
	@Override public String getMediaID() { return ARTIST_ID; }
	
	@Override public void onAttach( Activity activity ) {
			
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
	
	@Override public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		outState.putString( getString( R.string.key_state_media_id ), ARTIST_ID );
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		if ( null != savedInstanceState ) {
			
			ARTIST_ID = savedInstanceState.getString( getString( R.string.key_state_media_id ) );
			
		}
		
		LayoutInflater inflater = mActivity.getLayoutInflater();
		
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
				MediaStore.Audio.Media.ARTIST_ID + "=? AND " + MediaStore.Audio.Media.ALBUM + "=?",
				new String[] {
					
					ARTIST_ID,
					getString( R.string.no_album_string )
					
				},
				null
			);
		
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
		
    	//getListView().addHeaderView( mActivity.AdContainer, null, true );
    	
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
    	
		getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
    	
    	setListAdapter( adapter );
    	
	}
	
	@Override public void onResume() {
		super.onResume();

    	
    	mActivity.setActionbarTitle( adapter.ArtistName );
    	mActivity.setActionbarSubtitle( getString( R.string.artist_singular ) );
		
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
		
		if ( v.equals( AllSongs ) ) { // All Songs
			
			ArtistAllSongsFragment allSongsFragment = new ArtistAllSongsFragment( );
			allSongsFragment.setMediaID( ARTIST_ID );
			
			mActivity.transactFragment( allSongsFragment );
			
		} else if ( null != Singles && v.equals( Singles ) ) { // Load Singles
			
			ArtistsOneFragment allSinglesFragment = new ArtistsOneFragment( );
			allSinglesFragment.setMediaID( ARTIST_ID );
			
			mActivity.transactFragment( allSinglesFragment );
			
		} else {
			
			String albumID = ( String ) v.getTag( R.id.tag_album_id );
			
			AlbumsOneFragment albumFragment = new AlbumsOneFragment( );
			albumFragment.setMediaID( albumID );
			
			mActivity.transactFragment( albumFragment );
			
		}
		
	}
	
}