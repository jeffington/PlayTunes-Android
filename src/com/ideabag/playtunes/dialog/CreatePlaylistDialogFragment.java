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
        
        view.findViewById( R.id.DialogConfirmButton ).setOnClickListener( headerButtonClickListener );
        view.findViewById( R.id.DialogCloseButton ).setOnClickListener( headerButtonClickListener );
        
        return view;
        
    }
	
    @Override public void onStart() {
    	super.onStart();
    	
    	
    	mEditText.performClick();
    	
    }
    
    private View.OnClickListener headerButtonClickListener = new View.OnClickListener() {
		
		@Override public void onClick(View v) {
			
			int id = v.getId();
			
			if ( id == R.id.DialogCloseButton ) {
				
				dismiss();
				
			} else if ( id == R.id.DialogConfirmButton ) {
				
				createPlaylist();
				
			}
			
		}
		
	};
    
    private void createPlaylist() {
    	
    	String playlistName = mEditText.getEditableText().toString().trim();
    	
    	if ( null == playlistName || playlistName.length() < 1 ) {
    		
    		Toast.makeText(getActivity(), "Please enter a playlist name.", Toast.LENGTH_SHORT ).show();
    		
    	} else {
    		
    		if ( playlistName.toLowerCase().equals( getString( R.string.playlist_name_starred ).toLowerCase() ) ) {
    			
    			Toast.makeText(getActivity(), "Your playlist can't be named `Starred`.", Toast.LENGTH_SHORT ).show();
    			
    		} else {
	    		
	    		int playlist_id = mPlaylistManager.createPlaylist( playlistName );
	    		
	    		if ( null != mMediaID ) {
	    			
	    			mPlaylistManager.addSong( "" + playlist_id, mMediaID );
	    			
	    			Toast.makeText(getActivity(), "Added song to new playlist " + playlistName, Toast.LENGTH_SHORT ).show();
	    			
	    		} else {
	    			
	    			Toast.makeText(getActivity(), "Created playlist " + playlistName, Toast.LENGTH_SHORT ).show();
	    			
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
    
	/* 	public void createPlaylistDialog() {
	
	AlertDialog.Builder builder = new AlertDialog.Builder( mContext );
	builder.setTitle("New Playlist Name");
	
	// Set up the input
	final EditText input = new EditText( mContext );
	//input.setTransformationMethod( SingleLineTransformationMethod.getInstance() );
	// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
	input.setInputType( InputType.TYPE_CLASS_TEXT );
	input.setFocusable( true );
	input.setFocusableInTouchMode( true );
	//input.clearFocus();
	builder.setView( input );

	// Set up the buttons
	builder.setPositiveButton( "Ok", new DialogInterface.OnClickListener() {
		
	    @Override public void onClick( DialogInterface dialog, int which ) {
	        // Create playlist
	    	String playlistName = input.getText().toString();
	    	createPlaylist( playlistName );
	        Toast.makeText(mContext, "Created playlist " + playlistName, Toast.LENGTH_SHORT).show();
	        
	    }
	    
	});
	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    
		@Override public void onClick( DialogInterface dialog, int which ) {
			
	        dialog.cancel();
	        
	    }
	    
	});

	
	
	builder.show();
	
	// TODO: Get the software keyboard to pop up so the user can immediately start writing the name
	
	input.performClick();
	//InputMethodManager mgr = ( InputMethodManager ) mContext.getSystemService( Context.INPUT_METHOD_SERVICE );
	// only will trigger it if no physical keyboard is open
	//mgr.showSoftInput( input, InputMethodManager.SHOW_IMPLICIT );
	
} */
}
