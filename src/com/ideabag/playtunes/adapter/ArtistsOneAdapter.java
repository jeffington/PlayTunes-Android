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

public class ArtistsOneAdapter extends BaseAdapter {
	
	private Context mContext;
	private Cursor cursor;
	
	private String ARTIST_ID;
	
	public String ArtistName;
	
    private static final String[] albumsSelection = new String[] {
    	
    	MediaStore.Audio.Albums.ALBUM,
    	MediaStore.Audio.Albums.ARTIST,
    	MediaStore.Audio.Albums.NUMBER_OF_SONGS,
    	MediaStore.Audio.Albums.ALBUM_ART,
    	MediaStore.Audio.Albums._ID,
    	MediaStore.Audio.Artists._ID
    	
    };
	
	public ArtistsOneAdapter( Context context, String artist_id ) {
		super();
		
		mContext = context;
		ARTIST_ID = artist_id;
		
    	cursor = mContext.getContentResolver().query(
    			MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
    			albumsSelection,
				MediaStore.Audio.Artists._ID + "=?",
				new String[] {
					
					ARTIST_ID
					
				},
				MediaStore.Audio.Albums._ID
			);
    	
    	cursor.moveToFirst();
    	
    	try {
    		
    		ArtistName = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ARTIST ) );
    		
    	} catch( Exception e ) {
    		
    		ArtistName = mContext.getString( R.string.artist_unknown );
    		
    	}
    	
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
			
			convertView = li.inflate( R.layout.list_item_album, null );
			
		}
		
		cursor.moveToPosition( position );
		
		convertView.setTag( R.id.tag_album_id, cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums._ID ) ) );
		
		//String artistName = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ARTIST ) );
		String albumName = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.Albums.ALBUM ) );
		
		int songCount = cursor.getInt( cursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS ) );
		
		convertView.findViewById( R.id.AlbumArtist ).setVisibility( View.GONE );
		//( ( TextView ) convertView.findViewById( R.id.AlbumArtist )).setText( artistName );
		( ( TextView ) convertView.findViewById( R.id.AlbumTitle )).setText( albumName );
		
		( ( TextView ) convertView.findViewById( R.id.BadgeCount )).setText( "" + songCount );
		
		//
		// Set the album art
		//
		
		Uri albumArtUri = Uri.parse( cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.Albums.ALBUM_ART ) ) );
		
		( ( ImageView ) convertView.findViewById( R.id.AlbumArtThumb )).setImageURI( albumArtUri );
		
		return convertView;
		
	}

}
