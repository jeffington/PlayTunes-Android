package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.PlaylistManager;
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

public class SongListAdapter extends BaseAdapter {
	
	protected Context mContext;
	
	Cursor cursor = null;
	protected MediaQuery mQuery = null;
	
	private PlaylistManager PlaylistManager;
	
	View.OnClickListener songMenuClickListener;
    
    public MediaQuery getQuery() {
    	
    	return mQuery;
    	
    }
    
	public SongListAdapter( Context context, View.OnClickListener menuClickListener) {
		
		mContext = context;
		
		PlaylistManager = new PlaylistManager( mContext );
    	
    	this.songMenuClickListener = menuClickListener;
		
	}
	
	public void requery() {
		
		if ( null != cursor && !cursor.isClosed() ) {
			
			cursor.close();
			
		}
		
    	cursor = MediaQuery.execute( mContext, mQuery );
		
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
			
			convertView = li.inflate( R.layout.list_item_song_no_album, null );
			
			convertView.findViewById( R.id.StarButton ).setOnClickListener( songMenuClickListener );
			convertView.findViewById( R.id.MenuButton ).setOnClickListener( songMenuClickListener );
			
		}
		
		cursor.moveToPosition( position );
		
		String songTitle = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
		String songArtist = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
		String songAlbum = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) );
		String song_id = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media._ID ) );
		
		( ( TextView ) convertView.findViewById( R.id.SongTitle ) ).setText( songTitle );
		( ( TextView ) convertView.findViewById( R.id.SongArtist ) ).setText( songArtist );
		( ( TextView ) convertView.findViewById( R.id.SongAlbum ) ).setText( songAlbum );
		
		convertView.findViewById( R.id.StarButton ).setTag( R.id.tag_song_id, song_id );
		convertView.findViewById( R.id.MenuButton ).setTag( R.id.tag_song_id, song_id );
		
		ToggleButton starButton = ( ToggleButton ) convertView.findViewById( R.id.StarButton );
		
		starButton.setChecked( PlaylistManager.isStarred( song_id ) ); 
		
		
		return convertView;
		
	}
	


}
