package com.ideabag.playtunes.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;

public class CreatePlaylistDialogFragment extends DialogFragment implements OnEditorActionListener {
	
	private EditText mEditText;
	private PlaylistManager mPlaylistManager;
	
	private String mMediaID = null;
	
	public CreatePlaylistDialogFragment() {
		
		setStyle( STYLE_NORMAL, 0 );
		
		
		
		
	}
	
	public void setMediaID( String media_id ) {
		
		this.mMediaID = media_id;
		
	}
	
    @Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        View view = inflater.inflate( R.layout.dialog_frament_newplaylist, container);
        mEditText = ( EditText ) view.findViewById( R.id.NewPlaylistName );
        
        getDialog().requestWindowFeature( Window.FEATURE_NO_TITLE );
        
        mPlaylistManager = new PlaylistManager( getActivity() );
        
        mEditText.setOnEditorActionListener( this );
        mEditText.setSelected( false );
        
        view.findViewById( R.id.DialogConfirm ).setOnClickListener( headerButtonClickListener );
        view.findViewById( R.id.DialogCancel ).setOnClickListener( headerButtonClickListener );
        //view.findViewById( R.id.DialogCloseButton ).setOnClickListener( headerButtonClickListener );
        
        return view;
        
    }
	
    @Override public void onStart() {
    	super.onStart();
    	
    	if ( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB ) {
    		
    		getDialog().getWindow().setBackgroundDrawableResource( R.drawable.gb_dialog_background );
    		
    	}
    	
    	mEditText.performClick();
    	
    }
    
    private View.OnClickListener headerButtonClickListener = new View.OnClickListener() {
		
		@Override public void onClick(View v) {
			
			int id = v.getId();
			
			if ( id == R.id.DialogCancel ) {
				
				dismiss();
				
			} else if ( id == R.id.DialogConfirm ) {
				
				createPlaylist();
				
			}
			
		}
		
	};
    
    private void createPlaylist() {
    	
    	String playlistName = mEditText.getEditableText().toString().trim();
    	
    	if ( null == playlistName || playlistName.length() < 1 ) {
    		
    		Toast.makeText( getActivity(), getString( R.string.playlist_enter_name ), Toast.LENGTH_SHORT ).show();
    		
    	} else {
    		
    		if ( playlistName.toLowerCase().equals( getString( R.string.playlist_name_starred ).toLowerCase() ) ) {
    			
    			Toast.makeText( getActivity(), getString( R.string.playlist_cant_name_starred ), Toast.LENGTH_SHORT ).show();
    			
    		} else {
	    		
	    		int playlist_id = mPlaylistManager.createPlaylist( playlistName );
	    		
	    		if ( null != mMediaID ) {
	    			
	    			mPlaylistManager.addSong( "" + playlist_id, mMediaID );
	    			
	    			Toast.makeText( getActivity(), getString( R.string.playlist_added_song_to_new )  + " " + playlistName, Toast.LENGTH_SHORT ).show();
	    			
	    		} else {
	    			
	    			Toast.makeText( getActivity(), getString( R.string.playlist_created ) + " " + playlistName, Toast.LENGTH_SHORT ).show();
	    			
	    		}
	    		
	    		dismiss();
	    		
    		}
    		
    	}
    	
    }
    
    public boolean onEditorAction( TextView v, int actionId, KeyEvent event ) {
        
    	if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
            //EditNameDialogListener activity = (EditNameDialogListener) getActivity();
            //activity.onFinishEditDialog(mEditText.getText().toString());
            
    		createPlaylist();
    		
            return true;
            
        }
        
        return false;
        
    }
    
}
