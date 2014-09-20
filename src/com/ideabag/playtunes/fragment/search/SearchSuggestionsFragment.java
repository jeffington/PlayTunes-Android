package com.ideabag.playtunes.fragment.search;

import com.google.gson.Gson;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.fragment.SaveScrollListFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SearchSuggestionsFragment extends SaveScrollListFragment {
	
	public static final String TAG = "SearchSuggestionsFragment";
	
	TextView mClearHistory;
	SearchFragment mSearchFragment;
	
	SharedPreferences mSharedPrefs;
	
	ArrayAdapter < String > adapter;
	String[] mSearchQueries = null;
	
	public SearchSuggestionsFragment() {
		
		
		
	}
	
	public SearchSuggestionsFragment( SearchFragment parentFragment ) {
		
		mSearchFragment = parentFragment;
		
	}
	
	private void loadSearchHistory() {
		
		Gson gson = new Gson();
		String searchHistoryString = mSharedPrefs.getString( getString( R.string.pref_key_search_history ), "[]" );
		
		String[] mSearchQueries = gson.fromJson( searchHistoryString, String[].class);
		
		if ( null != mSearchQueries ) {
			
			adapter = new ArrayAdapter < String >( getActivity(), R.layout.list_item_title, R.id.Title, mSearchQueries );
			setListAdapter( adapter );
			
		}
		/*
    	if ( adapter.getCount() < 1 ) {
    		
        	mClearHistory.setVisibility( View.VISIBLE );
        	
    	} else {
    		
    		mClearHistory.setVisibility( View.GONE );
    		
    	}
		*/
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		mSharedPrefs = getActivity().getSharedPreferences( getString( R.string.prefs_file ), Context.MODE_PRIVATE );
		mSharedPrefs.registerOnSharedPreferenceChangeListener( mPreferencesChangeListener );
		
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
    	
		
    	mClearHistory = ( TextView ) inflater.inflate( R.layout.list_item_title, null );
    	mClearHistory.setText( getString( R.string.clear_search_history ) );
    	getListView().addFooterView( mClearHistory, null, true );
    

    	
    	getListView().setHeaderDividersEnabled( true );
		getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
    	
		loadSearchHistory();
    	
		
	}
	
	@Override public void onDestroyView() {
	    super.onDestroyView();
	    
	    setListAdapter( null );
	    
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		mSharedPrefs.unregisterOnSharedPreferenceChangeListener( mPreferencesChangeListener );
		
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		if ( position > adapter.getCount() ) {
			
			clearSearchHistory();
			
		} else {
			
			String mSearchTerm = ( String ) adapter.getItem( position );
			
			mSearchFragment.mQueryTextView.setText( mSearchTerm );
			mSearchFragment.setMediaID( mSearchTerm );
			
			
		}
		
	}
	
	private void clearSearchHistory() {
		
		//suggestions.clearHistory();
		//adapter.requery();
		adapter.notifyDataSetChanged();
		
		if ( adapter.getCount() > 0 ) {
			
			mClearHistory.setVisibility( View.VISIBLE );
			
		} else {
			
			mClearHistory.setVisibility( View.GONE );
			
		}
		
		
	}
	
	SharedPreferences.OnSharedPreferenceChangeListener mPreferencesChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
		
		@Override public void onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key ) {
			
			if ( key.equals( getString( R.string.pref_key_search_history ) ) ) {
				
				loadSearchHistory();
				
			}
			
		}
		
	};
	
}
