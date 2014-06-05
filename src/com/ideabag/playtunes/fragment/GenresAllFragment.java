package com.ideabag.playtunes.fragment;

import com.ideabag.playtunes.MusicPlayerService;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.adapter.GenresAllAdapter;
import com.ideabag.playtunes.util.CommandUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;

public class GenresAllFragment extends ListFragment {
	
	GenresAllAdapter adapter;
	
	
	@Override public void onAttach( Activity activity ) {
			
		super.onAttach( activity );
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		ActionBar bar =	( ( ActionBarActivity ) getActivity()).getSupportActionBar();
		
		adapter = new GenresAllAdapter( getActivity() );
    	
    	setListAdapter( adapter );
    	
    	bar.setTitle( "Genres" );
		//bar.setSubtitle( cursor.getCount() + " songs" );
    	getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
    	
	}
		
	@Override public void onResume() {
		super.onResume();
			
		
	}
		
	@Override public void onPause() {
		super.onPause();
			
			
			
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		
			
	}
	
}