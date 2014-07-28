package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.PlaylistsAllAdapter;
import com.ideabag.playtunes.dialog.CreatePlaylistDialogFragment;
import com.ideabag.playtunes.dialog.PlaylistMenuDialogFragment;
import com.ideabag.playtunes.util.PlaylistBrowser;
import com.ideabag.playtunes.util.TrackerSingleton;

import android.app.Activity;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PlaylistsAllFragment extends ListFragment implements PlaylistBrowser {
	
	public static final String TAG = "All Playlists Fragment";
	
	PlaylistsAllAdapter adapter;
	//MergeAdapter adapter;
	
	MainActivity mActivity;
	
	
	@Override public void onAttach( Activity activity ) {
			
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
    
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		adapter = new PlaylistsAllAdapter( getActivity(), playlistMenuClickListener );
		

    	
    	LayoutInflater inflater = mActivity.getLayoutInflater();
    	LinearLayout starredPlaylist = ( LinearLayout ) inflater.inflate( R.layout.list_item_playlist_starred, null );
    	
    	starredPlaylist.setTag( R.id.tag_playlist_id, mActivity.PlaylistManager.createStarredIfNotExist() );
    	
    	( ( TextView ) starredPlaylist.findViewById( R.id.BadgeCount ) ).setText( "" + mActivity.PlaylistManager.getStarredCursor().getCount() );
		
    	
    	setHasOptionsMenu( true );
    	
    	getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
		getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
		
		//adapter.addView( mActivity.AdContainer, false );
		getListView().addHeaderView( starredPlaylist, null, true );
		
		
    	setListAdapter( adapter );
    	
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, true, mediaStoreChanged );
    	
	}
	
	
	
	@Override public void onResume() {
		super.onResume();
		
    	mActivity.setActionbarTitle( getString( R.string.playlists_plural ) );
    	mActivity.setActionbarSubtitle( null );
		

		
		Tracker t = TrackerSingleton.getDefaultTracker( mActivity.getBaseContext() );

	    // Set screen name.
	    // Where path is a String representing the screen name.
		t.setScreenName( TAG );
		
	    // Send a screen view.
		t.send( new HitBuilders.AppViewBuilder().build() );
		
		t.send( new HitBuilders.EventBuilder()
    	.setCategory( "playlist" )
    	.setAction( "show" )
    	.setLabel( TAG )
    	.setValue( adapter.getCount() )
    	.build());
		
		//mActivity.AdView.resume();
		
	}
		
	@Override public void onPause() {
		super.onPause();
		
		//dialogBuilder = null;
		
		
		
		//mActivity.AdView.pause();
		
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		setHasOptionsMenu( false );
		//getListView().removeHeaderView( mActivity.AdContainer );
		getActivity().getContentResolver().unregisterContentObserver( mediaStoreChanged );
	}
	
	@Override public void onDestroyView() {
	    super.onDestroyView();
	    
	    setListAdapter( null );
	    
	}
	
	@Override public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate( R.menu.menu_playlists_all, menu );
		
	}
	
	@Override public boolean onOptionsItemSelected( MenuItem item ) {
 
        super.onOptionsItemSelected(item);
        
        int id = item.getItemId();
        
        if ( id == R.id.MenuPlaylistsAdd ) {
        	
        	FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        	
        	DialogFragment newFragment = new CreatePlaylistDialogFragment();
        	
            newFragment.show(ft, "dialog");
        	
        	
        }
        
        return true;
        
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		String playlist_id = (String) v.getTag( R.id.tag_playlist_id );
		
		PlaylistsOneFragment playlistFragment = new PlaylistsOneFragment();
		
		playlistFragment.setMediaID( playlist_id );
		
		mActivity.transactFragment( playlistFragment );
		
	}
	
	public View.OnClickListener playlistMenuClickListener = new View.OnClickListener() {
		
		@Override public void onClick(View v) {
			
			//int id = v.getId();
			
			ViewGroup list_item = ( ViewGroup ) v.getParent();
			String playlist_id = ( String ) list_item.getTag( R.id.tag_playlist_id);
			
			FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        	
			PlaylistMenuDialogFragment newFragment = new PlaylistMenuDialogFragment();
			newFragment.setMediaID( playlist_id );
        	
            newFragment.show( ft, "dialog" );
			
		}
		
	};


	// PlaylistBrowser interface methods
	
	@Override public void setMediaID(String media_id) { /* ... */ }

	@Override public String getMediaID() { return ""; }
	
	ContentObserver mediaStoreChanged = new ContentObserver( new Handler() ) {
		
        @Override public void onChange( boolean selfChange ) {
            
            mActivity.runOnUiThread( new Runnable() {

				@Override public void run() {
					
					adapter.requery();
					adapter.notifyDataSetChanged();
					
				}
            	
            });
            
            super.onChange( selfChange );
            
        }

	};
	
}