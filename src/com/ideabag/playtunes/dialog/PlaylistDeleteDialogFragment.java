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
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.util.QueryCountTask;

public class PlaylistDeleteDialogFragment extends DialogFragment {
	
	private PlaylistManager mPlaylistManager;
	
	private String mMediaID = null;
	private String mPlaylistName = null;
	private int mSongCount;
	
	private TextView mTitle;
	private TextView mCount;
	
	public PlaylistDeleteDialogFragment() {
		
		setStyle( STYLE_NORMAL, 0 );
		
	}
	
	public void setMediaID( String media_id ) {
		
		this.mMediaID = media_id;
		
	}
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		
	}
	
    @Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        View view = inflater.inflate( R.layout.dialog_fragment_deleteplaylist, container);
       
        MediaQuery mPlaylistMembers = new MediaQuery(
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
		
		MediaQuery mPlaylistTitle = new MediaQuery(
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
        
        getDialog().requestWindowFeature( Window.FEATURE_NO_TITLE );
        
        mTitle = ( TextView ) view.findViewById( R.id.Title );//.setText( mPlaylistName );
        mCount = ( TextView ) view.findViewById( R.id.BadgeCount );// ).setText( "" + mSongCount );
        
		MediaQuery.executeAsync(getActivity(), mPlaylistMembers, new MediaQuery.OnQueryCompletedListener() {

			@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
				
				mResult.moveToFirst();
				mSongCount = mResult.getCount();//( mResult.getColumnIndex( MediaStore.Audio.Playlists.NAME ) );
				mCount.setText( "" + mSongCount );
				mResult.close();
				
			}
			
		});
		
		MediaQuery.executeAsync(getActivity(), mPlaylistTitle, new MediaQuery.OnQueryCompletedListener() {

			@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
				
				mResult.moveToFirst();
				mPlaylistName = mResult.getString( mResult.getColumnIndex( MediaStore.Audio.Playlists.NAME ) );
				mTitle.setText( mPlaylistName );
				mResult.close();
				
			}
			
		});
		
		
        mPlaylistManager = new PlaylistManager( getActivity() );
        
        view.findViewById( R.id.DialogDeleteCancel ).setOnClickListener( buttonClickListener );
        view.findViewById( R.id.DialogDeleteConfirm ).setOnClickListener( buttonClickListener );
        
        
        return view;
        
    }
	
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
		
		@Override public void onClick(View v) {
			
			int id = v.getId();
			
			if ( id == R.id.DialogDeleteCancel ) {
				
				dismiss();
				
			} else if ( id == R.id.DialogDeleteConfirm ) {
				
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
