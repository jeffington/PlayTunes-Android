package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.util.StarToggleTask;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class PlaylistsOneAdapter extends SongListAdapter {
	
	private String PLAYLIST_ID;
	public String PLAYLIST_NAME;
	
	public boolean isEditing = false;
	
    private static final String[] singlePlaylistSelection = new String[] {
    	
    	// The ID of the media item must be the first thing in the selection
    	MediaStore.Audio.Playlists.Members.AUDIO_ID,
    	
    	MediaStore.Audio.Playlists.Members.TITLE,
    	MediaStore.Audio.Playlists.Members.ARTIST,
    	MediaStore.Audio.Playlists.Members.ALBUM,
    	MediaStore.Audio.Playlists.Members.DATA,
    	MediaStore.Audio.Playlists.Members._ID,
    	MediaStore.Audio.Playlists.Members.PLAYLIST_ID,
    	MediaStore.Audio.Playlists.Members.PLAY_ORDER
		
	};
    
    public PlaylistsOneAdapter( Context context, String playlist_id, View.OnClickListener menuClickListener, MediaQuery.OnQueryCompletedListener listener ) {
    	this( context, playlist_id, menuClickListener );
    	
    	setOnQueryCompletedListener( listener );
    	
    }
    public PlaylistsOneAdapter( Context context, String playlist_id, View.OnClickListener menuClickListener ) {
		
		super( context, menuClickListener );
		PLAYLIST_ID = playlist_id;
		
		//android.util.Log.i( "starred adapter", "" + playlist_id );
		
		mQuery = new MediaQuery(
				MediaStore.Audio.Playlists.Members.getContentUri( "external", Long.parseLong( PLAYLIST_ID ) ),
				singlePlaylistSelection,
				MediaStore.Audio.Playlists.Members.PLAYLIST_ID + "=?",
				new String[] {
					
					PLAYLIST_ID
					
				},
				MediaStore.Audio.Playlists.Members.PLAY_ORDER
			);
		
		requery();
		
	}

	@Override public View getView( int position, View convertView, ViewGroup parent ) {
		
		ViewHolder holder;
		
		if ( null == convertView ) {
			
			holder = new ViewHolder();
			
			convertView = inflater.inflate( R.layout.list_item_playlist_song, null );
			
			holder.starButton = ( ToggleButton ) convertView.findViewById( R.id.StarButton );
			holder.starButton.setOnClickListener( songMenuClickListener );
			
			holder.menuButton = ( ImageButton ) convertView.findViewById( R.id.MenuButton );
			holder.menuButton.setOnClickListener( songMenuClickListener );
			
			holder.removeButton = ( ImageButton ) convertView.findViewById( R.id.RemoveButton );
			holder.removeButton.setOnClickListener( songMenuClickListener );
			
			holder.dragButton = convertView.findViewById( R.id.DragButton );
			
			holder.songTitle = ( TextView ) convertView.findViewById( R.id.SongTitle );
			holder.songAlbum = ( TextView ) convertView.findViewById( R.id.SongAlbum );
			holder.songArtist = ( TextView ) convertView.findViewById( R.id.SongArtist );
			
			convertView.setTag( holder );
			
		} else {
			
			holder = ( ViewHolder ) convertView.getTag();
			
		}
		
		mCursor.moveToPosition( position );
		
		String songTitle = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Playlists.Members.TITLE ) );
		String songArtist = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
		String songAlbum = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) );
		String song_id = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Playlists.Members.AUDIO_ID ) );
		
		new StarToggleTask( holder.starButton ).execute( song_id );
		
		holder.songTitle.setText( songTitle );
		holder.songArtist.setText( songArtist );
		holder.songAlbum.setText( songAlbum );
		
		holder.starButton.setTag( R.id.tag_song_id, song_id );
		holder.menuButton.setTag( R.id.tag_song_id, song_id );
		holder.removeButton.setTag( R.id.tag_song_id, song_id );
		
		
		if ( !this.isEditing ) {
			
			holder.starButton.setVisibility( View.VISIBLE );
			holder.menuButton.setVisibility( View.VISIBLE );
			holder.dragButton.setVisibility( View.GONE );
			holder.removeButton.setVisibility( View.GONE );
			
		} else {
			
			holder.starButton.setVisibility( View.GONE );
			holder.menuButton.setVisibility( View.GONE );
			holder.dragButton.setVisibility( View.VISIBLE );
			holder.removeButton.setVisibility( View.VISIBLE );
			
		}
		
		return convertView;
		
	}
	
	
	public void setEditing( boolean editing ) {
		
		this.isEditing = editing;
		this.notifyDataSetChanged();
		
	}
	
	static class ViewHolder {
		
		View dragButton;
		ToggleButton starButton;
		ImageButton menuButton;
		ImageButton removeButton;
		TextView songTitle;
		TextView songArtist;
		TextView songAlbum;
		
	}

}
