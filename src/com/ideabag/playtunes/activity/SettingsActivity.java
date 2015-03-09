package com.ideabag.playtunes.activity;

import com.ideabag.playtunes.R;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class SettingsActivity extends ActionBarActivity {
	
	public static final String TAG = "Settings Activity";
	public static final String MARKET_URL = "https://play.google.com/store/apps/details?id=com.ideabag.playtunes";
	
	
	@Override public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_settings );
		
		ActionBar supportBar = getSupportActionBar();
		
		
        supportBar.setDisplayShowHomeEnabled( true );
        supportBar.setDisplayHomeAsUpEnabled( true );
        supportBar.setHomeButtonEnabled( true );
        
        
        
	}
	
	
}
