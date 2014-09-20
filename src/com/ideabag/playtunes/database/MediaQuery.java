package com.ideabag.playtunes.database;

import com.google.gson.Gson;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class MediaQuery {
	/*
	protected Uri mContentUri;
	
	protected String[] mProjection;
	protected String mSelection;
	protected String[] mSelectionArgs;
	protected String mOrderBy;
	*/
	protected class MediaQueryJSONObject {
		
		public String mContentUriString;
		public String[] mProjection;
		public String mSelection;
		public String[] mSelectionArgs;
		public String mOrderBy;
		
	}
	
	protected MediaQueryJSONObject mQueryObject;
	
	public MediaQuery( String jsonString ) {
		
		Gson gson = new Gson();
		
		this.mQueryObject = gson.fromJson( jsonString, MediaQueryJSONObject.class );
		
		
		
	}
	
	public MediaQuery( Uri mUri, String[] projection ) {
		this( mUri, projection, null, null, null );
	}
	
	public MediaQuery( Uri mUri, String[] projection, String selection, String[] args ) {
		this( mUri, projection, selection, args, null );
	}
	
	public MediaQuery( Uri mUri, String[] projection, String selection, String[] args, String orderBy ) {
		
		mQueryObject = new MediaQueryJSONObject();
		
		mQueryObject.mContentUriString = mUri.toString();
		mQueryObject.mProjection = projection;
		mQueryObject.mSelection = selection;
		mQueryObject.mSelectionArgs = args;
		mQueryObject.mOrderBy = orderBy;
		
	}
	/*
	public Uri getUri() { return this.mContentUri; }
	public String[] getProjection() { return this.mProjection; }
	public String getSelection() { return this.mSelection; }
	public String[] getSelectionArgs() { return this.mSelectionArgs; };
	public String getOrderBy() { return this.mOrderBy; }
	*/
	public static Cursor execute( Context mContext, MediaQuery query ) {
		
		return mContext.getContentResolver().query(
				Uri.parse( query.mQueryObject.mContentUriString ),
				query.mQueryObject.mProjection,
				query.mQueryObject.mSelection,
				query.mQueryObject.mSelectionArgs,
				query.mQueryObject.mOrderBy );
		
	}
	
	@Override public boolean equals( Object ob ) {
		
		boolean isEqual = false;
		
		if ( ob instanceof MediaQuery ) {
			
			try {
				
				MediaQuery other = ( MediaQuery ) ob;
				
				if ( other.mQueryObject.mContentUriString.equals( mQueryObject.mContentUriString )
						&& other.mQueryObject.mProjection.equals( mQueryObject.mProjection ) ) {
					
					if ( ( other.mQueryObject.mSelection == null && mQueryObject.mSelection == null )
							|| ( other.mQueryObject.mSelection != null && mQueryObject.mSelection != null && other.mQueryObject.mSelection.equals( mQueryObject.mSelection ) ) ) {
						
						if ( ( other.mQueryObject.mSelectionArgs == null && mQueryObject.mSelectionArgs == null)
								|| ( other.mQueryObject.mSelectionArgs != null
										&& mQueryObject.mSelectionArgs != null
										&& other.mQueryObject.mSelectionArgs.equals( mQueryObject.mSelectionArgs )
									) ) {
							
							if ( ( other.mQueryObject.mSelectionArgs == null && mQueryObject.mSelectionArgs == null)
								|| ( other.mQueryObject.mSelectionArgs != null
										&& mQueryObject.mSelectionArgs != null
										&& other.mQueryObject.mSelectionArgs.equals( mQueryObject.mSelectionArgs )
									) ) {
								
								isEqual = true;
								
							}
							
						}
						
					}
					
					
					
				}
				
			} catch( Exception e ) {
				
				isEqual = false;
				
			}
			
		}
		
		
		return isEqual;
		
	}
	
	public String toJSONString() {
		
		Gson gson = new Gson();
		
		String mJSONString = gson.toJson( mQueryObject, MediaQueryJSONObject.class );
		
		
		return mJSONString;
		
	}
	
	@Override public String toString() {
		
		return toJSONString();
		
	}
	
}
