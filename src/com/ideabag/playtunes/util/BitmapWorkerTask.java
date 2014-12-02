package com.ideabag.playtunes.util;

import java.lang.ref.WeakReference;

import com.ideabag.playtunes.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
	
    private final WeakReference<ImageView> imageViewReference;
    private String path;
    
    int mImageWidth;
    //private Context mContext;
    
    public BitmapWorkerTask( ImageView imageView, int destWidthPx ) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        
        mImageWidth = destWidthPx;
        
        //Context mContext = imageView.getContext();
        //mContext.getResources().getDisplayMetrics().density
        // TODO:
        // When this is first called, the width is 0
        // Creates bad album art on initial load
        
        //mContext = imageView.getContext();
        
    }
    
    public BitmapWorkerTask( ImageView imageView ) {
    	
        this( imageView, 0 );
        
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... paths) {
        
    	final BitmapFactory.Options options = new BitmapFactory.Options();
    	path = paths[0];
    	// First decode with inJustDecodeBounds=true to check dimensions
    	Bitmap bmp = null;
    	
    	if ( !isCancelled() ) {
	    	
	        if ( mImageWidth > 0 ) {
	        	
	            options.inJustDecodeBounds = true;
	            BitmapFactory.decodeFile( path, options );
		        int sample = calculateInSampleSize( options, mImageWidth, mImageWidth );
		        //android.util.Log.i( "BitmapWorkerTask", "Sample: " + sample + " desired width: " + mImageWidth );
		        // Calculate inSampleSize
		        options.inSampleSize = sample;
		        
		        // Decode bitmap with inSampleSize set
		        
	        }
	        options.inJustDecodeBounds = false;
	        
	        
	        if ( !isCancelled() ) {
	        	
	        	bmp = BitmapFactory.decodeFile( path, options );
	        	
	        }
	        
    	}
        
        return bmp;
    }
    
    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute( Bitmap bitmap ) {
    	
    	if ( isCancelled() ) {
    		
    		bitmap = null;
    		
    	}
    	
        if ( imageViewReference != null && bitmap != null ) {
        	
            final ImageView imageView = imageViewReference.get();
            if ( imageView != null ) {
                
            	AlphaAnimation fadeIn = ( AlphaAnimation ) AnimationUtils.loadAnimation( imageView.getContext(), R.anim.fadein );
            	
            	imageView.setImageBitmap( bitmap );
            	imageView.startAnimation( fadeIn );
                
            }
            
        }
        
    }
    
    private static BitmapWorkerTask getBitmapWorkerTask( ImageView imageView ) {
    		
    	   if ( imageView != null ) {
    		   
    	       final Drawable drawable = imageView.getDrawable();
    	       
    	       if ( drawable instanceof AsyncDrawable ) {
    	           
    	    	   final AsyncDrawable asyncDrawable = ( AsyncDrawable ) drawable;
    	           return asyncDrawable.getBitmapWorkerTask();
    	           
    	       }
    	       
    	   }
    	    
    	   return null;
    	    
    }
    
    public static boolean cancelPotentialWork( String data, ImageView imageView ) {
    	
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask( imageView );

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
    
    
    // TODO:
    // Rather than scaling by factors of 2, scale by factors of the device's screen density
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