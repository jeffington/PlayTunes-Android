package com.ideabag.playtunes.fragment.search;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.util.IMusicBrowser;
import com.ideabag.playtunes.util.SearchHistory;
import com.ideabag.playtunes.util.ISearchable;
import com.ideabag.playtunes.util.TrackerSingleton;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SearchFragment extends Fragment implements IMusicBrowser {
	
	public static final String TAG = "Search Fragment";
	
    private MainActivity mActivity;
	public AutoCompleteTextView mQueryTextView;
    
	//SearchRecentSuggestions mSuggestions;
	
	private ISearchable mCurrentFragment = null;
	//SongSearchAdapter adapter;
	
	protected String mSearchQuery = null;
	
	public SearchFragment() { }
	
	public SearchFragment( String query ) {
		
		setMediaID( query );
		
		
		
	}
	
	
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
		
	}
	
	@Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		
		return inflater.inflate( R.layout.fragment_search, container, false );
		
	}
	
	@Override public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		outState.putString( getString( R.string.key_state_query_string ), mSearchQuery );
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		if ( null != savedInstanceState ) {
			
			mSearchQuery = savedInstanceState.getString( getString( R.string.key_state_query_string ) );
			
		}
		
		setHasOptionsMenu( true );
    	
		/*
		mSuggestions = new SearchRecentSuggestions( getActivity(),
                RecentQuerySuggestionProvider.AUTHORITY, RecentQuerySuggestionProvider.MODE );
		*/
		
		ActionBar mBar = mActivity.getSupportActionBar();
			
		mBar.setCustomView( R.layout.view_search_compat );
		mBar.setDisplayShowCustomEnabled( true );
		
		mQueryTextView = ( AutoCompleteTextView ) mBar.getCustomView().findViewById( R.id.SearchQuery );
		
		mBar.getCustomView().findViewById( R.id.SearchButton ).setOnClickListener( new OnClickListener() {

			@Override public void onClick( View v ) {
				
				String mQueryString = mQueryTextView.getEditableText().toString();
				setMediaID( mQueryString );
				
			}
			
		});
		
		mBar.getCustomView().findViewById( R.id.ClearSearchButton ).setOnClickListener( new OnClickListener() {

			@Override public void onClick( View v ) {
				
				mQueryTextView.setText( "" );
				setMediaID( null );
				
			}
			
		});
		
		
		
		mQueryTextView.setOnEditorActionListener( new OnEditorActionListener() {

			@Override public boolean onEditorAction( TextView view, int actionId, KeyEvent event ) {
				
				if ( actionId == EditorInfo.IME_ACTION_SEARCH || event.getKeyCode() == KeyEvent.KEYCODE_ENTER ) {
					
					// Do search
					setMediaID( ( String ) view.getText() );
					
					return true;
					
				} else {
					
					if ( event.getAction() == KeyEvent.ACTION_DOWN ) {
						
						setMediaID( ( String ) view.getText() );
						
					}
					
					
				}
				
				// TODO Auto-generated method stub
				return false;
				
			}
			
		});
		
		
		SearchSuggestionsFragment mSuggestionsFragment = new SearchSuggestionsFragment( this );
		
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		
		ft.replace( R.id.SearchFragment, mSuggestionsFragment );
		// Don't add to back stack
		
		// Commit the transaction
		ft.commit();
		
		
		if ( null != mSearchQuery ) {
			
			android.util.Log.i( TAG, "Search query restore from saved instance state '" + mSearchQuery + "'" );
			mQueryTextView.setText( mSearchQuery );
			
			SearchAllFragment mSearchAllFragment = new SearchAllFragment();
			
			mSearchAllFragment.setQuery( mSearchQuery );
			//mSearchAllFragment.setP
			
			FragmentTransaction ft2 = getActivity().getSupportFragmentManager().beginTransaction();
			
			ft2.replace( R.id.SearchFragment, mSearchAllFragment );
	    	ft2.addToBackStack( null );
	    	ft2.commit();
			
		}
		
		
	}
	
	
	@Override public void onResume() {
		super.onResume();
		
		mActivity.setActionbarTitle( null );
    	mActivity.setActionbarSubtitle( null );
    	mActivity.mShowSearch = false;
    	mActivity.supportInvalidateOptionsMenu();
		/*
		Tracker tracker = TrackerSingleton.getDefaultTracker( mActivity.getBaseContext() );
		
		tracker.setScreenName( TAG );
		tracker.send( new HitBuilders.AppViewBuilder().build() );
		
		//t.set( "_count", ""+adapter.getCount() );
		tracker.send( new HitBuilders.EventBuilder()
    	.setCategory( "playlist" )
    	.setAction( "show" )
    	.setLabel( TAG )
    	.setValue( adapter.getCount() )
    	.build());
	        // Send a screen view.
		
		*/
	}
		
	@Override public void onPause() {
		super.onPause();
		//mActivity.AdView.pause();
		
	}
	
	@Override public void onDestroyView() {
		super.onDestroyView();
	    
	    //setListAdapter( null );
		ActionBar bar = mActivity.getSupportActionBar();
		bar.getCustomView().setVisibility( View.GONE );
		bar.setCustomView( null );
		mActivity.mShowSearch = true;
    	mActivity.supportInvalidateOptionsMenu();
		//mActivity.supportInvalidateOptionsMenu();
		
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		setHasOptionsMenu( false );
		
		
		
		//getActivity().getContentResolver().unregisterContentObserver( mediaStoreChanged );
		
	}

	// PlaylistBrowser interface methods
	
	@Override public void setMediaID(String media_id) {
		
		if ( null != media_id && media_id.length() > 1 ) {
			
			mSearchQuery = media_id;
			//mSuggestions.saveRecentQuery( mSearchQuery, null);
			SearchHistory.addSearchQuery( getActivity(), mSearchQuery );
			
			// Determine if a SearchableFragment is in place, if there call setQuery on that Fragment
			// If there isn't, add a SearchAllFragment
			
			Fragment mCurrentFragment = getActivity().getSupportFragmentManager().findFragmentById( R.id.SearchFragment );
			
			if ( null == mCurrentFragment
					|| mCurrentFragment instanceof SearchSuggestionsFragment ) {
				
				android.util.Log.i( TAG, "Loading search all fragment");
				
				SearchAllFragment mSearchAllFragment = new SearchAllFragment();
				
				mSearchAllFragment.setQuery( mSearchQuery );
				mSearchAllFragment.setSearchFragment( this );
				
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				
				ft.replace( R.id.SearchFragment, mSearchAllFragment );
		    	ft.addToBackStack( null );
		    	ft.commit();
		    	
			} else {
				
				android.util.Log.i( TAG, "Updating the existing searchable fragment.");
				
				ISearchable mSearchable = ( ISearchable ) mCurrentFragment;
				
				mSearchable.setQuery( mSearchQuery );
				
			}
	    	
		}
		
	}
	
	public void transactFragment( Fragment mFrag ) {
		
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		
		ft.replace( R.id.SearchFragment, mFrag );
    	ft.addToBackStack( null );
    	ft.commit();
		
	}
	
	@Override public String getMediaID() {
		
		return mSearchQuery;
		
	}

}