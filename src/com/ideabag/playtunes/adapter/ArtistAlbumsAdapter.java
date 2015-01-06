package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.database.MediaQuery;

import android.content.Context;
import android.provider.MediaStore;

public class ArtistAlbumsAdapter extends AlbumListAdapter {
	
	private String ARTIST_ID;
	
    private static final String[] albumsSelection = new String[] {
    	
    	MediaStore.Audio.Artists.Albums.ALBUM,
    	MediaStore.Audio.Artists.Albums.ALBUM_ART,
    	MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS,
    	MediaStore.Audio.Artists.Albums.ARTIST,
    	MediaStore.Audio.Albums._ID
    	
    };
	
	public ArtistAlbumsAdapter( Context context, String artist_id, MediaQuery.OnQueryCompletedListener listener ) {
		super( context );
		
		ARTIST_ID = artist_id;
		if ( null != ARTIST_ID && !artist_id.equals("") ) {
			
			mQuery = new MediaQuery(
	    			MediaStore.Audio.Artists.Albums.getContentUri( "external", Long.parseLong( ARTIST_ID ) ),
	    			albumsSelection,
					null,
					null,
					MediaStore.Audio.Artists.Albums.ALBUM
				);
	    	
			setOnQueryCompletedListener( listener );
			
			requery();
			
		}
		
	}
	
}
