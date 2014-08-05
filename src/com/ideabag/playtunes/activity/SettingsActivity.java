package com.ideabag.playtunes.activity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.dialog.FeedbackDialogFragment;
import com.ideabag.playtunes.dialog.RateAppDialogFragment;
import com.ideabag.playtunes.util.TrackerSingleton;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class SettingsActivity extends ActionBarActivity {
	
	public static final String TAG = "Settings Activity";
	
	@Override public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_settings );
		
		ActionBar supportBar = getSupportActionBar();
		
		
		//supportBar.setTitle( "PlayTunes Settings" );
        supportBar.setDisplayShowHomeEnabled( true );
        supportBar.setDisplayHomeAsUpEnabled( true );
        supportBar.setHomeButtonEnabled( true );
        
        findViewById(R.id.SettingsButtonSupport).setOnClickListener(mSettingsClickListener);
        findViewById(R.id.SettingsButtonFeedback).setOnClickListener(mSettingsClickListener);
        findViewById(R.id.SettingsButtonHelp).setOnClickListener(mSettingsClickListener);
        
	}
	
	@Override public void onResume() {
		super.onResume();
		
		Tracker t = TrackerSingleton.getDefaultTracker( this );

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
				
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	        	
	        	DialogFragment mNewFragment = new RateAppDialogFragment();
	        	
	            mNewFragment.show(ft, "dialog");
				
			} else if ( id == R.id.SettingsButtonFeedback ) {
				
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	        	
	        	DialogFragment mNewFragment = new FeedbackDialogFragment();
	        	
	            mNewFragment.show(ft, "dialog");
				
			} else if ( id == R.id.SettingsButtonHelp ) {
				
				
				
			}
			
		}
	};
	
}
