package com.ideabag.playtunes.dialog;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
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

public class PlaylistRenameDialogFragment extends DialogFragment implements OnEditorActionListener {
	
	private EditText mEditText;
	private PlaylistManager mPlaylistManager;
	
	private String mMediaID = null;
	private String mPlaylistName = null;
	
	public PlaylistRenameDialogFragment() {
		
		setStyle( STYLE_NORMAL, 0 );
		
		
		
		
	}
	
	public void setMediaID( String media_id ) {
		
		this.mMediaID = media_id;
		
	}
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		
		Cursor cursor = getActivity().getContentResolver().query(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				new String[] {
			    	
			    	MediaStore.Audio.Playlists.NAME,
					MediaStore.Audio.Playlists._ID
				
			    },
				MediaStore.Audio.Playlists._ID + " =?",
				new String[] {
					
					mMediaID
					
				},
				null
			);
		
		cursor.moveToFirst();
		
		mPlaylistName = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Playlists.NAME ) );
		cursor.close();
		
	}
	
    @Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        View view = inflater.inflate( R.layout.dialog_fragment_renameplaylist, container);
       
        
        
        getDialog().requestWindowFeature( Window.FEATURE_NO_TITLE );
        
        mPlaylistManager = new PlaylistManager( getActivity() );
        
        mEditText = ( EditText ) view.findViewById( R.id.NewPlaylistName );
        
        mEditText.setOnEditorActionListener( this );
        mEditText.setSelected( false );
        
        mEditText.setText( mPlaylistName );
        android.util.Log.i( "Rename playlist", "old name: " + mPlaylistName );
        
        view.findViewById( R.id.DialogRenameConfirm ).setOnClickListener( headerButtonClickListener );
        view.findViewById( R.id.DialogRenameCancel ).setOnClickListener( headerButtonClickListener );
        
        return view;
        
    }
	
    @Override public void onStart() {
    	super.onStart();
    	
    	
    	mEditText.performClick();
    	
    }
    
    private View.OnClickListener headerButtonClickListener = new View.OnClickListener() {
		
		@Override public void onClick(View v) {
			
			int id = v.getId();
			
			if ( id == R.id.DialogRenameCancel ) {
				
				dismiss();
				
			} else if ( id == R.id.DialogRenameConfirm ) {
				
				renamePlaylist();
				
			}
			
		}
		
	};
    
    private void renamePlaylist() {
    	
    	String playlistName = mEditText.getEditableText().toString().trim();
    	
    	if ( null == playlistName || playlistName.length() < 1 ) {
    		
    		Toast.makeText(getActivity(), "Please enter a playlist name.", Toast.LENGTH_SHORT ).show();
    		
    	} else {
    		
    		if ( playlistName.toLowerCase().equals( getString( R.string.playlist_name_starred ).toLowerCase() ) ) {
    			
    			Toast.makeText(getActivity(), "Your playlist can't be named `Starred`.", Toast.LENGTH_SHORT ).show();
    			
    		} else {
    			
    			//int playlist_id = mPlaylistManager.createPlaylist( playlistName );
    			
    			if ( null != mMediaID ) {
    				
    				mPlaylistManager.renamePlaylist( mMediaID, playlistName );
    				
    				Toast.makeText(getActivity(), "Added song to new playlist " + playlistName, Toast.LENGTH_SHORT ).show();
    				
    			}
    			
    			dismiss();
    			
    		}
    		
    	}
    	
    }
    
    public boolean onEditorAction( TextView v, int actionId, KeyEvent event ) {
    	
    	if ( EditorInfo.IME_ACTION_DONE == actionId ) {
            // Return input text to activity
            //EditNameDialogListener activity = (EditNameDialogListener) getActivity();
            //activity.onFinishEditDialog(mEditText.getText().toString());
            
    		renamePlaylist();
    		
            return true;
            
        }
        
        return false;
        
    }
    
}
