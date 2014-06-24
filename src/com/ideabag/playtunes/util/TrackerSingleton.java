package com.ideabag.playtunes.util;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;

import android.content.Context;

public class TrackerSingleton {

	  private static Tracker mTracker = null;
	  
	  public static synchronized Tracker getDefaultTracker( Context mContext ) {
		  
	    if ( null == mTracker ) {
	    	
	      GoogleAnalytics analytics = GoogleAnalytics.getInstance( mContext );
	      
	      mTracker = analytics.newTracker( R.xml.analytics );
	      

	    }
	    
	    return mTracker;
	    
	  }
	  
	}