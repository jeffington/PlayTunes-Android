package com.ideabag.playtunes.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;

import com.ideabag.playtunes.MusicPlayerService;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.AlbumsAllAdapter;
import com.ideabag.playtunes.util.CommandUtils;

public class AlbumsAllFragment extends ListFragment {
	
	private static final char MUSIC_NOTE = (char) 9834;

	AlbumsAllAdapter adapter;
	private MainActivity mActivity;
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		ActionBar bar =	( ( ActionBarActivity ) getActivity() ).getSupportActionBar();
    	
    	adapter = new AlbumsAllAdapter( getActivity() );
    	
    	setListAdapter( adapter );
    	
    	bar.setTitle( "Albums" );
		bar.setSubtitle( adapter.getCount() + " albums" );
    	
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
		
	}
	
	@Override public void onResume() {
		super.onResume();
		
		
	}
	
	@Override public void onPause() {
		super.onPause();
		
		
		
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		//convertView.setTag( R.id.tag_album_id, cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM ) ) );
		
		String albumID = ( String ) v.getTag( R.id.tag_album_id );
		
		AlbumsOneFragment albumFragment = new AlbumsOneFragment( );
		albumFragment.setAlbumId( albumID );
		
		mActivity.transactFragment( albumFragment );
		
	}
	
}