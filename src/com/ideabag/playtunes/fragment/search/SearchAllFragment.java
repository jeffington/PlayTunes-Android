package com.ideabag.playtunes.fragment.search;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.search.SearchAlbumsAdapter;
import com.ideabag.playtunes.adapter.search.SearchArtistsAdapter;
import com.ideabag.playtunes.adapter.search.SearchSongsAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.dialog.SongMenuDialogFragment;
import com.ideabag.playtunes.fragment.AlbumsOneFragment;
import com.ideabag.playtunes.fragment.ArtistsOneFragment;
import com.ideabag.playtunes.fragment.SaveScrollListFragment;
import com.ideabag.playtunes.util.MergeAdapter;
import com.ideabag.playtunes.util.ISearchableAdapter;

public class SearchAllFragment extends SaveScrollListFragment implements ISearchableAdapter {
	
	public static final String TAG = "SearchAllFragment";
	
	private static int SEARCH_RESULT_LIMIT = 3;
	
	// The adapters
	MergeAdapter adapter;
	
	protected SearchSongsAdapter mSearchSongs;
	protected SearchAlbumsAdapter mSearchAlbums;
	protected SearchArtistsAdapter mSearchArtists;
	
	// Headers/dividers
	
	LinearLayout mSongsHeader;
	LinearLayout mAlbumsHeader;
	LinearLayout mArtistsHeader;
	
	TextView mSongsCount;
	TextView mAlbumsCount;
	TextView mArtistsCount;
	
	
	
	MainActivity mActivity;
	protected String mQueryString;
	
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
		
		mSearchSongs = new SearchSongsAdapter( getActivity(), songMenuClickListener, mQueryString, SEARCH_RESULT_LIMIT, new MediaQuery.OnQueryCompletedListener() {
			
			@Override public void onQueryCompleted(MediaQuery mQuery, Cursor mResult) {
				
				int mSongCount = mResult.getCount();
				
				mSongsCount.setText( "" + mSongCount );
				
			}
			
		});
		mSearchAlbums = new SearchAlbumsAdapter( getActivity(), mQueryString, SEARCH_RESULT_LIMIT, new MediaQuery.OnQueryCompletedListener() {
			
			@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
				
				int mAlbumCount = mResult.getCount();
				
				mAlbumsCount.setText( "" + mAlbumCount );
				
			}
			
		});
		mSearchArtists = new SearchArtistsAdapter( getActivity(), mQueryString, SEARCH_RESULT_LIMIT, new MediaQuery.OnQueryCompletedListener() {
			
			@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
				
				int mArtistCount = mResult.getCount();
				
				mArtistsCount.setText( "" + mArtistCount );
				
			}
			
		});
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		mSongsHeader = ( LinearLayout ) inflater.inflate( R.layout.list_item_group_header, null );
		mAlbumsHeader = ( LinearLayout ) inflater.inflate( R.layout.list_item_group_header, null );
		mArtistsHeader = ( LinearLayout ) inflater.inflate( R.layout.list_item_group_header, null );
		
		( ( TextView ) mSongsHeader.findViewById( R.id.Title ) ).setText( getString( R.string.songs_plural ) );
		( ( TextView ) mAlbumsHeader.findViewById( R.id.Title ) ).setText( getString( R.string.albums_plural ) );
		( ( TextView ) mArtistsHeader.findViewById( R.id.Title ) ).setText( getString( R.string.artists_plural ) );
		
		//ImageView iv;
		( ( ImageView ) mArtistsHeader.findViewById( R.id.BadgeIcon ) ).setImageResource( R.drawable.ic_action_mic );
		( ( ImageView ) mAlbumsHeader.findViewById( R.id.BadgeIcon ) ).setImageResource( R.drawable.ic_action_record );
		mSongsCount = ( TextView ) mSongsHeader.findViewById( R.id.Count );
		mAlbumsCount = ( TextView ) mAlbumsHeader.findViewById( R.id.Count );
		mArtistsCount = ( TextView ) mArtistsHeader.findViewById( R.id.Count );
		
		// Songs
		adapter.addView( mSongsHeader, true );
		adapter.addAdapter( mSearchSongs );
		
		// Albums
		adapter.addView( mAlbumsHeader, true );
		adapter.addAdapter( mSearchAlbums );
		
		// Artists
		adapter.addView( mArtistsHeader, true );
		adapter.addAdapter( mSearchArtists );
		
		setListAdapter( adapter );
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
		getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
		
	}
	

	
	@Override public void onDestroyView() {
	    super.onDestroyView();
	    
	    setListAdapter( null );
	    
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
	}
	
	@Override public void setSearchTerms( String queryString ) {
		
		if ( null != queryString ) {
			
			if ( !queryString.equals( mQueryString ) ) {
				
				mQueryString = queryString;
				
				if ( null != mSearchSongs ) {
					
					mSearchSongs.setSearchTerms( queryString );
					
				}
				
				if ( null != mSearchArtists ) {
					
					mSearchArtists.setSearchTerms( queryString );
					
				}
				
				if ( null != mSearchAlbums ) {
					
					mSearchAlbums.setSearchTerms( queryString );
					
				}
				
			}
			
		}
		
	}
	
	
	ContentObserver mediaStoreChanged = new ContentObserver(new Handler()) {

        @Override public void onChange( boolean selfChange ) {
            
            mActivity.runOnUiThread( new Runnable() {
            	
				@Override public void run() {
					
					mSearchSongs.requery();
					mSearchAlbums.requery();
					mSearchArtists.requery();
					
				}
            	
            });
            
            super.onChange( selfChange );
            
        }

	};
	
	
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
		
		int mArtistsSection = mAlbumsSection + mSearchArtists.getCount();
		
		if ( position == 0 ) {
			// Songs header
			SearchSongsFragment songFragment = new SearchSongsFragment( mSearchSongs );
			//SearchSongsAdapter.
			
			mSearchFragment.transactFragment( songFragment );
			
		} else if ( position > 0 && position < mSongSection ) {
			
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
			
			
		} else if ( position == mSongSection ) { // Album Header
			
			SearchAlbumsFragment albumFragment = new SearchAlbumsFragment( mSearchAlbums );
			
			mSearchFragment.transactFragment( albumFragment );
			
			
		} else if ( position > mSongSection && position < mAlbumsSection ) {
			
			// Show Album
			String albumID = ( String ) v.getTag( R.id.tag_album_id );
			AlbumsOneFragment albumFragment = new AlbumsOneFragment( );
			albumFragment.setMediaID( albumID );
			
			mActivity.NavigationFragment.transactFragment( albumFragment );
			//mSearchFragment.transactFragment( albumFragment );
			
		} else if ( position == mAlbumsSection ) { // Artists Header
			
			SearchArtistsFragment artistFragment = new SearchArtistsFragment( mSearchArtists );
			
			mSearchFragment.transactFragment( artistFragment );
			
		} else if ( position > mAlbumsSection && position <= mArtistsSection ) {
			
			// Show Artist
			String artistID = ( String ) v.getTag( R.id.tag_artist_id );
			
			ArtistsOneFragment artistFragment = new ArtistsOneFragment();
			artistFragment.setMediaID( artistID );
			
			mActivity.NavigationFragment.transactFragment( artistFragment );
			
		}
		

		
	}
	
}
