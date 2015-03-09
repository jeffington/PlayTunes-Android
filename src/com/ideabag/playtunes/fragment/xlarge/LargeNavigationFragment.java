package com.ideabag.playtunes.fragment.xlarge;

import com.google.android.gms.analytics.HitBuilders;
import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.activity.SettingsActivity;
import com.ideabag.playtunes.adapter.AlbumsAllAdapter;
import com.ideabag.playtunes.adapter.ArtistsAllAdapter;
import com.ideabag.playtunes.adapter.GenresAllAdapter;
import com.ideabag.playtunes.adapter.PlaylistsAllAdapter;
import com.ideabag.playtunes.adapter.PlaylistsOneAdapter;
import com.ideabag.playtunes.adapter.SongsAllAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.dialog.CreatePlaylistDialogFragment;
import com.ideabag.playtunes.dialog.PlaylistMenuDialogFragment;
import com.ideabag.playtunes.dialog.SettingsDialogFragment;
import com.ideabag.playtunes.fragment.ArtistsAllFragment;
import com.ideabag.playtunes.fragment.BaseNavigationFragment;
import com.ideabag.playtunes.fragment.GenresAllFragment;
import com.ideabag.playtunes.fragment.MusicBrowserFragment;
import com.ideabag.playtunes.fragment.PlaylistsAllFragment;
import com.ideabag.playtunes.fragment.PlaylistsOneFragment;
import com.ideabag.playtunes.fragment.SongsFragment;
import com.ideabag.playtunes.fragment.xlarge.browser.BrowseSongsFragment;
import com.ideabag.playtunes.util.QueryCountTask;
import com.ideabag.playtunes.util.SearchHistory;
import com.ideabag.playtunes.util.GAEvent.Categories;
import com.ideabag.playtunes.util.GAEvent.Playlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.AdapterView;

