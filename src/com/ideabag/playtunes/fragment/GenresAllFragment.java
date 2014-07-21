package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.GenresAllAdapter;
import com.ideabag.playtunes.util.MergeAdapter;
import com.ideabag.playtunes.util.TrackerSingleton;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;

public class GenresAllFragment extends ListFragment {
	
	public static final String TAG = "All Genres Fragment";
	
	MergeAdapter adapter;
	GenresAllAdapter genresAdapter;
	
	private MainActivity mActivity;
	
	@Override public void onAttach( Activity activity ) {
			
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		ActionBar bar =	( ( ActionBarActivity ) getActivity()).getSupportActionBar();
		adapter = new MergeAdapter();
		genresAdapter = new GenresAllAdapter( getActivity() );
		
		//adapter.addView( mActivity.AdContainer, true );
		adapter.addAdapter( genresAdapter );
		
    	
    	bar.setTitle( getString( R.string.genres_plural) );
    	mActivity.actionbarTitle = bar.getTitle();
    	
		bar.setSubtitle( genresAdapter.getCount() + " " + ( adapter.getCount() == 1 ? getString( R.string.genre_singular ) : getString( R.string.genres_plural ) ) );
		mActivity.actionbarSubtitle = bar.getSubtitle();
		
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
		getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
		
		
    	setListAdapter( adapter );
    	
	}
		
	@Override public void onResume() {
		super.onResume();
		
		//mActivity.AdView.resume();
		
		Tracker t = TrackerSingleton.getDefaultTracker( mActivity );

	    // Set screen name.
	    // Where path is a String representing the screen name.
		t.setScreenName( TAG );
		//t.set( "_count", ""+adapter.getCount() );
		
	    // Send a screen view.
		t.send( new HitBuilders.AppViewBuilder().build() );
		
		t.send( new HitBuilders.EventBuilder()
    	.setCategory( "playlist" )
    	.setAction( "show" )
    	.setLabel( TAG )
    	.setValue( adapter.getCount() )
    	.build());
		
	}
		
	@Override public void onPause() {
		super.onPause();
		//mActivity.AdView.pause();
		
		
	}
	
	@Override public void onDestroyView() {
	    super.onDestroyView();
	    
	    setListAdapter( null );
	    
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		String genre_id = (String) v.getTag( R.id.tag_genre_id );
		
		GenresOneFragment genreFragment = new GenresOneFragment();
		
		genreFragment.setMediaID( genre_id );
		
		mActivity.transactFragment( genreFragment );
		
	}
	
}