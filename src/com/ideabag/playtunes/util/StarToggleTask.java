package com.ideabag.playtunes.util;

import java.lang.ref.WeakReference;

import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.ToggleButton;

public class StarToggleTask extends AsyncTask<String, Void, Boolean > {
	
    private final WeakReference< ToggleButton > toggleButtonReference;
    private String media_id;
    
    private Context mContext;
    
    public StarToggleTask( ToggleButton toggleView ) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
    	toggleButtonReference = new WeakReference< ToggleButton >( toggleView );
        
        mContext = toggleView.getContext();
        
    }

    // Decode image in background.
    @Override
    protected Boolean doInBackground(String... media_ids ) {
        
    	Boolean mIsFavorite = Boolean.FALSE;
    	
    	media_id = media_ids[0];
    	
    	PlaylistManager mPlaylistManager = new PlaylistManager( mContext );
    	
    	mIsFavorite = Boolean.valueOf( mPlaylistManager.isStarred( media_id ) );
    	
        return mIsFavorite;
        
    }
    
    // Once complete, see if ImageView is still around and set bitmap.
    @Override protected void onPostExecute( Boolean isFavorite ) {
    	
        if ( toggleButtonReference != null ) {
        	
            final ToggleButton mStarButton = toggleButtonReference.get();
            
            if ( mStarButton != null ) {
                
            	String mTagId = ( String ) mStarButton.getTag( R.id.tag_song_id );
            	
            	if ( mTagId.equals( media_id ) ) {
            		
            		mStarButton.setTag( null );
            		mStarButton.setChecked( ( isFavorite != null ? isFavorite.booleanValue() : false ) );
            		
            	}
            	
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