package com.ideabag.playtunes.activity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.util.TrackerSingleton;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

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
	
}
