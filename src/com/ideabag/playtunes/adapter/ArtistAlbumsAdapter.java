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

public class ArtistAlbumsAdapter extends BaseAdapter {
	
	private Context mContext;
	private LayoutInflater inflater;
	private Cursor cursor;
	
	private String ARTIST_ID;
	
	public String ArtistName;
	
    private static final String[] albumsSelection = new String[] {
    	
    	MediaStore.Audio.Artists.Albums.ALBUM,
    	MediaStore.Audio.Artists.Albums.ALBUM_ART,
    	MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS,
    	MediaStore.Audio.Artists.Albums.ARTIST,
    	MediaStore.Audio.Albums._ID
    	
    	
    };
	
	public ArtistAlbumsAdapter( Context context, String artist_id ) {
		super();
		
		mContext = context;
		ARTIST_ID = artist_id;
		
		inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    	
		cursor = mContext.getContentResolver().query(
    			MediaStore.Audio.Artists.Albums.getContentUri( "external", Long.parseLong( ARTIST_ID ) ),
    			albumsSelection,
				null,
				null,
				MediaStore.Audio.Artists.Albums.ALBUM
			);
    	
    	cursor.moveToFirst();
    	
    	try {
    		
    		ArtistName = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.Albums.ARTIST ) );
    		
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
		
		ViewHolder holder;
		
		if ( null == convertView ) {
			
			holder = new ViewHolder();
			
			convertView = inflater.inflate( R.layout.list_item_album, null );
			
			holder.albumThumb = ( ImageView ) convertView.findViewById( R.id.AlbumArtThumb );
			holder.albumTitle = ( TextView ) convertView.findViewById( R.id.AlbumTitle );
			//holder.albumArtist = 
			holder.songCount = ( TextView ) convertView.findViewById( R.id.SongCount );
			convertView.setTag( holder );
			
		} else {
			
			holder = ( ViewHolder ) convertView.getTag();
			
		}
		
		cursor.moveToPosition( position );
		
		convertView.setTag( R.id.tag_album_id, cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums._ID ) ) );
		
		//String artistName = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ARTIST ) );
		String albumName = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.Albums.ALBUM ) );
		//MediaStore.Audio.Artists.Albums.ARTIST
		int songCount = cursor.getInt( cursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS ) );
		
		convertView.findViewById( R.id.AlbumArtist ).setVisibility( View.GONE );
		//( ( TextView ) convertView.findViewById( R.id.AlbumArtist )).setText( artistName );
		holder.albumTitle.setText( albumName );
		
		holder.songCount.setText( "" + songCount );
		
		//
		// Set the album art
		//
		
		Uri albumArtUri = null;
		
		try {
			
			albumArtUri = Uri.parse( cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.Albums.ALBUM_ART ) ) );
			
		} catch( Exception e ) { /* ... */ }
		
		if ( albumArtUri != null ) {
			
			holder.albumThumb.setImageURI( albumArtUri );
			
		} else {
			
			holder.albumThumb.setImageResource( R.drawable.no_album_art_thumb );
			
		}
		
		return convertView;
		
	}
	
	static class ViewHolder {
		
		TextView albumTitle;
		//TextView albumArtist;
		TextView songCount;
		ImageView albumThumb;
		
	}

}
