package com.ideabag.playtunes;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity {
	
	private int getPref( String str ) {
		
		Resources res = getResources();
		String[] strings = res.getStringArray( R.array.commands );
		int[] codes = res.getIntArray( R.array.command_codes );
		
		if ( str.equals( "" ) ) {
		
			return -1;
			
		}
		
		for ( int x = 0; x < strings.length; x++ ) {
			
			if ( strings[ x ].equals( str ) ) {
				
				return codes[ x ];
				
			}
			
		}
		
		return -1;
		
	}
	
	SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
		
		public void onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key ) {
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( getBaseContext() );
			SharedPreferences.Editor editor = prefs.edit();
			
			if ( key.equals( getString( R.string.preference_topleft_string ) ) ) {
				
				editor.putInt( getString( R.string.preference_topleft ), getPref( prefs.getString( getString( R.string.preference_topleft_string), "" ) ) );
				editor.commit();
			
			} else if ( key.equals( getString( R.string.preference_topright_string ) ) ) {
				
				editor.putInt( getString( R.string.preference_topright ), getPref( prefs.getString( getString( R.string.preference_topright_string ), "" ) ) );
				editor.commit();
				
			} else if ( key.equals( getString( R.string.preference_bottomleft_string ) ) ) {
				
				editor.putInt( getString( R.string.preference_bottomleft ), getPref( prefs.getString( getString( R.string.preference_bottomleft_string ), "" ) ) );
				editor.commit();
				
			} else if ( key.equals( getString( R.string.preference_bottomright_string ) ) ) {
				
				editor.putInt( getString( R.string.preference_bottomright ), getPref( prefs.getString( getString( R.string.preference_bottomright_string ), "" ) ) );
				editor.commit();
				
			}
			
		}
	};
	
	@Override protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		addPreferencesFromResource( R.xml.preferences );
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( getBaseContext() );
		prefs.registerOnSharedPreferenceChangeListener( preferenceChangeListener );
		
	}
	
}
