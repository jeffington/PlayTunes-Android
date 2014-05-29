package com.ideabag.playtunes.fragment;

import com.ideabag.playtunes.MusicPlayerService;
import com.ideabag.playtunes.PlayTunesListAdapter;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.util.CommandUtils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;

public class SongsFragment extends ListFragment {
	
	private static final char MUSIC_NOTE = (char) 9834;
	
    private static final String[] allSongsSelection = new String[] {
    	
    	MediaStore.Audio.Media.TITLE,
    	MediaStore.Audio.Media.ARTIST,
    	MediaStore.Audio.Media.ALBUM,
    	MediaStore.Audio.Media.TRACK,
    	MediaStore.Audio.Media.DATA,
    	MediaStore.Audio.Media.ALBUM_ID,
    	MediaStore.Audio.Media.ARTIST_ID,
    	MediaStore.Audio.Media._ID
    	
    };
	
	Cursor cursor;
	PlayTunesListAdapter adapter;
	
	
	@Override public void onAttach( Activity activity ) {
			
		super.onAttach( activity );
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		ActionBar bar =	( ( ActionBarActivity ) getActivity()).getSupportActionBar();
		
    	cursor = getActivity().getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				allSongsSelection,
				null,
				null,
				MediaStore.Audio.Media.TITLE
			);
    	
    	adapter = new PlayTunesListAdapter( getActivity(),
    				R.layout.list_item_song_no_album,
    				cursor,
    				allSongsSelection,
    				new int[] {
    					R.id.SongTitle,
    					R.id.SongArtist,
    					R.id.SongAlbum
    				}
    			);
    	
    	setListAdapter( adapter );
    	
    	bar.setTitle( "" + Character.toString(MUSIC_NOTE) + "Dark Side of the Moon" );
		bar.setSubtitle( cursor.getCount() + " songs" );
    	
	}
		
	@Override public void onResume() {
		super.onResume();
			
		
	}
		
	@Override public void onPause() {
		super.onPause();
			
			
			
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		// Play the song
		Intent servicePlay = new Intent( getActivity(), MusicPlayerService.class );
		
		servicePlay.setAction(  getString( R.string.action_play )  );
		
		servicePlay.putExtra( "position", position );
		servicePlay.putExtra( "command", CommandUtils.COMMAND_SONGS_CODE );
		
		getActivity().startService( servicePlay );
		
			
	}
	
}