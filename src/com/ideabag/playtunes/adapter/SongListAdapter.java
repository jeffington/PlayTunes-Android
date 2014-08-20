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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SongListAdapter extends BaseAdapter {
	
	protected Context mContext;
	protected LayoutInflater inflater;
	
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
    	
    	inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
	}
	
	public void requery() {
		
		if ( null != cursor && !cursor.isClosed() ) {
			
			cursor.close();
			
		}
		
    	cursor = MediaQuery.execute( mContext, mQuery );
		
	}
	
	@Override
	public int getCount() {
		
		return ( null == cursor ? 0 : cursor.getCount() );
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
			
			convertView = inflater.inflate( R.layout.list_item_song_no_album, null );
			
			holder = new ViewHolder();
			
			holder.starButton = ( ToggleButton ) convertView.findViewById( R.id.StarButton );
			holder.menuButton = ( ImageButton ) convertView.findViewById( R.id.MenuButton );
			holder.songTitle = ( TextView ) convertView.findViewById( R.id.SongTitle );
			holder.songArtist = ( TextView ) convertView.findViewById( R.id.SongArtist );
			holder.songAlbum = ( TextView ) convertView.findViewById( R.id.SongAlbum );
			
			holder.starButton.setOnClickListener( songMenuClickListener );
			holder.menuButton.setOnClickListener( songMenuClickListener );
			
			convertView.setTag( holder );
			
		} else {
			
			holder = ( ViewHolder ) convertView.getTag();
			
		}
		
		cursor.moveToPosition( position );
		
		String songTitle = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
		String songArtist = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
		String songAlbum = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) );
		String song_id = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media._ID ) );
		
		holder.songTitle.setText( songTitle );
		holder.songArtist.setText( songArtist );
		holder.songAlbum.setText( songAlbum );
		
		holder.starButton.setTag( R.id.tag_song_id, song_id );
		holder.menuButton.setTag( R.id.tag_song_id, song_id );
		
		holder.starButton.setChecked( PlaylistManager.isStarred( song_id ) ); 
		
		
		return convertView;
		
	}
	
	static class ViewHolder {
		
		ToggleButton starButton;
		ImageButton menuButton;
		TextView songTitle;
		TextView songArtist;
		TextView songAlbum;
		
		
	}

}
