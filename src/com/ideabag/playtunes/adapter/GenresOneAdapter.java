package com.ideabag.playtunes.adapter;

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

public class GenresOneAdapter extends BaseAdapter {
	
	private Context mContext;
	private Cursor cursor;
	
	private String GENRE_ID;
	
    private static final String[] oneGenreSelection = new String[] {
    	
		MediaStore.Audio.Genres.Members._ID,
		MediaStore.Audio.Genres.Members.ALBUM,
		MediaStore.Audio.Genres.Members.TITLE,
		MediaStore.Audio.Genres.Members.ARTIST
		
    };
    
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
		
		String genreName = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Genres.Members.TITLE ) );
		String media_id = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Genres.Members._ID ) );
		
		
		convertView.setTag( R.id.tag_song_id, media_id );
		
		( ( TextView ) convertView.findViewById( R.id.Title ) ).setText( genreName );
		
		
		return convertView;
		
	}
	

}
