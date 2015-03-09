package com.ideabag.playtunes.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.fragment.SettingsFragment;

public class SettingsDialogFragment extends DialogFragment {
	
    public SettingsDialogFragment() {
        
    	setStyle( STYLE_NORMAL , 0 );
    	
    }
    

    @Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        View view = inflater.inflate( R.layout.dialog_fragment_settings, container );
        getDialog().requestWindowFeature( Window.FEATURE_NO_TITLE );
        
        FragmentTransaction mTransaction = getChildFragmentManager().beginTransaction();
        
        mTransaction.add( R.id.SettingsFragmentContainer, new SettingsFragment() );
        mTransaction.commit();
        
        view.findViewById( R.id.ButtonCancel ).setOnClickListener( headerButtonClickListener );
        
        return view;
        
    }
    
    @Override public void onStart() {
    	super.onStart();
    	
    	if ( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB ) {
    		
    		getDialog().getWindow().setBackgroundDrawableResource( R.drawable.gb_dialog_background );
    		
    	}
    	
    }
    
    private View.OnClickListener headerButtonClickListener = new View.OnClickListener() {
		
		@Override public void onClick( View v ) {
			
			int id = v.getId();
			
			if ( id == R.id.ButtonCancel ) {
				
				dismiss();
				
			}
			
		}
		
	};
	
}
