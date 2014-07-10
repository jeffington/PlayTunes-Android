package com.ideabag.playtunes.dialog;

import android.app.Activity;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.fragment.PlaylistsAllFragment;
import com.ideabag.playtunes.fragment.PlaylistsOneFragment;

public class AddToPlaylistDialogFragment extends DialogFragment implements OnItemClickListener {
	
	String mMediaID;
	
	PlaylistManager mPlaylistManager;
	
    public AddToPlaylistDialogFragment() {
        // Empty constructor required for DialogFragment
    	setStyle( STYLE_NORMAL, 0 );
    	
    }
    
    public void setMediaID( String media_id ) {
    	
    	this.mMediaID = media_id;
    	
    }
    
    @Override public void onAttach( Activity activity ) {
    	super.onAttach( activity );
    	
    	mPlaylistManager = new PlaylistManager( activity );
    	
    	
    	
    }
    
    @Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        View view = inflater.inflate(R.layout.dialog_fragment_addtoplaylist, container);
        //mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        getDialog().requestWindowFeature( Window.FEATURE_NO_TITLE );
        
        
        ListView lv = ( ListView ) view.findViewById( R.id.AddToPlaylistSelection );
        lv.setDivider( getResources().getDrawable( R.drawable.list_divider ) );
        lv.setDividerHeight( 1 );
        lv.setSelector( R.drawable.list_item_background );
        LinearLayout starredHeader = ( LinearLayout ) inflater.inflate( R.layout.list_item_playlist_starred, null );
        starredHeader.findViewById(R.id.BadgeSong).setVisibility( View.GONE );
        
        LinearLayout createPlaylistHeader = ( LinearLayout ) inflater.inflate( R.layout.list_item_new_playlist, null );
        
        lv.addHeaderView( createPlaylistHeader, null, true );
        lv.addHeaderView( starredHeader, null, true );
        
        lv.setOnItemClickListener( this );
        lv.setAdapter( new ChoosePlaylistAdapter( getActivity() ) );
        
        return view;
        
    }
    
	@Override public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		
		if ( 0 == position ) { // New playlist
			
			FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        	
			Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
			
		    if ( prev != null ) {
		        
		    	ft.remove( prev );
		    	
		    }
			
			CreatePlaylistDialogFragment newFragment = new CreatePlaylistDialogFragment();
			newFragment.setMediaID( mMediaID );
        	
            newFragment.show( ft, "dialog" );
			
		} else if ( 1 == position ) { //starred
			
			
			
		} else {
			
			
        	
			
		}
		
		dismiss();
		
	}
    
    public class ChoosePlaylistAdapter extends BaseAdapter {
    	
    	protected Context mContext;
    	protected Cursor cursor = null;
    	
    	private PlaylistManager mPlaylistManager;
    	
    	View.OnClickListener playlistMenuClickListener;
    	
    	//View.OnClickListener playlistMenuClickListener;
    	
        private final String[] allPlaylistsSelection = new String[] {
        	
        	MediaStore.Audio.Playlists.NAME,
        	MediaStore.Audio.Playlists.DATE_MODIFIED,
    		MediaStore.Audio.Playlists._ID
    	
        };
        
    	public ChoosePlaylistAdapter( Context context ) {
    		
    		mContext = context;
    		
    		mPlaylistManager = new PlaylistManager( mContext );
    		
    		requery();
        	
    	}
    	
    	public void requery() {
    		
    		if ( null != cursor)
    			cursor.close(); 
    		
    		String starred_id = mPlaylistManager.createStarredIfNotExist();
    		
    		
        	cursor = mContext.getContentResolver().query(
    				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
    				allPlaylistsSelection,
    				MediaStore.Audio.Playlists._ID + " !=?",
    				new String[] {
    					
    						starred_id
    						
    				},
    				MediaStore.Audio.Playlists.DATE_MODIFIED + " DESC"
    			);
    		
    	}

    	
    	@Override public int getCount() {
    		
    		return cursor.getCount();
    		
    	}

    	@Override public Object getItem( int position ) {
    		
    		return null;
    		
    	}

    	@Override public long getItemId( int position ) {
    		
    		return 0;
    		
    	}

    	@Override public View getView( int position, View convertView, ViewGroup parent ) {
    		
    		if ( null == convertView ) {
    			
    			LayoutInflater li = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    			
    			convertView = li.inflate( R.layout.list_item_playlist, null );
    			
    			convertView.findViewById( R.id.PlaylistMenuButton ).setVisibility( View.GONE );
    			
    		}
    		
    		cursor.moveToPosition( position );
    		String playlist_id = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Playlists._ID ) );
    		convertView.setTag( R.id.tag_playlist_id, playlist_id );
    		
    		String playlistTitle = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Playlists.NAME ) );
    		
    		// Get song count for the given playlist
    		//MediaStore.Audio.Playlists._COUNT
    		
    		Cursor songs = mContext.getContentResolver().query(
    				MediaStore.Audio.Playlists.Members.getContentUri( "external", Long.parseLong( playlist_id ) ),
    				new String[] {
    					MediaStore.Audio.Playlists.Members._ID
    				},
    				null,
    				null,
    				null
    			);
    		
    		int song_count = songs.getCount();
    		
    		songs.close();
    		
    		( ( TextView ) convertView.findViewById( R.id.PlaylistTitle ) ).setText( playlistTitle );
    		
    		( ( TextView ) convertView.findViewById( R.id.BadgeCount ) ).setText( "" + song_count );
    		
    		
    		
    		return convertView;
    		
    	}

    } 
    
    
}
