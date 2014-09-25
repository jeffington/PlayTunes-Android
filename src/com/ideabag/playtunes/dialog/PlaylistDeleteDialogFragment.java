package com.ideabag.playtunes.dialog;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;

public class PlaylistDeleteDialogFragment extends DialogFragment {
	
	private PlaylistManager mPlaylistManager;
	
	private String mMediaID = null;
	private String mPlaylistName = null;
	private int mSongCount;
	
	public PlaylistDeleteDialogFragment() {
		
		setStyle( STYLE_NORMAL, 0 );
		
	}
	
	public void setMediaID( String media_id ) {
		
		this.mMediaID = media_id;
		
	}
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		
		Cursor playlistMembers = getActivity().getContentResolver().query(
				MediaStore.Audio.Playlists.Members.getContentUri( "external", Long.parseLong( mMediaID ) ),
				new String[] {
			    	
						MediaStore.Audio.Playlists.Members.AUDIO_ID,
				
			    },
			    MediaStore.Audio.Playlists.Members.PLAYLIST_ID + " =?",
				new String[] {
					
					mMediaID
					
				},
				null
			);
		
		playlistMembers.moveToFirst();
		mSongCount = playlistMembers.getCount();
		playlistMembers.close();
		
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
    	
        View view = inflater.inflate( R.layout.dialog_fragment_deleteplaylist, container);
       
        
        
        getDialog().requestWindowFeature( Window.FEATURE_NO_TITLE );
        
        mPlaylistManager = new PlaylistManager( getActivity() );
        
        
        ( (TextView) view.findViewById( R.id.DeletePlaylistMessage)).setText(
        		mPlaylistName
        		+ " has " + mSongCount
        		+ " "
        		+ ( mSongCount == 1 ? getString( R.string.song_singular ) : getString( R.string.songs_plural ) )
        		+ "\n\n"
        		+ "Are you sure you want to delete this playlist?"
        );
        
        //view.findViewById( R.id.DialogConfirmButton ).setOnClickListener( buttonClickListener );
        view.findViewById( R.id.DialogCloseButton ).setOnClickListener( buttonClickListener ); 
        view.findViewById( R.id.DialogDeleteCancel ).setOnClickListener( buttonClickListener );
        view.findViewById( R.id.DialogDeleteConfirm ).setOnClickListener( buttonClickListener );
        
        return view;
        
    }
	
    @Override public void onStart() {
    	super.onStart();
    	
    	
    }
    
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
		
		@Override public void onClick(View v) {
			
			int id = v.getId();
			
			if ( id == R.id.DialogCloseButton || id == R.id.DialogDeleteCancel ) {
				
				dismiss();
				
			} else if ( id == R.id.DialogConfirmButton || id == R.id.DialogDeleteConfirm ) {
				
				deletePlaylist();
				
			}
			
		}
		
	};
    
    private void deletePlaylist() {
    	
    	if ( null != mMediaID ) {
    		
    		mPlaylistManager.deletePlaylist( mMediaID );
    		
    		Toast.makeText( getActivity(), "Deleted playlist " + mPlaylistName, Toast.LENGTH_SHORT ).show();
    		
    		dismiss();
    		
    	}
    	
    }
    
}
