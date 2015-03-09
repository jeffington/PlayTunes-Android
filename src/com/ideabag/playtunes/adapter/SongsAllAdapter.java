package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.database.MediaQuery;

import android.content.Context;
import android.provider.MediaStore;
import android.view.View;

public class SongsAllAdapter extends SongListAdapter {
	
    public static final String[] SELECTION = new String[] {
    	
    	MediaStore.Audio.Media._ID,
    	
    	MediaStore.Audio.Media.TITLE,
    	MediaStore.Audio.Media.ARTIST,
    	MediaStore.Audio.Media.ALBUM,
    	MediaStore.Audio.Media.TRACK,
    	MediaStore.Audio.Media.DATA,
    	MediaStore.Audio.Media.ALBUM_ID,
    	MediaStore.Audio.Media.ARTIST_ID
    	
    	
    };
    
	public SongsAllAdapter( Context context, View.OnClickListener menuClickListener, MediaQuery.OnQueryCompletedListener listener ) {
		super( context, menuClickListener );
		
		mQuery = new MediaQuery(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				SELECTION,
				MediaStore.Audio.Media.IS_MUSIC + " != 0",
				null,
				MediaStore.Audio.Media.TITLE
				);
		
    	setOnQueryCompletedListener( listener );
    	
    	requery();
    	
	}

}
