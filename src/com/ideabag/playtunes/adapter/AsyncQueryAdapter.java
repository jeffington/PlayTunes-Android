package com.ideabag.playtunes.adapter;

import java.lang.ref.WeakReference;

import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.database.MediaQuery.OnQueryCompletedListener;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class AsyncQueryAdapter extends BaseAdapter implements MediaQuery.OnQueryCompletedListener {
	
	protected Context mContext;
	
	protected Cursor mCursor = null;
	protected MediaQuery mQuery = null;
	
	protected WeakReference< MediaQuery.OnQueryCompletedListener > mListener = null;
	
	public AsyncQueryAdapter( Context context ) {
		
		mContext = context;
		
	}
	
    public MediaQuery getQuery() {
    	
    	return mQuery;
    	
    }
    
    public void setOnQueryCompletedListener( OnQueryCompletedListener listener ) {
    	
    	mListener = new WeakReference< MediaQuery.OnQueryCompletedListener > ( listener );
    	
    }
    
    protected void setMediaQuery( MediaQuery query ) {
    	
    	mQuery = query;
    	
    	requery();
    	
    }
    
    //
    // Probably depreciated, use requeryAsync instead
	//
    public void requery() {
		
    	MediaQuery.executeAsync( mContext, mQuery, this );
		
	}
    
    @Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
		
		if ( null != mCursor && !mCursor.isClosed() ) {
			
			mCursor.close();
			
		}
		
		mCursor = mResult;
		notifyDataSetChanged();
		
		if ( null != mListener ) {
			
			MediaQuery.OnQueryCompletedListener listener = mListener.get();
			
			if ( null != listener ) {
				
				listener.onQueryCompleted( mQuery, mResult );
				
			}
			
		}
		
	}
	
	
	@Override public int getCount() {
		
		return ( null != mCursor && !mCursor.isClosed() ? mCursor.getCount() : 0 );
		
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

}
