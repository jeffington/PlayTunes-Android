package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ArtistAllSongsAdapter extends BaseAdapter {
	
	private Context mContext;
	Cursor cursor;
	private String ARTIST_ID;
	public String ARTIST_NAME;
	
	private PlaylistManager mPlaylistManager;
	View.OnClickListener songMenuClickListener;
	
    private static final String[] allSongsSelection = new String[] {
    	
    	MediaStore.Audio.Media._ID, // Needs to be at position 0
    	
    	MediaStore.Audio.Media.TITLE,
    	MediaStore.Audio.Media.ARTIST,
    	MediaStore.Audio.Media.ALBUM,
    	MediaStore.Audio.Media.TRACK,
    	MediaStore.Audio.Media.DATA,
    	MediaStore.Audio.Media.ALBUM_ID,
    	MediaStore.Audio.Media.ARTIST_ID
    	
    	
    };
    
	public ArtistAllSongsAdapter( Context context, String artist_id, View.OnClickListener clickListener ) {
		
		mContext = context;
		ARTIST_ID = artist_id;
		
		mPlaylistManager = new PlaylistManager( mContext );
		
		songMenuClickListener = clickListener;
		
    	cursor = mContext.getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					allSongsSelection,
					MediaStore.Audio.Media.ARTIST_ID + "=? AND " + MediaStore.Audio.Media.IS_MUSIC + " != 0",
					new String[] {
						
						ARTIST_ID
						
					},
					MediaStore.Audio.Media.TITLE
    			);
    	
    	cursor.moveToFirst();
    	
    	try {
    		
    		ARTIST_NAME = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
    		
    		if ( "<unknown>".equals( ARTIST_NAME ) ) {
        		
        		ARTIST_NAME = mContext.getString( R.string.artist_unknown );
        		
        	}
    		
    	} catch( Exception e ) {
    		
    		ARTIST_NAME = mContext.getString( R.string.artist_unknown );
    		
    	}
    	
	}
	
	public Cursor getCursor() {
		
		return cursor;
		
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
			
			convertView.findViewById( R.id.StarButton ).setOnClickListener( songMenuClickListener );
			convertView.findViewById( R.id.MenuButton ).setOnClickListener( songMenuClickListener );
			
		}
		
		cursor.moveToPosition( position );
		
		String songTitle = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
		String songArtist = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
		String songAlbum = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) );
		String song_id = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media._ID ) );
		
		( ( TextView ) convertView.findViewById( R.id.SongTitle ) ).setText( songTitle );
		( ( TextView ) convertView.findViewById( R.id.SongArtist ) ).setText( songArtist );
		( ( TextView ) convertView.findViewById( R.id.SongAlbum ) ).setText( songAlbum );
		
		convertView.findViewById( R.id.StarButton ).setTag( R.id.tag_song_id, song_id );
		convertView.findViewById( R.id.MenuButton ).setTag( R.id.tag_song_id, song_id );
		
		ToggleButton starButton = ( ToggleButton ) convertView.findViewById( R.id.StarButton );
		
		starButton.setChecked( mPlaylistManager.isStarred( song_id ) );
		
		return convertView;
		
	}

}
