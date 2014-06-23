package com.ideabag.playtunes.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.NowPlayingActivity;

public class NowPlayingControlsFragment extends Fragment {

	
	private NowPlayingActivity mActivity;
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( NowPlayingActivity ) activity;
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		//getView().setOnClickListener( controlsClickListener );
		
		//getView().findViewById( R.id.FooterControls ).setOnClickListener( controlsClickListener );
    	
		//getView().findViewById( R.id.FooterControlsPlayPauseButton ).setOnClickListener( controlsClickListener );
		
		//mActivity.BoundService.setOnSongInfoChangedListener( this );
		
		//this.Bar = ( SeekBar ) getView().findViewById( R.id.TrackProgressBar );
		
		//this.Bar.setProgressDrawable( mActivity.getResources().getDrawable( R.drawable.progress_indicator ) );
		
		
	}
	
	@Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		
		return inflater.inflate( R.layout.fragment_track_progress, null, false );
		
	}
	
	@Override public void onResume() {
		super.onResume();
		
		
	}
		
	@Override public void onPause() {
		super.onPause();
		
		
		
	}
	
}
