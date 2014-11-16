package com.ideabag.playtunes.adapter.search;

import com.ideabag.playtunes.adapter.SongListAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.util.ISearchableAdapter;

import android.content.Context;
import android.provider.MediaStore;
import android.view.View.OnClickListener;

public class SearchSongsAdapter extends SongListAdapter implements ISearchableAdapter {
	
	private static final String SEPARATORS = ":|;|,|.|-|_|\"|'|\\s|";
	
	public static final String TAG = "SongSearchAdapter";
	
	private int mTruncateAmount;
	
	public SearchSongsAdapter( Context context, OnClickListener menuClickListener, String searchTerms, int truncated, MediaQuery.OnQueryCompletedListener listener ) {
		super(context, menuClickListener);
		
		mTruncateAmount = truncated;
		
		setOnQueryCompletedListener( listener );
		
		if ( null != searchTerms && searchTerms.length() > 0 ) {
			
			setSearchTerms( searchTerms );
    		
		}
		
	}
	
	//
	// This is where the search query is generated using the SQL LIKE operator, we use a dumb algorithm to determine a 'best' match
	// If a term is anywhere in the title, artist, or album ('%term%') it gets 3 points,
	// if a term is at the beginning of the title, artist, or album ('term%') it gets 30 points
	// if a term is in the title, artist, or album and is preceded by whitespace (' +term%') it gets 9 points
	// 
	
	@Override public void setSearchTerms( String searchTerms ) {
		
		String[] terms = searchTerms.split( "\\s+" );
		
		String mRelevance = "("; 
		
		for ( int i = 0, count = terms.length; i < count; i++ ) {
			
			String term = terms[ i ];
			
			if ( i > 0 ) {
				
				mRelevance += "+";
				
			}
			
			mRelevance += "3 * (" + MediaStore.Audio.Media.TITLE + " LIKE '%" + term + "%' )";
			mRelevance += "+ 30 * (" + MediaStore.Audio.Media.TITLE + " LIKE '" + term + "%' )";
			mRelevance += "+ 9 * (" + MediaStore.Audio.Media.TITLE + " LIKE '% +" + term + "%' )";
			
			mRelevance += "+ (" + MediaStore.Audio.Media.ARTIST + " LIKE '%" + term + "%' )";
			mRelevance += "+ 10 * (" + MediaStore.Audio.Media.ARTIST + " LIKE '" + term + "%' )";
			mRelevance += "+ 3 * (" + MediaStore.Audio.Media.ARTIST + " LIKE '% +" + term + "%' )";
			
			mRelevance += "+ (" + MediaStore.Audio.Media.ALBUM + " LIKE '%" + term + "%' )";
			mRelevance += "+ 10 * (" + MediaStore.Audio.Media.ALBUM + " LIKE '" + term + "%' )";
			mRelevance += "+ 3 * (" + MediaStore.Audio.Media.ALBUM + " LIKE '% +" + term + "%' )";
			
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
    	
    	//setMediaQuery( mQuery );
    	requery();
    	
	}
	
	@Override public int getCount() {
		
		int count = 0;
		
		if ( null != mCursor ) {
			
			if ( mTruncateAmount <= 0 ) {
				
				count = mCursor.getCount();
				
			} else {
				
				count = ( mCursor.getCount() > mTruncateAmount ? mTruncateAmount : mCursor.getCount() );
				
			}
			
		}
		
		return count;
		
	}
	
	public int hasMore() {
		
		return ( mCursor != null && mTruncateAmount > 0 && mCursor.getCount() > mTruncateAmount ? mCursor.getCount() - mTruncateAmount : 0 );
		
	}
	
	public void setTruncateAmount( int mAmt ) {
		
		mTruncateAmount = mAmt;
		notifyDataSetChanged();
		
	}
	
}
