package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.util.TrackerSingleton;
import com.ideabag.playtunes.view.AllPlaylistsDialogBuilder;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PlaylistsAllFragment extends ListFragment {
	
	public static final String TAG = "All Playlists Fragment";
	
	PlaylistsAllAdapter adapter;
	MainActivity mActivity;
	
	AllPlaylistsDialogBuilder dialogBuilder;
	
	Handler handle;
	
	ContentObserver playlistsChanged = new ContentObserver(new Handler()) {

        @Override public void onChange( boolean selfChange ) {
            
            Log.i("onChange", "?" + selfChange);
            
            mActivity.runOnUiThread( new Runnable() {

				@Override public void run() {
					
					adapter.requery();
					adapter.notifyDataSetChanged();
					//getListView().invalidate();
				}
            	
            });
            
            super.onChange( selfChange );
            
        }

};
	
	@Override public void onAttach( Activity activity ) {
			
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		ActionBar bar =	( ( ActionBarActivity ) getActivity()).getSupportActionBar();
		
		//mActivity.PlaylistManager.createStarredIfNotExist();
    	
		adapter = new PlaylistsAllAdapter( getActivity() );
		
		
    	
    	LayoutInflater inflater = mActivity.getLayoutInflater();
    	LinearLayout starredPlaylist = ( LinearLayout ) inflater.inflate( R.layout.list_item_playlist_starred, null );
    	getListView().addHeaderView( starredPlaylist );
    	
    	starredPlaylist.setTag( R.id.tag_playlist_id, mActivity.PlaylistManager.createStarredIfNotExist() );
    	
    	( ( TextView ) starredPlaylist.findViewById( R.id.BadgeCount ) ).setText( "" + mActivity.PlaylistManager.getStarredCursor().getCount() );
		
    	setListAdapter( adapter );
    	
    	bar.setTitle( "All Playlists" );
    	mActivity.actionbarTitle = bar.getTitle();
    	mActivity.actionbarSubtitle = null;
		bar.setSubtitle( null );
    	
    	setHasOptionsMenu( true );
    	
    	getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
    	
    	
	}
	
	
	
	@Override public void onResume() {
		super.onResume();
		
		dialogBuilder = new AllPlaylistsDialogBuilder( mActivity, mActivity.PlaylistManager );
		
		getActivity().getContentResolver().registerContentObserver( MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, true, playlistsChanged );
		
		Tracker t = TrackerSingleton.getDefaultTracker( mActivity.getBaseContext() );

	    // Set screen name.
	    // Where path is a String representing the screen name.
		t.setScreenName( TAG );
		t.set( "_count", ""+adapter.getCount() );
		
	    // Send a screen view.
		t.send( new HitBuilders.AppViewBuilder().build() );
		
	}
		
	@Override public void onPause() {
		super.onPause();
		
		dialogBuilder = null;
		
		getActivity().getContentResolver().unregisterContentObserver( playlistsChanged );
		
	}
	
	
	@Override public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate( R.menu.menu_playlists_all, menu );
		
	}
	
	@Override public boolean onOptionsItemSelected( MenuItem item ) {
 
        super.onOptionsItemSelected(item);
        
        int id = item.getItemId();
        
        if ( id == R.id.MenuPlaylistsAdd ) {
        	
        	mActivity.PlaylistManager.createPlaylistDialog();
        	
        }
        
        return true;
        
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		String playlist_id = (String) v.getTag( R.id.tag_playlist_id );
		
		PlaylistsOneFragment playlistFragment = new PlaylistsOneFragment();
		
		playlistFragment.setPlaylistId( playlist_id );
		
		mActivity.transactFragment( playlistFragment );
		
	}
	
	public View.OnClickListener playlistMenuClickListener = new View.OnClickListener() {
		
		@Override public void onClick(View v) {
			
			int id = v.getId();
			ViewGroup list_item = ( ViewGroup ) v.getParent();
			String playlist_id = ( String ) list_item.getTag( R.id.tag_playlist_id);
			Log.i("clicked playlist", playlist_id );
			
			if ( null != dialogBuilder ) {
				
				dialogBuilder.buildPlaylistMenuDialog( playlist_id ).show();
				
			}
			
		}
		
	};
	

	
	public class PlaylistsAllAdapter extends BaseAdapter {
		
		protected Context mContext;
		protected Cursor cursor = null;
		
		//View.OnClickListener playlistMenuClickListener;
		
	    private final String[] allPlaylistsSelection = new String[] {
	    	
	    	MediaStore.Audio.Playlists.NAME,
	    	MediaStore.Audio.Playlists.DATE_MODIFIED,
			MediaStore.Audio.Playlists._ID
		
	    };
	    
		public PlaylistsAllAdapter( Context context) {
			
			mContext = context;
			
			requery();
	    	
	    	
		}
		
		public void requery() {
			
			if ( null != cursor)
				cursor.close();
			
	    	cursor = mContext.getContentResolver().query(
					MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
					allPlaylistsSelection,
					MediaStore.Audio.Playlists._ID + " !=?",
					new String[] {
						
						mActivity.PlaylistManager.createStarredIfNotExist()
							
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
				
				convertView.findViewById( R.id.PlaylistMenuButton ).setOnClickListener( playlistMenuClickListener );
				
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