package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.util.StarToggleTask;
import com.ideabag.playtunes.widget.StarButton;

import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
		notifyDataSetChanged();
		
	}
	

	@Override public long getItemId( int position ) {
		
		int mID = -1;
		
		if ( mCursor != null ) {
			
			mCursor.moveToPosition( position );
			
			if ( !(mCursor.isAfterLast() || mCursor.isBeforeFirst() ) ) {
				
				// TODO: Remember, in any playlist or song query, the first field _MUST_ be a media _id
				mID = mCursor.getInt( 0 );
				
			}
			
		}
		
		return mID;
		
	}
	
	@Override public boolean hasStableIds() {
		
        return true;
        
	}

	@Override public View getView( int position, View convertView, ViewGroup parent ) {
		
		ViewHolder holder;
		
		if ( null == convertView ) {
			
			convertView = inflater.inflate( R.layout.list_item_song_no_album, null );
			
			holder = new ViewHolder();
			
			holder.starButton = ( StarButton ) convertView.findViewById( R.id.StarButton );
			holder.menuButton = ( ImageButton ) convertView.findViewById( R.id.MenuButton );
			
			holder.songTitle = ( TextView ) convertView.findViewById( R.id.SongTitle );
			
			holder.row = ( LinearLayout ) convertView;
			
			//holder.indicator = convertView.findViewById( R.id.NowPlayingIndicator );
			

			holder.songArtist = ( TextView ) convertView.findViewById( R.id.SongArtist );
			holder.songAlbum = ( TextView ) convertView.findViewById( R.id.SongAlbum );
			
			holder.artistButton = ( Button ) convertView.findViewById( R.id.SongArtistButton );
			holder.albumButton = ( Button ) convertView.findViewById( R.id.SongAlbumButton );
			
			if ( holder.artistButton != null ) {
				
				holder.artistButton.setOnClickListener( songMenuClickListener );
				
			}
			
			if ( holder.albumButton != null ) {
				
				holder.albumButton.setOnClickListener( songMenuClickListener );
				
			}
			
			holder.starButton.setOnClickListener( songMenuClickListener );
			holder.menuButton.setOnClickListener( songMenuClickListener );
			
			convertView.setTag( holder );
			
		} else {
			
			holder = ( ViewHolder ) convertView.getTag();
			
			StarToggleTask starTask = ( StarToggleTask ) holder.starButton.getTag();
			
			if ( starTask != null && !starTask.isCancelled() ) {
				
				starTask.cancel( true );
				
			}
			
		}
		
		mCursor.moveToPosition( position );
		
		String songTitle = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
		String songArtist = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
		String songAlbum = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) );
		String song_id = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Media._ID ) );
		
		StarToggleTask starTask = new StarToggleTask( holder.starButton );
		holder.starButton.setTag( R.id.tag_song_id, song_id );
		holder.starButton.setTag( starTask );
		
		starTask.execute( song_id );
		
		holder.songTitle.setText( songTitle );
		
		if ( holder.songArtist != null ) {
			
			holder.songArtist.setText( songArtist );
			
		} else {
			
			String artist_id = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST_ID ) );
			holder.artistButton.setText( songArtist );
			holder.artistButton.setTag( R.id.tag_artist_id, artist_id );
		}
		
		if ( holder.songAlbum != null ) {
			
			holder.songAlbum.setText( songAlbum );
			
		} else {
			
			String album_id = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM_ID ) );
			
			holder.albumButton.setText( songAlbum );
			holder.albumButton.setTag( R.id.tag_album_id, album_id );
		}
		
		holder.menuButton.setTag( R.id.tag_song_id, song_id );
		
		// TODO: Add now playing indicator
		
		if ( null != mNowPlayingMediaID && mNowPlayingMediaID.equals( song_id ) ) {
			
			// Is now playing
			holder.row.setBackgroundResource( R.drawable.indicator );
			
		} else {
			
			holder.row.setBackgroundResource( android.R.color.transparent );
			
		}
		
		return convertView;
		
	}
	
	static class ViewHolder {
		
		LinearLayout row;
		StarButton starButton;
		ImageButton menuButton;
		TextView songTitle;
		TextView songArtist;
		TextView songAlbum;
		
		Button artistButton;
		Button albumButton;
		
	}

}
