package com.ideabag.playtunes.util;

import java.lang.ref.WeakReference;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.database.MediaQuery;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

public class LoadAlbumStackTask extends AsyncTask<String, Void, Cursor > {
	
	private final static String TAG = "LoadAlbumStackTask";
	
    private final WeakReference< ImageView > recordTop, recordMiddle, recordBottom;
    //private MediaQuery songsQuery, albumsQuery;
    //private String artist_id;
    
    private Context mContext;
    
    private int mAlbumThumbWidthPx;
    
    public LoadAlbumStackTask( ImageView top, ImageView middle, ImageView bottom ) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
    	recordTop = new WeakReference< ImageView >( top );
    	recordMiddle = new WeakReference< ImageView >( middle );
    	recordBottom = new WeakReference< ImageView >( bottom );
    	
        mContext = top.getContext();
        
        
        //artist_id = mArtistId;
        
        mAlbumThumbWidthPx = mContext.getResources().getDimensionPixelSize( R.dimen.album_stack_height );
        
    }

    // Decode image in background.
    @Override protected Cursor doInBackground( String... artist_ids ) {
        
    	String artist_id = artist_ids[ 0 ];
    	
    	Cursor mAlbumArtQuery = MediaQuery.execute( mContext, new MediaQuery(
				MediaStore.Audio.Artists.Albums.getContentUri( "external", Long.parseLong( artist_id ) ),
				new String[] {
					MediaStore.Audio.Artists.Albums.ALBUM_ART
					
				},
				MediaStore.Audio.Artists.Albums.ALBUM_ART + " IS NOT NULL",
				null,
				null//MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS
			)
		);
    	
        return mAlbumArtQuery;//mQueryCount;
        
    }
    
    // Once complete, see if ImageView is still around and set bitmap.
    @Override protected void onPostExecute( Cursor albumCovers ) {
    	
    	int count = (albumCovers == null ? 0 : albumCovers.getCount() );
    	
    	
    	
    	final ImageView TopRecord = recordTop.get();
    	final ImageView MiddleRecord = recordMiddle.get();
    	final ImageView BottomRecord = recordBottom.get();
    	
    	if ( !( TopRecord == null || MiddleRecord == null || BottomRecord == null ) ) {
	    	
			if ( count > 0 ) {
				
				albumCovers.moveToPosition( 0 );
				
				String albumUriString = albumCovers.getString( albumCovers.getColumnIndex(MediaStore.Audio.Artists.Albums.ALBUM_ART ) );
				
				
				final BitmapWorkerTask albumTask = new BitmapWorkerTask( TopRecord, mAlbumThumbWidthPx );
		        final AsyncDrawable asyncThumbDrawable =
		                new AsyncDrawable( mContext.getResources(),
		                		null, // BitmapFactory.decodeResource( mContext.getResources(), R.drawable.no_album_art_thumb )
		                		albumTask );
		        
		        TopRecord.setImageDrawable( asyncThumbDrawable );
		        albumTask.execute( albumUriString );
		        
			} else {
				
				TopRecord.setImageResource( R.drawable.no_album_art_thumb );
				
			}
	
			if ( count > 1 ) {
				
				albumCovers.moveToPosition( 1 );
				String albumUriString = albumCovers.getString( albumCovers.getColumnIndex( MediaStore.Audio.Artists.Albums.ALBUM_ART ) );
				
				final BitmapWorkerTask albumTask = new BitmapWorkerTask( MiddleRecord, mAlbumThumbWidthPx );
		        final AsyncDrawable asyncThumbDrawable =
		                new AsyncDrawable( mContext.getResources(),
		                		null, // BitmapFactory.decodeResource( mContext.getResources(), R.drawable.no_album_art_thumb )
		                		albumTask );
		        
		        MiddleRecord.setImageDrawable( asyncThumbDrawable );
		        albumTask.execute( albumUriString );
		        MiddleRecord.setVisibility( View.VISIBLE );
				
				
			} else {
				
				
				MiddleRecord.setVisibility( View.INVISIBLE );
				
			}
			
			if ( count > 2 ) {
				
				albumCovers.moveToPosition( 2 );
				String albumUriString = albumCovers.getString( albumCovers.getColumnIndex(MediaStore.Audio.Artists.Albums.ALBUM_ART ) );
				
				final BitmapWorkerTask albumTask = new BitmapWorkerTask( BottomRecord, mAlbumThumbWidthPx );
		        final AsyncDrawable asyncThumbDrawable =
		                new AsyncDrawable( mContext.getResources(),
		                		null, // BitmapFactory.decodeResource( mContext.getResources(), R.drawable.no_album_art_thumb )
		                		albumTask );
		        
		        BottomRecord.setImageDrawable( asyncThumbDrawable );
		        albumTask.execute( albumUriString );
		        BottomRecord.setVisibility( View.VISIBLE );
				
				
			} else {
				
				BottomRecord.setVisibility( View.INVISIBLE );
				
			}
			
    	}
    	
    	albumCovers.close();
		
    }
    
    /*
    public static boolean cancelPotentialWork( String data, ImageView imageView ) {
    	
        final BitmapWorkerTask bitmapWorkerTask = null;//getBitmapWorkerTask(imageView);

        if ( bitmapWorkerTask != null ) {
        	
            final String bitmapData = bitmapWorkerTask.path;
            // If bitmapData is not yet set or it differs from the new data
            if ( bitmapData == null || bitmapData != data ) {
                // Cancel previous task
                
            	bitmapWorkerTask.cancel( true );
                
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }
    
    /*
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
            int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
*/
    /*
    public void loadBitmap(int resId, ImageView imageView) {
        if (cancelPotentialWork(resId, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(getResources(), mPlaceHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(resId);
        }
    }
    */
}