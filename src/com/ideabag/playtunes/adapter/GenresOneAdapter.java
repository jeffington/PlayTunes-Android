package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.database.MediaQuery;

import android.content.Context;
import android.provider.MediaStore;
import android.view.View;

public class GenresOneAdapter extends SongListAdapter {
	
	protected String GENRE_ID;
	
    private static final String[] oneGenreSelection = new String[] {
    	
    	// Audio media ID must always be at position 0
    	MediaStore.Audio.Genres.Members._ID,
    	
		MediaStore.Audio.Genres.Members.ALBUM,
		MediaStore.Audio.Genres.Members.TITLE,
		MediaStore.Audio.Genres.Members.ARTIST,
		MediaStore.Audio.Genres.Members.DATA,
		MediaStore.Audio.Genres.Members.DURATION
		
    };
    
	public GenresOneAdapter( Context context, String genre_id, View.OnClickListener menuClickListener ) {
		super( context, menuClickListener );
		
		this.GENRE_ID = genre_id;
		
		mQuery = new MediaQuery(
				MediaStore.Audio.Genres.Members.getContentUri( "external", Long.parseLong( GENRE_ID ) ),
				oneGenreSelection
			);
		
		requery();
		
	}

}
