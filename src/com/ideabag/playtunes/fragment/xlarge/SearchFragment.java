package com.ideabag.playtunes.fragment.xlarge;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.fragment.AlbumsOneFragment;
import com.ideabag.playtunes.fragment.ArtistsOneFragment;
import com.ideabag.playtunes.fragment.search.SearchAlbumsFragment;
import com.ideabag.playtunes.fragment.search.SearchAllFragment;
import com.ideabag.playtunes.fragment.search.SearchArtistsFragment;
import com.ideabag.playtunes.fragment.search.SearchSongsFragment;
import com.ideabag.playtunes.util.IMusicBrowser;

public class SearchFragment extends SearchAllFragment implements IMusicBrowser {
	
	MainActivity mActivity;
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
	
	@Override public void setMediaID( String media_id ) {
		
		setSearchTerms( media_id );
		
	}

	@Override public String getMediaID() {
		
		return mQueryString;
		
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		
		
		//convertView.setTag( R.id.tag_album_id, cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM ) ) );
		
		
		int mSongSection = mSearchSongs.getCount() + 1;
		
		int mAlbumsSection = mSongSection + mSearchAlbums.getCount() + 1;
		
		int mArtistsSection = mAlbumsSection + mSearchArtists.getCount();
		
		if ( position == 0 ) {
			// Songs header
			SearchSongsFragment songFragment = new SearchSongsFragment( mSearchSongs );
			//SearchSongsAdapter.
			mActivity.NavigationFragment.transactFragment( songFragment );
			
			
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
			
			mActivity.NavigationFragment.transactFragment( albumFragment );
			
			
		} else if ( position > mSongSection && position < mAlbumsSection ) {
			
			// Show Album
			String albumID = ( String ) v.getTag( R.id.tag_album_id );
			AlbumsOneFragment albumFragment = new AlbumsOneFragment( );
			albumFragment.setMediaID( albumID );
			
			mActivity.NavigationFragment.transactFragment( albumFragment );
			//mSearchFragment.transactFragment( albumFragment );
			
		} else if ( position == mAlbumsSection ) { // Artists Header
			
			SearchArtistsFragment artistFragment = new SearchArtistsFragment( mSearchArtists );
			
			mActivity.NavigationFragment.transactFragment( artistFragment );
			
		} else if ( position > mAlbumsSection && position <= mArtistsSection ) {
			
			// Show Artist
			String artistID = ( String ) v.getTag( R.id.tag_artist_id );
			
			ArtistsOneFragment artistFragment = new ArtistsOneFragment();
			artistFragment.setMediaID( artistID );
			
			mActivity.NavigationFragment.transactFragment( artistFragment );
			
		}
		

		
	}

}
