package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.database.MediaQuery;

import android.content.Context;
import android.provider.MediaStore;

public class ArtistsAllAdapter extends ArtistListAdapter {
	
	private static final String[] artistsSelection = new String[] {
		
    	MediaStore.Audio.Artists.ARTIST,
    	MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
    	MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
    	MediaStore.Audio.Artists._ID 
    	
	};
	
	public ArtistsAllAdapter( Context context, MediaQuery.OnQueryCompletedListener listener ) {
		super( context );
		
    	mQuery = new MediaQuery(
				MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
				artistsSelection,
				null,
				null,
				MediaStore.Audio.Artists.DEFAULT_SORT_ORDER
			);
    	
    	setOnQueryCompletedListener( listener );
    	
		requery();
		
	}
	
}
