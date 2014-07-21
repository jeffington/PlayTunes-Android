package com.ideabag.playtunes.fragment;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.activity.NowPlayingActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FooterControlsFragment extends Fragment {
	
	private MainActivity mActivity;
	
	private String lastAlbumUri;
	private String current_media_id;
	
	private ImageButton mPlayPauseButton;
	private boolean isPlaying = false;
	
	private boolean isShowing = false;
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		//getView().setOnClickListener( controlsClickListener );
		
		getView().findViewById( R.id.FooterControls ).setOnClickListener( controlsClickListener );
    	
		
		mPlayPauseButton = ( ImageButton ) getView().findViewById( R.id.FooterControlsPlayPauseButton );
		mPlayPauseButton.setOnClickListener( controlsClickListener );
		
		
		//mActivity.BoundService.setOnSongInfoChangedListener( MusicStateChanged );
		
	}
	
	@Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		
		return inflater.inflate( R.layout.fragment_footer_controls, container, false );
		
	}
	
	@Override public void onResume() {
		super.onResume();
		
		
	}
		
	@Override public void onPause() {
		super.onPause();
		
		
	}
	

	
	
	View.OnClickListener controlsClickListener = new View.OnClickListener() {
		
		@Override public void onClick( View v ) {
			
			int id = v.getId();
			
			
			if ( id == R.id.FooterControls ) {
				
				Intent startNowPlayingActivity = new Intent( getActivity(), NowPlayingActivity.class );
				
				getActivity().startActivityForResult( startNowPlayingActivity, 0 );
				
			} else if ( id == R.id.FooterControlsPlayPauseButton ) {
				
				if ( isPlaying ) {
					
					mActivity.mBoundService.pause();
					
				} else {
					
					mActivity.mBoundService.play();
					
				}
				
			}
			
			
		}
		
	};
	
	public void setMediaID( String media_id ) {
		
		if ( null == media_id ) {
	    		
	    		if ( isShowing ) {
		    		
	    			// Hide the fragment
		    		//AnimationSet mSlideDown = ( AnimationSet ) AnimationUtils.loadAnimation( getActivity(), R.anim.slide_down );
		    		
		    		//mSlideDown.setFillAfter( true );
		    		
		    		//getView().setAnimation(  );
		    		//getView().startAnimation( mSlideDown );
		    		isShowing = false;
	    			FragmentManager fm = getActivity().getSupportFragmentManager();
	    			fm.beginTransaction()
	    			          .setCustomAnimations( R.anim.slide_up, R.anim.slide_down )
	    			          .hide( this )
	    			          .commit();
	    			
	    		} else {
	    			
	    			FragmentManager fm = getActivity().getSupportFragmentManager();
	    			fm.beginTransaction()
	    			          //.setCustomAnimations( R.anim.slide_up, R.anim.slide_down )
	    			          .hide( this )
	    			          .commit();
	    			
	    			//getView().setVisibility( View.GONE );
	    			
	    		}
	    		
	    	} else if ( media_id != this.current_media_id ) {
	    		
	    		if ( !isShowing ) {
	    			
	    			FragmentManager fm = getActivity().getSupportFragmentManager();
	    			fm.beginTransaction()
	    			          .setCustomAnimations( R.anim.slide_up, R.anim.slide_down )
	    			          .show( this )
	    			          .commit();
		    		
		    		isShowing = true;
	    			
	    		}
	    		
	    		this.current_media_id = media_id;
	    		
	    		Cursor mSongCursor = mActivity.getContentResolver().query(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						new String[] {
							
							MediaStore.Audio.Media.ALBUM,
							MediaStore.Audio.Media.ARTIST,
							MediaStore.Audio.Media.TITLE,
							MediaStore.Audio.Media.ALBUM_ID,
							MediaStore.Audio.Media.DURATION,
							MediaStore.Audio.Media._ID
							
						},
						MediaStore.Audio.Media._ID + "=?",
						new String[] {
							
								media_id
							
						},
						null
					);
				
	    		
				mSongCursor.moveToFirst();
				
				String title = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
				String artist = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
				
				String album_id = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM_ID ) );
				
				
				
				Cursor albumCursor = mActivity.getContentResolver().query(
						MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
					    new String[] {
					    	
					    	MediaStore.Audio.Albums.ALBUM_ART,
					    	MediaStore.Audio.Albums._ID
					    	
					    },
					    MediaStore.Audio.Albums._ID + "=?",
						new String[] {
							
							album_id
							
						},
						null
					);
				
				albumCursor.moveToFirst();
				
				String newAlbumUri = albumCursor.getString( albumCursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
				
				ImageView mAlbumCover = ( ImageView ) getView().findViewById( R.id.FooterControlsAlbumArt );
				
				if ( !newAlbumUri.equals( lastAlbumUri ) ) {
					
					//lastAlbumUri = newAlbumUri;
					
					Uri albumArtUri = Uri.parse( newAlbumUri );
					
					
					
					if ( null != lastAlbumUri ) {
						
						BitmapDrawable bd = ( BitmapDrawable ) mAlbumCover.getDrawable();
						
						if ( null != bd ) {
							
							bd.getBitmap().recycle();
							mAlbumCover.setImageBitmap( null );
							
						}
						
					}
					
					mAlbumCover.setImageURI( albumArtUri );
					
					lastAlbumUri = newAlbumUri;
				}
				
				albumCursor.close();
				mSongCursor.close();
				
				( ( TextView ) getView().findViewById( R.id.FooterControlsSongTitle ) ).setText( title );
				( ( TextView ) getView().findViewById( R.id.FooterControlsArtistName ) ).setText( artist );
				
				
	    	}
	    	
	    }
	   
	   
	   public void showPaused() {
		   
		   this.isPlaying = false;
		   
		   mPlayPauseButton.setImageResource( R.drawable.ic_action_playback_play_white );
		   
		   ( ( TextView ) getView().findViewById( R.id.FooterControlsSongTitle ) ).setEllipsize( null );
		   
	   }
	   
	   public void showPlaying() {
		   
		   this.isPlaying = true;
		   
		   mPlayPauseButton.setImageResource( R.drawable.ic_action_playback_pause_white );
		   
		   ( ( TextView ) getView().findViewById( R.id.FooterControlsSongTitle ) ).setEllipsize( TextUtils.TruncateAt.MARQUEE );
		   
	   }
	
}
