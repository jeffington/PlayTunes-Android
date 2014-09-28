package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.database.MediaQuery;

import android.content.Context;
import android.provider.MediaStore;
import android.view.View;

public class ArtistSinglesAdapter extends SongListAdapter {

	private String ARTIST_ID;
	
    private static final String[] allSongsSelection = new String[] {
    	
    	MediaStore.Audio.Media._ID, // Needs to be at position 0
    	
    	MediaStore.Audio.Media.TITLE,
    	MediaStore.Audio.Media.ARTIST,
    	MediaStore.Audio.Media.ALBUM,
    	MediaStore.Audio.Media.TRACK,
    	MediaStore.Audio.Media.DATA,
    	MediaStore.Audio.Media.ALBUM_ID,
    	MediaStore.Audio.Media.ARTIST_ID
    	
    };
    
	public ArtistSinglesAdapter( Context context, String artist_id, View.OnClickListener menuClickListener, MediaQuery.OnQueryCompletedListener listener  ) {
		super( context, menuClickListener );
		
		ARTIST_ID = artist_id;
    	
		MediaQuery query = new MediaQuery(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				allSongsSelection,
				MediaStore.Audio.Media.ARTIST_ID + "=? AND " + MediaStore.Audio.Media.ALBUM + "=?  AND " + MediaStore.Audio.Media.IS_MUSIC + " != 0",
				new String[] {
					
					ARTIST_ID,
					context.getString( R.string.no_album_string )
					
				},
				MediaStore.Audio.Media.TITLE
			);
		
		setOnQueryCompletedListener( listener );
		
		requery();
		
	}

}
