package com.ideabag.playtunes.view;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.adapter.SongsAllAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

public class SongMenuButton extends ImageButton implements OnClickListener {

	AlertDialog.Builder DialogBuilder;
	
	LayoutInflater inflater = null;
	
	public SongMenuButton( Context context ) {
		super( context, null, R.style.MenuButton );
		
		setOnClickListener( this );
		
	}
	
	public SongMenuButton( Context context, AttributeSet attrs) {
		super( context, attrs, R.style.MenuButton );
		
		setOnClickListener( this );
		
	}
	
	public SongMenuButton( Context context, AttributeSet attrs, int defStyle ) {
		super( context, attrs, defStyle );
		
		setOnClickListener( this );
		
	}

	@Override public void onClick( View v ) {
		
		String id = ( String ) v.getTag( R.id.tag_song_id );
		
		if ( null != id ) {
			
			if ( null == inflater ) {
				
				inflater = ( LayoutInflater ) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
				
			}
			
			DialogBuilder = new AlertDialog.Builder( getContext() );
			
			//DialogBuilder.setTitle( "BUILT TITLE" );
			
			LinearLayout dialog_header = ( LinearLayout ) inflater.inflate( R.layout.dialog_header, null );
			
			DialogBuilder.setCustomTitle( dialog_header );
			

			
			DialogBuilder.setAdapter( new SongsAllAdapter( getContext() ), null );
			DialogBuilder.setPositiveButton( R.string.songs, null);
			
			AlertDialog dialog = DialogBuilder.create();
			/*
			int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
	        View titleDivider = dialog.getWindow().getDecorView().findViewById(titleDividerId);
	        titleDivider.setBackgroundColor( getResources().getColor( android.R.color.transparent ) ); // change divider color
			*/
	        dialog.show();
	        
		}
		
	}
	
	

}
