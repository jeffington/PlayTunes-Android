package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.activity.NowPlayingActivity;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.media.PlaylistMediaPlayer.PlaybackListener;
import com.ideabag.playtunes.util.GAEvent.AudioControls;
import com.ideabag.playtunes.util.GAEvent.Categories;
import com.ideabag.playtunes.util.GAEvent.FooterControls;
import com.ideabag.playtunes.util.GAEvent.Playlist;
import com.ideabag.playtunes.util.AsyncDrawable;
import com.ideabag.playtunes.util.BitmapWorkerTask;
import com.ideabag.playtunes.util.TrackerSingleton;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
	private Tracker mTracker;
	
	private String lastAlbumUri = null;
	private String current_media_id;
	private boolean isPlaying = false;
	private boolean isShowing = false;
	
	//
	// Views from fragment_footer_controls.xml
	//
	private ImageButton mPlayPauseButton;
	private ImageView mAlbumCover;
	private TextView mTitle, mArtist;
	
	private Fragment mFragmentSelf;
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		mTracker = TrackerSingleton.getDefaultTracker( mActivity );
		mFragmentSelf = this;
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		//getView().setOnClickListener( controlsClickListener );
		
		getView().findViewById( R.id.FooterControls ).setOnClickListener( controlsClickListener );
    	
		
		mPlayPauseButton = ( ImageButton ) getView().findViewById( R.id.FooterControlsPlayPauseButton );
		mPlayPauseButton.setOnClickListener( controlsClickListener );
		
		mTitle = ( TextView ) getView().findViewById( R.id.FooterControlsSongTitle );
		mArtist = ( TextView ) getView().findViewById( R.id.FooterControlsArtistName );
		mAlbumCover = ( ImageView ) getView().findViewById( R.id.FooterControlsAlbumArt );
		 
		View mNextButton = getView().findViewById( R.id.FooterControlsNextButton );
		
		if ( null != mNextButton ) {
			
			mNextButton.setOnClickListener( controlsClickListener );
			
		}
		
		//mActivity.BoundService.setOnSongInfoChangedListener( MusicStateChanged );
		
	}
	
	@Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		
		return inflater.inflate( R.layout.fragment_footer_controls, container, false );
		
	}
	
	@Override public void onDestroyView() {
		super.onDestroyView();
		
		recycleAlbumArt();
		
	}
	
	View.OnClickListener controlsClickListener = new View.OnClickListener() {
		
		@Override public void onClick( View v ) {
			
			int id = v.getId();
			
			
			if ( id == R.id.FooterControls ) {
				
				showNowPlayingActivity();
				
				mTracker.send( new HitBuilders.EventBuilder()
		    	.setCategory( Categories.FOOTER_CONTROLS )
		    	.setAction( Playlist.ACTION_CLICK )
		    	.build());
				
			} else if ( id == R.id.FooterControlsPlayPauseButton ) {
				
				if ( isPlaying ) {
					
					mActivity.mBoundService.pause();
					
					mTracker.send( new HitBuilders.EventBuilder()
			    	.setCategory( Categories.FOOTER_CONTROLS )
			    	.setAction( AudioControls.ACTION_PAUSE )
			    	.build());
					
				} else {
					
					mActivity.mBoundService.play();
					
					mTracker.send( new HitBuilders.EventBuilder()
					.setCategory( Categories.FOOTER_CONTROLS )
			    	.setAction( AudioControls.ACTION_PLAY )
			    	.build());
					
				}
				
			} else if ( id == R.id.FooterControlsNextButton ) {
				
				mActivity.mBoundService.next();
				
				mTracker.send( new HitBuilders.EventBuilder()
				.setCategory( Categories.FOOTER_CONTROLS )
		    	.setAction( AudioControls.ACTION_NEXT )
		    	.build());
				
			}
			
			
		}
		
	};
	
	   
	   
   private void recycleAlbumArt() {
	   
	   BitmapDrawable bd = ( BitmapDrawable ) mAlbumCover.getDrawable();
		
		if ( null != bd && null != bd.getBitmap() ) {
			
			bd.getBitmap().recycle();
			mAlbumCover.setImageBitmap( null );
			
		}
	   
   }
	
   public PlaybackListener PlaybackListener = new PlaybackListener() {

		@Override public void onTrackChanged(String media_id) {
			
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
	    			          .hide( mFragmentSelf )
	    			          .commitAllowingStateLoss();
	    			
	    		} else {
	    			
	    			FragmentManager fm = getActivity().getSupportFragmentManager();
	    			fm.beginTransaction()
	    			          //.setCustomAnimations( R.anim.slide_up, R.anim.slide_down )
	    			          .hide( mFragmentSelf )
	    			          .commitAllowingStateLoss();
	    			
	    			//getView().setVisibility( View.GONE );
	    			
	    		}
	    		
	    	} else if ( media_id != current_media_id ) {
	    		
	    		current_media_id = media_id;
	    		
	    		MediaQuery mSongQuery = new MediaQuery(
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
				
	    		MediaQuery.executeAsync( mActivity, mSongQuery, new MediaQuery.OnQueryCompletedListener() {
					
					@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
						

						mResult.moveToFirst();
						
						String title = mResult.getString( mResult.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
						String artist = mResult.getString( mResult.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
						
						String album_id = mResult.getString( mResult.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM_ID ) );
						mResult.close();
						
						// 
						// This tests if we loaded previous album art and that it wasn't null
						// If the nextAlbumUri is null, it means there's no album art and 
						// we load from an image resource.
						// 
						
						
						/*
						if ( null == nextAlbumUri && null != lastAlbumUri) {
							
							recycleAlbumArt();
							
						} else if ( null != nextAlbumUri && null != lastAlbumUri && !lastAlbumUri.equals( nextAlbumUri ) ) {
							
							recycleAlbumArt();
							
						}

						if ( null == nextAlbumUri ) {
							
							mAlbumCover.setImageResource( R.drawable.no_album_art_thumb );
							
						    
						} else {
							
							Uri albumArtUri = Uri.parse( nextAlbumUri );
							
							mAlbumCover.setImageURI( albumArtUri );
							
							lastAlbumUri = nextAlbumUri;
							
						}
						
						// Otherwise, nextAlbumUri and lastAlbumUri are the same, we leave the ImageView alone
						// and don't recycle the backing bitmap;
						
						lastAlbumUri = nextAlbumUri;
						
						albumCursor.close();
						mSongCursor.close();
						*/
						
						MediaQuery albumQuery = new MediaQuery(
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
						
						MediaQuery.executeAsync(getActivity(), albumQuery, new MediaQuery.OnQueryCompletedListener() {
							
							@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
								
								mResult.moveToFirst();
								
								String nextAlbumUri = mResult.getString( mResult.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
								
								if ( null != nextAlbumUri) {
									
									if ( !nextAlbumUri.equals( lastAlbumUri ) ) {
										
										lastAlbumUri = nextAlbumUri;
										
										final BitmapWorkerTask albumThumbTask = new BitmapWorkerTask( mAlbumCover, getResources().getDimensionPixelSize( R.dimen.footer_height ) );
										
										final AsyncDrawable asyncThumbDrawable =
								                new AsyncDrawable( getResources(),
								                		null, // BitmapFactory.decodeResource( mContext.getResources(), R.drawable.no_album_art_thumb )
								                		albumThumbTask );
								        
										mAlbumCover.setImageDrawable( asyncThumbDrawable );
								        albumThumbTask.execute( nextAlbumUri );
								        
									}
							        
								} else {
									
									mAlbumCover.setImageResource( R.drawable.no_album_art_thumb );
									
								}
								
							}
							
						});
				        
						
						mTitle.setText( title );
						mArtist.setText( artist );
						
						// Show the footer controls
						
			    		if ( !isShowing ) {
			    			
			    			FragmentManager fm = getActivity().getSupportFragmentManager();
			    			fm.beginTransaction()
			    			          .setCustomAnimations( R.anim.slide_up, R.anim.slide_down )
			    			          .show( mFragmentSelf )
			    			          .commit();
				    		
			    			//getActivity().findViewById( R.id.FooterShadow ).setVisibility( View.VISIBLE );
			    			
				    		isShowing = true;
			    			
			    		}
						
					}
					
				});
	    		
				
	    	}
			
		}

		@Override public void onPlay(int playbackPositionMilliseconds) {
			   
			isPlaying = true;
			   
			mPlayPauseButton.setImageResource( R.drawable.ic_action_playback_pause_white );
			mTitle.setSelected( true );
		    mTitle.setSingleLine( true );
			mTitle.setEllipsize( TextUtils.TruncateAt.MARQUEE );
			   
		}

		@Override public void onPause(int playbackPositionMilliseconds) {
			
			   isPlaying = false;
			   
			   mPlayPauseButton.setImageResource( R.drawable.ic_action_playback_play_white );
			   
			   mTitle.setEllipsize( TextUtils.TruncateAt.END );
			
		}

		@Override public void onPlaylistDone() {
			// TODO Auto-generated method stub
			
		}

		@Override public void onLoopingChanged(int loop) {
			// TODO Auto-generated method stub
			
		}

		@Override public void onShuffleChanged(boolean isShuffling) {
			// TODO Auto-generated method stub
			
		}
		
	   };
	   
		private void showNowPlayingActivity() {
			
			Intent startNowPlayingActivity = new Intent( getActivity(), NowPlayingActivity.class );
			if ( current_media_id != null ) {
				startNowPlayingActivity.putExtra("album_art_uri", lastAlbumUri );
			}
			startActivityForResult( startNowPlayingActivity, 0 );
			
		}
	   
}
