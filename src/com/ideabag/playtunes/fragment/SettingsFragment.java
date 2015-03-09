package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.plus.PlusOneButton;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.dialog.FeedbackDialogFragment;
import com.ideabag.playtunes.dialog.RateAppDialogFragment;
import com.ideabag.playtunes.media.MusicScanner;
import com.ideabag.playtunes.util.TrackerSingleton;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends Fragment {

	public static final String TAG = "Settings Fragment";
	public static final String MARKET_URL = "https://play.google.com/store/apps/details?id=com.ideabag.playtunes";
	
	PlusOneButton mPlusOneButton;
	
	MusicScanner mScanner;
	
	public SettingsFragment() {
		
		
		
	}
	
	@Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		
		return inflater.inflate( R.layout.fragment_settings, container, false );
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		
		super.onActivityCreated( savedInstanceState );
		
		getView().findViewById(R.id.SettingsButtonSupport).setOnClickListener( mSettingsClickListener );
		getView().findViewById(R.id.SettingsButtonFeedback).setOnClickListener( mSettingsClickListener );
		getView().findViewById( R.id.SettingsScanMediaButton ).setOnClickListener( mSettingsClickListener );
        //findViewById(R.id.SettingsButtonHelp).setOnClickListener(mSettingsClickListener);
        mPlusOneButton = ( PlusOneButton ) getView().findViewById(R.id.plus_one_button);
		
        mScanner = new MusicScanner( getActivity() );
        
	}
	
	@Override public void onResume() {
		super.onResume();
		
		mPlusOneButton.initialize( MARKET_URL, 0 );
		//mPlusOneButton.
		
		Tracker t = TrackerSingleton.getDefaultTracker( getActivity() );

	        // Set screen name.
	        // Where path is a String representing the screen name.
		t.setScreenName( TAG );
		//t.set( "_count", ""+adapter.getCount() );
		
	        // Send a screen view.
		t.send( new HitBuilders.AppViewBuilder().build() );
		
		
	}
	

	
	View.OnClickListener mSettingsClickListener = new View.OnClickListener() {
		
		@Override public void onClick( View v ) {
			int id = v.getId();
			
			if ( id == R.id.SettingsButtonSupport ) {
				
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	        	
	        	DialogFragment mNewFragment = new RateAppDialogFragment();
	        	
	            mNewFragment.show(ft, "dialog");
				
			} else if ( id == R.id.SettingsButtonFeedback ) {
				
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	        	
	        	DialogFragment mNewFragment = new FeedbackDialogFragment();
	        	
	            mNewFragment.show(ft, "dialog");
				
			} else if ( id == R.id.SettingsScanMediaButton ) {
				
				mScanner.scan();
				
			}
			
		}
	};

}
