package com.ideabag.playtunes.fragment.xlarge;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.fragment.TrackProgressFragment;
import com.ideabag.playtunes.media.PlaylistMediaPlayer;
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
import android.widget.ToggleButton;

public class LargeFooterControlsFragment extends Fragment {
	
	public static final String TAG = "Large Footer Controls";
	
	private MainActivity mActivity;
	private Tracker mTracker;
	
	private String lastAlbumUri = null;
	private String current_media_id;
	private boolean isPlaying = false;
	//private boolean isShowing = false;
	
	//
	// Views from fragment_footer_controls.xml
	//
	//private ImageButton mPlayPauseButton;
	//private ImageView mAlbumCover;
	//private TextView mTitle, mArtist;
	private ImageButton mPrevButton;
	private ImageButton mNextButton;
	private ImageButton mPlayPauseButton;
	private ImageButton mRepeatButton;
	private ImageButton mShuffleButton;
	
	private TrackProgressFragment mProgressFragment;
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		mTracker = TrackerSingleton.getDefaultTracker( mActivity );
		
	}
	
	@SuppressLint("NewApi")
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		mNextButton = ( ImageButton ) getView().findViewById( R.id.NowPlayingNextButton );
		mPlayPauseButton = ( ImageButton ) getView().findViewById( R.id.NowPlayingPlayPauseButton );
		mPrevButton = ( ImageButton ) getView().findViewById( R.id.NowPlayingPrevButton );
		mRepeatButton = ( ImageButton ) getView().findViewById( R.id.NowPlayingRepeatButton );
		mShuffleButton = ( ImageButton ) getView().findViewById( R.id.NowPlayingShuffleButton );
		
		mNextButton.setOnClickListener( controlsClickListener );
		mPlayPauseButton.setOnClickListener( controlsClickListener );
		mPrevButton.setOnClickListener( controlsClickListener );
		mRepeatButton.setOnClickListener( controlsClickListener );
		mShuffleButton.setOnClickListener( controlsClickListener );
		
	}
	
	
	@Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		
		return inflater.inflate( R.layout.fragment_footer_controls, container, false );
		
	}
	
	@Override public void onResume() {
		super.onResume();
		
		if ( mProgressFragment == null ) {
			android.util.Log.i( TAG, "ProgressFragment is null");
			mProgressFragment = ( TrackProgressFragment ) getChildFragmentManager().findFragmentById( R.id.TrackProgressFragment );
			
		}
		
		mActivity.addPlaybackListener( PlaybackListener );
		
	}
	
	@Override public void onPause() {
		super.onPause();
		
		mActivity.removePlaybackListener( PlaybackListener );
		
	}
	
	@Override public void onDestroyView() {
		super.onDestroyView();
		
		//recycleAlbumArt();
		
	}
	
	View.OnClickListener controlsClickListener = new View.OnClickListener() {
		
		@Override public void onClick( View v ) {
			
			int id = v.getId();
			
			
			if ( id == R.id.NowPlayingPlayPauseButton ) {
				
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
				
			} else if ( id == R.id.NowPlayingNextButton ) {
				
				mActivity.mBoundService.next();
				
				mTracker.send( new HitBuilders.EventBuilder()
				.setCategory( Categories.FOOTER_CONTROLS )
		    	.setAction( AudioControls.ACTION_NEXT )
		    	.build());
				
			} else if ( id == R.id.NowPlayingPrevButton ) {
				
				mActivity.mBoundService.prev();
				
				mTracker.send( new HitBuilders.EventBuilder()
				.setCategory( Categories.FOOTER_CONTROLS )
		    	.setAction( AudioControls.ACTION_PREV )
		    	.build());
				
			}else if ( id == R.id.NowPlayingRepeatButton ) {
				
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
	
	   
	   /*
   private void recycleAlbumArt() {
	   
	   BitmapDrawable bd = ( BitmapDrawable ) mAlbumCover.getDrawable();
		
		if ( null != bd && null != bd.getBitmap() ) {
			
			bd.getBitmap().recycle();
			mAlbumCover.setImageBitmap( null );
			
		}
	   
   }
	*/

	PlaybackListener PlaybackListener = new PlaybackListener() {

		@Override public void onTrackChanged( String media_id ) { }

		@Override public void onPlay() {
			   
			isPlaying = true;
			   
			mPlayPauseButton.setImageResource( R.drawable.ic_action_playback_pause_white );
			//mTitle.setSelected( true );
		    //mTitle.setSingleLine( true );
			//mTitle.setEllipsize( TextUtils.TruncateAt.MARQUEE );
			mProgressFragment.startProgress();
			   
		}

		@Override public void onPause() {
			
			   isPlaying = false;
			   
			   mPlayPauseButton.setImageResource( R.drawable.ic_action_playback_play_white );
			   
			   //mTitle.setEllipsize( TextUtils.TruncateAt.END );
			   mProgressFragment.stopProgress();
			
		}

		@Override public void onPlaylistDone() {
			
			mProgressFragment.stopProgress();
			
		}
		
		@Override public void onLoopingChanged( int loopState ) {
			
			if ( loopState == PlaylistMediaPlayer.LOOP_ALL ) {
				
				mRepeatButton.setTag( R.id.tag_repeat_state, "1" );
				mRepeatButton.setImageResource( R.drawable.ic_action_playback_repeat_orange_dark );
				
			} else if ( loopState == PlaylistMediaPlayer.LOOP_ONE ) {
				
				mRepeatButton.setTag( R.id.tag_repeat_state, "2" );
				mRepeatButton.setImageResource( R.drawable.ic_action_playback_repeat_1_orange_dark );
				
			} else {
				
				mRepeatButton.setTag( R.id.tag_repeat_state, "0" );
				mRepeatButton.setImageResource( R.drawable.ic_action_playback_repeat_white );
				
			}
			
		}
		
		@Override public void onShuffleChanged(boolean isShuffling) {
			
			if ( !isShuffling ) {
				
				mShuffleButton.setTag( R.id.tag_shuffle_state, "0" );
				mShuffleButton.setImageResource( R.drawable.ic_action_playback_schuffle_white );
				
			} else {
				
				mShuffleButton.setTag( R.id.tag_shuffle_state, "1" );
				mShuffleButton.setImageResource( R.drawable.ic_action_playback_schuffle_orange_dark );
				
			}
			
		}
		
		@Override public void onDurationChanged( int position, int duration ) {
			
			if ( mProgressFragment == null ) {
				
				android.util.Log.i( TAG, "null 1");
				
			}
			
			mProgressFragment.setDuration( duration );
			mProgressFragment.setProgress( position );
			
		}
		
	};
	   
}
