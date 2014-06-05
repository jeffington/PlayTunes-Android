package com.ideabag.playtunes.view;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.service.PlaylistService;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.view.View;
import android.view.View.OnClickListener;

public class StarButton extends CheckBox implements OnClickListener {
	
	public StarButton( Context context) {
		super( context, null, 0 );
		
		setOnClickListener( this );
		
	}
	
	public StarButton( Context context, AttributeSet attrs ) {
		super( context, attrs, 0 );
		
		setOnClickListener( this );
		
	}
	
	public StarButton( Context context, AttributeSet attrs, int defStyle ) {
		super( context, attrs, defStyle );
		
		setOnClickListener( this );
		
	}
	
	@Override public void onClick( View v ) {
		
		String song_id = ( String ) v.getTag( R.id.tag_song_id );
		
		Intent toggleFavoriteIntent = new Intent( getContext(), PlaylistService.class );
		
		if ( null == song_id ) {
			
			toggleFavoriteIntent.putExtra( PlaylistService.EXTRA_SONG_ID, song_id );
			
			if ( isChecked() ) {
				
				toggleFavoriteIntent.setAction( PlaylistService.ACTION_ADD_FAVORITE );
				
			} else {
				
				toggleFavoriteIntent.setAction( PlaylistService.ACTION_REMOVE_FAVORITE );
				
			}
			
			getContext().startService( toggleFavoriteIntent );
			
		}
		
	}
	
	

}
