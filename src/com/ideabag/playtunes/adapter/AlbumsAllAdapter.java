package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumsAllAdapter extends BaseAdapter {
	
	protected Context mContext;
	protected Cursor cursor;
	
    private static final String[] allAlbumsSelection = new String[] {
    	
    	MediaStore.Audio.Albums.ALBUM,
    	MediaStore.Audio.Albums.ARTIST,
    	MediaStore.Audio.Albums.NUMBER_OF_SONGS,
    	MediaStore.Audio.Albums.ALBUM_ART,
    	MediaStore.Audio.Albums._ID
    	
    };
	
	public AlbumsAllAdapter( Context context ) {
		
		mContext = context;
		
    	cursor = mContext.getContentResolver().query(
				MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
				allAlbumsSelection,
				MediaStore.Audio.Media.ALBUM + "!='Music'",
				null,
				MediaStore.Audio.Albums.ALBUM
			);
		
	}
	
	@Override public int getCount() {
		
		return cursor.getCount();
		
	}

	@Override public Object getItem( int position ) {
		
		return null;
		
	}

	@Override public long getItemId(int position) {
		
		return 0;
		
	}

	@Override public View getView( int position, View convertView, ViewGroup parent ) {
		
		if ( null == convertView ) {
			
			LayoutInflater li = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			
			convertView = li.inflate( R.layout.list_item_album, null );
			
		}
		
		cursor.moveToPosition( position );
		
		convertView.setTag( R.id.tag_album_id, cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums._ID ) ) );
		
		String artistName = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ARTIST ) );
		String albumName = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM ) );
		
		int songCount = cursor.getInt( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.NUMBER_OF_SONGS ) );
		
		( ( TextView ) convertView.findViewById( R.id.AlbumArtist )).setText( artistName );
		( ( TextView ) convertView.findViewById( R.id.AlbumTitle )).setText( albumName );
		
		( ( TextView ) convertView.findViewById( R.id.BadgeCount )).setText( "" + songCount );
		
		//
		// Set the album art
		//
		
		ImageView albumArtThumb = ( ImageView ) convertView.findViewById( R.id.AlbumArtThumb );
		String albumArtUriString = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
		
		if ( null != albumArtUriString ) {
			
			Uri albumArtUri = Uri.parse( albumArtUriString );
			
			albumArtThumb.setImageURI( albumArtUri );
			
		} else {
			
			albumArtThumb.setImageResource( R.drawable.no_album_art );
			
		}
		
		return convertView;
		
	}
	
}
