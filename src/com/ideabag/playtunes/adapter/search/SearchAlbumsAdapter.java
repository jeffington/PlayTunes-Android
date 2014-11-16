package com.ideabag.playtunes.adapter.search;

import com.ideabag.playtunes.adapter.AlbumListAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.util.ISearchableAdapter;

import android.content.Context;
import android.provider.MediaStore;

public class SearchAlbumsAdapter extends AlbumListAdapter implements ISearchableAdapter {
	
	public static final String TAG = "SongAlbumsAdapter";
	String mSearchTerms;
	
	private int mTruncateAmount;
	
	public SearchAlbumsAdapter( Context context, String searchTerms, int truncated, MediaQuery.OnQueryCompletedListener listener ) {
		super( context );
		
		mTruncateAmount = truncated;
		
		setOnQueryCompletedListener( listener );
		
		if ( null != searchTerms && searchTerms.length() > 0 ) {
			
			setSearchTerms( searchTerms );
    		
		}
		
	}
	//
	// Compare both the album name and artist
	//
	public void setSearchTerms( String queryString ) {
		
		if ( null != queryString && !queryString.equals( mSearchTerms ) ) {
			
			mSearchTerms = queryString;

			String[] terms = mSearchTerms.split( "\\s+" );
			
			String mRelevance = "("; 
			
			for ( int i = 0, count = terms.length; i < count; i++ ) {
				
				String term = terms[ i ];
				
				if ( i > 0 ) {
					
					mRelevance += "+";
					
				}
			
				mRelevance += "2 * (" + MediaStore.Audio.Albums.ALBUM + " LIKE '%" + term + "%' )";
				mRelevance += "+ 20 * (" + MediaStore.Audio.Albums.ALBUM + " LIKE '" + term + "%' )";
				mRelevance += "+ 6 * (" + MediaStore.Audio.Albums.ALBUM + " LIKE '% +" + term + "%' )";
				
				mRelevance += "+ (" + MediaStore.Audio.Albums.ARTIST + " LIKE '%" + term + "%' )";
				mRelevance += "+ 10 * (" + MediaStore.Audio.Albums.ARTIST + " LIKE '" + term + "%' )";
				mRelevance += "+ 3 * (" + MediaStore.Audio.Albums.ARTIST + " LIKE '% +" + term + "%' )";
				
			}
			
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
			
	    	MediaQuery query = new MediaQuery(
					MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
					allAlbumsSelection,
					"WEIGHT > 0",
					null,
					"WEIGHT DESC"
					);
			
			setMediaQuery( query );
	    	
		}
    	
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
