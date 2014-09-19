package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.database.MediaQuery;

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

public class AlbumListAdapter extends BaseAdapter {
	
	protected Context mContext;
	protected LayoutInflater inflater;
	protected Cursor cursor = null;
	protected MediaQuery mQuery;
	
	public AlbumListAdapter( Context context ) {
		
		mContext = context;

		inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
	}
	
	public void requery() {
		
		if ( null != cursor && !cursor.isClosed() ) {
			
			cursor.close();
			
		}
		
    	cursor = MediaQuery.execute( mContext, mQuery );
		
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
		
		ViewHolder holder;
		
		if ( null == convertView ) {
			
			holder = new ViewHolder();
			
			convertView = inflater.inflate( R.layout.list_item_album, null );
			
			holder.albumArtist = ( TextView ) convertView.findViewById( R.id.AlbumArtist );
			holder.albumTitle = ( TextView ) convertView.findViewById( R.id.AlbumTitle );
			holder.albumThumb = ( ImageView ) convertView.findViewById( R.id.AlbumArtThumb );
			holder.songCount = ( TextView ) convertView.findViewById( R.id.SongCount );
			
			convertView.setTag( holder );
			
		} else {
			
			holder = ( ViewHolder ) convertView.getTag();
			
		}
		
		cursor.moveToPosition( position );
		
		convertView.setTag( R.id.tag_album_id, cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums._ID ) ) );
		
		String artistName = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ARTIST ) );
		String albumName = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM ) );
		
		int songCount = cursor.getInt( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.NUMBER_OF_SONGS ) );
		
		holder.albumArtist.setText( artistName );
		holder.albumTitle.setText( albumName );
		
		holder.songCount.setText( "" + songCount );
		
		//
		// Set the album art
		//
		
		String albumArtUriString = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
		
		if ( null != albumArtUriString ) {
			
			Uri albumArtUri = Uri.parse( albumArtUriString );
			
			holder.albumThumb.setImageURI( albumArtUri );
			
		} else {
			
			holder.albumThumb.setImageResource( R.drawable.no_album_art_thumb );
			
		}
		
		
		return convertView;
		
	}
	
	static class ViewHolder {
		
		ImageView albumThumb;
		TextView songCount;
		TextView albumTitle;
		TextView albumArtist;
		
	}
	
	public MediaQuery getQuery() { return mQuery; }
	
}
