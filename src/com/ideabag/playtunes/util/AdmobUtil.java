package com.ideabag.playtunes.util;

import com.google.android.gms.ads.AdRequest;

import com.ideabag.playtunes.R;
import android.content.Context;

public class AdmobUtil {
	
	public static void AddTestDevices( Context mContext, AdRequest.Builder requestBuilder ) {
		
		String[] device_ids = mContext.getResources().getStringArray( R.array.admob_test_device_ids );
		
		for ( int i = 0, count = device_ids.length; i < count; i++ ) {
			
			requestBuilder.addTestDevice( device_ids[ i ] );
			
		}
		
		
	}
	
}
