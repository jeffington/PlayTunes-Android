package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.cursor.MediaQueryCursor;
import com.ideabag.playtunes.cursor.StaticMediaQueries;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlaylistsAllAdapter extends BaseAdapter {
	
	protected Context mContext;
	protected MediaQueryCursor mediaQuery;
	
	View.OnClickListener playlistMenuClickListener;
    
	public PlaylistsAllAdapter( Context context, View.OnClickListener onClick ) {
		
		mContext = context;
		
		this.playlistMenuClickListener = onClick;
		
		mediaQuery = StaticMediaQueries.getPlaylists( mContext );
    	
    	
    	
	}

	public void requery() {
		
		
	}
	
	@Override public int getCount() {
		
		return mediaQuery.getCursor().getCount();
	}

	@Override public Object getItem(int position) {
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
			
			convertView = li.inflate( R.layout.list_item_playlist, null );
			
			convertView.findViewById( R.id.PlaylistMenuButton ).setOnClickListener( playlistMenuClickListener );
			
		}
		
		Cursor cursor = mediaQuery.getCursor();
		
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
		
		( ( TextView ) convertView.findViewById( R.id.PlaylistTitle ) ).setText( playlistTitle );
		
		( ( TextView ) convertView.findViewById( R.id.BadgeCount ) ).setText( "" + song_count );
		
		
		
		return convertView;
		
	}

}
