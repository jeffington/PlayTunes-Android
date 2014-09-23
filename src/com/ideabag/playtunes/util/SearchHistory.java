package com.ideabag.playtunes.util;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.ideabag.playtunes.R;

import android.content.Context;
import android.content.SharedPreferences;

public class SearchHistory {
	
	public static final String TAG = "SearchHistory";
	
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
		
		android.util.Log.i( TAG, "" + jsonArray );
		String[] mSearchHistory = gson.fromJson( jsonArray, String[].class );
		ArrayList< String > mSearchArray = new ArrayList< String >( mSearchHistory.length );
		

		
		for ( int i = 0, count = mSearchHistory.length; i < count; i++ ) {
			
			// Remove nulls
			if ( mSearchHistory[ i ] != null ) {
				
				mSearchArray.add( mSearchHistory[ i ] );
				
			}
			
		}
		
		
		int index = mSearchArray.indexOf( mQuery );
		
		
		if ( index >= 0 ) {
			
			String tmp = mSearchArray.remove(index);
			mSearchArray.add( 0, tmp );
			
		} else {
			
			mSearchArray.add( 0, mQuery );
			
		}
		
		edit.putString( mContext.getString( R.string.pref_key_search_history ), gson.toJson( mSearchArray.toArray() ) );
		edit.commit();
		
	}
	
	public static void clearHistory( Context mContext ) {
		
		SharedPreferences mSharedPrefs = mContext.getSharedPreferences( mContext.getString( R.string.prefs_file ), Context.MODE_PRIVATE );
		SharedPreferences.Editor edit = mSharedPrefs.edit();
		
		edit.putString( mContext.getString( R.string.pref_key_search_history ), "[]" );
		
		edit.commit();
		
	}
	
}
