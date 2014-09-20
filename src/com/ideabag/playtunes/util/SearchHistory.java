package com.ideabag.playtunes.util;

import com.google.gson.Gson;
import com.ideabag.playtunes.R;

import android.content.Context;
import android.content.SharedPreferences;

public class SearchHistory {
	
	public static final int SEARCH_HISTORY_SIZE = 20;
	
	public static void addSearchQuery( Context mContext, String mQuery ) {
		
		Gson gson = new Gson();
		
		mQuery = mQuery.trim();
		
		SharedPreferences mSharedPrefs = mContext.getSharedPreferences( mContext.getString( R.string.prefs_file ), Context.MODE_PRIVATE );
		SharedPreferences.Editor edit = mSharedPrefs.edit();
		
		String jsonArray = mSharedPrefs.getString( mContext.getString( R.string.pref_key_search_history ), "[]" );
		
		// Let's assume perfect decoding to a String array[]
		//Arrays.
		//JSONArray array = new JSONArray( jsonArray );
		
		String[] mSearchHistory = gson.fromJson( jsonArray, String[].class );
		
		int index = -1;
		
		for ( int i = 0, count = mSearchHistory.length; i < count; i++ ) {
			
			if ( mSearchHistory[i].equals( mQuery ) ) {
				
				index = i;
				break;
				
			}
			
		}
		
		if ( index != -1 ) {
			
			String tmp = mSearchHistory[ index ];
			
			//String[] trimmedArray = new String[ mSearchHistory.length ];
			
			
			
			for ( int i = 1, count = mSearchHistory.length; i < index && i < count - 1; i++ ) {
				
				mSearchHistory[ i ] = mSearchHistory[ i - 1 ];
				
			}
			
			mSearchHistory[0] = tmp;
			
			
		} else {
			
			String[] shiftedArray = new String[ mSearchHistory.length + 1 ];
			
			for ( int i = 1, count = mSearchHistory.length + 1; i < count && i < SEARCH_HISTORY_SIZE; i++ ) {
				
				shiftedArray[ i ] = mSearchHistory[ i - 1 ];
				
			}
			
			mSearchHistory = shiftedArray;
			mSearchHistory[0] = mQuery;
			
		}
		
		edit.putString( mContext.getString( R.string.pref_key_search_history ), gson.toJson( mSearchHistory ) );
		edit.commit();
		
	}
	
}
