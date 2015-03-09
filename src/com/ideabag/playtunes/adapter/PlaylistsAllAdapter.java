package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.util.QueryCountTask;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class PlaylistsAllAdapter extends AsyncQueryAdapter {
	
	protected LayoutInflater inflater;
	
	protected PlaylistManager mPlaylistManager;
	
	protected View.OnClickListener playlistMenuClickListener;
	
    public static final String[] SELECTION = new String[] {
    	
    	MediaStore.Audio.Playlists.NAME,
    	MediaStore.Audio.Playlists.DATE_MODIFIED,
		MediaStore.Audio.Playlists._ID
	
    };
    
	public PlaylistsAllAdapter( Context context, View.OnClickListener menuClickListener ) {
		super( context );
		
		playlistMenuClickListener = menuClickListener;
		
		mPlaylistManager = new PlaylistManager( mContext );
    	
		
		inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		mQuery = new MediaQuery(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				SELECTION,
				MediaStore.Audio.Playlists._ID + " !=?",
				new String[] {
					
					mPlaylistManager.createStarredIfNotExist()
						
				},
				MediaStore.Audio.Playlists.DATE_MODIFIED + " DESC"
			);
		
		requery();
		
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
		
		mCursor.moveToPosition( position );
		String playlist_id = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Playlists._ID ) );
		
		// Get song count for the given playlist
		//MediaStore.Audio.Playlists._COUNT
		new QueryCountTask( holder.songCount ).execute(
			new MediaQuery(
					MediaStore.Audio.Playlists.Members.getContentUri( "external", Long.parseLong( playlist_id ) ),
					new String[] {
						MediaStore.Audio.Playlists.Members._ID
					},
					null,
					null,
					null
			)
		);
		
		convertView.setTag( R.id.tag_playlist_id, playlist_id );
		
		String playlistTitle = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Playlists.NAME ) );
		
		
		holder.playlistName.setText( playlistTitle );
		
		//holder.songCount.setText( "" + song_count );
		
		
		
		return convertView;
		
	}
	
	static class ViewHolder {
		
		TextView songCount;
		TextView playlistName;
		ImageButton menuButton;
		
	}

}
