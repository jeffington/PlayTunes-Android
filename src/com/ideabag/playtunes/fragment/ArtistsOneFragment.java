package com.ideabag.playtunes.fragment;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.ArtistsOneAdapter;

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
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class ArtistsOneFragment extends ListFragment {
	
	private ViewGroup AllSongs;
	private ViewGroup Singles;
	
	private TextView albumDivider;
	
	ArtistsOneAdapter adapter;
	
	private MainActivity mActivity;
	
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
    	
    	int songCount = songCountCursor.getCount();
    	songCountCursor.close();
    	
    	int singlesCount = singlesCountCursor.getCount();
    	singlesCountCursor.close();
    	
    	LayoutInflater inflater = mActivity.getLayoutInflater();
    	
    	AllSongs = ( ViewGroup ) inflater.inflate( R.layout.list_item_title_one_badge, null );
    	( ( TextView ) AllSongs.findViewById( R.id.BadgeCount ) ).setText( "" + songCount );
    	( ( TextView ) AllSongs.findViewById( R.id.Title ) ).setText( getString( R.string.artist_all_songs ) );
    	
    	Singles = ( ViewGroup ) inflater.inflate( R.layout.list_item_title_one_badge, null );
    	( ( TextView ) Singles.findViewById( R.id.BadgeCount ) ).setText( "" + singlesCount );
    	( ( TextView ) Singles.findViewById( R.id.Title ) ).setText( getString( R.string.artist_singles ) );
    	
    	albumDivider = ( TextView ) inflater.inflate( R.layout.list_item_header_albums, null );
    	
    	getListView().addHeaderView( AllSongs, null, true );
    	getListView().addHeaderView( Singles, null, true );
    	getListView().addHeaderView( albumDivider, null, false );
    	
    	setListAdapter( adapter );
    	
    	bar.setTitle( adapter.ArtistName );
		
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
    	
	}
	
	@Override public void onResume() {
		super.onResume();
		
		
	}
		
	@Override public void onPause() {
		super.onPause();
		
		
		
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		//android.util.Log.i( "Clicked", "" + position);
		
		if ( 0 == position ) { // All Songs
			
			ArtistAllSongsFragment allSongsFragment = new ArtistAllSongsFragment( );
			allSongsFragment.setArtistId( ARTIST_ID );
			
			mActivity.transactFragment( allSongsFragment );
			
		} else if ( 1 == position ) { // Singles
			// Any singles?
			TextView badge = ( ( TextView ) v.findViewById( R.id.BadgeCount ) );
			
			android.util.Log.i( "Singles Click", badge.getText().toString() );
			
			if ( "0".equals( badge.getText().toString() ) ) {
				
				//badge.setType
				badge.setTypeface(null, Typeface.BOLD);
				badge.setTextColor( getResources().getColor( android.R.color.black ) );
				badge.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 16 );
				
			} else {
				
				// Load the singles
				
			}
			
			
		} else {
			
			String albumID = ( String ) v.getTag( R.id.tag_album_id );
			
			AlbumsOneFragment albumFragment = new AlbumsOneFragment( );
			albumFragment.setAlbumId( albumID );
			
			mActivity.transactFragment( albumFragment );
			
		}
			
	}
	
}