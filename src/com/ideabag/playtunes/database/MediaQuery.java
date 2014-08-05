package com.ideabag.playtunes.database;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class MediaQuery {
	
	protected Uri mContentUri;
	
	protected String[] mProjection;
	protected String mSelection;
	protected String[] mSelectionArgs;
	protected String mOrderBy;
	
	public MediaQuery( Uri mUri, String[] projection ) {
		this( mUri, projection, null, null, null );
	}
	
	public MediaQuery( Uri mUri, String[] projection, String selection, String[] args ) {
		this( mUri, projection, selection, args, null );
	}
	
	public MediaQuery( Uri mUri, String[] projection, String selection, String[] args, String orderBy ) {
		
		this.mContentUri = mUri;
		this.mProjection = projection;
		this.mSelection = selection;
		this.mSelectionArgs = args;
		this.mOrderBy = orderBy;
		
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
				query.mContentUri,
				query.mProjection,
				query.mSelection,
				query.mSelectionArgs,
				query.mOrderBy );
		
	}
	
	@Override public boolean equals( Object ob ) {
		
		boolean isEqual = false;
		
		if ( ob instanceof MediaQuery ) {
			
			try {
				
				MediaQuery other = ( MediaQuery ) ob;
				
				if ( other.mContentUri.equals( mContentUri )
						&& other.mProjection.equals( mProjection ) ) {
					
					if ( ( other.mSelection == null && mSelection == null )
							|| ( other.mSelection != null && mSelection != null && other.mSelection.equals( mSelection ) ) ) {
						
						if ( ( other.mSelectionArgs == null && mSelectionArgs == null)
								|| ( other.mSelectionArgs != null
										&& mSelectionArgs != null
										&& other.mSelectionArgs.equals( mSelectionArgs )
									) ) {
							
							if ( ( other.mSelectionArgs == null && mSelectionArgs == null)
								|| ( other.mSelectionArgs != null
										&& mSelectionArgs != null
										&& other.mSelectionArgs.equals( mSelectionArgs )
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
	
}
