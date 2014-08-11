package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.database.MediaQuery;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;

public class AlbumsOneAdapter extends SongListAdapter {
	
	private String ALBUM_ID;
	
	public String albumArtUri = null;
	
	public String albumTitle = null;
	
	private static final String[] singleAlbumSelection = new String[] {
		
		// Media ID must always be at position 0
		MediaStore.Audio.Media._ID,
		
		MediaStore.Audio.Media.TITLE,
		MediaStore.Audio.Media.ARTIST,
		MediaStore.Audio.Media.TRACK,
		MediaStore.Audio.Media.ALBUM,
		MediaStore.Audio.Media.DATA,
		MediaStore.Audio.Media.ALBUM_ID
		
		
	};
	
	public AlbumsOneAdapter( Context context, String album_id, View.OnClickListener menuClickListener ) {
		super( context, menuClickListener );
		
		ALBUM_ID = album_id;
		
		mQuery = new MediaQuery(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				singleAlbumSelection,
				MediaStore.Audio.Media.ALBUM_ID + "=?",
				new String[] {
					
					ALBUM_ID
					
				},
				MediaStore.Audio.Media.TRACK
			);
    	
		requery();
		
	}
	
	@Override public void requery() {
		super.requery();
		
    	Cursor album = mContext.getContentResolver().query(
    			MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
    			new String[] {
    				
    				MediaStore.Audio.Albums.ALBUM,
    				MediaStore.Audio.Albums.ALBUM_ART,
    				MediaStore.Audio.Albums._ID
    				
    			},
    			MediaStore.Audio.Albums._ID + "=?",
				new String[] {
    				
    				ALBUM_ID
    				
    			},
    			null);
    			
    	
    	album.moveToFirst();
    	
    	String albumUriString = album.getString( album.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
    	
    	albumArtUri = albumUriString;
    	
    	albumTitle = album.getString( album.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM ) );
		album.close();
		
	}
	
	

}
