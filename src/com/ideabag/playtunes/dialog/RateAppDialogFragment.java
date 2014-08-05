package com.ideabag.playtunes.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.ideabag.playtunes.R;

public class RateAppDialogFragment extends DialogFragment {
	
	public RateAppDialogFragment() {
		
		setStyle( STYLE_NORMAL, 0 );
		
	}
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		
	}
	
    @Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        View view = inflater.inflate( R.layout.dialog_fragment_rateapp, container);
        
        getDialog().requestWindowFeature( Window.FEATURE_NO_TITLE );
        
        view.findViewById( R.id.DialogCloseButton ).setOnClickListener( buttonClickListener ); 
        view.findViewById( R.id.DialogCancel ).setOnClickListener( buttonClickListener );
        view.findViewById( R.id.DialogConfirm ).setOnClickListener( buttonClickListener );
        
        return view;
        
    }
	
    // 
    // Set the app open count to 20, so we don't bother the user about ratings again
    // 
    @Override public void onDismiss(DialogInterface dialog) {
    	
    	SharedPreferences prefs = getActivity().getSharedPreferences( getString( R.string.prefs_file) , Context.MODE_PRIVATE );
    	SharedPreferences.Editor edit = prefs.edit();
    	edit.putInt( getString( R.string.pref_key_appopen ), 20 );
    	edit.commit();
    	
    	super.onDismiss(dialog);
    	
    }
    
    @Override public void onStart() {
    	super.onStart();
    	
    	
    }
    
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
		
		@Override public void onClick(View v) {
			
			int id = v.getId();
			
			if ( id == R.id.DialogCloseButton || id == R.id.DialogCancel ) {
				
				dismiss();
				
			} else if ( id == R.id.DialogConfirm ) {
				
				final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
				
				try {
					
				    getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				
				} catch (android.content.ActivityNotFoundException anfe) {
					
					getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
					
				}
				
			}
			
		}
		
	};
    
}
