package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlaylistsAllAdapter extends BaseAdapter {
	
	protected Context mContext;
	protected Cursor cursor;
	
    private static final String[] allPlaylistsSelection = new String[] {
    	
    	MediaStore.Audio.Playlists.NAME,
		MediaStore.Audio.Playlists._ID
	
    };
    
	public PlaylistsAllAdapter( Context context ) {
		
		mContext = context;
		
    	cursor = mContext.getContentResolver().query(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				allPlaylistsSelection,
				null,
				null,
				MediaStore.Audio.Playlists.NAME
			);
    	
    	
    	
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
			
			convertView = li.inflate( R.layout.list_item_title_one_badge, null );
			
		}
		
		cursor.moveToPosition( position );
		
		convertView.setTag( R.id.tag_playlist_id, cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Playlists._ID ) ) );
		
		String playlistTitle = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Playlists.NAME ) );
		
		// Get song count for the given playlist
		
		int song_count = 0;
		
		( ( TextView ) convertView.findViewById( R.id.Title )).setText( playlistTitle );
		
		( ( TextView ) convertView.findViewById( R.id.BadgeCount )).setText( "" + song_count );
		
		return convertView;
		
	}

}
