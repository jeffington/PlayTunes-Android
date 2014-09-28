package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.database.MediaQuery;

import android.content.Context;
import android.provider.MediaStore;

public class AlbumsAllAdapter extends AlbumListAdapter {
	
    private static final String[] allAlbumsSelection = new String[] {
    	
    	MediaStore.Audio.Albums.ALBUM,
    	MediaStore.Audio.Albums.ARTIST,
    	MediaStore.Audio.Albums.NUMBER_OF_SONGS,
    	MediaStore.Audio.Albums.ALBUM_ART,
    	MediaStore.Audio.Albums._ID,
    	
    	
    };
	
	public AlbumsAllAdapter( Context context, MediaQuery.OnQueryCompletedListener listener ) {
		super( context );
		
		mQuery = new MediaQuery(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
				allAlbumsSelection,
				MediaStore.Audio.Media.ALBUM + "!=?",
				new String[] {
						mContext.getString( R.string.no_album_string )
				},
				MediaStore.Audio.Albums.DEFAULT_SORT_ORDER
			);
		
		setOnQueryCompletedListener( listener );
		
		requery();
		
	}
	
}
