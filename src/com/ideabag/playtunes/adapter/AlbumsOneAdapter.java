package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AlbumsOneAdapter extends BaseAdapter {
	
	private String ALBUM_ID;
	
	private Context mContext;
	private Cursor cursor;
	
	public Uri albumArtUri = null;
	
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
	
	public AlbumsOneAdapter( Context context, String album_id ) {
		
		mContext = context;
		
		ALBUM_ID = album_id;
		
		Log.i("ALBUM_ID", ALBUM_ID);
		
    	cursor = mContext.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				singleAlbumSelection,
				MediaStore.Audio.Media.ALBUM_ID + "=?",
				new String[] {
					
					ALBUM_ID
					
				},
				MediaStore.Audio.Media.TRACK
			);
    	
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
    	
    	albumArtUri = Uri.parse( albumUriString );
    	
    	albumTitle = album.getString( album.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM ) );
		
	}
	
	public Cursor getCursor() {
		
		return cursor;
		
	}
	
	@Override public int getCount() {
		
		return cursor.getCount();
		
	}

	@Override public Object getItem( int position ) {
		
		return null;
		
	}

	@Override public long getItemId( int position ) {
		
		return 0;
		
	}
	
	@Override public View getView( int position, View convertView, ViewGroup parent ) {
		
		if ( null == convertView ) {
			
			LayoutInflater li = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			
			convertView = li.inflate( R.layout.list_item_song_no_album, null );
			
		}
		
		cursor.moveToPosition( position );
		
		String songTitle = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
		
		String songArtist = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
		
		convertView.findViewById(R.id.SongAlbum).setVisibility( View.GONE );
		
		
		( ( TextView ) convertView.findViewById( R.id.SongTitle )).setText( songTitle );
		( ( TextView ) convertView.findViewById( R.id.SongArtist )).setText( songArtist );
		
		return convertView;
		
	}

}
