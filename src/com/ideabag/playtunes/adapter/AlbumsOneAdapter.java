package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.adapter.SongListAdapter.ViewHolder;
import com.ideabag.playtunes.database.MediaQuery;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AlbumsOneAdapter extends SongListAdapter {
	
	private String ALBUM_ID;
	
	public String albumArtUri = null;
	
	public String albumTitle = null;
	public String albumArtist = null;
	
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
	
	public AlbumsOneAdapter( Context context, String album_id, View.OnClickListener menuClickListener ) {
		super( context, menuClickListener );
		
		ALBUM_ID = album_id;
		
		mQuery = new MediaQuery(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				singleAlbumSelection,
				MediaStore.Audio.Media.ALBUM_ID + "=?",
				new String[] {
					
					ALBUM_ID
					
				},
				MediaStore.Audio.Media.TRACK
			);
    	
		requery();
		
	}
	
	@Override public void requery() {
		super.requery();
		
    	Cursor album = mContext.getContentResolver().query(
    			MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
    			new String[] {
    				
    				MediaStore.Audio.Albums.ALBUM,
    				MediaStore.Audio.Albums.ALBUM_ART,
    				MediaStore.Audio.Albums.ARTIST,
    				MediaStore.Audio.Albums._ID
    				
    			},
    			MediaStore.Audio.Albums._ID + "=?",
				new String[] {
    				
    				ALBUM_ID
    				
    			},
    			null);
    			
    	
    	album.moveToFirst();
    	
    	String albumUriString = album.getString( album.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
    	
    	albumArtUri = albumUriString;
    	
    	albumTitle = album.getString( album.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM ) );
    	albumArtist = album.getString( album.getColumnIndex( MediaStore.Audio.Albums.ARTIST ) );
		album.close();
		
	}
	
	@Override public View getView( int position, View convertView, ViewGroup parent ) {
		
		ViewHolder holder;
		
		if ( null == convertView ) {
			
			convertView = inflater.inflate( R.layout.list_item_song_no_album, null );
			
			holder = new ViewHolder();
			
			holder.starButton = ( ToggleButton ) convertView.findViewById( R.id.StarButton );
			holder.menuButton = ( ImageButton ) convertView.findViewById( R.id.MenuButton );
			holder.songTitle = ( TextView ) convertView.findViewById( R.id.SongTitle );
			//holder.songArtist = ( TextView ) convertView.findViewById( R.id.SongArtist );
			//holder.songAlbum = ( TextView ) convertView.findViewById( R.id.SongAlbum );
			convertView.findViewById( R.id.SongAlbum ).setVisibility( View.GONE );
			convertView.findViewById( R.id.SongArtist ).setVisibility( View.GONE );
			
			holder.starButton.setOnClickListener( songMenuClickListener );
			holder.menuButton.setOnClickListener( songMenuClickListener );
			
			convertView.setTag( holder );
			
		} else {
			
			holder = ( ViewHolder ) convertView.getTag();
			
		}
		
		cursor.moveToPosition( position );
		
		String songTitle = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
		String songArtist = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
		String songAlbum = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) );
		String song_id = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media._ID ) );
		
		holder.songTitle.setText( songTitle );
		//holder.songArtist.setText( songArtist );
		//holder.songAlbum.setText( songAlbum );
		
		holder.starButton.setTag( R.id.tag_song_id, song_id );
		holder.menuButton.setTag( R.id.tag_song_id, song_id );
		
		holder.starButton.setChecked( PlaylistManager.isStarred( song_id ) ); 
		
		if ( null != mNowPlayingMediaID && mNowPlayingMediaID.equals( song_id ) ) {
			
			//convertView.setBackgroundResource(resid)
			holder.songTitle.setTextColor( mContext.getResources().getColor( R.color.primaryAccentColor ) );
			
			
		} else {
			
			holder.songTitle.setTextColor( mContext.getResources().getColor( R.color.textColorPrimary ) );
			
			//convertView.setBackgroundResource(resid)
			
		}
		
		return convertView;
		
	}

}
