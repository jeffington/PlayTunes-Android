package com.ideabag.playtunes.fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.NavigationListAdapter;
import com.ideabag.playtunes.util.AdmobUtil;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MusicBrowserFragment extends Fragment {
	
	private MainActivity mActivity;
	
	private AdView mAdView;
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate( R.layout.fragment_music_browser, null, false );
		
	}
	
	@Override public void onAttach( Activity activity ) {
		
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		
		super.onActivityCreated( savedInstanceState );
        
    	mAdView = ( AdView ) getView().findViewById( R.id.adView );
	    
		AdRequest.Builder adRequestBuilder = new AdRequest.Builder().addTestDevice( AdRequest.DEVICE_ID_EMULATOR );
	    AdmobUtil.AddTestDevices( getActivity(), adRequestBuilder );
	    
	    AdRequest adRequest = adRequestBuilder.build();
		
		
		// Start loading the ad in the background.
	    mAdView.loadAd( adRequest );
	    
	}
	
	@Override public void onResume() {
		super.onResume();
		
		mAdView.resume();
		
	}
	
	@Override public void onPause() {
		
		mAdView.pause();
		
		super.onPause();
		
	}
	
	@Override public void onDestroy() {
		
		mAdView.destroy();
		
		super.onDestroy();
		
	}
	
}
