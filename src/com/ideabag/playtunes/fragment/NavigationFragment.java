package com.ideabag.playtunes.fragment;

import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.activity.SettingsActivity;
import com.ideabag.playtunes.adapter.NavigationListAdapter;
import com.ideabag.playtunes.dialog.FeedbackDialogFragment;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class NavigationFragment extends Fragment implements OnItemClickListener {
	
	public static final String TAG = "NavigationFragment";
	
	private MainActivity mActivity;
	private PlaylistManager mPlaylistManager;
	
	NavigationListAdapter adapter;
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate( R.layout.fragment_navigation, container, false );
		
	}
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
		mPlaylistManager = new PlaylistManager( getActivity() );
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		
		super.onActivityCreated( savedInstanceState );
        
		adapter = new NavigationListAdapter( getActivity() );
		
		ListView lv = ( ListView ) getView().findViewById( R.id.NavigationListView );
		lv.setAdapter( adapter );
		
		
		
		lv.setOnItemClickListener( this );
		
		getView().findViewById( R.id.NavigationToolbarSettings ).setOnClickListener( NavigationClickListener );
		getView().findViewById( R.id.NavigationToolbarFeedback ).setOnClickListener( NavigationClickListener );
		lv.setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		lv.setDividerHeight( 1 );
		lv.setSelector( R.drawable.list_item_background );
		
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mediaStoreChanged );
		
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Playlists.Members.getContentUri( "external",
						Long.parseLong( mPlaylistManager.createStarredIfNotExist() ) ), true, mediaStoreChanged );
		
	}
	
	@Override public void onResume() {
		super.onResume();
	}
	
	@Override public void onPause() {
		super.onPause();
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		getActivity().getContentResolver().unregisterContentObserver( mediaStoreChanged );
		
	}
	
	private View.OnClickListener NavigationClickListener = new View.OnClickListener() {
		
		@Override public void onClick( View v ) {
			
			int id = v.getId();
			
			if ( id == R.id.NavigationToolbarSettings ) {
				
				Intent launchSettingsIntent = new Intent( getActivity(), SettingsActivity.class);
				
				getActivity().startActivity( launchSettingsIntent );
				
			} else if ( id == R.id.NavigationToolbarFeedback ) {
				
	        	FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	        	
	        	DialogFragment mNewFragment = new FeedbackDialogFragment();
	        	
	            mNewFragment.show(ft, "dialog");
				//mActivity.getSupportFragmentManager()
				
			}
			
		}
		
	};

	@Override public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		
		load( position, false );
		
	}
	
	private void load( int position, boolean silent ) {
		
		Fragment mNewFragment = null;
		
		switch ( position ) {
		
		case NavigationListAdapter.ARTISTS:
			mNewFragment = new ArtistsAllFragment();
			break;
			
		case NavigationListAdapter.ALBUMS:
			mNewFragment = new AlbumsAllFragment();
			break;
			
		case NavigationListAdapter.GENRES:
	    	mNewFragment = new GenresAllFragment();
			break;
			
		case NavigationListAdapter.SONGS:
			
	    	mNewFragment = new SongsFragment();
			break;
		
		case NavigationListAdapter.STARRED:
			
			mNewFragment = new PlaylistsOneFragment();
			((PlaylistsOneFragment)mNewFragment).setMediaID( mPlaylistManager.createStarredIfNotExist() );
			
			break;
			
		default:
			
			mNewFragment = new PlaylistsAllFragment();
			break;
			
		}
		
		if ( null != mNewFragment ) {
			
			mActivity.transactFragment( mNewFragment );
			
		}
		
		if ( !silent ) {
			
			mActivity.toggleDrawer();
			
		}
		
	}
	
	ContentObserver mediaStoreChanged = new ContentObserver(new Handler()) {

        @Override public void onChange( boolean selfChange ) {
            
            mActivity.runOnUiThread( new Runnable() {

				@Override public void run() {
					
					adapter.notifyDataSetChanged();
				
				}
            	
            });
            
            super.onChange( selfChange );
            
        }

	};
	
}
