package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.util.AsyncDrawable;
import com.ideabag.playtunes.util.BitmapWorkerTask;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumListAdapter extends AsyncQueryAdapter {
	
	protected LayoutInflater inflater;
	
	private int mAlbumThumbWidthPx;
	
	public AlbumListAdapter( Context context ) {
		super( context );

		inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		mAlbumThumbWidthPx = context.getResources().getDimensionPixelSize( R.dimen.list_item_height );
		
	}
	
	@Override public View getView( int position, View convertView, ViewGroup parent ) {
		
		ViewHolder holder;
		
		if ( null == convertView ) {
			
			holder = new ViewHolder();
			
			convertView = inflater.inflate( R.layout.list_item_album, null );
			
			holder.albumArtist = ( TextView ) convertView.findViewById( R.id.AlbumArtist );
			holder.albumTitle = ( TextView ) convertView.findViewById( R.id.AlbumTitle );
			holder.albumThumb = ( ImageView ) convertView.findViewById( R.id.AlbumArtThumb );
			holder.songCount = ( TextView ) convertView.findViewById( R.id.SongCount );
			
			convertView.setTag( holder );
			
		} else {
			
			holder = ( ViewHolder ) convertView.getTag();
			
			//holder.albumThumb.get
			
			BitmapDrawable mAlbumArtDrawable = ( BitmapDrawable ) holder.albumThumb.getDrawable();
				
			if ( null != mAlbumArtDrawable ) {
				
				// Clear the drawable, but we retain the reference through mAlbumArtDrawable
				holder.albumThumb.setImageBitmap( null ); 
				//new RecycleBitmapTask().execute( mAlbumArtDrawable );
				
			}
			
		}
		
		mCursor.moveToPosition( position );
		
		//
		// Set the album art
		//
		
		String albumArtUriString = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
		
		if ( null != albumArtUriString ) {
			
			//Uri albumArtUri = Uri.parse( albumArtUriString );
			
			//holder.albumThumb.setImageURI( albumArtUri );
			
			if ( BitmapWorkerTask.cancelPotentialWork( albumArtUriString, holder.albumThumb ) ) {
				
				final BitmapWorkerTask task = new BitmapWorkerTask( holder.albumThumb, mAlbumThumbWidthPx );
		        final AsyncDrawable asyncDrawable =
		                new AsyncDrawable( mContext.getResources(),
		                		null, // BitmapFactory.decodeResource( mContext.getResources(), R.drawable.no_album_art_thumb )
		                		task );
		        
		        holder.albumThumb.setImageDrawable( asyncDrawable );
		        
		        task.execute( albumArtUriString );
		        
			}
	        
		} else {
			
			holder.albumThumb.setImageResource( R.drawable.no_album_art_thumb );
			
		}
		
		
		
		convertView.setTag( R.id.tag_album_id, mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Albums._ID ) ) );
		
		String artistName = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ARTIST ) );
		String albumName = mCursor.getString( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM ) );
		
		int songCount = mCursor.getInt( mCursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.NUMBER_OF_SONGS ) );
		
		holder.albumArtist.setText( artistName );
		holder.albumTitle.setText( albumName );
		
		holder.songCount.setText( "" + songCount );
		
		
		return convertView;
		
	}
	
	static class ViewHolder {
		
		ImageView albumThumb;
		TextView songCount;
		TextView albumTitle;
		TextView albumArtist;
		
	}
	
}
