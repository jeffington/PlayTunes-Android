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
	
	SharedPreferences prefs;
	
	public RateAppDialogFragment() {
		
		setStyle( STYLE_NORMAL, 0 );
		
	}
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		prefs = activity.getSharedPreferences( getString( R.string.prefs_file) , Context.MODE_PRIVATE );
		
	}
	
    @Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        View view = inflater.inflate( R.layout.dialog_fragment_rateapp, container);
        
        getDialog().requestWindowFeature( Window.FEATURE_NO_TITLE );
        
        view.findViewById( R.id.DialogCancel ).setOnClickListener( buttonClickListener );
        view.findViewById( R.id.DialogConfirm ).setOnClickListener( buttonClickListener );
        
        return view;
        
    }
	
    
    
    // 
    // Set the app open count to 100, so we don't bother the user about ratings again
    // 
    @Override public void onDismiss(DialogInterface dialog) {
    	
    	try {
    		
    		SharedPreferences.Editor edit = prefs.edit();
    		edit.putInt( getString( R.string.pref_key_appopen ), 100 );
    		edit.commit();
    		
    	} catch ( Exception e ) {
    		
    		//IllegalStateException (@RateAppDialogFragment:onDismiss:50) {main}
    		
    	}
    	
    	super.onDismiss(dialog);
    	
    }
    
    @Override public void onStart() {
    	super.onStart();
    	
    	if ( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB ) {
    		
    		getDialog().getWindow().setBackgroundDrawableResource( R.drawable.gb_dialog_background );
    		
    	}
    	
    }
    
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
		
		@Override public void onClick(View v) {
			
			int id = v.getId();
			
			if ( id == R.id.DialogCancel ) {
				
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
