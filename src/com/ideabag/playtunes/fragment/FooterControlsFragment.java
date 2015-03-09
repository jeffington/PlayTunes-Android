package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.dialog.SongMenuDialogFragment;
import com.ideabag.playtunes.media.PlaylistMediaPlayer;
import com.ideabag.playtunes.media.PlaylistMediaPlayer.PlaybackListener;
import com.ideabag.playtunes.slidinguppanel.SlidingUpPanelLayout;
import com.ideabag.playtunes.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.ideabag.playtunes.slidinguppanel.SlidingUpPanelLayout.PanelState;
import com.ideabag.playtunes.util.GAEvent.AudioControls;
import com.ideabag.playtunes.util.GAEvent.Categories;
import com.ideabag.playtunes.util.AsyncDrawable;
import com.ideabag.playtunes.util.BitmapWorkerTask;
import com.ideabag.playtunes.util.StarToggleTask;
import com.ideabag.playtunes.util.TrackerSingleton;
import com.ideabag.playtunes.widget.StarButton;
import com.ideabag.playtunes.widget.TimeProgressBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class FooterControlsFragment extends Fragment {
	
	public static final String TAG = "FooterControlsFragment";
	
	private MainActivity mActivity;
	private Tracker mTracker;
	
	private String lastAlbumUri = null;
	private String current_media_id;
	private boolean isPlaying = false;
	private boolean isShowing = false;
	
	SlidingUpPanelLayout mSlidingPanel;
	
	//
	// Views from fragment_footer_controls_expanded.xml
	//
	
	// 
	// 
	// 
	private ImageButton mCompactPlayPauseButton;
	private ImageButton mCompactNextButton;
	private ImageButton mCompactPrevButton;
	private ImageView mCompactAlbumCover;
	
	
	// 
	// Compact controls: album thumbnail, song title, song artist, mini progress bar
	// This also includes the star button and song menu button for the expanded view
	// 
	private RelativeLayout mCompactAlbumArtContainer;
	private StarButton mStarButton;
	private TextView mTitle, mArtist;
	private LinearLayout mCompactPlaybackControls;
	private ImageButton mSongMenuButton;
	
	//
	// Expanded control views: repeat, back, play/pause, next, and shuffle
	//
	private ImageButton mExpControlsRepeatButton;
	private ImageButton mExpControlsShuffleButton;
	private ImageButton mExpControlsPrevButton;
	private ImageButton mExpControlsNextButton;
	private ImageButton mExpControlsPlayPauseButton;
	
	
	// 
	// 
	// 
	private ImageView mFullAlbumArt;
	RelativeLayout mFullAlbumArtContainer;
	ImageView mAlbumArtBackground;
	
	private TrackProgressFragment mProgressFragment;
	private TimeProgressBar mCompactProgressBar;
	
	private Fragment mFragmentSelf;
	private PlaylistManager mPlaylistManager;
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		mTracker = TrackerSingleton.getDefaultTracker( mActivity );
		mFragmentSelf = this;
		mPlaylistManager = new PlaylistManager( activity );
		
	}
	
	@SuppressLint("NewApi")
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		mSlidingPanel = ( SlidingUpPanelLayout ) getActivity().findViewById( R.id.sliding_layout );
		
		mSlidingPanel.setPanelSlideListener( new PanelSlideListener() {
			
			float mPreviousOffset = 0f;
			// offset of 1.0 = top, offset of 0 = bottom
			@Override public void onPanelSlide(View panel, float slideOffset) {
				
				if ( slideOffset > mPreviousOffset && slideOffset > 0.5f ) {
                	
            		showFull();
                	
               } else if ( mPreviousOffset > slideOffset && slideOffset < 0.5f ) {
            	   
            	   showCollapsed();
            	   
               }
                
               mPreviousOffset = slideOffset;
               
			}

			@Override public void onPanelCollapsed( View panel ) {
				
				showCollapsed();
				
			}

			@Override public void onPanelExpanded( View panel ) {
				
				showFull();
				
			}

			@Override public void onPanelAnchored( View panel ) { }
			@Override public void onPanelHidden( View panel ) { }
			
		});
		//getView().findViewById( R.id.FooterControls ).setOnClickListener( controlsClickListener );
		// 
		// Compact media controls
		// 
		mTitle = ( TextView ) getView().findViewById( R.id.ControlsTitle );
		mArtist = ( TextView ) getView().findViewById( R.id.ControlsSubtitle );
		
		mCompactAlbumCover = ( ImageView ) getView().findViewById( R.id.CompactAlbumArt );
		mStarButton = ( StarButton ) getView().findViewById( R.id.StarButton );
		mSongMenuButton = ( ImageButton ) getView().findViewById( R.id.MenuButton );
		mCompactProgressBar = (TimeProgressBar) getView().findViewById( R.id.CompactProgressBar );
		mCompactPlaybackControls = ( LinearLayout ) getView().findViewById( R.id.CompactPlaybackControls );
		mCompactAlbumArtContainer = ( RelativeLayout ) getView().findViewById( R.id.CompactAlbumArtContainer );
		
		mCompactPlayPauseButton = ( ImageButton ) getView().findViewById( R.id.CompactPlayPauseButton );
		// Previous and Next may be null, they only appear on larger screen devices
		mCompactNextButton = ( ImageButton ) getView().findViewById( R.id.CompactNextButton );
		mCompactPrevButton = ( ImageButton ) getView().findViewById( R.id.CompactPrevButton );
		
		if ( null != mCompactPrevButton ) {
			
			mCompactPrevButton.setOnClickListener( controlsClickListener );
			
		}
		if ( null != mCompactNextButton ) {
			
			mCompactNextButton.setOnClickListener( controlsClickListener );
			
		}
		
		mSongMenuButton.setOnClickListener( controlsClickListener );
		mStarButton.setOnClickListener( controlsClickListener );
		mCompactPlayPauseButton.setOnClickListener( controlsClickListener );
		
		// 
		// Expanded media controls
		// 
		mExpControlsRepeatButton = ( ImageButton ) getView().findViewById( R.id.NowPlayingRepeatButton );
		mExpControlsShuffleButton = ( ImageButton ) getView().findViewById( R.id.NowPlayingShuffleButton );
		mExpControlsPrevButton = ( ImageButton ) getView().findViewById( R.id.NowPlayingPrevButton );
		mExpControlsNextButton = ( ImageButton ) getView().findViewById( R.id.NowPlayingNextButton );
		mExpControlsPlayPauseButton = ( ImageButton ) getView().findViewById( R.id.NowPlayingPlayPauseButton );
		
		mExpControlsPrevButton.setOnClickListener( controlsClickListener );
		mExpControlsShuffleButton.setOnClickListener( controlsClickListener );
		mExpControlsRepeatButton.setOnClickListener( controlsClickListener );
		mExpControlsNextButton.setOnClickListener( controlsClickListener );
		mExpControlsPlayPauseButton.setOnClickListener( controlsClickListener );
		mExpControlsNextButton.setOnClickListener( controlsClickListener );
		
		//
		// Full controls, with large album art and SeekBar
		//
    	
		mFullAlbumArtContainer = (RelativeLayout) getView().findViewById( R.id.FullAlbumArtContainer );
		mFullAlbumArt = ( ImageView ) getView().findViewById( R.id.FullAlbumArt );
		mProgressFragment = ( TrackProgressFragment ) getChildFragmentManager().findFragmentById( R.id.TrackProgressFragment );
		mAlbumArtBackground = ( ImageView ) getView().findViewById( R.id.AlbumArtBackground );
		mAlbumArtBackground.setColorFilter( getResources().getColor( R.color.textColorPrimary ), PorterDuff.Mode.MULTIPLY );
		
		mCompactPlaybackControls.setOnClickListener( controlsClickListener );
		
		//showCollapsed();
		
		ViewCompat.setElevation(mCompactPlaybackControls, 8 );
		
	}
	
	
	@Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		
		return inflater.inflate( R.layout.fragment_footer_controls_expanded, container, false );
		
	}
	
	@Override public void onResume() {
		super.onResume();
		
		PanelState state = mSlidingPanel.getPanelState();
		
		if ( state == PanelState.COLLAPSED ) {
			
			showCollapsed();
			
		} else if ( state == PanelState.EXPANDED ) {
			
			showFull();
			
		}
		
		mActivity.addPlaybackListener( PlaybackListener );
		
	}
	
	@Override public void onPause() {
		super.onPause();
		
	}
	
	@Override public void onDestroyView() {
		super.onDestroyView();
		
		//recycleAlbumArt();
		
	}
	
	View.OnClickListener controlsClickListener = new View.OnClickListener() {
		
		@Override public void onClick( View v ) {
			
			int id = v.getId();
			
			if ( id == R.id.CompactPlaybackControls ) {
				
				PanelState state = mSlidingPanel.getPanelState();
				
				if ( state == PanelState.COLLAPSED ) {
					
					mSlidingPanel.setPanelState( PanelState.EXPANDED );
					
				} else if ( state == PanelState.EXPANDED ) {
					
					mSlidingPanel.setPanelState( PanelState.COLLAPSED );
					
				}
				
			} else if ( id == R.id.NowPlayingPlayPauseButton || id == R.id.CompactPlayPauseButton ) {
				
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
				
			}  else if ( id == R.id.NowPlayingNextButton || id == R.id.CompactNextButton  ) {
				
				mTracker.send( new HitBuilders.EventBuilder()
	        	.setCategory( "now playing button" )
	        	.setAction( "click" )
	        	.setLabel( "next" )
	        	.build());
				
				mActivity.mBoundService.next();
				
			} else if ( id == R.id.NowPlayingPrevButton || id == R.id.CompactPrevButton ) {
				
				mTracker.send( new HitBuilders.EventBuilder()
	        	.setCategory( "now playing button" )
	        	.setAction( "click" )
	        	.setLabel( "previous" )
	        	.build());
				
				mActivity.mBoundService.prev();
				
			} else if ( id == R.id.StarButton ) {
				
				//current_media_id
				ToggleButton starButton = (ToggleButton) v;
				
				if ( starButton.isChecked() ) {
					
					mTracker.send( new HitBuilders.EventBuilder()
		        	.setCategory( "now playing button" )
		        	.setAction( "click" )
		        	.setLabel( "star" )
		        	.setValue( 1 )
		        	.build());
					
					mPlaylistManager.addFavorite( current_media_id );
					
				} else {
					
					mTracker.send( new HitBuilders.EventBuilder()
		        	.setCategory( "now playing button" )
		        	.setAction( "click" )
		        	.setLabel( "star" )
		        	.setValue( 0 )
		        	.build());
					
					mPlaylistManager.removeFavorite( current_media_id );
					
				}
				
			} else if ( id == R.id.MenuButton ) {
				
				mTracker.send( new HitBuilders.EventBuilder()
	        	.setCategory( "now playing button" )
	        	.setAction( "click" )
	        	.setLabel( "song menu" )
	        	.build());
				
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	        	
				Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
				
			    if ( prev != null ) {
			        
			    	ft.remove( prev );
			    	
			    }
				
				SongMenuDialogFragment newFragment = new SongMenuDialogFragment();
				newFragment.setMediaID( current_media_id );
	        	
	            newFragment.show( ft, "dialog" );
				
			}  else if ( id == R.id.NowPlayingRepeatButton ) {
				
				String repeatState = (String) v.getTag( R.id.tag_repeat_state );
				
				if ( null == repeatState || repeatState.equals( "0" ) ) {
					
					mActivity.mBoundService.setLooping( PlaylistMediaPlayer.LOOP_ALL );
					
					mTracker.send( new HitBuilders.EventBuilder()
		        	.setCategory( "now playing button" )
		        	.setAction( "click" )
		        	.setLabel( "repeat" )
		        	.setValue( 1 )
		        	.build());
					
				} else if ( repeatState.equals( "1" ) ) {
					
					mActivity.mBoundService.setLooping( PlaylistMediaPlayer.LOOP_ONE );
					
					mTracker.send( new HitBuilders.EventBuilder()
		        	.setCategory( "now playing button" )
		        	.setAction( "click" )
		        	.setLabel( "repeat" )
		        	.setValue( 2 )
		        	.build());
					
				} else {
					
					mActivity.mBoundService.setLooping( PlaylistMediaPlayer.LOOP_NO );
					
					mTracker.send( new HitBuilders.EventBuilder()
		        	.setCategory( "now playing button" )
		        	.setAction( "click" )
		        	.setLabel( "repeat" )
		        	.setValue( 0 )
		        	.build());
					
				}
				
			} else if ( id == R.id.NowPlayingShuffleButton ) {
				
				//ImageButton ib = ( ImageButton ) v;
				
				String shuffleState = (String) v.getTag( R.id.tag_shuffle_state );
				
				if ( null == shuffleState || shuffleState.equals( "0" ) ) {
					
					mActivity.mBoundService.setShuffle( true );
					
					mTracker.send( new HitBuilders.EventBuilder()
		        	.setCategory( "now playing button" )
		        	.setAction( "click" )
		        	.setLabel( "shuffle" )
		        	.setValue( 1 )
		        	.build());
					
				} else {
					
					mActivity.mBoundService.setShuffle( false );
					
					mTracker.send( new HitBuilders.EventBuilder()
		        	.setCategory( "now playing button" )
		        	.setAction( "click" )
		        	.setLabel( "shuffle" )
		        	.setValue( 0 )
		        	.build());
					
				}
				
			}
			
			
		}
		
	};
	
	   
	   
   private void recycleAlbumArt() {
	   
	   BitmapDrawable bd = ( BitmapDrawable ) mCompactAlbumCover.getDrawable();
		
		if ( null != bd && null != bd.getBitmap() ) {
			
			bd.getBitmap().recycle();
			mCompactAlbumCover.setImageBitmap( null );
			
		}
	   
   }
	
   public PlaybackListener PlaybackListener = new PlaybackListener() {
	   
		@Override public void onTrackChanged( String media_id ) {
		
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
	    		
	    		new StarToggleTask( mStarButton ).execute( current_media_id );
	    		
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
						*/
						
						
						// Otherwise, nextAlbumUri and lastAlbumUri are the same, we leave the ImageView alone
						// and don't recycle the backing bitmap;
						
						//lastAlbumUri = nextAlbumUri;
						
						//albumCursor.close();
						//mSongCursor.close();
						
						
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
										
										final BitmapWorkerTask albumThumbTask = new BitmapWorkerTask( mCompactAlbumCover, getResources().getDimensionPixelSize( R.dimen.footer_height ) );
										final BitmapWorkerTask albumFullTask = new BitmapWorkerTask( mFullAlbumArt );
										final BitmapWorkerTask albumBackgroundTask = new BitmapWorkerTask( mAlbumArtBackground );
										
										final AsyncDrawable asyncThumbDrawable =
								                new AsyncDrawable( getResources(),
								                		null, // BitmapFactory.decodeResource( mContext.getResources(), R.drawable.no_album_art_thumb )
								                		albumThumbTask );
										
										final AsyncDrawable asyncFullDrawable =
								                new AsyncDrawable( getResources(),
								                		null, // BitmapFactory.decodeResource( mContext.getResources(), R.drawable.no_album_art_thumb )
								                		albumFullTask );
										
										final AsyncDrawable asyncBackgroundDrawable =
												new AsyncDrawable( getResources(),
								                		null, // BitmapFactory.decodeResource( mContext.getResources(), R.drawable.no_album_art_thumb )
								                		albumBackgroundTask );
										
										mCompactAlbumCover.setImageDrawable( asyncThumbDrawable );
										mFullAlbumArt.setImageDrawable( asyncFullDrawable );
										mAlbumArtBackground.setImageDrawable( asyncBackgroundDrawable );
										
								        albumThumbTask.execute( nextAlbumUri );
								        albumFullTask.execute( nextAlbumUri );
								        albumBackgroundTask.execute( nextAlbumUri );
								        
								        
									}
							        
								} else {
									
									mCompactAlbumCover.setImageResource( R.drawable.no_album_art_thumb );
									mFullAlbumArt.setImageResource( R.drawable.no_album_art_full );
									
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

		@Override public void onPlay() {
			   
			isPlaying = true;
			
			mExpControlsPlayPauseButton.setImageResource( R.drawable.ic_action_playback_pause_white );
			mCompactPlayPauseButton.setImageResource( R.drawable.ic_action_playback_pause_white );
			mTitle.setSelected( true );
		    mTitle.setSingleLine( true );
			mTitle.setEllipsize( TextUtils.TruncateAt.MARQUEE );
			
			mProgressFragment.startProgress();
			mCompactProgressBar.start();
			
		}

		@Override public void onPause() {
		
		   isPlaying = false;
		   
		   mProgressFragment.stopProgress();
		   
		   mExpControlsPlayPauseButton.setImageResource( R.drawable.ic_action_playback_play_white );
		   
		   if ( null != mCompactPlayPauseButton ) {
			   
			   mCompactPlayPauseButton.setImageResource( R.drawable.ic_action_playback_play_white );
			   
		   }
		   
		   
		   
		   mTitle.setEllipsize( TextUtils.TruncateAt.END );
		   mCompactProgressBar.stop();
		   
		}

		@Override public void onPlaylistDone() {  }
		
		@Override public void onLoopingChanged( int loopState ) {
			
			if ( loopState == PlaylistMediaPlayer.LOOP_ALL ) {
				
				mExpControlsRepeatButton.setTag( R.id.tag_repeat_state, "1" );
				mExpControlsRepeatButton.setImageResource( R.drawable.ic_action_playback_repeat_orange_dark );
				
			} else if ( loopState == PlaylistMediaPlayer.LOOP_ONE ) {
				
				mExpControlsRepeatButton.setTag( R.id.tag_repeat_state, "2" );
				mExpControlsRepeatButton.setImageResource( R.drawable.ic_action_playback_repeat_1_orange_dark );
				
			} else {
				
				mExpControlsRepeatButton.setTag( R.id.tag_repeat_state, "0" );
				mExpControlsRepeatButton.setImageResource( R.drawable.ic_action_playback_repeat_white );
				
			}
			
		}
		
		@Override public void onShuffleChanged( boolean isShuffling ) {
			
			if ( !isShuffling ) {
				
				mExpControlsShuffleButton.setTag( R.id.tag_shuffle_state, "0" );
				mExpControlsShuffleButton.setImageResource( R.drawable.ic_action_playback_schuffle_white );
				
			} else {
				
				mExpControlsShuffleButton.setTag( R.id.tag_shuffle_state, "1" );
				mExpControlsShuffleButton.setImageResource( R.drawable.ic_action_playback_schuffle_orange_dark );
				
			}
			
		}
		
		@Override public void onDurationChanged( int position, int duration ) {
			
			mProgressFragment.setDuration( duration );
			mProgressFragment.setProgress( position );
			
			mCompactProgressBar.setMax( duration );
			mCompactProgressBar.setProgress( position );
			
		}
		
	   };
	   
	   
		
		
	   //
	   // Logic to control the transitions between the states of the fragment
	   // And state animation
	   // 
	   // There are four animations needed:
	   // Compact to full
	   // Expanded to full
	   // and Full to Compact
	   //
	   // Also needs to drag between these discrete states
	   // 
		
		private void showFull() {
			
			mCompactPlayPauseButton.setVisibility( View.GONE );
			mSongMenuButton.setVisibility( View.VISIBLE );
			mStarButton.setVisibility( View.VISIBLE );
			//mFullAlbumArt.setVisibility( View.VISIBLE );
			mCompactAlbumCover.setVisibility( View.GONE );
			//mFullPlaybackControls.setVisibility( View.VISIBLE );
			//mProgressFragment.getView().setVisibility( View.VISIBLE );
			mCompactAlbumArtContainer.setVisibility( View.GONE );
			
			mCompactProgressBar.setVisibility( View.GONE );
			
			//mFullAlbumArtContainer.setVisibility( View.VISIBLE );
			
		}
		
		private void showCollapsed() {
			
			// 
			mCompactProgressBar.setVisibility( View.VISIBLE );
			mCompactPlayPauseButton.setVisibility( View.VISIBLE );
			mSongMenuButton.setVisibility( View.GONE );
			mStarButton.setVisibility( View.GONE );
			//mFullAlbumArt.setVisibility( View.GONE );
			mCompactAlbumCover.setVisibility( View.VISIBLE );
			//mFullPlaybackControls.setVisibility( View.GONE );
			//mProgressFragment.getView().setVisibility( View.GONE );
			mCompactAlbumArtContainer.setVisibility( View.VISIBLE );
			
			//mFullAlbumArtContainer.setVisibility( View.GONE );
			
			
		}
		
		ContentObserver mediaStoreChanged = new ContentObserver( new Handler() ) {
			
	        @Override public void onChange( boolean selfChange ) {
	            
	            getActivity().runOnUiThread( new Runnable() {

					@Override public void run() {
						
						new StarToggleTask( mStarButton ).execute( current_media_id );
						
					}
	            	
	            });
	            
	            super.onChange( selfChange );
	            
	        }

		};
		
		
}
