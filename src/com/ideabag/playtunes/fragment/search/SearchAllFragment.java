package com.ideabag.playtunes.fragment.search;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.search.SearchAlbumsAdapter;
import com.ideabag.playtunes.adapter.search.SearchArtistsAdapter;
import com.ideabag.playtunes.adapter.search.SearchSongsAdapter;
import com.ideabag.playtunes.dialog.SongMenuDialogFragment;
import com.ideabag.playtunes.fragment.AlbumsOneFragment;
import com.ideabag.playtunes.fragment.ArtistAllSongsFragment;
import com.ideabag.playtunes.fragment.ArtistsOneFragment;
import com.ideabag.playtunes.fragment.SaveScrollListFragment;
import com.ideabag.playtunes.util.MergeAdapter;
import com.ideabag.playtunes.util.ISearchable;

public class SearchAllFragment extends SaveScrollListFragment implements ISearchable {
	
	public static final String TAG = "SearchAllFragment";
	
	private static final int SEARCH_RESULT_LIMIT = 5;
	
	// The adapters
	MergeAdapter adapter;
	
	SearchSongsAdapter mSearchSongs;
	SearchAlbumsAdapter mSearchAlbums;
	SearchArtistsAdapter mSearchArtists;
	
	// Headers/dividers
	
	LinearLayout mSongsHeader;
	LinearLayout mAlbumsHeader;
	LinearLayout mArtistsHeader;
	
	TextView mSongsSeeAll;
	TextView mAlbumsSeeAll;
	TextView mArtistsSeeAll;
	
	MainActivity mActivity;
	private String mQueryString;
	
	SearchFragment mSearchFragment;
	
	public SearchAllFragment() {
		
		
		
		
	}
	
	public void setSearchFragment( SearchFragment fragment ) {
		
		mSearchFragment = fragment;
		
	}
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		outState.putString( getString( R.string.key_state_query_string ), mQueryString );
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		if ( null != savedInstanceState ) {
			
			mQueryString = savedInstanceState.getString( getString( R.string.key_state_query_string ) );
			
		}
		
		adapter = new MergeAdapter();
		
		mSearchSongs = new SearchSongsAdapter( getActivity(), songMenuClickListener, mQueryString, SEARCH_RESULT_LIMIT );
		mSearchAlbums = new SearchAlbumsAdapter( getActivity(), mQueryString, SEARCH_RESULT_LIMIT );
		mSearchArtists = new SearchArtistsAdapter( getActivity(), mQueryString, SEARCH_RESULT_LIMIT );
		
		mSearchSongs.registerDataSetObserver( mAdapterObserver );
		mSearchAlbums.registerDataSetObserver( mAdapterObserver );
		mSearchArtists.registerDataSetObserver( mAdapterObserver );
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		mSongsHeader = ( LinearLayout ) inflater.inflate( R.layout.list_item_group_header, null );
		mAlbumsHeader = ( LinearLayout ) inflater.inflate( R.layout.list_item_group_header, null );
		mArtistsHeader = ( LinearLayout ) inflater.inflate( R.layout.list_item_group_header, null );
		
		( ( TextView ) mSongsHeader.findViewById( R.id.TextHeaderLabel ) ).setText( getString( R.string.songs_plural ) );
		( ( TextView ) mAlbumsHeader.findViewById( R.id.TextHeaderLabel ) ).setText( getString( R.string.albums_plural ) );
		( ( TextView ) mArtistsHeader.findViewById( R.id.TextHeaderLabel ) ).setText( getString( R.string.artists_plural ) );
		
		mSongsHeader.findViewById( R.id.SeeAllButton ).setOnClickListener( mSeeAllClickListener );
		mAlbumsHeader.findViewById( R.id.SeeAllButton ).setOnClickListener( mSeeAllClickListener );
		mArtistsHeader.findViewById( R.id.SeeAllButton ).setOnClickListener( mSeeAllClickListener );
		
		// Songs
		adapter.addView( mSongsHeader, false );
		adapter.addAdapter( mSearchSongs );
		//adapter.addView( mSongsSeeAll, true );
		
		// Albums
		adapter.addView( mAlbumsHeader, false );
		adapter.addAdapter( mSearchAlbums );
		//adapter.addView( mAlbumsSeeAll, true );
		
		// Artists
		adapter.addView( mArtistsHeader, false );
		adapter.addAdapter( mSearchArtists );
		//adapter.addView( mArtistsSeeAll, true );
		
		getListView().setHeaderDividersEnabled( true );
		
		setListAdapter( adapter );
		
