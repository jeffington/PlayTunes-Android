package com.ideabag.playtunes.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.ideabag.playtunes.R;

public class PlaylistMenuDialogFragment extends DialogFragment {
	
	String mMediaID;
	
	
    public PlaylistMenuDialogFragment() {
        // Empty constructor required for DialogFragment
    	setStyle( STYLE_NORMAL, 0 );
    	
    }
    
    public void setMediaID( String media_id ) {
    	
    	this.mMediaID = media_id;
    	
    }

    @Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        View view = inflater.inflate(R.layout.dialog_fragment_playlistoptions, container);
        //mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        getDialog().requestWindowFeature( Window.FEATURE_NO_TITLE );
        
        view.findViewById( R.id.ButtonCancel ).setOnClickListener( mMenuClickListener );
        view.findViewById( R.id.PlaylistOptionsRename ).setOnClickListener( mMenuClickListener );
        view.findViewById( R.id.PlaylistOptionsDelete ).setOnClickListener( mMenuClickListener );
        
        return view;
        
    }
    
    @Override public void onStart() {
    	super.onStart();
    	
    	if ( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB ) {
    		
    		getDialog().getWindow().setBackgroundDrawableResource( R.drawable.gb_dialog_background );
    		
    	}
    	
    }
    
	View.OnClickListener mMenuClickListener = new View.OnClickListener() {
		
		@Override public void onClick( View v ) {
			
			int id = v.getId();
			
			if ( id == R.id.PlaylistOptionsRename ) {
				
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	        	
				Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
				
			    if ( prev != null ) {
			        
			    	ft.remove( prev );
			    	
			    }
				
			    PlaylistRenameDialogFragment newFragment = new PlaylistRenameDialogFragment();
				newFragment.setMediaID( mMediaID );
	        	
	            newFragment.show( ft, "dialog" );
				
			} else if ( id == R.id.PlaylistOptionsDelete ) {
				
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	        	
				Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
				
			    if ( prev != null ) {
			        
			    	ft.remove( prev );
			    	
			    }
				
			    PlaylistDeleteDialogFragment newFragment = new PlaylistDeleteDialogFragment();
				newFragment.setMediaID( mMediaID );
	        	
	            newFragment.show( ft, "dialog" );
				
			} else if ( id == R.id.ButtonCancel ) {
				
				dismiss();
				
			}
			
		}
		
	};
	
}
