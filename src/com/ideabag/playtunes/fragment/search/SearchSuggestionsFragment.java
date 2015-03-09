package com.ideabag.playtunes.fragment.search;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.fragment.SaveScrollListFragment;
import com.ideabag.playtunes.util.SearchHistory;
import com.ideabag.playtunes.util.TrackerSingleton;
import com.ideabag.playtunes.util.GAEvent.Categories;
import com.ideabag.playtunes.util.GAEvent.Playlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SearchSuggestionsFragment extends Fragment implements ListView.OnItemClickListener {
	
	public static final String TAG = "Search Suggestions Fragment";
	
	SearchFragment mSearchFragment;
	ListView mListView;
	
	private Button mButtonClearHistory;
	
	private Tracker mTracker;
	
	SearchHistory mSearchHistory;
	
	public SearchSuggestionsFragment() {
		
		
		
	}
	
	public SearchSuggestionsFragment( SearchFragment parentFragment ) {
		
		mSearchFragment = parentFragment;
		mSearchHistory = new SearchHistory( getActivity() );
		
	}
	
	private void loadSearchHistory() {
		
		mListView.setAdapter( mSearchHistory.getAdapter() );
		
    	if ( mSearchHistory.getAdapter().getCount() >= 1 ) {
    		
    		mButtonClearHistory.setVisibility( View.VISIBLE );
    		
    	} else {
    		
    		mButtonClearHistory.setVisibility( View.GONE );
    		
    	}
		
	}
	
	@Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		
		return inflater.inflate( R.layout.fragment_search_suggestions, container, false );
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		
		
		mListView = ( ListView ) getView().findViewById( R.id.SuggestionListView );
		mButtonClearHistory = ( Button ) getView().findViewById( R.id.ButtonClearHistory );
    	
    	
    	mListView.setDivider( getResources().getDrawable( R.drawable.list_divider ) );
    	mListView.setDividerHeight( 1 );
    	
    	mListView.setOnItemClickListener( this );
    	
		mTracker = TrackerSingleton.getDefaultTracker( getActivity() );
		
		loadSearchHistory();
    	
		mButtonClearHistory.setOnClickListener( new OnClickListener() {

			@Override public void onClick( View v ) {
				
				clearSearchHistory();
				
			}
			
		});
		
	}
	
	@Override public void onResume() {
		super.onResume();
		
		mTracker.setScreenName( TAG );
		
		mTracker.send( new HitBuilders.AppViewBuilder().build() );
		
		mTracker.send( new HitBuilders.EventBuilder()
    	.setCategory( Categories.PLAYLIST )
    	.setAction( Playlist.ACTION_SHOWLIST )
    	.setValue( mSearchHistory.getAdapter().getCount() )
    	.build());
		
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		mSearchHistory.destroy();
		
	}
	
	@Override public void onItemClick(AdapterView<?> list, View v, int position, long id ) {
		
		String mSearchTerm = ( String ) mSearchHistory.getAdapter().getItem( position );
			
		mSearchFragment.mQueryTextView.setText( mSearchTerm );
		mSearchFragment.setMediaID( mSearchTerm );
		
		mTracker.send( new HitBuilders.EventBuilder()
    	.setCategory( Categories.PLAYLIST )
    	.setAction( Playlist.ACTION_CLICK )
    	.setValue( position )
    	.build());
		
	}
	
	private void clearSearchHistory() {
		
		mSearchHistory.clearHistory();
		
		if ( mSearchHistory.getAdapter().getCount() > 0 ) {
			
			mButtonClearHistory.setVisibility( View.VISIBLE );
			
		} else {
			
			mButtonClearHistory.setVisibility( View.GONE );
			
		}
		
		mTracker.send( new HitBuilders.EventBuilder()
    	.setCategory( Categories.PLAYLIST )
    	.setAction( Playlist.ACTION_SHOWLIST )
    	.setValue( mSearchHistory.getAdapter().getCount() )
    	.build());
		
	}
	


}
