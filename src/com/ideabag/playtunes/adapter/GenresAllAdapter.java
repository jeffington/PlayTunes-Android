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

public class GenresAllAdapter extends BaseAdapter {
	
	private Context mContext;
	private Cursor cursor;
	
    private static final String[] allGenresSelection = new String[] {
    	
		MediaStore.Audio.Genres.NAME,
		//MediaStore.Audio.Genres._COUNT,
		MediaStore.Audio.Genres._ID
		
    };
    
	public GenresAllAdapter( Context context ) {
		super();
		
		mContext = context;
		// TODO Auto-generated constructor stub
		
		cursor = mContext.getContentResolver().query(
				MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
				allGenresSelection,
				null,
				null,
				MediaStore.Audio.Genres.NAME
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
			
			convertView = li.inflate( R.layout.list_item_artist_album, null );
			
		}
		
		cursor.moveToPosition( position );
		
		String genreName = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Genres.NAME ) );
		//int songCount = cursor.getInt( cursor.getColumnIndexOrThrow( MediaStore.Audio.Genres._COUNT ) );
		//int albumCount = cursor.getInt( cursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.NUMBER_OF_ALBUMS ) );
		
		convertView.setTag(R.id.tag_genre_id, cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Genres._ID ) ) );
		
		( ( TextView ) convertView.findViewById( R.id.Title )).setText( genreName );
		
		//( ( TextView ) convertView.findViewById( R.id.BadgeCount )).setText( "" + songCount );
		
		return convertView;
		
	}
	

}
