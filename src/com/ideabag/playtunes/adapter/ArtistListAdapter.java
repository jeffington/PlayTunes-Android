package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.util.AsyncDrawable;
import com.ideabag.playtunes.util.BitmapWorkerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ArtistListAdapter extends AsyncQueryAdapter {
	
	protected LayoutInflater inflater;
	
	public String ARTIST_NAME;
	
	private final String SONG_SINGULAR, SONGS_PLURAL, ALBUM_SINGULAR, ALBUMS_PLURAL;
	
	public ArtistListAdapter( Context context ) {
		super( context );
		
		inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		SONG_SINGULAR = mContext.getString( R.string.song_singular );
		SONGS_PLURAL = mContext.getString( R.string.songs_plural );
		ALBUM_SINGULAR = mContext.getString( R.string.album_singular );
		ALBUMS_PLURAL = mContext.getString( R.string.albums_plural );
		
	}

	@SuppressLint("InflateParams")
	@Override public View getView( int position, View convertView, ViewGroup parent ) {
		
		ViewHolder holder;
		
		if ( null == convertView ) {
			
			holder = new ViewHolder();
			
			convertView = inflater.inflate( R.layout.list_item_artist, null );
			
			//holder.albumCount = ( TextView ) convertView.findViewById( R.id.AlbumCount );
			//holder.songCount = ( TextView ) convertView.findViewById( R.id.SongCount );
			holder.artistName = ( TextView ) convertView.findViewById( R.id.Title );
			holder.subtitle = ( TextView ) convertView.findViewById( R.id.Subtitle );
			holder.albumArtOne = ( ImageView ) convertView.findViewById( R.id.ArtistAlbumOne );
			holder.albumArtTwo = ( ImageView ) convertView.findViewById( R.id.ArtistAlbumTwo );
			holder.albumArtThree = ( ImageView ) convertView.findViewById( R.id.ArtistAlbumThree );
			
			holder.albumArtTwo.setColorFilter( mContext.getResources().getColor( R.color.textColorTertiary ), PorterDuff.Mode.MULTIPLY );
			holder.albumArtThree.setColorFilter( mContext.getResources().getColor( R.color.textColorPrimary ), PorterDuff.Mode.MULTIPLY );
			
			convertView.setTag( holder );
			
		} else {
			
			holder = ( ViewHolder ) convertView.getTag();
			
		}
		
		mCursor.moveToPosition( position );
		
		
		String artistName = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.ARTIST ) ).trim();
		int songCount = mCursor.getInt( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.NUMBER_OF_TRACKS ) );
		int albumCount = mCursor.getInt( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.NUMBER_OF_ALBUMS ) );
		String artistID = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Artists._ID ) );
		
		convertView.setTag( R.id.tag_artist_id, artistID );
		
		if ( mContext.getString( R.string.no_artist_string ).equals( artistName ) ) {
			
			holder.artistName.setText( mContext.getString( R.string.artist_unknown ) );
			convertView.setTag( R.id.tag_artist_unknown, "1" );
			
		} else {
			
			holder.artistName.setText( artistName );
			convertView.setTag( R.id.tag_artist_unknown, "0" );
			
		}
		
		//holder.songCount.setText( "" + songCount );
		//holder.albumCount.setText( "" + albumCount );
		holder.subtitle.setText( "" + albumCount + " " + ( albumCount == 1 ? ALBUM_SINGULAR : ALBUMS_PLURAL ) + " " + songCount + " " + ( songCount == 1 ? SONG_SINGULAR : SONGS_PLURAL ) );
		
    	//MediaStore.Audio.Albums.ALBUM_ART
		Cursor mAlbumArtQuery = mContext.getContentResolver().query(
				MediaStore.Audio.Artists.Albums.getContentUri( "external", Long.parseLong( artistID ) ),
				new String[] {
					MediaStore.Audio.Artists.Albums.ALBUM_ART
					
				},
				MediaStore.Audio.Artists.Albums.ALBUM_ART + " IS NOT NULL",
				null,
				null//MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS
			);
		
		if ( mAlbumArtQuery == null || mAlbumArtQuery.getCount() == 0 ) {
			
			
			
		} else if ( mAlbumArtQuery.getCount() > 0 ) {
			
			int count = mAlbumArtQuery.getCount();
			
			if ( count > 0 ) {
				
				mAlbumArtQuery.moveToPosition( 0 );
				
				String albumUriString = mAlbumArtQuery.getString( mAlbumArtQuery.getColumnIndex(MediaStore.Audio.Artists.Albums.ALBUM_ART ) );
				
				final BitmapWorkerTask albumTask = new BitmapWorkerTask( holder.albumArtOne );
		        final AsyncDrawable asyncThumbDrawable =
		                new AsyncDrawable( mContext.getResources(),
		                		null, // BitmapFactory.decodeResource( mContext.getResources(), R.drawable.no_album_art_thumb )
		                		albumTask );
		        
		        holder.albumArtOne.setImageDrawable( asyncThumbDrawable );
		        albumTask.execute( albumUriString );
				
			} else {
				
				holder.albumArtOne.setImageResource( R.drawable.no_album_art_thumb );
				
			}
			holder.albumArtOne.setVisibility( View.VISIBLE );
			
			if ( count > 1 ) {
				
				mAlbumArtQuery.moveToPosition( 1 );
				String albumUriString = mAlbumArtQuery.getString( mAlbumArtQuery.getColumnIndex(MediaStore.Audio.Artists.Albums.ALBUM_ART ) );
				
				final BitmapWorkerTask albumTask = new BitmapWorkerTask( holder.albumArtTwo );
		        final AsyncDrawable asyncThumbDrawable =
		                new AsyncDrawable( mContext.getResources(),
		                		null, // BitmapFactory.decodeResource( mContext.getResources(), R.drawable.no_album_art_thumb )
		                		albumTask );
		        
		        holder.albumArtTwo.setImageDrawable( asyncThumbDrawable );
		        albumTask.execute( albumUriString );
		        holder.albumArtTwo.setVisibility( View.VISIBLE );
				
				
			} else {
				
				
				holder.albumArtTwo.setVisibility( View.GONE );
				
			}
			
			if ( count > 2 ) {
				
				mAlbumArtQuery.moveToPosition( 2 );
				String albumUriString = mAlbumArtQuery.getString( mAlbumArtQuery.getColumnIndex(MediaStore.Audio.Artists.Albums.ALBUM_ART ) );
				
				final BitmapWorkerTask albumTask = new BitmapWorkerTask( holder.albumArtThree );
		        final AsyncDrawable asyncThumbDrawable =
		                new AsyncDrawable( mContext.getResources(),
		                		null, // BitmapFactory.decodeResource( mContext.getResources(), R.drawable.no_album_art_thumb )
		                		albumTask );
		        
		        holder.albumArtThree.setImageDrawable( asyncThumbDrawable );
		        albumTask.execute( albumUriString );
		        holder.albumArtThree.setVisibility( View.VISIBLE );
				
				
			} else {
				
				holder.albumArtThree.setVisibility( View.GONE );
				
			}
			
			
		}
		
		mAlbumArtQuery.close();
		
		return convertView;
		
	}
	
	static class ViewHolder {
		
		TextView artistName;
		TextView subtitle;
		//TextView songCount;
		//TextView albumCount;
		ImageView albumArtOne;
		ImageView albumArtTwo;
		ImageView albumArtThree;
		
		
	}

}
