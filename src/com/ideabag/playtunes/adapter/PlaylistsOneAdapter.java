package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.database.MediaQuery;

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

public class PlaylistsOneAdapter extends BaseAdapter {
	
	protected Context mContext;
	protected LayoutInflater inflater;
	protected Cursor cursor = null;
	private MediaQuery mQuery = null;
	
	private PlaylistManager PlaylistManager;
	private View.OnClickListener songMenuClickListener;
	
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
    
	public PlaylistsOneAdapter( Context context, String playlist_id, View.OnClickListener menuClickListener ) {
		
		mContext = context;
		PLAYLIST_ID = playlist_id;
		
		PlaylistManager = new PlaylistManager( mContext );
		
		songMenuClickListener = menuClickListener;
		
		inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
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
	
	public MediaQuery getQuery() {
		
		return mQuery;
		
	}
	
	public void requery() {
		
		if ( null != cursor ) {
			
			cursor.close();
			
		}
		
		cursor = MediaQuery.execute( mContext, mQuery );
		
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

	@Override public long getItemId( int position ) {
		
		int mID = 0;
		
		if ( null != cursor ) {
			
			cursor.moveToPosition( position );
			
			mID = cursor.getInt( cursor.getColumnIndex( MediaStore.Audio.Media._ID ) );
			
		}
		
		return mID;
		
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
		
		cursor.moveToPosition( position );
		
		String songTitle = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Playlists.Members.TITLE ) );
		String songArtist = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
		String songAlbum = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) );
		String song_id = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Playlists.Members.AUDIO_ID ) );
		
		holder.songTitle.setText( songTitle );
		holder.songArtist.setText( songArtist );
		holder.songAlbum.setText( songAlbum );
		
		holder.starButton.setTag( R.id.tag_song_id, song_id );
		holder.menuButton.setTag( R.id.tag_song_id, song_id );
		holder.removeButton.setTag( R.id.tag_song_id, song_id );
		
		holder.starButton.setChecked( PlaylistManager.isStarred( song_id ) ); 
		
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
