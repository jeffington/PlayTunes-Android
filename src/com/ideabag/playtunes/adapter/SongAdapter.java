package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SongAdapter extends BaseAdapter {
	
	protected Context mContext;
	protected Cursor cursor;
	
    
	public SongAdapter( Context context ) {
		
		mContext = context;
		
		
	}
	
	public SongAdapter( Cursor songsCursor ) {
		
		cursor = songsCursor;
		
	}
	
	@Override
	public int getCount() {
		
		return cursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
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
		String songAlbum = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) );
		
		( ( TextView ) convertView.findViewById( R.id.SongTitle )).setText( songTitle );
		( ( TextView ) convertView.findViewById( R.id.SongArtist )).setText( songArtist );
		( ( TextView ) convertView.findViewById( R.id.SongAlbum )).setText( songAlbum );
		
		return convertView;
		
	}

}
