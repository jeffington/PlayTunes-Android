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
	private LayoutInflater inflater;
	private Cursor cursor = null;
	
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
		inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		requery();
		
	}
	
	public void requery() {
		
		if ( null != cursor && !cursor.isClosed() ) {
			
			cursor.close();
			
		}
		
    	cursor = mContext.getContentResolver().query(
				MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
				artistsSelection,
				null,
				null,
				MediaStore.Audio.Artists.DEFAULT_SORT_ORDER
			);
		
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
			
			convertView = inflater.inflate( R.layout.list_item_genre, null );
			
			holder.albumCount = ( TextView ) convertView.findViewById( R.id.AlbumCount );
			holder.songCount = ( TextView ) convertView.findViewById( R.id.SongCount );
			holder.artistName = ( TextView ) convertView.findViewById( R.id.Title );
			
			convertView.setTag( holder );
			
		} else {
			
			holder = ( ViewHolder ) convertView.getTag();
			
		}
		
		cursor.moveToPosition( position );
		
		
		String artistName = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.ARTIST ) ).trim();
		int songCount = cursor.getInt( cursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.NUMBER_OF_TRACKS ) );
		int albumCount = cursor.getInt( cursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.NUMBER_OF_ALBUMS ) );
		
		
		convertView.setTag( R.id.tag_artist_id, cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Artists._ID ) ) );
		
		if ( mContext.getString( R.string.no_artist_string ).equals( artistName ) ) {
			
			holder.artistName.setText( mContext.getString( R.string.artist_unknown ) );
			convertView.setTag( R.id.tag_artist_unknown, "1" );
			
		} else {
			
			holder.artistName.setText( artistName );
			convertView.setTag( R.id.tag_artist_unknown, "0" );
			
		}
		
		holder.songCount.setText( "" + songCount );
		holder.albumCount.setText( "" + albumCount );
		
		return convertView;
		
	}
	
	static class ViewHolder {
		
		TextView artistName;
		TextView songCount;
		TextView albumCount;
		
		
	}

}
