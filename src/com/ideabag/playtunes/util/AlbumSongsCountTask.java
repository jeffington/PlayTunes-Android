package com.ideabag.playtunes.util;

import java.lang.ref.WeakReference;

import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.database.MediaQuery;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.TextView;

public class AlbumSongsCountTask extends AsyncTask<MediaQuery, Void, String > {
	
    private final WeakReference< TextView > toggleButtonReference;
    private MediaQuery songsQuery, albumsQuery;
    
    private Context mContext;
    
    public AlbumSongsCountTask( TextView toggleView ) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
    	toggleButtonReference = new WeakReference< TextView >( toggleView );
        
        mContext = toggleView.getContext();
        
    }

    // Decode image in background.
    @Override
    protected String doInBackground(MediaQuery... media_ids ) {
        
    	String mQueryCount = "";
    	
    	songsQuery = media_ids[ 0 ];
    	albumsQuery = media_ids[ 1 ];
    	
    	Cursor c = MediaQuery.execute(mContext, albumsQuery);
    	
    	mQueryCount += c.getCount();
    	mQueryCount += " " + ( c.getCount() == 1 ? mContext.getString( R.string.album_singular) : mContext.getString( R.string.albums_plural ) );
    			
    			
    	c.close();
    	
    	Cursor c2 = MediaQuery.execute(mContext, songsQuery);
    	
    	mQueryCount += " " + c2.getCount();
    	mQueryCount += " " + ( c2.getCount() == 1 ? mContext.getString( R.string.song_singular ) : mContext.getString( R.string.songs_plural ) );
    	c2.close();
    	
        return mQueryCount;
        
    }
    
    // Once complete, see if ImageView is still around and set bitmap.
    @Override protected void onPostExecute( String mCount ) {
    	
        if ( toggleButtonReference != null ) {
        	
            final TextView mTextBadge = toggleButtonReference.get();
            
            if ( mCount != null ) {
                
            	mTextBadge.setText( mCount );
            	
            }
            
        }
        
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