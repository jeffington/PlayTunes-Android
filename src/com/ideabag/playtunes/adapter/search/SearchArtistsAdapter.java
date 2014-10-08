package com.ideabag.playtunes.adapter.search;

import com.ideabag.playtunes.adapter.ArtistListAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.util.ISearchableAdapter;

import android.content.Context;
import android.provider.MediaStore;

public class SearchArtistsAdapter extends ArtistListAdapter implements ISearchableAdapter {
	
	private String searchTerms;
	
	private int mTruncateAmount;
	
	public SearchArtistsAdapter( Context context, String query, int truncate, MediaQuery.OnQueryCompletedListener listener ) {
		super( context );
		
		mTruncateAmount = truncate;
		
		setOnQueryCompletedListener( listener );
		
		if ( null != query && query.length() > 0) {
			
			setSearchTerms( query );
			
		}
		
	}
	
	@Override public void setSearchTerms( String queryString ) {
		
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
