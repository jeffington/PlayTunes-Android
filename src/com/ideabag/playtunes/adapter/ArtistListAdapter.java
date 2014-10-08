package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.util.LoadAlbumStackTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
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
		String artistID = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Artists._ID ) );
    	
		new LoadAlbumStackTask( holder.albumArtOne, holder.albumArtTwo, holder.albumArtThree ).execute( artistID );
		
		String artistName = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.ARTIST ) ).trim();
		int songCount = mCursor.getInt( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.NUMBER_OF_TRACKS ) );
		int albumCount = mCursor.getInt( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Artists.NUMBER_OF_ALBUMS ) );
		
		
		convertView.setTag( R.id.tag_artist_id, artistID );
		
		if ( mContext.getString( R.string.no_artist_string ).equals( artistName ) ) {
			
			holder.artistName.setText( mContext.getString( R.string.artist_unknown ) );
			convertView.setTag( R.id.tag_artist_unknown, "1" );
			
		} else {
			
			holder.artistName.setText( artistName );
			convertView.setTag( R.id.tag_artist_unknown, "0" );
			
		}
		
		holder.subtitle.setText( "" + albumCount + " " + ( albumCount == 1 ? ALBUM_SINGULAR : ALBUMS_PLURAL ) + " " + songCount + " " + ( songCount == 1 ? SONG_SINGULAR : SONGS_PLURAL ) );
		
		
		return convertView;
		
	}
	
	static class ViewHolder {
		
		TextView artistName;
		TextView subtitle;
		ImageView albumArtOne;
		ImageView albumArtTwo;
		ImageView albumArtThree;
		
	}

}