		adapter.registerDataSetObserver( mAdapterObserver );
		
	}
	

	
	@Override public void onDestroyView() {
	    super.onDestroyView();
	    
	    setListAdapter( null );
	    
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		adapter.unregisterDataSetObserver( mAdapterObserver );
		
	}
	
	@Override public void setQuery( String queryString ) {
		
		if ( null != queryString ) {
			
			if ( !queryString.equals( mQueryString ) ) {
				
				mQueryString = queryString;
				
				if ( null != mSearchSongs ) {
					
					mSearchSongs.setQuery( queryString );
					
				}
				
				if ( null != mSearchArtists ) {
					
					mSearchArtists.setQuery( queryString );
					
				}
				
				if ( null != mSearchAlbums ) {
					
					mSearchAlbums.setQuery( queryString );
					
				}
				
			}
			
		}
		
	}
	
	DataSetObserver mAdapterObserver = new DataSetObserver() {
		
		@Override public void onChanged() {
			
			android.util.Log.i( TAG, "onChanged");
			
			int mSongCount = mSearchSongs.getCount();
			int mArtistCount = mSearchArtists.getCount();
			int mAlbumCount = mSearchAlbums.getCount();
			
			if ( mArtistCount == 0 ) {
				
				mArtistsHeader.setVisibility( View.GONE );
				
			} else {
				
				mArtistsHeader.setVisibility( View.VISIBLE );
				
			}
			
			if ( mAlbumCount == 0 ) {
				
				mAlbumsHeader.setVisibility( View.GONE );
				
			} else {
				
				mAlbumsHeader.setVisibility( View.VISIBLE );
				
			}
			
			if ( mSongCount == 0 ) {
				
				mSongsHeader.setVisibility( View.GONE );
				
			} else {
				
				mSongsHeader.setVisibility( View.VISIBLE );
				
			}
			
		}
		
	};
	
	/*
	ContentObserver mediaStoreChanged = new ContentObserver(new Handler()) {

        @Override public void onChange( boolean selfChange ) {
            
            mActivity.runOnUiThread( new Runnable() {
            	
				@Override public void run() {
					
					adapter.requery();
					adapter.notifyDataSetChanged();
					
				}
            	
            });
            
            super.onChange( selfChange );
            
        }

	};
	*/
	
	View.OnClickListener songMenuClickListener = new View.OnClickListener() {
		
		@Override public void onClick( View v ) {
			
			int viewID = v.getId();
			String songID = "" + v.getTag( R.id.tag_song_id );
			
			if ( viewID == R.id.StarButton ) {
				
				ToggleButton starButton = ( ToggleButton ) v;
				
				if ( starButton.isChecked() ) {
					
					mActivity.PlaylistManager.addFavorite( songID );
					//android.util.Log.i( "starred", songID );
					
				} else {
					
					mActivity.PlaylistManager.removeFavorite( songID );
					//android.util.Log.i( "unstarred", songID );
					
				}
				
			} else if ( viewID == R.id.MenuButton ) {
				
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	        	
				SongMenuDialogFragment newFragment = new SongMenuDialogFragment();
				newFragment.setMediaID( songID );
	        	
	            newFragment.show( ft, "dialog" );
				
			}
			
			
			
		}
		
	};
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		
		
		//convertView.setTag( R.id.tag_album_id, cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM ) ) );
		
		int mSongSection = mSearchSongs.getCount() + 1;
		
		int mAlbumsSection = mSongSection + mSearchAlbums.getCount() + 1;
		
		int mArtistsSection = mAlbumsSection + mSearchArtists.getCount() + 1;
		
		if ( position > 0 && position <= mSongSection ) {
			
			// Play song
			String playlistName = getString( R.string.search ) + " \"" + mQueryString + "\"";
			
			//
			// A bit of explanation is required here.
			// The setPlaylist method takes a Fragment class and a media id
			// When the user wants to return to "now playing", this information is used to regenerate the playing fragment
			// In this case, the media_id is the search query instead of a normal media id for an album, playlist, or artist
			// 
			mActivity.mBoundService.setPlaylist( mSearchSongs.getQuery(), playlistName, SearchFragment.class, mQueryString );
			
			mActivity.mBoundService.setPlaylistPosition( position - 1 ); // subtract one for the SONGS header
			
			mActivity.mBoundService.play();
			
			
		} else if ( position > mSongSection && position <= mAlbumsSection ) {
			
			// Show Album
			String albumID = ( String ) v.getTag( R.id.tag_album_id );
			AlbumsOneFragment albumFragment = new AlbumsOneFragment( );
			albumFragment.setMediaID( albumID );
			
			mActivity.transactFragment( albumFragment );
			
		} else if ( position > mAlbumsSection && position <= mArtistsSection ) {
			
			// Show Artist
			String artistID = ( String ) v.getTag( R.id.tag_artist_id );
			
			//boolean artistUnknown = v.getTag( R.id.tag_artist_unknown ).equals( "1" );
			
			//int albumCount = Integer.parseInt( ( ( TextView ) v.findViewById( R.id.AlbumCount ) ).getText().toString() );
			/*
			if ( artistUnknown || 0 == albumCount ) {
				
				ArtistAllSongsFragment artistAllFragment = new ArtistAllSongsFragment();
				artistAllFragment.setMediaID( artistID );
				
				mActivity.transactFragment( artistAllFragment );
				
			} else {
			*/
				ArtistsOneFragment artistFragment = new ArtistsOneFragment();
				artistFragment.setMediaID( artistID );
				
				mActivity.transactFragment( artistFragment );
				
			//}
			
		}

		
	}
	
	View.OnClickListener mSeeAllClickListener = new View.OnClickListener() {

		@Override public void onClick( View v ) {
			
			View parent = ( View ) v.getParent();
			
			if ( mSongsHeader == parent ) {
				
				SearchSongsFragment mSearchSongsFragment = new SearchSongsFragment();
				mSearchSongsFragment.setQuery( mQueryString );
				
				if ( mSearchFragment != null ) {
					
					mSearchFragment.transactFragment( mSearchSongsFragment );
					
				}
				
			} else if ( mAlbumsHeader == parent ) {
				
				SearchAlbumsFragment mSearchAlbumsFragment = new SearchAlbumsFragment();
				mSearchAlbumsFragment.setQuery( mQueryString );
				
				if ( mSearchFragment != null ) {
					
					mSearchFragment.transactFragment( mSearchAlbumsFragment );
					
				}
				
				
			} else {
				
				SearchArtistsFragment mSearchArtistsFragment = new SearchArtistsFragment();
				mSearchArtistsFragment.setQuery( mQueryString );
				
				if ( mSearchFragment != null ) {
					
					mSearchFragment.transactFragment( mSearchArtistsFragment );
					
				}
				
			}
			
		}
		
	};
	
}
