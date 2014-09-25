package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.ArtistAlbumsAdapter;
import com.ideabag.playtunes.adapter.ArtistAllSongsAdapter;
import com.ideabag.playtunes.dialog.SongMenuDialogFragment;
import com.ideabag.playtunes.util.GAEvent.Categories;
import com.ideabag.playtunes.util.GAEvent.Playlist;
import com.ideabag.playtunes.util.IMusicBrowser;
import com.ideabag.playtunes.util.MergeAdapter;
import com.ideabag.playtunes.util.TrackerSingleton;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ArtistsOneFragment extends SaveScrollListFragment implements IMusicBrowser {
	
	public static final String TAG = "One Artist Fragment";
	
	private ViewGroup AllSongs;
	private ViewGroup Singles = null;
	
	//private TextView albumDivider;
	
	//ArtistAlbumsAdapter adapter;
	ArtistAlbumsAdapter mAlbumsAdapter;
	ArtistAllSongsAdapter mSongsAdapter;
	
	MergeAdapter adapter;
	
	private MainActivity mActivity;
	private Tracker mTracker;
    
	private String ARTIST_ID = "";
	
	@Override public void setMediaID( String media_id ) {
		
		ARTIST_ID = media_id;
		
	}
	
	@Override public String getMediaID() { return ARTIST_ID; }
	
	@Override public void onAttach( Activity activity ) {
			
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		mTracker = TrackerSingleton.getDefaultTracker( mActivity );
		
	}
	
	@Override public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		outState.putString( getString( R.string.key_state_media_id ), ARTIST_ID );
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		adapter = new MergeAdapter();
		
		if ( null != savedInstanceState ) {
			
			ARTIST_ID = savedInstanceState.getString( getString( R.string.key_state_media_id ) );
			
		}
		
		LayoutInflater inflater = mActivity.getLayoutInflater();
		
		mAlbumsAdapter = new ArtistAlbumsAdapter( getActivity(), ARTIST_ID );
		
    	adapter.addAdapter( mAlbumsAdapter );
    	
    	
    	Cursor songCountCursor = getActivity().getContentResolver().query(
    				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    				new String[] {
    					
    					MediaStore.Audio.Media.ARTIST_ID,
    					MediaStore.Audio.Media._ID
    					
    				},
    				MediaStore.Audio.Media.ARTIST_ID + "=?",
    				new String[] {
    					
    					ARTIST_ID
    					
    				},
    				null
    			);
    	
    	Cursor singlesCountCursor = getActivity().getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] {
					
					MediaStore.Audio.Media.ARTIST_ID,
					MediaStore.Audio.Media._ID
					
				},
				MediaStore.Audio.Media.ARTIST_ID + "=? AND " + MediaStore.Audio.Media.ALBUM + "=?",
				new String[] {
					
					ARTIST_ID,
					getString( R.string.no_album_string )
					
				},
				null
			);
		
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
		
    	//getListView().addHeaderView( mActivity.AdContainer, null, true );
    	
    	int songCount = songCountCursor.getCount();
    	songCountCursor.close();
    	
    	int singlesCount = singlesCountCursor.getCount();
    	singlesCountCursor.close();
    	
    	/*
    	AllSongs = ( ViewGroup ) inflater.inflate( R.layout.list_item_title_one_badge, null );
    	( ( TextView ) AllSongs.findViewById( R.id.SongCount ) ).setText( "" + songCount );
    	( ( TextView ) AllSongs.findViewById( R.id.Title ) ).setText( getString( R.string.artist_all_songs ) );
    	getListView().addHeaderView( AllSongs, null, true );
    	*/
    	if ( singlesCount > 0) {
    		
    		Singles = ( ViewGroup ) inflater.inflate( R.layout.list_item_navigation_title, null );
    		( ( TextView ) Singles.findViewById( R.id.BadgeCount ) ).setText( "" + singlesCount );
    		( ( TextView ) Singles.findViewById( R.id.Title ) ).setText( getString( R.string.artist_singles ) );
    		//getListView().addHeaderView( Singles, null, true );
    		adapter.addView( Singles );
    		
    	}
    	/*
    	albumDivider = ( TextView ) inflater.inflate( R.layout.list_header_albums, null );
    	
    	
    	getListView().addHeaderView( albumDivider, null, false );
    	*/
    	mSongsAdapter = new ArtistAllSongsAdapter( getActivity(), ARTIST_ID, songMenuClickListener );
    	adapter.addAdapter( mSongsAdapter );
    	//getListView().setHeaderDividersEnabled( true );
		getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setOnItemLongClickListener( mSongMenuLongClickListener );
    	
    	setListAdapter( adapter );
    	
    	
    	
	}
	
	@Override public void onResume() {
		super.onResume();
		
    	// TODO:
    	mActivity.setActionbarTitle( mSongsAdapter.ARTIST_NAME );
    	mActivity.setActionbarSubtitle( getString( R.string.artist_singular ) );

	        // Set screen name.
	        // Where path is a String representing the screen name.
    	mTracker.setScreenName( TAG );
		//t.set( "_count", ""+adapter.getCount() );
		
	        // Send a screen view.
    	mTracker.send( new HitBuilders.AppViewBuilder().build() );
		
    	mTracker.send( new HitBuilders.EventBuilder()
    	.setCategory( Categories.PLAYLIST )
    	.setAction( Playlist.ACTION_SHOWLIST )
    	.setValue( adapter.getCount() )
    	.build());
    	
	}
	
	@Override public void onPause() {
		super.onPause();
		
		
		
	}
	
	@Override public void onDestroyView() {
	    super.onDestroyView();
	    
	    setListAdapter( null );
	    
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		int hasSingles = 0;
		/*
		int hasSingles = ( null == Singles ? 1 : 0 );
		
		if ( hasSingles > 0 && v.equals( Singles ) ) { // Load Singles
			
			ArtistSinglesFragment allSinglesFragment = new ArtistSinglesFragment( );
			allSinglesFragment.setMediaID( ARTIST_ID );
			
			mActivity.transactFragment( allSinglesFragment );
			
		}
		*/
		if ( position < mAlbumsAdapter.getCount() + hasSingles ) {
		
			
			String albumID = ( String ) v.getTag( R.id.tag_album_id );
			
			AlbumsOneFragment albumFragment = new AlbumsOneFragment( );
			albumFragment.setMediaID( albumID );
			
			mActivity.transactFragment( albumFragment );
			
	    	mTracker.send( new HitBuilders.EventBuilder()
	    	.setCategory( Categories.PLAYLIST )
	    	.setAction( Playlist.ACTION_CLICK )
	    	.setValue( position )
	    	.setLabel( "album" )
	    	.build());
			
		} else if ( position >= mAlbumsAdapter.getCount() + hasSingles ) {
			
			String playlistName = mActivity.getSupportActionBar().getTitle().toString();
			
			mActivity.mBoundService.setPlaylist( mSongsAdapter.getQuery(), playlistName, ArtistsOneFragment.class, ARTIST_ID );
			//mActivity.mBoundService.setPlaylistCursor( adapter.getCursor() );
			
			mActivity.mBoundService.setPlaylistPosition( position - ( mAlbumsAdapter.getCount() + hasSingles ) );
			
			mActivity.mBoundService.play();
			
	    	mTracker.send( new HitBuilders.EventBuilder()
	    	.setCategory( Categories.PLAYLIST )
	    	.setAction( Playlist.ACTION_CLICK )
	    	.setValue( position - mAlbumsAdapter.getCount() )
	    	.setLabel( "song" )
	    	.build());
			
		}
		

		
	}
	
	protected AdapterView.OnItemLongClickListener mSongMenuLongClickListener = new AdapterView.OnItemLongClickListener() {
		
		@Override public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long id) {
			
			if ( position >= mAlbumsAdapter.getCount() ) {
				
				int absPosition = position - mAlbumsAdapter.getCount();
				
				showSongMenuDialog( "" + mSongsAdapter.getItemId( absPosition ) );
				
		    	mTracker.send( new HitBuilders.EventBuilder()
		    	.setCategory( Categories.PLAYLIST )
		    	.setAction( Playlist.ACTION_LONGCLICK )
		    	.setValue( position - mAlbumsAdapter.getCount() )
		    	.build());
				
			}
			
			return true;
			
		}
		
	};
	
	protected View.OnClickListener songMenuClickListener = new View.OnClickListener() {
		
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
				
				showSongMenuDialog( songID );
				
			}
			
			
			
		}
		
	};
	
	protected void showSongMenuDialog( String songID ) {
		
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
    	
		SongMenuDialogFragment newFragment = new SongMenuDialogFragment();
		newFragment.setMediaID( songID );
    	
        newFragment.show( ft, "dialog" );
		
	}
	
}