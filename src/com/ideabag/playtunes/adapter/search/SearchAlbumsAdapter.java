package com.ideabag.playtunes.adapter.search;

import com.ideabag.playtunes.adapter.AlbumListAdapter;
import com.ideabag.playtunes.database.MediaQuery;

import android.content.Context;
import android.provider.MediaStore;

public class SearchAlbumsAdapter extends AlbumListAdapter {
	
	public static final String TAG = "SongAlbumsAdapter";
	String mQueryString;
	
	private int mTruncateAmount;
	
	public SearchAlbumsAdapter( Context context, String searchTerms, int truncated ) {
		super( context );
		
		mTruncateAmount = truncated;
		
		if ( null != searchTerms && searchTerms.length() > 0 ) {
			
			setQuery( searchTerms );
    		
		}
		
	}
	//
	// Compare both the album name and artist
	//
	public void setQuery( String queryString ) {
		
		if ( null != queryString && !queryString.equals( mQueryString ) ) {
			
			mQueryString = queryString;
			
			String mRelevance = "("; 
			
			mRelevance += "2 * (" + MediaStore.Audio.Albums.ALBUM + " LIKE '%" + mQueryString + "%' )";
			mRelevance += "+ 20 * (" + MediaStore.Audio.Albums.ALBUM + " LIKE '" + mQueryString + "%' )";
			mRelevance += "+ 6 * (" + MediaStore.Audio.Albums.ALBUM + " LIKE '% " + mQueryString + "%' )";
			
			mRelevance += "+ (" + MediaStore.Audio.Albums.ARTIST + " LIKE '%" + mQueryString + "%' )";
			mRelevance += "+ 10 * (" + MediaStore.Audio.Albums.ARTIST + " LIKE '" + mQueryString + "%' )";
			mRelevance += "+ 3 * (" + MediaStore.Audio.Albums.ARTIST + " LIKE '% " + mQueryString + "%' )";
			
			mRelevance += ") WEIGHT";
			
			String[] allAlbumsSelection = new String[] {
			    	
			    	MediaStore.Audio.Albums.ALBUM,
			    	MediaStore.Audio.Albums.ARTIST,
			    	MediaStore.Audio.Albums.NUMBER_OF_SONGS,
			    	MediaStore.Audio.Albums.ALBUM_ART,
			    	MediaStore.Audio.Albums._ID,
			    	mRelevance,
			    	
			    };
		    
		    //android.util.Log.i( TAG + "@setQuery", "Weight projection: " + mRelevance );
			
	    	mQuery = new MediaQuery(
					MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
					allAlbumsSelection,
					"WEIGHT > 0",
					null,
					"WEIGHT DESC"
					);
			
			requery();
	    	
		}
    	
	}
	
	@Override public int getCount() {
		
		int count = 0;
		
		if ( null != cursor ) {
			
			if ( mTruncateAmount <= 0 ) {
				
				count = cursor.getCount();
				
			} else {
				
				count = ( cursor.getCount() > mTruncateAmount ? mTruncateAmount : cursor.getCount() );
				
			}
			
		}
		
		return count;
		
	}
	
	public int hasMore() {
		
		return ( cursor != null && mTruncateAmount > 0 ? cursor.getCount() - mTruncateAmount : 0 );
		
	}
	
	
}
