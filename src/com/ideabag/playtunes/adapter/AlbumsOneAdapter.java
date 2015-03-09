package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.util.StarToggleTask;
import com.ideabag.playtunes.widget.StarButton;

import android.content.Context;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;


public class AlbumsOneAdapter extends SongListAdapter {
	
	private String ALBUM_ID;
	
	private static final String[] singleAlbumSelection = new String[] {
		
		// Media ID must always be at position 0
		MediaStore.Audio.Media._ID,
		
		MediaStore.Audio.Media.TITLE,
		MediaStore.Audio.Media.ARTIST,
		MediaStore.Audio.Media.TRACK,
		MediaStore.Audio.Media.ALBUM,
		MediaStore.Audio.Media.DATA,
		MediaStore.Audio.Media.ALBUM_ID,
    	MediaStore.Audio.Media.ARTIST_ID
		
	};
	
	public AlbumsOneAdapter( Context context, String album_id, View.OnClickListener menuClickListener, MediaQuery.OnQueryCompletedListener listener  ) {
		super( context, menuClickListener );
		
		ALBUM_ID = album_id;
		
		MediaQuery query = new MediaQuery(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				singleAlbumSelection,
				MediaStore.Audio.Media.ALBUM_ID + "=?",
				new String[] {
					
					ALBUM_ID
					
				},
				MediaStore.Audio.Media.TRACK
			);
		
		setOnQueryCompletedListener( listener );
    	
		setMediaQuery( query );
		
	}
	
	
	@Override public View getView( int position, View convertView, ViewGroup parent ) {
		
		ViewHolder holder;
		
		if ( null == convertView ) {
			
			convertView = inflater.inflate( R.layout.list_item_song_no_album, null );
			
			holder = new ViewHolder();
			
			holder.starButton = ( StarButton ) convertView.findViewById( R.id.StarButton );
			holder.menuButton = ( ImageButton ) convertView.findViewById( R.id.MenuButton );
			holder.songTitle = ( TextView ) convertView.findViewById( R.id.SongTitle );
			holder.songArtist = ( TextView ) convertView.findViewById( R.id.SongArtist );
			holder.songAlbum = ( TextView ) convertView.findViewById( R.id.SongAlbum );
			holder.row = ( LinearLayout ) convertView;
			
			holder.songArtist.setVisibility( View.GONE );
			holder.songAlbum.setVisibility( View.GONE );
			
			holder.starButton.setOnClickListener( songMenuClickListener );
			holder.menuButton.setOnClickListener( songMenuClickListener );
			
			convertView.setTag( holder );
			
		} else {
			
			holder = ( ViewHolder ) convertView.getTag();
			
			StarToggleTask starTask = ( StarToggleTask ) holder.starButton.getTag();
			
			if ( starTask != null && !starTask.isCancelled() ) {
				
				starTask.cancel( true );
				
			}
			
		}
		
		mCursor.moveToPosition( position );
		
		String songTitle = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
		String song_id = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Media._ID ) );
		
		StarToggleTask starTask = new StarToggleTask( holder.starButton );
		
		holder.starButton.setTag( starTask );
		holder.starButton.setTag( R.id.tag_song_id, song_id );
		starTask.execute( song_id );
		
		holder.songTitle.setText( songTitle );
		
		
		holder.menuButton.setTag( R.id.tag_song_id, song_id );
		
		if ( null != mNowPlayingMediaID && mNowPlayingMediaID.equals( song_id ) ) {
			
			// Is now playing
			holder.row.setBackgroundResource( R.drawable.indicator );
			
		} else {
			
			holder.row.setBackgroundResource( android.R.color.transparent );
			
		}
		
		return convertView;
		
	}

}
