package com.ideabag.playtunes.fragment.xlarge.browser;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;

import com.google.android.gms.analytics.HitBuilders;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.AlbumListAdapter;
import com.ideabag.playtunes.adapter.AlbumsAllAdapter;
import com.ideabag.playtunes.adapter.ArtistAlbumsAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.fragment.AlbumsOneFragment;
import com.ideabag.playtunes.util.GAEvent.Categories;
import com.ideabag.playtunes.util.GAEvent.Playlist;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class FilteredAlbumsFragment extends ListFragment implements OnItemClickListener {
	
	AlbumListAdapter mAlbumsAdapter;
	
	FilteredSongsFragment mFilteredSongsFragment;
	
	String artist_id;
	
	MainActivity mActivity;
	
	public FilteredAlbumsFragment() {
		
		
	}
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		mAlbumsAdapter = new AlbumsAllAdapter( getActivity(), null );
		mAlbumsAdapter.LAYOUT_RESOURCE_ID = R.layout.list_item_album;
		
		setListAdapter( mAlbumsAdapter );
		
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
		getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
		
	}
	
	
	
	public void setArtistID( String id ) {
		
		artist_id = id;
		
		mAlbumsAdapter = new ArtistAlbumsAdapter( getActivity(), artist_id, new MediaQuery.OnQueryCompletedListener() {
			
			@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
				
				int count = mResult.getCount();
				
				//mActivity.setActionbarSubtitle( count + " " + ( count == 1 ? getString( R.string.album_singular ) : getString( R.string.albums_plural ) ) );
				
			}
			
		});
		
		mAlbumsAdapter.LAYOUT_RESOURCE_ID = R.layout.list_item_album;
		// Adapters are asynchronous, so do them first
		setListAdapter( mAlbumsAdapter );
		
		if ( null == mFilteredSongsFragment ) {
			
			mFilteredSongsFragment = ( FilteredSongsFragment ) getFragmentManager().findFragmentById( R.id.FilteredSongsFragment );
			
		}
		
		if ( mFilteredSongsFragment != null ) {
			
			mFilteredSongsFragment.setArtistID( artist_id );
			
		}
		
	}
	
	public void setGenreID( String genre_id ) {
		
		
		
	}
	
/*
	TODO:
	On list item click, we will pass the artist_id and album_id to the songs fragment
	
*/
	
    public static final String[] SELECTION = new String[] {
    	
    	MediaStore.Audio.Media._ID,
    	
    	MediaStore.Audio.Media.TITLE,
    	MediaStore.Audio.Media.ARTIST,
    	MediaStore.Audio.Media.ALBUM,
    	MediaStore.Audio.Media.TRACK,
    	MediaStore.Audio.Media.DATA,
    	MediaStore.Audio.Media.ALBUM_ID,
    	MediaStore.Audio.Media.ARTIST_ID
    	
    	
    };
    
	@Override public void onItemClick( AdapterView<?> parent, View v, int position, long id ) {
		
		String albumID = ( String ) v.getTag( R.id.tag_album_id );
		
		if ( null == mFilteredSongsFragment ) {
			
			mFilteredSongsFragment = ( FilteredSongsFragment ) getFragmentManager().findFragmentById( R.id.FilteredSongsFragment );
			
		}
		
		if ( mFilteredSongsFragment != null ) {
			
			mFilteredSongsFragment.setAlbumID( albumID );
			
		}
		
		/*
    	mTracker.send( new HitBuilders.EventBuilder()
    	.setCategory( Categories.PLAYLIST )
    	.setAction( Playlist.ACTION_CLICK )
    	.setValue( position )
    	.build());
		*/
		
	}
	
}
