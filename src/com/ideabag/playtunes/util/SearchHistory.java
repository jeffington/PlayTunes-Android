package com.ideabag.playtunes.util;

import org.json.JSONArray;
import org.json.JSONException;

import com.ideabag.playtunes.R;

import android.content.Context;
import android.content.SharedPreferences;

public class SearchHistory {
	
	public static final int SEARCH_HISTORY_SIZE = 20;
	
	public static void addSearchQuery( Context mContext, String mQuery ) {
		
		mQuery = mQuery.trim();
		
		SharedPreferences mSharedPrefs = mContext.getSharedPreferences( mContext.getString( R.string.prefs_file ), Context.MODE_PRIVATE );
		SharedPreferences.Editor edit = mSharedPrefs.edit();
		
		try {
			
			String jsonArray = mSharedPrefs.getString( mContext.getString( R.string.pref_key_search_history ), "[]" );
			
			JSONArray array = new JSONArray( jsonArray );
			
			int index = -1;
			for ( int i = 0, count = array.length(); i < count; i++ ) {
				
				if ( array.getString( i ).equals( mQuery ) ) {
					
					index = i;
					break;
					
				}
				
			}
			//android.util.Log.i( "SearchHistory", "Index: " + index );
			
			if ( index != -1 ) {
				
				JSONArray trimmedArray = new JSONArray();
				
				for ( int i = 0; i < index; i++ ) {
					
					trimmedArray.put( i + 1, array.getString( i ) );
					
				}
				
				array = trimmedArray;
				
			} else {
				
				JSONArray shiftedArray = new JSONArray();
				
				for ( int i = 0, count = array.length() + 1; i < count && i < SEARCH_HISTORY_SIZE; i++ ) {
					
					if ( i == 0 ) {
						
						shiftedArray.put( 0, null );
						
					} else {
						
						shiftedArray.put( i, array.getString( i - 1 ) );
						
					}
					
				}
				
				array = shiftedArray;
				
			}
			
			array.put( 0, mQuery );
			
			android.util.Log.i( "SearchHistory", "" + array.toString() );
			
			edit.putString( mContext.getString( R.string.pref_key_search_history ), array.toString() );
			edit.commit();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
