package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.util.AlbumSongsCountTask;

import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GenresAllAdapter extends AsyncQueryAdapter {
	
	private LayoutInflater inflater;
	
    private static final String[] allGenresSelection = new String[] {
    	
		MediaStore.Audio.Genres.NAME,
		MediaStore.Audio.Genres._ID
		
    };
    
	public GenresAllAdapter( Context context, MediaQuery.OnQueryCompletedListener listener ) {
		super( context );
		
		inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
    	mQuery = new MediaQuery(
				MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
				allGenresSelection,
				null,
				null,
				MediaStore.Audio.Genres.DEFAULT_SORT_ORDER
			);
    	
    	setOnQueryCompletedListener( listener );
    	
		requery();
		
	}
	
	@Override public View getView( int position, View convertView, ViewGroup parent ) {
		
		ViewHolder holder;
		
		if ( null == convertView ) {
			
			holder = new ViewHolder();
			
			convertView = inflater.inflate( R.layout.list_item_genre, null );
			
			holder.genreName = ( TextView ) convertView.findViewById( R.id.Title );
			holder.subtitle = ( TextView ) convertView.findViewById( R.id.Subtitle );
			
			convertView.setTag( holder );
			
		} else {
			
			holder = ( ViewHolder ) convertView.getTag();
			
		}
		
		mCursor.moveToPosition( position );
		
		
		String genre_id = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Genres._ID ) );
		

		new AlbumSongsCountTask( holder.subtitle ).execute(
				new MediaQuery( // First songs
						MediaStore.Audio.Genres.Members.getContentUri( "external", Long.parseLong( genre_id ) ),
						new String[] {
							
							MediaStore.Audio.Genres.Members._ID
							
						},
						null,
						null,
						null
					),
					new MediaQuery( // Then albums
							MediaStore.Audio.Genres.Members.getContentUri( "external", Long.parseLong( genre_id ) ),
							new String[] {
								
								"DISTINCT " + MediaStore.Audio.Genres.Members.ALBUM_ID
								
							},
							null,
							null,
							null
						)
				
				);

		
		
		String genreName = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Genres.NAME ) );
		
		
		convertView.setTag( R.id.tag_genre_id, mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Genres._ID ) ) );
		
		holder.genreName.setText( genreName );
		
		
		return convertView;
		
	}
	
	static class ViewHolder {
		
		TextView genreName;
		TextView subtitle;
		
	}

}
