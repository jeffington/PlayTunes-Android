package com.ideabag.playtunes.fragment.xlarge.browser;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.util.IMusicBrowser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BrowseSongsFragment extends Fragment implements IMusicBrowser {
	
	private MainActivity mActivity;
	
	@Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		
		return inflater.inflate( R.layout.fragment_browse_songs, container, false );
		
	}
	
	@Override public void setMediaID(String media_id) {
		// TODO Auto-generated method stub
		
	}
	
	@Override public String getMediaID() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
}
