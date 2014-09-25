package com.ideabag.playtunes.adapter.search;

import com.ideabag.playtunes.adapter.SongListAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.util.ISearchable;

import android.content.Context;
import android.provider.MediaStore;
import android.view.View.OnClickListener;

public class SearchSongsAdapter extends SongListAdapter implements ISearchable {
	
	private static final String SEPARATORS = ":|;|,|.|-|_|\"|'|\\s|";
	
	public static final String TAG = "SongSearchAdapter";
	
	private int mTruncateAmount;
	
	public SearchSongsAdapter( Context context, OnClickListener menuClickListener, String searchTerms, int truncated ) {
		super(context, menuClickListener);
		
		mTruncateAmount = truncated;
		
		if ( null != searchTerms && searchTerms.length() > 0 ) {
			
			setQuery( searchTerms );
    		
		}
		
	}
	
	@Override public void setQuery( String searchTerms ) {
		
		String[] terms = searchTerms.split( " " );
		
		String mRelevance = "("; 
		
		for ( int i = 0, count = terms.length; i < count; i++ ) {
			
			String term = terms[ i ];
			
			if ( i > 0 ) {
				
				mRelevance += "+";
				
			}
			
			mRelevance += "3 * (" + MediaStore.Audio.Media.TITLE + " LIKE '%" + term + "%' )";
			mRelevance += "+ 30 * (" + MediaStore.Audio.Media.TITLE + " LIKE '" + term + "%' )";
			mRelevance += "+ 9 * (" + MediaStore.Audio.Media.TITLE + " LIKE '% " + term + "%' )";
			
			mRelevance += "+ (" + MediaStore.Audio.Media.ARTIST + " LIKE '%" + term + "%' )";
			mRelevance += "+ 10 * (" + MediaStore.Audio.Media.ARTIST + " LIKE '" + term + "%' )";
			mRelevance += "+ 3 * (" + MediaStore.Audio.Media.ARTIST + " LIKE '% " + term + "%' )";
			
			mRelevance += "+ (" + MediaStore.Audio.Media.ALBUM + " LIKE '%" + term + "%' )";
			mRelevance += "+ 10 * (" + MediaStore.Audio.Media.ALBUM + " LIKE '" + term + "%' )";
			mRelevance += "+ 3 * (" + MediaStore.Audio.Media.ALBUM + " LIKE '% " + term + "%' )";
			
		}
		
		mRelevance += ") WEIGHT";
		
	    String[] songSearchSelection = new String[] {
		    	
		    	MediaStore.Audio.Media._ID,
		    	
		    	MediaStore.Audio.Media.TITLE,
		    	MediaStore.Audio.Media.ARTIST,
		    	MediaStore.Audio.Media.ALBUM,
		    	MediaStore.Audio.Media.TRACK,
		    	MediaStore.Audio.Media.DATA,
		    	MediaStore.Audio.Media.ALBUM_ID,
		    	MediaStore.Audio.Media.ARTIST_ID,
		    	mRelevance
		    	
		    };
	    
	    //android.util.Log.i( TAG + "@setQuery", "Weight projection: " + mRelevance );
		
    	mQuery = new MediaQuery(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				songSearchSelection,
				MediaStore.Audio.Media.IS_MUSIC + " != 0 AND WEIGHT > 0",
				null,
				"WEIGHT DESC"
				);
		
    	super.requery();
    	
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
		
		return ( cursor != null && mTruncateAmount > 0 && cursor.getCount() > mTruncateAmount ? cursor.getCount() - mTruncateAmount : 0 );
		
	}
	
	
}
