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
import android.widget.TextView;
import android.widget.ToggleButton;

public class ArtistAllSongsAdapter extends BaseAdapter {
	
	private Context mContext;
	Cursor cursor = null;
	private MediaQuery mQuery;
	private String ARTIST_ID;
	public String ARTIST_NAME;
	
	private PlaylistManager mPlaylistManager;
	View.OnClickListener songMenuClickListener;
	
    private static final String[] allSongsSelection = new String[] {
    	
    	MediaStore.Audio.Media._ID, // Needs to be at position 0
    	
    	MediaStore.Audio.Media.TITLE,
    	MediaStore.Audio.Media.ARTIST,
    	MediaStore.Audio.Media.ALBUM,
    	MediaStore.Audio.Media.TRACK,
    	MediaStore.Audio.Media.DATA,
    	MediaStore.Audio.Media.ALBUM_ID,
    	MediaStore.Audio.Media.ARTIST_ID
    	
    	
    };
    
	public ArtistAllSongsAdapter( Context context, String artist_id, View.OnClickListener clickListener ) {
		
		mContext = context;
		ARTIST_ID = artist_id;
		
		mPlaylistManager = new PlaylistManager( mContext );
		
		songMenuClickListener = clickListener;
		
		mQuery = new MediaQuery(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				allSongsSelection,
				MediaStore.Audio.Media.ARTIST_ID + "=? AND " + MediaStore.Audio.Media.IS_MUSIC + " != 0",
				new String[] {
					
					ARTIST_ID
					
				},
				MediaStore.Audio.Media.TITLE
			);
		
    	requery();
    	
	}
	
	public void requery() {
		
		if ( null != cursor && !cursor.isClosed() ) {
			
			cursor.close();
			
		}
		
		cursor = MediaQuery.execute( mContext, mQuery );
		
		cursor.moveToFirst();
		
		try {
			
			ARTIST_NAME = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
			
			if ( mContext.getString( R.string.no_artist_string ).equals( ARTIST_NAME ) ) {
	    		
	    		ARTIST_NAME = mContext.getString( R.string.artist_unknown );
	    		
	    	}
			
		} catch( Exception e ) {
			
			ARTIST_NAME = mContext.getString( R.string.artist_unknown );
			
		}
		
	}
	
	public MediaQuery getQuery() {
		
		return mQuery;
		
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
		
		ViewHolder holder;
		
		if ( null == convertView ) {
			
			holder = new ViewHolder();
			LayoutInflater li = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			
			convertView = li.inflate( R.layout.list_item_song_no_album, null );
			
			holder.songTitle =  ( TextView ) convertView.findViewById( R.id.SongTitle );
			holder.songAlbum =  ( TextView ) convertView.findViewById( R.id.SongAlbum );
			holder.songArtist = ( TextView ) convertView.findViewById( R.id.SongArtist );
			
			holder.songArtist.setVisibility( View.GONE );
			//holder
			holder.starButton = ( ToggleButton ) convertView.findViewById( R.id.StarButton );
			holder.starButton.setOnClickListener( songMenuClickListener );
			
			holder.menuButton = convertView.findViewById( R.id.MenuButton );
			holder.menuButton.setOnClickListener( songMenuClickListener );
			
			convertView.setTag( holder );
			
		} else {
			
			holder = ( ViewHolder ) convertView.getTag();
			
		}
		
		cursor.moveToPosition( position );
		
		String songTitle = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
		//String songArtist = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
		String songAlbum = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) );
		String song_id = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media._ID ) );
		
		holder.songTitle.setText( songTitle );
		//( ( TextView ) convertView.findViewById( R.id.SongArtist ) ).setText( songArtist );
		holder.songAlbum.setText( songAlbum );
		
		holder.starButton.setTag( R.id.tag_song_id, song_id );
		holder.menuButton.setTag( R.id.tag_song_id, song_id );
		
		holder.starButton.setChecked( mPlaylistManager.isStarred( song_id ) );
		
		return convertView;
		
	}
	
	static class ViewHolder {
		
		TextView songTitle;
		TextView songAlbum;
		TextView songArtist;
		ToggleButton starButton;
		View menuButton;
		
	}

}
