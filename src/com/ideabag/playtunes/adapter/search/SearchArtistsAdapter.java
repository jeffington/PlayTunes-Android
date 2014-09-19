package com.ideabag.playtunes.adapter.search;

import com.ideabag.playtunes.adapter.ArtistListAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.util.ISearchable;

import android.content.Context;
import android.provider.MediaStore;

public class SearchArtistsAdapter extends ArtistListAdapter implements ISearchable {
	
	private String searchTerms;
	
	private int mTruncateAmount;
	
	public SearchArtistsAdapter( Context context, String query, int truncate ) {
		super( context );
		
		mTruncateAmount = truncate;
		
		if ( null != searchTerms && searchTerms.length() > 0) {
			
			setQuery( query );
			
		}
		
	}
	
	@Override public void setQuery( String queryString ) {
		
		if ( null != queryString && !queryString.equals( searchTerms ) ) {
			
			searchTerms = queryString;
			
			String mRelevance = "("; 
			
			mRelevance += "(" + MediaStore.Audio.Artists.ARTIST + " LIKE '%" + searchTerms + "%' )";
			mRelevance += "+ 10 * (" + MediaStore.Audio.Artists.ARTIST + " LIKE '" + searchTerms + "%' )";
			mRelevance += "+ 3 * (" + MediaStore.Audio.Artists.ARTIST + " LIKE '% " + searchTerms + "%' )";
			
			mRelevance += ") WEIGHT";
			
			String[] artistsSelection = new String[] {
					
			    	MediaStore.Audio.Artists.ARTIST,
			    	MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
			    	MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
			    	MediaStore.Audio.Artists._ID,
			    	mRelevance
				};
		    
		    //android.util.Log.i( TAG + "@setQuery", "Weight projection: " + mRelevance );
			
			mQuery = new MediaQuery(
					MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
					artistsSelection,
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
