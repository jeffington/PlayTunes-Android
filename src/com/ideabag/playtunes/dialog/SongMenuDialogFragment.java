package com.ideabag.playtunes.dialog;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.fragment.AlbumsOneFragment;
import com.ideabag.playtunes.fragment.ArtistsOneFragment;

public class SongMenuDialogFragment extends DialogFragment {
	
	String mMediaID;
	
	Cursor mSongCursor = null;
	
	private MainActivity mActivity;
	
    public SongMenuDialogFragment() {
        // Empty constructor required for DialogFragment
    	setStyle( STYLE_NORMAL, 0 );
    	
    }
    
    public void setMediaID( String media_id ) {
    	
    	this.mMediaID = media_id;
    	
    	android.util.Log.i( "SongMenuDialogFragment", "Media ID set" );
    	
    	
    }
    
    @Override public void onAttach( Activity activity ) {
    	super.onAttach( activity );
    	
    	mActivity = ( MainActivity ) activity;
    	
    	mSongCursor = mActivity.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] {
					
					MediaStore.Audio.Media.ALBUM,
					MediaStore.Audio.Media.ALBUM_ID,
					MediaStore.Audio.Media.ARTIST,
					MediaStore.Audio.Media.ARTIST_ID,
					MediaStore.Audio.Media.TITLE,
					MediaStore.Audio.Media._ID
					
				},
				MediaStore.Audio.Media._ID + "=?",
				new String[] {
					
						mMediaID
					
				},
				null
			);
    	
    	mSongCursor.moveToFirst();
    	
    }
    
    @Override public void onDestroy() {
    	super.onDestroy();
    	
    	if ( mSongCursor != null && !mSongCursor.isClosed() ) {
    		
    		mSongCursor.close();
    		
    	}
    	
    }

    @Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        View view = inflater.inflate(R.layout.dialog_fragment_songmenu, container);
        //mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        getDialog().requestWindowFeature( Window.FEATURE_NO_TITLE );
        
        view.findViewById( R.id.DialogCloseButton ).setOnClickListener( mMenuClickListener );
        view.findViewById( R.id.SongMenuAlbum ).setOnClickListener( mMenuClickListener );
        view.findViewById( R.id.SongMenuArtist ).setOnClickListener( mMenuClickListener );
        view.findViewById( R.id.SongMenuAddTo ).setOnClickListener( mMenuClickListener );
        
        String songArtist = mSongCursor.getString( mSongCursor.getColumnIndex( MediaStore.Audio.Media.ARTIST ) );
        String songArtistID = mSongCursor.getString( mSongCursor.getColumnIndex( MediaStore.Audio.Media.ARTIST_ID ) );
        String songAlbum = mSongCursor.getString( mSongCursor.getColumnIndex( MediaStore.Audio.Media.ALBUM ) );
        String songAlbumID = mSongCursor.getString( mSongCursor.getColumnIndex( MediaStore.Audio.Media.ALBUM_ID ) );
        
        View songArtistButton = view.findViewById( R.id.SongMenuArtist );
        View songAlbumButton = view.findViewById( R.id.SongMenuAlbum );
        
        // Show Artist name
        if ( songArtist.equals( getString( R.string.no_artist_string ) ) ) {
        	
        	songArtistButton.setVisibility( View.GONE );
        	
        } else {
        	
        	( ( TextView ) view.findViewById( R.id.SongMenuArtistTitle ) ).setText( songArtist );
        	
        	songArtistButton.setTag( R.id.tag_artist_id, songArtistID );
        	
        }
        
        // Show album name
        if ( songAlbum.equals( getString( R.string.no_album_string ) ) ) {
        	
        	songAlbumButton.setVisibility( View.GONE );
        	
        } else {
        	
        	( ( TextView ) view.findViewById( R.id.SongMenuAlbumTitle ) ).setText( songAlbum );
        	
        	songAlbumButton.setTag( R.id.tag_album_id, songAlbumID );
        	
        }
        
        if ( songAlbum.equals( getString( R.string.no_album_string ) )
        		&& songArtist.equals( getString( R.string.no_artist_string ) ) ) {
        	
        	view.findViewById( R.id.SongMenuInfo ).setVisibility( View.GONE );
        	
        }
        
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
				
				String artist_id = "" + v.getTag( R.id.tag_artist_id );
				
				ArtistsOneFragment artistOneFragment = new ArtistsOneFragment();
				artistOneFragment.setMediaID( artist_id );
				
				mActivity.transactFragment( artistOneFragment );
				
				dismiss();
				
			} else if ( id == R.id.SongMenuAlbum ) {
				
				String album_id = "" + v.getTag( R.id.tag_album_id );
				
				AlbumsOneFragment albumsOneFragment = new AlbumsOneFragment();
				albumsOneFragment.setMediaID( album_id );
				
				mActivity.transactFragment( albumsOneFragment );
				
				dismiss();
				
			} else if ( id == R.id.DialogCloseButton ) {
				
				dismiss();
				
			}
			
		}
		
	};
	
}
