package com.ideabag.playtunes.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.NowPlayingActivity;

public class TrackProgressFragment extends Fragment {
	
	private static final int ONE_SECOND_IN_MILLI = 1000;
	
	private NowPlayingActivity mActivity;
	
	private SeekBar mBar;
	
	private Handler handle;
	
	private boolean isPlaying = false;
	
	TextView mPlayLength, mPlayPosition;
	
	int mPlayLengthMilli, mPlayPositionMilli;
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( NowPlayingActivity ) activity;
		
		handle = new Handler();
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		mBar = ( SeekBar ) getView().findViewById( R.id.TrackProgressBar );
		
		mPlayLength = ( TextView ) getView().findViewById( R.id.TrackProgressPlayLength );
		mPlayPosition = ( TextView ) getView().findViewById( R.id.TrackProgressPlayPosition );
		
		mBar.setOnSeekBarChangeListener( mSeekBarChangedListener );
		
	}
	
	@Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		
		return inflater.inflate( R.layout.fragment_track_progress, container, false );
		
	}
	
	@Override public void onResume() {
		super.onResume();
		startProgress();
		
	}
		
	@Override public void onPause() {
		super.onPause();
		
		stopProgress();
		
	}
	
	
	private OnSeekBarChangeListener mSeekBarChangedListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged( SeekBar seekmBar, int progress, boolean fromUser) {
			
			if ( fromUser ) {
				
				stopProgress();
				
				setProgress( progress );
				
				mActivity.mBoundService.setSeekPosition( progress );
				
			}
			
			
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekmBar) {
			
			stopProgress();
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekmBar) {
			
			if ( isPlaying ) {
				
				startProgress();
				
			}
			
		}
		
	};
	
	private final Runnable mUpdateTimer = new Runnable() {

		@Override public void run() {
			
			
			//android.util.Log.i("mBar Progress",  "" + mBar.getProgress() );
			
			mPlayPositionMilli += ONE_SECOND_IN_MILLI;
			
			setProgress( mPlayPositionMilli );
			
			handle.postDelayed( mUpdateTimer, ONE_SECOND_IN_MILLI );
			
		}
		
		
		
	};
	
	//private 
	public void startProgress() {
		
		stopProgress();
		handle.postDelayed( mUpdateTimer, ONE_SECOND_IN_MILLI );
		//handle.postDelayed( mUpdateTimer, ONE_SECOND_IN_MILLI );
		
		
	}
	
	public void stopProgress() {
		
		handle.removeCallbacks( mUpdateTimer );
		
	}
	
	public void setProgress( int progress ) {
		
		stopProgress();
		
		mPlayPositionMilli = progress;
		
		mBar.setProgress( mPlayPositionMilli );
		
		int seconds = progress / ONE_SECOND_IN_MILLI;
		int minutes = seconds / 60;
		seconds = seconds % 60;
		
		mPlayPosition.setText( minutes + ":" + ( seconds < 10 ? "0" + seconds : seconds ) );
		
		if ( isPlaying ) {
			
			startProgress();
			
		}
		
	}
	
	public void setDuration( int duration ) {
		
		mPlayLengthMilli = duration;
		
		mBar.setMax( mPlayLengthMilli );
		
		int seconds = duration / ONE_SECOND_IN_MILLI;
		int minutes = seconds / 60;
		seconds = seconds % 60;
		
		mPlayLength.setText( minutes + ":" + ( seconds < 10 ? "0" + seconds : seconds ) );
		
	}
	
}
