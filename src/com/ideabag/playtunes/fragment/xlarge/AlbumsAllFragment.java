package com.ideabag.playtunes.fragment.xlarge;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.DragNDrop.DynamicListView;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.AlbumsAllAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.fragment.AlbumsOneFragment;
import com.ideabag.playtunes.util.IMusicBrowser;
import com.ideabag.playtunes.util.TrackerSingleton;
import com.ideabag.playtunes.util.GAEvent.Categories;
import com.ideabag.playtunes.util.GAEvent.Playlist;

import android.app.Activity;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.AdapterView;

/*
	The big difference between this and the other AlbumsAllFragment is that it uses a GridView instead of ListView
	
	Note:
	In the future, this Fragment may toggle between GridView and ListView
	
*/

public class AlbumsAllFragment extends Fragment implements IMusicBrowser, AdapterView.OnItemClickListener {

	public static final String TAG = "All Albums Grid Fragment";
	
	private MainActivity mActivity;
	private Tracker mTracker;
	
	AlbumsAllAdapter adapter;
	GridView mGridView;
	
	@Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		
		Resources r = getResources();
		float px = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 14, r.getDisplayMetrics() );
		
		mGridView = new GridView( getActivity() );
		
		mGridView.setHorizontalSpacing( r.getDimensionPixelSize( R.dimen.spacing_medium ) );
		mGridView.setVerticalSpacing( r.getDimensionPixelSize( R.dimen.spacing_medium ) );
		mGridView.setNumColumns( -1 ); // -1 = auto_fit
		mGridView.setColumnWidth( ( int ) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 240, r.getDisplayMetrics() ) );
		
		return mGridView;
		
	}
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		mTracker =  TrackerSingleton.getDefaultTracker( mActivity.getBaseContext() );
		mTracker.setScreenName( TAG );
		mActivity.setActionbarTitle( getString( R.string.albums_plural) );
		
	}
	

	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
    	adapter = new AlbumsAllAdapter( getActivity(), new MediaQuery.OnQueryCompletedListener() {
			
			@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
				
				mActivity.setActionbarSubtitle( mResult.getCount() + " " + ( mResult.getCount() == 1 ? getString( R.string.album_singular ) : getString( R.string.albums_plural ) ) );
				
				//restoreScrollPosition();
				
		    	mTracker.send( new HitBuilders.EventBuilder()
		    	.setCategory( Categories.PLAYLIST )
		    	.setAction( Playlist.ACTION_SHOWLIST )
		    	.setValue( mResult.getCount() )
		    	.build());
		    	
			}
			
    	});
    	
    	//adapter.setOn
    	
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
		//getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		//getListView().setDividerHeight( 1 );
		mGridView.setSelector( R.drawable.list_item_background );
		mGridView.setOnItemClickListener( this );
		mGridView.setAdapter( adapter );
		
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, true, mediaStoreChanged );
		
		
	}
	
	@Override public void onResume() {
		super.onResume();
		
    	//mActivity.setActionbarTitle( getString( R.string.albums_plural) );
		mActivity.setActionbarTitle( getString( R.string.albums_plural) );


	        // Set screen name.
	        // Where path is a String representing the screen name.
		//t.set( "_count", ""+adapter.getCount() );
		
		// Send a screen view.
    	mTracker.send( new HitBuilders.AppViewBuilder().build() );
		
	}
	
	@Override public void onPause() {
		super.onPause();
		
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		getActivity().getContentResolver().unregisterContentObserver( mediaStoreChanged );
		
	}
	
	@Override
	public void setMediaID(String media_id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getMediaID() {
		// TODO Auto-generated method stub
		return null;
	}
	
	ContentObserver mediaStoreChanged = new ContentObserver( new Handler() ) {
		
        @Override public void onChange( boolean selfChange ) {
            
            mActivity.runOnUiThread( new Runnable() {

				@Override public void run() {
					
					//saveScrollPosition();
					adapter.requery();
					//adapter.notifyDataSetChanged();
					
				}
            	
            });
            
            super.onChange( selfChange );
            
        }

	};

	@Override public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

		String albumID = ( String ) v.getTag( R.id.tag_album_id );
		
		AlbumsOneFragment albumFragment = new AlbumsOneFragment( );
		albumFragment.setMediaID( albumID );
		
		mActivity.transactFragment( albumFragment );
		
    	mTracker.send( new HitBuilders.EventBuilder()
    	.setCategory( Categories.PLAYLIST )
    	.setAction( Playlist.ACTION_CLICK )
    	.setValue( position )
    	.build());
		
	}
	
}
