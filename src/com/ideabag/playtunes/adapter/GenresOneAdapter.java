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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class GenresOneAdapter extends BaseAdapter {
	
	private Context mContext;
	private Cursor cursor;
	private PlaylistManager mPlaylistManager;
	
	private String GENRE_ID;
	
    private static final String[] oneGenreSelection = new String[] {
    	
    	// Audio media ID must always be at position 0
    	MediaStore.Audio.Genres.Members.AUDIO_ID,
    	
		MediaStore.Audio.Genres.Members._ID,
		MediaStore.Audio.Genres.Members.ALBUM,
		MediaStore.Audio.Genres.Members.TITLE,
		MediaStore.Audio.Genres.Members.ARTIST,
		MediaStore.Audio.Genres.Members.DATA,
		MediaStore.Audio.Genres.Members.DURATION
		
    };
    
    public Cursor getCursor() {
    	
    	return cursor;
    	
    }
    
	public GenresOneAdapter( Context context, String genre_id ) {
		super();
		
		mContext = context;
		this.GENRE_ID = genre_id;
		
		cursor = mContext.getContentResolver().query(
				MediaStore.Audio.Genres.Members.getContentUri( "external", Long.parseLong( genre_id ) ),
				oneGenreSelection,
				null,
				null,
				null
			);
		
		mPlaylistManager = new PlaylistManager( mContext );
		
	}

	@Override public int getCount() {
		
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
			
		}
		
		cursor.moveToPosition( position );
		
		String songTitle = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Genres.Members.TITLE ) );
		String song_id = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Genres.Members.AUDIO_ID ) );
		String songArtist = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Genres.Members.ARTIST ) );
		String songAlbum = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Genres.Members.ALBUM ) );
		
		convertView.setTag( R.id.tag_song_id, song_id );
		
		( ( TextView ) convertView.findViewById( R.id.SongTitle ) ).setText( songTitle );
		( ( TextView ) convertView.findViewById( R.id.SongAlbum ) ).setText( songAlbum );
		( ( TextView ) convertView.findViewById( R.id.SongArtist ) ).setText( songArtist );
		
		ToggleButton starButton = ( ToggleButton ) convertView.findViewById( R.id.StarButton );
		
		starButton.setChecked( mPlaylistManager.isStarred( song_id ) ); 
		
		return convertView;
		
	}
	

}
