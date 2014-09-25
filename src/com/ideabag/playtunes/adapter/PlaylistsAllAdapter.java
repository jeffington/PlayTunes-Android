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
import android.widget.ImageButton;
import android.widget.TextView;

public class PlaylistsAllAdapter extends BaseAdapter {
	
	protected Context mContext;
	private LayoutInflater inflater;
	protected Cursor cursor = null;
	
	private PlaylistManager mPlaylistManager;
	
	View.OnClickListener playlistMenuClickListener;
	
	//View.OnClickListener playlistMenuClickListener;
	
    private final String[] allPlaylistsSelection = new String[] {
    	
    	MediaStore.Audio.Playlists.NAME,
    	MediaStore.Audio.Playlists.DATE_MODIFIED,
		MediaStore.Audio.Playlists._ID
	
    };
    
	public PlaylistsAllAdapter( Context context, View.OnClickListener menuClickListener ) {
		
		mContext = context;
		
		inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    	
		playlistMenuClickListener = menuClickListener;
		
		mPlaylistManager = new PlaylistManager( mContext );
    	
		requery();
		
	}
	
	public void requery() {
		
		if ( null != cursor)
			cursor.close(); 
		
    	cursor = mContext.getContentResolver().query(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				allPlaylistsSelection,
				MediaStore.Audio.Playlists._ID + " !=?",
				new String[] {
					
					mPlaylistManager.createStarredIfNotExist()
						
				},
				MediaStore.Audio.Playlists.DATE_MODIFIED + " DESC"
			);
		
	}

	
	@Override public int getCount() {
		
		return ( cursor != null ? cursor.getCount() : 0 );
		
	}

	@Override public Object getItem( int position ) {
		
		return null;
		
	}

	@Override public long getItemId( int position ) {
		
		int mID = 0;
		
		if ( cursor != null ) {
			
			cursor.moveToPosition( position );
			
			mID = cursor.getInt( cursor.getColumnIndex( MediaStore.Audio.Playlists._ID ) );
			
		}
		
		return mID;
		
	}

	@Override public View getView( int position, View convertView, ViewGroup parent ) {
		
		ViewHolder holder;
		
		if ( null == convertView ) {
			
			holder = new ViewHolder();
			
			convertView = inflater.inflate( R.layout.list_item_playlist, null );
			
			holder.menuButton = ( ImageButton ) convertView.findViewById( R.id.PlaylistMenuButton );
			
			holder.menuButton.setOnClickListener( playlistMenuClickListener );
			
			holder.playlistName = ( TextView ) convertView.findViewById( R.id.PlaylistTitle );
			holder.songCount = ( TextView ) convertView.findViewById( R.id.SongCount );
			
			convertView.setTag( holder );
			
		} else {
			
			holder = ( ViewHolder ) convertView.getTag();
			
		}
		
		cursor.moveToPosition( position );
		String playlist_id = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Playlists._ID ) );
		convertView.setTag( R.id.tag_playlist_id, playlist_id );
		
		String playlistTitle = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Playlists.NAME ) );
		
		// Get song count for the given playlist
		//MediaStore.Audio.Playlists._COUNT
		
		Cursor songs = mContext.getContentResolver().query(
				MediaStore.Audio.Playlists.Members.getContentUri( "external", Long.parseLong( playlist_id ) ),
				new String[] {
					MediaStore.Audio.Playlists.Members._ID
				},
				null,
				null,
				null
			);
		
		int song_count = songs.getCount();
		
		songs.close();
		
		holder.playlistName.setText( playlistTitle );
		
		holder.songCount.setText( "" + song_count );
		
		
		
		return convertView;
		
	}
	
	static class ViewHolder {
		
		TextView songCount;
		TextView playlistName;
		ImageButton menuButton;
		
	}

}
