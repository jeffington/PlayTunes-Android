package com.ideabag.playtunes;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

public class PlayTunesListAdapter extends SimpleCursorAdapter {
	
	ArrayList<View> headers;
	//int[] headerResources;
	// Store the header views public methods to access them
	
	public PlayTunesListAdapter( Context context, int layout, Cursor c, String[] from, int[] to ) {
		
		super( context, layout, c, from, to );
		headers = new ArrayList<View>();
		
	}
	
	// Convenience methods for dealing with the headers
	public void addHeader( View v ) {
		
		headers.add( v );
		
	}
	
	public void removeHeader( View v ) {
		
		headers.remove( v );
		
	}
	
	public void addHeaders(ArrayList<View> list) {
		
		headers.addAll(list);
	}
	
	public void removeHeader(int index) {
		
		headers.remove(index);
	
	}
	
	public void clearHeaders() {
		headers.clear();
	}
	
	public void removeBottom() {
		
		if ( headers.size() > 0 ) {
			
			headers.remove( headers.size() - 1 );
			
		}
		
	}
	
	public void removeTop() {
		
		if ( headers.size() > 0 ) {
			
			headers.remove( 0 );
			
		}
		
	}
	
	public int getHeaderCount() {
		
		return headers.size();
		
	}
	
	public int getCount() {
		
		return super.getCount() + headers.size();
		
	}
	
	@Override public View getView( int position, View view, ViewGroup parent ) {
		
		if ( !headers.isEmpty() && position < headers.size() ) {
			
			return headers.get( position );
			
		}
		
		if ( null != view ) {// && view.getId() == R.id.ListHeader)
			
			view = null;
			
		}
		
		return super.getView( position - headers.size(), view, parent );
		
	}
}
