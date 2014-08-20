package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GenresAllAdapter extends BaseAdapter {
	
	private Context mContext;
	private LayoutInflater inflater;
	private Cursor cursor = null;
	
    private static final String[] allGenresSelection = new String[] {
    	
		MediaStore.Audio.Genres.NAME,
		MediaStore.Audio.Genres._ID
		
    };
    
	public GenresAllAdapter( Context context ) {
		super();
		
		mContext = context;
		
		inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		requery();
		
	}
	
	public void requery() {
		
		if ( null != cursor && !cursor.isClosed() ) {
			
			cursor.close();
			
		}
		
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
		
		ViewHolder holder;
		
		if ( null == convertView ) {
			
			holder = new ViewHolder();
			
			convertView = inflater.inflate( R.layout.list_item_genre, null );
			
			holder.genreName = ( TextView ) convertView.findViewById( R.id.Title );
			holder.songCount =  ( TextView ) convertView.findViewById( R.id.SongCount );
			holder.albumCount =  ( TextView ) convertView.findViewById( R.id.AlbumCount );
			
			convertView.setTag( holder );
			
		} else {
			
			holder = ( ViewHolder ) convertView.getTag();
			
		}
		
		cursor.moveToPosition( position );
		
		String genreName = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Genres.NAME ) );
		String genre_id = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Genres._ID ) );
		
		Cursor genreSongCount = mContext.getContentResolver().query(
				MediaStore.Audio.Genres.Members.getContentUri( "external", Long.parseLong( genre_id ) ),
				new String[] {
					
					MediaStore.Audio.Genres.Members._ID
					
				},
				null,
				null,
				null
			);
		
		Cursor genreAlbumCount = mContext.getContentResolver().query(
				MediaStore.Audio.Genres.Members.getContentUri( "external", Long.parseLong( genre_id ) ),
				new String[] {
					
					"DISTINCT " + MediaStore.Audio.Genres.Members.ALBUM_ID
					
				},
				null,
				null,
				null
			);
		
		
		
		
		
		int songCount = genreSongCount.getCount();
		genreSongCount.close();
		
		int albumCount = genreAlbumCount.getCount();
		genreAlbumCount.close();
		
		convertView.setTag( R.id.tag_genre_id, cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Genres._ID ) ) );
		
		holder.genreName.setText( genreName );
		
		
		
		holder.albumCount.setText( "" + albumCount );
		holder.songCount.setText( "" + songCount );
		
		return convertView;
		
	}
	
	static class ViewHolder {
		
		TextView albumCount;
		TextView songCount;
		TextView genreName;
		
	}

}
