package com.ideabag.playtunes.fragment;

import com.ideabag.playtunes.MusicPlayerService.SongInfoChangedListener;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.activity.NowPlayingActivity;
import com.ideabag.playtunes.adapter.ArtistAllSongsAdapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FooterControlsFragment extends Fragment {
	
	private MainActivity mActivity;
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		//getView().setOnClickListener( controlsClickListener );
		
		getView().findViewById( R.id.FooterControls ).setOnClickListener( controlsClickListener );
    	
		getView().findViewById( R.id.FooterControlsPlayPauseButton ).setOnClickListener( controlsClickListener );
		
		//mActivity.BoundService.setOnSongInfoChangedListener( this );
		
	}
	
	@Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		
		return inflater.inflate( R.layout.fragment_footer_controls, null, false );
		
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
				
				Intent i = new Intent( mActivity, NowPlayingActivity.class );
				
				mActivity.startActivityForResult( i, 0 );//( i );
				
			} else if ( id == R.id.FooterControlsPlayPauseButton ) {
				
				mActivity.BoundService.pause();
				
				// or Play
				//mActivity.BoundService.play();
				
			}
			
			
		}
		
	};

	
}
