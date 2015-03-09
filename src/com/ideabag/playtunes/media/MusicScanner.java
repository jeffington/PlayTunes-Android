package com.ideabag.playtunes.media;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

public class MusicScanner implements MediaScannerConnection.MediaScannerConnectionClient {
	
	public static final String TAG = "MusicScanner";
	
	private Context mContext;
	
	private MediaScannerConnection mScanner;
	
	public MusicScanner( Context context ) {
		
		mContext = context;
		
	}
	
	public void scan() {
		
		if ( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT ) {
			
			legacyScan();
			
		} else {
			
			mScanner = new MediaScannerConnection( mContext, this );
			mScanner.connect();
			
		}
		
	}
	
	private void legacyScan() {
		
		mContext.sendBroadcast( new Intent( Intent.ACTION_MEDIA_MOUNTED, Uri.parse( "file://" + Environment.getExternalStorageDirectory() ) ) );
		
	}

	@Override public void onMediaScannerConnected() {
		
		mScanner.scanFile( "file://" + Environment.getExternalStorageDirectory(), null );
		
	}

	@Override public void onScanCompleted(String path, Uri uri) {
		android.util.Log.i( TAG, "Completed " + path);
		//mScanner.disconnect();
		
	}
	
}
