package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.database.MediaQuery;

import android.content.Context;
import android.provider.MediaStore;
import android.view.View.OnClickListener;

public class SongSearchAdapter extends SongListAdapter {

    /* $sql_query = "SELECT (";
	$words = explode(" ", $text);
	for ($x = 0, $count = count($words); $x < $count; $x++)
		$sql_query .= "(userName LIKE '%".$words[$x]."%') + (firstName LIKE '%".$words[$x]."%') + (lastName LIKE '%".$words[$x]."%')";
	$sql_query .= ") RELEVANCE, userName, firstName, lastName, points, imageurl FROM USER HAVING RELEVANCE > 0 ORDER BY RELEVANCE DESC" */
	public SongSearchAdapter( Context context, OnClickListener menuClickListener, String searchTerms ) {
		super(context, menuClickListener);
		

    	buildSearchQuery( searchTerms );
		
	}
	
	public void buildSearchQuery( String searchTerms ) {
		
		String mRelevanceSelection = "(" + MediaStore.Audio.Media.TITLE + " LIKE '%" + searchTerms + "%' ) WEIGHT";
		
	    String[] songSearchSelection = new String[] {
		    	
		    	MediaStore.Audio.Media._ID,
		    	
		    	MediaStore.Audio.Media.TITLE,
		    	MediaStore.Audio.Media.ARTIST,
		    	MediaStore.Audio.Media.ALBUM,
		    	MediaStore.Audio.Media.TRACK,
		    	MediaStore.Audio.Media.DATA,
		    	MediaStore.Audio.Media.ALBUM_ID,
		    	MediaStore.Audio.Media.ARTIST_ID,
		    	mRelevanceSelection
		    	
		    };
		
    	mQuery = new MediaQuery(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				songSearchSelection,
				MediaStore.Audio.Media.IS_MUSIC + " != 0",
				null,
				"WEIGHT"
				);
		
    	requery();
    	
	}
	
	
}
