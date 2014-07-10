package com.ideabag.playtunes.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter; 
import android.widget.ListView;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;

public class SongMenuDialogFragment extends DialogFragment {
	
	String mMediaID;
	
	
    public SongMenuDialogFragment() {
        // Empty constructor required for DialogFragment
    	setStyle( STYLE_NORMAL, 0 );
    	
    }
    
    public void setMediaID( String media_id ) {
    	
    	this.mMediaID = media_id;
    	
    	android.util.Log.i( "SongMenuDialogFragment", "Media ID set" );
    	
    }

    @Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        View view = inflater.inflate(R.layout.dialog_fragment_songmenu, container);
        //mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        getDialog().requestWindowFeature( Window.FEATURE_NO_TITLE );
        
        view.findViewById( R.id.SongMenuAlbum ).setOnClickListener( mMenuClickListener );
        view.findViewById( R.id.SongMenuArtist ).setOnClickListener( mMenuClickListener );
        view.findViewById( R.id.SongMenuAddTo ).setOnClickListener( mMenuClickListener );
        
        return view;
        
    }

	View.OnClickListener mMenuClickListener = new View.OnClickListener() {
		
		@Override public void onClick( View v ) {
			
			int id = v.getId();
			
			if ( id == R.id.SongMenuAddTo ) {
				
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	        	
				Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
				
			    if ( prev != null ) {
			        
			    	ft.remove( prev );
			    	
			    }
				
				AddToPlaylistDialogFragment newFragment = new AddToPlaylistDialogFragment();
				newFragment.setMediaID( mMediaID );
	        	
	            newFragment.show( ft, "dialog" );
				
			} else if ( id == R.id.SongMenuArtist ) {
				
				
				
			} else if ( id == R.id.SongMenuAlbum ) {
				
				
				
			}
			
		}
		
	};
	
}
