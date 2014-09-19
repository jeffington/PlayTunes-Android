package com.ideabag.playtunes.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.AlbumsOneAdapter;
import com.ideabag.playtunes.dialog.SongMenuDialogFragment;
import com.ideabag.playtunes.util.IMusicBrowser;
import com.ideabag.playtunes.util.IPlayableList;
import com.ideabag.playtunes.util.TrackerSingleton;

public class AlbumsOneFragment extends SaveScrollListFragment implements IMusicBrowser, IPlayableList {
	
	public static final String TAG = "One Album Fragment";
	
	AlbumsOneAdapter adapter;
	private MainActivity mActivity;
	
	private String ALBUM_ID = "";
	
	private View albumArtHeader;
	private ImageView mAlbumArt;
	private ImageView mAlbumArtBackground;
	
	@Override public void setMediaID( String media_id ) {
		
		ALBUM_ID = media_id;
		
	}
	
	@Override public String getMediaID() { return ALBUM_ID; }
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}

	@Override public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		outState.putString( getString( R.string.key_state_media_id ), ALBUM_ID );
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
    	
		if ( null != savedInstanceState ) {
			
			ALBUM_ID = savedInstanceState.getString( getString( R.string.key_state_media_id ) );
			
		}
		
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
		getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
		
		adapter = new AlbumsOneAdapter( getActivity(), ALBUM_ID, songMenuClickListener );
		// TODO: A start at showing an indicator next to the song that's playing in the list.
		//adapter.setNowPlayingMedia( mActivity.mBoundService.CURRENT_MEDIA_ID );
		
		//if ( null != adapter.albumArtUri ) {
			
			int headerHeightPx = ( int ) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 196, getResources().getDisplayMetrics() );
			albumArtHeader = getActivity().getLayoutInflater().inflate( R.layout.list_header_albumart, null, false );
			albumArtHeader.setLayoutParams( new AbsListView.LayoutParams( AbsListView.LayoutParams.MATCH_PARENT, headerHeightPx ) );
			
			mAlbumArt = ( ImageView ) albumArtHeader.findViewById( R.id.AlbumArtFull );
			
			mAlbumArtBackground = ( ImageView ) albumArtHeader.findViewById( R.id.AlbumArtBackground );
			
			TextView mAlbumTitle = ( TextView ) albumArtHeader.findViewById( R.id.AlbumArtTitle );
			TextView mAlbumSubtitle = ( TextView ) albumArtHeader.findViewById( R.id.AlbumArtSubtitle );
			
			mAlbumTitle.setText( adapter.albumTitle );
			mAlbumSubtitle.setText( adapter.albumArtist );
			
			Bitmap albumArtBitmap;
			
			if ( null != adapter.albumArtUri ) {
				
				albumArtBitmap = BitmapFactory.decodeFile( adapter.albumArtUri );
				
			} else {
				
				albumArtBitmap = BitmapFactory.decodeResource( getResources(), R.drawable.no_album_art_full );
				
			}
			Bitmap newAlbumArt = Bitmap.createScaledBitmap( albumArtBitmap, albumArtBitmap.getWidth() * 4, albumArtBitmap.getHeight() * 4, true );
			
			
			getListView().addHeaderView( albumArtHeader, null, false );
			
			mAlbumArtBackground.setImageBitmap( newAlbumArt );
			//albumArtHeader.setBackground( new BitmapDrawable( getResources(), newAlbumArt ) );
			//albumArtHeader.setBackgroundDrawable( new BitmapDrawable( getResources(), newAlbumArt ) );
			
			mAlbumArtBackground.setColorFilter( getResources().getColor( R.color.now_playing_background ), PorterDuff.Mode.MULTIPLY );
			
			mAlbumArt.setImageBitmap( albumArtBitmap );
			//iv.setImageURI( Uri.parse( adapter.albumArtUri ) );
			
		//}
		
    	setListAdapter( adapter );
		
	}
	
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		String playlistName = mActivity.getSupportActionBar().getTitle().toString();
		
		mActivity.mBoundService.setPlaylist( adapter.getQuery(), playlistName, AlbumsOneFragment.class, ALBUM_ID );
		mActivity.mBoundService.setPlaylistPosition( position - l.getHeaderViewsCount() );
		
		mActivity.mBoundService.play();
		
		// Set the title of the playlist
		
		// mPlaylistMediaID = ALBUM_ID
		// mPlaylistName mActivity.getSupportActionBar().getTitle().toString()
		// mPlaylistFragmentClass = AlbumsOneFragment.class
		
	}
	
	@Override public void onResume() {
		super.onResume();
		
		if ( null != adapter.albumTitle ) {
			
			mActivity.setActionbarTitle( adapter.albumTitle );
			mActivity.setActionbarSubtitle( adapter.getCount() + " " + ( adapter.getCount() == 1 ? getString( R.string.song_singular ) : getString( R.string.songs_plural ) ) );
			
		}
		
		Tracker t = TrackerSingleton.getDefaultTracker( mActivity );
		
	        // Set screen name.
	        // Where path is a String representing the screen name.
		t.setScreenName( TAG );
		//t.set( "_count", ""+adapter.getCount() );
		
	        // Send a screen view.
		t.send( new HitBuilders.AppViewBuilder().build() );
		
		t.send( new HitBuilders.EventBuilder()
    	.setCategory( "playlist" )
    	.setAction( "show" )
    	.setLabel( TAG )
    	.setValue( adapter.getCount() )
    	.build());
		
	}
	
	@Override public void onPause() {
		super.onPause();
		
	}
	
	@Override public void onDestroyView() {
	    super.onDestroyView();
	    
	    if ( null != mAlbumArt ) {
		    
		    BitmapDrawable mAlbumArtDrawable = ( BitmapDrawable ) mAlbumArt.getDrawable();
			
			if ( null != mAlbumArtDrawable ) {
				
				mAlbumArtDrawable.getBitmap().recycle();
				mAlbumArt.setImageBitmap( null );
				
			}
			
	    }
	    
	    if ( null != mAlbumArtBackground ) {
		    
		    BitmapDrawable mAlbumArtBackgroundDrawable = ( BitmapDrawable ) mAlbumArtBackground.getDrawable();
			
			if ( null != mAlbumArtBackgroundDrawable ) {
				
				mAlbumArtBackgroundDrawable.getBitmap().recycle();
				mAlbumArtBackground.setImageBitmap( null );
				
			}
			
	    }
	    
	    setListAdapter( null );
	    
	}
	
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

	@Override public void onNowPlayingMediaChanged( String media_id ) {
		
		adapter.setNowPlayingMedia( media_id );
		
	}
	
}