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

public class PlaylistsOneAdapter extends BaseAdapter {
	
	protected Context mContext;
	protected Cursor cursor = null;
	
	private PlaylistManager PlaylistManager;
	private View.OnClickListener songMenuClickListener;
	
	private String PLAYLIST_ID;
	public String PLAYLIST_NAME;
	
	private boolean isEditing = false;
	
    private static final String[] singlePlaylistSelection = new String[] {
    	
    	// The ID of the media item must be the first thing in the selection
    	MediaStore.Audio.Playlists.Members.AUDIO_ID,
    	
    	MediaStore.Audio.Playlists.Members.TITLE,
    	MediaStore.Audio.Playlists.Members.ARTIST,
    	MediaStore.Audio.Playlists.Members.ALBUM,
    	MediaStore.Audio.Playlists.Members.DATA,
    	MediaStore.Audio.Playlists.Members._ID,
    	MediaStore.Audio.Playlists.Members.PLAYLIST_ID,
    	MediaStore.Audio.Playlists.NAME
		
	};
    
	public PlaylistsOneAdapter( Context context, String playlist_id, View.OnClickListener menuClickListener ) {
		
		mContext = context;
		PLAYLIST_ID = playlist_id;
		
		PlaylistManager = new PlaylistManager( mContext );
		
		songMenuClickListener = menuClickListener;
		
		//android.util.Log.i( "starred adapter", "" + playlist_id );
		
		Cursor cursor = mContext.getContentResolver().query(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				new String[] {
				    	
				    	MediaStore.Audio.Playlists.NAME,
						MediaStore.Audio.Playlists._ID
					
				},
				MediaStore.Audio.Playlists._ID + " =?",
				new String[] {
					
						PLAYLIST_ID
						
				},
				null
			);
		
		cursor.moveToFirst();
		
		PLAYLIST_NAME = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Playlists.NAME ) );
		cursor.close();
		
		requery();
		
	}
	
	public Cursor getCursor() {
		
		return cursor;
		
	}
	
	public void requery() {
		
		if ( null != cursor ) {
			
			cursor.close();
			
		}
		
		cursor = mContext.getContentResolver().query(
				MediaStore.Audio.Playlists.Members.getContentUri( "external", Long.parseLong( PLAYLIST_ID ) ),
				singlePlaylistSelection,
				MediaStore.Audio.Playlists.Members.PLAYLIST_ID + "=?",
				new String[] {
					
					PLAYLIST_ID
					
				},
				null
			);
		
		cursor.moveToFirst();
		
		PLAYLIST_NAME = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Playlists.NAME ) );
		
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
			
			convertView.findViewById( R.id.StarButton ).setOnClickListener( songMenuClickListener );
			convertView.findViewById( R.id.MenuButton ).setOnClickListener( songMenuClickListener );
			
		}
		
		cursor.moveToPosition( position );
		
		String songTitle = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Playlists.Members.TITLE ) );
		String songArtist = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
		String songAlbum = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) );
		String song_id = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Playlists.Members.AUDIO_ID ) );
		
		( ( TextView ) convertView.findViewById( R.id.SongTitle )).setText( songTitle );
		( ( TextView ) convertView.findViewById( R.id.SongArtist )).setText( songArtist );
		( ( TextView ) convertView.findViewById( R.id.SongAlbum )).setText( songAlbum );
		
		convertView.findViewById( R.id.StarButton ).setTag( R.id.tag_song_id, song_id );
		
		ToggleButton starButton = ( ToggleButton ) convertView.findViewById( R.id.StarButton );
		
		starButton.setChecked( PlaylistManager.isStarred( song_id ) ); 
		
		if ( !this.isEditing ) {
			
			convertView.findViewById( R.id.StarButton ).setVisibility( View.VISIBLE );
			convertView.findViewById( R.id.MenuButton ).setVisibility( View.VISIBLE );
			convertView.findViewById( R.id.DragButton ).setVisibility( View.GONE );
			
		} else {
			
			convertView.findViewById( R.id.StarButton ).setVisibility( View.GONE );
			convertView.findViewById( R.id.MenuButton ).setVisibility( View.GONE );
			convertView.findViewById( R.id.DragButton ).setVisibility( View.VISIBLE );
			
		}
		
		return convertView;
		
	}
	
	
	public void setEditing( boolean editing ) {
		
		this.isEditing = editing;
		this.notifyDataSetChanged();
		
	}


}
