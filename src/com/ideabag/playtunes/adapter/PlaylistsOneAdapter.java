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

public class PlaylistsOneAdapter extends BaseAdapter {
	
	protected Context mContext;
	protected Cursor cursor;
	
	private static final char MUSIC_NOTE = (char) 9834;
	
	private String PLAYLIST_ID;
	public String PLAYLIST_NAME;
	
    private static final String[] singlePlaylistSelection = new String[] { // And this...
    	/*
		MediaStore.Audio.Media.TITLE,
		MediaStore.Audio.Media.ARTIST,
		MediaStore.Audio.Media.TRACK,
		MediaStore.Audio.Media.ALBUM,
		MediaStore.Audio.Media.DATA,
		MediaStore.Audio.Media.ALBUM_ID,
		*/
    	MediaStore.Audio.Playlists.Members.TITLE,
    	MediaStore.Audio.Playlists.Members.ARTIST,
    	MediaStore.Audio.Playlists.Members.ALBUM,
		//MediaStore.Audio.Playlists._ID,
		
	};
    
	public PlaylistsOneAdapter( Context context, String playlist_id ) {
		
		mContext = context;
		PLAYLIST_ID = playlist_id;
		//PLAYLIST_NAME = name;
		
		cursor = context.getContentResolver().query(
				MediaStore.Audio.Playlists.Members.getContentUri( "external", Long.parseLong( PLAYLIST_ID ) ),
				singlePlaylistSelection,
				null,
				null,
				null
			);
	
		//cursor.moveToFirst();
		
		
		
	}
	
	@Override
	public int getCount() {
		
		return cursor.getCount();
	}

	@Override
	public Object getItem( int position ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId( int position ) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override public View getView( int position, View convertView, ViewGroup parent ) {
		
		if ( null == convertView ) {
			
			LayoutInflater li = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			
			convertView = li.inflate( R.layout.list_item_song_no_album, null );
			
		}
		
		cursor.moveToPosition( position );
		
		String songTitle = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Playlists.Members.TITLE ) );
		String songArtist = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
		String songAlbum = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) );
		
		( ( TextView ) convertView.findViewById( R.id.SongTitle )).setText( songTitle );
		( ( TextView ) convertView.findViewById( R.id.SongArtist )).setText( songArtist );
		( ( TextView ) convertView.findViewById( R.id.SongAlbum )).setText( songAlbum );
		
		return convertView;
		
	}

}