/*
	This fragment handles a lot.
	LargeNavigationFragment is a hybrid of NavigationFragment and PlaylistsAllFragment.
	The layout view_basic_navigation.xml is used as the ListView header while the list is 
	backed by a PlaylistsAllAdapter.
	
	A Toolbar is another header on the ListView and is used instead of the ActionBar from PlaylistsAllFragment.
	
	Lastly, it manages the Search field and autocomplete terms found on the top ActionBar.

*/
public class LargeNavigationFragment extends BaseNavigationFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
	
	public static final String TAG = "NavigationFragment";
	
	protected MainActivity mActivity;
	protected FragmentManager mFragmentManager;
	//protected ActionBar mActionBar;
	
	protected Toolbar mMusicBrowserToolbar;
	protected Toolbar mActionBarToolbar;
	
	protected PlaylistManager mPlaylistManager;
	
	protected MusicBrowserFragment MusicBrowserFragment;
	
	protected LinearLayout mBasicNavigationView;
	
	protected ListView mNavigationListView;
	
	PlaylistsAllAdapter adapter;
	
	Toolbar mPlaylistToolbar;
	
	private TextView mBadgeSongsAll;
	private TextView mBadgeAlbumsAll;
	private TextView mBadgeArtistsAll;
	//private TextView mBadgeGenresAll;
	//private TextView mBadgePlaylistsAll;
	private TextView mBadgeStarredCount;
	
	MediaQuery mArtistsAllQuery;
	MediaQuery mGenresAllQuery;
	MediaQuery mPlaylistsAllQuery;
	MediaQuery mStarredCountQuery;
	MediaQuery mSongsAllQuery;
	MediaQuery mAlbumsAllQuery;
	
	//
	// Search functionality
	//
	public AutoCompleteTextView mQueryTextView;
	protected String mSearchQuery = null;
	SearchHistory mSearchHistory;
	
	// Have we warned the user that pressing Back will close the app?
	protected boolean mCloseWarningOn = false;
	
	public CharSequence mActionbarTitle, mActionbarSubtitle;
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate( R.layout.fragment_navigation, container, false );
		
	}
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
		mPlaylistManager = new PlaylistManager( getActivity() );
		/*
		mActionBar = mActivity.getSupportActionBar();
		
        mActionBar.setDisplayShowHomeEnabled( true );
        mActionBar.setIcon( R.drawable.ic_launcher ); 
        mActionBar.setDisplayHomeAsUpEnabled( false );
        mActionBar.setTitle( null );
        */
        mFragmentManager = mActivity.getSupportFragmentManager();
		
        
	}
	
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		
		super.onActivityCreated( savedInstanceState );
		
		
		
		if ( null != savedInstanceState ) {
			
			mSearchQuery = savedInstanceState.getString( getString( R.string.key_state_query_string ) );
			
		}
		
		// 
		// Set up new ActionBar (Toolbar) and toolbar for the music browser, specific to the large layout
		// 
		mMusicBrowserToolbar = ( Toolbar ) getActivity().findViewById( R.id.MusicBrowserToolbar );
		mActivity.setSupportActionBar( mMusicBrowserToolbar );
		
		mActionBarToolbar = ( Toolbar ) getActivity().findViewById( R.id.ActionBarToolbar );
		mActionBarToolbar.setLogo( R.drawable.ic_launcher );
		
		// 
		// Set up the sidebar listview
		// 
		
		mNavigationListView = ( ListView ) getView().findViewById( R.id.LargeNavigationListView );
		
		LayoutInflater inflater = ( LayoutInflater ) mActivity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		mBasicNavigationView = ( LinearLayout ) inflater.inflate( R.layout.view_basic_navigation, null, false );
		
		mNavigationListView.addHeaderView( mBasicNavigationView );
		
		mPlaylistToolbar = ( Toolbar ) inflater.inflate( R.layout.view_toolbar_playlists, null, false );
		mPlaylistToolbar.inflateMenu( R.menu.menu_playlists_all );
		mPlaylistToolbar.setTitle( R.string.playlists_plural );
		//mPlaylistToolbar.setLogo( R.drawable.ic_action_list_2 );
		
		mPlaylistToolbar.setOnMenuItemClickListener( PlaylistOnMenuItemClickListener );
		
		mNavigationListView.addHeaderView( mPlaylistToolbar );
		
		mNavigationListView.setOnItemClickListener( this );
		
		//
		// Add headers before setting the adapter
		//
		adapter = new PlaylistsAllAdapter( getActivity(), playlistMenuClickListener );
		mNavigationListView.setAdapter( adapter );
		

		mBadgeStarredCount = ( TextView ) getView().findViewById( R.id.BadgeStarredCount );
		mBadgeSongsAll = ( TextView ) getView().findViewById( R.id.BadgeSongsAll );
		mBadgeAlbumsAll = ( TextView ) getView().findViewById( R.id.BadgeAlbumsAll );
		mBadgeArtistsAll = ( TextView ) getView().findViewById( R.id.BadgeArtistsAll );
		//mBadgeGenresAll = ( TextView ) getView().findViewById( R.id.BadgeGenresAll );
		//mBadgePlaylistsAll = ( TextView ) getView().findViewById( R.id.BadgePlaylistsAll );
		
		mArtistsAllQuery = new MediaQuery(
				MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
				ArtistsAllAdapter.SELECTION,
				null,
				null,
				MediaStore.Audio.Artists.DEFAULT_SORT_ORDER
			);
		
		mGenresAllQuery = new MediaQuery(
				MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
				GenresAllAdapter.SELECTION,
				null,
				null,
				MediaStore.Audio.Genres.DEFAULT_SORT_ORDER
			);
		
		String mStarredId = mPlaylistManager.createStarredIfNotExist();
		/*
		mPlaylistsAllQuery = new MediaQuery(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				PlaylistsAllAdapter.SELECTION,
				MediaStore.Audio.Playlists._ID + " !=?",
				new String[] {
					
						mStarredId
						
				},
				null
			);
		*/
		mStarredCountQuery = new MediaQuery(
				MediaStore.Audio.Playlists.Members.getContentUri( "external", Long.parseLong( mStarredId ) ),
				PlaylistsOneAdapter.SELECTION,
				MediaStore.Audio.Playlists.Members.PLAYLIST_ID + "=?",
				new String[] {
					
					mStarredId
					
				},
				null
			);
		
		mSongsAllQuery = new MediaQuery(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				SongsAllAdapter.SELECTION,
				MediaStore.Audio.Media.IS_MUSIC + " != 0",
				null,
				null
				);
		
		mAlbumsAllQuery = new MediaQuery(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
				AlbumsAllAdapter.SELECTION,
				MediaStore.Audio.Media.ALBUM + "!=?",
				new String[] {
						getString( R.string.no_album_string )
				},
				MediaStore.Audio.Albums.DEFAULT_SORT_ORDER
			);
		
		mBasicNavigationView.findViewById( R.id.NavigationArtistsAll ).setOnClickListener( this );
		mBasicNavigationView.findViewById( R.id.NavigationAlbumsAll ).setOnClickListener( this );
		mBasicNavigationView.findViewById( R.id.NavigationStarred ).setOnClickListener( this );
		mBasicNavigationView.findViewById( R.id.NavigationSongsAll ).setOnClickListener( this );
        
		updateBadges();
		
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mediaStoreChanged );
		
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Playlists.Members.getContentUri( "external",
						Long.parseLong( mPlaylistManager.createStarredIfNotExist() ) ), true, mediaStoreChanged );
		
		 
		 MusicBrowserFragment = ( MusicBrowserFragment ) getActivity().getSupportFragmentManager().findFragmentById( R.id.MusicBrowserFragment );
		 
		 //
		 // Search functionality
		 //
		 
		 Toolbar.LayoutParams mSearchInputParams = new Toolbar.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
		 mSearchInputParams.gravity = Gravity.CENTER;
		 
		 //mActionBar.setCustomView( R.layout.view_search_compat );
		 
		 
		 //mActionBar.getCustomView().setLayoutParams( mSearchInputParams );
		 //mActionBar.setDisplayShowCustomEnabled( true );
		
		 LinearLayout mSearchView = ( LinearLayout ) getActivity().getLayoutInflater().inflate( R.layout.view_search_compat, null );
		 
		 mActionBarToolbar.addView( mSearchView, mSearchInputParams );
		 mActionBarToolbar.inflateMenu( R.menu.menu_settings );
		 mActionBarToolbar.setOnMenuItemClickListener( new OnMenuItemClickListener() {

			@Override public boolean onMenuItemClick( MenuItem item ) {
				
				if ( item.getItemId() == R.id.MenuItemSettings ) {
					
					FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			    	
					SettingsDialogFragment newFragment = new SettingsDialogFragment();
			    	
			        newFragment.show( ft, "dialog" );
			        
					return true;
					
				}
				
				return false;
				
			}
			 
			 
		 });
		 
		
		mSearchView.findViewById( R.id.SearchButton ).setOnClickListener( new OnClickListener() {
			
			@Override public void onClick( View v ) {
				
				String mQueryString = mQueryTextView.getEditableText().toString();
				//setMediaID( mQueryString );
				
			}
			
		});
		
		mSearchView.findViewById( R.id.ClearSearchButton ).setOnClickListener( new OnClickListener() {

			@Override public void onClick( View v ) {
				
				mQueryTextView.setText( "" );
				//setMediaID( null );
				
			}
			
		});
		
		mQueryTextView = ( AutoCompleteTextView ) mSearchView.findViewById( R.id.SearchQueryTextView );
		
		mQueryTextView.setOnEditorActionListener( new OnEditorActionListener() {

			@Override public boolean onEditorAction( TextView view, int actionId, KeyEvent event ) {
				
				boolean mConsumed = false;
				String query = mQueryTextView.getEditableText().toString();
				
				if ( event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER ) {
					
					android.util.Log.i( TAG, "Enter");
					
					
					SearchFragment mSearchFragment = new SearchFragment();
					
					mSearchFragment.setMediaID( query );
					
					transactFragment( mSearchFragment );
					
					mQueryTextView.clearFocus();
					mConsumed = true;
					
				} else if ( event != null && event.getAction() == KeyEvent.ACTION_DOWN ) {
					
					//setMediaID( query );
					android.util.Log.i( TAG, "Action Down");
					
					mConsumed = true;
					
				} else if ( actionId == EditorInfo.IME_ACTION_SEARCH ) {
					
					//setMediaID( query );
					android.util.Log.i( TAG, "Search");
					
					mConsumed = true;
					
				}
				
				return mConsumed;
				
			}
			
		});
		
		mSearchHistory = new SearchHistory( getActivity() );
		
		mQueryTextView.setAdapter( mSearchHistory.getAdapter() );
	 
		 
	}
    
	
	@Override public void onPause() {
		super.onPause();
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		getActivity().getContentResolver().unregisterContentObserver( mediaStoreChanged );
		
	}
	
	@Override public void onClick( View v ) {
		
		int id = v.getId();
		load( id );
		
	}
	
	protected void load( int id ) {
		
		Fragment mNewFragment = null;
		
		switch ( id ) {
		
		case R.id.NavigationArtistsAll:
			mNewFragment = new ArtistsAllFragment();
			break;
			
		case R.id.NavigationAlbumsAll:
			mNewFragment = new AlbumsAllFragment();
			break;
			
		case R.id.NavigationGenresAll:
	    	mNewFragment = new GenresAllFragment();
			break;
			
		case R.id.NavigationSongsAll:
			
	    	mNewFragment = new BrowseSongsFragment();
			break;
		
		case R.id.NavigationStarred:
			
			mNewFragment = new PlaylistsOneFragment();
			((PlaylistsOneFragment)mNewFragment).setMediaID( mPlaylistManager.createStarredIfNotExist() );
			
			break;
		
		case R.id.NavigationSearch:
			
			mNewFragment = new SearchFragment();
			
			break;
		case R.id.SettingsButton:
				
				Intent launchSettingsIntent = new Intent( getActivity(), SettingsActivity.class);
				getActivity().startActivity( launchSettingsIntent );
				
			break;
		default:
			
			mNewFragment = new PlaylistsAllFragment();
			break;
			
		}
		
		if ( null != mNewFragment ) {
			
			transactFragment( mNewFragment );
			
		}
		
	}
	
	ContentObserver mediaStoreChanged = new ContentObserver(new Handler()) {

        @Override public void onChange( boolean selfChange ) {
            
            mActivity.runOnUiThread( new Runnable() {

				@Override public void run() {
					
					adapter.notifyDataSetChanged();
					updateBadges();
					
				}
            	
            });
            
            super.onChange( selfChange );
            
        }

	};
	
    
    public void showNowPlaying() {
    	
    	MusicBrowserFragment.showNowPlaying();
    	
    }
    
    public void showSearch() {
    	
    	Fragment mSearchFragment = new SearchFragment();
    	
    	transactFragment( mSearchFragment );
    	
    }
    
    public void transactFragment( Fragment mFragment ) {
    	
    	MusicBrowserFragment.transactFragment( mFragment );
    	
    	mCloseWarningOn = false;
    	
    }
    
    public boolean onKeyDown( int keycode, KeyEvent e ) {
    	
    	switch ( keycode ) {
	        
	        case KeyEvent.KEYCODE_SEARCH:
	        	
	        	showSearch();
	        	return true;
	        
	        case KeyEvent.KEYCODE_BACK:
	        	
	        	if ( mFragmentManager.getBackStackEntryCount() == 0 ) {
	        		
	        		if ( !mCloseWarningOn ) {
		        		
		        		android.widget.Toast.makeText( mActivity, getString( R.string.back_warning ), android.widget.Toast.LENGTH_LONG ).show();
		        		mCloseWarningOn = true;
		        		
		        		return true;
		        		
		        	}
	        		
	        	} else {
	        		
	        		mCloseWarningOn = false;
	        		
	        	}
	        	break;
    	}
    	
    	return false;
    	
    }
	
    // TODO:
    // How do we want to deal with the Actionbar on big tablets?
    
	@Override public void setActionbarTitle( String titleString ) {
		
		mActionbarTitle = ( CharSequence ) titleString;
		
		mMusicBrowserToolbar.setTitle( mActionbarTitle );
		
	}
	
	@Override public void setActionbarSubtitle( String subtitleString ) {
		
		mActionbarSubtitle = ( CharSequence ) subtitleString;
		
		mMusicBrowserToolbar.setSubtitle( mActionbarSubtitle );
		
	}
	
	public View.OnClickListener playlistMenuClickListener = new View.OnClickListener() {
		
		@Override public void onClick(View v) {
			
			//int id = v.getId();
			
			ViewGroup list_item = ( ViewGroup ) v.getParent();
			String playlist_id = ( String ) list_item.getTag( R.id.tag_playlist_id);
			
			showPlaylistMenuDialog( playlist_id );
			
		}
		
	};
	
	private void showPlaylistMenuDialog( String playlist_id ) {
		
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
    	
		PlaylistMenuDialogFragment newFragment = new PlaylistMenuDialogFragment();
		newFragment.setMediaID( playlist_id );
    	
        newFragment.show( ft, "dialog" );
		
	}
	
	
	@Override public boolean onCreateOptionsMenu( Menu menu ) {
		
		//MenuInflater inflater = getActivity().getMenuInflater();
	    //inflater.inflate( R.menu.menu_settings, menu );
	    
		return false;
		
	}
    
    public void updateBadges() {
    	
    	new QueryCountTask( mBadgeSongsAll ).execute( mSongsAllQuery );
    	new QueryCountTask( mBadgeArtistsAll ).execute( mArtistsAllQuery );
    	new QueryCountTask( mBadgeAlbumsAll ).execute( mAlbumsAllQuery );
    	//new QueryCountTask( mBadgePlaylistsAll ).execute( mPlaylistsAllQuery );
    	new QueryCountTask( mBadgeSongsAll ).execute( mSongsAllQuery );
    	//new QueryCountTask( mBadgeGenresAll ).execute( mGenresAllQuery );
    	new QueryCountTask( mBadgeStarredCount ).execute( mStarredCountQuery );
    	
    }
    
    Toolbar.OnMenuItemClickListener PlaylistOnMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
		
		@Override public boolean onMenuItemClick( MenuItem item ) {
			
	        int id = item.getItemId();
	        
	        if ( id == R.id.MenuPlaylistsAdd ) {
	        	
	        	FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	        	
	        	DialogFragment newFragment = new CreatePlaylistDialogFragment();
	        	
	            newFragment.show( ft, "dialog" );
	        	
	        }
			
			return false;
			
		}
		
	};

	@Override public void onItemClick( AdapterView<?> parent, View v, int position, long id) {
		
		String playlist_id = ( String ) v.getTag( R.id.tag_playlist_id );
		
		PlaylistsOneFragment playlistFragment = new PlaylistsOneFragment();
		
		playlistFragment.setMediaID( playlist_id );
		
		mActivity.transactFragment( playlistFragment );
		/*
    	mTracker.send( new HitBuilders.EventBuilder()
    	.setCategory( Categories.PLAYLIST )
    	.setAction( Playlist.ACTION_CLICK )
    	.setValue( position )
    	.build());
		*/
	}
	
}
