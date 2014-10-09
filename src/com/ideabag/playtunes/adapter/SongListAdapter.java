package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.util.StarToggleTask;

import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SongListAdapter extends AsyncQueryAdapter {
	
	protected LayoutInflater inflater;
	
	String mNowPlayingMediaID = null;
	
	protected PlaylistManager PlaylistManager;
	
	View.OnClickListener songMenuClickListener;
    
    
	public SongListAdapter( Context context, View.OnClickListener menuClickListener) {
		super( context );
		
		PlaylistManager = new PlaylistManager( mContext );
    	
    	this.songMenuClickListener = menuClickListener;
    	
    	inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
	}
	
	public void setNowPlayingMedia( String media_id ) {
		
		mNowPlayingMediaID = media_id;
		
	}
	

	@Override public long getItemId( int position ) {
		
		int mID = 0;
		
		if ( mCursor != null ) {
			
			mCursor.moveToPosition( position );
			
			mID = mCursor.getInt( mCursor.getColumnIndex( MediaStore.Audio.Media._ID ) );
			
		}
		
		return mID;
		
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
			
			//holder.indicator = convertView.findViewById( R.id.NowPlayingIndicator );
			
			holder.starButton.setOnClickListener( songMenuClickListener );
			holder.menuButton.setOnClickListener( songMenuClickListener );
			
			convertView.setTag( holder );
			
		} else {
			
			holder = ( ViewHolder ) convertView.getTag();
			
		}
		
		mCursor.moveToPosition( position );
		
		String songTitle = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
		String songArtist = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
		String songAlbum = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) );
		String song_id = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Media._ID ) );
		
		new StarToggleTask( holder.starButton ).execute( song_id );
		
		holder.songTitle.setText( songTitle );
		holder.songArtist.setText( songArtist );
		holder.songAlbum.setText( songAlbum );
		
		holder.starButton.setTag( R.id.tag_song_id, song_id );
		holder.menuButton.setTag( R.id.tag_song_id, song_id );
		
		// TODO: Add now playing indicator
		/*
		if ( null != mNowPlayingMediaID && mNowPlayingMediaID.equals( song_id ) ) {
			
			holder.songTitle.setTextColor( mContext.getResources().getColor( R.color.primaryAccentColor ) );
			holder.indicator.setVisibility( View.VISIBLE );
			
		} else {
			
			holder.songTitle.setTextColor( mContext.getResources().getColor( R.color.textColorPrimary ) );
			
			holder.indicator.setVisibility( View.INVISIBLE );
		}
		*/
		return convertView;
		
	}
	
	static class ViewHolder {
		
		ToggleButton starButton;
		ImageButton menuButton;
		TextView songTitle;
		TextView songArtist;
		TextView songAlbum;
		// TODO: Add now playing indicator
		//View indicator;
		
	}

}
