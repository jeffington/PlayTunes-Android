package com.ideabag.playtunes.fragment;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.activity.SettingsActivity;
import com.ideabag.playtunes.adapter.NavigationListAdapter;
import com.ideabag.playtunes.dialog.CreatePlaylistDialogFragment;
import com.ideabag.playtunes.dialog.FeedbackDialogFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;


public class NavigationFragment extends Fragment implements OnItemClickListener {
	
	private MainActivity mActivity;
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate( R.layout.fragment_navigation, null, false );
		
	}
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		
		super.onActivityCreated( savedInstanceState );
        
		//this.getListView().setSelector(R.drawable.nav_list_item_back);
		//getActivity().getResources().getDimension(resourceID) 
		//this.setListAdapter( listAdapter );
		//this.getListView().setDivider( new ColorDrawable( 0xCCCCCC ) );
		//this.getListView().setDividerHeight( 1 );
		//this.getListView().setChoiceMode( ListView.CHOICE_MODE_SINGLE );
		//this.getListView().setHeaderDividersEnabled( true );
		
		//getView().findViewById( R.id.NavigationSettings ).setOnClickListener( NavigationClickListener );
		
		ListView lv = ( ListView ) getView().findViewById( R.id.NavigationListView );
		lv.setAdapter( new NavigationListAdapter( getActivity() ) );
		
		
		
		lv.setOnItemClickListener( this );
		//lv.setOnItemLongClickListener( this );
		
		getView().findViewById( R.id.NavigationToolbarSettings ).setOnClickListener( NavigationClickListener );
		getView().findViewById( R.id.NavigationToolbarFeedback ).setOnClickListener( NavigationClickListener );
		lv.setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		lv.setDividerHeight( 1 );
		lv.setSelector( R.drawable.list_item_background );
		
	}
	
	@Override public void onResume() {
		super.onResume();
		
		
	}
	
	@Override public void onPause() {
		super.onPause();
		
		
		
	}
	
	private View.OnClickListener NavigationClickListener = new View.OnClickListener() {
		
		@Override public void onClick( View v ) {
			
			int id = v.getId();
			
			if ( id == R.id.NavigationToolbarSettings ) {
				
				Intent launchSettingsIntent = new Intent( getActivity(), SettingsActivity.class);
				
				getActivity().startActivity( launchSettingsIntent );
				
			} else if ( id == R.id.NavigationToolbarFeedback ) {
				
	        	FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	        	
	        	DialogFragment newFragment = new FeedbackDialogFragment();
	        	
	            newFragment.show(ft, "dialog");
				//mActivity.getSupportFragmentManager()
				
			}
			
		}
		
	};

	@Override public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		
		load( position, false );
		
	}
	
	private void load( int position, boolean silent ) {
		
		switch ( position ) {
		
		case 0:
			mActivity.showArtists();
			break;
			
		case 1:
			mActivity.showAlbums();
			break;
			
		case 2:
			mActivity.showGenres();
			break;
			
		case 3:
			mActivity.showSongs();
			break;
		
		default:
			mActivity.showPlaylists();
			break;
			
		}
		
		if ( !silent ) {
			
			mActivity.toggleDrawer();
			
		}
		
	}
	
}
