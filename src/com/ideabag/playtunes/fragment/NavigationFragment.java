package com.ideabag.playtunes.fragment;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.SettingsActivity;
import com.ideabag.playtunes.adapter.AlbumsAllAdapter;
import com.ideabag.playtunes.adapter.ArtistsAllAdapter;
import com.ideabag.playtunes.adapter.GenresAllAdapter;
import com.ideabag.playtunes.adapter.PlaylistsAllAdapter;
import com.ideabag.playtunes.adapter.PlaylistsOneAdapter;
import com.ideabag.playtunes.adapter.SongsAllAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.fragment.search.SearchFragment;
import com.ideabag.playtunes.util.QueryCountTask;

import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class NavigationFragment extends BaseNavigationFragment implements View.OnClickListener {
	
	public static final String TAG = "NavigationFragment";

	
	private TextView mBadgeSongsAll;
	private TextView mBadgeAlbumsAll;
	private TextView mBadgeArtistsAll;
	private TextView mBadgeGenresAll;
	private TextView mBadgePlaylistsAll;
	private TextView mBadgeStarredCount;
	
	MediaQuery mArtistsAllQuery;
	MediaQuery mGenresAllQuery;
	MediaQuery mPlaylistsAllQuery;
	MediaQuery mStarredCountQuery;
	MediaQuery mSongsAllQuery;
	MediaQuery mAlbumsAllQuery;
	
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate( R.layout.fragment_navigation, container, false );
		
	}
	
	
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		
		super.onActivityCreated( savedInstanceState );
		
		mBadgeStarredCount = ( TextView ) getView().findViewById( R.id.BadgeStarredCount );
		mBadgeSongsAll = ( TextView ) getView().findViewById( R.id.BadgeSongsAll );
		mBadgeAlbumsAll = ( TextView ) getView().findViewById( R.id.BadgeAlbumsAll );
		mBadgeArtistsAll = ( TextView ) getView().findViewById( R.id.BadgeArtistsAll );
		mBadgeGenresAll = ( TextView ) getView().findViewById( R.id.BadgeGenresAll );
		mBadgePlaylistsAll = ( TextView ) getView().findViewById( R.id.BadgePlaylistsAll );
		
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
		
		mPlaylistsAllQuery = new MediaQuery(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				PlaylistsAllAdapter.SELECTION,
				MediaStore.Audio.Playlists._ID + " !=?",
				new String[] {
					
						mStarredId
						
				},
				null
			);
		
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
		
		getView().findViewById( R.id.NavigationArtistsAll ).setOnClickListener( this );
		getView().findViewById( R.id.NavigationAlbumsAll ).setOnClickListener( this );
		getView().findViewById( R.id.NavigationGenresAll ).setOnClickListener( this );
		getView().findViewById( R.id.NavigationPlaylistsAll ).setOnClickListener( this );
		getView().findViewById( R.id.NavigationSearch ).setOnClickListener( this );
		getView().findViewById( R.id.NavigationStarred ).setOnClickListener( this );
		getView().findViewById( R.id.NavigationSongsAll ).setOnClickListener( this );
        
		
		View settingsButton = getView().findViewById( R.id.SettingsButton );
		if ( null != settingsButton ) {
			
			settingsButton.setOnClickListener( this );
			
			ImageView settingsIcon = ( ImageView ) getView().findViewById( R.id.SettingsIcon );
			settingsIcon.getDrawable().mutate().setColorFilter( 0xFF999999, Mode.MULTIPLY );
			
		}
		
		
		
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mediaStoreChanged );
		
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Playlists.Members.getContentUri( "external",
						Long.parseLong( mPlaylistManager.createStarredIfNotExist() ) ), true, mediaStoreChanged );
		
		updateBadges();
		
		 
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
			
	    	mNewFragment = new SongsFragment();
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
					
					//adapter.notifyDataSetChanged();
					updateBadges();
					
				}
            	
            });
            
            super.onChange( selfChange );
            
        }

	};
	
    public void updateBadges() {
    	
    	new QueryCountTask( mBadgeSongsAll ).execute( mSongsAllQuery );
    	new QueryCountTask( mBadgeArtistsAll ).execute( mArtistsAllQuery );
    	new QueryCountTask( mBadgeAlbumsAll ).execute( mAlbumsAllQuery );
    	new QueryCountTask( mBadgePlaylistsAll ).execute( mPlaylistsAllQuery );
    	new QueryCountTask( mBadgeSongsAll ).execute( mSongsAllQuery );
    	new QueryCountTask( mBadgeGenresAll ).execute( mGenresAllQuery );
    	new QueryCountTask( mBadgeStarredCount ).execute( mStarredCountQuery );
    	
    }
    
}
