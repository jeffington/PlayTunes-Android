package com.ideabag.playtunes.fragment;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.NavigationListAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MusicBrowserFragment extends Fragment {
	private MainActivity mActivity;
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate( R.layout.fragment_music_browser, null, false );
		
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

		
		//getView().findViewById( R.id.NavigationToolbarSettings ).setOnClickListener( NavigationClickListener );
		
		
	}
	
	@Override public void onResume() {
		super.onResume();
		
		
	}
	
	@Override public void onPause() {
		super.onPause();
		
		
		
	}
	
}
