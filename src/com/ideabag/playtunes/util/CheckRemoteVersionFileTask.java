package com.ideabag.playtunes.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import com.ideabag.playtunes.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;

public class CheckRemoteVersionFileTask extends AsyncTask< String, Void, JSONObject> {
	
	private static final String VERSION_FILE_URL = "http://version.playtunesapp.com";
	private static final String UPDATE_PREF_FILE = "update_pref_file";
	private static final String UPDATE_PREF_KEY = "update_timestamp";
	
	private static final int ONE_WEEK_MILLI = 604800000;
	
	Context mContext;
	SharedPreferences prefs;
	
	public CheckRemoteVersionFileTask( Context context) {
		
		mContext = context;
		prefs = mContext.getSharedPreferences( UPDATE_PREF_FILE, Context.MODE_PRIVATE );
		
		
		
	}
	
/*
	This is what the JSON file looks like:
	
	{
		
		versionInfo: {
			
			code: 9,
			name: "4.0",
			
		},
		
		changelog: "",
		
	}
*/
	
	private static final String VERSION_INFO = "versionInfo";
	private static final String VERSION_CODE = "versionCode";
	private static final String VERSION_NAME = "versionName";
	private static final String CHANGE_LOG = "changelog";
	

	
    /** The system calls this to perform work in a worker thread and
      * delivers it the parameters given to AsyncTask.execute() */
    protected JSONObject doInBackground( String... urls ) {
    	
    	JSONObject job = null;
    	String result = "";
    	
    	long then = prefs.getLong( UPDATE_PREF_KEY, 0 );
    	long mNow = new Date().getTime();
    	
    	
    	if ( mNow - ONE_WEEK_MILLI < then ) {
    		
    		cancel( true );
    		
    	} else {
	    	
	    	try {
	    		//HttpURLConnection.setFollowRedirects( true );
	    		URL url = new URL( VERSION_FILE_URL );
	    		HttpURLConnection mUrlConnection = ( HttpURLConnection ) url.openConnection();
	    		mUrlConnection.setUseCaches( false );
	    		//mUrlConnection.setInstanceFollowRedirects( true );
	    		
	    		while ( true ) {
		    		switch ( mUrlConnection.getResponseCode() ) {
		    			
		    	        case HttpURLConnection.HTTP_MOVED_PERM:
		    	        case HttpURLConnection.HTTP_MOVED_TEMP:
		    	        	
		    	        	url = new URL( mUrlConnection.getHeaderField( "Location" ) );
		    	        	mUrlConnection.disconnect();
		    	        	mUrlConnection = ( HttpURLConnection ) url.openConnection();
		    	           continue;
		    	           
		    	     }
		    		
		    		break;
		    		
	    		}
	    		
	    		long lastModified = mUrlConnection.getLastModified();
	    		
	    		if ( mNow > lastModified ) {
		    		
	    			//mUrlConnection.connect();
	    			
					InputStream in = new BufferedInputStream( mUrlConnection.getInputStream() );
					    
					@SuppressWarnings("resource")
					Scanner s = new Scanner( in ).useDelimiter("\\A");
				    
					while ( s.hasNext() ) {
						
						result = result.concat( s.next() );
						
					}
					
					s.close();
					
					//mUrlConnection.disconnect();
					
					job = new JSONObject( result );
					SharedPreferences.Editor edit = prefs.edit();
					edit.putLong( UPDATE_PREF_KEY, mNow );
					edit.commit();
					
	    		}
				
			} catch ( IOException e ) {
					
				e.printStackTrace();
					
			} catch ( JSONException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				android.util.Log.i( "CheckRemoveVersionFile", "" + result );
			}
	    	
    	}
    	
        return job;
    }
    
    /** The system calls this to perform work in the UI thread and delivers
      * the result from doInBackground() */
    protected void onPostExecute( JSONObject result ) {
        
    	if ( !isCancelled() ) {
    	
	    	if ( null != result ) {
	    		
	    		try {
	    			
	    			int remoteVersionCode = result.getInt( VERSION_CODE );
	    			String changelog = result.getString( CHANGE_LOG );
	    			
	    			int versionCode = mContext.getPackageManager()
	    				    .getPackageInfo( mContext.getPackageName(), 0).versionCode;
	    			
	    			if ( versionCode < remoteVersionCode ) {
	    				
	    				//handle.post( notUpToDate );
	    				
	    				new AlertDialog.Builder( mContext )
	    										.setTitle( mContext.getString( R.string.update_app ) )
	    										.setMessage( mContext.getString( R.string.version_outdated ) + ( changelog != null && changelog.length() > 1 ? "\n\n" + changelog : "" ) )
	    										.setNegativeButton( mContext.getString( R.string.cancel ), mClickListener)
	    										.setPositiveButton( mContext.getString( R.string.update ), mClickListener)
	    										.show();
	    				
	    				
	    			}
	    			
	    			
	    		} catch( Exception e ) {
	    			
	    			
	    			
	    		}
	    		
	    	}
    		
    	}
    	
    }
    
    DialogInterface.OnClickListener mClickListener = new DialogInterface.OnClickListener() {
		
		@Override public void onClick( DialogInterface arg0, int arg1 ) {
			
			if ( arg1 == DialogInterface.BUTTON_POSITIVE ) {
				
				final String appPackageName = mContext.getPackageName(); // getPackageName() from Context or Activity object
				
				try {
					
				    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				
				} catch (android.content.ActivityNotFoundException anfe) {
					
					mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
					
				}
				
			}
			
		}
		
	};
	
}
