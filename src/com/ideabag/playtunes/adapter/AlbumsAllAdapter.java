package com.ideabag.playtunes.adapter;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.database.MediaQuery;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumsAllAdapter extends AlbumListAdapter {
	
    private static final String[] allAlbumsSelection = new String[] {
    	
    	MediaStore.Audio.Albums.ALBUM,
    	MediaStore.Audio.Albums.ARTIST,
    	MediaStore.Audio.Albums.NUMBER_OF_SONGS,
    	MediaStore.Audio.Albums.ALBUM_ART,
    	MediaStore.Audio.Albums._ID,
    	
    	
    };
	
	public AlbumsAllAdapter( Context context ) {
		super( context );
		
		mQuery = new MediaQuery(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
				allAlbumsSelection,
				MediaStore.Audio.Media.ALBUM + "!=?",
				new String[] {
						mContext.getString( R.string.no_album_string )
				},
				MediaStore.Audio.Albums.DEFAULT_SORT_ORDER
			);
		
		requery();
		
	}
	
}
