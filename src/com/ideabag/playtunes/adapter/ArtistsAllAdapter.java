package com.ideabag.playtunes.adapter;
import com.ideabag.playtunes.R;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ArtistsAllAdapter extends BaseAdapter {
	
	private Context mContext;
	private Cursor cursor;
	
	public String ARTIST_NAME;
	
	private static final String[] artistsSelection = new String[] {
		
    	MediaStore.Audio.Artists.ARTIST,
    	MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
    	MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
    	MediaStore.Audio.Artists._ID 
    	
	};
	
	public ArtistsAllAdapter( Context context ) {
		super();
		
		mContext = context;
		
    	cursor = mContext.getContentResolver().query(
				MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
				artistsSelection,
				null,
				null,
				MediaStore.Audio.Artists.ARTIST
			);
    	
    	cursor.moveToFirst();
    	
    	
		
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
			
			convertView = li.inflate( R.layout.list_item_genre, null );
			
		}
		cursor.moveToPosition( position );
		
		
		String artistName = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.ARTIST ) ).trim();
		int songCount = cursor.getInt( cursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.NUMBER_OF_TRACKS ) );
		int albumCount = cursor.getInt( cursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.NUMBER_OF_ALBUMS ) );
		
		
		convertView.setTag( R.id.tag_artist_id, cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Artists._ID ) ) );
		
		if ( mContext.getString( R.string.no_artist_string ).equals( artistName ) ) {
			
			( ( TextView ) convertView.findViewById( R.id.Title )).setText( mContext.getString( R.string.artist_unknown ) );
			convertView.setTag( R.id.tag_artist_unknown, "1" );
			
		} else {
			
			( ( TextView ) convertView.findViewById( R.id.Title )).setText( artistName );
			convertView.setTag( R.id.tag_artist_unknown, "0" );
			
		}
		
		LinearLayout songBadge, albumBadge;
		
		songBadge = ( LinearLayout ) convertView.findViewById( R.id.BadgeSong );
		
		albumBadge = ( LinearLayout ) convertView.findViewById( R.id.BadgeAlbum );
		
		( ( TextView ) songBadge.findViewById( R.id.BadgeCount )).setText( "" + songCount );
		( ( TextView ) albumBadge.findViewById( R.id.BadgeCount )).setText( "" + albumCount );
		
		return convertView;
		
	}

}
